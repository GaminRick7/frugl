package interface_adapter.set_goal;

import entity.Category;
import entity.GoalTree;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class SetGoalState {
    public static final String TITLE_LABEL = "Financial Forest";
    public static final String SET_GOAL_BUTTON_LABEL = "Set New Goal";

    private YearMonth yearMonth = YearMonth.now();
    private List<Category> selectedCategories = new ArrayList<>();
    private float goalAmount = 0;
    private String errorMessage = null;
    private String successMessage = null;
    private List<GoalTree> forest = new ArrayList<>();

    public YearMonth getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(YearMonth yearMonth) {
        this.yearMonth = yearMonth;
    }

    public List<Category> getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(List<Category> selectedCategories) {
        this.selectedCategories = selectedCategories;
    }

    public float getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(float goalAmount) {
        this.goalAmount = goalAmount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public List<GoalTree> getForest() {
        return forest;
    }

    public void setForest(List<GoalTree> forest) {
        this.forest = forest;
    }
    // handling property change

    public void setSuccess(String message) {
        this.successMessage = message;
        this.errorMessage = null;
    }

    public void setFailure(String error) {
        this.errorMessage = error;
        this.successMessage = null;
    }
}
