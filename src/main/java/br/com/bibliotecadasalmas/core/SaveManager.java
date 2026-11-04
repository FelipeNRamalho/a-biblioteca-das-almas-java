package br.com.bibliotecadasalmas.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;

/** Guarda um ponto de continuação do protótipo e preferências locais. */
public final class SaveManager {
    private static final Gson JSON = new GsonBuilder().setStrictness(Strictness.STRICT).setPrettyPrinting().create();
    private final Path directory;

    public SaveManager() {
        this(Path.of(System.getProperty("user.home"), ".biblioteca-das-almas"));
    }

    public SaveManager(Path directory) { this.directory = directory; }

    public boolean hasSave() { return hasStorySave() || Files.isRegularFile(directory.resolve("progresso.properties")); }
    public boolean hasStorySave() { return Files.isRegularFile(directory.resolve("historia.json")); }

    public StoryProgress readStoryProgress() {
        try (var reader = Files.newBufferedReader(directory.resolve("historia.json"), StandardCharsets.UTF_8)) {
            StoryProgress progress = JSON.fromJson(reader, StoryProgress.class);
            if (progress == null) throw new IllegalArgumentException("Arquivo vazio");
            return progress;
        } catch (IOException | RuntimeException error) {
            throw new IllegalStateException("Não foi possível carregar a história. O arquivo foi preservado.", error);
        }
    }

    /** Grava em arquivo temporário antes de substituir o último ponto de continuação. */
    public void writeStoryProgress(StoryProgress progress) {
        Path temporary = null;
        try {
            Files.createDirectories(directory);
            temporary = Files.createTempFile(directory, "historia-", ".tmp");
            Files.writeString(temporary, JSON.toJson(progress), StandardCharsets.UTF_8);
            try {
                Files.move(temporary, directory.resolve("historia.json"), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException unsupported) {
                Files.move(temporary, directory.resolve("historia.json"), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException error) {
            throw new IllegalStateException("Não foi possível salvar a história. Seu progresso continua aberto.", error);
        } finally {
            if (temporary != null) {
                try { Files.deleteIfExists(temporary); }
                catch (IOException ignored) { /* O save anterior não é apagado para limpar um temporário. */ }
            }
        }
    }

    public void writeProgress(GameMode mode, int introPage) {
        Properties p = new Properties();
        p.setProperty("mode", mode == GameMode.INTRO ? "INTRO" : "EXPLORACAO");
        p.setProperty("introPage", Integer.toString(introPage));
        write("progresso.properties", p);
    }

    public Progress readProgress() {
        Properties p = read("progresso.properties");
        GameMode mode = "INTRO".equals(p.getProperty("mode")) ? GameMode.INTRO : GameMode.EXPLORACAO;
        int page;
        try { page = Integer.parseInt(p.getProperty("introPage", "0")); }
        catch (NumberFormatException e) { page = 0; }
        return new Progress(mode, Math.max(0, Math.min(2, page)));
    }

    public void writeSettings(GameSettings s) {
        Properties p = new Properties();
        p.setProperty("effects", Boolean.toString(s.effectsEnabled()));
        p.setProperty("speed", Integer.toString(s.textSpeed()));
        p.setProperty("fullscreen", Boolean.toString(s.fullscreen()));
        write("configuracoes.properties", p);
    }

    public void readSettings(GameSettings settings) {
        Properties p = read("configuracoes.properties");
        int speed;
        try { speed = Integer.parseInt(p.getProperty("speed", "1")); }
        catch (NumberFormatException e) { speed = 1; }
        settings.load(Boolean.parseBoolean(p.getProperty("effects", "true")), speed,
                Boolean.parseBoolean(p.getProperty("fullscreen", "false")));
    }

    private Properties read(String name) {
        Properties p = new Properties();
        Path file = directory.resolve(name);
        if (Files.isRegularFile(file)) {
            try (InputStream in = Files.newInputStream(file)) { p.load(in); }
            catch (Exception error) { System.err.println("Erro ao ler " + file + ": " + error.getMessage()); }
        }
        return p;
    }

    private void write(String name, Properties p) {
        try {
            Files.createDirectories(directory);
            try (OutputStream out = Files.newOutputStream(directory.resolve(name))) {
                p.store(out, "A Biblioteca das Almas");
            }
        } catch (Exception error) {
            System.err.println("Não foi possível salvar preferências ou progresso: " + error.getMessage());
        }
    }

    public record Progress(GameMode mode, int introPage) {}
}
