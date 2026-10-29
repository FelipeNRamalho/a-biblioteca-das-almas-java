package br.com.bibliotecadasalmas.ui;

import java.awt.*;

/** Outra implementação de ClickTarget: opções pequenas em cena, sem textura de botão. */
public final class TextOption extends AbstractControl {
    public TextOption(Rectangle bounds, String label, Runnable command) {
        super(bounds, label, true, command);
    }

    @Override
    public void draw(Graphics2D g, boolean hovered, boolean selected) {
        g.setColor(hovered || selected ? new Color(125, 49, 67, 226) : new Color(32, 23, 33, 225));
        g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 12, 12);
        g.setColor(new Color(176, 139, 94, hovered ? 240 : 170));
        g.setStroke(new BasicStroke(1.4f));
        g.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 12, 12);
        g.setFont(new Font("Serif", Font.PLAIN, 19));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(new Color(241, 221, 200));
        g.drawString(label, bounds.x + (bounds.width-fm.stringWidth(label))/2,
                bounds.y + (bounds.height + fm.getAscent()-fm.getDescent())/2 - 1);
    }
}
