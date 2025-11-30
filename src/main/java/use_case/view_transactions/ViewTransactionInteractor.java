package use_case.view_transactions;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.Transaction;

/**
 * Interactor for the View Transactions Use Case.
 * Orchestrates the flow between the DAO and the Presenter.
 */
public class ViewTransactionInteractor implements ViewTransactionInputBoundary {
    private final ViewTransactionDataAccessInterface viewDataAccessObject;
    private final ViewTransactionOutputBoundary viewTransactionPresenter;

    public ViewTransactionInteractor(ViewTransactionDataAccessInterface viewDataAccessObject,
                                     ViewTransactionOutputBoundary viewTransactionPresenter) {
        this.viewDataAccessObject = viewDataAccessObject;
        this.viewTransactionPresenter = viewTransactionPresenter;
    }

    private List<Map<String, Object>> convertTransactionToString(final List<Transaction> trans) {
        final List<Map<String, Object>> processedTransactions = new ArrayList<>();

        for (final Transaction transaction : trans) {
            final Map<String, Object> transactionMap = new HashMap<>();

            transactionMap.put("date", transaction.getDate());
            transactionMap.put("source", transaction.getSource().getName());
            transactionMap.put("amount", String.valueOf(transaction.getAmount()));
            transactionMap.put("category", viewDataAccessObject.getSourceCategory(transaction.getSource()).getName());

            processedTransactions.add(transactionMap);
        }
        return processedTransactions;
    }

    /**
     * Executes the View Transaction use case logic.
     * @param transactionInputData The input data containing the date range.
     */
    @Override
    public void execute(final ViewTransactionInputData transactionInputData) {
        final LocalDate start = transactionInputData.getStartDate();
        final LocalDate end = transactionInputData.getEndDate();
        final List<Transaction> trans = viewDataAccessObject.getByDateRange(start, end);
        final List<Map<String, Object>> processedTransactions = convertTransactionToString(trans);

        final YearMonth yearMonth = YearMonth.from(start);

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
