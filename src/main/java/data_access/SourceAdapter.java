package data_access;

import com.google.gson.*;
import java.lang.reflect.Type;
import entity.Source;

public class SourceAdapter implements JsonDeserializer<Source>, JsonSerializer<Source> {
    @Override
    public Source deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            return new Source(json.getAsString());
        }
        throw new JsonParseException("expected a string for source");
    }
    @Override
    public JsonElement serialize(Source src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getName()); // write as string
    }
}
