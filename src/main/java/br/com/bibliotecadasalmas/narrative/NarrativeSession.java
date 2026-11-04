package br.com.bibliotecadasalmas.narrative;

import br.com.bibliotecadasalmas.core.GameState;
import java.util.List;
import java.util.Optional;

/** Executa nós sobre GameState; apresentação, exploração e gravação pertencem ao controlador. */
public final class NarrativeSession {
    private final Story story;
    private final GameState state;
    private boolean active;
    private String pendingChoice;
    private ChoiceOption.Destination.Exploration exploration;

    public NarrativeSession(Story story, GameState state) { this.story = story; this.state = state; }

    public boolean active() { return active; }
    public NarrativeNode node() { return scene().node(state.position().nodeId()); }
    public Story.Scene scene() { return story.scene(state.position().sceneId()); }
    public Optional<String> pendingChoice() { return Optional.ofNullable(pendingChoice); }
    public Optional<ChoiceOption.Destination.Exploration> exploration() { return Optional.ofNullable(exploration); }
    public boolean ended() { return !active && node() instanceof NarrativeNode.End; }
    public boolean sceneCompleted(String id) { return state.isInteractionCompleted("scene/" + id); }

    public void start(String sceneId) { startAt(sceneId, story.scene(sceneId).entryNode()); }

    public void startAt(String sceneId, String nodeId) {
        Story.Scene next = story.scene(sceneId);
        next.node(nodeId);
        if (next.replayPolicy() == Story.ReplayPolicy.ONCE && sceneCompleted(sceneId)) {
            throw new IllegalStateException("Cena já concluída: " + sceneId);
        }
        GameState candidate = state.copy();
        candidate.moveToLocation(next.location());
        candidate.setNarrativePosition(sceneId, nodeId);
        state.replaceWith(candidate);
        pendingChoice = null;
        exploration = null;
        active = true;
        settle();
    }

    /** Restaurar uma tela não reexecuta efeitos: só posições de espera podem estar ativas. */
    public void restore(boolean active, String pendingChoice) {
        NarrativeNode current = node();
        if (active && !(current instanceof NarrativeNode.Line || current instanceof NarrativeNode.Cutscene
                || current instanceof NarrativeNode.Choice)) throw new IllegalArgumentException("Save em nó automático");
        if (active && !scene().location().equals(state.locationId())) throw new IllegalArgumentException("Local divergente do diálogo");
        if (pendingChoice != null) {
            if (!active || !(current instanceof NarrativeNode.Choice choice) || choice.confirmation().isEmpty()) {
                throw new IllegalArgumentException("Confirmação sem escolha ativa");
            }
            option(pendingChoice);
        }
        this.active = active;
        this.pendingChoice = pendingChoice;
        exploration = null;
    }

    public void suspend() { active = false; pendingChoice = null; }

    public List<ChoiceOption> options() {
        if (!active || !(node() instanceof NarrativeNode.Choice choice)) return List.of();
        return choice.options().stream().filter(o -> o.requires().map(c -> c.matches(state)).orElse(true)).toList();
    }

    public void advance() {
        if (!active || pendingChoice != null) return;
        switch (node()) {
            case NarrativeNode.Line line -> move(line.next());
            case NarrativeNode.Cutscene cutscene -> move(cutscene.next());
            default -> { return; }
        }
        settle();
    }

    public void choose(String id) {
        if (!active || pendingChoice != null) return;
        ChoiceOption option = option(id);
        NarrativeNode.Choice choice = (NarrativeNode.Choice) node();
        if (choice.confirmation().isPresent()) pendingChoice = id;
        else commitChoice(option);
    }

    public void confirm() {
        if (pendingChoice == null) return;
        commitChoice(option(pendingChoice));
        pendingChoice = null;
    }

    public void cancelConfirmation() { pendingChoice = null; }

