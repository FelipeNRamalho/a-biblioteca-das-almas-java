package br.com.bibliotecadasalmas;

import br.com.bibliotecadasalmas.characters.CharacterRegistry;
import br.com.bibliotecadasalmas.core.GameController;
import br.com.bibliotecadasalmas.core.GameMode;
import br.com.bibliotecadasalmas.core.GameState;
import br.com.bibliotecadasalmas.core.SaveManager;
import br.com.bibliotecadasalmas.narrative.NarrativeNode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Testes de integração do Ato II: Corredor dos Livros Esquecidos e A Fantasma (cenas 08–12).
 * Cobre as 12 rotas de escolhas, cutscene, retratos, reparabilidade de confiança (R05),
 * entrega imediata (R06), recuperação adiada de fotografia/Memória 2 (R04) e persistência.
 */
public final class ActTwoGameplayTest {
    public static void main(String[] args) throws Exception {
        testAllActTwoRoutes();
        testReparableTrustR05();
        testImmediateReturnR06();
        testDelayedPhotoCollectionR04();
        testCorridorNavigationAndRevisit();
        testSaveAndRestoreActTwo();
        System.out.println("OK: Ato II (Corredor e Fantasma) passou em todas as 12 rotas, R04, R05, R06, navegação e persistência.");
    }

    private static void testAllActTwoRoutes() throws Exception {
        for (int optC10 = 1; optC10 <= 3; optC10++) {
            for (int optC11 = 1; optC11 <= 2; optC11++) {
                for (int optC12 = 1; optC12 <= 2; optC12++) {
                    runActTwoRoute(optC10, optC11, optC12);
                }
            }
        }
    }

    private static void runActTwoRoute(int optC10, int optC11, int optC12) throws Exception {
        GameController game = reachBalcaoWithCorridorUnlocked();

        // Viaja do balcão para o corredor dos esquecidos
        game.travel("corredor_esquecidos", null);
        check(game.state().locationId().equals("corredor_esquecidos"), "Moveu-se para o corredor dos esquecidos");

        // Cena 08 (c08_corredor) inicia automaticamente
        check(game.session().scene().id().equals("c08_corredor"), "Iniciou cena 08 no corredor");
        drainScene(game, "c08_corredor");

        // Transição automática para Cena 09 (c09_fantasma_aparicao) com cutscene
        check(game.session().scene().id().equals("c09_fantasma_aparicao"), "Avançou para cena 09");
        check(game.session().node() instanceof NarrativeNode.Cutscene cutscene
                && cutscene.asset().equals("fantasma_primeira_aparicao"), "Cutscene da primeira aparição da Fantasma exibida");
        drainScene(game, "c09_fantasma_aparicao");
        check(game.state().flag("fantasma_encontrada"), "Flag fantasma_encontrada definida na cena 09");

        // Transição automática para Cena 10 (c10_primeira_impressao)
        check(game.session().scene().id().equals("c10_primeira_impressao"), "Avançou para cena 10");
        check(game.session().node() instanceof NarrativeNode.Choice, "Cena 10 apresenta escolha de primeira impressão");

        String expectedReaction = switch (optC10) {
            case 1 -> "empatia";
            case 2 -> "curiosidade";
            case 3 -> "medo";
            default -> throw new IllegalArgumentException();
        };
        game.chooseStory("n001_op" + optC10);
        check(game.state().value("fantasma_primeira_reacao").orElse("").equals(expectedReaction),
                "Reação da Fantasma registrada: " + expectedReaction);
        drainScene(game, "c10_primeira_impressao");

        // Transição automática para Cena 11 (c11_fotografia)
        check(game.session().scene().id().equals("c11_fotografia"), "Avançou para cena 11");
        drainToChoice(game);
        check(game.session().node() instanceof NarrativeNode.Choice, "Cena 11 apresenta escolha de investigar fotografia");

        game.chooseStory("n002_op" + optC11);
        if (optC11 == 1) {
            check(game.state().hasItem("fotografia_sem_nome"), "Fotografia sem nome no inventário");
            check(game.state().hasMemory("escolha"), "Memória 2 (A Escolha) desbloqueada");
            check(game.state().flag("fotografia_coletada"), "Flag fotografia_coletada definida");
            check(game.state().flag("memoria_escolha"), "Flag memoria_escolha definida");
        } else {
            check(!game.state().hasItem("fotografia_sem_nome"), "Fotografia não recolhida se adiada");
            check(!game.state().hasMemory("escolha"), "Memória 2 não desbloqueada se adiada");
        }
        drainScene(game, "c11_fotografia");

        // Transição automática para Cena 12 (c12_medalhao)
        check(game.session().scene().id().equals("c12_medalhao"), "Avançou para cena 12");
        drainToChoice(game);
        check(game.session().node() instanceof NarrativeNode.Choice, "Cena 12 apresenta escolha do medalhão");

        game.chooseStory("n002_op" + optC12);
        if (optC12 == 1) {
            check(!game.state().hasItem("medalhao_fantasma"), "Medalhão não fica no inventário ao ser entregue");
            check(game.state().flag("medalhao_devolvido"), "Flag medalhao_devolvido definida");
            check(game.state().flag("fantasma_confia"), "Flag fantasma_confia definida");
        } else {
            check(game.state().hasItem("medalhao_fantasma"), "Medalhão guardado no inventário");
            check(!game.state().flag("medalhao_devolvido"), "Flag medalhao_devolvido ainda falsa");
            check(!game.state().flag("fantasma_confia"), "Flag fantasma_confia ainda falsa");
        }
        drain(game);

        // Fim do Ato II: revelação do passado e suspensão em modo exploração
        check(game.state().flag("fantasma_revelou_passado"), "Fantasma revelou passado ao final do Ato II");
        check(!game.session().active(), "Sessão suspensa após c12 (modo exploração ativo)");
        check(game.state().locationId().equals("corredor_esquecidos"), "Permanece em corredor_esquecidos para exploração");
    }

