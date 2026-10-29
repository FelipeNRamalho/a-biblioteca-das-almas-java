package br.com.bibliotecadasalmas;

import br.com.bibliotecadasalmas.core.GameController;
import br.com.bibliotecadasalmas.core.GameMode;
import br.com.bibliotecadasalmas.core.SaveManager;
import br.com.bibliotecadasalmas.ui.GamePanel;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/** Teste local sem dependências: menu, fluxo do prólogo, configurações e imagens. */
public final class MenuSmokeTest {
    public static void main(String[] args) throws Exception {
        Path home = Files.createTempDirectory("biblioteca-menu-teste-");
        GameController controller = new GameController(new SaveManager(home));
        GamePanel panel = new GamePanel(controller, () -> {}, () -> {}, false);
        panel.setSize(GamePanel.WIDTH, GamePanel.HEIGHT);
        Path output = args.length>0?Path.of(args[0]):Path.of(".");
        Files.createDirectories(output);
        image(panel, output.resolve("menu.png"));
        click(panel, 300, 379);
        check(controller.mode()==GameMode.INTRO, "Novo Jogo deve abrir o prólogo");
        check(controller.hasSave(), "Novo Jogo deve criar salvamento");
        image(panel, output.resolve("intro.png"));
        for (int i=0;i<3;i++) {
            click(panel, 680, 605); // Revela texto por completo
            click(panel, 680, 605); // Avança a página
        }
        check(controller.mode()==GameMode.EXPLORACAO, "Prólogo deve abrir a demonstração");
        image(panel, output.resolve("exploracao.png"));
        click(panel, 200, 645);
        check(controller.inspectingBook(), "Hotspot deve abrir inspeção de livro");
        image(panel, output.resolve("inspecao.png"));
        click(panel, 800, 600);
        check(!controller.inspectingBook(), "Clicar deve fechar inspeção");
        controller.returnToMenu();
        image(panel, output.resolve("menu_com_salvamento.png"));
        click(panel, 300, 440);
        check(controller.mode()==GameMode.EXPLORACAO, "Continuar deve restaurar progresso salvo");
        controller.returnToMenu();
        image(panel, output.resolve("menu_antes_config.png"));
        click(panel, 300, 504);
        check(controller.mode()==GameMode.CONFIGURACOES, "Configurações deve abrir");
        image(panel, output.resolve("configuracoes.png"));
        boolean fx=controller.settings().effectsEnabled();
        click(panel, 812, 280);
        check(controller.settings().effectsEnabled()!=fx, "Efeitos devem ser configuráveis");
        click(panel, 600, 580);
        check(controller.mode()==GameMode.MENU, "Voltar deve abrir menu");
        image(panel, output.resolve("menu_depois_config.png"));
        click(panel, 300, 566);
        check(controller.mode()==GameMode.CREDITOS, "Créditos deve abrir");
        image(panel, output.resolve("creditos.png"));
        System.out.println("OK: todas as verificações do menu passaram. Previews: " + output);
    }

    private static void image(GamePanel panel, Path output) throws Exception {
        BufferedImage canvas = new BufferedImage(1280,720,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = canvas.createGraphics();
        panel.render(g,1280,720);
        g.dispose();
        ImageIO.write(canvas,"PNG",output.toFile());
    }

    private static void click(GamePanel panel, int x,int y) {
        panel.dispatchEvent(new MouseEvent(panel,MouseEvent.MOUSE_PRESSED,System.currentTimeMillis(),0,x,y,1,false,MouseEvent.BUTTON1));
    }

    private static void check(boolean condition,String message) {
        if(!condition) throw new AssertionError(message);
    }
}
