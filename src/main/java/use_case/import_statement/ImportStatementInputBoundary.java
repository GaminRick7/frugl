package use_case.import_statement;

/**
 * The Import Bank Statement Use Case.
 */
public interface ImportStatementInputBoundary {

    /**
     * Execute the Change Password Use Case.
     * @param importStatementInputData the input data for this use case
     */
    void execute(ImportStatementInputData importStatementInputData);
}
