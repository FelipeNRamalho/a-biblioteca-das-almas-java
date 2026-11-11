package br.com.bibliotecadasalmas;

import br.com.bibliotecadasalmas.core.GameController;
import br.com.bibliotecadasalmas.core.GameMode;
import br.com.bibliotecadasalmas.core.SaveManager;
import br.com.bibliotecadasalmas.narrative.NarrativeNode;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Testes de integração do Ato IV e Epílogos: Câmara dos Registros e os Três Finais (cenas 20–26).
 * Cobre:
 * - R01: Final I - A Última Página (Queimar o Livro) sem investigação completa
 * - R02: Final II - Entre Páginas Fechadas (Preservar o Livro) sem investigação completa
 * - R03: Final III - A História que Ainda Não Foi Escrita (Reescrever o Pacto) com os 5 requisitos
 * - R04 e S02: Recuperar pistas ignoradas via open_exploration na cena 20, retomada e persistência em revisita
 * - R08: Cancelamento e confirmação da decisão final na cena 25
 * - Cutscenes do Escriba e dos Três Finais
 * - Persistência narrativa (save/load) durante o confronto, na confirmação e no epílogo.
 */
public final class ActFourGameplayTest {
    public static void main(String[] args) throws Exception {
        testFinalOneBurnBook();
        testFinalTwoPreserveBook();
        testFinalThreeRewritePact();
        testDelayedInvestigationAndRevisitResumption();
        testFinalDecisionConfirmationAndCancel();
        testActFourSaveAndRestore();
        System.out.println("OK: Ato IV (Câmara dos Registros e os Três Finais) passou em R01, R02, R03, R04, S02, R08, cutscenes e persistência.");
    }

    /**
     * R01: Final I - A Última Página.
     * Partida sem investigação completa (adiou carta, fotografia e cláusula, guardou medalhão).
     * Natasha avança até a Câmara, escolhe queimar o próprio livro e liberta as almas ao custo de desaparecer.
     */
    private static void testFinalOneBurnBook() throws Exception {
        Path tempDir = Files.createTempDirectory("biblioteca-actfour-final1-");
        GameController game = new GameController(new SaveManager(tempDir));
        setupToActFour(game, false, false, false, false);

        // Seleciona entrar na Câmara
        game.chooseStory("entrar_camara");
        check(game.state().flag("confronto_iniciado"), "Flag confronto_iniciado definida");
        check(game.session().scene().id().equals("c21_escriba_aparicao"), "Avançou para cena 21 (Câmara)");
        check(game.state().locationId().equals("camara_registros"), "Localização atualizada para camara_registros");

        // Cena 21: Primeira aparição do Escriba com cutscene
        check(game.session().node() instanceof NarrativeNode.Cutscene cs && cs.asset().equals("escriba_primeira_aparicao"),
                "Cutscene escriba_primeira_aparicao exibida na cena 21");
        drainScene(game, "c21_escriba_aparicao");
        check(game.state().flag("escriba_encontrado"), "Flag escriba_encontrado definida");

        // Cena 22: O Preço do Esquecimento com cutscene
        check(game.session().scene().id().equals("c22_apagamento"), "Avançou para cena 22");
        drainToCutscene(game, "escriba_apagando_alma");
        check(game.session().node() instanceof NarrativeNode.Cutscene, "Cutscene escriba_apagando_alma exibida");
        drainScene(game, "c22_apagamento");
        check(game.state().flag("apagamento_testemunhado"), "Flag apagamento_testemunhado definida");

        // Cena 23: A Armadilha do Pacto com cutscene
        check(game.session().scene().id().equals("c23_armadilha_pacto"), "Avançou para cena 23");
        check(game.session().node() instanceof NarrativeNode.Cutscene cs && cs.asset().equals("escriba_confronto"),
                "Cutscene escriba_confronto exibida na cena 23");
        drainScene(game, "c23_armadilha_pacto");

        // Cena 24: A Terceira Possibilidade
        // Como a investigação não foi completa, o ramo secreto não é ativado
        check(game.session().scene().id().equals("c24_terceira_possibilidade"), "Avançou para cena 24");
        drainScene(game, "c24_terceira_possibilidade");
        check(!game.state().flag("final_secreto_revelado"), "final_secreto_revelado permanece falso sem os 5 requisitos");

        // Cena 25: A Decisão
        check(game.session().scene().id().equals("c25_decisao"), "Avançou para cena 25");
        drainToChoice(game);
        check(game.session().node() instanceof NarrativeNode.Choice, "Na escolha da cena 25");

        // Apenas 'queimar' e 'preservar' estão disponíveis; 'reescrever' está oculta
        var opts = game.session().options();
        check(opts.size() == 2, "Apenas 2 opções disponíveis na cena 25 sem o segredo");
        check(opts.stream().anyMatch(o -> o.id().equals("queimar")), "Opção 'queimar' disponível");
        check(opts.stream().anyMatch(o -> o.id().equals("preservar")), "Opção 'preservar' disponível");
        check(opts.stream().noneMatch(o -> o.id().equals("reescrever")), "Opção 'reescrever' oculta");

        // Seleciona queimar
        game.chooseStory("queimar");
        check(game.session().pendingChoice().isPresent() && game.session().pendingChoice().get().equals("queimar"),
                "Confirmação pendente para 'queimar'");

        // Confirma
        game.confirmStoryChoice();
        check(game.session().scene().id().equals("c26_final_queimar"), "Iniciou epílogo Final I");

        // Percorre o epílogo com cutscene
        drainToCutscene(game, "final_queimar");
        check(game.session().node() instanceof NarrativeNode.Cutscene, "Cutscene final_queimar exibida");
        drain(game);

        // Verifica estado final de R01
        check(game.state().flag("capitulo_1_concluido"), "capitulo_1_concluido é verdadeiro no Final I");
        check(game.state().flag("almas_libertas"), "almas_libertas é verdadeiro no Final I");
        check(!game.state().flag("escriba_derrotado"), "escriba_derrotado é falso no Final I");
        check(game.state().value("final_conquistado").orElse("").equals("queimar"), "final_conquistado é 'queimar'");
    }

