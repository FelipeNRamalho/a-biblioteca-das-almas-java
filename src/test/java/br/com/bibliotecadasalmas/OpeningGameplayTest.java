package br.com.bibliotecadasalmas;

import br.com.bibliotecadasalmas.core.GameController;
import br.com.bibliotecadasalmas.core.GameMode;
import br.com.bibliotecadasalmas.core.GameState;
import br.com.bibliotecadasalmas.core.SaveManager;
import br.com.bibliotecadasalmas.narrative.ChoiceOption;
import br.com.bibliotecadasalmas.narrative.NarrativeEffect;
import br.com.bibliotecadasalmas.narrative.NarrativeNode;
import br.com.bibliotecadasalmas.narrative.NarrativeSession;
import br.com.bibliotecadasalmas.narrative.Story;
import br.com.bibliotecadasalmas.narrative.StoryCondition;
import br.com.bibliotecadasalmas.narrative.StoryLoader;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.imageio.ImageIO;

/** Seis rotas da abertura, retomada, compatibilidade e regras do executor, sem saves pessoais. */
public final class OpeningGameplayTest {
    public static void main(String[] args) throws Exception {
        for (boolean collect : List.of(false, true)) {
            for (int posture = 1; posture <= 3; posture++) openingRoute(collect, posture);
        }
        invalidAndLegacySaves();
        directDoorAndAssets();
        transactionalEffects();
        conditionsAndConfirmation();
        System.out.println("OK: abertura passou em 6 rotas, retomadas, save inválido/legado, efeitos atômicos e confirmação.");
    }

    private static void directDoorAndAssets() throws Exception {
        GameController game = new GameController(new SaveManager(Files.createTempDirectory("biblioteca-porta-direta-")));
        game.newGame(); drain(game);
        game.interact("portas"); drain(game); game.chooseStory("n004_op1"); drain(game);
        check(game.state().locationId().equals("salao_principal") && !game.state().hasMemory("retorno"),
                "Porta pode ser usada sem abrir a interação da carta");
        check(game.opening().speakers().get("rodion").name().equals("Uma voz")
                        && game.opening().speakers().get("rodion").portraits().isEmpty(),
                "Cena 04 não antecipa a apresentação de Rodion");
        for (String path : game.opening().speakers().get("natasha").portraits().values()) {
            try (var input = OpeningGameplayTest.class.getResourceAsStream("/assets/" + path)) {
                var image = ImageIO.read(input);
                check(image != null && image.getWidth() > 0 && image.getColorModel().hasAlpha(), "Retrato válido e com canal alfa: " + path);
            }
        }
    }

    private static void openingRoute(boolean collect, int posture) throws Exception {
        Path directory = Files.createTempDirectory("biblioteca-abertura-");
        GameController game = new GameController(new SaveManager(directory));
        game.newGame();
        check(game.mode() == GameMode.STORY && game.session().node() instanceof NarrativeNode.Cutscene, "Novo Jogo usa o JSON");
        game = reload(game, directory);
        drain(game);
        check(!game.session().active() && game.state().flag("prologo_concluido"), "Despertar libera a exploração");
        check(game.state().locationId().equals("entrada_biblioteca"), "Começa explorando a Entrada oficial");

        game.interact("carta"); drain(game);
        check(game.session().node() instanceof NarrativeNode.Choice, "Carta abre escolha");
        var before = game.state().position();
        game.interact("portas");
        check(game.state().position().equals(before), "Hotspot não atravessa escolha modal");
        game.chooseStory(collect ? "n002_op1" : "n002_op2");
        game = reload(game, directory);
        check(game.state().hasItem("carta_entrada") == collect && game.state().hasMemory("retorno") == collect,
                "Escolha e seus efeitos são retomados juntos");
        drain(game);

        game.openOverlay(GameController.Overlay.INVENTORY);
        before = game.state().position();
        game.interact("portas");
        check(game.state().position().equals(before), "Inventário bloqueia hotspots");
        game.closeOverlay();
        game.interact("portas"); drain(game);
        check(game.state().position().sceneId().equals("c03_portas"), "Porta aciona a cena 03");
        game.chooseStory("n004_op" + posture);
        drain(game);
        check(game.state().locationId().equals("salao_principal") && game.state().position().nodeId().equals("n005"),
                "Entrada no Salão pausa antes de investigar o livro");
        check(!game.session().active(), "Livro é investigado por clique");
        check(game.state().value("postura_entrada").orElseThrow().equals(
                List.of("decidida", "educada", "investigativa").get(posture - 1)), "Postura escolhida foi aplicada");
        game = reload(game, directory);

        game.interact("entrada");
        check(game.state().locationId().equals("entrada_biblioteca"), "Permite retornar à carta");
        if (!collect) {
            game.interact("carta"); drain(game); game.chooseStory("n002_op1"); drain(game);
        }
        check(game.state().inventory().size() == 1 && game.state().memories().size() == 1, "Carta e memória sem duplicatas");
        check(game.hotspots().stream().noneMatch(h -> h.id().equals("carta")), "Carta coletada não fica no chão");
        game.openOverlay(GameController.Overlay.MEMORIES);
        GameState journalState = game.state();
        check(game.opening().documents().stream().filter(d -> d.kind().equals("memory") && d.available(journalState)).count() == 1,
                "Diário revela somente a memória recuperada");
        game.closeOverlay();
        game = reload(game, directory);
        game.interact("portas");
        check(!game.session().active() && game.state().position().nodeId().equals("n005"), "Retorno não repete prólogo ou escolhas da porta");
        game.interact("livro"); drain(game);
        check(game.state().flag("livro_mutavel_visto") && game.state().isLocationUnlocked("balcao_rodion"), "Conclui a cena 04 pelo JSON");
        check(game.state().position().sceneId().equals("c04_livro_mutavel") && !game.state().flag("rodion_encontrado"),
                "Não executa a cena 05 ainda não integrada");
        game = reload(game, directory);
        check(!game.session().active() && game.hotspots().stream().noneMatch(h -> h.id().equals("livro")), "Continuar não repete o livro");
        game.interact("entrada");
        before = game.state().position();
        game.interact("carta");
        check(before.equals(game.state().position()) && game.state().inventory().size() == 1, "Revisita não concede carta novamente");
    }

