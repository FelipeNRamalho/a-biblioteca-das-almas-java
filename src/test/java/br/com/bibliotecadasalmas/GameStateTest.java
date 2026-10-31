package br.com.bibliotecadasalmas;

import br.com.bibliotecadasalmas.core.GameController;
import br.com.bibliotecadasalmas.core.GameState;
import br.com.bibliotecadasalmas.core.GameState.NarrativePosition;
import br.com.bibliotecadasalmas.core.SaveManager;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;

/** Testes de regras do progresso, executáveis por main, sem dependências externas. */
public final class GameStateTest {
    public static void main(String[] args) throws Exception {
        initialState();
        inventoryAndSnapshots();
        memoryConsistency();
        choicesAndNarrativePosition();
        locationAccess();
        secretEndingCombinations();
        invalidChangesAreRejected();
        newGameResetsProgress();
        System.out.println("OK: GameState passou em 8 grupos de verificações, incluindo 32 combinações do final secreto.");
    }

    private static void initialState() {
        GameState state = new GameState();
        check(state.locationId().equals("entrada_biblioteca"), "Partida começa na Entrada");
        check(state.position().equals(new NarrativePosition("c01_despertar", "n001")),
                "Cursor começa no despertar");
        check(state.unlockedLocations().equals(Set.of("entrada_biblioteca")),
                "Somente a Entrada começa desbloqueada");
        check(state.inventory().isEmpty() && state.memories().isEmpty(), "Nenhuma pista foi coletada");
        check(!state.flag("fantasma_confia"), "Flag ainda não definida vale falso");
        check(state.value("postura_entrada").isEmpty(), "Postura não é presumida");
        check(!state.canRewritePact(), "Reescrita não começa liberada");

        GameState independent = new GameState();
        state.giveItem("carta_entrada");
        check(independent.inventory().isEmpty(), "Partidas não compartilham inventário");
    }

    private static void inventoryAndSnapshots() {
        GameState state = new GameState();
        check(state.giveItem("carta_entrada"), "Primeira coleta adiciona a carta");
        check(!state.giveItem("carta_entrada"), "Coleta repetida não concede outra carta");
        Set<String> inventory = state.inventory();
        expect(UnsupportedOperationException.class, () -> inventory.add("medalhao_fantasma"),
                "Consulta não permite inserir um item");
        state.giveItem("fotografia_sem_nome");
        check(inventory.equals(Set.of("carta_entrada")), "Snapshot não muda por alterações posteriores");
        check(state.inventory().size() == 2, "Inventário guarda itens distintos");
        check(!state.removeItem("medalhao_fantasma"), "Entrega direta não falha com item ausente");
        check(state.giveItem("medalhao_fantasma"), "Medalhão guardado entra no inventário");
        check(state.removeItem("medalhao_fantasma") && !state.hasItem("medalhao_fantasma"),
                "Devolução remove o objeto guardado");
        check(!state.removeItem("medalhao_fantasma"), "Repetir remoção não altera outros itens");
    }

    private static void memoryConsistency() {
        GameState state = new GameState();
        Map<String, String> memories = Map.of(
                "retorno", "memoria_retorno", "escolha", "memoria_escolha", "pacto", "memoria_pacto");
        for (Map.Entry<String, String> memory : memories.entrySet()) {
            check(state.unlockMemory(memory.getKey()), "Memória pode ser descoberta");
            check(!state.unlockMemory(memory.getKey()), "Memória não é duplicada");
            check(state.flag(memory.getValue()) && state.hasMemory(memory.getKey()),
                    "Desbloquear memória também define a flag");
            state.setFlag(memory.getValue(), false);
            check(!state.hasMemory(memory.getKey()) && !state.flag(memory.getValue()),
                    "Flag falsa não deixa uma memória divergente");
            state.setFlag(memory.getValue(), true);
            check(state.hasMemory(memory.getKey()), "Definir flag também atualiza o diário");
        }
        check(state.memories().size() == 3, "Diário contém as três memórias oficiais");
        expect(UnsupportedOperationException.class, () -> state.memories().clear(),
                "Consulta não permite apagar memórias");
        Map<String, Boolean> flags = state.flags();
        expect(UnsupportedOperationException.class, () -> flags.put("memoria_retorno", false),
                "Consulta não permite contornar a sincronização");
        state.setFlag("fantasma_confia", true);
        check(!flags.containsKey("fantasma_confia"), "Snapshot de flags não é uma visão mutável do estado");
    }

