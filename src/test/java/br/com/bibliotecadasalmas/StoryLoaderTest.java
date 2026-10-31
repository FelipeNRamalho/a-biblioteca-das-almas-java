package br.com.bibliotecadasalmas;

import br.com.bibliotecadasalmas.core.GameState;
import br.com.bibliotecadasalmas.narrative.ChoiceOption;
import br.com.bibliotecadasalmas.narrative.NarrativeEffect;
import br.com.bibliotecadasalmas.narrative.NarrativeNode;
import br.com.bibliotecadasalmas.narrative.Story;
import br.com.bibliotecadasalmas.narrative.StoryCondition;
import br.com.bibliotecadasalmas.narrative.StoryLoadException;
import br.com.bibliotecadasalmas.narrative.StoryLoader;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/** Valida o roteiro real e cópias inválidas, sem alterar o conteúdo oficial. */
public final class StoryLoaderTest {
    private static final StoryLoader LOADER = new StoryLoader();
    private static JsonObject original;
    private static int rejected;

    public static void main(String[] args) throws Exception {
        try (InputStream input = StoryLoaderTest.class.getResourceAsStream(StoryLoader.DEFAULT_RESOURCE)) {
            if (input == null) throw new AssertionError("Roteiro ausente do classpath de teste");
            original = JsonParser.parseString(new String(input.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
        }
        Story story = LOADER.loadDefault();
        completeChapter(story);
        typedChoicesAndEffects(story);
        secretConditions(story);
        immutableContent(story);
        sourcesAndEncoding();
        rejectInvalidStructure();
        rejectInvalidReferences();
        rejectInvalidRulesAndCycles();
        System.out.println("OK: StoryLoader validou 28 cenas e 620 nós, 32 combinações secretas e "
                + rejected + " amostras inválidas.");
    }

    private static void completeChapter(Story story) {
        check(story.scenes().size() == 28, "Carrega as três versões da cena 26");
        check(story.scenes().values().stream().mapToInt(s -> s.nodes().size()).sum() == 620, "Carrega todos os nós");
        check(story.metadata().chapterTitle().equals("O Nome que Não Deveria Existir"), "Preserva título UTF-8");
        check(story.entryScene().id().equals("c01_despertar"), "Resolve a cena inicial");
        Set<Class<?>> types = new HashSet<>();
        Set<String> locations = new HashSet<>();
        story.scenes().values().forEach(scene -> {
            locations.add(scene.location());
            scene.nodes().values().forEach(node -> types.add(node.getClass()));
        });
        check(types.size() == 7 && locations.size() == 6, "Representa sete tipos de nó e seis ambientes");
        NarrativeNode.Line line = (NarrativeNode.Line) story.entryScene().node("n002");
        check(line.text().equals("A pedra está fria. O ar carrega o cheiro de chuva antiga e papel úmido."),
                "Preserva o texto original e sua acentuação");
        check(((NarrativeNode.Cutscene) story.entryScene().node("n001")).asset().equals("natasha_despertando"),
                "Preserva o identificador lógico da cutscene");
        GameState state = story.newGameState();
        check(state.locationId().equals("entrada_biblioteca") && state.position().nodeId().equals("n001"),
                "Cria estado a partir dos dados de entrada");
        check(state.flags().isEmpty() && state.inventory().isEmpty(), "Carregar não executa efeitos");
        state.giveItem("carta_entrada");
        check(story.newGameState().inventory().isEmpty(), "Cada nova partida tem estado independente");
    }

    private static void typedChoicesAndEffects(Story story) {
        NarrativeNode.Choice letter = (NarrativeNode.Choice) story.scene("c02_carta").node("n002");
        ChoiceOption investigate = letter.options().getFirst();
        check(investigate.destination().equals(new ChoiceOption.Destination.Node("n004")), "Resolve destino local");
        check(investigate.requires().isEmpty(), "Requisito ausente não cria condição adicional");
        check(investigate.effects().getFirst().equals(new NarrativeEffect.GiveItem("carta_entrada")), "Tipa efeito de item");
        check(investigate.effects().get(1).equals(new NarrativeEffect.UnlockMemory("retorno")), "Tipa efeito de memória");
        NarrativeNode.Choice beforeChamber = (NarrativeNode.Choice) story.scene("c20_caminho_sem_retorno").node("n015");
        check(beforeChamber.options().get(1).destination().equals(
                new ChoiceOption.Destination.Exploration("arquivo_proibido", "c20_caminho_sem_retorno")),
                "Conserva ação e contexto declarado de retorno");
        NarrativeNode.Choice decision = (NarrativeNode.Choice) story.scene("c25_decisao").node("n004");
        check(decision.confirmation().orElseThrow().cancelLabel().equals("Voltar"), "Preserva confirmação e cancelamento");
        check(decision.options().getFirst().requires().isEmpty(), "requires null é incondicional");
        NarrativeNode.End ending = (NarrativeNode.End) story.scene("c26_final_reescrever").node("n052");
        check(ending.nextScene().isEmpty(), "Epílogo terminal não exige destino artificial");
    }

    private static void secretConditions(Story story) {
        StoryCondition metadata = story.metadata().secretEndingRequires();
        StoryCondition reveal = ((NarrativeNode.Branch) story.scene("c24_terceira_possibilidade").node("n001")).test();
        StoryCondition decision = ((NarrativeNode.Choice) story.scene("c25_decisao").node("n004"))
                .options().stream().filter(o -> o.id().equals("reescrever")).findFirst().orElseThrow().requires().orElseThrow();
        String[] flags = { "memoria_retorno", "memoria_escolha", "memoria_pacto", "fantasma_confia", "regra_reescrita_descoberta" };
        for (int mask = 0; mask < 32; mask++) {
            GameState state = story.newGameState();
            for (int bit = 0; bit < flags.length; bit++) state.setFlag(flags[bit], (mask & (1 << bit)) != 0);
            state.setValue("fantasma_primeira_reacao", "medo");
            state.setFlag("final_secreto_revelado", false);
            boolean expected = mask == 31;
            check(metadata.matches(state) == expected && reveal.matches(state) == expected
                            && decision.matches(state) == expected && state.canRewritePact() == expected,
                    "JSON e GameState concordam na combinação " + mask);
        }
        JsonObject reordered = original.deepCopy();
        JsonArray tests = reordered.getAsJsonObject("metadata").getAsJsonObject("special_conditions")
                .getAsJsonObject("ending_secret_requires").getAsJsonArray("all");
        JsonElement first = tests.remove(0);
        tests.add(first);
        load(reordered); // Conjunção equivalente não depende da ordem dos testes.
    }

    private static void immutableContent(Story story) {
        immutable(() -> story.scenes().clear());
        immutable(() -> story.entryScene().nodes().clear());
        immutable(() -> story.metadata().endingScenes().clear());
        NarrativeNode.Choice choice = (NarrativeNode.Choice) story.scene("c02_carta").node("n002");
        immutable(() -> choice.options().clear());
        immutable(() -> choice.options().getFirst().effects().clear());
        immutable(() -> ((StoryCondition.All) story.metadata().secretEndingRequires()).conditions().clear());
    }

    private static void sourcesAndEncoding() throws Exception {
        Path directory = Files.createTempDirectory("biblioteca-story-teste-");
        Path valid = directory.resolve("roteiro.json");
        Files.writeString(valid, "\uFEFF" + original, StandardCharsets.UTF_8);
        check(LOADER.load(valid).scenes().size() == 28, "Aceita UTF-8 com BOM em arquivo externo");
        Path malformed = directory.resolve("utf8-invalido.json");
        Files.write(malformed, new byte[] { (byte) 0xC3, (byte) 0x28 });
        failure(() -> LOADER.load(malformed), "utf8-invalido.json", "JSON inválido");
        failure(() -> LOADER.load(directory.resolve("ausente.json")), "ausente.json", "Falha ao ler");
        failure(() -> LOADER.loadResource("/story/ausente.json"), "/story/ausente.json", "classpath");
        try (StringReader reader = new StringReader(original.toString())) {
            LOADER.load(reader, "reader.json");
            check(reader.read() == -1, "Carregador não fecha o Reader do chamador");
        }
    }

    private static void rejectInvalidStructure() {
        failure(() -> LOADER.load(new StringReader("{\"metadata\":{\"id\":\"a\",\"id\":\"b\"}}"), "duplicado.json"),
                "duplicado.json", "metadata.id", "Chave duplicada");
        failure(() -> LOADER.load(new StringReader(original + " {}"), "sobrando.json"), "sobrando.json");
        failure(() -> LOADER.load(new StringReader("/* comentário */{}"), "sintaxe.json"), "sintaxe.json", "JSON inválido");
        failure(() -> LOADER.load(new StringReader("{\"metadata\":"), "incompleto.json"), "incompleto.json");
        reject(j -> j.addProperty("scenes", "não é array"), "scenes", "Array");
        reject(j -> j.addProperty("metadta", true), "metadta", "Campo desconhecido");
        reject(j -> j.getAsJsonObject("metadata").addProperty("scene_count", 27), "scene_count", "Contagem");
        reject(j -> j.getAsJsonObject("metadata").addProperty("cutscene_count", 11), "cutscene_count", "Contagem");
        reject(j -> j.getAsJsonObject("metadata").addProperty("scene_count", 28.5), "scene_count", "Inteiro");
        reject(j -> scene(j, "c01_despertar").addProperty("optional", "false"), "c01_despertar", "optional", "Booleano");
        reject(j -> node(j, "c01_despertar", "n001").addProperty("type", "video"), "c01_despertar", "n001", "type");
        reject(j -> node(j, "c01_despertar", "n002").add("expression", JsonNull.INSTANCE), "expression", "Identificador");
        reject(j -> scene(j, "c01_despertar").addProperty("replay_policy", "always"), "replay_policy");
        reject(j -> scene(j, "c01_despertar").getAsJsonArray("nodes").add(node(j, "c01_despertar", "n001").deepCopy()),
                "c01_despertar", "n001", "duplicado");
        reject(j -> j.getAsJsonArray("scenes").add(scene(j, "c01_despertar").deepCopy()), "c01_despertar", "duplicado");
        reject(j -> option(j, "c02_carta", "n002", 1).addProperty("id", "n002_op1"), "n002", "duplicado");
        reject(j -> node(j, "c02_carta", "n002").add("options", new JsonArray()), "options", "vazio");
        reject(j -> effect(j, "c02_carta", "n002", 0, 2).addProperty("value", "true"), "n002_op1", "Booleano");
        reject(j -> effect(j, "c02_carta", "n002", 0, 0).addProperty("type", "cast_spell"), "n002_op1", "Tipo de efeito");
    }

    private static void rejectInvalidReferences() {
        reject(j -> j.getAsJsonObject("metadata").addProperty("entry_scene", "c99_ausente"), "entry_scene", "c99_ausente");
        reject(j -> scene(j, "c01_despertar").addProperty("entry_node", "n999"), "c01_despertar", "entry_node", "n999");
        reject(j -> node(j, "c01_despertar", "n002").addProperty("next", "n999"), "n002", "next", "n999");
        reject(j -> node(j, "c01_despertar", "n012").addProperty("next_scene", "c99_ausente"), "n012", "next_scene");
        reject(j -> node(j, "c01_despertar", "n012").remove("next_scene"), "n012", "Encerramento");
        reject(j -> node(j, "c13_passagem", "n007").addProperty("then", "n999"), "n007", "then");
        reject(j -> node(j, "c13_passagem", "n007").addProperty("else", "n999"), "n007", "else");
        reject(j -> option(j, "c20_caminho_sem_retorno", "n015", 1).addProperty("resume_scene", "c99_ausente"),
                "revisitar", "resume_scene");
        reject(j -> option(j, "c20_caminho_sem_retorno", "n015", 1).addProperty("location", "local_inexistente"),
                "revisitar", "location");
        reject(j -> option(j, "c20_caminho_sem_retorno", "n015", 1).addProperty("action", "teleport"), "revisitar", "Ação desconhecida");
        reject(j -> option(j, "c02_carta", "n002", 0).remove("next"), "n002_op1", "exatamente uma saída");
        reject(j -> option(j, "c02_carta", "n002", 0).addProperty("next_scene", "c03_portas"), "n002_op1", "exatamente uma saída");
        reject(j -> node(j, "c13_passagem", "n007").getAsJsonObject("test").addProperty("flag", "memoria_retormo"),
                "n007", "memoria_retormo");
        reject(j -> effect(j, "c02_carta", "n002", 0, 1).addProperty("memory", "desconhecida"), "n002_op1", "memory");
        reject(j -> effect(j, "c12_medalhao", "n002", 0, 0).addProperty("item", "objeto_inexistente"), "n002_op1", "item");
        reject(j -> node(j, "c01_despertar", "n002").addProperty("speaker", "fantasama"), "n002", "speaker");
    }

    private static void rejectInvalidRulesAndCycles() {
        reject(j -> node(j, "c24_terceira_possibilidade", "n001").getAsJsonObject("test").add("all", new JsonArray()),
                "n001", "all", "vazio");
        reject(j -> node(j, "c13_passagem", "n007").getAsJsonObject("test").addProperty("any", true), "any", "Campo desconhecido");
        reject(j -> node(j, "c24_terceira_possibilidade", "n001").getAsJsonObject("test").getAsJsonArray("all").remove(0),
                "c24_terceira_possibilidade", "cinco condições");
        reject(j -> option(j, "c25_decisao", "n004", 2).add("requires", JsonNull.INSTANCE), "reescrever", "Reescrita sem requisitos");
        reject(j -> node(j, "c02_carta", "n003").addProperty("next", "n003"), "c02_carta", "n003", "Ciclo");
        reject(j -> {
            scene(j, "c01_despertar").addProperty("entry_node", "n012");
            scene(j, "c02_carta").addProperty("entry_node", "n017");
            node(j, "c02_carta", "n017").addProperty("next_scene", "c01_despertar");
        }, "Ciclo");
    }

    private static JsonObject scene(JsonObject root, String id) {
        for (JsonElement value : root.getAsJsonArray("scenes")) {
            JsonObject scene = value.getAsJsonObject();
            if (scene.get("id").getAsString().equals(id)) return scene;
        }
        throw new AssertionError("Cena de fixture ausente: " + id);
    }

    private static JsonObject node(JsonObject root, String sceneId, String id) {
        for (JsonElement value : scene(root, sceneId).getAsJsonArray("nodes")) {
            JsonObject node = value.getAsJsonObject();
            if (node.get("id").getAsString().equals(id)) return node;
        }
        throw new AssertionError("Nó de fixture ausente: " + id);
    }

    private static JsonObject option(JsonObject root, String sceneId, String nodeId, int index) {
        return node(root, sceneId, nodeId).getAsJsonArray("options").get(index).getAsJsonObject();
    }

    private static JsonObject effect(JsonObject root, String sceneId, String nodeId, int option, int index) {
        return option(root, sceneId, nodeId, option).getAsJsonArray("effects").get(index).getAsJsonObject();
    }

    private static Story load(JsonObject json) { return LOADER.load(new StringReader(json.toString()), "fixture.json"); }

    private static void reject(Consumer<JsonObject> change, String... fragments) {
        JsonObject fixture = original.deepCopy();
        change.accept(fixture);
        failure(() -> load(fixture), fragments);
    }

    private static void failure(Runnable action, String... fragments) {
        try {
            action.run();
        } catch (StoryLoadException error) {
            for (String fragment : fragments) check(error.getMessage().contains(fragment),
                    "Diagnóstico deve conter '" + fragment + "': " + error.getMessage());
            rejected++;
            return;
        }
        throw new AssertionError("Fixture inválida foi aceita");
    }

    private static void immutable(Runnable action) {
        try { action.run(); }
        catch (UnsupportedOperationException expected) { return; }
        throw new AssertionError("Conteúdo narrativo exposto é mutável");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
