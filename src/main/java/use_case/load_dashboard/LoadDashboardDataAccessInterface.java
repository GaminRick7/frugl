package use_case.load_dashboard;

import java.time.LocalDate;
import java.util.List;

import entity.Category;
import entity.Source;
import entity.Transaction;

/**
 * Data Access Interface for the Load Dashboard Use Case.
 */
public interface LoadDashboardDataAccessInterface {
    /**
     * Retrieves a list of transactions within the specified date range.
     * @param startDate Start date of the range (inclusive).
     * @param endDate End date of the range (inclusive)
     * @return A list of transactions within the given range.
     */
    List<Transaction> getByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves the category associated with a given source.
     * @param source The source to look up.
     * @return The Category associated with the source
     */
    Category getSourceCategory(Source source);
}
