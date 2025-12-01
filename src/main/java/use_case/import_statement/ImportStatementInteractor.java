package use_case.import_statement;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import entity.Category;
import entity.Source;
import entity.Transaction;

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

        if (inputData.getFilePath().isBlank()) {
            presenter.prepareFailView("blank file path");
            return;
        }

        final File file = new File(inputData.getFilePath());
        if (!file.exists()) {
            presenter.prepareFailView("file does not exist");
            return;
        }

        final JsonArray transactionsJsonArray;
        try {
            transactionsJsonArray = readArrayFromFile(inputData.getFilePath());
        } catch (Exception exception) {
            presenter.prepareFailView("file does not contain a JSON array");
            return;
        }

        if (transactionsJsonArray.isEmpty()) {
            presenter.prepareFailView("no transactions found");
            return;
        }

        final List<JsonObject> categorized = new ArrayList<>();
        final List<JsonObject> uncategorized = new ArrayList<>();

        try {
            separateTransactions(transactionsJsonArray, categorized, uncategorized);
        } catch (Exception exception) {
            presenter.prepareFailView("unsupported file");
            return;
        }

        try {
            addTransactions(categorized);
        } catch (Exception exception) {
            presenter.prepareFailView("unsupported file");
            return;
        }

        try {
            categorizeSources(uncategorized);
        } catch (Exception exception) {
            presenter.prepareFailView("could not categorize transactions");
            return;
        }

        try {
            addTransactions(uncategorized);
        } catch (Exception exception) {
            presenter.prepareFailView("unsupported file");
            return;
        }

        final YearMonth ym = extractYearMonth(transactionsJsonArray);
        presenter.prepareSuccessView(new ImportStatementOutputData(ym));

    }

    private JsonArray readArrayFromFile(String filePath) throws Exception {
        try (FileReader reader = new FileReader(filePath)) {
            try {
                final JsonElement element = JsonParser.parseReader(reader);
                return element.getAsJsonArray();
            } catch (Exception exception) {
                throw new Exception("File does not contain a JSON array");
            }

        }
    }

    private void separateTransactions(JsonArray array,
                                      List<JsonObject> categorized,
                                      List<JsonObject> uncategorized) throws Exception {

        for (JsonElement element : array) {

            final JsonObject tx = element.getAsJsonObject();

            final String sourceName = tx.get("source").getAsString();

            if (transactionsDataAccessObject.sourceExists(new Source(sourceName))) {
                categorized.add(tx);
            } else {
                uncategorized.add(tx);
            }
        }
    }

    private void categorizeSources(List<JsonObject> uncategorized) throws Exception {

        final Set<String> uniqueSourceNames = new HashSet<>();
        for (JsonObject tx : uncategorized) {
            final String sourceName = tx.get("source").getAsString();
            uniqueSourceNames.add(sourceName);
        }
        if (uniqueSourceNames.isEmpty()) {
            return;
        }
        final List<String> sourcesToCategorize = new ArrayList<>(uniqueSourceNames);

        final Map<String, Category> categorizedSources = geminiCategorizer.categorizeSources(sourcesToCategorize);

        for (String sourceName : sourcesToCategorize) {
            final Category category = categorizedSources.get(sourceName);

            if (category == null) {
                throw new Exception("Missing category for source: " + sourceName);
            }
        }
        for (String sourceName : sourcesToCategorize) {
            final Category category = categorizedSources.get(sourceName);

            transactionsDataAccessObject.addSourceCategory(new Source(sourceName), category);
        }

    }

    private void addTransactions(List<JsonObject> transactions) throws Exception {

        for (JsonObject tx : transactions) {
            if (!tx.has("source") || !tx.has("amount") || !tx.has("date")) {
                throw new Exception("Missing fields in transaction");
            }
        }
        for (JsonObject tx : transactions) {
            final String sourceName = tx.get("source").getAsString();
            final double amount = tx.get("amount").getAsDouble();
            final String dateString = tx.get("date").getAsString();
            final Transaction transaction = new Transaction(new Source(sourceName), amount,
                    LocalDate.parse(dateString));
            transactionsDataAccessObject.addTransaction(transaction);
        }
    }

    private YearMonth extractYearMonth(JsonArray array) {

        final JsonObject first = array.get(0).getAsJsonObject();
        final String dateString = first.get("date").getAsString();
        final LocalDate date = LocalDate.parse(dateString);
        return YearMonth.from(date);

    }

}