    /**
     * R02: Final II - Entre Páginas Fechadas.
     * Partida sem investigação completa. Seleciona preservar o livro.
     * Natasha continua existindo, o pacto permanece e as almas continuam presas.
     */
    private static void testFinalTwoPreserveBook() throws Exception {
        Path tempDir = Files.createTempDirectory("biblioteca-actfour-final2-");
        GameController game = new GameController(new SaveManager(tempDir));
        setupToActFour(game, false, false, false, false);

        game.chooseStory("entrar_camara");
        drainScene(game, "c21_escriba_aparicao");
        drainScene(game, "c22_apagamento");
        drainScene(game, "c23_armadilha_pacto");
        drainScene(game, "c24_terceira_possibilidade");

        drainToChoice(game);
        check(game.session().scene().id().equals("c25_decisao"), "Em c25_decisao");

        // Seleciona preservar e confirma
        game.chooseStory("preservar");
        check(game.session().pendingChoice().isPresent() && game.session().pendingChoice().get().equals("preservar"),
                "Confirmação pendente para 'preservar'");
        game.confirmStoryChoice();

        check(game.session().scene().id().equals("c26_final_preservar"), "Iniciou epílogo Final II");
        drainToCutscene(game, "final_preservar");
        check(game.session().node() instanceof NarrativeNode.Cutscene, "Cutscene final_preservar exibida");
        drain(game);

        // Verifica estado final de R02
        check(game.state().flag("capitulo_1_concluido"), "capitulo_1_concluido é verdadeiro no Final II");
        check(!game.state().flag("almas_libertas"), "almas_libertas permanece falso no Final II");
        check(!game.state().flag("escriba_derrotado"), "escriba_derrotado permanece falso no Final II");
        check(game.state().value("final_conquistado").orElse("").equals("preservar"), "final_conquistado é 'preservar'");
    }

