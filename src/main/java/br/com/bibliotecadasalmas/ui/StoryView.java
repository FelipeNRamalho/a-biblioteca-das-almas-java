package br.com.bibliotecadasalmas.ui;

import br.com.bibliotecadasalmas.assets.AssetManager;
import br.com.bibliotecadasalmas.core.GameController;
import br.com.bibliotecadasalmas.core.GameController.Overlay;
import br.com.bibliotecadasalmas.narrative.NarrativeNode;
import br.com.bibliotecadasalmas.narrative.OpeningContent;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;

/** Apresentação do trecho JSON em coordenadas lógicas; não concede itens nem interpreta efeitos. */
final class StoryView {
    private static final Color TEXT = new Color(244, 227, 204);
    private static final Color GOLD = new Color(213, 177, 125);
    private final GameController controller;
    private final AssetManager assets;
    private final LongSupplier clock;
    private final List<ClickTarget> controls = new ArrayList<>();
    private long revision = -1;
    private long lineStarted;
    private boolean revealed;
    private int hover = -1;
    private int selection = -1;
    private int documentIndex;

    StoryView(GameController controller, AssetManager assets, LongSupplier clock) {
        this.controller = controller; this.assets = assets; this.clock = clock;
    }

    private void sync() {
        if (revision != controller.revision()) {
            revision = controller.revision(); lineStarted = clock.getAsLong(); revealed = false;
            hover = -1; selection = -1; documentIndex = 0;
        }
    }

    void draw(Graphics2D g) {
        sync();
        var session = controller.session();
        String background = controller.opening().locations().get(controller.state().locationId()).background();
        if (session.active() && session.node() instanceof NarrativeNode.Cutscene cutscene) {
            background = controller.opening().cutscenes().get(cutscene.asset());
        }
        cover(g, assets.get(background));
        g.setPaint(new GradientPaint(0, 0, new Color(8, 6, 14, 220), 0, 155, new Color(8, 6, 14, 0)));
        g.fillRect(0, 0, 1280, 155);
        text(g, "CAPÍTULO I", 45, 42, 15, GOLD);
        text(g, controller.opening().locations().get(controller.state().locationId()).title(), 45, 81, 29, TEXT);
        if (controller.overlay() != Overlay.NONE) drawJournal(g);
        else if (session.active()) {
            switch (session.node()) {
                case NarrativeNode.Line line -> drawLine(g, line);
                case NarrativeNode.Choice choice -> drawChoice(g, choice);
                case NarrativeNode.Cutscene ignored -> {
                    shade(g, 535, 185);
                    text(g, session.scene().title(), 58, 608, 30, TEXT);
                    text(g, "Clique para continuar  ›", 58, 655, 19, GOLD);
                }
                default -> throw new IllegalStateException("Nó automático chegou à apresentação");
            }
        } else {
            shade(g, 628, 92);
            if (controller.state().flag("livro_mutavel_visto")) {
                text(g, controller.state().hasItem("carta_entrada")
                        ? "Trecho concluído. Consulte a carta e a lembrança que encontrou."
                        : "Trecho concluído. A carta ainda pode ser investigada na escadaria.", 545, 681, 16, TEXT);
            } else text(g, "Clique nos objetos e nas passagens para investigar.", 545, 681, 16, TEXT);
        }
        buildControls();
        for (int i = 0; i < controls.size(); i++) controls.get(i).draw(g, i == hover, i == selection);
        if (!controller.notice().isBlank()) {
            g.setColor(new Color(28, 9, 17, 240)); g.fillRoundRect(40, 100, 1190, 65, 12, 12);
            wrapped(g, controller.notice(), 62, 128, 1140, 21, 16, TEXT);
        }
    }

    private void drawLine(Graphics2D g, NarrativeNode.Line line) {
        var portrait = controller.opening().portrait(line);
        panel(g, 42, 460, 1196, 240);
        portrait.ifPresent(path -> contain(g, assets.get(path), 45, 352, 285, 340));
        int left = portrait.isPresent() ? 345 : 180;
        text(g, controller.opening().speakers().get(line.speaker()).name(), portrait.isPresent() ? 345 : 200, 505, 20, GOLD);
        wrapped(g, line.text().substring(0, visible(line.text())), left, 548, 1080 - left, 30, 22, TEXT);
        text(g, visible(line.text()) == line.text().length() ? "CLIQUE PARA CONTINUAR  ›" : "CLIQUE PARA REVELAR  ›",
                790, 676, 13, GOLD);
    }

