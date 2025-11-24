package use_case.import_statement;

/**
 * The output boundary for the Import Bank Statement Use Case.
 */
public interface ImportStatementOutputBoundary {
    /**
     * Prepares the success view for the Import Bank Statement Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(ImportStatementOutputData outputData);

    /**
     * Prepares the failure view for the Import Bank Statement Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}
