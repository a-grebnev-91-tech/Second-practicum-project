package tasktracker.manager;

import tasktracker.taskdata.*;
import tasktracker.util.csv.CsvFileLoader;
import tasktracker.util.csv.CsvFileSaver;
import tasktracker.test.FifthSprintTest;

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
    public boolean createEpicTask(EpicTask epicTask) {
        boolean result = super.createEpicTask(epicTask);
        save();
        return result;
    }

    @Override
    public boolean createSubtask(Subtask subtask) {
        boolean result = super.createSubtask(subtask);
        save();
        return result;
    }

    @Override
    public boolean createTask(Task task) {
        boolean result = super.createTask(task);
        save();
        return result;
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

    private void save() {
        saver.save();
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
    public boolean removeEpicTask(long id) {
        boolean result = super.removeEpicTask(id);
        save();
        return result;
    }

    @Override
    public boolean removeSubtask(long id) {
        boolean result = super.removeSubtask(id);
        save();
        return result;
    }

    @Override
    public boolean removeTask(long id) {
        boolean result = super.removeTask(id);
        save();
        return result;
    }

    @Override
    public boolean updateEpicTask(EpicTask epicTask) {
        boolean result = super.updateEpicTask(epicTask);
        save();
        return result;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        boolean result = super.updateSubtask(subtask);
        save();
        return result;
    }

    @Override
    public boolean updateTask(Task task) {
        boolean result = super.updateTask(task);
        save();
        return result;
    }
}
