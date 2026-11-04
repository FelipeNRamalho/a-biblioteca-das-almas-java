package br.com.bibliotecadasalmas.ui;

import br.com.bibliotecadasalmas.assets.AssetManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/** Área de investigação com rótulo clicável, sem cobrir a ilustração do cenário. */
final class HotspotControl extends AbstractControl {
    private final AssetManager assets;

    HotspotControl(AssetManager assets, Rectangle bounds, String label, Runnable command) {
        super(bounds, label, true, command);
        this.assets = assets;
    }

    private Rectangle tag() {
        int width = Math.max(bounds.width, 220);
        return new Rectangle(bounds.x + (bounds.width - width) / 2, bounds.y + bounds.height - 34, width, 34);
    }

    @Override public boolean contains(int x, int y) { return bounds.contains(x, y) || tag().contains(x, y); }

    @Override public void draw(Graphics2D g, boolean hovered, boolean selected) {
        if (hovered || selected) {
            g.setColor(new Color(225, 190, 128, 22));
            g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 15, 15);
            g.setColor(new Color(225, 190, 128, 150));
            g.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 15, 15);
        }
        Rectangle tag = tag();
        g.drawImage(assets.get("ui/escolhas/" + ((hovered || selected) ? "escolha_hover.png" : "escolha_normal.png")),
                tag.x, tag.y, tag.width, tag.height, null);
        g.setFont(new Font("Serif", Font.BOLD, 15));
        g.setColor(new Color(245, 226, 196));
        g.drawString(label, tag.x + (tag.width - g.getFontMetrics().stringWidth(label)) / 2, tag.y + 23);
    }
}
