package use_case.set_goal;

import entity.Goal;

public class SetGoalOutputData {

    private final Goal goal;
    private final boolean success;
    private final String message;

    public SetGoalOutputData(Goal goal, boolean success, String message) {
        this.goal = goal;
        this.success = success;
        this.message = message;
    }

    public Goal getGoal() {
        return goal;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
