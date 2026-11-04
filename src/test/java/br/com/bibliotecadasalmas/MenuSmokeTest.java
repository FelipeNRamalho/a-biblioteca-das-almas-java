package br.com.bibliotecadasalmas;

import br.com.bibliotecadasalmas.core.GameController;
import br.com.bibliotecadasalmas.core.GameMode;
import br.com.bibliotecadasalmas.core.SaveManager;
import br.com.bibliotecadasalmas.narrative.NarrativeNode;
import br.com.bibliotecadasalmas.ui.GamePanel;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

/** Percorre a abertura por mouse/teclado na EDT, sem janela e sem saves pessoais. */
public final class MenuSmokeTest {
    public static void main(String[] args) throws Exception {
        Path output = args.length > 0 ? Path.of(args[0]) : Path.of("target/validacao-opening/previews");
        Files.createDirectories(output);
        SwingUtilities.invokeAndWait(() -> {
            try { opening(output); }
            catch (Exception error) { throw new RuntimeException(error); }
        });
        System.out.println("OK: menu e abertura JSON passaram por cliques, escolhas, diário, retomada, escala e telas legadas. Previews: " + output);
    }

    private static void opening(Path output) throws Exception {
        Path saveDirectory = Files.createTempDirectory("biblioteca-menu-teste-");
        GameController game = new GameController(new SaveManager(saveDirectory));
        GamePanel panel = panel(game);
        image(panel, output.resolve("menu.png"));
        click(panel, 300, 379);
        check(game.mode() == GameMode.STORY && game.session().node() instanceof NarrativeNode.Cutscene,
                "Novo Jogo abre a cutscene do JSON");
        image(panel, output.resolve("despertar-cutscene.png"));
        click(panel, 750, 600);
        image(panel, output.resolve("despertar-dialogo.png"));
        var firstLine = game.state().position();
        click(panel, 640, 360);
        check(game.state().position().equals(firstLine), "Primeiro clique revela sem avançar ou acionar portas");
        image(panel, output.resolve("despertar-texto-revelado.png"));
        click(panel, 750, 600);
        check(!game.state().position().equals(firstLine), "Segundo clique avança a fala");
        readUntilChoiceOrExplore(panel, game);
        check(game.state().flag("prologo_concluido") && !game.session().active(), "Despertar abre a exploração da Entrada");
        image(panel, output.resolve("entrada.png"));

        panel.setSize(1600, 1000);
        image(panel, output.resolve("entrada-redimensionada.png"));
        var before = game.state().position();
        physicalClick(panel, 800, 10);
        check(game.state().position().equals(before), "Barras fora da área lógica não avançam o jogo");
        click(panel, 990, 585);
        var letterLine = game.state().position();
        physicalClick(panel, 800, 49); // Um pixel acima do cenário renderizado.
        click(panel, 700, 620);
        check(game.state().position().equals(letterLine), "Clique na borda preta não revela a fala atrás dela");
        readUntilChoiceOrExplore(panel, game);
        check(game.session().node() instanceof NarrativeNode.Choice, "Hotspot redimensionado abre a carta");
        image(panel, output.resolve("escolha-carta.png"));
        before = game.state().position();
        click(panel, 1230, 690);
        check(game.state().position().equals(before), "Clique fora das opções não confirma escolha");
        click(panel, 700, 401); // Adia a carta.
        readUntilChoiceOrExplore(panel, game);
        check(!game.state().hasMemory("retorno"), "Adiar não concede memória");
        click(panel, 640, 360);
        readUntilChoiceOrExplore(panel, game);
        image(panel, output.resolve("escolha-portas.png"));
        click(panel, 700, 481); // Examina a inscrição.
        readUntilChoiceOrExplore(panel, game);
        check(game.state().locationId().equals("salao_principal") && !game.session().active(), "Chegada ao Salão aguarda clique no livro");
        image(panel, output.resolve("salao.png"));

        click(panel, 430, 440); // Retorna pela passagem.
        click(panel, 990, 585);
        readUntilChoiceOrExplore(panel, game);
        click(panel, 700, 321); // Investiga a carta agora.
        click(panel, 700, 620); // Revela a fala de Natasha.
        image(panel, output.resolve("carta-retrato.png"));
        readUntilChoiceOrExplore(panel, game);
        check(game.state().hasItem("carta_entrada") && game.state().hasMemory("retorno"), "Retorno recupera item e memória");
        click(panel, 150, 674);
        check(game.overlay() == GameController.Overlay.INVENTORY, "Inventário abre por clique");
        image(panel, output.resolve("inventario.png"));
        click(panel, 640, 360);
        check(game.overlay() == GameController.Overlay.INVENTORY && game.state().locationId().equals("entrada_biblioteca"),
                "Inventário não deixa clicar na porta atrás dele");
        click(panel, 1050, 620);
        key(panel, KeyEvent.VK_J);
        check(game.overlay() == GameController.Overlay.MEMORIES, "Diário abre por teclado");
        image(panel, output.resolve("memorias.png"));
        key(panel, KeyEvent.VK_ESCAPE);
        click(panel, 640, 360);
        check(!game.session().active() && game.state().value("postura_entrada").orElseThrow().equals("investigativa"),
                "Retorno ao Salão conserva escolha sem repeti-la");
        click(panel, 930, 510);
        check(game.session().active() && game.state().position().sceneId().equals("c04_livro_mutavel"), "Livro abre o roteiro oficial");
        click(panel, 700, 620);
        image(panel, output.resolve("livro.png"));
        readUntilChoiceOrExplore(panel, game);
        check(game.state().flag("livro_mutavel_visto"), "Conclui investigação do livro");
        image(panel, output.resolve("trecho-concluido.png"));
        key(panel, KeyEvent.VK_ESCAPE);
        image(panel, output.resolve("menu-com-save.png"));

        game = new GameController(new SaveManager(saveDirectory));
        panel = panel(game);
        image(panel, output.resolve("menu-reaberto.png"));
        click(panel, 300, 440);
        check(game.mode() == GameMode.STORY && !game.session().active() && game.state().hasMemory("retorno")
                && game.state().flag("livro_mutavel_visto"), "Continuar restaura partida JSON após recriar o controlador");
        image(panel, output.resolve("continuacao.png"));
        key(panel, KeyEvent.VK_ESCAPE);
        image(panel, output.resolve("menu-antes-config.png"));
        click(panel, 300, 504);
        check(game.mode() == GameMode.CONFIGURACOES, "Configurações continua disponível");
        image(panel, output.resolve("configuracoes.png"));
        boolean effects = game.settings().effectsEnabled();
        click(panel, 812, 280);
        check(game.settings().effectsEnabled() != effects, "Preferências permanecem editáveis");
        image(panel, output.resolve("configuracoes-alteradas.png"));
        click(panel, 600, 580);
        image(panel, output.resolve("menu-antes-creditos.png"));
        click(panel, 300, 566);
        check(game.mode() == GameMode.CREDITOS, "Créditos continua disponível");
        image(panel, output.resolve("creditos.png"));

        SaveManager legacySave = new SaveManager(Files.createTempDirectory("biblioteca-menu-legado-"));
        legacySave.writeProgress(GameMode.INTRO, 2);
        GameController legacy = new GameController(legacySave);
        legacy.continueGame();
        GamePanel legacyPanel = panel(legacy);
        image(legacyPanel, output.resolve("prologo-legado.png"));
        legacy.nextIntroPage();
        image(legacyPanel, output.resolve("demo-legada.png"));
        click(legacyPanel, 200, 645);
        check(legacy.inspectingBook(), "Inspeção da demonstração legada foi preservada");
        image(legacyPanel, output.resolve("inspecao-legada.png"));
        click(legacyPanel, 800, 600);
        check(!legacy.inspectingBook(), "Inspeção legada fecha por clique");
    }

