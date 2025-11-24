package use_case.set_goal;

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

        if (input.goalAmount < 0) {
            presenter.prepareFailView("Goal amount must be at least 0.");
            return;
        }

        if (input.categories.isEmpty()) {
            presenter.prepareFailView("At least one category must be provided.");
            return;
        }

        Goal goal = new Goal(input.yearMonth, input.categories, input.goalAmount);

        try {
            goalDataAccess.saveGoal(goal);

            List<Goal> allGoals = goalDataAccess.getAll();
            List<Transaction> allTransactions = transactionDataAccess.getAll();
            List<GoalTree> forest = new ArrayList<>();

            for (Goal g : allGoals) {

                // generate goal trees based on goals with deterministic coordinates

                int seed = 123;
                Random rng = new Random(seed);

                int x = rng.nextInt(700);
                int y = rng.nextInt(500);
                // TODO: Adjust this so that it looks nice
                // TODO: Add logic so that trees do not overlap
                GoalTree tree = new GoalTree(g, x, y);

                tree.updateStatus(allTransactions);

                forest.add(tree);
            }

            presenter.prepareSuccessView(new SetGoalOutputData(goal, forest, true, "Goal successfully saved."));
        } catch (Exception e) {
            presenter.prepareFailView("An error occurred while saving goal: " + e.getMessage());
        }
    }
}
