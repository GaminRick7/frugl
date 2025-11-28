package use_case.view_transactions;

import entity.Category;
import entity.Source;
import entity.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MockTransactionDAO implements ViewTransactionDataAccessInterface {

    @Override
    public List<Transaction> getByDateRange(LocalDate startDate, LocalDate endDate) {
        // 1. LOGGING: This proves the Controller -> Interactor -> DAO connection worked!
        System.out.println("DAO: getByDateRange called for " + startDate + " to " + endDate);

        List<Transaction> transactions = new ArrayList<>();

        // Use the startDate from your Controller to figure out what data to show
        int year = startDate.getYear();
        int month = startDate.getMonthValue();

        // SCENARIO 1: January 2025 -> Return Empty List (Simulate "No Data")
        if (year == 2025 && month == 1) {
            System.out.println("DAO: Returning empty list for Jan 2025.");
            return transactions;
        }

        // SCENARIO 2: Other months in 2025 -> Return Fixed Data
        if (year == 2025) {
            System.out.println("DAO: Generating fixed data for 2025.");
            return generateFixedDataForMonth(year, month);
        }

        // SCENARIO 3: 2023 or 2024 -> Return Random Data
        if (year == 2023 || year == 2024) {
            System.out.println("DAO: Generating random data.");
            return generateRandomDataForMonth(year, month);
        }

        return transactions;
    }

    // Helper to generate consistent data for testing
    private List<Transaction> generateFixedDataForMonth(int year, int month) {
        List<Transaction> list = new ArrayList<>();
        String[] monthNames = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        // 1. Income Transaction
        LocalDate date1 = LocalDate.of(year, month, 15);
        Source source1 = new Source("Monthly Salary", new Category("Income", ""));
        list.add(new Transaction(source1, 3000.00 + (month * 10), date1));

        // 2. Expense Transaction
        LocalDate date2 = LocalDate.of(year, month, 20);
        Source source2 = new Source(monthNames[month] + " Grocery Run", new Category("Food", ""));
        list.add(new Transaction(source2, -150.00, date2));

        return list;
    }

    // Helper to generate random data
    private List<Transaction> generateRandomDataForMonth(int year, int month) {
        List<Transaction> list = new ArrayList<>();
        Random rand = new Random();
        int numTransactions = rand.nextInt(5) + 1;

        for (int i = 0; i < numTransactions; i++) {
            int day = rand.nextInt(28) + 1; // Avoid end-of-month complexity for mock
            LocalDate date = LocalDate.of(year, month, day);

            boolean isExpense = rand.nextBoolean();
            String categoryStr = isExpense ? "Shopping" : "Income";
            double amount = (rand.nextDouble() * 100);
            if (isExpense) amount *= -1;

            Source source = new Source("Random Store " + rand.nextInt(100), new Category(categoryStr, ""));
            list.add(new Transaction(source, amount, date));
        }
        return list;
    }
}