package br.com.bibliotecadasalmas.ui;

import br.com.bibliotecadasalmas.assets.AssetManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.font.TextAttribute;
import java.util.Map;

/** Botão com ornamentação do UI Kit. Implementação concreta polimórfica de ClickTarget. */
public final class GothicButton extends AbstractControl {
    private final AssetManager assets;

    public GothicButton(AssetManager assets, Rectangle bounds, String label, boolean enabled, Runnable command) {
        super(bounds, label, enabled, command);
        this.assets = assets;
    }

    @Override
    public void draw(Graphics2D g, boolean hovered, boolean selected) {
        BufferedImage picture = assets.get("ui/escolhas/" +
            (enabled() && (hovered || selected) ? "escolha_hover.png" : "escolha_normal.png"));
        Composite previous = g.getComposite();
        if (!enabled()) g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.42f));
        g.drawImage(picture, bounds.x, bounds.y, bounds.width, bounds.height, null);
        g.setComposite(previous);

        Font font = new Font("Serif", Font.BOLD, 19).deriveFont(Map.of(TextAttribute.TRACKING, 0.06f));
        g.setFont(font);
        g.setColor(!enabled() ? new Color(139, 128, 133) :
                hovered || selected ? new Color(255, 237, 210) : new Color(223, 202, 177));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, bounds.x + (bounds.width - fm.stringWidth(label))/2,
                bounds.y + (bounds.height + fm.getAscent() - fm.getDescent())/2 - 2);
    }
}
