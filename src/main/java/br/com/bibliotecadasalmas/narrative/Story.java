package br.com.bibliotecadasalmas.narrative;

import br.com.bibliotecadasalmas.core.GameState;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Conteúdo narrativo imutável, indexado pelos IDs do roteiro. Não executa cenas. */
public record Story(Metadata metadata, Map<String, Scene> scenes) {
    public Story { scenes = orderedCopy(scenes); }

    public Scene scene(String id) {
        Scene scene = scenes.get(id);
        if (scene == null) throw new IllegalArgumentException("Cena inexistente: " + id);
        return scene;
    }

    public Scene entryScene() { return scene(metadata.entryScene()); }

    /** Cria um estado independente a partir do ponto inicial carregado. */
    public GameState newGameState() {
        Scene entry = entryScene();
        return new GameState(entry.location(), entry.id(), entry.entryNode());
    }

    public record Metadata(String id, String chapterTitle, String version, String language,
                           String entryScene, int sceneCount, int cutsceneCount,
                           StoryCondition secretEndingRequires, List<String> endingScenes) {
        public Metadata { endingScenes = List.copyOf(endingScenes); }
    }

    public enum ReplayPolicy { ONCE, REPEAT_IF_INTERACTABLE }

    public record Scene(String id, int number, String title, String act, String location,
                        boolean optional, String kind, String entryNode, ReplayPolicy replayPolicy,
                        Map<String, NarrativeNode> nodes) {
        public Scene { nodes = orderedCopy(nodes); }

        public NarrativeNode node(String id) {
            NarrativeNode node = nodes.get(id);
            if (node == null) throw new IllegalArgumentException("Nó inexistente em " + this.id + ": " + id);
            return node;
        }
    }

    private static <K, V> Map<K, V> orderedCopy(Map<K, V> source) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(source));
    }
}
