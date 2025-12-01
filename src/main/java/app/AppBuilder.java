package app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import data_access.GoalDataAccessObject;
import data_access.TransactionDataAccessObject;
import interface_adapter.ViewManagerModel;
import interface_adapter.autosave.AutosaveController;
import interface_adapter.autosave.AutosavePresenter;
import interface_adapter.autosave.AutosaveViewModel;
import interface_adapter.dashboard.DashboardController;
import interface_adapter.dashboard.DashboardPresenter;
import interface_adapter.dashboard.DashboardViewModel;
import interface_adapter.dashboard.PieChartRenderer;
import interface_adapter.dashboard.TimeChartRenderer;
import interface_adapter.import_statement.ImportStatementController;
import interface_adapter.import_statement.ImportStatementPresenter;
import interface_adapter.import_statement.ImportStatementViewModel;
import interface_adapter.set_goal.SetGoalController;
import interface_adapter.set_goal.SetGoalPresenter;
import interface_adapter.set_goal.SetGoalViewModel;
import interface_adapter.view_transaction.ViewTransactionController;
import interface_adapter.view_transaction.ViewTransactionPresenter;
import interface_adapter.view_transaction.ViewTransactionViewModel;
import use_case.autosave.AutosaveInputBoundary;
import use_case.autosave.AutosaveInteractor;
import use_case.autosave.AutosaveOutputBoundary;
import use_case.import_statement.GeminiCategorizer;
import use_case.import_statement.ImportStatementInputBoundary;
import use_case.import_statement.ImportStatementInteractor;
import use_case.import_statement.ImportStatementOutputBoundary;
import use_case.load_dashboard.LoadDashboardInputBoundary;
import use_case.load_dashboard.LoadDashboardInteractor;
import use_case.load_dashboard.LoadDashboardOutputBoundary;
import use_case.set_goal.SetGoalInputBoundary;
import use_case.set_goal.SetGoalInteractor;
import use_case.set_goal.SetGoalOutputBoundary;
import use_case.view_transactions.ViewTransactionInputBoundary;
import use_case.view_transactions.ViewTransactionInteractor;
import use_case.view_transactions.ViewTransactionOutputBoundary;
import view.AutosaveView;
import view.DashboardView;
import view.GoalView;
import view.ImportStatementView;
import view.TransactionsView;
import view.ViewManager;

public class AppBuilder {
    private static final int BORDER_TOP = 5;
    private static final int BORDER_LEFT = 10;
    private static final int BORDER_BOTTOM = 5;
    private static final int BORDER_RIGHT = 10;

    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final TransactionDataAccessObject transactionDataAccessObject = new TransactionDataAccessObject();
    private final GoalDataAccessObject goalDataAccessObject = new GoalDataAccessObject();

    private final ViewManagerModel viewManagerModel = new ViewManagerModel();
    private final ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    private AutosaveView autosaveView;
    private AutosaveViewModel autosaveViewModel;

    private ImportStatementView importStatementView;
    private ImportStatementViewModel importStatementViewModel;

    private GoalView goalView;
    private SetGoalViewModel setGoalViewModel;

    private DashboardView dashboardView;
    private DashboardViewModel dashboardViewModel;

    private SetGoalController setGoalController;
    private TransactionsView viewTransactionView;
    private ViewTransactionViewModel viewTransactionViewModel;

