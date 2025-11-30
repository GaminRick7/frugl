package use_case.set_goal;

import java.util.List;

import entity.Transaction;

public interface ForestDataAccessInterface {

    /**
     * Retrieves all transactions from the data source.
     *
     * @return a list of all transactions
     */
    List<Transaction> getAll();
}
