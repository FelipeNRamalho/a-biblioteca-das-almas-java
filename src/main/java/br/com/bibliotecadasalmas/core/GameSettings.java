package br.com.bibliotecadasalmas.core;

/** Estado encapsulado das preferências da interface. */
public final class GameSettings {
    private boolean effectsEnabled = true;
    private boolean fullscreen = false;
    private int textSpeed = 1; // 0: lenta; 1: normal; 2: rápida

    public boolean effectsEnabled() { return effectsEnabled; }
    public void toggleEffects() { effectsEnabled = !effectsEnabled; }

    public boolean fullscreen() { return fullscreen; }
    public void setFullscreen(boolean fullscreen) { this.fullscreen = fullscreen; }

    public int textSpeed() { return textSpeed; }
    public void cycleTextSpeed() { textSpeed = (textSpeed + 1) % 3; }
    public String speedName() { return switch (textSpeed) {
        case 0 -> "LENTA";
        case 2 -> "RÁPIDA";
        default -> "NORMAL";
    }; }
    public double charactersPerSecond() { return switch (textSpeed) {
        case 0 -> 16.0;
        case 2 -> 64.0;
        default -> 32.0;
    }; }

    void load(boolean effects, int speed, boolean fullscreen) {
        this.effectsEnabled = effects;
        this.textSpeed = Math.max(0, Math.min(2, speed));
        this.fullscreen = fullscreen;
    }
}
