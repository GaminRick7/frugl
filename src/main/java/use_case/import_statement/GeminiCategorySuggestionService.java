package use_case.import_statement;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeminiCategorySuggestionService {
    private static final Logger LOGGER = Logger.getLogger(GeminiCategorySuggestionService.class.getName());

    private static final String GEMINI_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private final String apiKey;
    private final List<String> allowedCategories;

    public GeminiCategorySuggestionService(String apiKey, List<String> allowedCategories) {
        this.apiKey = apiKey;
        this.allowedCategories = allowedCategories;
        this.httpClient = new OkHttpClient.Builder()
                .callTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public String suggestCategory(String sourceName, double amount, LocalDate date) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (apiKey == null || apiKey.isBlank()) {
            LOGGER.warning("no GEMNIII API key");
            return null;
        }

        String prompt = buildPrompt(sourceName, amount, date);
        String requestBody = buildRequestBody(prompt);

        Request request = new Request.Builder()
                .url(GEMINI_ENDPOINT + "?key=" + apiKey)
                .post(RequestBody.create(requestBody, JSON))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                String bodySnippet;
                if (response.body() == null) {
                    bodySnippet = "";
                } else {
                    bodySnippet = response.body().string();
                }
                LOGGER.warning(response.code() + bodySnippet);
                return null;
            }

            String body = response.body().string();
            String rawPrediction = parsePrediction(body);
            if (rawPrediction == null) {
                LOGGER.info("no prediction");
                return null;
            }

            for (String category : allowedCategories) {
                if (category.equalsIgnoreCase(rawPrediction.trim())) {
                    LOGGER.fine("Gemini classified " + sourceName + " as " + category);
                    return category;
                }
            }
            LOGGER.info(rawPrediction + "is not avalid category");
            return null;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "request failed", e);
            return null;
        }
    }

    private String buildPrompt(String sourceName, double amount, LocalDate date) {
        return "You are a financial classification assistant. Pick exactly one category from this list: "
                + String.join(", ", allowedCategories)
                + ". Respond with only the category text and nothing else.\n"
                + "Transaction details:\n"
                + "Merchant: " + sourceName + "\n"
                + "Amount: " + String.format(Locale.US, "%.2f", amount) + "\n"
                + "Date: " + date + "\n";
    }

    private String buildRequestBody(String prompt) {
        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", prompt);

        JsonArray parts = new JsonArray();
        parts.add(textPart);

        JsonObject content = new JsonObject();
        content.add("parts", parts);

        JsonArray contents = new JsonArray();
        contents.add(content);

        JsonObject root = new JsonObject();
        root.add("contents", contents);
        return root.toString();
    }

    private String parsePrediction(String responseBody) {
        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
        JsonArray candidates = json.getAsJsonArray("candidates");
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
        JsonObject content = firstCandidate.getAsJsonObject("content");
        if (content == null) {
            return null;
        }

        JsonArray parts = content.getAsJsonArray("parts");
        if (parts == null || parts.isEmpty()) {
            return null;
        }

        JsonElement part = parts.get(0);
        if (!part.isJsonObject()) {
            return null;
        }

        JsonObject partObject = part.getAsJsonObject();
        JsonElement text = partObject.get("text");
        if (text == null) {
            return null;
        } else {
            return text.getAsString();
        }
    }
}