    /**
     * Creates a new builder.
     */
    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    /**
     * Initializes the autosave view as a status bar.
     *
     * @return this builder
     */
    public AppBuilder addAutosaveView() {
        autosaveViewModel = new AutosaveViewModel();
        autosaveView = new AutosaveView(autosaveViewModel);

        autosaveView.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, java.awt.Color.GRAY),
                BorderFactory.createEmptyBorder(BORDER_TOP, BORDER_LEFT, BORDER_BOTTOM, BORDER_RIGHT)
        ));

        // it is not added to card panel since it will be added as a status bar in build()
        return this;
    }

    /**
     * Creates the autosave use case and connects it to the autosave view.
     *
     * @return this builder
     */
    public AppBuilder addAutosaveUseCase() {
        final AutosaveOutputBoundary autosaveOutputBoundary = new AutosavePresenter(autosaveViewModel);
        final AutosaveInputBoundary autosaveInputBoundary =
                new AutosaveInteractor(transactionDataAccessObject, goalDataAccessObject, autosaveOutputBoundary);
        final AutosaveController controller = new AutosaveController(autosaveInputBoundary);

        autosaveView.setupAutosaveController(controller);
        return this;
    }

    /**
     * Initializes the import statement view .
     *
     * @return this builder
     */
    public AppBuilder addImportStatementView() {
        importStatementViewModel = new ImportStatementViewModel();
        importStatementView = new ImportStatementView(importStatementViewModel, viewManagerModel);

        cardPanel.add(importStatementView, importStatementViewModel.getViewName());
        return this;
    }

    /**
     * Creates the import statement use case and connects it to the view.
     *
     * @return this builder
     */
    public AppBuilder addImportStatementUseCase() {
        final ImportStatementOutputBoundary importStatementOutputBoundary =
                new ImportStatementPresenter(viewManagerModel, importStatementViewModel);
        final GeminiCategorizer geminiCategorizer = new GeminiCategorizer(System.getenv("GEMINI_API_KEY"));
        final ImportStatementInputBoundary importStatementInputBoundary =
                new ImportStatementInteractor(transactionDataAccessObject, importStatementOutputBoundary,
                        geminiCategorizer);
        final ImportStatementController importStatementController =
                new ImportStatementController(importStatementInputBoundary);

        importStatementView.setImportStatementController(importStatementController);
        return this;
    }

    /**
     * Initializes goal-setting view.
     *
     * @return this builder
     */
    public AppBuilder addSetGoalView() {
        setGoalViewModel = new SetGoalViewModel();
        goalView = new GoalView(setGoalViewModel, viewManagerModel);

        cardPanel.add(goalView, setGoalViewModel.getViewName());
        return this;
    }

    /**
     * Creates the goal use case and connects it to the goal view.
     *
     * @return this builder
     */
    public AppBuilder addGoalUseCase() {
        final SetGoalOutputBoundary setGoalOutputBoundary = new SetGoalPresenter(setGoalViewModel);
        final SetGoalInputBoundary setGoalInputBoundary = new SetGoalInteractor(goalDataAccessObject,
                transactionDataAccessObject, setGoalOutputBoundary);

        this.setGoalController = new SetGoalController(setGoalInputBoundary);
        goalView.setGoalController(setGoalController);

        return this;
    }

    /**
     * Initializes the dashboard view.
     *
     * @return this builder
     */
    public AppBuilder addDashboardView() {
        dashboardViewModel = new DashboardViewModel();
        dashboardView = new DashboardView(dashboardViewModel, viewManagerModel);

        cardPanel.add(dashboardView, dashboardViewModel.getViewName());
        return this;
    }

    /**
     * Creates the dashboard use case and connects it to the dashboard view.
     *
     * @return this builder for chaining additional configuration
     */
    public AppBuilder addDashboardUseCase() {
        final PieChartRenderer pieChartRenderer = new PieChartRenderer();
        final TimeChartRenderer timeChartRenderer = new TimeChartRenderer();
        final LoadDashboardOutputBoundary loadDashboardOutputBoundary =
                new DashboardPresenter(dashboardViewModel, pieChartRenderer, timeChartRenderer);
        final LoadDashboardInputBoundary loadDashboardInputBoundary =
                new LoadDashboardInteractor(loadDashboardOutputBoundary, transactionDataAccessObject);
        final DashboardController dashboardController = new DashboardController(loadDashboardInputBoundary);

        dashboardView.setDashboardController(dashboardController);
        return this;
    }

    /**
     * Getter for the  dashboard view.
     *
     * @return the dashboard view instance
     */
    public DashboardView getDashboardView() {
        return this.dashboardView;
    }

    /**
     * Get TransactionView.
     * @return TransactionView
     */
    public AppBuilder addTransactionsView() {
        viewTransactionViewModel = new ViewTransactionViewModel();
        viewTransactionView = new TransactionsView(viewTransactionViewModel, viewManagerModel);
        cardPanel.add(viewTransactionView, viewTransactionViewModel.getViewName());

        return this;
    }
    /**
     * Does transactionViewUseCases.
     * @return transactionViewUseCase
     */

    public AppBuilder addTransactionViewUseCase() {

        final ViewTransactionOutputBoundary viewTransactionOutputBoundary =
                new ViewTransactionPresenter(viewManagerModel, viewTransactionViewModel);
        final ViewTransactionInputBoundary viewTransactionInputBoundary =
                new ViewTransactionInteractor(transactionDataAccessObject, viewTransactionOutputBoundary);
        final ViewTransactionController viewTransactionController =
                new ViewTransactionController(viewTransactionInputBoundary);
        viewTransactionView.setViewTransactionController(viewTransactionController);

        final YearMonth currentYearMonth = YearMonth.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        final String formattedYearMonth = currentYearMonth.format(formatter);
        viewTransactionController.execute(formattedYearMonth);
        return this;

    }

    /**
     * Getter for the SetGoalController.
     *
     * @return the SetGoalController instance
     */

    public SetGoalController getSetGoalController() {
        return this.setGoalController;
    }

    /**
     * Builds the application frame, shows the initial view, and returns it.
     *
     * @return a JFrame application frame ready to display
     */
    public JFrame build() {
        final JFrame application = new JFrame("frugl");
        final JPanel mainPanel = new JPanel(new BorderLayout());

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // autosave view is added as a status bar
        if (autosaveView != null) {
            mainPanel.add(autosaveView, BorderLayout.SOUTH);
        }

        application.add(mainPanel);
        viewManagerModel.setState(dashboardViewModel.getViewName());
        viewManagerModel.firePropertyChange();
        return application;
    }
}

