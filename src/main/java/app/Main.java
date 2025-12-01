package app;

import javax.swing.JFrame;

import view.DashboardView;

public class Main {

    /**
     * Main entry point for the application.
     *
     * @param args command line arguments (not used)
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {
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
