package use_case.load_dashboard;

/**
 * Output Data for the Load Dashboard Use Case.
 */
public class LoadDashboardOutputData {
    private final TimeChartData timeChartData;
    private final PieChartData pieChartData;

    public LoadDashboardOutputData(TimeChartData timeChartData,
                                   PieChartData pieChartData) {
        this.timeChartData = timeChartData;
        this.pieChartData = pieChartData;
    }

    public PieChartData getPieChartData() {
        return pieChartData;
    }

    public TimeChartData getTimeChartData() {
        return timeChartData;
    }
}