    private ChoiceOption option(String id) {
        if (!(node() instanceof NarrativeNode.Choice)) throw new IllegalStateException("Nó atual não é escolha");
        return optionsForCurrent().stream().filter(o -> o.id().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Opção indisponível: " + id));
    }

    private List<ChoiceOption> optionsForCurrent() {
        return ((NarrativeNode.Choice) node()).options().stream()
                .filter(o -> o.requires().map(c -> c.matches(state)).orElse(true)).toList();
    }

    private void commitChoice(ChoiceOption option) {
        String sceneId = scene().id();
        String nodeId = node().id();
        GameState candidate = state.copy();
        apply(candidate, option.effects(), "effect/" + sceneId + "/" + nodeId + "/" + option.id());
        candidate.recordChoice(sceneId, nodeId, option.id());
        boolean nextActive = true;
        ChoiceOption.Destination.Exploration request = null;
        switch (option.destination()) {
            case ChoiceOption.Destination.Node next -> {
                scene().node(next.nodeId());
                candidate.setNarrativePosition(sceneId, next.nodeId());
            }
            case ChoiceOption.Destination.Scene next -> {
                Story.Scene destination = story.scene(next.sceneId());
                if (destination.replayPolicy() == Story.ReplayPolicy.ONCE && sceneCompleted(destination.id())) {
                    throw new IllegalStateException("Cena já concluída: " + destination.id());
                }
                candidate.completeInteraction("scene/" + sceneId);
                candidate.moveToLocation(destination.location());
                candidate.setNarrativePosition(destination.id(), destination.entryNode());
            }
            case ChoiceOption.Destination.Exploration next -> {
                story.scene(next.resumeScene());
                candidate.moveToLocation(next.location());
                request = next;
                nextActive = false;
            }
        }
        state.replaceWith(candidate);
        active = nextActive;
        exploration = request;
        settle();
    }

    private void settle() {
        for (int step = 0; active && step < 2000; step++) {
            switch (node()) {
                case NarrativeNode.Join join -> move(join.next());
                case NarrativeNode.Branch branch -> move(branch.test().matches(state) ? branch.thenNode() : branch.elseNode());
                case NarrativeNode.Event event -> {
                    GameState candidate = state.copy();
                    apply(candidate, event.effects(), "effect/" + scene().id() + "/" + event.id());
                    scene().node(event.next());
                    candidate.setNarrativePosition(scene().id(), event.next());
                    state.replaceWith(candidate);
                }
                case NarrativeNode.End end -> {
                    GameState candidate = state.copy();
                    apply(candidate, end.effects(), "effect/" + scene().id() + "/" + end.id());
                    candidate.completeInteraction("scene/" + scene().id());
                    state.replaceWith(candidate);
                    active = false;
                }
                default -> {
                    if (node() instanceof NarrativeNode.Choice && options().isEmpty()) {
                        throw new IllegalStateException("Escolha sem opções disponíveis: " + state.position());
                    }
                    return;
                }
            }
        }
        if (active) throw new IllegalStateException("Sequência automática sem espera: " + state.position());
    }

    private void move(String nodeId) {
        scene().node(nodeId);
        state.setNarrativePosition(scene().id(), nodeId);
    }

    private static void apply(GameState candidate, List<NarrativeEffect> effects, String marker) {
        if (candidate.isInteractionCompleted(marker)) return;
        for (NarrativeEffect effect : effects) {
            switch (effect) {
                case NarrativeEffect.SetFlag flag -> candidate.setFlag(flag.key(), flag.value());
                case NarrativeEffect.SetValue value -> candidate.setValue(value.key(), value.value());
                case NarrativeEffect.GiveItem item -> candidate.giveItem(item.item());
                case NarrativeEffect.RemoveItem item -> candidate.removeItem(item.item());
                case NarrativeEffect.UnlockMemory memory -> candidate.unlockMemory(memory.memory());
                case NarrativeEffect.UnlockLocation location -> candidate.unlockLocation(location.location());
            }
        }
        candidate.completeInteraction(marker);
    }
}
