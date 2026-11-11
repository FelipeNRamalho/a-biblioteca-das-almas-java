package br.com.bibliotecadasalmas.characters;

import br.com.bibliotecadasalmas.core.GameState;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Classe base para os personagens da narrativa, encapsulando identidade e retratos. */
public abstract class GameCharacter {
    private final String id;
    private final String defaultName;
    private final Map<String, String> expressions;

    protected GameCharacter(String id, String defaultName, Map<String, String> expressions) {
        this.id = id;
        this.defaultName = defaultName;
        this.expressions = Collections.unmodifiableMap(new LinkedHashMap<>(expressions));
    }

    public String id() { return id; }
    public String defaultName() { return defaultName; }
    public Map<String, String> expressions() { return expressions; }

    public boolean hasExpression(String expression) {
        return expression != null && expressions.containsKey(expression);
    }

    public String displayName(GameState state, String sceneId) {
        return defaultName;
    }

    public Optional<String> portrait(String expression, GameState state, String sceneId) {
        if (expression == null) return Optional.empty();
        return Optional.ofNullable(expressions.get(expression));
    }
}

