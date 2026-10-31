package br.com.bibliotecadasalmas.core;

/** Organiza transições de telas e progresso, sem depender do Graphics2D. */
public final class GameController {
    private final SaveManager saves;
    private final GameSettings settings;
    private final GameState state = new GameState();
    private GameMode mode = GameMode.MENU;
    private int introPage;
    private boolean inspectingBook;

    public GameController() { this(new SaveManager()); }

    public GameController(SaveManager saves) {
        this.saves = saves;
        this.settings = new GameSettings();
        saves.readSettings(settings);
    }

    public GameMode mode() { return mode; }
    public GameSettings settings() { return settings; }
    public GameState state() { return state; }
    public boolean hasSave() { return saves.hasSave(); }
    public int introPage() { return introPage; }
    public boolean inspectingBook() { return inspectingBook; }

    public void newGame() {
        state.reset();
        introPage = 0;
        inspectingBook = false;
        mode = GameMode.INTRO;
        saves.writeProgress(mode, introPage);
    }

    public void continueGame() {
        if (!saves.hasSave()) return;
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
    public void returnToMenu() { mode = GameMode.MENU; inspectingBook = false; }
    public void updateSettings() { saves.writeSettings(settings); }
}
