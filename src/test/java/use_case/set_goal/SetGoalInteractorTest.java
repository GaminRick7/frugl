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

    @Test
    void testOutputDataGetters() {
        Goal g = new Goal(YearMonth.of(2025, 1), List.of(new Category("Test")), 300);
        List<GoalTree> forest = List.of(new GoalTree(g, 0, 0));

        SetGoalOutputData data = new SetGoalOutputData(g, forest, true, "OK");

        assertEquals(g, data.getGoal());
        assertEquals(forest, data.getForest());
        assertTrue(data.isSuccess());
        assertEquals("OK", data.getMessage());
        assertNotNull(data.getTimestamp());
    }

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


    @Test
    void testOverwriteExistingGoal() throws IOException {
        InMemoryGoalRepo repo = new InMemoryGoalRepo();
        ForestDataAccessInterface txRepo = new InMemoryTransactionRepo(List.of(), Map.of());

        Category food = new Category("Food");
        YearMonth m = YearMonth.of(2025, 1);

        Goal g1 = new Goal(m, List.of(food), 100);
        repo.saveGoal(g1);
        SetGoalInputData input = new SetGoalInputData(m, 300, List.of(food));

        SetGoalOutputBoundary presenter = new SetGoalOutputBoundary() {
            @Override public void prepareSuccessView(SetGoalOutputData d) {
                assertEquals(300, d.getGoal().getGoalAmount());
            }
            @Override public void prepareFailView(String e) { fail(); }
        };

        new SetGoalInteractor(repo, txRepo, presenter).execute(input);

        // Ensure repo contains only the updated goal
        assertEquals(1, repo.getAll().size());
        assertEquals(300, repo.getAll().get(0).getGoalAmount());
    }
    @Test
    void testExecuteRuntimeException() {
        // Mock repository that throws RuntimeException
        SetGoalDataAccessInterface repo = new SetGoalDataAccessInterface() {
            @Override
            public void saveGoal(Goal g) {
                throw new RuntimeException("Unexpected runtime error");
            }

            @Override
            public List<Goal> getAll() {
                return List.of();
            }
        };

        ForestDataAccessInterface txRepo = new InMemoryTransactionRepo(List.of(), Map.of());

        SetGoalInputData input = new SetGoalInputData(
                YearMonth.of(2025, 12),
                200,
                List.of(new Category("Food"))
        );

        final boolean[] failCalled = {false};

        SetGoalOutputBoundary presenter = new SetGoalOutputBoundary() {
            @Override
            public void prepareSuccessView(SetGoalOutputData data) {
                fail("Should not succeed");
            }

            @Override
            public void prepareFailView(String error) {
                failCalled[0] = true;
                assertEquals("Unexpected runtime error", error);
            }
        };

        new SetGoalInteractor(repo, txRepo, presenter).execute(input);

        assertTrue(failCalled[0], "prepareFailView was not called");
    }
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
                assertNotNull(data.getTimestamp());
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
            public void saveGoal(Goal g) throws IOException { throw new IOException("DB broken"); }
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

        YearMonth now = YearMonth.now();
        YearMonth past = now.minusMonths(1);    // already passed → healthy/dead
        YearMonth current = now;                // current month → sapling
        YearMonth future = now.plusMonths(1);   // future → sapling

        List<Transaction> txs = List.of(
                new Transaction(sFood, 50, past.atDay(10)),
                new Transaction(sRent, 200, past.atDay(5))
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

        // Create goals
        interactor.execute(new SetGoalInputData(past, 100, List.of(food)));    // healthy/dead
        interactor.execute(new SetGoalInputData(past, 150, List.of(rent)));    // healthy/dead
        interactor.execute(new SetGoalInputData(future, 200, List.of(food)));  // sapling

        List<GoalTree> forest = new ArrayList<>();
        for (Goal g : repo.getAll()) {
            List<Transaction> filtered = txRepo.getTransactionsByCategoriesAndMonth(g.getCategories(), g.getMonth());
            GoalTree t = new GoalTree(g, 0, 0);  // fixed coordinates
            t.updateStatus(filtered);             // uses YearMonth.now() internally
            forest.add(t);
        }

        // Assertions
        assertEquals("healthy", forest.stream()
                .filter(t -> t.getGoal().getGoalAmount() == 100)
                .findFirst().get().getStatus());

        assertEquals("dead", forest.stream()
                .filter(t -> t.getGoal().getGoalAmount() == 150)
                .findFirst().get().getStatus());

        assertEquals("sapling", forest.stream()
                .filter(t -> t.getGoal().getGoalAmount() == 200)
                .findFirst().get().getStatus());
    }

    @Test
    void testLoadForestSuccess() {
        Category c = new Category("Food");
        Goal g = new Goal(YearMonth.of(2026, 1), List.of(c), 500);

        InMemoryGoalRepo repo = new InMemoryGoalRepo();
        try { repo.saveGoal(g); } catch (IOException e) { fail(); }

        ForestDataAccessInterface txRepo = new InMemoryTransactionRepo(List.of(), Map.of());

        SetGoalOutputBoundary presenter = new SetGoalOutputBoundary() {
            @Override
            public void prepareSuccessView(SetGoalOutputData data) {
                assertEquals(1, data.getForest().size());
                assertNull(data.getGoal());
                assertFalse(data.isSuccess()); // success flag is false in loadForest
                assertNull(data.getMessage()); // covers message=null branch
                assertNotNull(data.getTimestamp());
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
            @Override public void saveGoal(Goal goal) {}
            @Override public List<Goal> getAll() throws IOException { throw new IOException("Load fail"); }
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

    @Test
    void testLoadAndPresentForestWithSuccessMessage() throws IOException {
        // Prepare a goal and repo
        Goal g = new Goal(YearMonth.of(2025, 11), List.of(new Category("Test")), 100);

        SetGoalDataAccessInterface repo = new InMemoryGoalRepo();
        repo.saveGoal(g);  // save a goal so loadAndPresentForest has something to process

        ForestDataAccessInterface txRepo = new InMemoryTransactionRepo(List.of(), Map.of());

        final boolean[] successCalled = {false};

        SetGoalOutputBoundary presenter = new SetGoalOutputBoundary() {
            @Override
            public void prepareSuccessView(SetGoalOutputData data) {
                successCalled[0] = true;
                assertEquals(g, data.getGoal());
                assertFalse(data.getForest().isEmpty());
                assertTrue(data.isSuccess());            // successMessage != null branch
                assertEquals("Custom success message", data.getMessage());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Should not fail");
            }
        };

        SetGoalInteractor interactor = new SetGoalInteractor(repo, txRepo, presenter);

        // Directly call the private method using reflection
        try {
            var method = SetGoalInteractor.class.getDeclaredMethod("loadAndPresentForest", Goal.class, String.class);
            method.setAccessible(true);
            method.invoke(interactor, g, "Custom success message"); // non-null message
        } catch (Exception e) {
            fail(e);
        }

        assertTrue(successCalled[0], "prepareSuccessView was not called");
    }

    @Test
    void testLoadForestEmpty() {
        // Hits the branch where the loop in loadAndPresentForest is skipped entirely
        SetGoalDataAccessInterface repo = new InMemoryGoalRepo();
        ForestDataAccessInterface txRepo = new InMemoryTransactionRepo(List.of(), Map.of());

        SetGoalOutputBoundary presenter = new SetGoalOutputBoundary() {
            @Override
            public void prepareSuccessView(SetGoalOutputData data) {
                assertTrue(data.getForest().isEmpty());
                assertNull(data.getGoal());
                assertFalse(data.isSuccess());
            }
            @Override
            public void prepareFailView(String error) { fail(); }
        };

        new SetGoalInteractor(repo, txRepo, presenter).loadForest();
    }

    @Test
    void testExecuteWithEmptyRepo() {
        // Hits the branch inside execute() where the loop over allGoals is skipped.
        // This requires mocking getAll() to return empty even after saveGoal is called.
        SetGoalDataAccessInterface repo = new SetGoalDataAccessInterface() {
            @Override public void saveGoal(Goal g) {}
            @Override public List<Goal> getAll() { return List.of(); } // Empty list
        };

        ForestDataAccessInterface txRepo = new InMemoryTransactionRepo(List.of(), Map.of());

        SetGoalInputData input = new SetGoalInputData(YearMonth.of(2025, 1), 100, List.of(new Category("Cat")));

        SetGoalOutputBoundary presenter = new SetGoalOutputBoundary() {
            @Override
            public void prepareSuccessView(SetGoalOutputData data) {
                assertTrue(data.getForest().isEmpty());
                assertEquals(100, data.getGoal().getGoalAmount());
                assertTrue(data.isSuccess());
            }
            @Override
            public void prepareFailView(String error) { fail(); }
        };

        new SetGoalInteractor(repo, txRepo, presenter).execute(input);
    }
}
