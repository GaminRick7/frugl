package use_case.view_transactions;
import entity.Source;
import entity.Category;

import entity.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Inside your TransactionsViewTest.java file

 class MockTransactionDAO implements ViewTransactionDataAccessInterface {

    @Override
    public List<Transaction> getByDateRange(LocalDate start, LocalDate end) {
        // 1. Create the list we will return
        List<Transaction> result = new ArrayList<>();

        // 2. HAND-CODE LOGIC: Based on the requested start date

        // CASE A: Requesting January 2025 (FORCE EMPTY)
        if (start.getMonthValue() == 1 && start.getYear() == 2025) {
            return result; // Empty list -> "No Data" error
        }

        // CASE B: Requesting November 2025 (FORCE DATA)
        if (start.getMonthValue() == 11 && start.getYear() == 2025) {

            // Create helpers (Sources/Categories) locally
            Category food = new Category("Food", "Dining");
            Category transport = new Category("Transportation", "Rides");
            Category income = new Category("Income", "Salary");

            Source starbucks = new Source("Starbucks", food);
            Source uber = new Source("Uber", transport);
            Source job = new Source("Employer", income);

            // Add transactions manually
            result.add(new Transaction(starbucks, 15.50, LocalDate.of(2025, 11, 1)));
            result.add(new Transaction(uber, 45.20, LocalDate.of(2025, 11, 5)));
            result.add(new Transaction(job, 3500.00, LocalDate.of(2025, 11, 15)));
        }

        // CASE C: Requesting April 2023 (FORCE DATA)
        if (start.getMonthValue() == 4 && start.getYear() == 2023) {
            Category shopping = new Category("Shopping", "Online");
            Source amazon = new Source("Amazon", shopping);

            result.add(new Transaction(amazon, 99.99, LocalDate.of(2023, 4, 10)));
        }

        return result;
    }
}


//// --- FORCE DATA Mock DAO ---
//class MockTransactionDAO implements ViewTransactionDataAccessInterface {
//    // In TransactionsViewTest.java -> MockTransactionDAO
//
//    @Override
//    public List<Transaction> getByDateRange(LocalDate start, LocalDate end) {
//        System.out.println("DAO: Request for " + start);
//        List<Transaction> result = new ArrayList<>();
//
//        // Case 1: January 2025 -> Return EMPTY list (Simulate Failure)
//        if (start.getMonthValue() == 1 && start.getYear() == 2025) {
//            System.out.println("DAO: Returning EMPTY list for Jan 2025");
//            return result;
//        }
//
//        // Case 2: All other dates -> Return FAKE data (Simulate Success)
//        System.out.println("DAO: Returning FAKE data");
//        Category food = new Category("Food", "Dining");
//        Source starbucks = new Source("Starbucks", food);
//
//        result.add(new Transaction(starbucks, 10.00, start));
//        result.add(new Transaction(starbucks, 20.00, start.plusDays(1)));
//
//        return result;
//    }
//}