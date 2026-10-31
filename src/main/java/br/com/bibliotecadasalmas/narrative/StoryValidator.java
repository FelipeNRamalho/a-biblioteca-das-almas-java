package br.com.bibliotecadasalmas.narrative;

import br.com.bibliotecadasalmas.core.GameState.NarrativePosition;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** Validação do grafo e do contrato do Capítulo I, independente da apresentação. */
final class StoryValidator {
    private static final Set<String> MEMORIES = Set.of("retorno", "escolha", "pacto");
    private static final Set<String> SPEAKERS = Set.of("natasha", "rodion", "fantasma", "escriba",
            "natasha_memoria", "rodion_memoria", "voz_escriba", "narrador", "carta", "documento",
            "inscricao", "livro", "registro");
    private final String source;

    StoryValidator(String source) { this.source = source; }

    void validate(Story story) {
        if (story.scenes().size() != story.metadata().sceneCount()) fail("metadata.scene_count", "Contagem de cenas divergente");
        long cutscenes = story.scenes().values().stream().flatMap(s -> s.nodes().values().stream())
                .filter(n -> n instanceof NarrativeNode.Cutscene).count();
        if (cutscenes != story.metadata().cutsceneCount()) fail("metadata.cutscene_count", "Contagem de cutscenes divergente");
        sceneRef(story, story.metadata().entryScene(), "metadata.entry_scene");
        for (String ending : story.metadata().endingScenes()) sceneRef(story, ending, "metadata.ending_scenes");

        Set<String> locations = story.scenes().values().stream().map(Story.Scene::location).collect(Collectors.toSet());
        Set<String> flags = new HashSet<>();
        Set<String> items = new HashSet<>();
        for (Story.Scene scene : story.scenes().values()) {
            for (NarrativeNode node : scene.nodes().values()) {
                for (NarrativeEffect effect : effects(node)) {
                    if (effect instanceof NarrativeEffect.SetFlag flag) flags.add(flag.key());
                    if (effect instanceof NarrativeEffect.GiveItem item) items.add(item.item());
                    if (effect instanceof NarrativeEffect.UnlockMemory memory && MEMORIES.contains(memory.memory())) {
                        flags.add("memoria_" + memory.memory());
                    }
                }
            }
        }
        condition(story.metadata().secretEndingRequires(), flags, "metadata.special_conditions.ending_secret_requires");
        for (Story.Scene scene : story.scenes().values()) {
            nodeRef(scene, scene.entryNode(), "cena " + scene.id() + ".entry_node");
            for (NarrativeNode node : scene.nodes().values()) {
                String c = "cena " + scene.id() + " / nó " + node.id();
                switch (node) {
                    case NarrativeNode.Line line -> {
                        nodeRef(scene, line.next(), c + ".next");
                        if (!SPEAKERS.contains(line.speaker())) fail(c + ".speaker", "Falante desconhecido: " + line.speaker());
                    }
                    case NarrativeNode.Cutscene cutscene -> nodeRef(scene, cutscene.next(), c + ".next");
                    case NarrativeNode.Join join -> nodeRef(scene, join.next(), c + ".next");
                    case NarrativeNode.Event event -> nodeRef(scene, event.next(), c + ".next");
                    case NarrativeNode.End end -> {
                        end.nextScene().ifPresent(id -> sceneRef(story, id, c + ".next_scene"));
                        if (end.nextScene().isEmpty() && !story.metadata().endingScenes().contains(scene.id())) {
                            fail(c, "Encerramento sem destino fora de um epílogo declarado");
                        }
                    }
                    case NarrativeNode.Branch branch -> {
                        nodeRef(scene, branch.thenNode(), c + ".then");
                        nodeRef(scene, branch.elseNode(), c + ".else");
                        condition(branch.test(), flags, c + ".test");
                    }
                    case NarrativeNode.Choice choice -> {
                        for (ChoiceOption option : choice.options()) {
                            String oc = c + " / opção " + option.id();
                            option.requires().ifPresent(test -> condition(test, flags, oc + ".requires"));
                            validateEffects(option.effects(), locations, items, oc);
                            switch (option.destination()) {
                                case ChoiceOption.Destination.Node destination -> nodeRef(scene, destination.nodeId(), oc + ".next");
                                case ChoiceOption.Destination.Scene destination -> sceneRef(story, destination.sceneId(), oc + ".next_scene");
                                case ChoiceOption.Destination.Exploration destination -> {
                                    reference(locations, destination.location(), oc + ".location", "Local desconhecido");
                                    sceneRef(story, destination.resumeScene(), oc + ".resume_scene");
                                }
                            }
                        }
                    }
                }
                if (!(node instanceof NarrativeNode.Choice)) validateEffects(effects(node), locations, items, c);
            }
        }
        validateChapterOneRules(story);
        rejectAutomaticCycles(story);
    }

