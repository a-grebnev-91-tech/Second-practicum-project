package tasktracker.manager;

import tasktracker.taskdata.*;

import java.util.List;

public interface TaskManager {

    List<Task> getTasks();

    List<EpicTask> getEpicTasks();

    List<Subtask> getSubtasks();

    List<Task> history();

    HistoryManager getHistoryManager();

    void removeAllTasks();

    void removeAllEpicTasks();

    void removeAllSubtasks();

    Task getTask(long id);

    EpicTask getEpicTask(long id);

    Subtask getSubtask(long id);

    boolean createTask(Task task);

    boolean createEpicTask(EpicTask epicTask);

    boolean createSubtask(Subtask subtask);

    boolean updateTask(Task task);

    boolean updateEpicTask(EpicTask epicTask);

    boolean updateSubtask(Subtask subtask);

    boolean removeTask(long id);

    boolean removeEpicTask(long id);

    boolean removeSubtask(long id);

    List<Subtask> getEpicTaskSubtasks(EpicTask epicTask);
}
