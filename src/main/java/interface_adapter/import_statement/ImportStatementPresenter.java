package interface_adapter.import_statement;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * The Presenter for the Import Bank Statement Use Case.
 */
public class ImportStatementPresenter implements ImportStatementOutputBoundary {

    private final ViewManagerModel viewManagerModel;
    private final ImportStatementViewModel importStatementViewModel;


    public ImportStatementPresenter(ViewManagerModel viewManagerModel, ImportStatementViewModel importViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.importStatementViewModel = importViewModel;
    }

    @Override
    public void prepareSuccessView(ImportStatementOutputData outputData) {

        YearMonth month = outputData.getStatementMonth();

        String message = "Importing Successful for " + month.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        viewManagerModel.showPopup(message);
        importStatementViewModel.setState("");
        importStatementViewModel.firePropertyChange("filePath");
    }

    @Override
    public void prepareFailView(String errorMessage) {
        String message = "Importing Unsuccessful: " + errorMessage;
        viewManagerModel.showPopup(message);
        importStatementViewModel.setState("");
        importStatementViewModel.firePropertyChange("filePath");
    }
}
