package br.com.bibliotecadasalmas;

import br.com.bibliotecadasalmas.core.GameController;
import br.com.bibliotecadasalmas.core.GameMode;
import br.com.bibliotecadasalmas.core.SaveManager;
import br.com.bibliotecadasalmas.narrative.NarrativeNode;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Testes de integração do Ato III: O Arquivo Proibido e A Confissão (cenas 13–19).
 * Cobre as 12 rotas de escolhas (Cena 14 × Cena 18 × Cena 19), cutscenes, Memória 3 (O Pacto),
 * recuperação adiada da cláusula sem reiniciar cenas concluídas, os 5 requisitos da reescrita
 * (canRewritePact), navegação livre entre os 5 ambientes e persistência por save/load.
 */
public final class ActThreeGameplayTest {
    public static void main(String[] args) throws Exception {
        testAllActThreeRoutes();
        testDelayedClauseInvestigation();
        testSecretRewriteFiveConditions();
        testFullFiveEnvironmentNavigation();
        testSaveAndRestoreActThree();
        System.out.println("OK: Ato III (Arquivo Proibido e Confissão) passou em todas as 12 rotas, recuperação adiada da cláusula, 5 requisitos da reescrita, navegação em 5 ambientes e persistência.");
    }

    private static void testAllActThreeRoutes() throws Exception {
        for (int optC14 = 1; optC14 <= 2; optC14++) {
            for (int optC18 = 1; optC18 <= 2; optC18++) {
                for (int optC19 = 1; optC19 <= 3; optC19++) {
                    runActThreeRoute(optC14, optC18, optC19);
                }
            }
        }
    }

