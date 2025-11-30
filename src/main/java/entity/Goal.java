package entity;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class Goal {

    private YearMonth yearMonth;

    private List<Category> categories;

    private float goalAmount;

    public Goal(YearMonth yearMonth, float goalAmount) {
        this.yearMonth = yearMonth;
        this.categories = new ArrayList<>();
        this.goalAmount = goalAmount;
    }

    // Alternative constructor for utility
    public Goal(YearMonth yearMonth, List<Category> categories, float goalAmount) {
        this.yearMonth = yearMonth;
        this.categories = categories;
        this.goalAmount = goalAmount;
    }

    public YearMonth getMonth() {
        return yearMonth;
    }

    public void setMonth(YearMonth newYearMonth) {
        this.yearMonth = newYearMonth;
    }

    public List<Category> getCategories() {
        return categories;
    }

    /**
     * Adds a category to this goal.
     *
     * @param category the category to add
     */

    public void addCategory(Category category) {
        categories.add(category);
    }

    /**
     * Removes a category from this goal.
     *
     * @param category the category to remove
     * @return true if the category was removed successfully, false otherwise
     */

    public boolean removeCategory(Category category) {
        Boolean result = false;

        if (categories.contains(category)) {
            categories.remove(category);
            result = true;
        }

        return result;
        // return if the removal was successful or not
    }

    public float getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(float goalAmount) {
        this.goalAmount = goalAmount;
    }

}
