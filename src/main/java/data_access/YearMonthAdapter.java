package data_access;

import java.io.IOException;
import java.time.YearMonth;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Gson adapter that converts between YearMonth and its string form.
 * Used when serializing and deserializing LocalDate fields because GSON cannot directly parse YearMonth
 */
public class YearMonthAdapter extends TypeAdapter<YearMonth> {

    @Override
    public void write(JsonWriter out, YearMonth date) throws IOException {
        // Stored as a string the json file
        out.value(date.toString());
    }

    @Override
    public YearMonth read(JsonReader in) throws IOException {
        // Reads the string from json and converts back to a local date
        return YearMonth.parse(in.nextString());
    }
}