    /**
     * R05 — Confiança reparável:
     * Recuar diante da Fantasma (medo), guardar o medalhão e avançar.
     * Retornar e devolvê-lo depois: remove o item e define medalhao_devolvido e fantasma_confia.
     */
    private static void testReparableTrustR05() throws Exception {
        GameController game = reachBalcaoWithCorridorUnlocked();
        game.travel("corredor_esquecidos", null);
        drainScene(game, "c08_corredor");
        drainScene(game, "c09_fantasma_aparicao");
        game.chooseStory("n001_op3"); // medo
        drainScene(game, "c10_primeira_impressao");
        drainToChoice(game); // c11 (n001 -> n002)
        game.chooseStory("n002_op2"); // adiar foto
        drainScene(game, "c11_fotografia");
        drainToChoice(game); // c12 (n001 -> n002)
        game.chooseStory("n002_op2"); // guardar medalhão
        drain(game); // c12

        check(game.state().value("fantasma_primeira_reacao").orElse("").equals("medo"), "Reação inicial foi medo");
        check(game.state().hasItem("medalhao_fantasma"), "Medalhão está no inventário");
        check(!game.state().flag("fantasma_confia"), "Fantasma ainda não confia");
        check(!game.state().flag("medalhao_devolvido"), "Medalhão ainda não devolvido");

        // O hotspot do medalhão permanece visível enquanto medalhao_devolvido for falso
        check(game.hotspots().stream().anyMatch(h -> h.id().equals("medalhao")),
                "Hotspot de entrega do medalhão visível para reparação da confiança");

        // Natasha conversa com a Fantasma sobre o medalhão guardado
        game.interact("medalhao");
        check(game.session().scene().id().equals("c12_medalhao"), "Cena 12 reaberta pelo hotspot");
        drainToChoice(game); // avança até n002
        check(game.session().node() instanceof NarrativeNode.Choice, "Escolha do medalhão reapresentada");

        // Natasha decide devolver o medalhão
        game.chooseStory("n002_op1");
        check(!game.state().hasItem("medalhao_fantasma"), "Medalhão removido do inventário após entrega");
        check(game.state().flag("medalhao_devolvido"), "medalhao_devolvido agora verdadeiro");
        check(game.state().flag("fantasma_confia"), "fantasma_confia agora verdadeiro mesmo após reação inicial de medo");
        drain(game);

        // Agora o hotspot não aparece mais
        check(game.hotspots().stream().noneMatch(h -> h.id().equals("medalhao")),
                "Hotspot do medalhão ocultado após devolução (evita duplicação)");
    }

