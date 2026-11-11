package br.com.bibliotecadasalmas.characters;

import br.com.bibliotecadasalmas.core.GameState;
import java.util.Map;
import java.util.Optional;

/** Representação de Rodion Raskólnikov, com anonimato preservado antes da revelação formal. */
public final class Rodion extends GameCharacter {
    public Rodion() {
        super("rodion", "Rodion Raskólnikov", Map.of(
                "neutro", "personagens/rodion/expressoes/Rodion_Neutro.png",
                "serio", "personagens/rodion/expressoes/Rodion_Serio.png",
                "pensativo", "personagens/rodion/expressoes/Rodion_Pensativo.png",
                "melancolico", "personagens/rodion/expressoes/Rodion_Melancolico.png",
                "reflexivo", "personagens/rodion/expressoes/Rodion_Reflexivo.png",
                "desconfiado", "personagens/rodion/expressoes/Rodion_Desconfiado.png"
        ));
    }

    @Override
    public String displayName(GameState state, String sceneId) {
        if (sceneId != null && sceneId.equals("c04_livro_mutavel")) {
            return "Uma voz";
        }
        return defaultName();
    }

    @Override
    public Optional<String> portrait(String expression, GameState state, String sceneId) {
        if (sceneId != null && sceneId.equals("c04_livro_mutavel")) {
            return Optional.empty();
        }
        return super.portrait(expression, state, sceneId);
    }
}

