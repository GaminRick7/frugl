package use_case.set_goal;

import entity.Goal;

public class SetGoalInteractor implements SetGoalInputBoundary {

    private final SetGoalDataAccessInterface dataAccess;

    private final SetGoalOutputBoundary presenter;

    public SetGoalInteractor(SetGoalDataAccessInterface dataAccess, SetGoalOutputBoundary presenter) {
        this.dataAccess = dataAccess;
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
            dataAccess.saveGoal(goal);
            presenter.prepareSuccessView(new SetGoalOutputData(goal, true, "Goal successfully saved."));
        } catch (Exception e) {
            presenter.prepareFailView("An error occurred while saving goal: " + e.getMessage());
        }
    }
}