    /**
     * R06 — Entrega imediata:
     * Em partida nova, devolver o medalhão na primeira oportunidade, sem guardá-lo antes.
     * A entrega não falha por remover item ausente; confiança é concedida e nada sobra no inventário.
     */
    private static void testImmediateReturnR06() throws Exception {
        GameController game = reachBalcaoWithCorridorUnlocked();
        game.travel("corredor_esquecidos", null);
        drainScene(game, "c08_corredor");
        drainScene(game, "c09_fantasma_aparicao");
        game.chooseStory("n001_op1"); // empatia
        drainScene(game, "c10_primeira_impressao");
        drainToChoice(game); // c11 (n001 -> n002)
        game.chooseStory("n002_op1"); // foto
        drainScene(game, "c11_fotografia");
        drainToChoice(game); // c12 (n001 -> n002)

        check(!game.state().hasItem("medalhao_fantasma"), "Medalhão ainda não está no inventário");
        game.chooseStory("n002_op1"); // Devolver imediatamente

        check(!game.state().hasItem("medalhao_fantasma"), "Medalhão não permanece no inventário");
        check(game.state().flag("medalhao_devolvido"), "medalhao_devolvido é verdadeiro");
        check(game.state().flag("fantasma_confia"), "fantasma_confia é concedido");
        drain(game);
    }

    /**
     * R04 — Recuperação adiada da fotografia e Memória 2:
     * Adiar a fotografia na cena 11 e depois investigar no corredor via hotspot.
     */
    private static void testDelayedPhotoCollectionR04() throws Exception {
        GameController game = reachBalcaoWithCorridorUnlocked();
        game.travel("corredor_esquecidos", null);
        drainScene(game, "c08_corredor");
        drainScene(game, "c09_fantasma_aparicao");
        game.chooseStory("n001_op2"); // curiosidade
        drainScene(game, "c10_primeira_impressao");
        drainToChoice(game); // c11 (n001 -> n002)
        game.chooseStory("n002_op2"); // Deixar fotografia
        drainScene(game, "c11_fotografia");
        drainToChoice(game); // c12 (n001 -> n002)
        game.chooseStory("n002_op1"); // Devolver medalhão
        drain(game); // c12

        check(!game.state().hasItem("fotografia_sem_nome"), "Fotografia não recolhida inicialmente");
        check(!game.state().hasMemory("escolha"), "Memória 2 não desbloqueada inicialmente");

        // Hotspot de fotografia permanece visível
        check(game.hotspots().stream().anyMatch(h -> h.id().equals("fotografia")),
                "Hotspot da fotografia permanece visível após ser adiada");

        // Reinvestiga a fotografia
        game.interact("fotografia");
        check(game.session().scene().id().equals("c11_fotografia"), "Cena 11 reiniciada pela investigação");
        drainToChoice(game); // avança até n002
        game.chooseStory("n002_op1"); // Agora examina a imagem e o verso

        check(game.state().hasItem("fotografia_sem_nome"), "Fotografia recolhida com sucesso");
        check(game.state().hasMemory("escolha"), "Memória 2 desbloqueada no retorno");
        check(game.state().flag("fotografia_coletada"), "Flag fotografia_coletada definida");
        drain(game);

        // Agora o hotspot da fotografia foi ocultado
        check(game.hotspots().stream().noneMatch(h -> h.id().equals("fotografia")),
                "Hotspot da fotografia ocultado após coleta");
    }

    /**
     * Navegação completa e livre entre todos os 4 ambientes integrados:
     * entrada_biblioteca <-> salao_principal <-> balcao_rodion <-> corredor_esquecidos.
     */
    private static void testCorridorNavigationAndRevisit() throws Exception {
        GameController game = reachBalcaoWithCorridorUnlocked();
        game.travel("corredor_esquecidos", null);
        drainScene(game, "c08_corredor");
        drainScene(game, "c09_fantasma_aparicao");
        game.chooseStory("n001_op1"); drainScene(game, "c10_primeira_impressao");
        drainToChoice(game); game.chooseStory("n002_op1"); drainScene(game, "c11_fotografia");
        drainToChoice(game); game.chooseStory("n002_op1"); drain(game); // c12

        check(game.state().locationId().equals("corredor_esquecidos"), "Está no corredor");

        // Retorna ao balcão
        game.interact("balcao");
        check(game.state().locationId().equals("balcao_rodion"), "Voltou ao balcão");

        // Retorna ao salão
        game.interact("salao");
        check(game.state().locationId().equals("salao_principal"), "Voltou ao salão principal");

        // Retorna à entrada
        game.interact("entrada");
        check(game.state().locationId().equals("entrada_biblioteca"), "Voltou à entrada");

        // Navega de volta até o corredor
        game.interact("portas");
        check(game.state().locationId().equals("salao_principal"), "Atravessou para o salão");
        game.interact("balcao");
        check(game.state().locationId().equals("balcao_rodion"), "Atravessou para o balcão");
        game.interact("corredor");
        check(game.state().locationId().equals("corredor_esquecidos"), "Retornou ao corredor dos esquecidos");
        check(!game.session().active(), "Exploração permanece ativa sem reiniciar diálogos completados");
    }

