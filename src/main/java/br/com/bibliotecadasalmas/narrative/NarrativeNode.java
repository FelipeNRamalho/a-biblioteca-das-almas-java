package br.com.bibliotecadasalmas.narrative;

import java.util.List;
import java.util.Optional;

/** Cada tipo expõe somente os dados pertinentes ao seu comportamento narrativo. */
public sealed interface NarrativeNode {
    String id();

    record Line(String id, String speaker, String text, Optional<String> expression, String next)
            implements NarrativeNode {}

    record Cutscene(String id, String asset, String next) implements NarrativeNode {}

    record Choice(String id, String prompt, List<ChoiceOption> options, Optional<Confirmation> confirmation)
            implements NarrativeNode {
        public Choice { options = List.copyOf(options); }
    }

    record Confirmation(String title, String message, String confirmLabel, String cancelLabel) {}

    record Branch(String id, StoryCondition test, String thenNode, String elseNode) implements NarrativeNode {}

    record Event(String id, List<NarrativeEffect> effects, String next) implements NarrativeNode {
        public Event { effects = List.copyOf(effects); }
    }

    record Join(String id, String next) implements NarrativeNode {}

    record End(String id, List<NarrativeEffect> effects, Optional<String> nextScene) implements NarrativeNode {
        public End { effects = List.copyOf(effects); }
    }
}
