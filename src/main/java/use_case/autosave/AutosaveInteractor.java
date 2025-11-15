package use_case.autosave;

import java.time.LocalDateTime;

public class AutosaveInteractor implements AutosaveInputBoundary {

    private final AutosaveDataAccessInterface autosaveDataAccessObject;

    private final AutosaveOutputBoundary autosaveOutputBoundary;

    public AutosaveInteractor(AutosaveDataAccessInterface autosaveDataAccessObject,
                              AutosaveOutputBoundary autosaveOutputBoundary) {
        this.autosaveDataAccessObject = autosaveDataAccessObject;
        this.autosaveOutputBoundary = autosaveOutputBoundary;
    }

    @Override
    public void execute(AutosaveInputData inputData) {
        try {
            autosaveDataAccessObject.save();
            AutosaveOutputData outputData = new AutosaveOutputData(
                    "Autosave completed successfully",
                    LocalDateTime.now());
            autosaveOutputBoundary.presentSuccess(outputData);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to save data: " + e.getMessage();
            autosaveOutputBoundary.presentFailure(errorMessage);
        }
    }
}

