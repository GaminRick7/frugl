package app;

import java.awt.CardLayout;

import javax.swing.*;

import charts.PieChartRenderer;
import charts.ProcessedPieChartData;
import charts.TimeChartRenderer;
import data_access.GoalDataAccessObject;
import data_access.TransactionDataAccessObject;
import entity.Goal;
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
import interface_adapter.set_goal.SetGoalController;
import interface_adapter.set_goal.SetGoalPresenter;
import interface_adapter.set_goal.SetGoalViewModel;
import interface_adapter.view_transaction.ViewTransactionController;
import interface_adapter.view_transaction.ViewTransactionPresenter;
import interface_adapter.view_transaction.ViewTransactionState;
import interface_adapter.view_transaction.ViewTransactionViewModel;
import use_case.autosave.AutosaveInputBoundary;
import use_case.autosave.AutosaveInteractor;
import use_case.autosave.AutosaveOutputBoundary;
import use_case.import_statement.ImportStatementInputBoundary;
import use_case.import_statement.ImportStatementInteractor;
import use_case.import_statement.ImportStatementOutputBoundary;
import use_case.load_dashboard.LoadDashboardInputBoundary;
import use_case.load_dashboard.LoadDashboardInteractor;
import use_case.load_dashboard.LoadDashboardOutputBoundary;
import use_case.set_goal.SetGoalInputBoundary;
import use_case.set_goal.SetGoalInteractor;
import use_case.set_goal.SetGoalOutputBoundary;
import use_case.view_transactions.*;
import view.*;

public class AppBuilder {

    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final TransactionDataAccessObject transactionDataAccessObject = new TransactionDataAccessObject();
    private final GoalDataAccessObject goalDataAccessObject = new GoalDataAccessObject();

    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    private AutosaveView autosaveView;
    private AutosaveViewModel autosaveViewModel;

    private ImportStatementView importStatementView;
    private ImportStatementViewModel importStatementViewModel;

    private GoalView goalView;
    private SetGoalViewModel setGoalViewModel;

    private DashboardView dashboardView;
    private DashboardViewModel dashboardViewModel;

    private TransactionsView viewTransactionView;
    private ViewTransactionViewModel viewTransactionViewModel;


    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addAutosaveView() {
        autosaveViewModel = new AutosaveViewModel();
        autosaveView = new AutosaveView(autosaveViewModel);

        cardPanel.add(autosaveView, autosaveView.getViewName());
        return this;
    }

    public AppBuilder addAutosaveUseCase() {
        final AutosaveOutputBoundary autosaveOutputBoundary = new AutosavePresenter(autosaveViewModel);
        final AutosaveInputBoundary autosaveInputBoundary = new AutosaveInteractor(transactionDataAccessObject, autosaveOutputBoundary);
        AutosaveController controller = new AutosaveController(autosaveInputBoundary);

        autosaveView.setupAutosaveController(controller);
        return this;
    }

    public AppBuilder addImportStatementView() {
        importStatementViewModel = new ImportStatementViewModel();
        importStatementView = new ImportStatementView(importStatementViewModel, viewManagerModel);

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

    public AppBuilder addSetGoalView() {
        setGoalViewModel = new SetGoalViewModel();
        goalView = new GoalView(setGoalViewModel);

        cardPanel.add(goalView, setGoalViewModel.getViewName());
        return this;
    }

    public AppBuilder addGoalUseCase() {
        final SetGoalOutputBoundary setGoalOutputBoundary = new SetGoalPresenter(setGoalViewModel);
        final SetGoalInputBoundary setGoalInputBoundary = new SetGoalInteractor(goalDataAccessObject,
                transactionDataAccessObject, setGoalOutputBoundary);
        SetGoalController setGoalController = new SetGoalController(setGoalInputBoundary);
        goalView.setGoalController(setGoalController);

        return this;
    }

    public AppBuilder addDashboardView() {
        dashboardViewModel = new DashboardViewModel();
        dashboardView = new DashboardView(dashboardViewModel, viewManagerModel);

        cardPanel.add(dashboardView, dashboardViewModel.getViewName());
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

    public DashboardView getDashboardView() {
        return this.dashboardView;
    }


    public AppBuilder addTransactionsView() {
        viewTransactionViewModel = new ViewTransactionViewModel();
        viewTransactionView = new TransactionsView(viewTransactionViewModel);

        cardPanel.add(viewTransactionView, viewTransactionViewModel.getViewName());
        return this;
    }

    public AppBuilder TransactionViewUseCase() {


        final ViewTransactionOutputBoundary viewTransactionOutputBoundary = new ViewTransactionPresenter(viewManagerModel, viewTransactionViewModel);
        final ViewTransactionInputBoundary viewTransactionInputBoundary = new ViewTransactionInteractor(transactionDataAccessObject, viewTransactionOutputBoundary);
        ViewTransactionController viewTransactionController  = new ViewTransactionController(viewTransactionInputBoundary);
        viewTransactionView.setViewTransactionController(viewTransactionController);

        //load initial value
        viewTransactionController.execute("2025-11");
        return this;
    }


    public JFrame build() {
        JFrame application = new JFrame("Frugl");

        application.add(cardPanel);
        viewManagerModel.setState(goalView.viewName);
        viewManagerModel.firePropertyChange();
        return application;
    }

}


