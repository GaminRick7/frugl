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
        this.categories = new ArrayList<>(categories);
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

    public void setMonth(YearMonth yearMonth) {
        this.yearMonth = yearMonth;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public boolean removeCategory(Category category) {

        if (categories.contains(category)) {
            categories.remove(category);
            return true;
        }

        return false; // return if the removal was successful or not
    }

    public float getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(float goalAmount) {
        this.goalAmount = goalAmount;
    }

}


