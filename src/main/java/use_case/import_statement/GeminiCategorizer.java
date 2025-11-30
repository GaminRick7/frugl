package use_case.import_statement;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entity.Category;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Wrapper class to categorize transaction sources using Gemini API.
 */
public class GeminiCategorizer {
    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey;

    private final String apiUrl =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";
    private final Gson gson = new Gson();

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

        final Map<String, Category> finalResult = new HashMap<>();

        final int batchSize = 15;
        for (int i = 0; i < sources.size(); i += batchSize) {

            // Create sub-list for this batch
            final List<String> batch = sources.subList(i, Math.min(i + batchSize, sources.size()));

            // Build prompt just for this batch
            final String prompt = buildPrompt(batch);

            // Make API call
            final String jsonResponse = callGeminiApi(prompt);

            // Parse and merge result
            final Map<String, Category> batchResult = parseResponse(jsonResponse, batch);
            finalResult.putAll(batchResult);
        }

        return finalResult;
    }

    /**
     * Builds the classification prompt.
     *
     * @param sources the list of vendor names that need to be classified
     * @return a formatted prompt string instructing the model to classify each vendor
     * and return the results as a JSON object.
     * */
    private String buildPrompt(List<String> sources) {
        return "Classify each of the following vendor names into one of the categories:\n"
            + "- Income\n- Transportation\n- Rent and Utilities\n- Food and Dining\n- Shopping\n- Other\n\n"
            + "Return ONLY a JSON object mapping vendor→category. Example:\n"
            + "{ \"Uber\": \"Transportation\", \"McDonalds\": \"Food and Dining\" }\n\n"
            + "Vendors:\n"
            + gson.toJson(sources);
    }

    /**
     * Makes a HTTP POST request to Gemini using OkHttp.
     *
     * @param prompt the text prompt containing the vendor names and classification instructions
     * @return the raw JSON response body returned by the Gemini API as a String
     * @throws IOException if the request fails, the thread is interrupted, or the API returns an error response.
     */
    private String callGeminiApi(String prompt) throws IOException {

        try {
            final int sleepTime = 100;
            Thread.sleep(sleepTime);
        }
        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }

        final JsonObject contentObj = new JsonObject();
        final JsonArray partsArray = new JsonArray();
        final JsonObject textObj = new JsonObject();
        textObj.addProperty("text", prompt);
        partsArray.add(textObj);
        contentObj.add("parts", partsArray);
        final JsonArray contentsArray = new JsonArray();
        contentsArray.add(contentObj);

        final JsonObject requestJson = new JsonObject();
        requestJson.add("contents", contentsArray);

        final RequestBody body = RequestBody.create(
            requestJson.toString(),
            MediaType.parse("application/json")
        );

        final Request request = new Request.Builder()
            .url(apiUrl + apiKey)
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
     * Parses the JSON response returned by Gemini and extracts a mapping of vendor names
     * to their corresponding spending categories. The method validates that the Gemini
     * response contains valid JSON, properly formatted candidate and parts fields, and
     * that each original source is present in the parsed output.
     *
     * @param jsonResponse    the raw JSON response body returned by the Gemini API
     * @param originalSources the list of vendor names that were originally sent for classification
     * @return a map where each vendor name maps to a {@code Category} object representing
     * the category assigned by Gemini.
     * @throws Exception if the response is missing required fields, contains invalid JSON,
     * or does not provide a category for one of the original sources.
     */
    private Map<String, Category> parseResponse(String jsonResponse, List<String> originalSources) throws Exception {

        final JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();

        final JsonArray candidates = root.getAsJsonArray("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new Exception("Gemini returned no candidates");
        }

        final JsonObject content = candidates.get(0)
            .getAsJsonObject()
            .getAsJsonObject("content");

        final JsonArray parts = content.getAsJsonArray("parts");
        if (parts == null || parts.isEmpty()) {
            throw new Exception("Gemini returned no parts");
        }

        String modelText = parts.get(0)
            .getAsJsonObject()
            .get("text")
            .getAsString();

        modelText = stripMarkdownCodeFence(modelText);

        final JsonObject parsedCategories;
        try {
            parsedCategories = JsonParser.parseString(modelText).getAsJsonObject();
        }
        catch (Exception exception) {
            throw new Exception("Gemini response was not valid JSON: " + modelText);
        }

        final Map<String, Category> result = new HashMap<>();

        for (String source : originalSources) {
            if (!parsedCategories.has(source)) {
                throw new Exception("Missing category for: " + source);
            }

            final String categoryName = parsedCategories.get(source).getAsString();
            result.put(source, new Category(categoryName));
        }

        return result;
    }

    private String stripMarkdownCodeFence(String txt) {
        final String leading = "```";
        String text = txt;
        if (text.startsWith(leading)) {
            final int firstNewline = text.indexOf('\n');
            if (firstNewline != -1) {
                text = text.substring(firstNewline + 1);
            }
        }

        if (text.endsWith(leading)) {
            text = text.substring(0, text.lastIndexOf(leading)).trim();
        }

        return text.trim();
    }
}
