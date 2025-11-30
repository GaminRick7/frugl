package interface_adapter.autosave;

import use_case.autosave.AutosaveInputBoundary;
import use_case.autosave.AutosaveInputData;

public class AutosaveController {

    private final AutosaveInputBoundary autosaveInteractor;

    /**
     * Creates a controller for the autosave use case.
     *
     * @param autosaveInteractor input boundary handling autosave requests
     */
    public AutosaveController(AutosaveInputBoundary autosaveInteractor) {
        this.autosaveInteractor = autosaveInteractor;
    }

    /**
     * Executes the autosave use case.
     */
    public void autosaveNow() {
        autosaveInteractor.execute(new AutosaveInputData());
    }
}