    private static GameController reload(GameController previous, Path directory) {
        GameState state = previous.state().copy();
        boolean active = previous.session().active();
        GameController restored = new GameController(new SaveManager(directory));
        restored.continueGame();
        check(restored.notice().isEmpty() && restored.mode() == GameMode.STORY, "Save válido foi restaurado");
        check(restored.state().position().equals(state.position()) && restored.state().locationId().equals(state.locationId())
                        && restored.state().flags().equals(state.flags()) && restored.state().values().equals(state.values())
                        && restored.state().inventory().equals(state.inventory()) && restored.state().memories().equals(state.memories())
                        && restored.state().choices().equals(state.choices())
                        && restored.state().completedInteractions().equals(state.completedInteractions())
                        && restored.state().unlockedLocations().equals(state.unlockedLocations()) && restored.session().active() == active,
                "Retomada conserva todos os dados sem reaplicar efeitos");
        return restored;
    }

    private static void invalidAndLegacySaves() throws Exception {
        Path directory = Files.createTempDirectory("biblioteca-save-invalido-");
        GameController game = new GameController(new SaveManager(directory));
        game.newGame(); drain(game); game.interact("carta"); drain(game); game.chooseStory("n002_op1"); drain(game);
        Path file = directory.resolve("historia.json");
        String valid = Files.readString(file);
        var data = JsonParser.parseString(valid).getAsJsonObject();
        data.add("memories", new JsonArray());
        Files.writeString(file, data.toString());
        rejectedSave(directory);
        Files.writeString(file, "{"); rejectedSave(directory);
        data = JsonParser.parseString(valid).getAsJsonObject(); data.addProperty("formatVersion", 99);
        Files.writeString(file, data.toString()); rejectedSave(directory);
        data = JsonParser.parseString(valid).getAsJsonObject(); data.addProperty("node", "n999");
        Files.writeString(file, data.toString()); rejectedSave(directory);

        Path legacyDirectory = Files.createTempDirectory("biblioteca-save-legado-");
        SaveManager legacy = new SaveManager(legacyDirectory);
        legacy.writeProgress(GameMode.INTRO, 2);
        GameController old = new GameController(legacy);
        old.continueGame();
        check(old.mode() == GameMode.INTRO && old.introPage() == 2, "Mantém continuação do prólogo legado");
        old.nextIntroPage(); old.toggleBook();
        check(old.mode() == GameMode.EXPLORACAO && old.inspectingBook(), "Demonstração legada continua funcionando");

        Path unusable = Files.createTempFile("biblioteca-save-sem-diretorio-", ".tmp");
        GameController unsaved = new GameController(new SaveManager(unusable));
        unsaved.newGame();
        check(unsaved.mode() == GameMode.STORY && !unsaved.notice().isBlank(), "Falha de gravação é informada sem interromper partida");
        unsaved.advanceStory();
        check(unsaved.session().node() instanceof NarrativeNode.Line, "Pode continuar lendo após falha de gravação");
    }

