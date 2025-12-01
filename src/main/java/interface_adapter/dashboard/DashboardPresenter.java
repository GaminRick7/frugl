package interface_adapter.dashboard;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import charts.ChartRenderer;
import charts.ProcessedPieChartData;
import charts.ProcessedTimeChartData;
import use_case.load_dashboard.LoadDashboardOutputBoundary;
import use_case.load_dashboard.LoadDashboardOutputData;

public class DashboardPresenter implements LoadDashboardOutputBoundary {
    private final DashboardViewModel viewModel;
    private final ChartRenderer<ProcessedPieChartData> pieChartRenderer;
    private final ChartRenderer<ProcessedTimeChartData> timeChartRenderer;

    public DashboardPresenter(DashboardViewModel viewModel, ChartRenderer<ProcessedPieChartData> pieChartRenderer,
                              ChartRenderer<ProcessedTimeChartData> timeChartRenderer) {
        this.viewModel = viewModel;
        this.pieChartRenderer = pieChartRenderer;
        this.timeChartRenderer = timeChartRenderer;
    }

    @Override
    public void present(LoadDashboardOutputData outputData) throws Exception {
        final List<Image> images = new ArrayList<>();
        images.add(pieChartRenderer.render(outputData.getPieChartData()));
        images.add(timeChartRenderer.render(outputData.getTimeChartData()));

        final DashboardState state = viewModel.getState();
        state.setChartImages(images);

        this.viewModel.setState(state);
        this.viewModel.firePropertyChange();
    }

}
