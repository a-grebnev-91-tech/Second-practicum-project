package tasktracker.manager;

import java.io.File;

public final class Managers {
    private Managers() {}

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }


}
