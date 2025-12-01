package app;

import javax.swing.JFrame;

import view.DashboardView;

/**
 * The Main class of the application.
 */
public class Main {
    /**
     * Main entry point for the application.
     *
     * @param args command line arguments (not used)
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

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);

        final DashboardView dashboardView = appBuilder.getDashboardView();
        if (dashboardView != null) {
            dashboardView.loadInitialData();
        }
    }
}
