package use_case.view_transactions;

public interface ViewTransactionOutputBoundary {
    /**
     * Prepare success view if entries are nonempty for selected month and year.
     * @param viewTransactionOutputData Output Data for View Transaction Use Case.
     */
    void prepareSuccessView(ViewTransactionOutputData viewTransactionOutputData);

    /**
     * Prepares the failure view if there is no data.
     * @param errorMessage Explains that there is no data.
     */
    void prepareFailView(String errorMessage);
}
