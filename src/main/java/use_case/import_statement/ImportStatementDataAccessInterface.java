package use_case.import_statement;

import entity.Transaction;

import java.time.YearMonth;
import java.util.List;

/**
 * The DAO interface for the Import Bank Statement Use Case.
 */

public interface ImportStatementDataAccessInterface {

    void addTransaction(Transaction transaction);

    boolean sourceExists(String sourceName);
}
