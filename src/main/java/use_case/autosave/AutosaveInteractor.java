package use_case.autosave;

import java.time.LocalDateTime;

public class AutosaveInteractor implements AutosaveInputBoundary {

    private final AutosaveDataAccessInterface transactionDataAccessObject;
    private final AutosaveDataAccessInterface goalDataAccessObject;

    private final AutosaveOutputBoundary autosaveOutputBoundary;

    public AutosaveInteractor(AutosaveDataAccessInterface transactionDataAccessObject,
                              AutosaveDataAccessInterface goalDataAccessObject,
                              AutosaveOutputBoundary autosaveOutputBoundary) {
        this.transactionDataAccessObject = transactionDataAccessObject;
        this.goalDataAccessObject = goalDataAccessObject;
        this.autosaveOutputBoundary = autosaveOutputBoundary;
    }

    /**
     * Executes an autosave request.
     *
     * @param inputData contextual input for the request
     */
    @Override
    public void execute(AutosaveInputData inputData) {
        try {
            transactionDataAccessObject.save();
            goalDataAccessObject.save();
            final AutosaveOutputData outputData = new AutosaveOutputData(
                    "Autosave completed successfully",
                    LocalDateTime.now());
            autosaveOutputBoundary.presentSuccess(outputData);
        }
        catch (RuntimeException ex) {
            final String errorMessage = "Failed to save data: " + ex.getMessage();
            autosaveOutputBoundary.presentFailure(errorMessage);
        }
    }
}

