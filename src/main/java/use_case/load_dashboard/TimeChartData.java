package use_case.load_dashboard;

import java.util.List;

/**
 * Collects time chart data that is processed from the transactions.
 */
public class TimeChartData implements ChartData {
    private final List<DataPoint> dataPoints;

    public TimeChartData(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    /**
     * A record representing a single point in the time chart.
     *
     * @param label The label of the data point as the name of the month.
     * @param income The total income of the data point for a given month.
     * @param expense The total expenses of the data point for a given month.
     */
    public record DataPoint(String label, double income, double expense) {
    }
}
