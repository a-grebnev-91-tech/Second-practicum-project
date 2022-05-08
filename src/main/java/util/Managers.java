package util;

import manager.FileBackedTaskManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;

import java.io.File;

public final class Managers {
    private static final String file = "src"
            + File.separator
            + "main"
            + File.separator
            + "resources"
            + File.separator
            + "file.csv";
    private Managers() {}

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(file);
    }

    public static TaskManager getFileBackedManager(String file) {
        return new FileBackedTaskManager(file);
    }

}
