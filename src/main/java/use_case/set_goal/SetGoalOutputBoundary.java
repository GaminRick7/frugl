package use_case.set_goal;

public interface SetGoalOutputBoundary {
    /**
     * Prepares the view for a successful goal operation.
     *
     * @param data the output data containing the result of the operation
     */

    void prepareSuccessView(SetGoalOutputData data);
    /**
     * Prepares the view for a failed goal operation.
     *
     * @param errorMessage the error message explaining why the operation failed
     */

    void prepareFailView(String errorMessage);
}
