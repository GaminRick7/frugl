package interface_adapter.view_transaction;

import entity.Transaction;
import use_case.view_transactions.ViewTransactionInputBoundary;
import use_case.view_transactions.ViewTransactionInputData;

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

        ViewTransactionInputData inputData = new ViewTransactionInputData(yearMonth);
        viewTransactionInteractor.execute(inputData);
    }
}
