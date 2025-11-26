package use_case.import_statement;

import entity.Source;
import entity.Transaction;


/**
 * The DAO interface for the Import Bank Statement Use Case.
 */

public interface ImportStatementDataAccessInterface {

    void addTransaction(Transaction transaction);

    boolean sourceExists(Source sourceName);
}