    private static void choicesAndNarrativePosition() {
        GameState state = new GameState();
        state.recordChoice("c06_nome_omitido", "n001", "n001_op1");
        state.recordChoice("c10_primeira_impressao", "n001", "n001_op3");
        check(state.choices().size() == 2, "Mesmo ID de nó em cenas diferentes não mistura escolhas");
        check(state.choiceAt("c06_nome_omitido", "n001").orElseThrow().equals("n001_op1"),
                "A escolha de Rodion continua registrada");
        check(state.choiceAt("c10_primeira_impressao", "n001").orElseThrow().equals("n001_op3"),
                "A reação de medo é registrada separadamente");
        check(state.choiceAt("c02_carta", "n002").isEmpty(), "Não inventa uma escolha ainda não feita");
        expect(UnsupportedOperationException.class, () -> state.choices().clear(),
                "Consulta não apaga escolhas");
        state.setValue("fantasma_primeira_reacao", "medo");
        check(state.value("fantasma_primeira_reacao").orElseThrow().equals("medo"),
                "Postura narrativa é consultável");
        expect(UnsupportedOperationException.class, () -> state.values().put("postura_entrada", "decidida"),
                "Consulta não altera valores narrativos");
        state.setNarrativePosition("c02_carta", "n002");
        check(state.position().equals(new NarrativePosition("c02_carta", "n002")),
                "Cena e nó avançam juntos");
        check(state.locationId().equals("entrada_biblioteca"), "Avançar um nó não troca o ambiente");
        check(state.completeInteraction("c02_carta/investigar"), "Primeira investigação é registrada");
        check(!state.completeInteraction("c02_carta/investigar"), "Conclusão repetida é identificada");
        check(state.isInteractionCompleted("c02_carta/investigar"), "Descoberta pode ser consultada");
        expect(UnsupportedOperationException.class, () -> state.completedInteractions().clear(),
                "Consulta não reinicia interações concluídas");
    }

    private static void locationAccess() {
        GameState state = new GameState();
        expect(IllegalStateException.class, () -> state.moveToLocation("arquivo_proibido"),
                "Não permite navegar a um local bloqueado");
        check(state.locationId().equals("entrada_biblioteca"), "Tentativa recusada preserva o local atual");
        check(state.unlockLocation("salao_principal"), "Salão pode ser desbloqueado");
        check(!state.unlockLocation("salao_principal"), "Desbloqueio não é duplicado");
        check(state.locationId().equals("entrada_biblioteca"), "Desbloquear não navega automaticamente");
        state.moveToLocation("salao_principal");
        check(state.locationId().equals("salao_principal"), "Navega a um local liberado");
        expect(UnsupportedOperationException.class, () -> state.unlockedLocations().add("arquivo_proibido"),
                "Consulta não contorna o desbloqueio");

        GameState configured = new GameState("salao_principal", "c04_livro_mutavel", "n001");
        configured.unlockLocation("balcao_rodion");
        configured.moveToLocation("balcao_rodion");
        configured.reset();
        check(configured.locationId().equals("salao_principal")
                        && configured.position().sceneId().equals("c04_livro_mutavel"),
                "Reinício respeita o ponto inicial fornecido pelo chamador");
        check(configured.unlockedLocations().equals(Set.of("salao_principal")),
                "Reinício não mantém locais liberados durante a partida");
    }

    private static void secretEndingCombinations() {
        String[] requiredFlags = { "memoria_retorno", "memoria_escolha", "memoria_pacto",
                "fantasma_confia", "regra_reescrita_descoberta" };
        for (int combination = 0; combination < 32; combination++) {
            GameState state = new GameState();
            for (int bit = 0; bit < requiredFlags.length; bit++) {
                state.setFlag(requiredFlags[bit], (combination & (1 << bit)) != 0);
            }
            state.setValue("fantasma_primeira_reacao", "medo");
            state.setValue("rodion_confissao_resposta", "confronto");
            state.setFlag("final_secreto_revelado", false);
            check(state.canRewritePact() == (combination == 31),
                    "Reescrita exige somente os cinco requisitos: combinação " + combination);
        }
        GameState state = new GameState();
        state.setFlag("final_secreto_revelado", true);
        check(!state.canRewritePact(), "Sinalizar revelação não substitui os requisitos");
    }

