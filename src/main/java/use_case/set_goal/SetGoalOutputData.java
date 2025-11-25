package use_case.set_goal;

import java.time.LocalDateTime;
import java.util.List;

import entity.Goal;
import entity.GoalTree;

public class SetGoalOutputData {

    private final Goal goal;

    private final boolean success;

    private final String message;

    private final LocalDateTime timestamp;

    private final List<GoalTree> forest;

    public SetGoalOutputData(Goal goal, List<GoalTree> forest, boolean success, String message) {
        this.goal = goal;
        this.forest = forest;
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<GoalTree> getForest() {
        return forest;
    }

}