    private static void runActThreeRoute(int optC14, int optC18, int optC19) throws Exception {
        GameController game = reachActThreeReady(true, true, true);

        // No salão principal, com fantasma_revelou_passado = true, o hotspot da estante está visível
        check(game.hotspots().stream().anyMatch(h -> h.id().equals("passagem_arquivo")),
                "Hotspot da passagem oculta visível no salão");

        // Clica na passagem para o Arquivo Proibido
        game.interact("passagem_arquivo");

        // Cena 13 (c13_passagem) inicia
        check(game.session().scene().id().equals("c13_passagem"), "Iniciou cena 13 no salão");
        drainScene(game, "c13_passagem");
        check(game.state().flag("passagem_descoberta"), "Flag passagem_descoberta definida na cena 13");
        check(game.state().isLocationUnlocked("arquivo_proibido"), "Local arquivo_proibido desbloqueado");

        // Transição automática para Cena 14 (c14_escadaria_oculta)
        check(game.session().scene().id().equals("c14_escadaria_oculta"), "Avançou para cena 14");
        check(game.state().locationId().equals("arquivo_proibido"), "Posição atualizada para arquivo_proibido");
        drainToChoice(game);
        check(game.session().node() instanceof NarrativeNode.Choice, "Cena 14 apresenta escolha da escadaria");

        game.chooseStory("n006_op" + optC14);
        check(game.state().flag("entrou_arquivo"), "Flag entrou_arquivo definida");
        drainScene(game, "c14_escadaria_oculta");

        // Transição automática para Cena 15 (c15_arquivo)
        check(game.session().scene().id().equals("c15_arquivo"), "Avançou para cena 15");
        drainScene(game, "c15_arquivo");

        // Transição automática para Cena 16 (c16_livro_natasha) com cutscene
        check(game.session().scene().id().equals("c16_livro_natasha"), "Avançou para cena 16");
        check(game.session().node() instanceof NarrativeNode.Cutscene cutscene
                && cutscene.asset().equals("natasha_descobrindo_livro"), "Cutscene natasha_descobrindo_livro exibida");
        drainScene(game, "c16_livro_natasha");

        // Verifica efeitos da Cena 16 (Livro de Natasha e Memória 3)
        check(game.state().flag("livro_natasha_descoberto"), "Flag livro_natasha_descoberto definida");
        check(game.state().flag("pacto_revelado"), "Flag pacto_revelado definida");
        check(game.state().flag("memoria_pacto"), "Flag memoria_pacto definida");
        check(game.state().hasMemory("pacto"), "Memória 3 (pacto) desbloqueada");
        check(game.state().memories().size() == 3, "Todas as 3 memórias desbloqueadas (retorno, escolha, pacto)");

        // Transição automática para Cena 17 (c17_registros)
        check(game.session().scene().id().equals("c17_registros"), "Avançou para cena 17");
        drainScene(game, "c17_registros");

        // Transição automática para Cena 18 (c18_clausula)
        check(game.session().scene().id().equals("c18_clausula"), "Avançou para cena 18");
        drainToChoice(game);
        check(game.session().node() instanceof NarrativeNode.Choice, "Cena 18 apresenta escolha da cláusula");

        game.chooseStory("n002_op" + optC18);
        if (optC18 == 1) {
            check(game.state().flag("regra_reescrita_descoberta"), "regra_reescrita_descoberta definida na cena 18");
            check(game.state().hasItem("copia_clausula"), "Item copia_clausula recebido");
        } else {
            check(!game.state().flag("regra_reescrita_descoberta"), "regra_reescrita_descoberta não definida ao ignorar");
            check(!game.state().hasItem("copia_clausula"), "Item copia_clausula não recolhido");
        }
        drainScene(game, "c18_clausula");

        // Transição automática para Cena 19 (c19_confissao_rodion)
        check(game.session().scene().id().equals("c19_confissao_rodion"), "Avançou para cena 19");
        check(game.state().locationId().equals("balcao_rodion"), "Localização atualizada para balcao_rodion na cena 19");
        check(game.session().node() instanceof NarrativeNode.Cutscene cutscene
                && cutscene.asset().equals("rodion_revelacao"), "Cutscene rodion_revelacao exibida");

        drainToChoice(game);
        check(game.session().node() instanceof NarrativeNode.Choice, "Cena 19 apresenta escolha de resposta à confissão");

        String expectedResponse = switch (optC19) {
            case 1 -> "confronto";
            case 2 -> "limites";
            case 3 -> "reconciliacao";
            default -> throw new IllegalArgumentException();
        };
        game.chooseStory("n036_op" + optC19);
        check(game.state().value("rodion_confissao_resposta").orElse("").equals(expectedResponse),
                "Resposta à confissão registrada: " + expectedResponse);

        drain(game); // Conclui cena 19

        // Efeitos de conclusão da Cena 19
        check(game.state().flag("rodion_confessou"), "Flag rodion_confessou definida");
        check(game.state().hasItem("anotacoes_rodion"), "Item anotacoes_rodion recebido no inventário");
        check(game.state().isLocationUnlocked("camara_registros"), "Local camara_registros desbloqueado");
        check(!game.session().active(), "Sessão suspensa após cena 19 (política explore)");
        check(game.state().locationId().equals("balcao_rodion"), "Jogador permanece no balcão de Rodion para exploração");

        // Verifica estado do final secreto (canRewritePact)
        if (optC18 == 1) {
            check(game.state().canRewritePact(), "Os 5 requisitos da reescrita satisfeitos na rota completa");
        } else {
            check(!game.state().canRewritePact(), "Reescrita indisponível quando a cláusula foi ignorada");
        }
    }

