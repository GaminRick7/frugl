package interface_adapter.view_transaction;

import entity.Transaction;
import use_case.view_transactions.ViewTransactionInputBoundary;
import use_case.view_transactions.ViewTransactionInputData;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

public class ViewTransactionController {
    private final ViewTransactionInputBoundary viewTransactionInteractor;

    public ViewTransactionController(ViewTransactionInputBoundary viewTransactionInteractor) {
        this.viewTransactionInteractor = viewTransactionInteractor;
    }


    public void execute(String monthString)
    {
        YearMonth yearMonth;
            yearMonth = YearMonth.parse(monthString); // Expects "YYYY-MM"


        // get the first and last dates of the month
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        ViewTransactionInputData inputData = new ViewTransactionInputData(startDate, endDate);
        viewTransactionInteractor.execute(inputData);
    }
}
