package use_case.autosave;

import java.util.List;

import entity.Transaction;




public interface AutosaveDataAccessInterface {
    void save();

    List<Transaction> getAll();
}