    /**
     * Recuperação adiada da Cláusula Esquecida:
     * O jogador ignora o documento na cena 18, completa a confissão de Rodion e depois
     * retorna ao Arquivo Proibido via exploração para investigar o documento.
     * Não deve reiniciar a confissão de Rodion e não deve duplicar itens.
     */
    private static void testDelayedClauseInvestigation() throws Exception {
        // Inicia com carta, foto e medalhão devolvido
        GameController game = reachActThreeReady(true, true, true);
        game.interact("passagem_arquivo");
        drainScene(game, "c13_passagem");

        // Cena 14: desce a escadaria
        drainToChoice(game); game.chooseStory("n006_op1"); drainScene(game, "c14_escadaria_oculta");
        drainScene(game, "c15_arquivo");
        drainScene(game, "c16_livro_natasha");
        drainScene(game, "c17_registros");

        // Cena 18: Ignora o documento antigo (op2)
        drainToChoice(game); game.chooseStory("n002_op2"); drainScene(game, "c18_clausula");

        // Cena 19: Completa a confissão de Rodion
        drainToChoice(game); game.chooseStory("n036_op2"); drain(game);

        check(!game.state().hasItem("copia_clausula"), "Cláusula não recolhida inicialmente");
        check(!game.state().flag("regra_reescrita_descoberta"), "regra_reescrita_descoberta falsa inicialmente");
        check(!game.state().canRewritePact(), "canRewritePact é falso sem a cláusula");
        check(game.state().locationId().equals("balcao_rodion"), "Está no balcão após confissão");

        // Volta ao salão principal
        game.interact("salao");
        check(game.state().locationId().equals("salao_principal"), "Voltou ao salão principal");

        // Entra no Arquivo Proibido pela passagem já desbloqueada
        game.interact("passagem_arquivo");
        check(game.state().locationId().equals("arquivo_proibido"), "Entrou no Arquivo Proibido");

        // Hotspot da cláusula deve estar visível
        check(game.hotspots().stream().anyMatch(h -> h.id().equals("clausula")),
                "Hotspot da cláusula visível para investigação adiada");

        // Investiga o documento antigo no Arquivo
        game.interact("clausula");
        check(game.session().scene().id().equals("c18_clausula"), "Cena 18 reaberta pelo hotspot");
        drainToChoice(game);

        // Desta vez, lê as cláusulas
        game.chooseStory("n002_op1");
        check(game.state().hasItem("copia_clausula"), "copia_clausula recebida no retorno");
        check(game.state().flag("regra_reescrita_descoberta"), "regra_reescrita_descoberta definida no retorno");

        // Drena até o fim de c18: como c19 já foi concluída, a sessão não reinicia c19 e suspende
        drain(game);
        check(!game.session().active(), "Sessão concluída após reinvestigação da cláusula");
        check(game.state().locationId().equals("arquivo_proibido"), "Permaneceu no Arquivo Proibido");

        // Agora o hotspot da cláusula foi ocultado
        check(game.hotspots().stream().noneMatch(h -> h.id().equals("clausula")),
                "Hotspot da cláusula ocultado após descoberta (evita duplicação)");

        // Agora todos os 5 requisitos foram reunidos
        check(game.state().canRewritePact(), "canRewritePact é verdadeiro após recuperação da cláusula");
    }

    /**
     * Verificação estrita dos cinco requisitos do final secreto (canRewritePact):
     * 1. Memória 1 (retorno)
     * 2. Memória 2 (escolha)
     * 3. Memória 3 (pacto)
     * 4. fantasma_confia == true
     * 5. regra_reescrita_descoberta == true
     * Se QUALQUER um dos cinco faltar, canRewritePact() deve ser false.
     */
    private static void testSecretRewriteFiveConditions() throws Exception {
        // Caso A: Faltou Memória 1 (porta direta, sem carta)
        GameController gameA = reachActThreeReady(false, true, true);
        completeActThree(gameA, true);
        check(!gameA.state().hasMemory("retorno"), "Caso A: sem memória 1");
        check(!gameA.state().canRewritePact(), "Caso A: canRewritePact falso sem memória 1");

        // Caso B: Faltou Memória 2 (fotografia adiada e não coletada)
        GameController gameB = reachActThreeReady(true, false, true);
        completeActThree(gameB, true);
        check(!gameB.state().hasMemory("escolha"), "Caso B: sem memória 2");
        check(!gameB.state().canRewritePact(), "Caso B: canRewritePact falso sem memória 2");

        // Caso C: Faltou Confiança da Fantasma (medalhão guardado e não devolvido)
        GameController gameC = reachActThreeReady(true, true, false);
        completeActThree(gameC, true);
        check(!gameC.state().flag("fantasma_confia"), "Caso C: sem fantasma_confia");
        check(!gameC.state().canRewritePact(), "Caso C: canRewritePact falso sem fantasma_confia");

        // Caso D: Faltou Cláusula (ignorada na cena 18)
        GameController gameD = reachActThreeReady(true, true, true);
        completeActThree(gameD, false);
        check(!gameD.state().flag("regra_reescrita_descoberta"), "Caso D: sem regra_reescrita_descoberta");
        check(!gameD.state().canRewritePact(), "Caso D: canRewritePact falso sem cláusula");

        // Caso E: Todos os 5 requisitos presentes
        GameController gameE = reachActThreeReady(true, true, true);
        completeActThree(gameE, true);
        check(gameE.state().canRewritePact(), "Caso E: canRewritePact verdadeiro com todos os 5 requisitos");
    }

