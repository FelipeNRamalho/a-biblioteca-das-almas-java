package br.com.bibliotecadasalmas.narrative;

/** Efeitos tipados para o futuro executor; o carregamento não os aplica. */
public sealed interface NarrativeEffect {
    record SetFlag(String key, boolean value) implements NarrativeEffect {}
    record SetValue(String key, String value) implements NarrativeEffect {}
    record GiveItem(String item) implements NarrativeEffect {}
    record RemoveItem(String item) implements NarrativeEffect {}
    record UnlockMemory(String memory) implements NarrativeEffect {}
    record UnlockLocation(String location) implements NarrativeEffect {}
}
