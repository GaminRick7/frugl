package app;

import java.awt.Frame;

import javax.swing.JFrame;

import interface_adapter.set_goal.SetGoalController;
import view.DashboardView;

/**
 * The Main class of the application.
 */
public class Main {
    /**
     * Main entry point for the application.
     *
     * @param args command line arguments (not used)
     * @throws Exception if an error occurs during application initialization
     */
    public static void main(String[] args) {
        final AppBuilder appBuilder = new AppBuilder();
        final JFrame application = appBuilder
                .addDashboardView()
                .addDashboardUseCase()
                .addAutosaveView()
                .addAutosaveUseCase()
                .addImportStatementView()
                .addImportStatementUseCase()
                .addSetGoalView()
                .addGoalUseCase()
                .addTransactionsView()
                .addTransactionViewUseCase()
                .build();

        application.setExtendedState(Frame.MAXIMIZED_BOTH);
        application.setLocationRelativeTo(null);
        application.setVisible(true);

        final DashboardView dashboardView = appBuilder.getDashboardView();
        if (dashboardView != null) {
            dashboardView.loadInitialData();
        }

        final SetGoalController goalController = appBuilder.getSetGoalController();
        if (goalController != null) {
            goalController.loadForest();
        }
    }
}
