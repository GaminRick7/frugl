package interface_adapter.autosave;

import use_case.autosave.AutosaveInputBoundary;
import use_case.autosave.AutosaveInputData;

public class AutosaveController {

    private final AutosaveInputBoundary autosaveInteractor;

    public AutosaveController(AutosaveInputBoundary autosaveInteractor) {
        this.autosaveInteractor = autosaveInteractor;
    }

    public void autosaveNow() {
        autosaveInteractor.execute(new AutosaveInputData());
    }
}


