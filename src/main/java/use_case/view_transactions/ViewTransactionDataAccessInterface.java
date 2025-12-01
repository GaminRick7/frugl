package use_case.view_transactions;

import java.time.LocalDate;
import java.util.List;

import entity.Category;
import entity.Source;
import entity.Transaction;

/**
 * Data Access Interface for View Transactions Use Case.
 */
public interface ViewTransactionDataAccessInterface {

    /**
     * Get transactions within the given date range.
     *
     * @param startDate Start of month.
     * @param endDate End of month.
     * @return The transactions in the given year and month.
     */
    List<Transaction> getByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Category getter for source.
     *
     * @param source The source to look up.
     * @return The category associated with a source.
     */
    Category getSourceCategory(Source source);

}
