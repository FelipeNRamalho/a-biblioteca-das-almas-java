package br.com.bibliotecadasalmas.core;

import br.com.bibliotecadasalmas.narrative.NarrativeNode;
import br.com.bibliotecadasalmas.narrative.NarrativeSession;
import br.com.bibliotecadasalmas.narrative.OpeningContent;
import br.com.bibliotecadasalmas.narrative.Story;
import br.com.bibliotecadasalmas.narrative.StoryLoader;
import java.util.List;

/** Organiza transições de telas e progresso, sem depender do Graphics2D. */
public final class GameController {
    private final SaveManager saves;
    private final GameSettings settings;
    private final Story story;
    private final GameState state;
    private final NarrativeSession session;
    private final OpeningContent opening;
    public enum Overlay { NONE, INVENTORY, MEMORIES }
    private Overlay overlay = Overlay.NONE;
    private String notice = "";
    private long revision;
    private GameMode mode = GameMode.MENU;
    private int introPage;
    private boolean inspectingBook;

    public GameController() { this(new SaveManager()); }

    public GameController(SaveManager saves) {
        this.saves = saves;
        this.settings = new GameSettings();
        saves.readSettings(settings);
        story = new StoryLoader().loadDefault();
        state = story.newGameState();
        session = new NarrativeSession(story, state);
        opening = OpeningContent.load(story);
    }

    public GameMode mode() { return mode; }
    public GameSettings settings() { return settings; }
    public GameState state() { return state; }
    public Story story() { return story; }
    public NarrativeSession session() { return session; }
    public OpeningContent opening() { return opening; }
    public Overlay overlay() { return overlay; }
    public String notice() { return notice; }
    public long revision() { return revision; }
    public boolean hasSave() { return saves.hasSave(); }
    public int introPage() { return introPage; }
    public boolean inspectingBook() { return inspectingBook; }

    public void newGame() {
        state.reset();
        introPage = 0;
        inspectingBook = false;
        overlay = Overlay.NONE;
        notice = "";
        session.start(story.metadata().entryScene());
        mode = GameMode.STORY;
        checkpoint();
    }

    public void continueGame() {
        if (!saves.hasSave()) return;
        if (saves.hasStorySave()) {
            try {
                StoryProgress progress = saves.readStoryProgress();
                GameState candidate = progress.restore(story);
                if (!opening.supports(candidate.position().sceneId()) || !opening.locations().containsKey(candidate.locationId())) {
                    throw new IllegalArgumentException("Save fora do trecho disponível");
                }
                NarrativeSession candidateSession = new NarrativeSession(story, candidate);
                candidateSession.restore(progress.active(), progress.pendingChoice());
                if (!progress.active() && !(candidateSession.node() instanceof NarrativeNode.End)
                        && opening.pauses().stream().noneMatch(p -> p.scene().equals(candidate.position().sceneId())
                        && p.node().equals(candidate.position().nodeId()) && candidate.isInteractionCompleted(p.marker()))) {
                    throw new IllegalArgumentException("Ponto de exploração inválido");
                }
                state.replaceWith(candidate);
                session.restore(progress.active(), progress.pendingChoice());
                mode = GameMode.STORY;
                overlay = Overlay.NONE;
                notice = "";
                revision++;
            } catch (RuntimeException error) {
                notice = "Não foi possível continuar a história. O salvamento foi preservado.";
                revision++;
            }
            return;
        }
        SaveManager.Progress progress = saves.readProgress();
        mode = progress.mode();
        introPage = progress.introPage();
        inspectingBook = false;
    }

    public void nextIntroPage() {
        if (mode != GameMode.INTRO) return;
        if (introPage < 2) introPage++;
        else mode = GameMode.EXPLORACAO;
        saves.writeProgress(mode, introPage);
    }

    public void toggleBook() { if (mode == GameMode.EXPLORACAO) inspectingBook = !inspectingBook; }
    public void openSettings() { mode = GameMode.CONFIGURACOES; }
    public void openCredits() { mode = GameMode.CREDITOS; }
    public void returnToMenu() {
        if (mode == GameMode.STORY) checkpoint();
        mode = GameMode.MENU; inspectingBook = false; overlay = Overlay.NONE;
    }
    public void updateSettings() { saves.writeSettings(settings); }

