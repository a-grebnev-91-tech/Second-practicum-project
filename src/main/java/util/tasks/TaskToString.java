package util.tasks;

import taskdata.EpicTask;
import taskdata.Subtask;
import taskdata.Task;

import java.time.format.DateTimeFormatter;

public class TaskToString {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm, dd.MM.yyyy");

    public static String getString(Task task) {
        StringBuilder builder = new StringBuilder("Task{");
        appendCommonPart(builder, task);
        builder.append('}');
        return builder.toString();
    }

    public static String getString(EpicTask epic) {
        StringBuilder builder = new StringBuilder("EpicTask{");
        appendCommonPart(builder, epic);
        builder.append(", subtasksID=");
        builder.append(epic.getSubtasksID());
        builder.append('}');
        return builder.toString();
    }

    public static String getString(Subtask subtask) {
        StringBuilder builder = new StringBuilder("Subtask{");
        appendCommonPart(builder, subtask);
        builder.append(", epicTaskID=");
        builder.append(subtask.getEpicTaskID());
        builder.append('}');
        return builder.toString();
    }

    private static void appendCommonPart(StringBuilder builder, Task task) {
        builder.append("id=");
        builder.append(task.getID());
        builder.append(", status=");
        builder.append(task.getStatus());
        builder.append(", name='");
        builder.append(task.getName());
        builder.append("'");
        if (task.getDescription() != null && !task.getDescription().isBlank()) {
            builder.append(", description.length()=");
            builder.append(task.getDescription().length());
        }
        if (task.getStartTime() != null && task.getDuration() != null) {
            builder.append(", startTime='");
            builder.append(task.getStartTime().format(FORMATTER));
            builder.append("', duration='");
            builder.append(task.getDuration().toMinutes());
            builder.append(" minutes'");
        }
    }
}