    /**
     * R03: Final III - A História que Ainda Não Foi Escrita.
     * Todas as 5 condições são satisfeitas antes do confronto.
     * Na cena 24, a Fantasma intervém; na cena 25, 'reescrever' fica disponível.
     * O Escriba é derrotado e Natasha sobrevive com memórias incompletas.
     */
    private static void testFinalThreeRewritePact() throws Exception {
        Path tempDir = Files.createTempDirectory("biblioteca-actfour-final3-");
        GameController game = new GameController(new SaveManager(tempDir));
        // Todas as 5 condições satisfeitas
        setupToActFour(game, true, true, true, true);

        check(game.state().canRewritePact(), "Os 5 requisitos para reescrever estão presentes");

        game.chooseStory("entrar_camara");
        drainScene(game, "c21_escriba_aparicao");
        drainScene(game, "c22_apagamento");
        drainScene(game, "c23_armadilha_pacto");

        // Cena 24: os 5 requisitos ativam a terceira possibilidade
        check(game.session().scene().id().equals("c24_terceira_possibilidade"), "Avançou para cena 24");
        drainScene(game, "c24_terceira_possibilidade");
        check(game.state().flag("final_secreto_revelado"), "final_secreto_revelado é verdadeiro com os 5 requisitos");

        // Cena 25: A Decisão revela as três opções
        drainToChoice(game);
        check(game.session().scene().id().equals("c25_decisao"), "Em c25_decisao");
        var opts = game.session().options();
        check(opts.size() == 3, "Todas as 3 opções disponíveis na cena 25 com o segredo");
        check(opts.stream().anyMatch(o -> o.id().equals("reescrever")), "Opção 'reescrever' disponível");

        // Seleciona reescrever e confirma
        game.chooseStory("reescrever");
        check(game.session().pendingChoice().isPresent() && game.session().pendingChoice().get().equals("reescrever"),
                "Confirmação pendente para 'reescrever'");
        game.confirmStoryChoice();

        check(game.session().scene().id().equals("c26_final_reescrever"), "Iniciou epílogo Final III");

        // Cutscene do pacto reescrito
        drainToCutscene(game, "final_reescrever");
        check(game.session().node() instanceof NarrativeNode.Cutscene, "Cutscene final_reescrever exibida");

        // Cutscene da derrota do Escriba
        drainToCutscene(game, "escriba_derrota_final");
        check(game.session().node() instanceof NarrativeNode.Cutscene, "Cutscene escriba_derrota_final exibida");

        drain(game);

        // Verifica estado final de R03
        check(game.state().flag("capitulo_1_concluido"), "capitulo_1_concluido é verdadeiro no Final III");
        check(game.state().flag("almas_libertas"), "almas_libertas é verdadeiro no Final III");
        check(game.state().flag("escriba_derrotado"), "escriba_derrotado é verdadeiro no Final III");
        check(game.state().value("final_conquistado").orElse("").equals("reescrever"), "final_conquistado é 'reescrever'");
    }

