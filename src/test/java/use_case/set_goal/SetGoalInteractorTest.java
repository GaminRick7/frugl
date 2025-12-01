package use_case.set_goal;

import entity.Category;
import entity.Goal;
import entity.GoalTree;
import entity.Source;
import entity.Transaction;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.YearMonth;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SetGoalInteractorTest {

    /**
     * In-memory goal repository for testing purposes.
     * Implements SetGoalDataAccessInterface.
     */
    private static class InMemoryGoalRepo implements SetGoalDataAccessInterface {
        private final List<Goal> goals = new ArrayList<>();

        @Override
        public void saveGoal(Goal goal) throws IOException {
            // Simulate unique constraint handling: replace goal with same month/category set.
            goals.removeIf(existingGoal ->
                    existingGoal.getMonth().equals(goal.getMonth()) &&
                            existingGoal.getCategories().equals(goal.getCategories())
            );
            goals.add(goal);
        }

        @Override
        public List<Goal> getAll() {
            return new ArrayList<>(goals);
        }
    }

    /**
     * In-memory transaction repository for testing purposes.
     * Implements ForestDataAccessInterface.
     */
    private static class InMemoryTransactionRepo implements ForestDataAccessInterface {
        private final List<Transaction> txs = new ArrayList<>();

        public InMemoryTransactionRepo(List<Transaction> transactions) {
            this.txs.addAll(transactions);
        }

        @Override
        public List<Transaction> getAll() {
            return new ArrayList<>(txs);
        }
    }

    @Test
    void successTest() {
        SetGoalDataAccessInterface goalRepo = new InMemoryGoalRepo();
        ForestDataAccessInterface transactionRepo = new InMemoryTransactionRepo(new ArrayList<>());

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
        ForestDataAccessInterface transactionRepo = new InMemoryTransactionRepo(new ArrayList<>());

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
        ForestDataAccessInterface transactionRepo = new InMemoryTransactionRepo(new ArrayList<>());

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

        ForestDataAccessInterface transactionRepo = new InMemoryTransactionRepo(new ArrayList<>());

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

    @Test
    void testTreeStatusCalculation() {
        // setup mock repositories
        SetGoalDataAccessInterface goalRepo = new InMemoryGoalRepo();

        // define categories
        Category food = new Category("Food");
        Category rent = new Category("Rent");

        // define sources using
        Source foodSource = new Source(food.getName());
        Source rentSource = new Source(rent.getName());

        YearMonth goalMonth = YearMonth.of(2025, 10);

        // 1. Goal 1: Healthy (Spent 50 / Goal 100)
        SetGoalInputData input1 = new SetGoalInputData(goalMonth, 100, Arrays.asList(food));
        // 2. Goal 2: Dead (Spent 200 / Goal 150)
        SetGoalInputData input2 = new SetGoalInputData(goalMonth, 150, Arrays.asList(rent));
        // 3. Goal 3: Sapling (Goal Month is in the future)
        SetGoalInputData input3 = new SetGoalInputData(goalMonth.plusMonths(1), 200, Arrays.asList(food));

        List<Transaction> transactions = Arrays.asList(
                // Transaction 1: Affects Goal 1 (Food in Oct 2025) -> $50
                new Transaction(foodSource, 50, LocalDate.of(2025, 10, 15)),
                // Transaction 2: Affects Goal 2 (Rent in Oct 2025) -> $200
                new Transaction(rentSource, 200, LocalDate.of(2025, 10, 10)),
                // Transaction 3: Outside scope (Sept 2025, wrong month)
                new Transaction(foodSource, 10, LocalDate.of(2025, 9, 1))
        );
        ForestDataAccessInterface transactionRepo = new InMemoryTransactionRepo(transactions);


        // Simple presenter for execution
        SetGoalOutputBoundary presenter = new SetGoalOutputBoundary() {
            @Override
            public void prepareSuccessView(SetGoalOutputData data) {
                assertTrue(data.isSuccess());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Unexpected failure during execution: " + error);
            }
        };

        SetGoalInteractor interactor = new SetGoalInteractor(goalRepo, transactionRepo, presenter);

        interactor.execute(input1);
        interactor.execute(input2);
        interactor.execute(input3);

        List<Goal> savedGoals = goalRepo.getAll();
        assertEquals(3, savedGoals.size(), "Expected 3 goals to be saved in the repository.");

        List<Transaction> allTransactions = transactionRepo.getAll();
        List<GoalTree> finalForest = new ArrayList<>();

        for (Goal goal : savedGoals) {
            GoalTree tree = new GoalTree(goal, 0, 0); // Coordinates are irrelevant for status test
            tree.updateStatus(allTransactions); // Calculate status
            finalForest.add(tree);
        }

        assertEquals(3, finalForest.size(), "Expected 3 GoalTree objects to be created.");


        GoalTree tree1 = finalForest.stream().filter(t ->
                t.getGoal().getGoalAmount() == 100 && t.getGoal().getCategories().contains(food)
        ).findFirst().orElseThrow(() -> new AssertionError("Could not find Goal 1 (Amount 100)"));

        GoalTree tree2 = finalForest.stream().filter(t ->
                t.getGoal().getGoalAmount() == 150 && t.getGoal().getCategories().contains(rent)
        ).findFirst().orElseThrow(() -> new AssertionError("Could not find Goal 2 (Amount 150)"));

        GoalTree tree3 = finalForest.stream().filter(t ->
                t.getGoal().getGoalAmount() == 200 && t.getGoal().getMonth().isAfter(goalMonth)
        ).findFirst().orElseThrow(() -> new AssertionError("Could not find Goal 3 (Future month, Amount 200)"));

        // Goal 1 (Food, 2025-10, $100 budget): Spent $50 -> Healthy
        assertEquals("healthy", tree1.getStatus(), "Goal 1: Spent $50 on $100 goal should be healthy.");
        // Goal 2 (Rent, 2025-10, $150 budget): Spent $200 -> Dead
        assertEquals("dead", tree2.getStatus(), "Goal 2: Spent $200 on $150 goal should be dead.");
        // Goal 3 (Food, 2025-11, $200 budget): Since month is in the future, status should be 'sapling' regardless of spending.
        assertEquals("sapling", tree3.getStatus(), "Goal 3: Future month goal should be sapling.");
    }
}