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
import java.util.ArrayList;
import java.util.List;

/**
 * The Import Bank Statement Interactor.
 */
public class ImportStatementInteractor implements ImportStatementInputBoundary {

    private final ImportStatementDataAccessInterface transactionsDataAccessObject;
    private final ImportStatementOutputBoundary presenter;
    private final GeminiCategorySuggestionService categorySuggestionService;

    public ImportStatementInteractor(ImportStatementDataAccessInterface transactionsDataAccessObject,
                                     ImportStatementOutputBoundary presenter,
                                     GeminiCategorySuggestionService categorySuggestionService) {
        this.transactionsDataAccessObject = transactionsDataAccessObject;
        this.presenter = presenter;
        this.categorySuggestionService = categorySuggestionService;
    }


    @Override
    public void execute(ImportStatementInputData inputData) {
        File file = new File(inputData.getFilePath());
        if (!file.exists()) {
            presenter.prepareFailView("Import unsuccessful: file does not exist");
            return;
        }

        JsonArray transactionsJsonArray;
        try {
            transactionsJsonArray = readArrayFromFile(inputData.getFilePath());
        } catch (Exception e) {
            presenter.prepareFailView("Import unsuccessful: unsupported file");
            return;
        }

        YearMonth statementMonth = extractYearMonth(transactionsJsonArray);
        List<JsonObject> categorized = new ArrayList<>();

        try {
            categorizeTransactions(transactionsJsonArray, categorized);
        } catch (Exception e) {
            presenter.prepareFailView("Import unsuccessful: unsupported file");
            return;
        }

        try {
            addTransactions(categorized);
        } catch (Exception e) {
            presenter.prepareFailView("Failed to save categorized transactions");
            return;
        }

        presenter.prepareSuccessView(new ImportStatementOutputData(statementMonth));
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

    private void categorizeTransactions(JsonArray array,
                                        List<JsonObject> categorized) throws Exception {

        for (JsonElement element : array) {

            if (!element.isJsonObject()) {
                throw new Exception("Array elements must be JSON objects");
            }

            JsonObject tx = element.getAsJsonObject();

            String sourceName = tx.get("source").getAsString();
            double amount = tx.get("amount").getAsDouble();
            LocalDate transactionDate = LocalDate.parse(tx.get("date").getAsString());
            Source source = new Source(sourceName);

            if (transactionsDataAccessObject.sourceExists(source)) {
                categorized.add(tx);
            } else {
                String suggestion = null;
                if (categorySuggestionService != null) {
                    suggestion = categorySuggestionService.suggestCategory(sourceName, amount, transactionDate);
                }

                String resolvedCategory;
                if (suggestion != null) {
                    resolvedCategory = suggestion;
                } else {
                    resolvedCategory = "Uncategorized";
                }
                transactionsDataAccessObject.addSourceCategory(source, new Category(resolvedCategory));
                categorized.add(tx);
            }
        }
    }

    private void addTransactions(List<JsonObject> transactions){

        for (JsonObject tx : transactions) {
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
        return YearMonth.parse(dateString.substring(0, 7));

    }

}