    private void validateEffects(List<NarrativeEffect> effects, Set<String> locations, Set<String> items, String c) {
        for (int i = 0; i < effects.size(); i++) {
            String ec = c + ".effects[" + i + "]";
            switch (effects.get(i)) {
                case NarrativeEffect.UnlockMemory memory -> reference(MEMORIES, memory.memory(), ec + ".memory", "Memória desconhecida");
                case NarrativeEffect.UnlockLocation location -> reference(locations, location.location(), ec + ".location", "Local desconhecido");
                case NarrativeEffect.RemoveItem item -> reference(items, item.item(), ec + ".item", "Item sem declaração de coleta");
                default -> { /* Os demais parâmetros já foram tipados pelo decoder. */ }
            }
        }
    }

    private void condition(StoryCondition condition, Set<String> flags, String c) {
        switch (condition) {
            case StoryCondition.Flag flag -> reference(flags, flag.flag(), c + ".flag", "Flag sem definição no roteiro");
            case StoryCondition.All all -> {
                for (int i = 0; i < all.conditions().size(); i++) condition(all.conditions().get(i), flags, c + ".all[" + i + "]");
            }
        }
    }

    /** As três declarações da regra oficial podem variar de ordem, sem mudar seu significado. */
    private void validateChapterOneRules(Story story) {
        if (!story.metadata().id().equals("biblioteca_das_almas_capitulo_1")) return;
        Set<StoryCondition.Flag> expected = Set.of(new StoryCondition.Flag("memoria_retorno", true),
                new StoryCondition.Flag("memoria_escolha", true), new StoryCondition.Flag("memoria_pacto", true),
                new StoryCondition.Flag("fantasma_confia", true), new StoryCondition.Flag("regra_reescrita_descoberta", true));
        secretRule(story.metadata().secretEndingRequires(), expected, "metadata.special_conditions.ending_secret_requires");
        Story.Scene reveal = sceneRef(story, "c24_terceira_possibilidade", "regra do final secreto");
        NarrativeNode revealNode = nodeRef(reveal, "n001", "cena " + reveal.id());
        if (!(revealNode instanceof NarrativeNode.Branch branch)) {
            fail("cena " + reveal.id() + " / nó n001", "Revelação deve consultar as condições secretas");
        } else {
            secretRule(branch.test(), expected, "cena " + reveal.id() + " / nó n001.test");
        }
        Story.Scene decision = sceneRef(story, "c25_decisao", "regra do final secreto");
        NarrativeNode decisionNode = nodeRef(decision, "n004", "cena " + decision.id());
        if (!(decisionNode instanceof NarrativeNode.Choice choice)) {
            fail("cena " + decision.id() + " / nó n004", "Decisão final deve ser uma escolha");
        } else {
            ChoiceOption rewrite = choice.options().stream().filter(o -> o.id().equals("reescrever")).findFirst()
                    .orElseThrow(() -> new StoryLoadException(source, "cena " + decision.id(), "Opção reescrever ausente"));
            StoryCondition requires = rewrite.requires().orElseThrow(() -> new StoryLoadException(source,
                    "cena " + decision.id() + " / opção reescrever.requires", "Reescrita sem requisitos"));
            secretRule(requires, expected, "cena " + decision.id() + " / opção reescrever.requires");
        }
    }

