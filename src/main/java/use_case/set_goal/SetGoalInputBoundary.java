package use_case.set_goal;

public interface SetGoalInputBoundary {
    /**
     * Executes the "Set Goal" use case.
     *
     * @param inputData the input data required to set a goal
     */
    void execute(SetGoalInputData inputData);
}
