package use_case.autosave;

public interface AutosaveOutputBoundary {
    void presentSuccess(AutosaveOutputData outputData);

    void presentFailure(String errorMessage);
}

