package br.com.bibliotecadasalmas.narrative;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** Lê o consolidado, cria modelos tipados e valida o fluxo sem executar efeitos. */
public final class StoryLoader {
    public static final String DEFAULT_RESOURCE = "/story/roteiro_completo.json";

    public Story loadDefault() { return loadResource(DEFAULT_RESOURCE); }

    public Story loadResource(String resource) {
        Objects.requireNonNull(resource, "resource");
        try (InputStream input = StoryLoader.class.getResourceAsStream(resource)) {
            if (input == null) throw new StoryLoadException(resource, "$", "Recurso não encontrado no classpath");
            try (Reader reader = utf8(input)) { return load(reader, resource); }
        } catch (IOException error) {
            throw new StoryLoadException(resource, "$", "Falha ao ler recurso", error);
        }
    }

    public Story load(Path path) {
        Objects.requireNonNull(path, "path");
        try (Reader reader = utf8(Files.newInputStream(path))) {
            return load(reader, path.toString());
        } catch (IOException error) {
            throw new StoryLoadException(path.toString(), "$", "Falha ao ler arquivo", error);
        }
    }

    /** O chamador mantém a responsabilidade de fechar o Reader fornecido. */
    public Story load(Reader reader, String source) {
        Objects.requireNonNull(reader, "reader");
        Objects.requireNonNull(source, "source");
        return new Decoder(source).decode(StrictJson.read(reader, source));
    }

