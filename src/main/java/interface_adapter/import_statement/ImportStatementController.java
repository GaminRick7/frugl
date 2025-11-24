package interface_adapter.import_statement;

import interface_adapter.ViewManagerModel;
import use_case.import_statement.ImportStatementInputBoundary;
import use_case.import_statement.ImportStatementInputData;

/**
 * The controller for the Import Bank Statement Use Case.
 */
public class ImportStatementController {
    private final ImportStatementInputBoundary importStatementInteractor;
    private final ViewManagerModel viewManagerModel;

    public ImportStatementController(ImportStatementInputBoundary interactor, ViewManagerModel viewManagerModel) {
        this.importStatementInteractor = interactor;
        this.viewManagerModel = viewManagerModel;
    }

    public void backToDashboard() {
        viewManagerModel.setState("dashboard");
        viewManagerModel.firePropertyChange("state");
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
