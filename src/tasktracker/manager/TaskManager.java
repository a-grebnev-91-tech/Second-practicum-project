package tasktracker.manager;

import tasktracker.taskdata.*;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    boolean createEpicTask(EpicTask epicTask);

    boolean createSubtask(Subtask subtask);

    boolean createTask(Task task);

    EpicTask getEpicTask(long id);

    List<EpicTask> getEpicTasks();

    List<Subtask> getEpicTaskSubtasks(EpicTask epicTask);

    HistoryManager getHistoryManager();

    List<Task> getTasksPrioritizer();

    Subtask getSubtask(long id);

    List<Subtask> getSubtasks();

    Task getTask(long id);

    List<Task> getTasks();

    List<Task> history();

    void removeAllEpicTasks();

    void removeAllSubtasks();

    void removeAllTasks();

    boolean removeEpicTask(long id);

    boolean removeSubtask(long id);

    boolean removeTask(long id);

    boolean updateEpicTask(EpicTask epicTask);

    boolean updateSubtask(Subtask subtask);

    boolean updateTask(Task task);
}