    private void secretRule(StoryCondition condition, Set<StoryCondition.Flag> expected, String c) {
        List<StoryCondition.Flag> tests = flatten(condition);
        if (tests.size() != expected.size() || !new HashSet<>(tests).equals(expected)) {
            fail(c, "Regra divergente das cinco condições oficiais da reescrita");
        }
    }

    private List<StoryCondition.Flag> flatten(StoryCondition condition) {
        return switch (condition) {
            case StoryCondition.Flag flag -> List.of(flag);
            case StoryCondition.All all -> all.conditions().stream().flatMap(c -> flatten(c).stream()).toList();
        };
    }

    /** Ordenação topológica evita recursão sobre grafos grandes e detecta ciclos entre cenas. */
    private void rejectAutomaticCycles(Story story) {
        Map<NarrativePosition, List<NarrativePosition>> graph = new LinkedHashMap<>();
        for (Story.Scene scene : story.scenes().values()) {
            for (NarrativeNode node : scene.nodes().values()) {
                List<NarrativePosition> next = new ArrayList<>();
                switch (node) {
                    case NarrativeNode.Join join -> next.add(new NarrativePosition(scene.id(), join.next()));
                    case NarrativeNode.Event event -> next.add(new NarrativePosition(scene.id(), event.next()));
                    case NarrativeNode.Branch branch -> {
                        next.add(new NarrativePosition(scene.id(), branch.thenNode()));
                        next.add(new NarrativePosition(scene.id(), branch.elseNode()));
                    }
                    case NarrativeNode.End end -> end.nextScene().ifPresent(id -> next.add(new NarrativePosition(id, story.scene(id).entryNode())));
                    default -> { continue; }
                }
                graph.put(new NarrativePosition(scene.id(), node.id()), next);
            }
        }
        Map<NarrativePosition, Integer> indegrees = new HashMap<>();
        graph.keySet().forEach(id -> indegrees.put(id, 0));
        graph.values().forEach(next -> next.stream().filter(graph::containsKey).forEach(id -> indegrees.merge(id, 1, Integer::sum)));
        ArrayDeque<NarrativePosition> ready = new ArrayDeque<>();
        indegrees.forEach((id, degree) -> { if (degree == 0) ready.add(id); });
        int visited = 0;
        while (!ready.isEmpty()) {
            NarrativePosition current = ready.remove();
            visited++;
            for (NarrativePosition next : graph.get(current)) {
                if (graph.containsKey(next) && indegrees.merge(next, -1, Integer::sum) == 0) ready.add(next);
            }
        }
        if (visited != graph.size()) {
            NarrativePosition cycle = indegrees.entrySet().stream().filter(e -> e.getValue() > 0).findFirst().orElseThrow().getKey();
            fail("cena " + cycle.sceneId() + " / nó " + cycle.nodeId(), "Ciclo de nós automáticos sem espera por entrada");
        }
    }

    private static List<NarrativeEffect> effects(NarrativeNode node) {
        return switch (node) {
            case NarrativeNode.Event event -> event.effects();
            case NarrativeNode.End end -> end.effects();
            case NarrativeNode.Choice choice -> choice.options().stream().flatMap(o -> o.effects().stream()).toList();
            default -> List.of();
        };
    }

    private Story.Scene sceneRef(Story story, String id, String c) {
        Story.Scene scene = story.scenes().get(id);
        if (scene == null) fail(c, "Cena inexistente: " + id);
        return scene;
    }

    private NarrativeNode nodeRef(Story.Scene scene, String id, String c) {
        NarrativeNode node = scene.nodes().get(id);
        if (node == null) fail(c, "Nó inexistente: " + id);
        return node;
    }

    private void reference(Set<String> known, String id, String c, String message) {
        if (!known.contains(id)) fail(c, message + ": " + id);
    }

    private void fail(String c, String message) { throw new StoryLoadException(source, c, message); }
}
