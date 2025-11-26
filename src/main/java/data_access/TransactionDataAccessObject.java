package data_access;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import entity.Category;
import entity.Source;
import entity.Transaction;
import use_case.autosave.AutosaveDataAccessInterface;
import use_case.set_goal.ForestDataAccessInterface;
import use_case.import_statement.ImportStatementDataAccessInterface;
import use_case.load_dashboard.LoadDashboardDataAccessInterface;

public class TransactionDataAccessObject implements AutosaveDataAccessInterface, LoadDashboardDataAccessInterface, ImportStatementDataAccessInterface, ForestDataAccessInterface {
    private final Gson gson;

    private final File transactionsFile;
    private List<Transaction> transactions;

    private final File mappingFile;
    private HashMap<Source, Category> sourceToCategoryMap;

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
                Type listType = new TypeToken<List<Transaction>>() {
                }.getType();
                this.transactions = gson.fromJson(reader, listType);
                if (this.transactions == null) {
                    this.transactions = new ArrayList<>();
                }
            } catch (IOException e) {
                System.err.println("Error loading transactions from JSON: " + e.getMessage());
                this.transactions = new ArrayList<>();
            }
        } else {
            this.transactions = new ArrayList<>();
        }
    }

    private void loadSourceToCategoryMap() {
        if (mappingFile.exists() && mappingFile.length() > 0) {
            try (FileReader reader = new FileReader(mappingFile)) {
                Type stringMapType = new TypeToken<HashMap<String, String>>() {}.getType();
                HashMap<String, String> stringMap = gson.fromJson(reader, stringMapType);

                this.sourceToCategoryMap = new HashMap<>();
                if (stringMap != null) {
                    for (String sourceName : stringMap.keySet()) {
                        Source source = new Source(sourceName);
                        Category category = new Category(stringMap.get(sourceName));
                        this.sourceToCategoryMap.put(source, category);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error loading source-category mappings: " + e.getMessage());
                this.sourceToCategoryMap = new HashMap<>();
            }
        } else {
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
                HashMap<String, String> stringMap = new HashMap<>();
                for (Source source : sourceToCategoryMap.keySet()) {
                    stringMap.put(source.getName(), sourceToCategoryMap.get(source).getName());
                }
                gson.toJson(stringMap, writer);
            }
        } catch (IOException e) {
            System.err.println("Error saving transactions to JSON: " + e.getMessage());
            throw new RuntimeException("Failed to save transactions", e);
        }
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        save();
    }

    public boolean removeTransaction(Transaction transaction) {
        boolean removed = transactions.remove(transaction);
        if (removed) {
            save();
        }
        return removed;
    }

    public List<Transaction> getAll() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> getByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            LocalDate date = transaction.getDate();
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                result.add(transaction);
            }
        }
        return result;
    }

    public List<Transaction> getBySource(Source source) {
         List<Transaction> result = new ArrayList<>();
         for (Transaction transaction : transactions) {
             if (transaction.getSource().equals(source)) {
                 result.add(transaction);
             }
         }
         return result;
     }

    public boolean sourceExists(Source source) {
        return sourceToCategoryMap.containsKey(source);
    }

    public void addSourceCategory(Source source, Category category) {
        sourceToCategoryMap.put(source, category);
    }

    public Category getSourceCategory(Source source) {
        return sourceToCategoryMap.get(source);
    }
}
