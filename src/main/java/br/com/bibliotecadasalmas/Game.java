package br.com.bibliotecadasalmas;

import br.com.bibliotecadasalmas.core.GameController;
import br.com.bibliotecadasalmas.ui.GamePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

public final class Game {
    private Game() {}

    public static void main(String[] args) {
        if (args.length>0 && "--preview".equals(args[0])) {
            preview(args.length>1 ? Path.of(args[1]) : Path.of("preview_menu.png"));
            return;
        }
        SwingUtilities.invokeLater(Game::launch);
    }

    private static void preview(Path path) {
        try {
            GamePanel panel = new GamePanel(new GameController(), ()->{}, ()->{}, false);
            BufferedImage canvas = new BufferedImage(GamePanel.WIDTH, GamePanel.HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = canvas.createGraphics();
            panel.render(graphics, GamePanel.WIDTH, GamePanel.HEIGHT);
            graphics.dispose();
            ImageIO.write(canvas, "PNG", path.toFile());
            System.out.println("Preview renderizado: " + path.toAbsolutePath());
        } catch (Exception error) {
            error.printStackTrace();
            System.exit(1);
        }
    }

    private static void launch() {
        JFrame frame = new JFrame("A Biblioteca das Almas — Capítulo I");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(true);
        GameController controller = new GameController();
        Runnable toggleFullscreen = () -> {
            GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            boolean desired = !controller.settings().fullscreen();
            try {
                if (desired) {
                    device.setFullScreenWindow(frame);
                } else {
                    device.setFullScreenWindow(null);
                    frame.setSize(1280, 720);
                    frame.setLocationRelativeTo(null);
                }
                controller.settings().setFullscreen(desired);
                controller.updateSettings();
            } catch (Exception error) {
                JOptionPane.showMessageDialog(frame, "Não foi possível alternar tela cheia neste computador.",
                        "Exibição", JOptionPane.WARNING_MESSAGE);
            }
        };
        GamePanel view = new GamePanel(controller, frame::dispose, toggleFullscreen, true);
        frame.setContentPane(view);
        frame.pack();
        frame.setMinimumSize(new Dimension(850, 500));
        frame.setLocationRelativeTo(null);
        frame.getRootPane().registerKeyboardAction(e -> toggleFullscreen.run(),
                KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        frame.setVisible(true);
        view.requestFocusInWindow();
        if (controller.settings().fullscreen()) {
            try {
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                        .setFullScreenWindow(frame);
            } catch (Exception error) {
                controller.settings().setFullscreen(false);
                controller.updateSettings();
            }
        }
    }
}
