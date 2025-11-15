package interface_adapter.autosave;

import use_case.autosave.AutosaveOutputBoundary;
import use_case.autosave.AutosaveOutputData;

public class AutosavePresenter implements AutosaveOutputBoundary {
    @Override
    public void presentSuccess(AutosaveOutputData outputData) {
        System.out.println(outputData.getMessage() + " at " + outputData.getTimestamp());
    }

    @Override
    public void presentFailure(String errorMessage) {
        System.err.println(errorMessage);
    }
}

