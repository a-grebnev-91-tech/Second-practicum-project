package tasktracker.util.tasks;

public class ValidationMessage {

    private final boolean isValid;
    private final String message;

    public ValidationMessage(boolean isValid, String message) {
        this.isValid = isValid;
        this.message = message;
    }

    public ValidationMessage(boolean isValid) {
        this.isValid = isValid;
        this.message = null;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getMessage() {
        return message;
    }
}
