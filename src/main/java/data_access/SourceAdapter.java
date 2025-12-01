package data_access;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import entity.Source;

/**
 * Adapter for serializing and deserializing Source objects to/from JSON.
 */
public class SourceAdapter implements JsonDeserializer<Source>, JsonSerializer<Source> {
    @Override
    public Source deserialize(JsonElement json, Type typeOfT,
                              JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            return new Source(json.getAsString());
        }
        throw new JsonParseException("expected a string for source");
    }

    @Override
    public JsonElement serialize(Source src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getName());
    }
}
