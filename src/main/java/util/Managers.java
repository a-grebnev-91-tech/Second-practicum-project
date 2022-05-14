package util;

import manager.HttpTaskManager;
import manager.TaskManager;

public final class Managers {
    private static final String URI = "http://localhost:8078";
    private Managers() {}

    public static TaskManager getDefault() {
        return new HttpTaskManager(URI);
    }
}
