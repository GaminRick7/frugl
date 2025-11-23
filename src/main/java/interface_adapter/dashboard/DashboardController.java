package interface_adapter.dashboard;

import use_case.load_dashboard.LoadDashboardInputBoundary;
import use_case.load_dashboard.LoadDashboardInputData;
import use_case.load_dashboard.TimeRange;

public class DashboardController {
    private final LoadDashboardInputBoundary useCase;

    public DashboardController(LoadDashboardInputBoundary useCase) {
        this.useCase = useCase;
    }

    public void loadDashboard(TimeRange timeRange){
        LoadDashboardInputData input = new LoadDashboardInputData(timeRange);
        useCase.execute(input);
    }
}
