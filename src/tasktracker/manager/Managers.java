package tasktracker.manager;

import java.io.File;

public final class Managers {
    private Managers() {}

    //todo edit this
    public static TaskManager getDefault() {
//        return new InMemoryTaskManager();
        return new FileBackedTaskManager("file.txt");
    }


}
