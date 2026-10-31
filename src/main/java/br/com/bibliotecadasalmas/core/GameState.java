package br.com.bibliotecadasalmas.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Progresso narrativo em memória, independente da interface e do salvamento.
 * As consultas de coleções devolvem cópias imutáveis, em ordem de descoberta.
 * A validação de IDs contra o roteiro pertence ao futuro carregador narrativo.
 */
public final class GameState {
    private static final Map<String, String> MEMORY_FLAGS = Map.of(
            "retorno", "memoria_retorno",
            "escolha", "memoria_escolha",
            "pacto", "memoria_pacto");

    private final String initialLocationId;
    private final NarrativePosition initialPosition;
    private final Map<String, Boolean> flags = new LinkedHashMap<>();
    private final Map<String, String> values = new LinkedHashMap<>();
    private final Map<NarrativePosition, String> choices = new LinkedHashMap<>();
    private final Set<String> inventory = new LinkedHashSet<>();
    private final Set<String> memories = new LinkedHashSet<>();
    private final Set<String> unlockedLocations = new LinkedHashSet<>();
    private final Set<String> completedInteractions = new LinkedHashSet<>();
    private String locationId;
    private NarrativePosition position;

    /** Ponto inicial do Capítulo I; não executa a cena nem carrega o JSON. */
    public GameState() {
        this("entrada_biblioteca", "c01_despertar", "n001");
    }

    /** Permite fornecer o ponto inicial quando os dados narrativos forem carregados. */
    public GameState(String initialLocationId, String initialSceneId, String initialNodeId) {
        this.initialLocationId = requireId(initialLocationId, "local inicial");
        this.initialPosition = new NarrativePosition(initialSceneId, initialNodeId);
        reset();
    }

    public String locationId() { return locationId; }
    public NarrativePosition position() { return position; }

    /** Retorna falso para uma flag ainda não definida. */
    public boolean flag(String key) {
        return flags.getOrDefault(requireId(key, "flag"), false);
    }

    /** Flags de memória também atualizam o diário, inclusive quando definidas como falsas. */
    public void setFlag(String key, boolean value) {
        requireId(key, "flag");
        flags.put(key, value);
        for (Map.Entry<String, String> memory : MEMORY_FLAGS.entrySet()) {
            if (memory.getValue().equals(key)) {
                if (value) memories.add(memory.getKey());
                else memories.remove(memory.getKey());
            }
        }
    }

    public Optional<String> value(String key) {
        return Optional.ofNullable(values.get(requireId(key, "valor narrativo")));
    }

    public void setValue(String key, String value) {
        requireId(key, "valor narrativo");
        requireId(value, "conteúdo do valor narrativo");
        values.put(key, value);
    }

    /** Registra a opção pela cena e pelo nó para não confundir nós de mesmo ID. */
    public void recordChoice(String sceneId, String nodeId, String optionId) {
        NarrativePosition choicePosition = new NarrativePosition(sceneId, nodeId);
        requireId(optionId, "opção");
        choices.put(choicePosition, optionId);
    }

    public Optional<String> choiceAt(String sceneId, String nodeId) {
        return Optional.ofNullable(choices.get(new NarrativePosition(sceneId, nodeId)));
    }

    public boolean giveItem(String itemId) {
        return inventory.add(requireId(itemId, "item"));
    }

    /** Remover um item ausente é uma operação sem efeito, compatível com a entrega direta. */
    public boolean removeItem(String itemId) {
        return inventory.remove(requireId(itemId, "item"));
    }

    public boolean hasItem(String itemId) {
        return inventory.contains(requireId(itemId, "item"));
    }

    public boolean unlockMemory(String memoryId) {
        String flag = memoryFlag(memoryId);
        boolean discovered = !memories.contains(memoryId);
        setFlag(flag, true);
        return discovered;
    }

    public boolean hasMemory(String memoryId) {
        memoryFlag(memoryId);
        return memories.contains(memoryId);
    }

    /** Desbloquear um ambiente não desloca o jogador até ele. */
    public boolean unlockLocation(String locationId) {
        return unlockedLocations.add(requireId(locationId, "local"));
    }

    public boolean isLocationUnlocked(String locationId) {
        return unlockedLocations.contains(requireId(locationId, "local"));
    }

    public void moveToLocation(String locationId) {
        if (!isLocationUnlocked(locationId)) {
            throw new IllegalStateException("Local ainda não desbloqueado: " + locationId);
        }
        this.locationId = locationId;
    }

    /** Atualiza cena e nó juntos; não altera o ambiente ou executa efeitos. */
    public void setNarrativePosition(String sceneId, String nodeId) {
        position = new NarrativePosition(sceneId, nodeId);
    }

    public boolean completeInteraction(String interactionId) {
        return completedInteractions.add(requireId(interactionId, "interação"));
    }

    public boolean isInteractionCompleted(String interactionId) {
        return completedInteractions.contains(requireId(interactionId, "interação"));
    }

    /** As cinco condições oficiais, sem requisitos de personalidade ou revelação adicional. */
    public boolean canRewritePact() {
        return hasMemory("retorno") && hasMemory("escolha") && hasMemory("pacto")
                && flag("fantasma_confia") && flag("regra_reescrita_descoberta");
    }

    public Map<String, Boolean> flags() { return snapshot(flags); }
    public Map<String, String> values() { return snapshot(values); }
    public Map<NarrativePosition, String> choices() { return snapshot(choices); }
    public Set<String> inventory() { return snapshot(inventory); }
    public Set<String> memories() { return snapshot(memories); }
    public Set<String> unlockedLocations() { return snapshot(unlockedLocations); }
    public Set<String> completedInteractions() { return snapshot(completedInteractions); }

    /** Reinicia o progresso sem substituir o objeto consultado pela interface. */
    public void reset() {
        flags.clear();
        values.clear();
        choices.clear();
        inventory.clear();
        memories.clear();
        unlockedLocations.clear();
        completedInteractions.clear();
        locationId = initialLocationId;
        position = initialPosition;
        unlockedLocations.add(initialLocationId);
    }

    private static String memoryFlag(String memoryId) {
        String flag = MEMORY_FLAGS.get(requireId(memoryId, "memória"));
        if (flag == null) throw new IllegalArgumentException("Memória desconhecida: " + memoryId);
        return flag;
    }

    private static String requireId(String id, String field) {
        if (id == null || id.isBlank() || !id.equals(id.strip())) {
            throw new IllegalArgumentException("Identificador inválido para " + field + ": " + id);
        }
        return id;
    }

    private static <T> Set<T> snapshot(Set<T> source) {
        return Collections.unmodifiableSet(new LinkedHashSet<>(source));
    }

    private static <K, V> Map<K, V> snapshot(Map<K, V> source) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(source));
    }

    /** Cursor imutável: um nó só tem significado em conjunto com sua cena. */
    public record NarrativePosition(String sceneId, String nodeId) {
        public NarrativePosition {
            requireId(sceneId, "cena");
            requireId(nodeId, "nó");
        }
    }
}
