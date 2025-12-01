package use_case.load_dashboard;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import charts.ProcessedPieChartData;
import charts.ProcessedTimeChartData;
import charts.ProcessedTimeChartData.DataPoint;
import entity.Transaction;

/**
 * Interactor for the Load Dashboard Use Case.
 */
public class LoadDashboardInteractor implements LoadDashboardInputBoundary {
    private final LoadDashboardOutputBoundary presenter;
    private final LoadDashboardDataAccessInterface transactionDataAccessObject;

    public LoadDashboardInteractor(LoadDashboardOutputBoundary presenter,
                                   LoadDashboardDataAccessInterface transactionDataAccessObject) {
        this.presenter = presenter;
        this.transactionDataAccessObject = transactionDataAccessObject;
    }

    @Override
    public void execute(LoadDashboardInputData inputData) {
        final TimeRange timeRange = inputData.getTimeRange();
        final LocalDate currentDate = inputData.getCurrentDate();

        final List<Transaction> pieRawData = transactionDataAccessObject.getByDateRange(inputData.getStartDate(),
                inputData.getEndDate());
        final List<Transaction> timeRawData = transactionDataAccessObject.getByDateRange(currentDate,
                inputData.getCurrentDate().minusMonths(timeRange.getValue()));

        final ProcessedPieChartData pieChartData = processPieChartData(pieRawData);
        final ProcessedTimeChartData timeChartData = processTimeChartData(timeRawData, timeRange);

        final LoadDashboardOutputData outputData = new LoadDashboardOutputData(timeChartData, pieChartData);
        presenter.present(outputData);
    }

    private ProcessedPieChartData processPieChartData(List<Transaction> transactions) {
        final Map<String, Double> categoryValues = transactions.stream()
                .filter(transaction -> transaction.getAmount() < 0)
                // filter for expenses only
                .collect(Collectors.groupingBy(
                        transaction -> transactionDataAccessObject.getSourceCategory(transaction.getSource()).getName(),
                        Collectors.summingDouble(transaction -> Math.abs(transaction.getAmount()))
        ));
        return new ProcessedPieChartData(categoryValues);
    }

    private ProcessedTimeChartData processTimeChartData(List<Transaction> transactions, TimeRange timeRange) {
        final int monthRange = timeRange.getValue();

        final List<String> labels = new ArrayList<>();
        final Map<String, Integer> labelToIndex = new HashMap<>();

        final SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
        final Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MONTH, -(monthRange - 1));

        for (int i = 0; i < monthRange; i++) {
            final String label = monthFormat.format(calendar.getTime());
            labels.add(label);
            labelToIndex.put(label, i);
            calendar.add(Calendar.MONTH, 1);
        }

        final double[] incomeArray = new double[monthRange];
        final double[] expenseArray = new double[monthRange];

        for (Transaction transaction : transactions) {
            final String bucketLabel = monthFormat.format(transaction.getDate());
            if (labelToIndex.containsKey(bucketLabel)) {
                final int index = labelToIndex.get(bucketLabel);

                if (transaction.getAmount() > 0) {
                    incomeArray[index] += transaction.getAmount();
                }
                else {
                    expenseArray[index] += transaction.getAmount();
                }
            }
        }

        final List<DataPoint> dataPoints = new ArrayList<>();
        for (int i = 0; i < monthRange; i++) {
            dataPoints.add(new DataPoint(labels.get(i), incomeArray[i], expenseArray[i]));
        }

        return new ProcessedTimeChartData(dataPoints);
    }
}
