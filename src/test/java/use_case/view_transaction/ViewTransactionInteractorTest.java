package use_case.view_transaction;

import entity.Transaction;
import entity.Source;
import entity.Category;
import org.junit.jupiter.api.Test;
import use_case.view_transactions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class ViewTransactionInteractorTest {

    //creating a mock DAO
    class MockTransactionDAO implements ViewTransactionDataAccessInterface {

        @Override
        public List<Transaction> getByDateRange(LocalDate startDate, LocalDate endDate) {
            if (startDate.getMonthValue() == 1){

                return getJanuaryTransactions();
            }

            if (startDate.getMonthValue() == 2){

                return getFebruaryTransactions();
            }

            if (startDate.getMonthValue() == 3){
                return getMarchTransactions();

            }
            return null;

        }

        @Override
        public Category getSourceCategory(Source source) {

            if (source.getName() == "Tech Corp") {
                return new Category("income");
            }
            if (source.getName() == "GoodLife Fitness") {
                return new Category("fitness");
            }
            if (source.getName() == "Loblaws") {
                return new Category("food");
            }
            if (source.getName() == "Fancy Restaurant") {
                return new Category("food");
            }
            if (source.getName() == "Uber") {
                return new Category("transportation");
            }

            return null;
        }


        //returining the hardcoded transactionValues
        private List<Transaction> getJanuaryTransactions() {
            List<Transaction> list = new ArrayList<>();

            Source salarySource = new Source("Tech Corp");
            list.add(new Transaction(salarySource, 5000.00, LocalDate.of(2025, 1, 15)));

            Source gymSource = new Source("GoodLife Fitness");
            list.add(new Transaction(gymSource, -60.00, LocalDate.of(2025, 1, 2)));

            // 3. Expense (Regular)
            Source grocerySource = new Source("Loblaws");
            list.add(new Transaction(grocerySource, -120.50, LocalDate.of(2025, 1, 20)));

            return list;
        }

        private List<Transaction> getFebruaryTransactions() {
            List<Transaction> list = new ArrayList<>();

            Source salarySource = new Source("Tech Corp");
            list.add(new Transaction(salarySource, 5000.00, LocalDate.of(2025, 2, 15)));

            Source dinnerSource = new Source("Fancy Restaurant");
            list.add(new Transaction(dinnerSource, -200.00, LocalDate.of(2025, 2, 14)));

            Source uberSource = new Source("Uber");
            list.add(new Transaction(uberSource, -35.75, LocalDate.of(2025, 2, 28)));

            return list;
        }

        private List<Transaction> getMarchTransactions() {

            return null;
        }

    }

    //Success test comes from having valid transaction
    @Test
    void successTestJan(){
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 30);

        ViewTransactionInputData inputData = new ViewTransactionInputData(startDate, endDate);
        ViewTransactionDataAccessInterface viewTransactionDAO = new MockTransactionDAO();

        ViewTransactionOutputBoundary successPresenter = new ViewTransactionOutputBoundary() {
            @Override
            public void prepareSuccessView(ViewTransactionOutputData output) {
                assertNotNull(output);
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }
        };
        ViewTransactionInteractor interactor = new ViewTransactionInteractor(viewTransactionDAO,successPresenter );
        interactor.execute(inputData);
    }

    @Test
    void successTestFeb(){
        LocalDate startDate = LocalDate.of(2025, 2, 1);
        LocalDate endDate = LocalDate.of(2025, 2, 28);

        ViewTransactionInputData inputData = new ViewTransactionInputData(startDate, endDate);
        ViewTransactionDataAccessInterface viewTransactionDAO = new MockTransactionDAO();

        ViewTransactionOutputBoundary successPresenter = new ViewTransactionOutputBoundary() {
            @Override
            public void prepareSuccessView(ViewTransactionOutputData output) {
                assertNotNull(output);
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }
        };
        ViewTransactionInteractor interactor = new ViewTransactionInteractor(viewTransactionDAO,successPresenter );
        interactor.execute(inputData);
    }

    @Test
    void FailTestMarch(){
        LocalDate startDate = LocalDate.of(2025, 3, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 30);

        ViewTransactionInputData inputData = new ViewTransactionInputData(startDate, endDate);
        ViewTransactionDataAccessInterface viewTransactionDAO = new MockTransactionDAO();

        ViewTransactionOutputBoundary successPresenter = new ViewTransactionOutputBoundary() {
            @Override
            public void prepareSuccessView(ViewTransactionOutputData output) {
                assertNotNull(output);
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("No data available.", error);
            }
        };
        ViewTransactionInteractor interactor = new ViewTransactionInteractor(viewTransactionDAO,successPresenter );
        interactor.execute(inputData);
    }

}
