package data_access;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Gson adapter that converts between LocalDate and its string form
 * Used when serializing and deserializing LocalDate fields because GSON cannot directly parse LocalDate
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    @Override
    public void write(JsonWriter out, LocalDate date) throws IOException {
        out.value(date.toString()); // Stored as a string the json file
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        return LocalDate.parse(in.nextString()); // Reads the string from json and converts back to a local date
    }
}
