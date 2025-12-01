package use_case.view_transactions;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.Transaction;

/**
 * Interactor for the View Transaction Use Case.
 */
public class ViewTransactionInteractor implements ViewTransactionInputBoundary {
    private final ViewTransactionDataAccessInterface viewDataAccessObject;
    private final ViewTransactionOutputBoundary viewTransactionPresenter;

    public ViewTransactionInteractor(ViewTransactionDataAccessInterface viewDataAccessObject,
                                     ViewTransactionOutputBoundary viewTransactionPresenter) {
        this.viewDataAccessObject = viewDataAccessObject;
        this.viewTransactionPresenter = viewTransactionPresenter;
    }

    private List<Map<String, Object>> convertTransactionToString(List<Transaction> transactions) {

        final List<Map<String, Object>> processedTransactions = new ArrayList<>();

        for (int i = 0; i < transactions.size(); i++) {
            final Transaction transaction = transactions.get(i);
            final Map<String, Object> t1 = new HashMap<>();

            t1.put("date", transaction.getDate());
            t1.put("source", transaction.getSource().getName());
            t1.put("amount", String.valueOf(transaction.getAmount()));
            t1.put("category", viewDataAccessObject.getSourceCategory(transaction.getSource()).getName());

            processedTransactions.add(t1);
        }
        return processedTransactions;
    }

    /**
     * Execute Interactor for View Transaction Use Case.
     * @param transactionInputData Has input data for view transactions use case.
     */
    public void execute(ViewTransactionInputData transactionInputData) {

        final LocalDate startDate = transactionInputData.getStartDate();
        final LocalDate endDate = transactionInputData.getEndDate();
        final List<Transaction> transactions = viewDataAccessObject.getByDateRange(startDate, endDate);
        final List<Map<String, Object>> processedTransactions = convertTransactionToString(transactions);

        final YearMonth yearMonth = YearMonth.from(startDate);

        if (!processedTransactions.isEmpty()) {
            final ViewTransactionOutputData viewTransactionOutputData =
                    new ViewTransactionOutputData(yearMonth.toString(), processedTransactions);
            viewTransactionPresenter.prepareSuccessView(viewTransactionOutputData);
        }
        else {
            viewTransactionPresenter.prepareFailView("No data available.");
        }
    }
}