    private static void rejectedSave(Path directory) throws Exception {
        String before = Files.readString(directory.resolve("historia.json"));
        GameController game = new GameController(new SaveManager(directory));
        game.continueGame();
        check(game.mode() == GameMode.MENU && !game.notice().isBlank() && game.state().inventory().isEmpty(),
                "Save inválido não publica estado parcial nem inicia outra partida");
        check(Files.readString(directory.resolve("historia.json")).equals(before), "Save inválido é preservado");
    }

    private static void transactionalEffects() {
        var invalid = new NarrativeNode.Choice("choice", "Teste", List.of(new ChoiceOption("take", "Pegar",
                new ChoiceOption.Destination.Node("finish"), Optional.empty(),
                List.of(new NarrativeEffect.GiveItem("carta_entrada"), new NarrativeEffect.UnlockMemory("inexistente")))), Optional.empty());
        var scene = new Story.Scene("scene", 1, "Teste", "prologo", "entrada_biblioteca", false, "story", "choice",
                Story.ReplayPolicy.ONCE, Map.of("choice", invalid, "finish", new NarrativeNode.End("finish", List.of(), Optional.empty())));
        var story = new Story(new Story.Metadata("teste", "Teste", "1", "pt-BR", "scene", 1, 0,
                new StoryCondition.Flag("teste", true), List.of("scene")), Map.of("scene", scene));
        GameState state = story.newGameState();
        NarrativeSession session = new NarrativeSession(story, state); session.start("scene");
        try { session.choose("take"); throw new AssertionError("Lote inválido foi aceito"); }
        catch (IllegalArgumentException expected) {
            check(state.inventory().isEmpty() && state.choices().isEmpty() && state.completedInteractions().isEmpty()
                    && state.position().nodeId().equals("choice"), "Efeito inválido não concede item, escolha ou avanço parcial");
        }
    }

    private static void conditionsAndConfirmation() {
        Story story = new StoryLoader().loadDefault();
        GameState state = story.newGameState();
        state.unlockLocation("camara_registros");
        NarrativeSession session = new NarrativeSession(story, state);
        session.start("c25_decisao"); drain(session);
        check(session.options().size() == 2, "Sem requisitos, somente queimar e preservar");
        session.choose("queimar");
        check(session.pendingChoice().isPresent() && state.choices().isEmpty(), "Pedir confirmação não aplica escolha");
        session.cancelConfirmation();
        check(state.position().sceneId().equals("c25_decisao") && state.choices().isEmpty(), "Cancelar não avança nem concede efeitos");
        state.unlockMemory("retorno"); state.unlockMemory("escolha"); state.unlockMemory("pacto");
        state.setFlag("fantasma_confia", true); state.setFlag("regra_reescrita_descoberta", true);
        check(session.options().size() == 3, "Requisitos liberam a terceira opção");
        session.choose("reescrever"); session.confirm();
        check(state.position().sceneId().equals("c26_final_reescrever") && !state.flag("capitulo_1_concluido"),
                "Confirmar inicia epílogo sem concluí-lo antecipadamente");
        drain(session);
        check(state.flag("capitulo_1_concluido") && state.flag("escriba_derrotado") && state.flag("almas_libertas"), "Executor respeita efeitos finais");
        GameState branchState = story.newGameState(); branchState.unlockLocation("camara_registros");
        NarrativeSession branch = new NarrativeSession(story, branchState); branch.start("c24_terceira_possibilidade");
        check(branchState.position().nodeId().equals("n028"), "Condição escolhe ramo sem terceira possibilidade");
        branchState.unlockMemory("retorno"); branchState.unlockMemory("escolha"); branchState.unlockMemory("pacto");
        branchState.setFlag("fantasma_confia", true); branchState.setFlag("regra_reescrita_descoberta", true);
        branch.start("c24_terceira_possibilidade"); drain(branch);
        check(branchState.flag("final_secreto_revelado"), "Condição, evento e junção executados automaticamente");
    }

    private static void drain(GameController game) {
        int steps = 0;
        while (game.session().active() && !(game.session().node() instanceof NarrativeNode.Choice)) {
            if (++steps > 200) throw new AssertionError("Fluxo não aguardou exploração");
            game.advanceStory();
        }
    }
    private static void drain(NarrativeSession session) {
        int steps = 0;
        while (session.active() && !(session.node() instanceof NarrativeNode.Choice)) {
            if (++steps > 200) throw new AssertionError("Fluxo automático não termina");
            session.advance();
        }
    }
    private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