    /**
     * Navegação livre entre todos os 5 ambientes integrados:
     * entrada_biblioteca <-> salao_principal <-> balcao_rodion <-> corredor_esquecidos
     * e salao_principal <-> arquivo_proibido.
     */
    private static void testFullFiveEnvironmentNavigation() throws Exception {
        GameController game = reachActThreeReady(true, true, true);
        completeActThree(game, true);

        check(game.state().locationId().equals("balcao_rodion"), "Inicia exploração no balcão");

        // Balcão -> Corredor
        game.interact("corredor");
        check(game.state().locationId().equals("corredor_esquecidos"), "Moveu ao corredor");

        // Corredor -> Balcão
        game.interact("balcao");
        check(game.state().locationId().equals("balcao_rodion"), "Voltou ao balcão");

        // Balcão -> Salão
        game.interact("salao");
        check(game.state().locationId().equals("salao_principal"), "Moveu ao salão");

        // Salão -> Entrada
        game.interact("entrada");
        check(game.state().locationId().equals("entrada_biblioteca"), "Moveu à entrada");

        // Entrada -> Salão
        game.interact("portas");
        check(game.state().locationId().equals("salao_principal"), "Voltou ao salão");

        // Salão -> Arquivo Proibido
        game.interact("passagem_arquivo");
        check(game.state().locationId().equals("arquivo_proibido"), "Moveu ao Arquivo Proibido");

        // Arquivo Proibido -> Salão
        game.interact("salao");
        check(game.state().locationId().equals("salao_principal"), "Voltou ao salão a partir do Arquivo");

        // Salão -> Balcão
        game.interact("balcao");
        check(game.state().locationId().equals("balcao_rodion"), "Voltou ao balcão com sucesso");
    }

    /**
     * Persistência de estado e posições narrativas do Ato III:
     * 1. Salva durante escolha da Cena 19 e recarrega.
     * 2. Conclui Ato III, salva e recarrega em modo exploração.
     */
    private static void testSaveAndRestoreActThree() throws Exception {
        Path tempDir = Files.createTempDirectory("biblioteca-actthree-save-");
        GameController game = new GameController(new SaveManager(tempDir));
        setupToActThree(game, true, true, true);

        // Avança até a escolha da cena 19
        game.interact("passagem_arquivo");
        drainScene(game, "c13_passagem");
        drainToChoice(game); game.chooseStory("n006_op1"); drainScene(game, "c14_escadaria_oculta");
        drainScene(game, "c15_arquivo");
        drainScene(game, "c16_livro_natasha");
        drainScene(game, "c17_registros");
        drainToChoice(game); game.chooseStory("n002_op1"); drainScene(game, "c18_clausula");
        drainToChoice(game);

        // Estado antes do reload: em c19 na escolha
        check(game.session().scene().id().equals("c19_confissao_rodion"), "Em c19 antes do reload");
        check(game.session().node() instanceof NarrativeNode.Choice, "Na escolha de c19");

        // Recarrega o jogo do save
        GameController reloaded = new GameController(new SaveManager(tempDir));
        reloaded.continueGame();
        check(reloaded.mode() == GameMode.STORY && reloaded.session().active(), "Restaurado em STORY ativo");
        check(reloaded.session().scene().id().equals("c19_confissao_rodion"), "Restaurou exatamente em c19");
        check(reloaded.session().node() instanceof NarrativeNode.Choice, "Restaurou na escolha de c19");

        // Conclui a escolha
        reloaded.chooseStory("n036_op3");
        drain(reloaded);

        check(reloaded.state().flag("rodion_confessou"), "Concluiu confissão após reload");
        check(reloaded.state().value("rodion_confissao_resposta").orElse("").equals("reconciliacao"), "Resposta reconciliação preservada");
        check(reloaded.state().hasItem("anotacoes_rodion"), "Anotações no inventário");
        check(reloaded.state().hasMemory("pacto"), "Memória 3 preservada");
        check(reloaded.state().isLocationUnlocked("camara_registros"), "Câmara desbloqueada");

        // Segundo reload: em modo exploração
        GameController reloaded2 = new GameController(new SaveManager(tempDir));
        reloaded2.continueGame();
        check(!reloaded2.session().active(), "Exploração ativa após segundo reload");
        check(reloaded2.state().locationId().equals("balcao_rodion"), "Local balcao_rodion preservado");
        check(reloaded2.state().hasItem("carta_entrada"), "Carta preservada");
        check(reloaded2.state().hasItem("fotografia_sem_nome"), "Fotografia preservada");
        check(reloaded2.state().hasItem("copia_clausula"), "Cláusula preservada");
        check(reloaded2.state().hasItem("anotacoes_rodion"), "Anotações de Rodion preservadas");
        check(reloaded2.state().memories().size() == 3, "Todas as 3 memórias preservadas");
        check(reloaded2.state().canRewritePact(), "canRewritePact preservado verdadeiro");
    }

