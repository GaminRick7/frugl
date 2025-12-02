package data_access;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import entity.Category;
import entity.Source;
import entity.Transaction;
import use_case.autosave.AutosaveDataAccessInterface;
import use_case.import_statement.ImportStatementDataAccessInterface;
import use_case.load_dashboard.LoadDashboardDataAccessInterface;
import use_case.set_goal.ForestDataAccessInterface;
import use_case.view_transactions.ViewTransactionDataAccessInterface;

/**
 * Data Access Object for Transactions.
 */
public class TransactionDataAccessObject implements AutosaveDataAccessInterface,
        LoadDashboardDataAccessInterface, ImportStatementDataAccessInterface,
        ForestDataAccessInterface, ViewTransactionDataAccessInterface {
    private final Gson gson;

    private final File transactionsFile;
    private List<Transaction> transactions;

    private final File mappingFile;
    private Map<Source, Category> sourceToCategoryMap;

    public TransactionDataAccessObject(String transactionsFilePath, String mappingFilePath) {
        this.transactionsFile = new File(transactionsFilePath);
        this.mappingFile = new File(mappingFilePath);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Source.class, new SourceAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
        this.transactions = new ArrayList<>();
        this.sourceToCategoryMap = new HashMap<>();

        loadTransactions();
        loadSourceToCategoryMap();
    }

    public TransactionDataAccessObject() {
        this("transactions.json", "source_categories.json");
    }

    private void loadTransactions() {
        if (transactionsFile.exists() && transactionsFile.length() > 0) {
            try (FileReader reader = new FileReader(transactionsFile)) {
                final Type listType = new TypeToken<List<Transaction>>() {
                }.getType();
                this.transactions = gson.fromJson(reader, listType);
                if (this.transactions == null) {
                    this.transactions = new ArrayList<>();
                }
            }
            catch (IOException ex) {
                System.err.println("Error loading transactions from JSON: " + ex.getMessage());
                this.transactions = new ArrayList<>();
            }
        }
        else {
            this.transactions = new ArrayList<>();
        }
    }

    private void loadSourceToCategoryMap() {
        if (mappingFile.exists() && mappingFile.length() > 0) {
            try (FileReader reader = new FileReader(mappingFile)) {
                final Type stringMapType = new TypeToken<HashMap<String, String>>() {
                }.getType();
                final Map<String, String> stringMap = gson.fromJson(reader, stringMapType);

                this.sourceToCategoryMap = new HashMap<>();
                if (stringMap != null) {
                    for (String sourceName : stringMap.keySet()) {
                        final Source source = new Source(sourceName);
                        final Category category = new Category(stringMap.get(sourceName));
                        this.sourceToCategoryMap.put(source, category);
                    }
                }
            }
            catch (IOException ex) {
                System.err.println("Error loading source-category mappings: " + ex.getMessage());
                this.sourceToCategoryMap = new HashMap<>();
            }
        }
        else {
            this.sourceToCategoryMap = new HashMap<>();
        }
    }

    @Override
    public void save() {
        try {
            if (transactionsFile.getParentFile() != null && !transactionsFile.getParentFile().exists()) {
                transactionsFile.getParentFile().mkdirs();
            }

            if (mappingFile.getParentFile() != null && !mappingFile.getParentFile().exists()) {
                mappingFile.getParentFile().mkdirs();
            }

            try (FileWriter writer = new FileWriter(transactionsFile)) {
                gson.toJson(transactions, writer);
            }

            try (FileWriter writer = new FileWriter(mappingFile)) {
                final Map<String, String> stringMap = new HashMap<>();
                for (Source source : sourceToCategoryMap.keySet()) {
                    stringMap.put(source.getName(), sourceToCategoryMap.get(source).getName());
                }
                gson.toJson(stringMap, writer);
            }
        }
        catch (IOException ex) {
            System.err.println("Error saving transactions to JSON: " + ex.getMessage());
            throw new RuntimeException("Failed to save transactions", ex);
        }
    }

    /**
     * Adds a transaction to the collection.
     *
     * @param transaction the transaction to add
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        save();
    }

    /**
     * Removes a transaction from the collection.
     *
     * @param transaction the transaction to remove
     * @return true if the transaction was removed, false otherwise
     */
    public boolean removeTransaction(Transaction transaction) {
        final boolean removed = transactions.remove(transaction);
        if (removed) {
            save();
        }
        return removed;
    }

    /**
     * Gets all transactions.
     *
     * @return a list of all transactions
     */
    public List<Transaction> getAll() {
        return new ArrayList<>(transactions);
    }
    /**
     * Returns the transaction filtered by both month and date.
     * @param categories the list of categories used to filter the transactions
     * @param month the month (as a {@code YearMonth}) used to filter the transactions
     * @return a list of transactions that match both the category and month criteria;
     */

    @Override
    public List<Transaction> getTransactionsByCategoriesAndMonth(List<Category> categories, YearMonth month) {
        final List<Transaction> result = new ArrayList<>();

        final List<String> categoryNames = new ArrayList<>();
        for (Category c : categories) {
            categoryNames.add(c.getName());
        }

        for (Transaction transaction : transactions) {
            final boolean categoryMatches = categoryNames
                    .contains(sourceToCategoryMap.get(transaction.getSource()).getName());
            final boolean monthMatches = YearMonth.from(transaction.getDate()).equals(month);
            if (categoryMatches && monthMatches) {
                result.add(transaction);
            }
        }

        return result;
    }
    /**
     * Returns a list of all transactions whose dates fall within the specified range
     * (inclusive of both start and end dates).
     * @param startDate the start of the date range (inclusive)
     * @param endDate   the end of the date range (inclusive)
     * @return a list of transactions occurring on or between the given dates;
     *         an empty list if none match
     * @throws NullPointerException if either {@code startDate} or {@code endDate} is {@code null}
     */

    public List<Transaction> getByDateRange(LocalDate startDate, LocalDate endDate) {
        final List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            final LocalDate date = transaction.getDate();
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                result.add(transaction);
            }
        }
        return result;
    }

    /**
     * Gets transactions by source.
     *
     * @param source the source to filter by
     * @return a list of transactions from the specified source
     */
    public List<Transaction> getBySource(Source source) {
        final List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getSource().equals(source)) {
                result.add(transaction);
            }
        }
        return result;
    }

    /**
     * Checks if a source exists in the mapping.
     *
     * @param source the source to check
     * @return true if the source exists, false otherwise
     */
    public boolean sourceExists(Source source) {
        return sourceToCategoryMap.containsKey(source);
    }

    /**
     * Adds a source-category mapping.
     *
     * @param source The source to be added.
     * @param category The category to map to.
     */
    public void addSourceCategory(Source source, Category category) {
        sourceToCategoryMap.put(source, category);
        save();
    }

    /**
     * Gets the category for a source.
     *
     * @param source The source to be looked up.
     * @return Category The category associated with the source, or null if not found.
     */
    public Category getSourceCategory(Source source) {
        return sourceToCategoryMap.get(source);
    }
}