    private static void invalidChangesAreRejected() {
        GameState state = new GameState();
        state.setValue("postura_entrada", "decidida");
        NarrativePosition position = state.position();
        expect(IllegalArgumentException.class, () -> state.giveItem(null), "Item nulo é recusado");
        expect(IllegalArgumentException.class, () -> state.setFlag(" ", true), "Flag em branco é recusada");
        expect(IllegalArgumentException.class, () -> state.unlockLocation(" salao_principal"),
                "Espaço indevido no ID é recusado");
        expect(IllegalArgumentException.class, () -> state.unlockMemory("desconhecida"),
                "Memória fora do capítulo é recusada");
        expect(IllegalArgumentException.class, () -> state.setNarrativePosition("c02_carta", ""),
                "Cursor incompleto é recusado");
        expect(IllegalArgumentException.class, () -> state.recordChoice("c02_carta", "n002", null),
                "Escolha sem opção é recusada");
        expect(IllegalArgumentException.class, () -> state.setValue("postura_entrada", null),
                "Valor nulo é recusado sem apagar a escolha anterior");
        check(state.position().equals(position) && state.choices().isEmpty()
                        && state.inventory().isEmpty() && state.memories().isEmpty() && state.flags().isEmpty(),
                "Entradas inválidas não deixam alterações parciais");
        check(state.value("postura_entrada").orElseThrow().equals("decidida"),
                "Valor válido anterior permanece intacto");
    }

    private static void newGameResetsProgress() throws Exception {
        GameController controller = new GameController(new SaveManager(Files.createTempDirectory("biblioteca-state-teste-")));
        GameState state = controller.state();
        state.giveItem("carta_entrada");
        state.unlockMemory("retorno");
        state.unlockMemory("escolha");
        state.unlockMemory("pacto");
        state.setFlag("fantasma_confia", true);
        state.setFlag("regra_reescrita_descoberta", true);
        state.setValue("final_conquistado", "reescrever");
        state.recordChoice("c25_decisao", "n004", "reescrever");
        state.completeInteraction("c02_carta/investigar");
        state.unlockLocation("salao_principal");
        state.moveToLocation("salao_principal");
        state.setNarrativePosition("c25_decisao", "n004");
        controller.settings().toggleEffects();
        controller.settings().cycleTextSpeed();
        controller.settings().setFullscreen(true);
        check(state.canRewritePact(), "Partida anterior tinha todos os requisitos secretos");

        controller.newGame();

        check(controller.state() == state, "Novo Jogo preserva a referência usada pelos consumidores");
        check(state.flags().isEmpty() && state.values().isEmpty() && state.choices().isEmpty()
                        && state.inventory().isEmpty() && state.memories().isEmpty()
                        && state.completedInteractions().isEmpty(),
                "Novo Jogo limpa todas as categorias de progresso");
        check(state.locationId().equals("entrada_biblioteca")
                        && state.unlockedLocations().equals(Set.of("entrada_biblioteca"))
                        && state.position().equals(new NarrativePosition("c01_despertar", "n001")),
                "Novo Jogo restaura posição e acesso iniciais");
        check(!state.canRewritePact(), "Nova partida não herda requisitos secretos");
        check(!controller.settings().effectsEnabled() && controller.settings().textSpeed() == 2
                        && controller.settings().fullscreen(),
                "Novo Jogo preserva preferências");
    }

    private static void expect(Class<? extends RuntimeException> type, Runnable operation, String message) {
        try {
            operation.run();
        } catch (RuntimeException error) {
            if (type.isInstance(error)) return;
            throw new AssertionError(message + ": exceção inesperada", error);
        }
        throw new AssertionError(message + ": exceção esperada não ocorreu");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
