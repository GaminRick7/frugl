package use_case.view_transactions;

public interface ViewTransactionInputBoundary {

    /**
     *
     * @param inputData has input data for view transactions use case
     */
    void execute(ViewTransactionInputData inputData);
}
