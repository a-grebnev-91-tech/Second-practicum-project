package tasktracker.manager;

public final class Managers {

    private Managers() {}

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}
