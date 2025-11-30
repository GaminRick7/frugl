package use_case.view_transactions;

import java.time.LocalDate;
import java.util.List;

import entity.Category;
import entity.Source;
import entity.Transaction;

/**
 * Interface for ViewTransactionDataAccess.
 */
public interface ViewTransactionDataAccessInterface {

    /**
     * Retrieves the transactions in the given date range.
     *
     * @param startDate start of month
     * @param endDate end of month
     * @return the transactions in the given year and month
     */
    List<Transaction> getByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves the category for a given source.
     *
     * @param source the source to check
     * @return the category associated with the source
     */
    Category getSourceCategory(Source source);

}