    public void advanceStory() {
        if (mode != GameMode.STORY || overlay != Overlay.NONE || !session.active()) return;
        session.advance();
        afterStoryAction();
    }

    public void chooseStory(String optionId) {
        if (mode != GameMode.STORY || overlay != Overlay.NONE) return;
        session.choose(optionId);
        afterStoryAction();
    }

    public void confirmStoryChoice() {
        if (mode != GameMode.STORY || overlay != Overlay.NONE) return;
        session.confirm(); afterStoryAction();
    }

    public void cancelStoryChoice() {
        if (mode != GameMode.STORY || overlay != Overlay.NONE) return;
        session.cancelConfirmation(); checkpoint();
    }

    public List<OpeningContent.Hotspot> hotspots() {
        if (!exploring()) return List.of();
        return opening.locations().get(state.locationId()).hotspots().stream()
                .filter(h -> h.hideWhenFlag() == null || !state.flag(h.hideWhenFlag()))
                .filter(h -> !h.kind().equals("investigation") || story.scene(h.target()).optional() || !session.sceneCompleted(h.target()))
                .toList();
    }

    public void interact(String hotspotId) {
        if (!exploring()) return;
        hotspots().stream().filter(h -> h.id().equals(hotspotId)).findFirst()
                .ifPresent(h -> h.interaction().interact(this));
    }

    public void investigate(String sceneId) {
        if (!exploring() || hotspots().stream().noneMatch(h -> h.kind().equals("investigation") && h.target().equals(sceneId))) return;
        requireOpening(sceneId);
        String node = opening.pauses().stream().filter(p -> p.scene().equals(sceneId) && state.isInteractionCompleted(p.marker()))
                .map(OpeningContent.Pause::node).findFirst().orElse(story.scene(sceneId).entryNode());
        session.startAt(sceneId, node);
        afterStoryAction();
    }

    public void travel(String locationId, String gateScene) {
        if (!exploring() || !opening.locations().containsKey(locationId)) return;
        if (hotspots().stream().noneMatch(h -> h.kind().equals("passage") && h.target().equals(locationId)
                && java.util.Objects.equals(h.gateScene(), gateScene))) return;
        if (!state.isLocationUnlocked(locationId)) {
            if (gateScene == null) return;
            requireOpening(gateScene);
            session.start(gateScene);
        } else {
            state.moveToLocation(locationId);
            session.suspend();
            String entry = opening.locations().get(locationId).entryScene();
            if (entry != null && !session.sceneCompleted(entry)) {
                var pause = opening.pauses().stream().filter(p -> p.scene().equals(entry) && state.isInteractionCompleted(p.marker())).findFirst();
                if (pause.isPresent()) {
                    session.startAt(entry, pause.get().node()); session.suspend();
                } else session.start(entry);
            }
        }
        afterStoryAction();
    }

    public void openOverlay(Overlay overlay) {
        if (mode != GameMode.STORY || session.active()) return;
        this.overlay = overlay; revision++;
    }

    public void closeOverlay() { overlay = Overlay.NONE; revision++; }

    private boolean exploring() { return mode == GameMode.STORY && !session.active() && overlay == Overlay.NONE; }

    private void afterStoryAction() {
        if (session.ended() && opening.sceneExits().get(session.scene().id()).equals("follow")) {
            NarrativeNode.End end = (NarrativeNode.End) session.node();
            String next = end.nextScene().orElseThrow();
            requireOpening(next);
            session.start(next);
        }
        if (session.active()) {
            opening.pauses().stream().filter(p -> p.scene().equals(state.position().sceneId())
                    && p.node().equals(state.position().nodeId()) && !state.isInteractionCompleted(p.marker()))
                    .findFirst().ifPresent(p -> { state.completeInteraction(p.marker()); session.suspend(); });
        }
        checkpoint();
    }

    private void checkpoint() {
        revision++;
        if (mode != GameMode.STORY) return;
        try {
            saves.writeStoryProgress(StoryProgress.capture(story, state, session));
            notice = "";
        } catch (IllegalStateException error) { notice = error.getMessage(); }
    }

    private void requireOpening(String sceneId) {
        if (!opening.supports(sceneId)) throw new IllegalArgumentException("Cena fora do trecho disponível: " + sceneId);
    }
}
