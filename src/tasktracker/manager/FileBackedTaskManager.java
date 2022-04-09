package tasktracker.manager;

import tasktracker.taskdata.EpicTask;
import tasktracker.taskdata.Subtask;
import tasktracker.taskdata.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {

    String file;

    public FileBackedTaskManager (String file) {
        super();
        this.file = file;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
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
    public Task getTask(long id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public EpicTask getEpicTask(long id) {
        EpicTask task = super.getEpicTask(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtask(long id) {
        Subtask task = super.getSubtask(id);
        save();
        return task;
    }

    @Override
    public boolean createTask(Task task) {
        boolean result = super.createTask(task);
        save();
        return result;
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
    public boolean updateTask(Task task) {
        boolean result = super.updateTask(task);
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
    public boolean removeTask(long id) {
        boolean result = super.removeTask(id);
        save();
        return result;
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

    private void save() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file), StandardCharsets.UTF_8))
    }

    static class ManagerSaveException extends RuntimeException {
    }
}
