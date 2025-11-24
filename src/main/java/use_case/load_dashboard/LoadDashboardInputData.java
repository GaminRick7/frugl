package use_case.load_dashboard;

import java.time.LocalDate;

public class LoadDashboardInputData {
    private final LocalDate currentDate;
    private final TimeRange timeRange;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public LoadDashboardInputData(LocalDate currentDate, TimeRange timeRange, LocalDate startDate, LocalDate endDate) {
        this.currentDate = currentDate;
        this.timeRange = timeRange;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getCurrentDate() {return currentDate;}
    public TimeRange getTimeRange() {return timeRange;}
    public LocalDate getStartDate() {return startDate;}
    public LocalDate getEndDate() {return endDate;}
}
