package use_case.import_statement;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entity.Category;
import entity.Source;
import entity.Transaction;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/**
 * The Import Bank Statement Interactor.
 */
public class ImportStatementInteractor implements ImportStatementInputBoundary {

    private final ImportStatementDataAccessInterface transactionsDataAccessObject;
    private final ImportStatementOutputBoundary presenter;
    private final GeminiCategorizer geminiCategorizer;

    public ImportStatementInteractor(ImportStatementDataAccessInterface transactionsDataAccessObject,
                                     ImportStatementOutputBoundary presenter, GeminiCategorizer geminiCategorizer) {
        this.transactionsDataAccessObject = transactionsDataAccessObject;
        this.presenter = presenter;
        this.geminiCategorizer = geminiCategorizer;
    }


    @Override
    public void execute(ImportStatementInputData inputData) {

        if (inputData.getFilePath() == null || inputData.getFilePath().isBlank()) {
            presenter.prepareFailView("file does not exist");
            return;
        }

        File file = new File(inputData.getFilePath());
        if (!file.exists()){
           presenter.prepareFailView("file does not exist");
           return;
        }

        JsonArray transactionsJsonArray;
        try {
            transactionsJsonArray = readArrayFromFile(inputData.getFilePath());
        } catch (Exception e) {
            presenter.prepareFailView("unsupported file");
            return;
        }

        if (transactionsJsonArray.isEmpty()) {
            presenter.prepareFailView("no transactions found");
            return;
        }

        List<JsonObject> categorized = new ArrayList<>();
        List<JsonObject> uncategorized = new ArrayList<>();

        try {
            separateTransactions(transactionsJsonArray, categorized, uncategorized);
        } catch (Exception e) {
            presenter.prepareFailView("unsupported file");
            return;
        }

        try {
            addTransactions(categorized);
        } catch (Exception e) {
            presenter.prepareFailView("unsupported file");
            return;
        }

        try {
            categorizeSources(uncategorized);
        } catch (Exception e) {
            presenter.prepareFailView("could not categorize transactions");
            return;
        }

        try {
            addTransactions(uncategorized);
        } catch (Exception e) {
            presenter.prepareFailView("unsupported file");
            return;
        }


        YearMonth ym = extractYearMonth(transactionsJsonArray);
        presenter.prepareSuccessView(new ImportStatementOutputData(ym));

    }

    private JsonArray readArrayFromFile(String filePath) throws Exception {
        try (FileReader reader = new FileReader(filePath)) {

            JsonElement element = JsonParser.parseReader(reader);

            if (!element.isJsonArray()) {
                throw new Exception("File does not contain a JSON array");
            }

            return element.getAsJsonArray();
        }
    }

    private void separateTransactions(JsonArray array,
                                      List<JsonObject> categorized,
                                      List<JsonObject> uncategorized) throws Exception {

        for (JsonElement element : array) {

            if (!element.isJsonObject()) {
                throw new Exception("Array elements must be JSON objects");
            }

            JsonObject tx = element.getAsJsonObject();

            String sourceName = tx.get("source").getAsString();

            if (transactionsDataAccessObject.sourceExists(new Source(sourceName))) {
                categorized.add(tx);
            }
            else {
                uncategorized.add(tx);
            }
        }
    }

    private void categorizeSources(List<JsonObject> uncategorized) throws Exception {

        Set<String> uniqueSourceNames = new HashSet<>();
        for (JsonObject tx : uncategorized) {
            if (!tx.has("source")) {
                throw new Exception("Transaction missing 'source' field");
            }
            String sourceName = tx.get("source").getAsString();
            uniqueSourceNames.add(sourceName);
        }
        if (uniqueSourceNames.isEmpty()) {
            return;
        }
        List<String> sourcesToCategorize = new ArrayList<>(uniqueSourceNames);

        Map<String, Category> categorizedSources = geminiCategorizer.categorizeSources(sourcesToCategorize);

        for (String sourceName : sourcesToCategorize) {
            Category category = categorizedSources.get(sourceName);

            if (category == null) {
                throw new Exception("Missing category for source: " + sourceName);
            }
            transactionsDataAccessObject.addSourceCategory(new Source(sourceName), category);
        }

    }


        private void addTransactions(List<JsonObject> transactions) throws Exception {

        for (JsonObject tx : transactions) {
            if (!tx.has("source") || !tx.has("amount") || !tx.has("date")) {
                throw new Exception("Missing fields in transaction");
            }
            String sourceName = tx.get("source").getAsString();
            double amount = tx.get("amount").getAsDouble();
            String dateString = tx.get("date").getAsString();
            Transaction transaction = new Transaction(new Source(sourceName), amount, LocalDate.parse(dateString));
            transactionsDataAccessObject.addTransaction(transaction);
        }
    }

    private YearMonth extractYearMonth(JsonArray array) {

        JsonObject first = array.get(0).getAsJsonObject();
        String dateString = first.get("date").getAsString();
        LocalDate date = LocalDate.parse(dateString);
        return YearMonth.from(date);

    }

}
