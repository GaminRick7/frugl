package use_case.view_transactions;

public interface ViewTransactionOutputBoundary {
    /**
     * Prepare success view if entries are nonempty for slescted month and year.
     * @param viewTransactionOutputData is the output data object
     */
    void prepareSuccessView(ViewTransactionOutputData viewTransactionOutputData);

    /**
     * Prepares the failure view if there is no data.
     * @param errorMessage explains there is no dats
     */
    void prepareFailView(String errorMessage);
}
