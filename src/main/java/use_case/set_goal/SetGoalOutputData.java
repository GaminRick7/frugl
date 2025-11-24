package use_case.set_goal;

import java.time.LocalDateTime;

import entity.Goal;


public class SetGoalOutputData {

    private final Goal goal;

    private final boolean success;

    private final String message;

    private final LocalDateTime timestamp;


    public SetGoalOutputData(Goal goal, boolean success, String message) {
        this.goal = goal;
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

}
