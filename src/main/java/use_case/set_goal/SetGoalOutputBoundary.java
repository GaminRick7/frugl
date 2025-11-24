package use_case.set_goal;

public interface SetGoalOutputBoundary {
    void prepareSuccessView(SetGoalOutputData data);

    void prepareFailView(String errorMessage);
}
