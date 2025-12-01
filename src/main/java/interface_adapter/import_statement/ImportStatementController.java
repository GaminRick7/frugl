package interface_adapter.import_statement;

import use_case.import_statement.ImportStatementInputBoundary;
import use_case.import_statement.ImportStatementInputData;

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
        final ImportStatementInputData inputData = new ImportStatementInputData(filePath);
        importStatementInteractor.execute(inputData);
    }

}
