package br.com.bibliotecadasalmas.ui;

import br.com.bibliotecadasalmas.assets.AssetManager;
import br.com.bibliotecadasalmas.core.GameController;
import br.com.bibliotecadasalmas.core.GameMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * View do menu em Java 2D. Toda a arte é composta em tempo de execução:
 * nenhum PNG contém o texto dos botões ou a lógica de navegação.
 */
public final class GamePanel extends JPanel {
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    private static final Color GOLD = new Color(190, 152, 104);
    private static final Color PARCHMENT = new Color(237, 220, 194);
    private static final String[] INTRO = {
            "Natasha despertou sobre os degraus frios de uma biblioteca que jamais vira antes.",
            "O crepúsculo parecia imóvel. Sua memória, no entanto, estava cheia de lacunas.",
            "Diante dela, as portas guardavam algo que talvez tivesse sido escrito antes de sua chegada."
    };

    private final AssetManager assets = new AssetManager();
    private final GameController controller;
    private final Runnable exit;
    private final Runnable toggleFullscreen;
    private final List<ClickTarget> controls = new ArrayList<>();
    private int hover = -1;
    private int keyboardSelection = -1;
    private long pageStarted = System.nanoTime();
    private long frameStart = System.nanoTime();
    private int previousPage = -1;
    private GameMode previousMode;

