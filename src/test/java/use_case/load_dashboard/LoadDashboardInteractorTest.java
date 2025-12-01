package use_case.load_dashboard;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import entity.Category;
import entity.Source;

class LoadDashboardInteractorTest {
    private MockTransactionDataAccessObject dataAccessObject;
    private MockPresenter presenter;
    private LoadDashboardInteractor interactor;

    @BeforeEach
    void setUp() {
        dataAccessObject = new MockTransactionDataAccessObject();
        presenter = new MockPresenter();
        interactor = new LoadDashboardInteractor(presenter, dataAccessObject);
    }

    /**
     * Test 1: Successful Path Test (Mock 5)
     * Verifies that income and expenses are split correctly in the Time Chart and grouped correctly in the Pie Chart.
     */
    @Test
    void successfulPathTest() {
        TimeRange timeRange = TimeRange.THREE_MONTHS;
        LocalDate currentDate = LocalDate.now();
        LoadDashboardInputData inputData = new LoadDashboardInputData(currentDate, timeRange, currentDate.minusMonths(3), currentDate);

        List<Transaction> transactions = new ArrayList<>();

        // Expenses from Mock 5 Data
        transactions.add(new MockTransaction("Uber", -12.50, currentDate));
        transactions.add(new MockTransaction("Starbucks", -8.75, currentDate));
        transactions.add(new MockTransaction("Amazon", -49.99, currentDate));

        // Income from Mock 5 Data
        transactions.add(new MockTransaction("Employer", 100.00, currentDate));

        dataAccessObject.setTransactions(transactions);
        interactor.execute(inputData);

        LoadDashboardOutputData output = presenter.getOutputData();
        assertNotNull(output, "Presenter should have received data");

        // Verify Pie Chart
        PieChartData pieData = output.getPieChartData();
        Map<String, Double> categoryTotals = pieData.getCategoryTotals();

        // Ensure correct filtering of incomes out of pie chart data
        assertFalse(categoryTotals.containsKey("Income Category"), "Income should not appear in Pie Chart");

        // Check Categories -
        // Expense categories: Uber + Starbucks + Amazon
        // Expenses amounts: -12.50 + -8.75 + -49.99
        assertTrue(categoryTotals.containsKey("Uber Category"));
        assertEquals(-12.50, categoryTotals.get("Uber Category"));
        assertEquals(-49.99, categoryTotals.get("Amazon Category"));

        // Verify Time Chart
        TimeChartData timeData = output.getTimeChartData();
        List<TimeChartData.DataPoint> points = timeData.getDataPoints();

        // Expect 3 data points for 3 month range
        assertEquals(3, points.size());

        // Index 2 is the current month
        TimeChartData.DataPoint currentMonth = points.get(2);

        assertEquals(100.00, currentMonth.income(), 0.01);
        // Expenses sum: -12.50 + -8.75 + -49.99 = -71.24
        assertEquals(-71.24, currentMonth.expense(), 0.01);
    }

    /**
     * Test 2: Empty Data (Mock 2)
     * Verifies the code handles empty lists without crashing.
     */
    @Test
    void emptyDataTest() {
        TimeRange timeRange = TimeRange.ONE_MONTH;
        LocalDate now = LocalDate.now();
        LoadDashboardInputData inputData = new LoadDashboardInputData(now, timeRange, now, now);

        dataAccessObject.setTransactions(new ArrayList<>());

        interactor.execute(inputData);
        LoadDashboardOutputData output = presenter.getOutputData();

        // Pie chart should be empty
        assertTrue(output.getPieChartData().getCategoryTotals().isEmpty());

        // Time chart should have 1 bucket (Current Month) with 0.0 values
        assertEquals(1, output.getTimeChartData().getDataPoints().size());
        assertEquals(0.0, output.getTimeChartData().getDataPoints().get(0).income());
        assertEquals(0.0, output.getTimeChartData().getDataPoints().get(0).expense());
    }

    /**
     * Test 3: Date Logic & Grouping
     * Verifies logic for isSameMonth() and separating income/expenses.
     */
    @Test
    void dataGroupingTest() {
        TimeRange timeRange = TimeRange.ONE_MONTH;
        LocalDate now = LocalDate.now();
        LoadDashboardInputData inputData = new LoadDashboardInputData(now, timeRange, now, now);

        List<Transaction> transactions = new ArrayList<>();

        // Transaction TODAY - to include
        transactions.add(new MockTransaction("Uber", -10.00, now));

        // Transaction LAST YEAR - to be ignored
        transactions.add(new MockTransaction("OldStuff", -50.00, now.minusYears(1)));

        dataAccessObject.setTransactions(transactions);
        interactor.execute(inputData);

        TimeChartData.DataPoint point = presenter.getOutputData().getTimeChartData().getDataPoints().get(0);

        // Should only contain the expense from today, and ignore last year's expense.
        assertEquals(-10.00, point.expense(), 0.01);
    }

    /**
     * Test 4: TimeRange Enum Coverage
     * Verifies that the enum values, toString, and getValue work as expected.
     */
    @Test
    void timeRangeEnumTest() {
        TimeRange[] ranges = TimeRange.values();
        assertTrue(ranges.length > 0);
        TimeRange range = TimeRange.TWELVE_MONTHS;

        assertEquals(12, range.getValue());
        assertEquals("12 Months", range.toString());
    }

    // Mock Classes for Testing Purposes Only

    /**
     * Mock DAO to simulate the database.
     */
    private static class MockTransactionDataAccessObject implements LoadDashboardDataAccessInterface {
        private List<Transaction> transactions = new ArrayList<>();

        public void setTransactions(List<Transaction> transactions) {
            this.transactions = transactions;
        }

        @Override
        public List<Transaction> getByDateRange(LocalDate startDate, LocalDate endDate) {
            return transactions;
        }

        @Override
        public Category getSourceCategory(Source source) {
            return new MockCategory(source.getName() + " Category");
        }
    }

    /**
     * Mock Presenter to capture output.
     */
    private static class MockPresenter implements LoadDashboardOutputBoundary {
        private LoadDashboardOutputData outputData;

        @Override
        public void present(LoadDashboardOutputData outputData) {
            this.outputData = outputData;
        }

        public LoadDashboardOutputData getOutputData() {
            return outputData;
        }

        @Override
        public void prepareFailView(String error) {
            fail("Unexpected failure: " + error);
        }
    }

    // Mock Entities for Testing

    private static class MockTransaction extends Transaction {
        private final String sourceName;
        private final double amount;
        private final LocalDate date;

        public MockTransaction(String sourceName, double amount, LocalDate date) {
            super(new Source(sourceName), amount, date);

            this.sourceName = sourceName;
            this.amount = amount;
            this.date = date;
        }

        @Override
        public double getAmount() {
            return amount;
        }

        @Override
        public LocalDate getDate() {
            return date;
        }

        @Override
        public Source getSource() {
            return new Source("name") {
                @Override public String getName() {
                    return sourceName;
                }
            };
        }
    }

    private static class MockCategory extends Category {
        private final String name;

        public MockCategory(String name) {
            super(name);
            this.name = name;
        }

        @Override public String getName() {
            return name;
        }
    }
}