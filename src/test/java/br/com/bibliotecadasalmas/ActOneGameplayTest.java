package br.com.bibliotecadasalmas;

import br.com.bibliotecadasalmas.characters.CharacterRegistry;
import br.com.bibliotecadasalmas.characters.Escriba;
import br.com.bibliotecadasalmas.characters.Fantasma;
import br.com.bibliotecadasalmas.characters.GameCharacter;
import br.com.bibliotecadasalmas.characters.Natasha;
import br.com.bibliotecadasalmas.characters.Rodion;
import br.com.bibliotecadasalmas.core.GameController;
import br.com.bibliotecadasalmas.core.GameMode;
import br.com.bibliotecadasalmas.core.GameState;
import br.com.bibliotecadasalmas.core.SaveManager;
import br.com.bibliotecadasalmas.narrative.NarrativeNode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Testes de integração do Ato I: Balcão de Rodion (cenas 05–07), escolhas e navegação. */
public final class ActOneGameplayTest {
    public static void main(String[] args) throws Exception {
        testCharacterHierarchy();
        for (int optC06 = 1; optC06 <= 3; optC06++) {
            for (int optC07 = 1; optC07 <= 3; optC07++) {
                testActOneFlow(optC06, optC07);
            }
        }
        testExplorationAndRevisit();
        testSaveAndRestoreActOne();
        System.out.println("OK: Ato I (Balcão de Rodion) passou em todas as 9 combinações de escolhas, navegação, retratos e persistência.");
    }

    private static void testCharacterHierarchy() {
        CharacterRegistry registry = new CharacterRegistry();
        GameCharacter natasha = registry.find("natasha").orElseThrow();
        GameCharacter rodion = registry.find("rodion").orElseThrow();
        GameCharacter fantasma = registry.find("fantasma").orElseThrow();
        GameCharacter escriba = registry.find("escriba").orElseThrow();

        check(natasha instanceof Natasha, "Natasha é subclasse de GameCharacter");
        check(rodion instanceof Rodion, "Rodion é subclasse de GameCharacter");
        check(fantasma instanceof Fantasma, "Fantasma é subclasse de GameCharacter");
        check(escriba instanceof Escriba, "Escriba é subclasse de GameCharacter");

        GameState dummy = new GameState("salao_principal", "c04_livro_mutavel", "n001");
        check(rodion.displayName(dummy, "c04_livro_mutavel").equals("Uma voz"),
                "Rodion aparece como 'Uma voz' na cena 04");
        check(rodion.portrait("neutro", dummy, "c04_livro_mutavel").isEmpty(),
                "Rodion não exibe retrato na cena 04");
        check(rodion.displayName(dummy, "c05_bibliotecario").equals("Rodion Raskólnikov"),
                "Rodion aparece como 'Rodion Raskólnikov' na cena 05");
        check(rodion.portrait("neutro", dummy, "c05_bibliotecario").isPresent(),
                "Rodion exibe retrato na cena 05");

        check(registry.displayName("natasha_memoria", dummy, "c02_carta").orElseThrow().equals("Natasha (Memória)"),
                "Alias natasha_memoria resolvido");
        check(registry.displayName("rodion_memoria", dummy, "c16_livro_natasha").orElseThrow().equals("Rodion (Memória)"),
                "Alias rodion_memoria resolvido");
        check(registry.displayName("voz_escriba", dummy, "c20_caminho_sem_retorno").orElseThrow().equals("Uma voz fria"),
                "Alias voz_escriba resolvido");
        check(registry.portrait("voz_escriba", "neutro", dummy, "c20_caminho_sem_retorno").isEmpty(),
                "Voz do Escriba não antecipa retrato");
    }

    private static void testActOneFlow(int choiceC06, int choiceC07) throws Exception {
        Path directory = Files.createTempDirectory("biblioteca-ato1-fluxo-");
        GameController game = new GameController(new SaveManager(directory));
        game.newGame(); drain(game);

        // Entrada -> Salão
        game.interact("portas"); drain(game); game.chooseStory("n004_op1"); drain(game);
        check(game.state().locationId().equals("salao_principal"), "Chega ao Salão Principal");

        // Cena 04: O Livro que Muda de Palavras
        game.interact("livro");
        boolean sawVoiceInC04 = false;
        while (game.session().active()) {
            if (game.session().node() instanceof NarrativeNode.Line line && line.speaker().equals("rodion")) {
                sawVoiceInC04 = true;
                check(game.speakerName(line).equals("Uma voz"), "Falas de Rodion na cena 04 identificadas como 'Uma voz'");
                check(game.portrait(line).isEmpty(), "Falas de Rodion na cena 04 não têm retrato");
            }
            game.advanceStory();
        }
        check(sawVoiceInC04, "Voz de Rodion ouvida na cena 04");
        check(game.state().flag("livro_mutavel_visto"), "Livro mutável visto");
        check(game.state().isLocationUnlocked("balcao_rodion"), "Balcão de Rodion desbloqueado");
        check(!game.state().flag("rodion_encontrado"), "Rodion ainda não encontrado formalmente");

        // Navega para o Balcão de Rodion
        game.interact("balcao");
        check(game.state().locationId().equals("balcao_rodion"), "Moveu-se para balcao_rodion");
        check(game.session().active(), "Cena 05 iniciada automaticamente ao entrar no balcão");
        check(game.session().node() instanceof NarrativeNode.Cutscene cutscene
                && cutscene.asset().equals("rodion_lendo_livro"), "Cena 05 abre com cutscene de Rodion lendo livro");

        // Avança cena 05
        boolean rodionNamedInC05 = false;
        while (game.session().active() && !(game.session().node() instanceof NarrativeNode.Choice)) {
            if (game.session().node() instanceof NarrativeNode.Line line && line.speaker().equals("rodion")) {
                check(game.speakerName(line).equals("Rodion Raskólnikov"), "Nome de Rodion revelado no balcão");
                check(game.portrait(line).isPresent(), "Retrato de Rodion visível no balcão");
                rodionNamedInC05 = true;
            }
            game.advanceStory();
        }
        check(rodionNamedInC05, "Rodion fala com nome e retrato na cena 05");

        // Cena 06: Um Nome que Não Foi Dito
        check(game.session().active() && game.session().node() instanceof NarrativeNode.Choice, "Cena 06 abre escolha");
        check(game.state().flag("rodion_encontrado"), "Flag rodion_encontrado aplicada no fim da cena 05");
        game.chooseStory("n001_op" + choiceC06);

        // Avança até a escolha da cena 07
        drain(game);
        check(game.session().active() && game.session().node() instanceof NarrativeNode.Choice, "Cena 07 abre escolha de investigação");
        game.chooseStory("n001_op" + choiceC07);

        // Conclui cena 07
        drain(game);
        check(!game.session().active(), "Cena 07 encerra e retorna à exploração do balcão");
        check(game.state().isLocationUnlocked("corredor_esquecidos"), "Corredor dos Livros Esquecidos desbloqueado ao fim do Ato I");
        check(game.state().locationId().equals("balcao_rodion"), "Natasha permanece no balcão após cena 07");
    }

