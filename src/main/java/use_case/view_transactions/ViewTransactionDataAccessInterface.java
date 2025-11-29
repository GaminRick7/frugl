package use_case.view_transactions;
import entity.Category;
import entity.Source;
import entity.Transaction;

import java.time.LocalDate;
import java.util.List;


public interface ViewTransactionDataAccessInterface {

    /**
     *
     * @return the trnsations in the given year and month
     * @param startDate start of month
     * @param endDate end of month
     */
    List<Transaction> getByDateRange(LocalDate startDate, LocalDate endDate);

    Category getSourceCategory(Source source);

}
