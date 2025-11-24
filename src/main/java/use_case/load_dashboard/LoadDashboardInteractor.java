package use_case.load_dashboard;

import charts.ProcessedPieChartData;
import charts.ProcessedTimeChartData;
import data_access.TransactionDataAccessObject;
import entity.Transaction;
import charts.ProcessedTimeChartData.DataPoint;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class LoadDashboardInteractor implements LoadDashboardInputBoundary {
    private final LoadDashboardOutputBoundary presenter;
    private final TransactionDataAccessObject transactionDOA;

    public LoadDashboardInteractor(LoadDashboardOutputBoundary presenter,  TransactionDataAccessObject transactionDOA) {
        this.presenter = presenter;
        this.transactionDOA = transactionDOA;
    }

    @Override
    public void execute(LoadDashboardInputData inputData) {
        TimeRange timeRange = inputData.getTimeRange();

//        List<Transaction> pieRawData = transactionDOA.getMonthlyTransactionData(); //TODO add method in DOA
//        List<Transaction> timeRawData = transactionDOA.getTimeRangeTransactions(timeRange); //TODO add method in DOA
//
//        ProcessedPieChartData pieChartData = processPieChartData(pieRawData);
//        ProcessedTimeChartData timeChartData = processTimeChartData(timeRawData, timeRange);
//
//        LoadDashboardOutputData outputData = new LoadDashboardOutputData(timeChartData, pieChartData);
//        presenter.present(outputData);
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
        int monthRange = 0;
        switch (timeRange) {
            case THREE_MONTHS: monthRange = 3; break;
            case SIX_MONTHS: monthRange = 6; break;
            case TWELVE_MONTHS: monthRange = 12; break;
            default: monthRange = 1;
        }

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
