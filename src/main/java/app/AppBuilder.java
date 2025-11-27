package app;

import java.awt.CardLayout;

import javax.swing.*;

import data_access.GoalDataAccessObject;
import data_access.TransactionDataAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.autosave.AutosaveController;
import interface_adapter.autosave.AutosavePresenter;
import interface_adapter.autosave.AutosaveViewModel;
import interface_adapter.dashboard.DashboardViewModel;
import interface_adapter.import_statement.ImportStatementController;
import interface_adapter.import_statement.ImportStatementPresenter;
import interface_adapter.import_statement.ImportStatementViewModel;
import interface_adapter.set_goal.SetGoalController;
import interface_adapter.set_goal.SetGoalPresenter;
import interface_adapter.set_goal.SetGoalViewModel;
import use_case.autosave.AutosaveInputBoundary;
import use_case.autosave.AutosaveInteractor;
import use_case.autosave.AutosaveOutputBoundary;
import use_case.import_statement.GeminiCategorySuggestionService;
import use_case.import_statement.ImportStatementInputBoundary;
import use_case.import_statement.ImportStatementInteractor;
import use_case.import_statement.ImportStatementOutputBoundary;
import use_case.set_goal.SetGoalInputBoundary;
import use_case.set_goal.SetGoalInteractor;
import use_case.set_goal.SetGoalOutputBoundary;
import view.*;

import java.util.List;

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
        List<String> allowedCategories = List.of("Food & Drink", "Transportation", "Retail", "Services", "Entertainment");
        String geminiApiKey = System.getenv("GEMINI_API_KEY");
        GeminiCategorySuggestionService categorySuggestionService =
                new GeminiCategorySuggestionService(geminiApiKey, allowedCategories);
        final ImportStatementInputBoundary importStatementInputBoundary = new ImportStatementInteractor(transactionDataAccessObject,
                importStatementOutputBoundary, categorySuggestionService);
        ImportStatementController importStatementController = new ImportStatementController(importStatementInputBoundary);

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
        dashboardView = new DashboardView(dashboardViewModel);

        cardPanel.add(dashboardView, dashboardViewModel.getViewName());
        return this;
    }

    public AppBuilder addDashboardUseCase() {
        //TODO: add Dashboard Use Case to AppBuilder
        return this;
    }

    public JFrame build() {
        JFrame application = new JFrame("Frugl");

        application.add(cardPanel);
        viewManagerModel.setState(importStatementViewModel.getViewName());
        viewManagerModel.firePropertyChange();
        return application;
    }

}


