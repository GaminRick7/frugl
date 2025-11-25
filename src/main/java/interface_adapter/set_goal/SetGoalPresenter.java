package interface_adapter.set_goal;

import use_case.set_goal.SetGoalOutputBoundary;
import use_case.set_goal.SetGoalOutputData;

public class SetGoalPresenter implements SetGoalOutputBoundary {

    private final SetGoalViewModel viewModel;

    public SetGoalPresenter(SetGoalViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(SetGoalOutputData outputData) {
        System.out.println(outputData.getMessage() + " at " + outputData.getTimestamp());
        SetGoalState state = viewModel.getState();
        state.setForest(outputData.getForest());
        state.setSuccess(outputData.getMessage());
        viewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        System.out.println(errorMessage);
        SetGoalState state = viewModel.getState();
        state.setFailure(errorMessage);
        viewModel.firePropertyChange();
    }
}
