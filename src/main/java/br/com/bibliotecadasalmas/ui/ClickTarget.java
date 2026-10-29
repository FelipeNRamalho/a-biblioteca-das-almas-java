package br.com.bibliotecadasalmas.ui;

import java.awt.Graphics2D;

/** Abstração comum de elementos clicáveis do menu. */
public interface ClickTarget {
    boolean contains(int x, int y);
    boolean enabled();
    void click();
    void draw(Graphics2D graphics, boolean hovered, boolean selected);
}
