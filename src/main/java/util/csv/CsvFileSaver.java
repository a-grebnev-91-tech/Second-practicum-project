package util.csv;

import manager.FileBackedTaskManager;
import manager.HistoryManager;
import taskdata.EpicTask;
import taskdata.Subtask;
import taskdata.Task;
import taskdata.TaskType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class CsvFileSaver {
    private final String file;
    public static final String FILE_HEADER = "id,type,name,status,description,epic";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
    private final FileBackedTaskManager manager;

    public CsvFileSaver (String file, FileBackedTaskManager manager) {
        this.file = file;
        this.manager = manager;
    }

    public void save() {
        StringBuilder builder = new StringBuilder(FILE_HEADER);
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
        String startTime = "";
        String duration = "";
        if (task.getStartTime() != null) {
            startTime = task.getStartTime().format(FORMATTER);
            duration = String.valueOf(task.getDuration().toMinutes());
        }
        if (type == TaskType.SUBTASK) {
            return new String[]{String.valueOf(task.getID()),
                    type.name(),
                    task.getName(),
                    task.getStatus().name(),
                    task.getDescription(),
                    startTime,
                    duration,
                    String.valueOf(((Subtask) task).getEpicTaskID()),
            };
        }
        return new String[]{String.valueOf(task.getID()),
                type.name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                startTime,
                duration
        };
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
