package use_case.import_statement;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import entity.Category;
import entity.Source;
import entity.Transaction;

/**
 * The Import Bank Statement Interactor.
 */
public class ImportStatementInteractor implements ImportStatementInputBoundary {

    private static final String ERROR_UNSUPPORTED_FILE = "unsupported file";

    private static final String ERROR_CATEGORIZE_TRANSACTIONS = "could not categorize transactions";

    private static final String FIELD_SOURCE = "source";

    private static final String FIELD_DATE = "date";

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
        try {
            final JsonArray transactionsJsonArray = loadTransactions(inputData);
            ensureTransactionsPresent(transactionsJsonArray);

            final List<JsonObject> categorized = new ArrayList<>();
            final List<JsonObject> uncategorized = new ArrayList<>();

            separateTransactions(transactionsJsonArray, categorized, uncategorized);
            addTransactions(categorized);
            processUncategorizedTransactions(uncategorized);

            final YearMonth yearMonth = extractYearMonth(transactionsJsonArray);
            presenter.prepareSuccessView(new ImportStatementOutputData(yearMonth));
        }
        catch (ImportStatementException exception) {
            presenter.prepareFailView(exception.getMessage());
        }
    }

    private JsonArray loadTransactions(ImportStatementInputData inputData) throws ImportStatementException {
        final String filePath = inputData.getFilePath();
        if (filePath == null || filePath.isBlank()) {
            throw new ImportStatementException("blank file path");
        }

        final File file = new File(filePath);
        if (!file.exists()) {
            throw new ImportStatementException("file does not exist");
        }

        try {
            return readArrayFromFile(file);
        }
        catch (IOException exception) {
            throw new ImportStatementException(ERROR_UNSUPPORTED_FILE, exception);
        }
        catch (JsonParseException | IllegalStateException exception) {
            throw new ImportStatementException("file does not contain a JSON array", exception);
        }
    }

    private void ensureTransactionsPresent(JsonArray transactionsJsonArray) throws ImportStatementException {
        if (transactionsJsonArray.isEmpty()) {
            throw new ImportStatementException("no transactions found");
        }
    }

    private void processUncategorizedTransactions(List<JsonObject> uncategorized)
            throws ImportStatementException {
        if (!uncategorized.isEmpty()) {
            categorizeSources(uncategorized);
            addTransactions(uncategorized);
        }
    }

    private JsonArray readArrayFromFile(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            final JsonElement element = JsonParser.parseReader(reader);
            return element.getAsJsonArray();
        }
    }

    private void separateTransactions(JsonArray array,
                                      List<JsonObject> categorized,
                                      List<JsonObject> uncategorized) throws ImportStatementException {

        try {
            for (JsonElement element : array) {
                final JsonObject tx = element.getAsJsonObject();
                if (!tx.has(FIELD_SOURCE)) {
                    throw new ImportStatementException(ERROR_UNSUPPORTED_FILE);
                }
                final String sourceName = tx.get(FIELD_SOURCE).getAsString();
                final Source source = new Source(sourceName);

                if (transactionsDataAccessObject.sourceExists(source)) {
                    categorized.add(tx);
                }
                else {
                    uncategorized.add(tx);
                }
            }
        }
        catch (ClassCastException | IllegalStateException exception) {
            throw new ImportStatementException(ERROR_UNSUPPORTED_FILE, exception);
        }
    }

    private void categorizeSources(List<JsonObject> uncategorized) throws ImportStatementException {
        final Set<String> uniqueSourceNames = new HashSet<>();
        for (JsonObject tx : uncategorized) {
            if (!tx.has(FIELD_SOURCE)) {
                throw new ImportStatementException(ERROR_CATEGORIZE_TRANSACTIONS);
            }
            uniqueSourceNames.add(tx.get(FIELD_SOURCE).getAsString());
        }
        if (!uniqueSourceNames.isEmpty()) {
            final List<String> sourcesToCategorize = new ArrayList<>(uniqueSourceNames);
            final Map<String, Category> categorizedSources;

            try {
                categorizedSources = geminiCategorizer.categorizeSources(sourcesToCategorize);
            }
            catch (IOException | GeminiCategorizerException exception) {
                throw new ImportStatementException(ERROR_CATEGORIZE_TRANSACTIONS, exception);
            }
            catch (RuntimeException exception) {
                throw new ImportStatementException(ERROR_CATEGORIZE_TRANSACTIONS, exception);
            }

            for (String sourceName : sourcesToCategorize) {
                if (categorizedSources.get(sourceName) == null) {
                    throw new ImportStatementException(ERROR_CATEGORIZE_TRANSACTIONS);
                }
            }

            try {
                for (String sourceName : sourcesToCategorize) {
                    final Category category = categorizedSources.get(sourceName);
                    transactionsDataAccessObject.addSourceCategory(new Source(sourceName), category);
                }
            }
            catch (RuntimeException exception) {
                throw new ImportStatementException(ERROR_CATEGORIZE_TRANSACTIONS, exception);
            }
        }
    }

    private void addTransactions(List<JsonObject> transactions) throws ImportStatementException {

        for (JsonObject tx : transactions) {
            if (!tx.has(FIELD_SOURCE) || !tx.has("amount") || !tx.has(FIELD_DATE)) {
                throw new ImportStatementException(ERROR_UNSUPPORTED_FILE);
            }
        }
        try {
            for (JsonObject tx : transactions) {
                final Source source = new Source(tx.get(FIELD_SOURCE).getAsString());
                final double amount = tx.get("amount").getAsDouble();
                final String dateString = tx.get(FIELD_DATE).getAsString();
                final Transaction transaction = new Transaction(source, amount,
                        LocalDate.parse(dateString));
                transactionsDataAccessObject.addTransaction(transaction);
            }
        }
        catch (RuntimeException exception) {
            throw new ImportStatementException(ERROR_UNSUPPORTED_FILE, exception);
        }
    }

    private YearMonth extractYearMonth(JsonArray array) {

        final JsonObject first = array.get(0).getAsJsonObject();
        final String dateString = first.get(FIELD_DATE).getAsString();
        final LocalDate date = LocalDate.parse(dateString);
        return YearMonth.from(date);

    }

    private static final class ImportStatementException extends Exception {

        ImportStatementException(String message) {
            super(message);
        }

        ImportStatementException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

