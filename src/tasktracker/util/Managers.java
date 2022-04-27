package tasktracker.util;

import tasktracker.manager.FileBackedTaskManager;
import tasktracker.manager.InMemoryTaskManager;
import tasktracker.manager.TaskManager;

import java.io.File;

public final class Managers {
    private Managers() {}

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileBackedManager(String file) {
        return new FileBackedTaskManager(file);
    }

}
