package charts;

import java.util.List;

public class ProcessedTimeChartData extends AbstractProcessedChartData{
    private final List<String> labels;
    private final List<Double> incomeValues;
    private final List<Double> expenseValues;

    public ProcessedTimeChartData(List<String> labels, List<Double> incomes, List<Double> expenses) {
        this.labels = labels;
        this.incomeValues = incomes;
        this.expenseValues = expenses;
    }

    public List<String> getLabels() {return labels;}
    public List<Double> getIncomeValues() {return incomeValues;}
    public List<Double> getExpenseValues() {return expenseValues;}
}
