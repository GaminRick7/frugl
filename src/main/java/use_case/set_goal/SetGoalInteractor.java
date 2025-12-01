package use_case.set_goal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entity.Goal;
import entity.GoalTree;
import entity.Transaction;

public class SetGoalInteractor implements SetGoalInputBoundary {

    private final SetGoalDataAccessInterface goalDataAccess;

    private final ForestDataAccessInterface transactionDataAccess;

    private final SetGoalOutputBoundary presenter;

    public SetGoalInteractor(SetGoalDataAccessInterface goalDataAccess, ForestDataAccessInterface transactionDataAccess,
                             SetGoalOutputBoundary presenter) {
        this.goalDataAccess = goalDataAccess;
        this.transactionDataAccess = transactionDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(SetGoalInputData input) {

        boolean valid = true;
        String errorMessage = null;

        if (input.getGoalAmount() < 0) {
            errorMessage = "Goal amount must be at least 0.";
            valid = false;
        }
        else if (input.getCategories().isEmpty()) {
            errorMessage = "At least one category must be provided.";
            valid = false;
        }

        if (valid) {
            final Goal goal = new Goal(input.getYearMonth(), input.getCategories(), input.getGoalAmount());

            try {
                goalDataAccess.saveGoal(goal);

                final List<Goal> allGoals = goalDataAccess.getAll();
                final List<Transaction> allTransactions = transactionDataAccess.getAll();
                final List<GoalTree> forest = new ArrayList<>();

                for (Goal g : allGoals) {

                    // generate goal trees based on goals with deterministic coordinates

                    final long seed = g.getMonth().hashCode() + g.getCategories().hashCode();
                    final Random rng = new Random(seed);

                    final int x = rng.nextInt(700);
                    final int y = rng.nextInt(500);
                    final GoalTree tree = new GoalTree(g, x, y);

                    tree.updateStatus(allTransactions);

                    forest.add(tree);
                }

                presenter.prepareSuccessView(new SetGoalOutputData(goal, forest, true, "Goal successfully saved."));
            }
            catch (IOException error) {
                presenter.prepareFailView(error.getMessage());
            }
            catch (RuntimeException error) {
                // repository interface methods may throw runtime exceptions
                presenter.prepareFailView(error.getMessage());
            }
        }

        else {
            presenter.prepareFailView(errorMessage);
        }

    }

    /**
     * Helper method for goal fetching and forest generation.
     * @param newGoal The goal just saved (can be null for initial load).
     * @param successMessage The message to show (can be null for initial load).
     * @throws IOException if the file cannot be written or accessed
     */

    private void loadAndPresentForest(Goal newGoal, String successMessage) throws IOException {
        final List<Goal> allGoals = goalDataAccess.getAll();

        final List<GoalTree> forest = new ArrayList<>();

        for (Goal g : allGoals) {

            // generate goal trees based on goals with deterministic coordinates

            final List<Transaction> filteredTransactions =
                    transactionDataAccess.getTransactionsByCategoriesAndMonth(g.getCategories(), g.getMonth());

            final long seed = g.getMonth().hashCode() + g.getCategories().hashCode();
            final Random rng = new Random(seed);

            final int x = rng.nextInt(700);
            final int y = rng.nextInt(500);
            final GoalTree tree = new GoalTree(g, x, y);

            tree.updateStatus(filteredTransactions);

            forest.add(tree);
        }

        presenter.prepareSuccessView(new SetGoalOutputData(newGoal, forest, successMessage != null, successMessage));
    }

    /**
     * This method is called once on application startup.
     */
    @Override
    public void loadForest() {
        try {
            // Load and present the forest, using null for the message to keep it silent
            loadAndPresentForest(null, null);
        }
        catch (IOException error) {
            presenter.prepareFailView("An error occurred while loading forest: " + error.getMessage());
        }
    }
}
