package app;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import charts.PieChartRenderer;
import charts.TimeChartRenderer;
import data_access.TransactionDataAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.autosave.AutosaveController;
import interface_adapter.autosave.AutosavePresenter;
import interface_adapter.autosave.AutosaveViewModel;
import interface_adapter.dashboard.DashboardController;
import interface_adapter.dashboard.DashboardPresenter;
import interface_adapter.dashboard.DashboardViewModel;
import interface_adapter.import_statement.ImportStatementController;
import interface_adapter.import_statement.ImportStatementPresenter;
import interface_adapter.import_statement.ImportStatementViewModel;
import use_case.autosave.AutosaveInputBoundary;
import use_case.autosave.AutosaveInteractor;
import use_case.autosave.AutosaveOutputBoundary;
import use_case.import_statement.ImportStatementInputBoundary;
import use_case.import_statement.ImportStatementInteractor;
import use_case.import_statement.ImportStatementOutputBoundary;
import use_case.load_dashboard.LoadDashboardInputBoundary;
import use_case.load_dashboard.LoadDashboardInteractor;
import use_case.load_dashboard.LoadDashboardOutputBoundary;
import view.AutosaveView;
import view.DashboardView;
import view.ImportStatementView;
import view.ViewManager;

public class AppBuilder {

    private final JPanel cardPanel = new JPanel();

    private final CardLayout cardLayout = new CardLayout();

    private final TransactionDataAccessObject transactionDataAccessObject = new TransactionDataAccessObject();

    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    private AutosaveView autosaveView;
    private AutosaveViewModel autosaveViewModel;

    private ImportStatementView importStatementView;
    private ImportStatementViewModel importStatementViewModel;

    private DashboardView dashboardView;
    private DashboardViewModel dashboardViewModel;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addAutosaveView() {
        autosaveViewModel = new AutosaveViewModel();
        autosaveView = new AutosaveView(autosaveViewModel);

        cardPanel.add(autosaveView, getAutosaveViewName());
        return this;
    }

    public  AppBuilder addAutosaveUseCase() {
        final AutosaveOutputBoundary autosaveOutputBoundary = new AutosavePresenter(autosaveViewModel);
        final AutosaveInputBoundary autosaveInputBoundary = new AutosaveInteractor(transactionDataAccessObject, autosaveOutputBoundary);
        AutosaveController controller = new AutosaveController(autosaveInputBoundary);

        autosaveView.setupAutosaveConntroller(controller);
        return this;
    }

    public AppBuilder addImportStatementView() {
        importStatementViewModel = new ImportStatementViewModel();
        importStatementView = new ImportStatementView(importStatementViewModel);

        cardPanel.add(importStatementView, importStatementView.getViewName());
        return this;
    }

    public AppBuilder addImportStatementUseCase() {
        final ImportStatementOutputBoundary importStatementOutputBoundary = new ImportStatementPresenter(viewManagerModel,
                importStatementViewModel);
        final ImportStatementInputBoundary importStatementInputBoundary = new ImportStatementInteractor(transactionDataAccessObject, importStatementOutputBoundary);
        ImportStatementController importStatementController = new ImportStatementController(importStatementInputBoundary, viewManagerModel);

        importStatementView.setImportStatementController(importStatementController);
        return this;

    }

    public AppBuilder addDashboardView() {
        dashboardViewModel = new DashboardViewModel();
        dashboardView = new DashboardView(dashboardViewModel);

        cardPanel.add(autosaveView, getAutosaveViewName());
        return this;
    }

    public AppBuilder addDashboardUseCase() {
        PieChartRenderer pieChartRenderer = new PieChartRenderer();
        TimeChartRenderer timeChartRenderer = new TimeChartRenderer();
        final LoadDashboardOutputBoundary loadDashboardOutputBoundary =  new DashboardPresenter(dashboardViewModel, pieChartRenderer, timeChartRenderer);
        final LoadDashboardInputBoundary loadDashboardInputBoundary = new LoadDashboardInteractor(loadDashboardOutputBoundary, transactionDataAccessObject);
        DashboardController dashboardController = new DashboardController(loadDashboardInputBoundary);

        dashboardView.setDashboardController(dashboardController);
        return this;
    }

    public JFrame build() {
        if (autosaveView == null) {
            throw new IllegalStateException("Call addAutosaveFeature() before build().");
        }
        JFrame frame = new JFrame("Frugl");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(cardPanel);
        cardLayout.show(cardPanel, getImportStatementViewName());
        frame.pack();
        frame.setLocationRelativeTo(null);
        return frame;
    }

    private String getAutosaveViewName() {
        return autosaveView.getClass().getSimpleName();
    }
    private String getImportStatementViewName() {return importStatementView.getViewName();}
}


