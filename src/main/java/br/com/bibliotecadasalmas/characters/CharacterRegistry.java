package br.com.bibliotecadasalmas.characters;

import br.com.bibliotecadasalmas.core.GameState;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Registro central de personagens, suportando identificadores canônicos e pseudônimos de memória/voz. */
public final class CharacterRegistry {
    private final Map<String, GameCharacter> characters;

    public CharacterRegistry() {
        Map<String, GameCharacter> map = new LinkedHashMap<>();
        register(map, new Natasha());
        register(map, new Rodion());
        register(map, new Fantasma());
        register(map, new Escriba());
        this.characters = Collections.unmodifiableMap(map);
    }

    private static void register(Map<String, GameCharacter> map, GameCharacter character) {
        map.put(character.id(), character);
    }

    public Optional<GameCharacter> find(String speakerId) {
        return Optional.ofNullable(characters.get(speakerId));
    }

    public Optional<String> displayName(String speakerId, GameState state, String sceneId) {
        if (speakerId == null) return Optional.empty();
        return switch (speakerId) {
            case "natasha_memoria" -> Optional.of("Natasha (Memória)");
            case "rodion_memoria" -> Optional.of("Rodion (Memória)");
            case "voz_escriba" -> Optional.of("Uma voz fria");
            default -> find(speakerId).map(c -> c.displayName(state, sceneId));
        };
    }

    public Optional<String> portrait(String speakerId, String expression, GameState state, String sceneId) {
        if (speakerId == null || expression == null) return Optional.empty();
        return switch (speakerId) {
            case "natasha_memoria" -> find("natasha").flatMap(c -> c.portrait(expression, state, sceneId));
            case "rodion_memoria" -> find("rodion").flatMap(c -> c.portrait(expression, state, sceneId));
            case "voz_escriba" -> Optional.empty();
            default -> find(speakerId).flatMap(c -> c.portrait(expression, state, sceneId));
        };
    }
}

