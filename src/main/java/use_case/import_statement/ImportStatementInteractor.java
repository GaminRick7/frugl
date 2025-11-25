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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Import Bank Statement Interactor.
 */
public class ImportStatementInteractor implements ImportStatementInputBoundary {

    private final ImportStatementDataAccessInterface transactionsDataAccessObject;
    private final ImportStatementOutputBoundary presenter;

    public ImportStatementInteractor(ImportStatementDataAccessInterface transactionsDataAccessObject, ImportStatementOutputBoundary presenter) {
        this.transactionsDataAccessObject = transactionsDataAccessObject;
        this.presenter = presenter;
    }


    @Override
    public void execute(ImportStatementInputData inputData) {
       File file = new File(inputData.getFilePath());
       if (!file.exists()){
           presenter.prepareFailView("Import unsuccessful: file does not exist");
       }

       JsonArray transactionsJsonArray;
        try {
            transactionsJsonArray = readArrayFromFile(inputData.getFilePath());
        } catch (Exception e) {
            presenter.prepareFailView("Import unsuccessful: unsupported file");
            return;
        }

        List<JsonObject> categorized = new ArrayList<>();
        List<JsonObject> uncategorized = new ArrayList<>();

        try {
            separateTransactions(transactionsJsonArray, categorized, uncategorized);
        } catch (Exception e) {
            presenter.prepareFailView("Import unsuccessful: unsupported file");
            return;
        }

        try {
            addTransactions(categorized);
        } catch (Exception e) {
            presenter.prepareFailView("failed to save categorized transactions");
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

            String source = tx.get("source").getAsString();

            if (transactionsDataAccessObject.sourceExists(source)) {
                categorized.add(tx);
            }
            else {
                uncategorized.add(tx);
            }
        }
    }

    private void addTransactions(List<JsonObject> transactions){

        for (JsonObject tx : transactions) {
            String sourceName = tx.get("source").getAsString();
            double amount = tx.get("amount").getAsDouble();
            String dateString = tx.get("date").getAsString();
            Category sourceCategory = transactionsDataAccessObject.getSourceCategory(sourceName);
            Transaction transaction = new Transaction(new Source(sourceName, sourceCategory), amount, LocalDate.parse(dateString));
            transactionsDataAccessObject.addTransaction(transaction);
        }
    }

    private YearMonth extractYearMonth(JsonArray array) {

        JsonObject first = array.get(0).getAsJsonObject();
        String dateString = first.get("date").getAsString();
        return YearMonth.parse(dateString.substring(0, 7));

    }

}
