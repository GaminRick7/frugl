package use_case.import_statement;

import entity.Category;
import entity.Source;
import entity.Transaction;

/**
 * The DataAccess interface for the Import Bank Statement Use Case.
 */

public interface ImportStatementDataAccessInterface {
    /**
     * Stores a transaction in the underlying data source.
     *
     * @param transaction the transaction to be added
     * */
    void addTransaction(Transaction transaction);

    /**
     * Checks whether a vendor source already exists in the data source.
     *
     * @param sourceName the source (vendor) to check
     * @return {@code true} if the source already exists; {@code false} otherwise
     */
    boolean sourceExists(Source sourceName);

    /**
     * Saves or updates the category associated with a vendor source.
     *
     * @param source   the vendor source to categorize
     * @param category the category to assign to the source
     */
    void addSourceCategory(Source source, Category category);
}
