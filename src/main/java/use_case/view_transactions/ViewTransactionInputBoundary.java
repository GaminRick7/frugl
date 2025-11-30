package use_case.view_transactions;

/**
 * Input boundary object for viewing transaction.
 */
public interface ViewTransactionInputBoundary {
    /**
     * Execute method for input boundary.
     * @param inputData has input data for view transactions use case
     */
    void execute(ViewTransactionInputData inputData);
}
