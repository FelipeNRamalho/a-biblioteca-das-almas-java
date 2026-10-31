package br.com.bibliotecadasalmas.narrative;

import br.com.bibliotecadasalmas.core.GameState;
import java.util.List;

/** Contrato polimórfico de consulta; avaliar uma condição não altera a partida. */
public sealed interface StoryCondition permits StoryCondition.Flag, StoryCondition.All {
    boolean matches(GameState state);

    record Flag(String flag, boolean expected) implements StoryCondition {
        @Override public boolean matches(GameState state) { return state.flag(flag) == expected; }
    }

    record All(List<StoryCondition> conditions) implements StoryCondition {
        public All { conditions = List.copyOf(conditions); }
        @Override public boolean matches(GameState state) {
            return conditions.stream().allMatch(condition -> condition.matches(state));
        }
    }
}
