package charts;

import java.util.Map;

public class ProcessedPieChartData extends AbstractProcessedChartData{
    private final Map<String, Double> categoryTotals;

    public ProcessedPieChartData(Map<String, Double> categoryTotals) {
        this.categoryTotals = categoryTotals;
    }

    public Map<String, Double> getCategoryTotals() {
        return categoryTotals;
    }
}
