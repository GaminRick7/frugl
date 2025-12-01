package use_case.load_dashboard;

import charts.ProcessedPieChartData;
import charts.ProcessedTimeChartData;

/**
 * Output Data for the Load Dashboard Use Case.
 */
public class LoadDashboardOutputData {
    private final ProcessedTimeChartData timeChartData;
    private final ProcessedPieChartData pieChartData;

    public LoadDashboardOutputData(ProcessedTimeChartData processedTimeChartData,
                                   ProcessedPieChartData processedPieChartData) {
        this.timeChartData = processedTimeChartData;
        this.pieChartData = processedPieChartData;
    }

    public ProcessedPieChartData getPieChartData() {
        return pieChartData;
    }

    public ProcessedTimeChartData getTimeChartData() {
        return timeChartData;
    }
}
