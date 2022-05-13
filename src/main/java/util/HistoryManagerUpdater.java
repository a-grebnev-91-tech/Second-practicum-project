package util;

import manager.HistoryManager;
import taskdata.EpicTask;
import taskdata.Subtask;
import taskdata.Task;

import java.util.List;
import java.util.Map;

public class HistoryManagerUpdater {

    public static void updateHistoryManager(HistoryManager historyManager,
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

    private static class HistoryMismatchException extends RuntimeException {
        public HistoryMismatchException(String message) {
            super(message);
        }

    }
}