    /**
     * R04 e S02: Recuperar pistas ignoradas via open_exploration e retomar na cena 20.
     * Partida onde a jogadora adiou carta, fotografia e cláusula, e guardou o medalhão.
     * Na cena 20, escolhe investigar a biblioteca.
     * Salva e recarrega no meio da exploração (S02).
     * Visita cada local, obtém as pistas que faltavam e devolve o medalhão.
     * Retorna ao Arquivo Proibido, interage com a passagem e retoma a escolha da cena 20.
     * Conclui com o Final III após a recuperação.
     */
    private static void testDelayedInvestigationAndRevisitResumption() throws Exception {
        Path tempDir = Files.createTempDirectory("biblioteca-actfour-revisit-");
        GameController game = new GameController(new SaveManager(tempDir));
        // Inicia com pistas adiadas e medalhão guardado
        setupToActFour(game, false, false, false, false);

        check(!game.state().canRewritePact(), "Inicialmente não atende aos requisitos de reescrita");
        check(game.session().scene().id().equals("c20_caminho_sem_retorno"), "Na cena 20");

        // Escolhe 'revisitar' (Investigar a biblioteca antes do confronto)
        game.chooseStory("revisitar");
        check(!game.session().active(), "Sessão narrativa suspensa para exploração aberta");
        check(game.state().locationId().equals("arquivo_proibido"), "Posicionada em arquivo_proibido para exploração");

        // S02: Salva e recarrega em modo exploração no ponto de revisita
        GameController reloaded = new GameController(new SaveManager(tempDir));
        reloaded.continueGame();
        check(reloaded.mode() == GameMode.STORY && !reloaded.session().active(), "Restaurado em modo exploração");
        check(reloaded.state().locationId().equals("arquivo_proibido"), "Local arquivo_proibido restaurado");

        // 1. Recupera a cláusula no Arquivo Proibido
        check(reloaded.hotspots().stream().anyMatch(h -> h.id().equals("clausula")), "Hotspot da cláusula visível");
        reloaded.interact("clausula");
        check(reloaded.session().scene().id().equals("c18_clausula"), "Iniciou investigação da cláusula");
        drainToChoice(reloaded);
        reloaded.chooseStory("n002_op1"); // Lê o fragmento
        drainScene(reloaded, "c18_clausula");
        check(reloaded.state().flag("regra_reescrita_descoberta"), "Cláusula recuperada!");
        check(!reloaded.session().active(), "Voltou à exploração após ler a cláusula");

        // 2. Viaja até o Corredor dos Esquecidos
        reloaded.interact("salao"); // Arquivo -> Salão
        check(reloaded.state().locationId().equals("salao_principal"), "No salão principal");
        reloaded.interact("balcao"); // Salão -> Balcão
        reloaded.interact("corredor"); // Balcão -> Corredor
        check(reloaded.state().locationId().equals("corredor_esquecidos"), "No corredor dos esquecidos");

        // 3. Recupera a fotografia (Memória 2)
        check(reloaded.hotspots().stream().anyMatch(h -> h.id().equals("fotografia")), "Hotspot da fotografia visível");
        reloaded.interact("fotografia");
        check(reloaded.session().scene().id().equals("c11_fotografia"), "Iniciou investigação da fotografia");
        drainToChoice(reloaded);
        reloaded.chooseStory("n002_op1"); // Examina o verso
        drainScene(reloaded, "c11_fotografia");
        check(reloaded.state().hasMemory("escolha"), "Memória 2 (escolha) recuperada!");

        // 4. Devolve o medalhão guardado à Fantasma (R05)
        check(reloaded.hotspots().stream().anyMatch(h -> h.id().equals("medalhao")), "Hotspot da Fantasma visível");
        reloaded.interact("medalhao");
        check(reloaded.session().scene().id().equals("c12_medalhao"), "Iniciou diálogo com a Fantasma");
        drainToChoice(reloaded);
        reloaded.chooseStory("n002_op1"); // Devolver o medalhão
        drain(reloaded);
        check(reloaded.state().flag("fantasma_confia"), "Confiança da Fantasma conquistada!");

        // 5. Viaja até a Entrada da Biblioteca
        reloaded.interact("balcao");
        reloaded.interact("salao");
        reloaded.interact("entrada");
        check(reloaded.state().locationId().equals("entrada_biblioteca"), "Na entrada da biblioteca");

        // 6. Recupera a carta da escadaria (Memória 1)
        check(reloaded.hotspots().stream().anyMatch(h -> h.id().equals("carta")), "Hotspot da carta visível");
        reloaded.interact("carta");
        check(reloaded.session().scene().id().equals("c02_carta"), "Iniciou investigação da carta");
        drainToChoice(reloaded);
        reloaded.chooseStory("n002_op1"); // Abrir e ler a carta
        drainScene(reloaded, "c02_carta");
        check(reloaded.state().hasMemory("retorno"), "Memória 1 (retorno) recuperada!");

        // Agora todas as 5 condições são verdadeiras!
        check(reloaded.state().canRewritePact(), "Todas as 5 condições foram completadas na revisita!");

        // 7. Retorna ao Arquivo Proibido
        reloaded.interact("portas");
        check(reloaded.state().locationId().equals("salao_principal"), "No salão principal");
        reloaded.interact("passagem_arquivo");
        check(reloaded.state().locationId().equals("arquivo_proibido"), "No arquivo proibido");

        // 8. Interage com a passagem para a Câmara dos Registros
        check(reloaded.hotspots().stream().anyMatch(h -> h.id().equals("porta_camara")), "Passagem para a Câmara visível");
        reloaded.interact("porta_camara");

        // Retoma a escolha da cena 20 exatamente no ponto suspenso!
        check(reloaded.session().scene().id().equals("c20_caminho_sem_retorno"), "Retomou cena 20");
        check(reloaded.session().node() instanceof NarrativeNode.Choice, "Retomou na escolha da cena 20");

        // Agora decide entrar na Câmara
        reloaded.chooseStory("entrar_camara");
        drainScene(reloaded, "c21_escriba_aparicao");
        drainScene(reloaded, "c22_apagamento");
        drainScene(reloaded, "c23_armadilha_pacto");
        drainScene(reloaded, "c24_terceira_possibilidade");

        check(reloaded.state().flag("final_secreto_revelado"), "O final secreto foi revelado após recuperar as pistas!");

        drainToChoice(reloaded);
        reloaded.chooseStory("reescrever");
        reloaded.confirmStoryChoice();
        drain(reloaded);

        check(reloaded.state().value("final_conquistado").orElse("").equals("reescrever"), "Final III concluído com sucesso!");
    }

