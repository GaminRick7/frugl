package use_case.viewTransaction;

import entity.Transaction;
import entity.Source;
import entity.Category;
import org.junit.Test;
import use_case.view_transactions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class ViewTransactionInteractorTest {

    @Test
    public void successTest_NonEmptyTransaction() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        ViewTransactionInputData inputData = new ViewTransactionInputData(startDate, endDate);

        ViewTransactionDataAccessInterface viewTransDAO = new ViewTransactionDataAccessInterface() {
            @Override
            public List<Transaction> getByDateRange(LocalDate start, LocalDate end) {
                List<Transaction> transactions = new ArrayList<>();
                transactions.add(new Transaction(
                        new Source("Uber"),
                        -23.00,
                        LocalDate.of(2025, 1, 5)
                ));
                transactions.add(new Transaction(
                        new Source("Salary"),
                        2500.00,
                        LocalDate.of(2025, 1, 15)
                ));
                return transactions;
            }

            @Override
            public Category getSourceCategory(Source source) {
                if (source.getName().equals("Uber")) {
                    return new Category("Transport");
                } else if (source.getName().equals("Salary")) {
                    return new Category("Income");
                } else {
                    return new Category("Food");
                }
            }
        };

        ViewTransactionOutputBoundary mockPresenter = new ViewTransactionOutputBoundary() {
            @Override
            public void prepareSuccessView(ViewTransactionOutputData outputData) {
                assertEquals("2025-01", outputData.getYearMonth());
                assertEquals(3, outputData.getMonthTransactions().size());

                HashMap<String, Object> firstTrans = outputData.getTransactionByIndex(0);
                assertEquals("Uber", firstTrans.get("source"));
                assertEquals("-23.0", firstTrans.get("amount"));
                assertEquals("Transport", firstTrans.get("category"));
                assertEquals(LocalDate.of(2025, 1, 5), firstTrans.get("date"));

                HashMap<String, Object> secondTrans = outputData.getTransactionByIndex(1);
                assertEquals("Salary", secondTrans.get("source"));
                assertEquals("2500.0", secondTrans.get("amount"));
                assertEquals("Income", secondTrans.get("category"));
            }

            @Override
            public void prepareFailView(String error) {
                fail("Error: " + error);
            }
        };

        ViewTransactionInputBoundary interactor = new ViewTransactionInteractor(viewTransDAO, mockPresenter);
        interactor.execute(inputData);
    }

    @Test
    public void failTest_NoTransactions() {

        LocalDate startDate = LocalDate.of(2025, 3, 1);
        LocalDate endDate = LocalDate.of(2025, 3, 31);
        ViewTransactionInputData inputData = new ViewTransactionInputData(startDate, endDate);

        ViewTransactionDataAccessInterface mockDAO = new ViewTransactionDataAccessInterface() {
            @Override
            public List<Transaction> getByDateRange(LocalDate start, LocalDate end) {
                assertEquals(startDate, start);
                assertEquals(endDate, end);
                return new ArrayList<>(); // No transactions
            }

            @Override
            public Category getSourceCategory(Source source) {
                fail("Should not be called when there are no transactions");
                return null;
            }
        };

        ViewTransactionOutputBoundary viewTransPresenter = new ViewTransactionOutputBoundary() {
            @Override
            public void prepareSuccessView(ViewTransactionOutputData outputData) {
                fail("Should call when no transactions exist");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("No data available.", error);
            }
        };
        ViewTransactionInputBoundary interactor = new ViewTransactionInteractor(mockDAO, viewTransPresenter);
        interactor.execute(inputData);
    }

    @Test
    public void successTest_SingleTransaction() {
        // Test edge case with only one transaction
        LocalDate startDate = LocalDate.of(2025, 2, 1);
        LocalDate endDate = LocalDate.of(2025, 2, 28);
        ViewTransactionInputData inputData = new ViewTransactionInputData(startDate, endDate);

        ViewTransactionDataAccessInterface mockDAO = new ViewTransactionDataAccessInterface() {
            @Override
            public List<Transaction> getByDateRange(LocalDate start, LocalDate end) {
                List<Transaction> transactions = new ArrayList<>();
                transactions.add(new Transaction(
                        new Source("Rent"),
                        -1200.00,
                        LocalDate.of(2025, 2, 1)
                ));
                return transactions;
            }

            @Override
            public Category getSourceCategory(Source source) {
                return new Category("Housing");
            }
        };

        ViewTransactionOutputBoundary mockPresenter = new ViewTransactionOutputBoundary() {
            @Override
            public void prepareSuccessView(ViewTransactionOutputData outputData) {
                assertEquals("2025-02", outputData.getYearMonth());
                assertEquals(1, outputData.getMonthTransactions().size());

                HashMap<String, Object> transaction = outputData.getTransactionByIndex(0);
                assertEquals("Rent", transaction.get("source"));
                assertEquals("-1200.0", transaction.get("amount"));
                assertEquals("Housing", transaction.get("category"));
            }

            @Override
            public void prepareFailView(String error) {
                fail("Should work with one transaction");
            }
        };

        ViewTransactionInputBoundary interactor = new ViewTransactionInteractor(mockDAO, mockPresenter);
        interactor.execute(inputData);
    }
}
