package use_case.set_goal;

import java.time.YearMonth;
import java.util.List;

import entity.Category;

public class SetGoalInputData {
    private final YearMonth yearMonth;

    private final float goalAmount;

    private final List<Category> categories;

    public SetGoalInputData(YearMonth yearMonth, float goalAmount, List<Category> categories) {
        this.yearMonth = yearMonth;
        this.goalAmount = goalAmount;
        this.categories = categories;
    }

    public YearMonth getYearMonth() {
        return yearMonth;
    }

    public float getGoalAmount() {
        return goalAmount;
    }

    public List<Category> getCategories() {
        return categories;
    }
}
