package use_case.autosave;

public interface AutosaveInputBoundary {

    /**
     * Executes the autosave workflow.
     *
     * @param inputData controller-provided context
     */
    void execute(AutosaveInputData inputData);
}