    private static GamePanel panel(GameController game) {
        GamePanel panel = new GamePanel(game, () -> {}, () -> {}, false, () -> 0L);
        panel.setSize(GamePanel.WIDTH, GamePanel.HEIGHT);
        return panel;
    }
    private static void readUntilChoiceOrExplore(GamePanel panel, GameController game) {
        int steps = 0;
        while (game.session().active() && !(game.session().node() instanceof NarrativeNode.Choice)) {
            if (++steps > 200) throw new AssertionError("Fluxo não aguardou exploração ou escolha");
            click(panel, 700, 620);
            click(panel, 700, 620);
        }
    }
    private static void image(GamePanel panel, Path file) throws Exception {
        BufferedImage canvas = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = canvas.createGraphics();
        panel.render(graphics, canvas.getWidth(), canvas.getHeight());
        graphics.dispose();
        ImageIO.write(canvas, "PNG", file.toFile());
    }
    private static void click(GamePanel panel, int x, int y) {
        double scale = Math.min(panel.getWidth() / 1280.0, panel.getHeight() / 720.0);
        int px = (int) Math.round((panel.getWidth() - 1280 * scale) / 2 + x * scale);
        int py = (int) Math.round((panel.getHeight() - 720 * scale) / 2 + y * scale);
        physicalClick(panel, px, py);
    }
    private static void physicalClick(GamePanel panel, int x, int y) {
        MouseEvent event = new MouseEvent(panel, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, x, y, 1, false, MouseEvent.BUTTON1);
        for (var listener : panel.getMouseListeners()) listener.mousePressed(event);
    }
    private static void key(GamePanel panel, int code) {
        KeyEvent event = new KeyEvent(panel, KeyEvent.KEY_PRESSED, 0, 0, code, KeyEvent.CHAR_UNDEFINED);
        for (var listener : panel.getKeyListeners()) listener.keyPressed(event);
    }
    private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
