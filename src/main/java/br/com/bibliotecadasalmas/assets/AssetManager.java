package br.com.bibliotecadasalmas.assets;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/** Carrega imagens do classpath e evita leituras repetidas durante a renderização. */
public final class AssetManager {
    private final Map<String, BufferedImage> images = new HashMap<>();

    public BufferedImage get(String relativePath) {
        return images.computeIfAbsent(relativePath, this::readImage);
    }

    private BufferedImage readImage(String path) {
        try (InputStream stream = AssetManager.class.getResourceAsStream("/assets/" + path)) {
            if (stream == null) throw new IllegalStateException("Recurso não encontrado: " + path);
            BufferedImage image = ImageIO.read(stream);
            if (image == null) throw new IOException("Imagem inválida: " + path);
            return image;
        } catch (IOException error) {
            throw new IllegalStateException("Não foi possível carregar: " + path, error);
        }
    }
}
