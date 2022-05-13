package manager;

import taskdata.*;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    boolean containsTask(long id);
    long createTask(Task task);

    EpicTask getEpicTask(long id);

    List<EpicTask> getEpicTasks();

    List<Subtask> getEpicTaskSubtasks(EpicTask epicTask);

    List<Subtask> getEpicTaskSubtasks(Long epicTaskId);

    HistoryManager getHistoryManager();
    TreeSet<Task> getPrioritizedTasks();

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
