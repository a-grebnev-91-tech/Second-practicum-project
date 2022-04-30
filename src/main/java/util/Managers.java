package util;

import manager.FileBackedTaskManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;

public final class Managers {
    private Managers() {}

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileBackedManager(String file) {
        return new FileBackedTaskManager(file);
    }

}
