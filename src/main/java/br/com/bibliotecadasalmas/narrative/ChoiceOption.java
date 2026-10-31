package br.com.bibliotecadasalmas.narrative;

import java.util.List;
import java.util.Optional;

public record ChoiceOption(String id, String text, Destination destination,
                           Optional<StoryCondition> requires, List<NarrativeEffect> effects) {
    public ChoiceOption { effects = List.copyOf(effects); }

    /** Tipos distintos impedem representar uma opção com duas saídas simultâneas. */
    public sealed interface Destination {
        record Node(String nodeId) implements Destination {}
        record Scene(String sceneId) implements Destination {}
        record Exploration(String location, String resumeScene) implements Destination {}
    }
}