    private static GameController reachActThreeReady(boolean collectLetter, boolean collectPhoto, boolean returnMedallion) throws Exception {
        Path tempDir = Files.createTempDirectory("biblioteca-actthree-temp-");
        GameController game = new GameController(new SaveManager(tempDir));
        setupToActThree(game, collectLetter, collectPhoto, returnMedallion);
        return game;
    }

    private static void setupToActThree(GameController game, boolean collectLetter, boolean collectPhoto, boolean returnMedallion) {
        game.newGame(); drain(game); // c01

        if (collectLetter) {
            game.interact("carta"); drainToChoice(game);
            game.chooseStory("n002_op1"); drain(game); // c02 (coleta carta e memória 1)
        }

        game.interact("portas"); drain(game); game.chooseStory("n004_op1"); drain(game); // c03
        game.interact("livro"); drain(game); // c04
        game.interact("balcao"); drain(game); // c05
        game.chooseStory("n001_op2"); drain(game); // c06
        game.chooseStory("n001_op1"); drain(game); // c07 (desbloqueia corredor)

        game.travel("corredor_esquecidos", null);
        drainScene(game, "c08_corredor");
        drainScene(game, "c09_fantasma_aparicao");

        // Cena 10
        game.chooseStory("n001_op1"); drainScene(game, "c10_primeira_impressao");

        // Cena 11
        drainToChoice(game);
        if (collectPhoto) game.chooseStory("n002_op1");
        else game.chooseStory("n002_op2");
        drainScene(game, "c11_fotografia");

        // Cena 12
        drainToChoice(game);
        if (returnMedallion) game.chooseStory("n002_op1");
        else game.chooseStory("n002_op2");
        drain(game); // c12 conclui com explore

        // Retorna ao balcão e depois ao salão principal
        game.interact("balcao");
        game.interact("salao");
    }

    private static void completeActThree(GameController game, boolean readClause) {
        game.interact("passagem_arquivo");
        drainScene(game, "c13_passagem");
        drainToChoice(game); game.chooseStory("n006_op1"); drainScene(game, "c14_escadaria_oculta");
        drainScene(game, "c15_arquivo");
        drainScene(game, "c16_livro_natasha");
        drainScene(game, "c17_registros");
        drainToChoice(game);
        if (readClause) game.chooseStory("n002_op1");
        else game.chooseStory("n002_op2");
        drainScene(game, "c18_clausula");
        drainToChoice(game);
        game.chooseStory("n036_op1");
        drain(game);
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
            if (++steps > 200) throw new IllegalStateException("Loop infinito ao buscar escolha");
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

