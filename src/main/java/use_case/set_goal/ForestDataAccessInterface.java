package use_case.set_goal;

import java.time.YearMonth;
import java.util.List;

import entity.Category;
import entity.Transaction;

public interface ForestDataAccessInterface {

    /**
     * Retrieves all transactions from the data source.
     *
     * @return a list of all transactions
     */
    List<Transaction> getAll();

    /**
     * Retrieves all transactions that fall within the specified categories and month.
     * The returned list includes only those transactions whose category is contained
     * in the provided category list and whose date matches the given {@link YearMonth}.
     * @param categories the list of categories used to filter the transactions
     * @param month the month (as a {@code YearMonth}) used to filter the transactions
     * @return a list of transactions matching the given categories and month
     */
    List<Transaction> getTransactionsByCategoriesAndMonth(List<Category> categories, YearMonth month);
}
