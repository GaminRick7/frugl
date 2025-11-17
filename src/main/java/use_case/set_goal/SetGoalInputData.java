package use_case.set_goal;

import entity.Category;
import java.time.YearMonth;
import java.util.List;

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