    private static void testSaveAndRestoreActTwo() throws Exception {
        Path tempDir = Files.createTempDirectory("biblioteca-acttwo-save-");
        GameController game = new GameController(new SaveManager(tempDir));
        game.newGame();
        drain(game); // c01
        game.interact("portas"); drain(game); game.chooseStory("n004_op1"); drain(game); // c03
        game.interact("livro"); drain(game); // c04
        game.interact("balcao"); drain(game); // c05
        game.chooseStory("n001_op2"); drain(game); // c06
        game.chooseStory("n001_op1"); drain(game); // c07
        game.travel("corredor_esquecidos", null);
        drainScene(game, "c08_corredor");
        drainScene(game, "c09_fantasma_aparicao");

        // Salva durante a escolha da cena 10
        check(game.session().scene().id().equals("c10_primeira_impressao"), "Em c10 antes do reload");
        GameController reloaded = new GameController(new SaveManager(tempDir));
        reloaded.continueGame();
        check(reloaded.mode() == GameMode.STORY && reloaded.session().active(), "Save em c10 restaurado em modo STORY ativo");
        check(reloaded.session().scene().id().equals("c10_primeira_impressao"), "Restaurou exatamente em c10");
        check(reloaded.session().node() instanceof NarrativeNode.Choice, "Restaurou na escolha de c10");

        reloaded.chooseStory("n001_op1"); drainScene(reloaded, "c10_primeira_impressao");
        drainToChoice(reloaded); reloaded.chooseStory("n002_op1"); drainScene(reloaded, "c11_fotografia");
        drainToChoice(reloaded); reloaded.chooseStory("n002_op1"); drain(reloaded); // c12

        check(reloaded.state().flag("fantasma_revelou_passado"), "Concluiu Ato II após reload");
        check(reloaded.state().hasItem("fotografia_sem_nome"), "Item fotografia preservado");
        check(reloaded.state().hasMemory("escolha"), "Memória 2 preservada");
        check(reloaded.state().flag("fantasma_confia"), "fantasma_confia preservado");

        // Salva após conclusão do Ato II e recarrega
        GameController reloaded2 = new GameController(new SaveManager(tempDir));
        reloaded2.continueGame();
        check(reloaded2.state().locationId().equals("corredor_esquecidos"), "Local corredor_esquecidos restaurado");
        check(!reloaded2.session().active(), "Modo exploração restaurado");
        check(reloaded2.state().hasItem("fotografia_sem_nome"), "Fotografia permanece no inventário");
        check(reloaded2.state().hasMemory("escolha"), "Memória 2 permanece registrada");
        check(reloaded2.state().flag("fantasma_confia"), "fantasma_confia permanece");
    }

    private static GameController reachBalcaoWithCorridorUnlocked() throws Exception {
        Path tempDir = Files.createTempDirectory("biblioteca-acttwo-temp-");
        GameController game = new GameController(new SaveManager(tempDir));
        game.newGame();
        drain(game); // c01
        game.interact("portas"); drain(game); game.chooseStory("n004_op1"); drain(game); // c03
        game.interact("livro"); drain(game); // c04
        game.interact("balcao"); drain(game); // c05
        game.chooseStory("n001_op2"); drain(game); // c06
        game.chooseStory("n001_op1"); drain(game); // c07 (desbloqueia corredor_esquecidos)
        return game;
    }

    private static void drainScene(GameController game, String sceneId) {
        int steps = 0;
        while (game.session().active() && game.session().scene().id().equals(sceneId)) {
            if (++steps > 200) throw new IllegalStateException("Loop infinito ao drenar cena: " + sceneId);
            game.advanceStory();
        }
    }

    private static void drainToChoice(GameController game) {
        int steps = 0;
        while (game.session().active() && !(game.session().node() instanceof NarrativeNode.Choice)) {
            if (++steps > 200) throw new IllegalStateException("Loop infinito até escolha");
            game.advanceStory();
        }
    }

    private static void drain(GameController game) {
        int steps = 0;
        while (game.session().active() && !(game.session().node() instanceof NarrativeNode.Choice)) {
            if (++steps > 200) throw new IllegalStateException("Loop infinito ao drenar narrativa");
            game.advanceStory();
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError("Falha no teste: " + message);
    }
}
