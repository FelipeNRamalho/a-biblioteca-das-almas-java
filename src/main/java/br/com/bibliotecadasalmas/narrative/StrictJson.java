package br.com.bibliotecadasalmas.narrative;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;

/** Usa o tokenizer do Gson e verifica duplicatas antes que a árvore possa sobrescrevê-las. */
final class StrictJson {
    private StrictJson() {}

    static JsonElement read(Reader input, String source) {
        JsonReader reader = new JsonReader(input);
        reader.setStrictness(Strictness.STRICT);
        try {
            JsonElement result = value(reader, source, 0);
            if (reader.peek() != JsonToken.END_DOCUMENT) {
                throw new StoryLoadException(source, reader.getPath(), "Conteúdo após o JSON principal");
            }
            return result;
        } catch (IOException | IllegalStateException | NumberFormatException error) {
            throw new StoryLoadException(source, reader.getPath(), "JSON inválido: " + error.getMessage(), error);
        }
    }

    private static JsonElement value(JsonReader reader, String source, int depth) throws IOException {
        if (depth > 128) throw new StoryLoadException(source, reader.getPath(), "JSON excessivamente aninhado");
        return switch (reader.peek()) {
            case BEGIN_OBJECT -> {
                JsonObject object = new JsonObject();
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (object.has(name)) {
                        throw new StoryLoadException(source, reader.getPath(), "Chave duplicada: " + name);
                    }
                    object.add(name, value(reader, source, depth + 1));
                }
                reader.endObject();
                yield object;
            }
            case BEGIN_ARRAY -> {
                JsonArray array = new JsonArray();
                reader.beginArray();
                while (reader.hasNext()) array.add(value(reader, source, depth + 1));
                reader.endArray();
                yield array;
            }
            case STRING -> new JsonPrimitive(reader.nextString());
            case NUMBER -> new JsonPrimitive(new BigDecimal(reader.nextString()));
            case BOOLEAN -> new JsonPrimitive(reader.nextBoolean());
            case NULL -> { reader.nextNull(); yield JsonNull.INSTANCE; }
            default -> throw new StoryLoadException(source, reader.getPath(), "Valor JSON esperado");
        };
    }
}
