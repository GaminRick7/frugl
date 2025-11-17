package use_case.autosave;

import java.time.LocalDateTime;

public class AutosaveOutputData {
    private final String message;
    private final LocalDateTime timestamp;

    public AutosaveOutputData(String message, LocalDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

