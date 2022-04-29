package tasktracker.manager;

import tasktracker.taskdata.*;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    boolean createTask(Task task);

    EpicTask getEpicTask(long id);

    List<EpicTask> getEpicTasks();

    List<Subtask> getEpicTaskSubtasks(EpicTask epicTask);

    HistoryManager getHistoryManager();

    Subtask getSubtask(long id);

    List<Subtask> getSubtasks();

    Task getTask(long id);

    List<Task> getTasks();

    List<Task> history();

    void removeAllEpicTasks();

    void removeAllSubtasks();

    void removeAllTasks();

    boolean removeTask(long id);

    boolean updateTask(Task task);
}
