package use_case.load_dashboard;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        final List<Transaction> timeRawData = transactionDataAccessObject.getByDateRange(
                currentDate.minusMonths(timeRange.getValue()), currentDate);

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
                        Collectors.summingDouble(Transaction::getAmount)
        ));
        return new ProcessedPieChartData(categoryValues);
    }

    private ProcessedTimeChartData processTimeChartData(List<Transaction> transactions, TimeRange timeRange) {
        final int monthRange = timeRange.getValue();
        final List<DataPoint> dataPoints = new ArrayList<>();

        final DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MMM");
        final LocalDate today = LocalDate.now();

        for (int i = monthRange - 1; i >= 0; i--) {
            final LocalDate stepDate = today.minusMonths(i);
            final String label = stepDate.format(monthFormat);

            final double incomeSum = transactions.stream()
                    .filter(transaction -> isSameMonth(transaction.getDate(), stepDate))
                    .filter(transaction -> transaction.getAmount() > 0)
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            final double expenseSum = transactions.stream()
                    .filter(transaction -> isSameMonth(transaction.getDate(), stepDate))
                    .filter(transaction -> transaction.getAmount() < 0)
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            dataPoints.add(new DataPoint(label, incomeSum, expenseSum));
        }
        return new ProcessedTimeChartData(dataPoints);
    }

    private boolean isSameMonth(LocalDate date1, LocalDate date2) {
        return date1.getYear() == date2.getYear() && date1.getMonth() == date2.getMonth();
    }
}
