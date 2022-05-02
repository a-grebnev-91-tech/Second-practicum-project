package manager;

import taskdata.*;
import util.csv.CsvFileLoader;
import util.csv.CsvFileSaver;

import java.io.*;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    CsvFileSaver saver;

    public FileBackedTaskManager(String file) {
        super();
        this.saver = new CsvFileSaver(file, this);
    }

    public FileBackedTaskManager(Map<Long, Task> tasks,
                                 Map<Long, EpicTask> epics,
                                 Map<Long, Subtask> subtasks,
                                 HistoryManager historyManager,
                                 long id,
                                 String file) {
        super(tasks, epics, subtasks, historyManager, id);
        this.saver = new CsvFileSaver(file, this);
    }

    @Override
    public long createTask(Task task) {
        long id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public EpicTask getEpicTask(long id) {
        EpicTask task = super.getEpicTask(id);
        save();
        return task;
    }

    @Override
    public Task getTask(long id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtask(long id) {
        Subtask task = super.getSubtask(id);
        save();
        return task;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        return CsvFileLoader.load(file);
    }

    @Override
    public void removeAllEpicTasks() {
        super.removeAllEpicTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public boolean removeTask(long id) {
        boolean result = super.removeTask(id);
        save();
        return result;
    }

    @Override
    public boolean updateTask(Task task) {
        boolean result = super.updateTask(task);
        save();
        return result;
    }

    private void save() {
        saver.save();
    }
}
