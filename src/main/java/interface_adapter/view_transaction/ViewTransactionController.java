package interface_adapter.view_transaction;

import java.time.LocalDate;
import java.time.YearMonth;

import use_case.view_transactions.ViewTransactionInputBoundary;
import use_case.view_transactions.ViewTransactionInputData;

/**
 * Controller for View Transaction Use Case.
 */
public class ViewTransactionController {
    private final ViewTransactionInputBoundary viewTransactionInteractor;

    public ViewTransactionController(ViewTransactionInputBoundary viewTransactionInteractor) {
        this.viewTransactionInteractor = viewTransactionInteractor;
    }

    /**
     * Executes View Transaction Use Case.
     * @param monthString String representing a month.
     */
    public void execute(String monthString) {
        final YearMonth yearMonth = YearMonth.parse(monthString);
        // Expects "YYYY-MM"

        // get the first and last dates of the month
        final LocalDate startDate = yearMonth.atDay(1);
        final LocalDate endDate = yearMonth.atEndOfMonth();

        final ViewTransactionInputData inputData = new ViewTransactionInputData(startDate, endDate);
        viewTransactionInteractor.execute(inputData);
    }
}