    /**
     * R08: Cancelar a decisão final na cena 25.
     * Selecionar uma opção e cancelar mantém o jogador na escolha sem iniciar o epílogo.
     * Somente a opção confirmada inicia seu epílogo.
     */
    private static void testFinalDecisionConfirmationAndCancel() throws Exception {
        Path tempDir = Files.createTempDirectory("biblioteca-actfour-cancel-");
        GameController game = new GameController(new SaveManager(tempDir));
        setupToActFour(game, true, true, true, true);

        game.chooseStory("entrar_camara");
        drainScene(game, "c21_escriba_aparicao");
        drainScene(game, "c22_apagamento");
        drainScene(game, "c23_armadilha_pacto");
        drainScene(game, "c24_terceira_possibilidade");

        drainToChoice(game);
        check(game.session().scene().id().equals("c25_decisao"), "Em c25_decisao");

        // 1. Clica em 'queimar' -> pendente
        game.chooseStory("queimar");
        check(game.session().pendingChoice().isPresent() && game.session().pendingChoice().get().equals("queimar"),
                "Pendente de confirmação para 'queimar'");
        check(!game.state().flag("capitulo_1_concluido"), "Efeitos ainda não foram aplicados");

        // Cancela
        game.cancelStoryChoice();
        check(game.session().pendingChoice().isEmpty(), "Confirmação cancelada");
        check(game.session().scene().id().equals("c25_decisao"), "Permanece na cena 25");

        // 2. Clica em 'preservar' -> pendente
        game.chooseStory("preservar");
        check(game.session().pendingChoice().isPresent() && game.session().pendingChoice().get().equals("preservar"),
                "Pendente de confirmação para 'preservar'");

        // Cancela novamente
        game.cancelStoryChoice();
        check(game.session().pendingChoice().isEmpty(), "Confirmação cancelada");
        check(game.session().scene().id().equals("c25_decisao"), "Permanece na cena 25");

        // 3. Clica em 'reescrever' e confirma
        game.chooseStory("reescrever");
        check(game.session().pendingChoice().isPresent() && game.session().pendingChoice().get().equals("reescrever"),
                "Pendente de confirmação para 'reescrever'");
        game.confirmStoryChoice();

        check(game.session().scene().id().equals("c26_final_reescrever"), "Iniciou epílogo Final III após confirmação");
        drain(game);
        check(game.state().value("final_conquistado").orElse("").equals("reescrever"), "Final III concluído");
    }

    /**
     * Persistência narrativa em diferentes pontos do Ato IV:
     * - Durante o confronto em c21
     * - Na tela de confirmação da decisão final
     * - Durante o epílogo
     */
    private static void testActFourSaveAndRestore() throws Exception {
        Path tempDir = Files.createTempDirectory("biblioteca-actfour-persist-");
        GameController game = new GameController(new SaveManager(tempDir));
        setupToActFour(game, true, true, true, true);

        game.chooseStory("entrar_camara");

        // Avança até o diálogo inicial de c21
        check(game.session().scene().id().equals("c21_escriba_aparicao"), "Em c21");
        game.advanceStory(); // Avança cutscene

        // 1. Save/Load durante o diálogo em c21
        GameController reloaded1 = new GameController(new SaveManager(tempDir));
        reloaded1.continueGame();
        check(reloaded1.session().scene().id().equals("c21_escriba_aparicao"), "Restaurou exatamente em c21");
        check(reloaded1.state().locationId().equals("camara_registros"), "Localização camara_registros restaurada");

        drainScene(reloaded1, "c21_escriba_aparicao");
        drainScene(reloaded1, "c22_apagamento");
        drainScene(reloaded1, "c23_armadilha_pacto");
        drainScene(reloaded1, "c24_terceira_possibilidade");
        drainToChoice(reloaded1);

        // 2. Save/Load com confirmação pendente na cena 25
        reloaded1.chooseStory("reescrever");
        check(reloaded1.session().pendingChoice().isPresent(), "Confirmação pendente antes do save");

        GameController reloaded2 = new GameController(new SaveManager(tempDir));
        reloaded2.continueGame();
        check(reloaded2.session().scene().id().equals("c25_decisao"), "Restaurou em c25_decisao");
        check(reloaded2.session().pendingChoice().isPresent() && reloaded2.session().pendingChoice().get().equals("reescrever"),
                "Restaurou confirmação pendente para 'reescrever'");

        // Confirma após o reload
        reloaded2.confirmStoryChoice();
        check(reloaded2.session().scene().id().equals("c26_final_reescrever"), "Iniciou epílogo Final III após reload");

        // Avança algumas falas no epílogo
        reloaded2.advanceStory();
        reloaded2.advanceStory();

        // 3. Save/Load durante o epílogo
        GameController reloaded3 = new GameController(new SaveManager(tempDir));
        reloaded3.continueGame();
        check(reloaded3.session().scene().id().equals("c26_final_reescrever"), "Restaurou durante o epílogo");

        drain(reloaded3);
        check(reloaded3.state().flag("capitulo_1_concluido"), "Capítulo concluído com sucesso após reloads sucessivos");
    }

