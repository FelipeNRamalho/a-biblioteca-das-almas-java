package br.com.bibliotecadasalmas.characters;

import java.util.Map;

/** Representação de Natasha Rostova com suas expressões canônicas. */
public final class Natasha extends GameCharacter {
    public Natasha() {
        super("natasha", "Natasha Rostova", Map.ofEntries(
                Map.entry("assustada", "personagens/natasha/expressoes/Natasha_Assustada.png"),
                Map.entry("cansada", "personagens/natasha/expressoes/Natasha_Cansada.png"),
                Map.entry("determinada", "personagens/natasha/expressoes/Natasha_Determinada.png"),
                Map.entry("irritada", "personagens/natasha/expressoes/Natasha_Irritada.png"),
                Map.entry("neutra", "personagens/natasha/expressoes/Natasha_Neutra.png"),
                Map.entry("pensativa", "personagens/natasha/expressoes/Natasha_Pensativa.png"),
                Map.entry("provocativa", "personagens/natasha/expressoes/Natasha_Provocativa.png"),
                Map.entry("sarcastica", "personagens/natasha/expressoes/Natasha_Sarcastica.png"),
                Map.entry("seria", "personagens/natasha/expressoes/Natasha_Seria.png"),
                Map.entry("surpresa", "personagens/natasha/expressoes/Natasha_Surpresa.png"),
                Map.entry("triste", "personagens/natasha/expressoes/Natasha_Triste.png"),
                Map.entry("vulneravel", "personagens/natasha/expressoes/Natasha_Vulneravel.png"),
                Map.entry("feliz", "personagens/natasha/expressoes/Natasha_Feliz.png"),
                Map.entry("chorando", "personagens/natasha/expressoes/Natasha_Chorando.png"),
                Map.entry("envergonhada", "personagens/natasha/expressoes/Natasha_Envergonhada.png")
        ));
    }
}

