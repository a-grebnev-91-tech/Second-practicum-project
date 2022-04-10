package tasktracker.manager;

import tasktracker.taskdata.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.File;

public class FileBackedTaskManager extends InMemoryTaskManager {

    String file;

    public FileBackedTaskManager(String file) {
        super();
        this.file = file;
    }

    public FileBackedTaskManager(HashMap<Long, Task> tasks,
                                 HashMap<Long, EpicTask> epicTasks,
                                 HashMap<Long, Subtask> subtasks,
                                 HistoryManager historyManager,
                                 long id,
                                 String file) {
        super(tasks, epicTasks, subtasks, historyManager, id);
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {

    }

    public static Task taskFromString(String value) {
        String[] splitValue = value.split(",");
        long id = Long.parseLong(splitValue[0]);
        TaskType type = TaskType.valueOf(splitValue[1]);
        String name = splitValue[2];
        TaskStatus status = TaskStatus.valueOf(splitValue[3]);
        String description = splitValue[4];
        switch (type) {
            case TASK:
                return new Task(id, status, name, description);
            case EPIC:
                return new EpicTask(id, status, name, description);
            case SUBTASK:
                long epicID = Long.parseLong(splitValue[5]);

        }
    }

    public static String toString(HistoryManager history) {
        StringBuilder builder = new StringBuilder();
        for (Task task : history.getHistory()) {
            builder.append(task.getID());
            builder.append(",");
        }
        return builder.toString();
    }

    public static List<Long> historyFromString(String value) {
        List<Long> resultList = new ArrayList<>();
        String[] ids = value.split(",");
        for (String id : ids) {
            resultList.add(Long.parseLong(id));
        }
        return resultList;
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
        StringBuilder builder = new StringBuilder("id,type,name,status,description,epic");
        builder.append(System.lineSeparator());
        appendTasksInCsv(builder);
        builder.append(System.lineSeparator());
        builder.append(toString(getHistoryManager()));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(builder.toString());
        } catch (IOException ex) {
            throw new ManagerSaveException(ex.getMessage(), ex.getCause());
        }
    }

    private void appendTasksInCsv(final StringBuilder builder) {
        for (Task task : getTasks()) {
            appendTaskCsvString(builder, task, TaskType.TASK);
            builder.append(System.lineSeparator());
        }
        for (EpicTask epic : getEpicTasks()) {
            appendTaskCsvString(builder, epic, TaskType.EPIC);
            builder.append(System.lineSeparator());
        }
        for (Subtask subtask : getSubtasks()) {
            appendTaskCsvString(builder, subtask, TaskType.SUBTASK);
            builder.append(",");
            builder.append(subtask.getEpicTaskID());
            builder.append(System.lineSeparator());
        }
    }

    private void appendTaskCsvString(final StringBuilder builder, final Task task, final TaskType type) {
        builder.append(task.getID());
        builder.append(",");
        builder.append(type.name());
        builder.append(",");
        builder.append(task.getName());
        builder.append(",");
        builder.append(task.getStatus().name());
        builder.append(",");
        builder.append(task.getDescription());
    }

    static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException() {
            super();
        }

        public ManagerSaveException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
