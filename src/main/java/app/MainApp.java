package app;

import charts.*;
import data_access.TransactionDataAccessObject;
import interface_adapter.dashboard.DashboardController;
import interface_adapter.dashboard.DashboardPresenter;
import interface_adapter.dashboard.DashboardViewModel;
import use_case.load_dashboard.LoadDashboardInputBoundary;
import use_case.load_dashboard.LoadDashboardInteractor;
import use_case.load_dashboard.LoadDashboardOutputBoundary;
import view.DashboardView;

import javax.swing.*;
import java.awt.*;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TransactionDataAccessObject transactionDAO = new TransactionDataAccessObject();
            DashboardViewModel viewModel = new DashboardViewModel();

            ChartRenderer<ProcessedTimeChartData> timeChartRenderer = new TimeChartRenderer();
            ChartRenderer<ProcessedPieChartData> pieChartRenderer = new PieChartRenderer();

            LoadDashboardOutputBoundary presenter = new DashboardPresenter(viewModel, timeChartRenderer, pieChartRenderer);
            LoadDashboardInputBoundary interactor = new LoadDashboardInteractor(presenter, transactionDAO);

            DashboardController controller = new DashboardController(interactor);

            JFrame frame = new JFrame("frugl dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            DashboardView dashboardView = new DashboardView(controller, viewModel);
            frame.setContentPane(dashboardView);

            frame.setMinimumSize(new Dimension(800, 600));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
