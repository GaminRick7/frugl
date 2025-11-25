package interface_adapter.autosave;

import use_case.autosave.AutosaveOutputBoundary;
import use_case.autosave.AutosaveOutputData;

public class AutosavePresenter implements AutosaveOutputBoundary {

    private final AutosaveViewModel viewModel;

    public AutosavePresenter(AutosaveViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentSuccess(AutosaveOutputData outputData) {
        AutosaveState state = viewModel.getState();
        state.setSuccess(outputData.getMessage(), outputData.getTimestamp());
        viewModel.firePropertyChange();
    }

    @Override
    public void presentFailure(String errorMessage) {
        AutosaveState state = viewModel.getState();
        state.setErrorMessage(errorMessage);
        viewModel.firePropertyChange();
    }
}

