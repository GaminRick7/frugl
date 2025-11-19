package use_case.load_dashboard;

public class LoadDashboardInputData {
    private final TimeRange timeRange;

    public LoadDashboardInputData(TimeRange timeRange){
        this.timeRange = timeRange;
    }

    public TimeRange getTimeRange() {return timeRange;}
}