    private static void testExplorationAndRevisit() throws Exception {
        Path directory = Files.createTempDirectory("biblioteca-ato1-revisita-");
        GameController game = new GameController(new SaveManager(directory));
        game.newGame(); drain(game);
        game.interact("portas"); drain(game); game.chooseStory("n004_op1"); drain(game);
        game.interact("livro"); drain(game);
        game.interact("balcao"); drain(game);
        game.chooseStory("n001_op2"); drain(game); // Conversar com calma
        game.chooseStory("n001_op3"); drain(game); // Não mexer no balcão

        check(!game.session().active(), "Explorando balcão");
        check(game.hotspots().stream().anyMatch(h -> h.id().equals("salao")), "Hotspot para voltar ao salão disponível");
        check(game.hotspots().stream().anyMatch(h -> h.id().equals("segredos_balcao")), "Hotspot para examinar balcão disponível");

        // Reinvestigar o balcão (cena 07 é opcional e repetível por interação)
        game.interact("segredos_balcao");
        check(game.session().active() && game.session().node() instanceof NarrativeNode.Choice, "Reinvestigação do balcão abre escolha");
        game.chooseStory("n001_op1"); // Agora examina o livro pessoal
        drain(game);
        check(!game.session().active(), "Reinvestigação concluída com sucesso");

        // Retorna ao Salão Principal
        game.interact("salao");
        check(game.state().locationId().equals("salao_principal"), "Retornou ao Salão Principal");
        check(game.hotspots().stream().anyMatch(h -> h.id().equals("balcao")), "Passagem de volta ao balcão disponível no salão");

        // Volta ao balcão
        game.interact("balcao");
        check(game.state().locationId().equals("balcao_rodion"), "Voltou ao Balcão de Rodion");
        check(!game.session().active(), "Não repete cena 05 ao entrar novamente no balcão");
    }

    private static void testSaveAndRestoreActOne() throws Exception {
        Path directory = Files.createTempDirectory("biblioteca-ato1-save-");
        GameController game = new GameController(new SaveManager(directory));
        game.newGame(); drain(game);
        game.interact("portas"); drain(game); game.chooseStory("n004_op1"); drain(game);
        game.interact("livro"); drain(game);
        game.interact("balcao"); drain(game);

        // Salvar na escolha da cena 06
        check(game.session().node() instanceof NarrativeNode.Choice, "No nó de escolha da cena 06");
        GameController restored = new GameController(new SaveManager(directory));
        restored.continueGame();
        check(restored.mode() == GameMode.STORY && restored.session().active(), "Save na cena 06 restaurado em modo STORY ativo");
        check(restored.state().flag("rodion_encontrado"), "Flag rodion_encontrado persistida no save");
        check(restored.state().locationId().equals("balcao_rodion"), "Localização balcao_rodion restaurada");
        check(restored.session().node() instanceof NarrativeNode.Choice, "Escolha da cena 06 mantida após reload");

        restored.chooseStory("n001_op1"); drain(restored);
        restored.chooseStory("n001_op2"); drain(restored);
        check(restored.state().isLocationUnlocked("corredor_esquecidos"), "Corredor desbloqueado");

        // Salvar após término do Ato I
        GameController finalRestored = new GameController(new SaveManager(directory));
        finalRestored.continueGame();
        check(!finalRestored.session().active(), "Sessão inativa ao retomar após cena 07");
        check(finalRestored.state().isLocationUnlocked("corredor_esquecidos"), "Desbloqueio do corredor mantido no save");
        check(finalRestored.state().isLocationUnlocked("balcao_rodion"), "Desbloqueio do balcão mantido no save");
    }

    private static void drain(GameController game) {
        int steps = 0;
        while (game.session().active() && !(game.session().node() instanceof NarrativeNode.Choice)) {
            if (++steps > 200) throw new AssertionError("Fluxo não aguardou exploração ou escolha");
            game.advanceStory();
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}

