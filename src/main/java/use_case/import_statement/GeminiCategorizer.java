package use_case.import_statement;

import com.google.gson.*;
import entity.Category;
import okhttp3.*;
import java.io.IOException;
import java.util.*;

/**
 * Wrapper class to categorize transaction sources using Gemini API.
 */
public class GeminiCategorizer {

    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey;

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";;

    private static final Gson gson = new Gson();

    public GeminiCategorizer(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Categorizes a list of source names using Gemini.
     *
     * @param sources list of vendor/source names
     * @return map of source → Category(name)
     * @throws Exception if API fails or response cannot be parsed
     */
    public Map<String, Category> categorizeSources(List<String> sources) throws Exception {

        String prompt = buildPrompt(sources);

        String jsonResponse = callGeminiApi(prompt);

        System.out.println(parseResponse(jsonResponse, sources));

        return parseResponse(jsonResponse, sources);

    }

    /**
     * Builds the classification prompt.
     */
    private String buildPrompt(List<String> sources) {
        return "Classify each of the following vendor names into one of the categories:\n" +
                "- Income\n- Transportation\n- Rent & Utilities\n- Food & Dining\n- Shopping\n- Other\n\n" +
                "Return ONLY a JSON object mapping vendor→category. Example:\n" +
                "{ \"Uber\": \"Transportation\", \"McDonalds\": \"Food & Dining\" }\n\n" +
                "Vendors:\n" +
                gson.toJson(sources);
    }

    /**
     * Makes a HTTP POST request to Gemini using OkHttp.
     */
    private String callGeminiApi(String prompt) throws Exception {

        JsonObject requestJson = new JsonObject();
        JsonArray contentsArray = new JsonArray();
        JsonObject contentObj = new JsonObject();
        JsonArray partsArray = new JsonArray();
        JsonObject textObj = new JsonObject();

        textObj.addProperty("text", prompt);
        partsArray.add(textObj);
        contentObj.add("parts", partsArray);
        contentsArray.add(contentObj);
        requestJson.add("contents", contentsArray);

        RequestBody body = RequestBody.create(
                requestJson.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(API_URL + apiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("Gemini API error: " + response.code());
            }

            return response.body().string();
        }
    }

    /**
     * Parses Gemini's JSON response into map of source → Category(name).
     */
    private Map<String, Category> parseResponse(String jsonResponse, List<String> originalSources) throws Exception {

        JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();

        JsonArray candidates = root.getAsJsonArray("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new Exception("Gemini returned no candidates");
        }

        JsonObject content = candidates.get(0)
                .getAsJsonObject()
                .getAsJsonObject("content");

        JsonArray parts = content.getAsJsonArray("parts");
        if (parts == null || parts.isEmpty()) {
            throw new Exception("Gemini returned no parts");
        }

        String modelText = parts.get(0)
                .getAsJsonObject()
                .get("text")
                .getAsString();

        modelText = stripMarkdownCodeFence(modelText);

        JsonObject parsedCategories;
        try {
            parsedCategories = JsonParser.parseString(modelText).getAsJsonObject();
        } catch (Exception e) {
            throw new Exception("Gemini response was not valid JSON: " + modelText);
        }

        Map<String, Category> result = new HashMap<>();

        for (String source : originalSources) {
            if (!parsedCategories.has(source)) {
                throw new Exception("Missing category for: " + source);
            }

            String categoryName = parsedCategories.get(source).getAsString();
            result.put(source, new Category(categoryName));
        }

        return result;
    }

    private String stripMarkdownCodeFence(String text) {
        // Remove leading ```json or ``` (any language tag)
        if (text.startsWith("```")) {
            int firstNewline = text.indexOf('\n');
            if (firstNewline != -1) {
                text = text.substring(firstNewline + 1);
            }
        }

        // Remove trailing ```
        if (text.endsWith("```")) {
            text = text.substring(0, text.lastIndexOf("```")).trim();
        }

        return text.trim();
    }
}
