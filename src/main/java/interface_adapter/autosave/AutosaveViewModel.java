package interface_adapter.autosave;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDateTime;

public class AutosaveViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private String statusMessage = "Autosave not run yet";

    private LocalDateTime lastSavedAt;

    private String errorMessage;

    public void setSuccess(String message, LocalDateTime timestamp) {
        this.statusMessage = message;
        this.lastSavedAt = timestamp;
        this.errorMessage = null;
        support.firePropertyChange("autosaveState", null, this);
    }

    public void setFailure(String errorMessage) {
        this.errorMessage = errorMessage;
        support.firePropertyChange("autosaveState", null, this);
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

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}