    public GamePanel(GameController controller, Runnable exit, Runnable toggleFullscreen, boolean timerOn) {
        this.controller = controller;
        this.exit = exit;
        this.toggleFullscreen = toggleFullscreen;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) { updateHover(e.getX(), e.getY()); }
            @Override public void mouseDragged(MouseEvent e) { updateHover(e.getX(), e.getY()); }
        });
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) handleClick(e.getX(), e.getY());
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { handleKey(e.getKeyCode()); }
        });
        if (timerOn) new Timer(33, e -> repaint()).start();
    }

    private void checkScreenChange() {
        if (previousMode != controller.mode() || previousPage != controller.introPage()) {
            previousMode = controller.mode();
            previousPage = controller.introPage();
            pageStarted = System.nanoTime();
            hover = -1;
            keyboardSelection = -1;
        }
    }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        render(g, getWidth(), getHeight());
        g.dispose();
    }

    /** Também usado para gerar um preview real da interface, sem janela. */
    public void render(Graphics2D g, int width, int height) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        double scale = Math.min(width / (double) WIDTH, height / (double) HEIGHT);
        int offsetX = (int) Math.round((width - WIDTH * scale) / 2);
        int offsetY = (int) Math.round((height - HEIGHT * scale) / 2);
        g.translate(offsetX, offsetY);
        g.scale(scale, scale);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        checkScreenChange();
        controls.clear();
        switch (controller.mode()) {
            case MENU -> drawMenu(g);
            case CONFIGURACOES -> drawSettings(g);
            case CREDITOS -> drawCredits(g);
            case INTRO -> drawIntro(g);
            case EXPLORACAO -> drawDemo(g);
        }
        for (int i = 0; i < controls.size(); i++) {
            controls.get(i).draw(g, i == hover, i == keyboardSelection);
        }
    }

    private void drawBackdrop(Graphics2D g) {
        BufferedImage salon = assets.get("menu/salao_principal.png");
        cover(g, salon, 0, 0, WIDTH, HEIGHT);
        g.setPaint(new GradientPaint(0, 0, new Color(14, 10, 19, 247),
                790, 0, new Color(17, 12, 24, 145)));
        g.fillRect(0, 0, 790, HEIGHT);
        g.setPaint(new GradientPaint(720, 0, new Color(17, 12, 24, 136),
                WIDTH, 0, new Color(13, 10, 18, 30)));
        g.fillRect(720, 0, 560, HEIGHT);
        g.setPaint(new GradientPaint(0, 0, new Color(5, 4, 8, 50),
                0, HEIGHT, new Color(5, 4, 8, 190)));
        g.fillRect(0, 0, WIDTH, HEIGHT);
    }

    private void drawMenu(Graphics2D g) {
        drawBackdrop(g);
        renderDust(g, 30);
        image(g, "ui/ornamentos/emblema_rosa.png", 112, 49, 74, 86);
        drawTracked(g, "PRÓLOGO  ·  UMA HISTÓRIA ENTRE VIDAS", 195, 102,
                new Font("Serif", Font.PLAIN, 15), new Color(208, 183, 151), 0.10f);
        drawTitleShadow(g, "A BIBLIOTECA", 111, 199, 61);
        drawTitleShadow(g, "DAS ALMAS", 114, 265, 69);
        drawTracked(g, "Onde até o silêncio tem uma história", 126, 292,
                new Font("Serif", Font.ITALIC, 19), new Color(216, 190, 171), 0.01f);
        image(g, "ui/ornamentos/divisor_rubi.png", 118, 310, 413, 36);

        button(125, 352, 407, 53, "NOVO JOGO", true, controller::newGame);
        button(125, 414, 407, 53, "CONTINUAR", controller.hasSave(), controller::continueGame);
        button(125, 476, 407, 53, "CONFIGURAÇÕES", true, controller::openSettings);
        button(125, 538, 407, 53, "CRÉDITOS", true, controller::openCredits);
        button(125, 600, 407, 53, "SAIR", true, exit);

        g.setColor(new Color(213, 187, 157, 168));
        g.setFont(new Font("Serif", Font.ITALIC, 18));
        g.drawString("\"Nem toda história termina quando sua última página é escrita.\"", 730, 654);
        line(g, 714, 667, 1225, 667, new Color(173, 132, 101, 100), 1);
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.setColor(new Color(183, 166, 159, 145));
        g.drawString("JAVA 2D  ·  CAPÍTULO I", 1051, 689);
    }

    private void drawSettings(Graphics2D g) {
        drawBackdrop(g);
        drawModal(g, "CONFIGURAÇÕES", "Ajustes de apresentação e leitura");
        text(g, "EFEITOS AMBIENTAIS", 354, 281, 18, GOLD, true);
        text(g, "Animação discreta de partículas no menu", 354, 306, 14, new Color(185, 169, 156), false);
        button(729, 257, 215, 51, controller.settings().effectsEnabled() ? "ATIVADOS" : "DESATIVADOS",
                true, () -> { controller.settings().toggleEffects(); controller.updateSettings(); });

        text(g, "VELOCIDADE DO TEXTO", 354, 369, 18, GOLD, true);
        text(g, "Controla a revelação das frases no prólogo", 354, 394, 14, new Color(185, 169, 156), false);
        button(729, 345, 215, 51, controller.settings().speedName(),
                true, () -> { controller.settings().cycleTextSpeed(); controller.updateSettings(); });

        text(g, "EXIBIÇÃO", 354, 457, 18, GOLD, true);
        text(g, "Alternar janela ou tela cheia (F11)", 354, 482, 14, new Color(185, 169, 156), false);
        button(729, 433, 215, 51, "ALTERNAR", true, toggleFullscreen);
        button(490, 552, 300, 54, "VOLTAR", true, controller::returnToMenu);
    }

    private void drawCredits(Graphics2D g) {
        drawBackdrop(g);
        drawModal(g, "CRÉDITOS", "Um universo criado entre páginas e sombras");
        textCenter(g, "A BIBLIOTECA DAS ALMAS", 640, 282, 27, PARCHMENT, true);
        textCenter(g, "Projeto acadêmico  ·  RPG narrativo 2D", 640, 318, 18, GOLD, false);
        image(g, "ui/ornamentos/divisor_flor_de_lis.png", 445, 337, 390, 35);
        textCenter(g, "Arte e personagens: acervo visual do projeto", 640, 407, 18, new Color(225, 201, 174), false);
        textCenter(g, "Interface e programação: Java 21 · Swing · Graphics2D", 640, 443, 17, new Color(225, 201, 174), false);
        textCenter(g, "Versão de demonstração do menu inicial", 640, 478, 15, new Color(177, 160, 151), false);
        button(490, 552, 300, 54, "VOLTAR", true, controller::returnToMenu);
    }

    private void drawModal(Graphics2D g, String title, String subtitle) {
        int x = 296, y = 72, w = 688, h = 574;
        g.setColor(new Color(20, 13, 22, 236));
        g.fillRoundRect(x, y, w, h, 25, 25);
        g.setColor(new Color(164, 124, 85, 185));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x, y, w, h, 25, 25);
        g.setColor(new Color(109, 75, 75, 90));
        g.drawRoundRect(x + 9, y + 9, w - 18, h - 18, 19, 19);
        image(g, "ui/ornamentos/canto_superior_esquerdo.png", x-4, y-5, 152, 87);
        image(g, "ui/ornamentos/canto_inferior_direito.png", x+w-148, y+h-90, 146, 91);
        drawTrackedCenter(g, title, WIDTH/2, 177, 40, PARCHMENT, 0.11f);
        textCenter(g, subtitle, WIDTH/2, 209, 17, new Color(184, 157, 145), false);
        image(g, "ui/ornamentos/divisor_rubi.png", 470, 218, 340, 30);
    }

    private void drawIntro(Graphics2D g) {
        cover(g, assets.get("menu/natasha_despertar.png"), 0, 0, WIDTH, HEIGHT);
        g.setPaint(new GradientPaint(0, 380, new Color(9, 7, 14, 0), 0, 720, new Color(10, 7, 15, 248)));
        g.fillRect(0, 350, WIDTH, 370);
        g.setColor(new Color(15, 11, 20, 214));
        g.fillRoundRect(66, 522, 1148, 155, 17, 17);
        g.setColor(new Color(163, 128, 93, 185));
        g.setStroke(new BasicStroke(1.4f));
        g.drawRoundRect(66, 522, 1148, 155, 17, 17);
        drawTracked(g, "PRÓLOGO  ·  O DESPERTAR", 102, 557,
                new Font("Serif", Font.BOLD, 17), new Color(213, 172, 123), 0.07f);
        String current = INTRO[controller.introPage()];
        int n = visibleChars();
        drawWrapped(g, current.substring(0, Math.min(current.length(), n)), 102, 601, 980, 29,
                new Font("Serif", Font.PLAIN, 24), new Color(247, 228, 209));
        text(g, n >= current.length() ? "CLIQUE PARA CONTINUAR  ›" : "CLIQUE PARA REVELAR  ›",
                927, 657, 15, new Color(216, 187, 149), true);
        text(g, "ESC  ·  MENU", 72, 43, 13, new Color(248, 229, 211), true);
        if (controller.settings().effectsEnabled()) renderDust(g, 6);
    }

    private int visibleChars() {
        long delta = System.nanoTime() - pageStarted;
        int visible = (int)(delta / 1_000_000_000.0 * controller.settings().charactersPerSecond());
        return Math.max(0, Math.min(INTRO[controller.introPage()].length(), visible));
    }

    private void advanceIntro() {
        if (visibleChars() < INTRO[controller.introPage()].length()) {
            pageStarted = System.nanoTime() - (long) (INTRO[controller.introPage()].length() /
                    controller.settings().charactersPerSecond() * 1_000_000_000L);
        } else {
            controller.nextIntroPage();
            checkScreenChange();
        }
    }

    private void drawDemo(Graphics2D g) {
        cover(g, assets.get("menu/salao_principal.png"), 0, 0, WIDTH, HEIGHT);
        g.setPaint(new GradientPaint(0, 0, new Color(12, 8, 16, 211), 0, 156, new Color(12, 8, 16, 0)));
        g.fillRect(0, 0, WIDTH, 156);
        g.setPaint(new GradientPaint(0, 557, new Color(10, 8, 15, 0), 0, 720, new Color(8, 6, 12, 225)));
        g.fillRect(0, 555, WIDTH, 165);
        drawTracked(g, "CAPÍTULO I", 74, 65, new Font("Serif", Font.BOLD, 19), GOLD, 0.10f);
        text(g, "SALÃO PRINCIPAL", 73, 108, 35, PARCHMENT, true);
        text(g, "Protótipo de exploração • cenário disponível no acervo", 77, 134, 14, new Color(216, 190, 171), false);
        controls.add(new TextOption(new Rectangle(75, 622, 253, 52),
                "EXAMINAR O LIVRO", controller::toggleBook));
        controls.add(new TextOption(new Rectangle(973, 622, 228, 52),
                "VOLTAR AO MENU", controller::returnToMenu));
        if (controller.inspectingBook()) {
            g.setColor(new Color(10, 7, 13, 174));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            image(g, "ui/dialogos/caixa_dialogo.png", 200, 438, 880, 226);
            text(g, "NATASHA", 260, 509, 19, GOLD, true);
            drawWrapped(g, "Há um livro aberto sobre a mesa. Algumas páginas parecem ter mudado de lugar sozinhas.",
                    259, 557, 755, 27, new Font("Serif", Font.PLAIN, 22), PARCHMENT);
            text(g, "CLIQUE PARA FECHAR", 851, 629, 14, GOLD, true);
        }
    }

    private void button(int x, int y, int w, int h, String label, boolean enabled, Runnable command) {
        controls.add(new GothicButton(assets, new Rectangle(x, y, w, h), label, enabled, command));
    }

    private void cover(Graphics2D g, BufferedImage image, int x, int y, int w, int h) {
        double scale = Math.max(w/(double)image.getWidth(), h/(double)image.getHeight());
        int sw = (int)Math.ceil(w / scale), sh = (int)Math.ceil(h / scale);
        int sx = Math.max(0, (image.getWidth()-sw)/2), sy = Math.max(0, (image.getHeight()-sh)/2);
        g.drawImage(image, x, y, x+w, y+h, sx, sy, sx+sw, sy+sh, null);
    }

    private void image(Graphics2D g, String path, int x, int y, int w, int h) {
        g.drawImage(assets.get(path), x, y, w, h, null);
    }

    private void renderDust(Graphics2D g, int particles) {
        if (!controller.settings().effectsEnabled()) return;
        double time = (System.nanoTime() - frameStart) / 1e9;
        for (int i = 0; i < particles; i++) {
            double seed = i * 123.693;
            double x = 640 + (Math.sin(seed * 0.7 + time * (0.05 + i%4*0.025)) * 600);
            double y = (seed * 13.37 + time * (2.0+i%5)) % HEIGHT;
            int s = (i%3==0) ? 3 : 2;
            g.setColor(new Color(225, 186, 119, 28 + (i%4)*9));
            g.fillOval((int)x, (int)y, s, s);
        }
    }

    private void text(Graphics2D g, String value, int x, int y, int size, Color color, boolean bold) {
        g.setFont(new Font("Serif", bold?Font.BOLD:Font.PLAIN, size));
        g.setColor(color);
        g.drawString(value, x, y);
    }

    private void textCenter(Graphics2D g, String value, int x, int y, int size, Color color, boolean bold) {
        g.setFont(new Font("Serif", bold?Font.BOLD:Font.PLAIN, size));
        g.setColor(color);
        g.drawString(value, x - g.getFontMetrics().stringWidth(value)/2, y);
    }

    private void drawTitleShadow(Graphics2D g, String txt, int x, int y, int fontSize) {
        Font f = new Font("Serif", Font.BOLD, fontSize).deriveFont(Map.of(TextAttribute.TRACKING, .045f));
        g.setFont(f);
        g.setColor(new Color(0, 0, 0, 210));
        g.drawString(txt, x+3, y+4);
        g.setPaint(new GradientPaint(x, y-48, new Color(248, 234, 213), x, y+8, new Color(193, 149, 111)));
        g.drawString(txt, x, y);
    }

    private void drawTracked(Graphics2D g, String txt, int x, int y, Font font, Color color, float tracking) {
        g.setFont(font.deriveFont(Map.of(TextAttribute.TRACKING, tracking)));
        g.setColor(color);
        g.drawString(txt, x, y);
    }

    private void drawTrackedCenter(Graphics2D g, String txt, int x, int y, int size, Color color, float tracking) {
        Font f = new Font("Serif", Font.BOLD, size).deriveFont(Map.of(TextAttribute.TRACKING, tracking));
        g.setFont(f);
        g.setColor(color);
        g.drawString(txt, x-g.getFontMetrics().stringWidth(txt)/2, y);
    }

    private void drawWrapped(Graphics2D g, String text, int x, int y, int maxWidth, int lineH, Font font, Color color) {
        g.setFont(font);
        g.setColor(color);
        FontMetrics fm = g.getFontMetrics();
        StringBuilder line = new StringBuilder();
        int yy = y;
        for (String word : text.split(" ")) {
            String candidate = line.isEmpty()?word:line+" "+word;
            if (fm.stringWidth(candidate) > maxWidth && !line.isEmpty()) {
                g.drawString(line.toString(), x, yy);
                yy += lineH;
                line = new StringBuilder(word);
            } else line = new StringBuilder(candidate);
        }
        if (!line.isEmpty()) g.drawString(line.toString(), x, yy);
    }

    private void line(Graphics2D g, int x1, int y1, int x2, int y2, Color color, int width) {
        g.setColor(color);
        g.setStroke(new BasicStroke(width));
        g.drawLine(x1,y1,x2,y2);
    }

    private Point toGame(int sx, int sy) {
        double scale = Math.min(getWidth() / (double) WIDTH, getHeight() / (double) HEIGHT);
        double offsetX = (getWidth()-WIDTH*scale)/2;
        double offsetY = (getHeight()-HEIGHT*scale)/2;
        return new Point((int)((sx-offsetX)/scale), (int)((sy-offsetY)/scale));
    }

    private void updateHover(int x, int y) {
        Point p = toGame(x,y);
        int found = -1;
        for (int i=0;i<controls.size();i++) if (controls.get(i).enabled() && controls.get(i).contains(p.x,p.y)) {
            found=i; break;
        }
        hover=found;
        setCursor(Cursor.getPredefinedCursor(found>=0 || controller.mode()==GameMode.INTRO ||
                controller.inspectingBook() ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
    }

    private void handleClick(int x, int y) {
        requestFocusInWindow();
        if (controller.mode()==GameMode.INTRO) { advanceIntro(); repaint(); return; }
        if (controller.mode()==GameMode.EXPLORACAO && controller.inspectingBook()) {
            controller.toggleBook(); repaint(); return;
        }
        Point p=toGame(x,y);
        for (ClickTarget c: List.copyOf(controls)) {
            if (c.enabled() && c.contains(p.x,p.y)) { c.click(); keyboardSelection=-1; break; }
        }
        repaint();
    }

    private void handleKey(int code) {
        if (code == KeyEvent.VK_ESCAPE) {
            if (controller.mode()==GameMode.EXPLORACAO && controller.inspectingBook()) controller.toggleBook();
            else controller.returnToMenu();
            repaint();
            return;
        }
        if (controller.mode()==GameMode.INTRO && (code==KeyEvent.VK_ENTER || code==KeyEvent.VK_SPACE)) {
            advanceIntro(); repaint(); return;
        }
        if (code==KeyEvent.VK_UP || code==KeyEvent.VK_DOWN) {
            if (controls.isEmpty()) return;
            int dir=code==KeyEvent.VK_DOWN?1:-1;
            int idx=keyboardSelection;
            for (int i=0;i<controls.size();i++) {
                idx=(idx+dir+controls.size())%controls.size();
                if (controls.get(idx).enabled()) { keyboardSelection=idx; break; }
            }
            repaint();
        }
        if ((code==KeyEvent.VK_ENTER || code==KeyEvent.VK_SPACE) && keyboardSelection>=0 &&
                keyboardSelection<controls.size() && controls.get(keyboardSelection).enabled()) {
            controls.get(keyboardSelection).click();
            repaint();
        }
    }
}
