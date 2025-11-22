package charts;

import java.util.List;

public class ProcessedTimeChartData extends AbstractProcessedChartData{
    private final List<DataPoint> dataPoints;

    public ProcessedTimeChartData(String chartName, List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public List<DataPoint> getDataPoints() {return dataPoints;}

    public static class DataPoint {
        private final String label;
        private final double income;
        private final double expense;

        public DataPoint(String label, double income, double expense) {
            this.label = label;
            this.income = income;
            this.expense = expense;
        }

        public String getLabel() {return label;}
        public double getIncome() {return income;}
        public double getExpense() {return expense;}
    }
}
