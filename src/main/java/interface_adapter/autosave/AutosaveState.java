package interface_adapter.autosave;

import java.time.LocalDateTime;

public class AutosaveState {
    private String statusMessage;
    private LocalDateTime lastSavedAt;
    private String errorMessage;

    AutosaveState() {
        this.statusMessage = "Autosave not run yet";
    }

    public void setSuccess(String message, LocalDateTime timestamp) {
        this.statusMessage = message;
        this.lastSavedAt = timestamp;
        this.errorMessage = null;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public LocalDateTime getLastSavedAt() {
        return lastSavedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
