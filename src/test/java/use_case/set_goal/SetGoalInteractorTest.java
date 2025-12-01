package use_case.set_goal;

import entity.Category;
import entity.Goal;
import entity.GoalTree;
import entity.Source;
import entity.Transaction;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SetGoalInteractorTest {

    // ---------------------------------------------------------
    // Mock Repositories
    // ---------------------------------------------------------

    private static class InMemoryGoalRepo implements SetGoalDataAccessInterface {
        private final List<Goal> goals = new ArrayList<>();

        @Override
        public void saveGoal(Goal goal) throws IOException {
            goals.removeIf(g ->
                    g.getMonth().equals(goal.getMonth()) &&
                            g.getCategories().equals(goal.getCategories())
            );
            goals.add(goal);
        }

        @Override
        public List<Goal> getAll() throws IOException {
            return new ArrayList<>(goals);
        }
    }

    private static class InMemoryTransactionRepo implements ForestDataAccessInterface {
        private final List<Transaction> txs = new ArrayList<>();
        private final Map<Source, Category> srcMap;

        public InMemoryTransactionRepo(List<Transaction> txs, Map<Source, Category> linkMap) {
            this.txs.addAll(txs);
            this.srcMap = linkMap;
        }

        @Override
        public List<Transaction> getAll() {
            return new ArrayList<>(txs);
        }

        @Override
        public List<Transaction> getTransactionsByCategoriesAndMonth(List<Category> categories, YearMonth month) {
            Set<String> categoryNames = new HashSet<>();
            for (Category c : categories) categoryNames.add(c.getName());

            List<Transaction> result = new ArrayList<>();
            for (Transaction t : txs) {
                Category mapped = srcMap.get(t.getSource());
                if (mapped == null) continue;

                boolean catMatch = categoryNames.contains(mapped.getName());
                boolean monthMatch = YearMonth.from(t.getDate()).equals(month);

                if (catMatch && monthMatch) result.add(t);
            }
            return result;
        }
    }

    // ---------------------------------------------------------
    // Tests
    // ---------------------------------------------------------

    @Test
    void testSuccess() {
        SetGoalDataAccessInterface goalRepo = new InMemoryGoalRepo();
        ForestDataAccessInterface txRepo = new InMemoryTransactionRepo(List.of(), Map.of());

        SetGoalInputData input = new SetGoalInputData(
                YearMonth.of(2025, 3),
                400,
                List.of(new Category("Food"), new Category("Travel"))
        );

        SetGoalOutputBoundary presenter = new SetGoalOutputBoundary() {
            @Override
            public void prepareSuccessView(SetGoalOutputData data) {
                assertTrue(data.isSuccess());
                assertEquals("Goal successfully saved.", data.getMessage());
                assertEquals(400, data.getGoal().getGoalAmount());
                assertEquals(1, data.getForest().size());
            }
            @Override
            public void prepareFailView(String error) {
                fail("Should not fail");
            }
        };

        new SetGoalInteractor(goalRepo, txRepo, presenter).execute(input);
    }

    @Test
    void testNegativeAmount() {
        SetGoalDataAccessInterface repo = new InMemoryGoalRepo();
        ForestDataAccessInterface txRepo = new InMemoryTransactionRepo(List.of(), Map.of());

        SetGoalInputData bad = new SetGoalInputData(
                YearMonth.of(2025, 3),
                -10,
                List.of(new Category("X"))
        );

        SetGoalOutputBoundary presenter = new SetGoalOutputBoundary() {
            @Override
            public void prepareSuccessView(SetGoalOutputData d) {
                fail("Should fail due to negative amount");
            }
            @Override
            public void prepareFailView(String error) {
                assertEquals("Goal amount must be at least 0.", error);
            }
        };

        new SetGoalInteractor(repo, txRepo, presenter).execute(bad);
    }

    @Test
    void testEmptyCategories() {
        SetGoalDataAccessInterface repo = new InMemoryGoalRepo();
        ForestDataAccessInterface txRepo = new InMemoryTransactionRepo(List.of(), Map.of());

        SetGoalInputData bad = new SetGoalInputData(
                YearMonth.of(2025, 3),
                200,
                List.of()
        );

        SetGoalOutputBoundary presenter = new SetGoalOutputBoundary() {
            @Override
            public void prepareSuccessView(SetGoalOutputData d) {
                fail("Should fail due to missing categories");
            }
            @Override
            public void prepareFailView(String error) {
                assertEquals("At least one category must be provided.", error);
            }
        };

        new SetGoalInteractor(repo, txRepo, presenter).execute(bad);
    }

    @Test
    void testSaveGoalIOException() {
        SetGoalDataAccessInterface repo = new SetGoalDataAccessInterface() {
            @Override
            public void saveGoal(Goal g) throws IOException {
                throw new IOException("DB broken");
            }
            @Override
            public List<Goal> getAll() { return List.of(); }
        };

        ForestDataAccessInterface txRepo = new InMemoryTransactionRepo(List.of(), Map.of());

        SetGoalInputData input = new SetGoalInputData(
                YearMonth.of(2025, 7),
                100,
                List.of(new Category("Food"))
        );

        SetGoalOutputBoundary presenter = new SetGoalOutputBoundary() {
            @Override
            public void prepareSuccessView(SetGoalOutputData d) {
                fail("Should fail because saveGoal throws");
            }
            @Override
            public void prepareFailView(String error) {
                assertTrue(error.contains("DB broken"));
            }
        };

        new SetGoalInteractor(repo, txRepo, presenter).execute(input);
    }

    @Test
    void testTreeStatusHealthyDeadSapling() throws IOException {
        Category food = new Category("Food");
        Category rent = new Category("Rent");

        Source sFood = new Source("Food");
        Source sRent = new Source("Rent");

        YearMonth month = YearMonth.of(2025, 10);
        YearMonth future = month.plusMonths(1);

        List<Transaction> txs = List.of(
                new Transaction(sFood, 50, LocalDate.of(2025, 10, 10)),
                new Transaction(sRent, 200, LocalDate.of(2025, 10, 5))
        );

        Map<Source, Category> link = Map.of(
                sFood, food,
                sRent, rent
        );

        SetGoalDataAccessInterface repo = new InMemoryGoalRepo();
        ForestDataAccessInterface txRepo = new InMemoryTransactionRepo(txs, link);

        SetGoalOutputBoundary presenter = new SetGoalOutputBoundary() {
            @Override public void prepareSuccessView(SetGoalOutputData d) {}
            @Override public void prepareFailView(String e) { fail(); }
        };

        SetGoalInteractor interactor = new SetGoalInteractor(repo, txRepo, presenter);

        interactor.execute(new SetGoalInputData(month, 100, List.of(food)));  // healthy
        interactor.execute(new SetGoalInputData(month, 150, List.of(rent)));  // dead
        interactor.execute(new SetGoalInputData(future, 200, List.of(food))); // sapling

        // Manual recomputation to test correctness
        List<GoalTree> forest = new ArrayList<>();
        for (Goal g : repo.getAll()) {
            List<Transaction> filtered =
                    txRepo.getTransactionsByCategoriesAndMonth(g.getCategories(), g.getMonth());
            GoalTree t = new GoalTree(g, 0, 0);
            t.updateStatus(filtered);
            forest.add(t);
        }

        assertEquals("healthy", forest.stream().filter(t -> t.getGoal().getGoalAmount() == 100).findFirst().get().getStatus());
        assertEquals("dead", forest.stream().filter(t -> t.getGoal().getGoalAmount() == 150).findFirst().get().getStatus());
        assertEquals("sapling", forest.stream().filter(t -> t.getGoal().getGoalAmount() == 200).findFirst().get().getStatus());
    }

    @Test
    void testLoadForestSuccess() {
        Category c = new Category("Food");
        Goal g = new Goal(YearMonth.of(2026, 1), List.of(c), 500);

        InMemoryGoalRepo repo = new InMemoryGoalRepo();
        repo.goals.add(g);

        ForestDataAccessInterface txRepo = new InMemoryTransactionRepo(List.of(), Map.of());

        SetGoalOutputBoundary presenter = new SetGoalOutputBoundary() {
            @Override
            public void prepareSuccessView(SetGoalOutputData data) {
                assertEquals(1, data.getForest().size());
                assertNull(data.getGoal()); // loadForest passes null
                assertFalse(data.isSuccess()); // message = null
            }
            @Override
            public void prepareFailView(String error) {
                fail("loadForest should succeed");
            }
        };

        new SetGoalInteractor(repo, txRepo, presenter).loadForest();
    }

    @Test
    void testLoadForestIOException() {
        SetGoalDataAccessInterface repo = new SetGoalDataAccessInterface() {
            @Override
            public void saveGoal(Goal goal) {}

            @Override
            public List<Goal> getAll() throws IOException {
                throw new IOException("Load fail");
            }
        };

        ForestDataAccessInterface txRepo = new InMemoryTransactionRepo(List.of(), Map.of());

        SetGoalOutputBoundary presenter = new SetGoalOutputBoundary() {
            @Override public void prepareSuccessView(SetGoalOutputData d) { fail(); }
            @Override public void prepareFailView(String e) {
                assertTrue(e.contains("Load fail"));
            }
        };

        new SetGoalInteractor(repo, txRepo, presenter).loadForest();
    }
}
