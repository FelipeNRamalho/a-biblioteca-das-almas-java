package br.com.bibliotecadasalmas.characters;

import java.util.Map;

/** Representação do Escriba com rosto de pergaminho sem feições. */
public final class Escriba extends GameCharacter {
    public Escriba() {
        super("escriba", "O Escriba", Map.of(
                "ameacador", "personagens/escriba/expressoes/Escriba_Ameacador.png",
                "condenando", "personagens/escriba/expressoes/Escriba_Condenando.png",
                "derrotado", "personagens/escriba/expressoes/Escriba_Derrotado.png",
                "neutro", "personagens/escriba/expressoes/Escriba_Neutro.png",
                "rindo", "personagens/escriba/expressoes/Escriba_Rindo.png"
        ));
    }
}

