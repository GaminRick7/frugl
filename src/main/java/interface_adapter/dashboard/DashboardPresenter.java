package interface_adapter.dashboard;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import use_case.load_dashboard.LoadDashboardOutputBoundary;
import use_case.load_dashboard.LoadDashboardOutputData;
import use_case.load_dashboard.PieChartData;
import use_case.load_dashboard.TimeChartData;

/**
 * Presenter for the Dashboard Use Case.
 */
public class DashboardPresenter implements LoadDashboardOutputBoundary {
    private final DashboardViewModel dashboardViewModel;
    private final ChartRenderer<PieChartData> pieChartRenderer;
    private final ChartRenderer<TimeChartData> timeChartRenderer;

    public DashboardPresenter(DashboardViewModel viewModel, ChartRenderer<PieChartData> pieChartRenderer,
                              ChartRenderer<TimeChartData> timeChartRenderer) {
        this.dashboardViewModel = viewModel;
        this.pieChartRenderer = pieChartRenderer;
        this.timeChartRenderer = timeChartRenderer;
    }

    @Override
    public void present(LoadDashboardOutputData outputData) {
        final List<Image> images = new ArrayList<>();
        final DashboardState state = dashboardViewModel.getState();
        try {
            images.add(pieChartRenderer.render(outputData.getPieChartData()));
            images.add(timeChartRenderer.render(outputData.getTimeChartData()));

            state.setChartImages(images);
            state.setError(null);

            this.dashboardViewModel.setState(state);
            this.dashboardViewModel.firePropertyChange();
        }
        catch (IOException exception) {
            prepareFailView("Unable to render charts: " + exception.getMessage());
        }
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final DashboardState state = dashboardViewModel.getState();
        state.setError(errorMessage);
        this.dashboardViewModel.setState(state);
        this.dashboardViewModel.firePropertyChange();

    }

}
