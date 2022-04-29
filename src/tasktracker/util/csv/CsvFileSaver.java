package tasktracker.util.csv;

import tasktracker.manager.FileBackedTaskManager;
import tasktracker.manager.HistoryManager;
import tasktracker.taskdata.EpicTask;
import tasktracker.taskdata.Subtask;
import tasktracker.taskdata.Task;
import tasktracker.taskdata.TaskType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class CsvFileSaver {
    final String file;
    final FileBackedTaskManager manager;
    final String fileHeader = "id,type,name,status,description,epic";

    public CsvFileSaver (String file, FileBackedTaskManager manager) {
        this.file = file;
        this.manager = manager;
    }

    public void save() {
        StringBuilder builder = new StringBuilder(fileHeader);
        builder.append(System.lineSeparator());
        appendTasksInCsv(builder);
        builder.append(System.lineSeparator());
        builder.append(historyToString(manager.getHistoryManager()));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(builder.toString());
        } catch (IOException ex) {
            throw new ManagerSaveException(ex.getMessage(), ex.getCause());
        }
    }

    private void appendTasksInCsv(final StringBuilder builder) {
        for (Task task : manager.getTasks()) {
            String[] values = convertTaskToStrings(task, TaskType.TASK);
            String taskLine = CsvConstructor.constructLine(values);
            builder.append(taskLine);
        }
        for (EpicTask epic : manager.getEpicTasks()) {
            String[] values = convertTaskToStrings(epic, TaskType.EPIC);
            String epicLine = CsvConstructor.constructLine(values);
            builder.append(epicLine);
        }
        for (Subtask subtask : manager.getSubtasks()) {
            String[] values = convertTaskToStrings(subtask, TaskType.SUBTASK);
            String subtaskLine = CsvConstructor.constructLine(values);
            builder.append(subtaskLine);
        }
    }

    private String[] convertTaskToStrings(Task task, TaskType type) {
        if (type == TaskType.SUBTASK) {
            return new String[]{String.valueOf(task.getID()),
                    type.name(),
                    task.getName(),
                    task.getStatus().name(),
                    task.getDescription(),
                    String.valueOf(((Subtask) task).getEpicTaskID())};
        }
        return new String[]{String.valueOf(task.getID()),
                type.name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription()};
    }

    private String historyToString(HistoryManager history) {
        StringBuilder builder = new StringBuilder();
        Iterator<Task> iterator = history.getHistory().iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next().getID());
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        return builder.toString();
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
