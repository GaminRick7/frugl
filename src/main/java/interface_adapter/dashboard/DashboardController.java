package interface_adapter.dashboard;

import use_case.load_dashboard.LoadDashboardInputBoundary;
import use_case.load_dashboard.LoadDashboardInputData;
import use_case.load_dashboard.TimeRange;

import java.time.LocalDate;

public class DashboardController {
    private final LoadDashboardInputBoundary useCase;

    public DashboardController(LoadDashboardInputBoundary useCase) {
        this.useCase = useCase;
    }

    public void loadDashboard(LocalDate currentDate, TimeRange timeRange, LocalDate startDate, LocalDate endDate) {
        LoadDashboardInputData input = new LoadDashboardInputData(currentDate, timeRange, startDate, endDate);
        useCase.execute(input);
    }
}
