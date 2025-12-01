package interface_adapter.dashboard;

import java.time.LocalDate;

import use_case.load_dashboard.LoadDashboardInputBoundary;
import use_case.load_dashboard.LoadDashboardInputData;
import use_case.load_dashboard.TimeRange;

/**
 * Controller for the Dashboard Use Case.
 */
public class DashboardController {
    private final LoadDashboardInputBoundary loadDashboardInteractor;

    public DashboardController(LoadDashboardInputBoundary loadDashboardInteractor) {
        this.loadDashboardInteractor = loadDashboardInteractor;
    }

    /**
     * Executes Load Dashboard Use Case.
     * @param currentDate The current date.
     * @param timeRange Time Chart timeRange enum.
     * @param startDate Pie Chart start date.
     * @param endDate Pie Chart end date.
     */
    public void loadDashboard(LocalDate currentDate, TimeRange timeRange, LocalDate startDate, LocalDate endDate) {
        final LoadDashboardInputData input = new LoadDashboardInputData(currentDate, timeRange, startDate, endDate);
        loadDashboardInteractor.execute(input);
    }
}
