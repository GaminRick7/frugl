package use_case.set_goal;

import entity.Category;
import entity.Goal;
import entity.GoalTree;
import entity.Transaction;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SetGoalInteractorTest {

    /**
     * in memory goal repository for testing purposes
     */
    private static class InMemoryGoalRepo implements SetGoalDataAccessInterface {
        private final List<Goal> goals = new ArrayList<>();

        @Override
        public void saveGoal(Goal goal) {
            goals.add(goal);
        }

        @Override
        public List<Goal> getAll() {
            return new ArrayList<>(goals);
        }
    }

    /**
     * in memory transaction repository for testing purposes
     */
    private static class InMemoryTransactionRepo implements ForestDataAccessInterface {
        private final List<Transaction> txs = new ArrayList<>();

        @Override
        public List<Transaction> getAll() {
            return new ArrayList<>(txs);
        }
    }

    @Test
    void successTest() {

        SetGoalDataAccessInterface goalRepo = new InMemoryGoalRepo();
        ForestDataAccessInterface transactionRepo = new InMemoryTransactionRepo();

        SetGoalInputData input = new SetGoalInputData(
                YearMonth.of(2025, 1),
                500,
                Arrays.asList(new Category("Food"), new Category("Travel"))
        );


        SetGoalOutputBoundary presenter = new SetGoalOutputBoundary() {
            @Override
            public void prepareSuccessView(SetGoalOutputData data) {

                Goal saved = data.getGoal();

                assertEquals(500, saved.getGoalAmount());
                List<String> actualCatNames = saved.getCategories()
                        .stream()
                        .map(Category::getName)
                        .toList();

                assertEquals(Arrays.asList("Food", "Travel"), actualCatNames);


                List<GoalTree> forest = data.getForest();
                assertEquals(1, forest.size());
                assertEquals(saved, forest.get(0).getGoal());

                assertTrue(data.isSuccess());
                assertEquals("Goal successfully saved.", data.getMessage());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Unexpected failure: " + error);
            }
        };

        SetGoalInteractor interactor = new SetGoalInteractor(goalRepo, transactionRepo, presenter);
        interactor.execute(input);
    }

    @Test
    void failureNegativeGoalAmount() {

        SetGoalDataAccessInterface goalRepo = new InMemoryGoalRepo();
        ForestDataAccessInterface transactionRepo = new InMemoryTransactionRepo();

        SetGoalInputData input = new SetGoalInputData(
                YearMonth.of(2025, 1),
                -5,
                Arrays.asList(new Category("Food"))
        );

        SetGoalOutputBoundary presenter = new SetGoalOutputBoundary() {
            @Override
            public void prepareSuccessView(SetGoalOutputData data) {
                fail("The test should not pass for negative goal amounts.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("Goal amount must be at least 0.", error);
            }
        };

        SetGoalInteractor interactor = new SetGoalInteractor(goalRepo, transactionRepo, presenter);
        interactor.execute(input);
    }

    @Test
    void failureEmptyCategories() {

        SetGoalDataAccessInterface goalRepo = new InMemoryGoalRepo();
        ForestDataAccessInterface transactionRepo = new InMemoryTransactionRepo();

        SetGoalInputData input = new SetGoalInputData(
                YearMonth.of(2025, 1),
                100,
                new ArrayList<>()
        );

        SetGoalOutputBoundary presenter = new SetGoalOutputBoundary() {
            @Override
            public void prepareSuccessView(SetGoalOutputData data) {
                fail("The test should not pass when no categories are provided.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("At least one category must be provided.", error);
            }
        };

        SetGoalInteractor interactor = new SetGoalInteractor(goalRepo, transactionRepo, presenter);
        interactor.execute(input);
    }

    @Test
    void failureExceptionThrownBySaveGoal() {

        SetGoalDataAccessInterface goalRepo = new SetGoalDataAccessInterface() {
            @Override
            public void saveGoal(Goal goal) throws IOException{
                throw new IOException("Simulated DB failure");
            }

            @Override
            public List<Goal> getAll() {
                return new ArrayList<>();
            }
        };

        ForestDataAccessInterface transactionRepo = new InMemoryTransactionRepo();

        SetGoalInputData input = new SetGoalInputData(
                YearMonth.of(2025, 1),
                100,
                Arrays.asList(new Category("Food"))
        );

        SetGoalOutputBoundary presenter = new SetGoalOutputBoundary() {
            @Override
            public void prepareSuccessView(SetGoalOutputData data) {
                fail("The test should not pass if the DAO throws exeption.");
            }

            @Override
            public void prepareFailView(String error) {
                assertTrue(error.startsWith("An error occurred while saving goal: Simulated DB failure"));
            }
        };

        SetGoalInteractor interactor = new SetGoalInteractor(goalRepo, transactionRepo, presenter);
        interactor.execute(input);
    }
}