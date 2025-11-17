package interface_adapter.import_statement;

/**
 * The controller for the Import Bank Statement Use Case.
 */
public class ImportStatementController {
    private final ImportStatementInputBoundary importStatementInteractor;

    public ImportStatementController(ImportStatementInputBoundary interactor) {
        this.importStatementInteractor = interactor;
    }

    /**
     * Executes the Import Bank Statement Use Case.
     * @param filePath the filePath inputted by the user
     */
    public void execute(String filePath) {
        ImportStatementInputData inputData = new ImportStatementInputData(filePath);
        importStatementInteractor.execute(inputData);
    }

}