    private int visible(String text) {
        return revealed ? text.length() : Math.min(text.length(), (int) ((clock.getAsLong() - lineStarted)
                / 1_000_000_000.0 * controller.settings().charactersPerSecond()));
    }

    private void drawChoice(Graphics2D g, NarrativeNode.Choice choice) {
        g.setColor(new Color(9, 5, 13, 180)); g.fillRect(0, 0, 1280, 720);
        modalPanel(g, 260, 165, 960, 470);
        if (controller.session().pendingChoice().isPresent()) {
            var confirmation = choice.confirmation().orElseThrow();
            text(g, confirmation.title(), 375, 235, 26, GOLD);
            wrapped(g, confirmation.message(), 375, 285, 720, 30, 22, TEXT);
        } else wrapped(g, choice.prompt(), 375, 235, 720, 30, 24, TEXT);
    }

    private List<OpeningContent.Document> documents() {
        String kind = controller.overlay() == Overlay.INVENTORY ? "item" : "memory";
        return controller.opening().documents().stream().filter(d -> d.kind().equals(kind) && d.available(controller.state())).toList();
    }

    private void drawJournal(Graphics2D g) {
        g.setColor(new Color(9, 5, 13, 195)); g.fillRect(0, 0, 1280, 720);
        modalPanel(g, 65, 135, 1150, 545);
        g.setColor(new Color(169, 131, 86, 90)); g.drawLine(445, 235, 445, 570);
        text(g, controller.overlay() == Overlay.INVENTORY ? "Inventário" : "Diário de memórias", 110, 200, 31, GOLD);
        List<OpeningContent.Document> documents = documents();
        if (documents.isEmpty()) {
            wrapped(g, controller.overlay() == Overlay.INVENTORY ? "Nenhum objeto foi recolhido."
                    : "Há lembranças que ainda não voltaram.", 470, 285, 650, 32, 23, TEXT);
        } else {
            OpeningContent.Document document = documents.get(Math.min(documentIndex, documents.size() - 1));
            text(g, document.title(), 470, 268, 24, GOLD);
            wrapped(g, controller.opening().documentText(document, controller.story()), 470, 320, 650, 31, 22, TEXT);
        }
    }

    private void buildControls() {
        controls.clear();
        button(1070, 20, 165, 44, "MENU", controller::returnToMenu);
        if (controller.overlay() != Overlay.NONE) {
            List<OpeningContent.Document> documents = documents();
            for (int i = 0; i < documents.size(); i++) {
                int index = i;
                button(110, 240 + 68 * i, 320, 55, documents.get(i).title(), () -> documentIndex = index);
            }
            button(955, 598, 220, 48, "VOLTAR", controller::closeOverlay);
        } else if (controller.session().active()) {
            if (controller.session().node() instanceof NarrativeNode.Choice choice) {
                if (controller.session().pendingChoice().isPresent()) {
                    var confirmation = choice.confirmation().orElseThrow();
                    button(375, 430, 720, 62, confirmation.confirmLabel(), controller::confirmStoryChoice);
                    button(375, 510, 720, 62, confirmation.cancelLabel(), controller::cancelStoryChoice);
                } else {
                    var options = controller.session().options();
                    for (int i = 0; i < options.size(); i++) {
                        var option = options.get(i);
                        button(375, 290 + 80 * i, 720, 62, option.text(), () -> controller.chooseStory(option.id()));
                    }
                }
            }
        } else {
            controller.hotspots().forEach(h -> controls.add(new HotspotControl(assets, h.bounds(), h.label(), () -> controller.interact(h.id()))));
            button(40, 650, 220, 46, "INVENTÁRIO · " + controller.state().inventory().size(), () -> controller.openOverlay(Overlay.INVENTORY));
            button(275, 650, 240, 46, "MEMÓRIAS · " + controller.state().memories().size() + "/3", () -> controller.openOverlay(Overlay.MEMORIES));
        }
    }

    boolean hover(int x, int y) {
        if (x < 0 || y < 0 || x >= 1280 || y >= 720) { hover = -1; return false; }
        sync(); buildControls(); hover = -1;
        for (int i = 0; i < controls.size(); i++) if (controls.get(i).contains(x, y)) { hover = i; break; }
        return hover >= 0 || (controller.overlay() == Overlay.NONE && controller.session().active()
                && !(controller.session().node() instanceof NarrativeNode.Choice));
    }

