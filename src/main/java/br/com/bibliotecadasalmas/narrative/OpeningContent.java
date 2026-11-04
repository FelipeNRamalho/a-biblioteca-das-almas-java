package br.com.bibliotecadasalmas.narrative;

import br.com.bibliotecadasalmas.core.GameState;
import br.com.bibliotecadasalmas.world.Interactable;
import com.google.gson.Gson;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Configuração parcial das cenas 01–04; não substitui os manifestos do capítulo completo. */
public record OpeningContent(Map<String, String> sceneExits, Map<String, Location> locations,
                             Map<String, String> cutscenes, Map<String, Speaker> speakers,
                             List<Pause> pauses, List<Document> documents) {
    private static final String RESOURCE = "/story/trecho_inicial.json";

    public OpeningContent {
        sceneExits = ordered(sceneExits); locations = ordered(locations);
        cutscenes = ordered(cutscenes); speakers = ordered(speakers);
        pauses = List.copyOf(pauses); documents = List.copyOf(documents);
    }

    public static OpeningContent load(Story story) {
        try (var input = OpeningContent.class.getResourceAsStream(RESOURCE)) {
            if (input == null) throw new IllegalStateException("Configuração ausente: " + RESOURCE);
            try (var reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                OpeningContent content = new Gson().fromJson(StrictJson.read(reader, RESOURCE), OpeningContent.class);
                content.validate(story);
                return content;
            }
        } catch (IOException | IllegalArgumentException error) {
            throw new IllegalStateException("Configuração inválida: " + RESOURCE, error);
        }
    }

    public boolean supports(String sceneId) { return sceneExits.containsKey(sceneId); }

    public Optional<String> portrait(NarrativeNode.Line line) {
        Speaker speaker = speakers.get(line.speaker());
        return line.expression().map(expression -> speaker.portraits().get(expression));
    }

    public String documentText(Document document, Story story) {
        return document.nodes().stream().map(id -> ((NarrativeNode.Line) story.scene(document.scene()).node(id)).text())
                .reduce((a, b) -> a + "\n\n" + b).orElse("");
    }

    public record Location(String title, String background, String entryScene, List<Hotspot> hotspots) {
        public Location { hotspots = List.copyOf(hotspots); }
    }
    public record Speaker(String name, Map<String, String> portraits) {
        public Speaker { portraits = ordered(portraits); }
    }
    public record Pause(String scene, String node, String marker) {}
    public record Document(String id, String title, String kind, String scene, List<String> nodes) {
        public Document { nodes = List.copyOf(nodes); }
        public boolean available(GameState state) {
            return kind.equals("item") ? state.hasItem(id) : state.hasMemory(id);
        }
    }
    public record Hotspot(String id, String label, String kind, String target, String gateScene,
                          String hideWhenFlag, int x, int y, int width, int height) {
        public Hotspot {
            if (width <= 0 || height <= 0 || x < 0 || y < 0 || x + width > 1280 || y + height > 720) {
                throw new IllegalArgumentException("Hotspot fora da área lógica: " + id);
            }
        }
        public Rectangle bounds() { return new Rectangle(x, y, width, height); }
        public Interactable interaction() {
            return switch (kind) {
                case "investigation" -> new Interactable.Investigation(target);
                case "passage" -> new Interactable.Passage(target, gateScene);
                default -> throw new IllegalArgumentException("Interação desconhecida: " + kind);
            };
        }
    }

    private void validate(Story story) {
        scene(story, story.metadata().entryScene());
        locations.values().forEach(location -> {
            asset(location.background());
            if (location.entryScene() != null) scene(story, location.entryScene());
            var ids = new java.util.HashSet<String>();
            for (Hotspot hotspot : location.hotspots()) {
                if (!ids.add(hotspot.id())) throw new IllegalArgumentException("Hotspot duplicado: " + hotspot.id());
                switch (hotspot.interaction()) {
                    case Interactable.Investigation investigation -> scene(story, investigation.sceneId());
                    case Interactable.Passage passage -> {
                        if (!locations.containsKey(passage.locationId())) throw new IllegalArgumentException("Passagem sem local");
                        if (passage.gateScene() != null) scene(story, passage.gateScene());
                    }
                }
            }
        });
        for (var entry : sceneExits.entrySet()) {
            Story.Scene scene = story.scene(entry.getKey());
            if (!entry.getValue().equals("follow") && !entry.getValue().equals("explore")) {
                throw new IllegalArgumentException("Saída de cena inválida: " + scene.id());
            }
            if (!locations.containsKey(scene.location())) throw new IllegalArgumentException("Cena sem fundo: " + scene.id());
            for (NarrativeNode node : scene.nodes().values()) {
                if (entry.getValue().equals("follow") && node instanceof NarrativeNode.End end) {
                    scene(story, end.nextScene().orElseThrow(() -> new IllegalArgumentException("Cena sem continuação")));
                }
                if (node instanceof NarrativeNode.Line line) {
                    if (!speakers.containsKey(line.speaker())) throw new IllegalArgumentException("Falante sem apresentação");
                    if (line.speaker().equals("natasha") && line.expression().isPresent() && portrait(line).isEmpty()) {
                        throw new IllegalArgumentException("Expressão sem PNG: " + line.expression().get());
                    }
                }
                if (node instanceof NarrativeNode.Cutscene cutscene) {
                    if (!cutscenes.containsKey(cutscene.asset())) throw new IllegalArgumentException("Cutscene sem imagem");
                    asset(cutscenes.get(cutscene.asset()));
                }
            }
        }
        speakers.values().forEach(s -> s.portraits().values().forEach(OpeningContent::asset));
        pauses.forEach(pause -> scene(story, pause.scene()).node(pause.node()));
        documents.forEach(document -> {
            if (!document.kind().equals("item") && !document.kind().equals("memory")) throw new IllegalArgumentException("Documento inválido");
            document.nodes().forEach(id -> {
                if (!(scene(story, document.scene()).node(id) instanceof NarrativeNode.Line)) {
                    throw new IllegalArgumentException("Documento deve referenciar falas");
                }
            });
        });
    }

    private Story.Scene scene(Story story, String id) {
        if (!supports(id)) throw new IllegalArgumentException("Cena fora do trecho integrado: " + id);
        return story.scene(id);
    }

    private static void asset(String path) {
        if (path == null || OpeningContent.class.getResource("/assets/" + path) == null) {
            throw new IllegalArgumentException("Imagem ausente do classpath: " + path);
        }
    }

    private static <K, V> Map<K, V> ordered(Map<K, V> map) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(map));
    }
}
