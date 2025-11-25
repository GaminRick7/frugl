package app;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        AppBuilder appBuilder = new AppBuilder();
        JFrame application = appBuilder
                .addAutosaveView()
                .addAutosaveUseCase()
                .addImportStatementView()
                .addImportStatementUseCase()
                .addDashboardView()
                .addDashboardUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}


