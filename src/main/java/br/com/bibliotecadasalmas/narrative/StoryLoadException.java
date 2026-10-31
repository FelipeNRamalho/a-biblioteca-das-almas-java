package br.com.bibliotecadasalmas.narrative;

/** Diagnóstico de conteúdo com a fonte e o contexto que impediram o carregamento. */
public final class StoryLoadException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    StoryLoadException(String source, String context, String message) {
        super(source + " | " + context + ": " + message);
    }

    StoryLoadException(String source, String context, String message, Throwable cause) {
        super(source + " | " + context + ": " + message, cause);
    }
}
