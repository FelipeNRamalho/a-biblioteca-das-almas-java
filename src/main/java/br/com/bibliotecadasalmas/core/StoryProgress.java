package br.com.bibliotecadasalmas.core;

import br.com.bibliotecadasalmas.narrative.NarrativeEffect;
import br.com.bibliotecadasalmas.narrative.NarrativeNode;
import br.com.bibliotecadasalmas.narrative.NarrativeSession;
import br.com.bibliotecadasalmas.narrative.Story;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/** Formato versionado do save narrativo. Nunca desserializa diretamente GameState. */
public record StoryProgress(int formatVersion, String storyId, String storyVersion, String location,
                            String scene, String node, boolean active, String pendingChoice,
                            Map<String, Boolean> flags, Map<String, String> values, List<Choice> choices,
                            Set<String> inventory, Set<String> memories, Set<String> unlockedLocations,
                            Set<String> completedInteractions) {
    public StoryProgress {
        flags = Map.copyOf(flags); values = Map.copyOf(values); choices = List.copyOf(choices);
        inventory = Set.copyOf(inventory); memories = Set.copyOf(memories);
        unlockedLocations = Set.copyOf(unlockedLocations); completedInteractions = Set.copyOf(completedInteractions);
    }
    public record Choice(String scene, String node, String option) {}

    public static StoryProgress capture(Story story, GameState state, NarrativeSession session) {
        List<Choice> choices = state.choices().entrySet().stream()
                .map(e -> new Choice(e.getKey().sceneId(), e.getKey().nodeId(), e.getValue())).toList();
        return new StoryProgress(1, story.metadata().id(), story.metadata().version(), state.locationId(),
                state.position().sceneId(), state.position().nodeId(), session.active(), session.pendingChoice().orElse(null),
                state.flags(), state.values(), choices, state.inventory(), state.memories(),
                state.unlockedLocations(), state.completedInteractions());
    }

    /** Reconstrói e valida um candidato antes de substituir a partida em andamento. */
    public GameState restore(Story story) {
        if (formatVersion != 1 || !story.metadata().id().equals(storyId) || !story.metadata().version().equals(storyVersion)) {
            throw new IllegalArgumentException("Versão de salvamento incompatível com o roteiro");
        }
        story.scene(scene).node(node);
        Set<String> knownLocations = new HashSet<>();
        Set<String> knownFlags = new HashSet<>();
        Set<String> knownItems = new HashSet<>();
        Map<String, Set<String>> knownValues = new HashMap<>();
        story.scenes().values().forEach(s -> {
            knownLocations.add(s.location());
            s.nodes().values().stream().flatMap(StoryProgress::effects).forEach(effect -> {
                if (effect instanceof NarrativeEffect.SetFlag flag) knownFlags.add(flag.key());
                if (effect instanceof NarrativeEffect.UnlockMemory memory) knownFlags.add("memoria_" + memory.memory());
                if (effect instanceof NarrativeEffect.GiveItem item) knownItems.add(item.item());
                if (effect instanceof NarrativeEffect.SetValue value) {
                    knownValues.computeIfAbsent(value.key(), key -> new HashSet<>()).add(value.value());
                }
            });
        });
        if (!knownLocations.containsAll(unlockedLocations) || !unlockedLocations.contains(location)
                || !knownItems.containsAll(inventory) || !knownFlags.containsAll(flags.keySet())) {
            throw new IllegalArgumentException("Salvamento referencia progresso desconhecido");
        }
        GameState candidate = story.newGameState();
        flags.forEach(candidate::setFlag);
        if (!candidate.memories().equals(memories)) throw new IllegalArgumentException("Memórias divergentes das flags");
        values.forEach((key, value) -> {
            if (!knownValues.getOrDefault(key, Set.of()).contains(value)) throw new IllegalArgumentException("Decisão desconhecida: " + key);
            candidate.setValue(key, value);
        });
        for (Choice choice : choices) {
            if (!(story.scene(choice.scene()).node(choice.node()) instanceof NarrativeNode.Choice decision)
                    || decision.options().stream().noneMatch(o -> o.id().equals(choice.option()))) {
                throw new IllegalArgumentException("Opção de salvamento inexistente");
            }
            candidate.recordChoice(choice.scene(), choice.node(), choice.option());
        }
        inventory.forEach(candidate::giveItem);
        unlockedLocations.forEach(candidate::unlockLocation);
        if (!candidate.unlockedLocations().equals(unlockedLocations)) {
            throw new IllegalArgumentException("Local inicial ausente do salvamento");
        }
        completedInteractions.forEach(candidate::completeInteraction);
        candidate.moveToLocation(location);
        candidate.setNarrativePosition(scene, node);
        return candidate;
    }

    private static Stream<NarrativeEffect> effects(NarrativeNode node) {
        return switch (node) {
            case NarrativeNode.Event event -> event.effects().stream();
            case NarrativeNode.End end -> end.effects().stream();
            case NarrativeNode.Choice choice -> choice.options().stream().flatMap(o -> o.effects().stream());
            default -> Stream.empty();
        };
    }
}
