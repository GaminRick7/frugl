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

    public void setGoal(YearMonth yearMonth, float goalAmount, List<Category> categories) {
        // create an instance of the input data object
        SetGoalInputData inputData = new SetGoalInputData(
                yearMonth,
                goalAmount,
                categories
        );

        setGoalUseCaseInteractor.execute(inputData);
    }
}