    void click(int x, int y) {
        if (x < 0 || y < 0 || x >= 1280 || y >= 720) return;
        sync(); buildControls();
        for (ClickTarget control : List.copyOf(controls)) {
            if (control.contains(x, y)) { control.click(); return; }
        }
        advance();
    }

    private void advance() {
        if (controller.overlay() != Overlay.NONE || !controller.session().active()) return;
        if (controller.session().node() instanceof NarrativeNode.Line line && visible(line.text()) < line.text().length()) {
            revealed = true;
        } else if (!(controller.session().node() instanceof NarrativeNode.Choice)) controller.advanceStory();
    }

    void key(int key) {
        sync(); buildControls();
        if (key == KeyEvent.VK_ESCAPE) {
            if (controller.overlay() != Overlay.NONE) controller.closeOverlay();
            else if (controller.session().pendingChoice().isPresent()) controller.cancelStoryChoice();
            else controller.returnToMenu();
        } else if (key == KeyEvent.VK_I) controller.openOverlay(Overlay.INVENTORY);
        else if (key == KeyEvent.VK_J) controller.openOverlay(Overlay.MEMORIES);
        else if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) {
            int direction = key == KeyEvent.VK_DOWN ? 1 : -1;
            if (selection < 0) selection = 0;
            selection = Math.floorMod(selection + direction, controls.size());
        } else if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE) {
            if (controller.session().active() && controller.overlay() == Overlay.NONE
                    && !(controller.session().node() instanceof NarrativeNode.Choice)) advance();
            else if (selection >= 0 && selection < controls.size()) controls.get(selection).click();
        }
    }

    private void button(int x, int y, int w, int h, String label, Runnable action) {
        controls.add(new GothicButton(assets, new Rectangle(x, y, w, h), label, true, action));
    }

    private void panel(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(15, 9, 20, 240)); g.fillRoundRect(x, y, w, h, 18, 18);
        g.drawImage(assets.get("ui/dialogos/caixa_dialogo.png"), x, y, w, h, null);
    }

    private void modalPanel(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(18, 11, 23, 245)); g.fillRoundRect(x, y, w, h, 18, 18);
        g.setColor(new Color(182, 141, 95, 190)); g.drawRoundRect(x, y, w, h, 18, 18);
        g.setColor(new Color(111, 76, 72, 120)); g.drawRoundRect(x + 8, y + 8, w - 16, h - 16, 13, 13);
        g.drawImage(assets.get("ui/ornamentos/canto_superior_esquerdo.png"), x - 12, y - 12, 100, 55, null);
        g.drawImage(assets.get("ui/ornamentos/canto_inferior_direito.png"), x + w - 120, y + h - 77, 120, 77, null);
    }

    private void shade(Graphics2D g, int y, int height) {
        g.setPaint(new GradientPaint(0, y, new Color(8, 5, 13, 10), 0, y + height / 2, new Color(8, 5, 13, 240)));
        g.fillRect(0, y, 1280, height);
    }

    private static void text(Graphics2D g, String value, int x, int y, int size, Color color) {
        g.setFont(new Font("Serif", Font.PLAIN, size)); g.setColor(color); g.drawString(value, x, y);
    }

    private static void wrapped(Graphics2D g, String value, int x, int y, int width, int lineHeight, int size, Color color) {
        g.setFont(new Font("Serif", Font.PLAIN, size)); g.setColor(color);
        for (String paragraph : value.split("\n", -1)) {
            String line = "";
            for (String word : paragraph.split(" ")) {
                String candidate = line.isEmpty() ? word : line + " " + word;
                if (!line.isEmpty() && g.getFontMetrics().stringWidth(candidate) > width) {
                    g.drawString(line, x, y); y += lineHeight; line = word;
                } else line = candidate;
            }
            g.drawString(line, x, y); y += lineHeight;
        }
    }

    private static void cover(Graphics2D g, BufferedImage image) {
        double scale = Math.max(1280.0 / image.getWidth(), 720.0 / image.getHeight());
        int width = (int) Math.ceil(image.getWidth() * scale), height = (int) Math.ceil(image.getHeight() * scale);
        g.drawImage(image, (1280 - width) / 2, (720 - height) / 2, width, height, null);
    }

    private static void contain(Graphics2D g, BufferedImage image, int x, int y, int width, int height) {
        double scale = Math.min((double) width / image.getWidth(), (double) height / image.getHeight());
        int w = (int) Math.round(image.getWidth() * scale), h = (int) Math.round(image.getHeight() * scale);
        g.drawImage(image, x + (width - w) / 2, y + height - h, w, h, null);
    }
}
