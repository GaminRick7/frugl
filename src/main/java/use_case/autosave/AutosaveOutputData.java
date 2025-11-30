package use_case.autosave;

import java.time.LocalDateTime;

public class AutosaveOutputData {

    private final String message;

    private final LocalDateTime timestamp;

    /**
     * Creates an output data object.
     *
     * @param message description of the autosave result
     * @param timestamp moment the autosave completed
     */
    public AutosaveOutputData(String message, LocalDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    /**
     * Getter for the result message.
     *
     * @return the message to display
     */
    public String getMessage() {
        return message;
    }

    /**
     * Getter for the completion timestamp.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

