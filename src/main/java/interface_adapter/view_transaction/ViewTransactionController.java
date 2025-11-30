package interface_adapter.view_transaction;

import java.time.LocalDate;
import java.time.YearMonth;

import use_case.view_transactions.ViewTransactionInputBoundary;
import use_case.view_transactions.ViewTransactionInputData;

public class ViewTransactionController {
    private final ViewTransactionInputBoundary viewTransactionInteractor;

    public ViewTransactionController(ViewTransactionInputBoundary viewTransactionInteractor) {
        this.viewTransactionInteractor = viewTransactionInteractor;
    }

    /**
     * Execute controller method.
     * @param monthString string month
     */
    public void execute(String monthString) {
        final YearMonth yearMonth;
        yearMonth = YearMonth.parse(monthString);

        final LocalDate startDate = yearMonth.atDay(1);
        final LocalDate endDate = yearMonth.atEndOfMonth();

        final ViewTransactionInputData inputData = new ViewTransactionInputData(startDate, endDate);
        viewTransactionInteractor.execute(inputData);
    }
}
