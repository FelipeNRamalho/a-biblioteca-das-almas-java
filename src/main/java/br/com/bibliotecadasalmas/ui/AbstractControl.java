package br.com.bibliotecadasalmas.ui;

import java.awt.Rectangle;

/** Herança de propriedades e geometria comum aos componentes de interface. */
public abstract class AbstractControl implements ClickTarget {
    protected final Rectangle bounds;
    protected final String label;
    protected final Runnable command;
    private final boolean enabled;

    protected AbstractControl(Rectangle bounds, String label, boolean enabled, Runnable command) {
        this.bounds = new Rectangle(bounds);
        this.label = label;
        this.command = command;
        this.enabled = enabled;
    }

    @Override public boolean contains(int x, int y) { return bounds.contains(x, y); }
    @Override public boolean enabled() { return enabled; }
    @Override public void click() { if (enabled) command.run(); }
}
