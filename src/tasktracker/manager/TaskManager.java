package tasktracker.manager;

import tasktracker.taskdata.*;

import java.util.List;

public interface TaskManager {

    List<Task> getTasks();

    List<EpicTask> getEpicTasks();

    List<Subtask> getSubtasks();

    List<Task> history();

    void removeAllTasks();

    void removeAllEpicTasks();

    void removeAllSubtasks();

    Task getTask(int id);

    EpicTask getEpicTask(int id);

    Subtask getSubtask(int id);

    boolean createTask(Task task);

    boolean createEpicTask(EpicTask epicTask);

    boolean createSubtask(Subtask subtask);

    boolean updateTask(Task task);

    boolean updateEpicTask(EpicTask epicTask);

    boolean updateSubtask(Subtask subtask);

    boolean removeTask(int id);

    boolean removeEpicTask(int id);

    boolean removeSubtask(int id);

    List<Subtask> getEpicTaskSubtasks(EpicTask epicTask);
}
