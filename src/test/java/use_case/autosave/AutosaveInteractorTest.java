package use_case.autosave;

import data_access.GoalDataAccessObject;
import data_access.TransactionDataAccessObject;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class AutosaveInteractorTest {

    @Test
    public void successTest() {
        AutosaveInputData inputData = new AutosaveInputData();
        AutosaveDataAccessInterface transactionDataAccessObject = new TransactionDataAccessObject(
                "test_transactions.json", "test_source_categories.json");
        AutosaveDataAccessInterface goalDataAccessObject = new GoalDataAccessObject("test_goals.json");

        AutosaveOutputBoundary successPresenter = new AutosaveOutputBoundary() {

            @Override
            public void presentSuccess(AutosaveOutputData outputData) {
                assertEquals("Autosave completed successfully", outputData.getMessage());
                assertNotNull(outputData.getTimestamp());
            }

            @Override
            public void presentFailure(String errorMessage) {
                fail("Use case failure is unexpected.");
            }

        };

        AutosaveInputBoundary interactor = new AutosaveInteractor(transactionDataAccessObject, goalDataAccessObject, successPresenter);
        interactor.execute(inputData);
    }

    @Test
    public void failureTest() {
        AutosaveInputData inputData = new AutosaveInputData();
        AutosaveDataAccessInterface transactionDataAccessObject = new TransactionDataAccessObject(
                "test_transactions.json", "test_source_categories.json") {
            @Override
            public void save() {
                throw new RuntimeException("Test failure");
            }
        };
        AutosaveDataAccessInterface goalDataAccessObject = new GoalDataAccessObject("test_goals.json");

        AutosaveOutputBoundary failurePresenter = new AutosaveOutputBoundary() {

            @Override
            public void presentSuccess(AutosaveOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void presentFailure(String errorMessage) {
                assertEquals("Failed to save data: Test failure", errorMessage);
            }

        };

        AutosaveInputBoundary interactor = new AutosaveInteractor(transactionDataAccessObject, goalDataAccessObject, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    public void failureWithDifferentErrorMessageTest() {
        AutosaveInputData inputData = new AutosaveInputData();
        AutosaveDataAccessInterface transactionDataAccessObject = new TransactionDataAccessObject(
                "test_transactions.json", "test_source_categories.json") {
            @Override
            public void save() {
                throw new RuntimeException("Failed to save transactions");
            }
        };
        AutosaveDataAccessInterface goalDataAccessObject = new GoalDataAccessObject("test_goals.json");

        AutosaveOutputBoundary failurePresenter = new AutosaveOutputBoundary() {

            @Override
            public void presentSuccess(AutosaveOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void presentFailure(String errorMessage) {
                assertEquals("Failed to save data: Failed to save transactions", errorMessage);
            }

        };

        AutosaveInputBoundary interactor = new AutosaveInteractor(transactionDataAccessObject, goalDataAccessObject, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    public void failureWithNullExceptionMessageTest() {
        AutosaveInputData inputData = new AutosaveInputData();
        AutosaveDataAccessInterface transactionDataAccessObject = new TransactionDataAccessObject(
                "test_transactions.json", "test_source_categories.json") {
            @Override
            public void save() {
                throw new RuntimeException((String) null);
            }
        };
        AutosaveDataAccessInterface goalDataAccessObject = new GoalDataAccessObject("test_goals.json");

        AutosaveOutputBoundary failurePresenter = new AutosaveOutputBoundary() {

            @Override
            public void presentSuccess(AutosaveOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void presentFailure(String errorMessage) {
                assertEquals("Failed to save data: null", errorMessage);
            }

        };

        AutosaveInputBoundary interactor = new AutosaveInteractor(transactionDataAccessObject, goalDataAccessObject, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    public void successTimestampIsRecentTest() {
        AutosaveInputData inputData = new AutosaveInputData();
        AutosaveDataAccessInterface transactionDataAccessObject = new TransactionDataAccessObject(
                "test_transactions.json", "test_source_categories.json");
        AutosaveDataAccessInterface goalDataAccessObject = new GoalDataAccessObject("test_goals.json");

        LocalDateTime beforeExecution = LocalDateTime.now();

        AutosaveOutputBoundary successPresenter = new AutosaveOutputBoundary() {

            @Override
            public void presentSuccess(AutosaveOutputData outputData) {
                assertEquals("Autosave completed successfully", outputData.getMessage());
                assertNotNull(outputData.getTimestamp());
                
                LocalDateTime afterExecution = LocalDateTime.now();
                assertTrue(outputData.getTimestamp().isAfter(beforeExecution.minus(1, ChronoUnit.SECONDS)));
                assertTrue(outputData.getTimestamp().isBefore(afterExecution.plus(1, ChronoUnit.SECONDS)));
            }

            @Override
            public void presentFailure(String errorMessage) {
                fail("Use case failure is unexpected.");
            }

        };

        AutosaveInputBoundary interactor = new AutosaveInteractor(transactionDataAccessObject, goalDataAccessObject, successPresenter);
        interactor.execute(inputData);
    }

    @Test
    public void multipleConsecutiveSavesTest() {
        AutosaveInputData inputData = new AutosaveInputData();
        AutosaveDataAccessInterface transactionDataAccessObject = new TransactionDataAccessObject(
                "test_transactions.json", "test_source_categories.json");
        AutosaveDataAccessInterface goalDataAccessObject = new GoalDataAccessObject("test_goals.json");

        final int[] successCount = {0};

        AutosaveOutputBoundary successPresenter = new AutosaveOutputBoundary() {

            @Override
            public void presentSuccess(AutosaveOutputData outputData) {
                assertEquals("Autosave completed successfully", outputData.getMessage());
                assertNotNull(outputData.getTimestamp());
                successCount[0]++;
            }

            @Override
            public void presentFailure(String errorMessage) {
                fail("Use case failure is unexpected.");
            }

        };

        AutosaveInputBoundary interactor = new AutosaveInteractor(transactionDataAccessObject, goalDataAccessObject, successPresenter);
        
        interactor.execute(inputData);
        interactor.execute(inputData);
        interactor.execute(inputData);
        
        assertEquals(3, successCount[0]);
    }

    @Test
    public void failureWhenGoalSaveFailsTest() {
        AutosaveInputData inputData = new AutosaveInputData();
        AutosaveDataAccessInterface transactionDataAccessObject = new TransactionDataAccessObject(
                "test_transactions.json", "test_source_categories.json");
        AutosaveDataAccessInterface goalDataAccessObject = new GoalDataAccessObject("test_goals.json") {
            @Override
            public void save() {
                throw new RuntimeException("Failed to save goals");
            }
        };

        AutosaveOutputBoundary failurePresenter = new AutosaveOutputBoundary() {

            @Override
            public void presentSuccess(AutosaveOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void presentFailure(String errorMessage) {
                assertEquals("Failed to save data: Failed to save goals", errorMessage);
            }

        };

        AutosaveInputBoundary interactor = new AutosaveInteractor(transactionDataAccessObject, goalDataAccessObject, failurePresenter);
        interactor.execute(inputData);
    }

}
