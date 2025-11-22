package interface_adapter.import_statement;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * The Presenter for the Import Bank Statement Use Case.
 */
public class ImportStatementPresenter implements ImportStatementOutputBoundary {

    private final ViewManagerModel viewManagerModel;
    private final ImportViewModel importViewModel;
    private final DashboardViewModel dashboardViewModel;
    private final TransactionsViewModel transactionsViewModel;
    private final GoalsViewModel goalsViewModel;


    public ImportStatementPresenter(ViewManagerModel viewManagerModel, ImportViewModel importViewModel,
                                    DashboardViewModel dashboardViewModel, TransactionsViewModel transactionsViewModel,
                                    GoalsViewModel goalsViewModel ) {
        this.viewManagerModel = viewManagerModel;
        this.importViewModel = importViewModel;
        this.dashboardViewModel = dashboardViewModel;
        this.transactionsViewModel = transactionsViewModel;
        this.goalsViewModel = goalsViewModel;
    }

    @Override
    public void prepareSuccessView(ImportStatementOutputData outputData) {

        YearMonth month = outputData.getStatementMonth();

        String message = "Importing Successful for " + month.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        viewManagerModel.showPopup(message);
        importViewModel.setState("");
        importViewModel.firePropertyChange("filePath");
        dashboardViewModel.fireStatementAdded(month);
        transactionsViewModel.fireStatementAdded(month);
        goalsViewModel.fireStatementAdded(month);
    }

    @Override
    public void prepareFailView(String errorMessage) {
        String message = "Importing Unsuccessful: " + errorMessage;
        viewManagerModel.showPopup(message);
        importViewModel.setState("");
        importViewModel.firePropertyChange("filePath");
    }
}
