package br.com.bibliotecadasalmas.characters;

import java.util.Map;

/** Representação da Fantasma com suas expressões melancólicas e sobrenaturais. */
public final class Fantasma extends GameCharacter {
    public Fantasma() {
        super("fantasma", "A Fantasma", Map.of(
                "assustada", "personagens/fantasma/expressoes/Fantasma_Assustada.png",
                "neutra", "personagens/fantasma/expressoes/Fantasma_Neutra.png",
                "sombras", "personagens/fantasma/expressoes/Fantasma_Sombras.png",
                "sorriso", "personagens/fantasma/expressoes/Fantasma_Sorriso.png",
                "timida", "personagens/fantasma/expressoes/Fantasma_Timida.png",
                "triste", "personagens/fantasma/expressoes/Fantasma_Triste.png"
        ));
    }
}

