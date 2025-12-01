package charts;

import java.util.Map;

/**
 * Collects pie chart data that is processed from the transactions.
 */
public class ProcessedPieChartData implements AbstractProcessedChartData {
    private final Map<String, Double> categoryTotals;

    public ProcessedPieChartData(Map<String, Double> categoryTotals) {
        this.categoryTotals = categoryTotals;
    }

    public Map<String, Double> getCategoryTotals() {
        return categoryTotals;
    }
}
