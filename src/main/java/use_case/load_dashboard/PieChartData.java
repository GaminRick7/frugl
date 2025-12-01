package use_case.load_dashboard;

import java.util.Map;

/**
 * Collects pie chart data that is processed from the transactions.
 */
public class PieChartData implements ChartData {
    private final Map<String, Double> categoryTotals;

    public PieChartData(Map<String, Double> categoryTotals) {
        this.categoryTotals = categoryTotals;
    }

    public Map<String, Double> getCategoryTotals() {
        return categoryTotals;
    }
}