    private static void setupToActFour(GameController game, boolean collectLetter, boolean collectPhoto, boolean returnMedallion, boolean readClause) {
        game.newGame(); drain(game); // c01

        if (collectLetter) {
            game.interact("carta"); drainToChoice(game);
            game.chooseStory("n002_op1"); drain(game); // c02
        }

        game.interact("portas"); drain(game); game.chooseStory("n004_op1"); drain(game); // c03
        game.interact("livro"); drain(game); // c04
        game.interact("balcao"); drain(game); // c05
        game.chooseStory("n001_op2"); drain(game); // c06
        game.chooseStory("n001_op1"); drain(game); // c07

        game.travel("corredor_esquecidos", null);
        drainScene(game, "c08_corredor");
        drainScene(game, "c09_fantasma_aparicao");

        game.chooseStory("n001_op1"); drainScene(game, "c10_primeira_impressao");

        drainToChoice(game);
        if (collectPhoto) game.chooseStory("n002_op1");
        else game.chooseStory("n002_op2");
        drainScene(game, "c11_fotografia");

        drainToChoice(game);
        if (returnMedallion) game.chooseStory("n002_op1");
        else game.chooseStory("n002_op2");
        drain(game);

        game.interact("balcao");
        game.interact("salao");

        // Ato III
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
        game.chooseStory("n036_op1"); // c19_confissao_rodion
        drain(game); // Conclui cena 19, saindo para exploração em balcao_rodion

        // Natasha caminha do Balcão ao Salão e à Passagem do Arquivo Proibido
        game.interact("salao");
        game.interact("passagem_arquivo");
        check(game.state().locationId().equals("arquivo_proibido"), "No arquivo proibido");

        // Entra no caminho sem retorno
        game.interact("porta_camara");
        check(game.session().scene().id().equals("c20_caminho_sem_retorno"), "Iniciou c20_caminho_sem_retorno");
        drainToChoice(game);
        check(game.session().node() instanceof NarrativeNode.Choice, "Na escolha da cena 20");
    }

    private static void drainScene(GameController game, String sceneId) {
        int steps = 0;
        while (game.session().active() && game.session().scene().id().equals(sceneId)) {
            if (++steps > 300) throw new IllegalStateException("Loop infinito ao drenar cena: " + sceneId);
            game.advanceStory();
        }
    }

    private static void drainToChoice(GameController game) {
        int steps = 0;
        while (game.session().active() && !(game.session().node() instanceof NarrativeNode.Choice)) {
            if (++steps > 300) throw new IllegalStateException("Loop infinito ao buscar escolha");
            game.advanceStory();
        }
    }

    private static void drainToCutscene(GameController game, String asset) {
        int steps = 0;
        while (game.session().active()) {
            if (game.session().node() instanceof NarrativeNode.Cutscene cs && cs.asset().equals(asset)) return;
            if (++steps > 300) throw new IllegalStateException("Loop infinito ao buscar cutscene: " + asset);
            game.advanceStory();
        }
    }

    private static void drain(GameController game) {
        int steps = 0;
        while (game.session().active() && !(game.session().node() instanceof NarrativeNode.Choice)) {
            if (++steps > 300) throw new IllegalStateException("Loop infinito ao drenar narrativa");
            game.advanceStory();
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError("Falha no teste: " + message);
    }
}
