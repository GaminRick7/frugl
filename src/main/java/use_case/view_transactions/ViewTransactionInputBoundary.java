package use_case.view_transactions;

/**
 * View Transaction Input Boundary.
 */
public interface ViewTransactionInputBoundary {
    /**
     * Executes view transaction logic.
     * @param inputData Has input data for view transactions use case.
     */
    void execute(ViewTransactionInputData inputData);
}
