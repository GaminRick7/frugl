package use_case.load_dashboard;

import charts.ProcessedPieChartData;
import charts.ProcessedTimeChartData;
import entity.Transaction;
import charts.ProcessedTimeChartData.DataPoint;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class LoadDashboardInteractor implements LoadDashboardInputBoundary {
    private final LoadDashboardOutputBoundary presenter;
    private final LoadDashboardDataAccessInterface transactionDOA;

    public LoadDashboardInteractor(LoadDashboardOutputBoundary presenter,  LoadDashboardDataAccessInterface transactionDOA) {
        this.presenter = presenter;
        this.transactionDOA = transactionDOA;
    }

    @Override
    public void execute(LoadDashboardInputData inputData) {
        TimeRange timeRange = inputData.getTimeRange();
        LocalDate currentDate = inputData.getCurrentDate();

        List<Transaction> pieRawData = transactionDOA.getByDateRange(inputData.getStartDate(), inputData.getEndDate());
        List<Transaction> timeRawData = transactionDOA.getByDateRange(currentDate,inputData.getCurrentDate().minusMonths(timeRange.getValue()));

        ProcessedPieChartData pieChartData = processPieChartData(pieRawData);
        ProcessedTimeChartData timeChartData = processTimeChartData(timeRawData, timeRange);

        LoadDashboardOutputData outputData = new LoadDashboardOutputData(timeChartData, pieChartData);
        presenter.present(outputData);
    }
    private ProcessedPieChartData processPieChartData(List<Transaction> transactions) {
        Map<String, Double> categoryValues = transactions.stream()
                .filter(t -> t.getAmount() < 0) //filter for expenses only
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.summingDouble(t -> Math.abs(t.getAmount()))
        ));
        return new ProcessedPieChartData(categoryValues);
    }

    private ProcessedTimeChartData processTimeChartData(List<Transaction> transactions, TimeRange timeRange) {
        int monthRange = timeRange.getValue();

        List<String> labels = new ArrayList<>();
        Map<String, Integer> labelToIndex = new HashMap<>();

        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MONTH, -(monthRange - 1));

        for (int i = 0; i < monthRange; i++) {
            String label = monthFormat.format(calendar.getTime());
            labels.add(label);
            labelToIndex.put(label, i);
            calendar.add(Calendar.MONTH, 1);
        }

        double[] incomeArray = new double[monthRange];
        double[] expenseArray = new double[monthRange];

        for (Transaction transaction : transactions) {
            String bucketLabel = monthFormat.format(transaction.getDate());
            if (labelToIndex.containsKey(bucketLabel)) {
                int index = labelToIndex.get(bucketLabel);

                if (transaction.getAmount() > 0) {
                    incomeArray[index] += transaction.getAmount();
                } else {
                    expenseArray[index] += transaction.getAmount();
                }
            }
        }

        List<DataPoint> dataPoints = new ArrayList<>();
        for (int i = 0; i < monthRange; i++) {
            dataPoints.add(new DataPoint(labels.get(i), incomeArray[i], expenseArray[i]));
        }

        return new ProcessedTimeChartData(timeRange.toString(), dataPoints);
    }
}