    private static Reader utf8(InputStream stream) {
        return new InputStreamReader(stream, StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT));
    }

    private static final class Decoder {
        private final String source;

        Decoder(String source) { this.source = source; }

        Story decode(JsonElement element) {
            JsonObject root = object(element, "$", "metadata", "scenes");
            Story.Metadata metadata = metadata(root.get("metadata"));
            JsonArray scenes = array(root, "scenes", "$", true);
            Map<String, Story.Scene> indexed = new LinkedHashMap<>();
            for (int i = 0; i < scenes.size(); i++) {
                Story.Scene scene = scene(scenes.get(i), "scenes[" + i + "]");
                if (indexed.putIfAbsent(scene.id(), scene) != null) {
                    throw error("cena " + scene.id(), "id", "ID de cena duplicado");
                }
            }
            Story story = new Story(metadata, indexed);
            new StoryValidator(source).validate(story);
            return story;
        }

        private Story.Metadata metadata(JsonElement element) {
            String c = "metadata";
            JsonObject data = object(element, c, "id", "chapter_title", "version", "language", "entry_scene",
                    "scene_count", "cutscene_count", "special_conditions", "ending_scenes");
            JsonObject special = object(data.get("special_conditions"), c + ".special_conditions", "ending_secret_requires");
            StoryCondition secret = condition(special.get("ending_secret_requires"), c + ".special_conditions.ending_secret_requires");
            List<String> endings = new ArrayList<>();
            for (JsonElement ending : array(data, "ending_scenes", c, true)) {
                String id = identifier(ending, c + ".ending_scenes");
                if (endings.contains(id)) throw error(c, "ending_scenes", "Final repetido: " + id);
                endings.add(id);
            }
            return new Story.Metadata(id(data, "id", c), text(data, "chapter_title", c), text(data, "version", c),
                    text(data, "language", c), id(data, "entry_scene", c), integer(data, "scene_count", c, 1),
                    integer(data, "cutscene_count", c, 0), secret, endings);
        }

        private Story.Scene scene(JsonElement element, String context) {
            JsonObject data = object(element, context, "id", "number", "title", "act", "location", "optional",
                    "kind", "entry_node", "replay_policy", "nodes");
            String id = id(data, "id", context);
            String c = "cena " + id;
            String policy = id(data, "replay_policy", c);
            Story.ReplayPolicy replayPolicy = switch (policy) {
                case "once" -> Story.ReplayPolicy.ONCE;
                case "repeat_if_interactable" -> Story.ReplayPolicy.REPEAT_IF_INTERACTABLE;
                default -> throw error(c, "replay_policy", "Política desconhecida: " + policy);
            };
            String kind = id(data, "kind", c);
            if (!kind.equals("story")) throw error(c, "kind", "Classificação desconhecida: " + kind);
            Map<String, NarrativeNode> indexed = new LinkedHashMap<>();
            for (JsonElement nodeElement : array(data, "nodes", c, true)) {
                NarrativeNode node = node(nodeElement, c);
                if (indexed.putIfAbsent(node.id(), node) != null) {
                    throw error(c + " / nó " + node.id(), "id", "ID de nó duplicado");
                }
            }
            return new Story.Scene(id, integer(data, "number", c, 1), text(data, "title", c), id(data, "act", c),
                    id(data, "location", c), bool(data, "optional", c), kind, id(data, "entry_node", c), replayPolicy, indexed);
        }

        private NarrativeNode node(JsonElement element, String context) {
            JsonObject data = rawObject(element, context + " / nodes");
            String id = id(data, "id", context + " / nodes");
            String c = context + " / nó " + id;
            String type = id(data, "type", c);
            return switch (type) {
                case "line" -> {
                    fields(data, c, "id", "type", "speaker", "text", "expression", "next");
                    yield new NarrativeNode.Line(id, id(data, "speaker", c), text(data, "text", c),
                            optionalId(data, "expression", c), id(data, "next", c));
                }
                case "cutscene" -> {
                    fields(data, c, "id", "type", "asset", "next");
                    yield new NarrativeNode.Cutscene(id, id(data, "asset", c), id(data, "next", c));
                }
                case "choice" -> {
                    fields(data, c, "id", "type", "prompt", "options", "confirmation");
                    List<ChoiceOption> options = new ArrayList<>();
                    Set<String> ids = new java.util.HashSet<>();
                    for (JsonElement optionElement : array(data, "options", c, true)) {
                        ChoiceOption option = option(optionElement, c);
                        if (!ids.add(option.id())) throw error(c, "options", "ID de opção duplicado: " + option.id());
                        options.add(option);
                    }
                    Optional<NarrativeNode.Confirmation> confirmation = Optional.empty();
                    if (data.has("confirmation")) {
                        JsonObject dialog = object(data.get("confirmation"), c + ".confirmation",
                                "title", "message", "confirm_label", "cancel_label");
                        confirmation = Optional.of(new NarrativeNode.Confirmation(text(dialog, "title", c + ".confirmation"),
                                text(dialog, "message", c + ".confirmation"), text(dialog, "confirm_label", c + ".confirmation"),
                                text(dialog, "cancel_label", c + ".confirmation")));
                    }
                    yield new NarrativeNode.Choice(id, text(data, "prompt", c), options, confirmation);
                }
                case "condition" -> {
                    fields(data, c, "id", "type", "test", "then", "else");
                    yield new NarrativeNode.Branch(id, condition(data.get("test"), c + ".test"),
                            id(data, "then", c), id(data, "else", c));
                }
                case "event" -> {
                    fields(data, c, "id", "type", "effects", "next");
                    yield new NarrativeNode.Event(id, effects(data, c, true), id(data, "next", c));
                }
                case "join" -> {
                    fields(data, c, "id", "type", "next");
                    yield new NarrativeNode.Join(id, id(data, "next", c));
                }
                case "end" -> {
                    fields(data, c, "id", "type", "effects", "next_scene");
                    yield new NarrativeNode.End(id, effects(data, c, true), optionalId(data, "next_scene", c));
                }
                default -> throw error(c, "type", "Tipo de nó desconhecido: " + type);
            };
        }

        private ChoiceOption option(JsonElement element, String context) {
            JsonObject data = rawObject(element, context + ".options");
            String id = id(data, "id", context + ".options");
            String c = context + " / opção " + id;
            int exits = (data.has("next") ? 1 : 0) + (data.has("next_scene") ? 1 : 0) + (data.has("action") ? 1 : 0);
            if (exits != 1) throw error(c, "next/next_scene/action", "A opção deve ter exatamente uma saída");
            ChoiceOption.Destination destination;
            if (data.has("next")) {
                fields(data, c, "id", "text", "requires", "effects", "next");
                destination = new ChoiceOption.Destination.Node(id(data, "next", c));
            } else if (data.has("next_scene")) {
                fields(data, c, "id", "text", "requires", "effects", "next_scene");
                destination = new ChoiceOption.Destination.Scene(id(data, "next_scene", c));
            } else {
                fields(data, c, "id", "text", "requires", "effects", "action", "location", "resume_scene");
                if (!id(data, "action", c).equals("open_exploration")) {
                    throw error(c, "action", "Ação desconhecida");
                }
                destination = new ChoiceOption.Destination.Exploration(id(data, "location", c), id(data, "resume_scene", c));
            }
            Optional<StoryCondition> requires = Optional.empty();
            if (data.has("requires") && !data.get("requires").isJsonNull()) {
                requires = Optional.of(condition(data.get("requires"), c + ".requires"));
            }
            return new ChoiceOption(id, text(data, "text", c), destination, requires, effects(data, c, false));
        }

        private StoryCondition condition(JsonElement element, String c) {
            JsonObject data = rawObject(element, c);
            if (data.has("all")) {
                fields(data, c, "all");
                List<StoryCondition> conditions = new ArrayList<>();
                JsonArray all = array(data, "all", c, true);
                for (int i = 0; i < all.size(); i++) conditions.add(condition(all.get(i), c + ".all[" + i + "]"));
                return new StoryCondition.All(conditions);
            }
            fields(data, c, "flag", "equals");
            return new StoryCondition.Flag(id(data, "flag", c), bool(data, "equals", c));
        }

        private List<NarrativeEffect> effects(JsonObject owner, String context, boolean required) {
            if (!required && !owner.has("effects")) return List.of();
            List<NarrativeEffect> effects = new ArrayList<>();
            JsonArray elements = array(owner, "effects", context, false);
            for (int i = 0; i < elements.size(); i++) {
                String c = context + ".effects[" + i + "]";
                JsonObject data = rawObject(elements.get(i), c);
                String type = id(data, "type", c);
                effects.add(switch (type) {
                    case "set_flag" -> {
                        fields(data, c, "type", "key", "value");
                        yield new NarrativeEffect.SetFlag(id(data, "key", c), bool(data, "value", c));
                    }
                    case "set_value" -> {
                        fields(data, c, "type", "key", "value");
                        yield new NarrativeEffect.SetValue(id(data, "key", c), id(data, "value", c));
                    }
                    case "give_item" -> {
                        fields(data, c, "type", "item");
                        yield new NarrativeEffect.GiveItem(id(data, "item", c));
                    }
                    case "remove_item" -> {
                        fields(data, c, "type", "item");
                        yield new NarrativeEffect.RemoveItem(id(data, "item", c));
                    }
                    case "unlock_memory" -> {
                        fields(data, c, "type", "memory");
                        yield new NarrativeEffect.UnlockMemory(id(data, "memory", c));
                    }
                    case "unlock_location" -> {
                        fields(data, c, "type", "location");
                        yield new NarrativeEffect.UnlockLocation(id(data, "location", c));
                    }
                    default -> throw error(c, "type", "Tipo de efeito desconhecido: " + type);
                });
            }
            return effects;
        }

        private JsonObject rawObject(JsonElement value, String c) {
            if (value == null || !value.isJsonObject()) throw error(c, "objeto", "Objeto JSON obrigatório");
            return value.getAsJsonObject();
        }

        private JsonObject object(JsonElement value, String c, String... allowed) {
            JsonObject object = rawObject(value, c);
            fields(object, c, allowed);
            return object;
        }

        private void fields(JsonObject object, String c, String... allowed) {
            Set<String> accepted = Set.of(allowed);
            for (String key : object.keySet()) {
                if (!accepted.contains(key)) throw error(c, key, "Campo desconhecido");
            }
        }

        private JsonArray array(JsonObject object, String field, String c, boolean nonempty) {
            JsonElement value = object.get(field);
            if (value == null || !value.isJsonArray()) throw error(c, field, "Array obrigatório");
            JsonArray array = value.getAsJsonArray();
            if (nonempty && array.isEmpty()) throw error(c, field, "Array não pode estar vazio");
            return array;
        }

        private String text(JsonObject object, String field, String c) {
            JsonElement value = object.get(field);
            if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()
                    || value.getAsString().isBlank()) throw error(c, field, "String não vazia obrigatória");
            return value.getAsString();
        }

        private String identifier(JsonElement value, String c) {
            if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()
                    || value.getAsString().isBlank() || !value.getAsString().equals(value.getAsString().strip())) {
                throw error(c, "id", "Identificador não vazio e sem espaços nas extremidades obrigatório");
            }
            return value.getAsString();
        }

        private String id(JsonObject object, String field, String c) {
            return identifier(object.get(field), c + "." + field);
        }

        private Optional<String> optionalId(JsonObject object, String field, String c) {
            return object.has(field) ? Optional.of(id(object, field, c)) : Optional.empty();
        }

        private boolean bool(JsonObject object, String field, String c) {
            JsonElement value = object.get(field);
            if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isBoolean()) {
                throw error(c, field, "Booleano obrigatório");
            }
            return value.getAsBoolean();
        }

        private int integer(JsonObject object, String field, String c, int minimum) {
            JsonElement value = object.get(field);
            if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
                throw error(c, field, "Inteiro obrigatório");
            }
            try {
                int number = value.getAsBigDecimal().intValueExact();
                if (number < minimum) throw error(c, field, "Valor mínimo: " + minimum);
                return number;
            } catch (ArithmeticException error) {
                throw error(c, field, "Inteiro fora do intervalo permitido");
            }
        }

        private StoryLoadException error(String c, String field, String message) {
            return new StoryLoadException(source, c + " / campo " + field, message);
        }
    }
}
