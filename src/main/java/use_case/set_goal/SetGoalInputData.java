package use_case.set_goal;

import java.time.YearMonth;
import java.util.List;

import entity.Category;


public class SetGoalInputData {
    public final YearMonth yearMonth;

    public final float goalAmount;

    public final List<Category> categories;

    public SetGoalInputData(YearMonth yearMonth, float goalAmount, List<Category> categories) {
        this.yearMonth = yearMonth;
        this.goalAmount = goalAmount;
        this.categories = categories;
    }
}
