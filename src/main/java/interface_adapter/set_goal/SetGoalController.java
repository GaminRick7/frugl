package interface_adapter.set_goal;

import java.time.YearMonth;
import java.util.List;

import entity.Category;
import use_case.set_goal.SetGoalInputBoundary;
import use_case.set_goal.SetGoalInputData;

public class SetGoalController {
    private final SetGoalInputBoundary setGoalUseCaseInteractor;

    public SetGoalController(SetGoalInputBoundary setGoalUseCaseInteractor) {
        this.setGoalUseCaseInteractor = setGoalUseCaseInteractor;
    }

    /**
     * Sets a goal for the specified month with the given amount and categories.
     *
     * @param yearMonth   the year and month the goal applies to
     * @param goalAmount  the monetary amount of the goal
     * @param categories  the list of categories associated with the goal
     */

    public void setGoal(YearMonth yearMonth, float goalAmount, List<Category> categories) {
        // create an instance of the input data object
        final SetGoalInputData inputData = new SetGoalInputData(
                yearMonth,
                goalAmount,
                categories
        );

        setGoalUseCaseInteractor.execute(inputData);
    }
    /**
     * Call the load forest.
     */

    public void loadForest() {
        setGoalUseCaseInteractor.loadForest();
    }
}
