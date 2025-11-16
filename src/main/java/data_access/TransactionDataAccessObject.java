package data_access;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import entity.Transaction;
import use_case.autosave.AutosaveDataAccessInterface;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDataAccessObject implements AutosaveDataAccessInterface {
    private final File jsonFile;
    private final Gson gson;
    private List<Transaction> transactions;

    public TransactionDataAccessObject(String jsonFilePath) {
        this.jsonFile = new File(jsonFilePath);
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        this.transactions = new ArrayList<>();
        load();
    }

    public TransactionDataAccessObject() {
        this("transactions.json");
    }

    public void load() {
        if (jsonFile.exists() && jsonFile.length() > 0) {
            try (FileReader reader = new FileReader(jsonFile)) {
                Type listType = new TypeToken<List<Transaction>>(){}.getType();
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

    @Override
    public void save() {
        try {
            if (jsonFile.getParentFile() != null && !jsonFile.getParentFile().exists()) {
                jsonFile.getParentFile().mkdirs();
            }
            
            try (FileWriter writer = new FileWriter(jsonFile)) {
                gson.toJson(transactions, writer);
            }
        } catch (IOException e) {
            System.err.println("Error saving transactions to JSON: " + e.getMessage());
            throw new RuntimeException("Failed to save transactions", e);
        }
    }

    public void add(Transaction transaction) {
        transactions.add(transaction);
        save();
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

    // public List<Transaction> getBySource(Source source) {
    //     List<Transaction> result = new ArrayList<>();
    //     for (Transaction transaction : transactions) {
    //         if (transaction.getSource().equals(source)) {
    //             result.add(transaction);
    //         }
    //     }
    //     return result;
    // }

    public boolean remove(Transaction transaction) {
        boolean removed = transactions.remove(transaction);
        if (removed) {
            save();
        }
        return removed;
    }
}
