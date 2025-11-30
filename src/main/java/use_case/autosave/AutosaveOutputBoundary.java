package use_case.autosave;

public interface AutosaveOutputBoundary {

    /**
     * Presents a successful autosave result.
     *
     * @param outputData message and timestamp to display
     */
    void presentSuccess(AutosaveOutputData outputData);

    /**
     * Presents a failed autosave result.
     *
     * @param errorMessage description of the problem
     */
    void presentFailure(String errorMessage);
}

