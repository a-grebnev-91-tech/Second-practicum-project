package util;

import manager.FileBackedTaskManager;
import manager.HttpTaskManager;
import manager.TaskManager;

import java.io.File;

public final class Managers {
    private static final String uri = "http://localhost:8078";
    private Managers() {}

    public static TaskManager getDefault() {
        return new HttpTaskManager(uri);
    }
}
