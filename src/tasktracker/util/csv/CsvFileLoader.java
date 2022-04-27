package tasktracker.util.csv;

import tasktracker.manager.FileBackedTaskManager;
import tasktracker.manager.HistoryManager;
import tasktracker.manager.InMemoryHistoryManager;
import tasktracker.taskdata.*;
import tasktracker.taskdata.TaskInvalidException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO add feature that saved time to a file
public class CsvFileLoader {
    public static FileBackedTaskManager load(File file) {
        Map<Long, Task> tasks = new HashMap<>();
        Map<Long, EpicTask> epics = new HashMap<>();
        Map<Long, Subtask> subtasks = new HashMap<>();
        List<Long> historyIDs;
        HistoryManager historyManager = new InMemoryHistoryManager();
        long id = 0;
        Task currentTask;

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            while (reader.ready()) {
                line = reader.readLine();
                if (line.isBlank()) {
                    break;
                }
                currentTask = taskFromString(line);
                if (currentTask instanceof EpicTask) {
                    epics.put(currentTask.getID(), (EpicTask) currentTask);
                } else if (currentTask instanceof Subtask) {
                    subtasks.put(currentTask.getID(), (Subtask) currentTask);
                } else {
                    tasks.put(currentTask.getID(), currentTask);
                }
                if (id < currentTask.getID()) {
                    id = currentTask.getID();
                    id++;
                }
            }
            line = reader.readLine();
            historyIDs = historyFromString(line);
        } catch (IOException ex) {
            throw new FileLoadException("Не могу прочитать CSV файл", ex.getCause());
        }

        matchEpicsWithSubtasks(epics, subtasks);
        updateHistoryManager(historyManager, historyIDs, tasks, epics, subtasks);
        return new FileBackedTaskManager(tasks, epics, subtasks, historyManager, id, file.getPath());
    }

    private static void updateHistoryManager(HistoryManager historyManager,
                                             List<Long> historyIDs,
                                             Map<Long, Task> tasks,
                                             Map<Long, EpicTask> epics,
                                             Map<Long, Subtask> subtasks) {
        Task task;
        for (long id : historyIDs) {
            task = tasks.get(id);
            if (task == null) {
                task = epics.get(id);
            }
            if (task == null) {
                task = subtasks.get(id);
            }
            if (task == null) {
                throw new HistoryMismatchException("В истории обнаружена не существующая задача");
            }
            historyManager.add(task);
        }
    }

    private static void matchEpicsWithSubtasks(final Map<Long, EpicTask> epics,
                                               final Map<Long, Subtask> subtasks) {
        for (Subtask subtask : subtasks.values()) {
            long epicID = subtask.getEpicTaskID();
            if (epics.containsKey(epicID)) {
                epics.get(epicID).addSubtask(subtask.getID());
            } else {
                throw new TaskInvalidException("Найдена подзадача не привязанная к эпику");
            }
        }
    }

    private static Task taskFromString(String value) {
        try {
            List<String> values = new ArrayList<>();
            CsvParser.parse(value, values);
            long id = Long.parseLong(values.get(0));
            TaskType type = TaskType.valueOf(values.get(1));
            String name = values.get(2);
            TaskStatus status = TaskStatus.valueOf(values.get(3));
            String description = values.get(4);
            switch (type) {
                case TASK:
                    return new Task(id, status, name, description);
                case EPIC:
                    return new EpicTask(id, status, name, description);
                case SUBTASK:
                    long epicID = Long.parseLong(values.get(5));
                    return new Subtask(id, epicID, status, name, description);
                default:
                    throw new TaskInvalidException("Неизвестный тип задачи");
            }
        } catch (IllegalArgumentException ex) {
            throw new FileLoadException("CSV файл имеет недопустимый вид", ex.getCause());
        }
    }

    private static List<Long> historyFromString(String value) {
        List<Long> resultList = new ArrayList<>();
        if (value != null && !value.isBlank()) {
            String[] ids = value.split(",");
            for (String id : ids) {
                resultList.add(Long.parseLong(id));
            }
        }
        return resultList;
    }


    static class FileLoadException extends RuntimeException {
        public FileLoadException() {
            super();
        }

        public FileLoadException(String message) {
            super(message);
        }

        public FileLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static class HistoryMismatchException extends RuntimeException {
        public HistoryMismatchException() {
        }

        public HistoryMismatchException(String message) {
            super(message);
        }

        public HistoryMismatchException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
