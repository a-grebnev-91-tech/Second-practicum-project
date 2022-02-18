package Manager;

import TaskData.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class TaskManager {

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, EpicTask> epicTasks;
    private final HashMap<Integer, Subtask> subtasks;
    private int id;

    public TaskManager() {
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
        id = 1;
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<EpicTask> getEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpicTasks() {
        subtasks.clear();
        epicTasks.clear();
    }

    public void removeAllSubtasks() {
        removeAllSubtasksFromAllEpics();
        subtasks.clear();
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public EpicTask getEpicTask(int id) {
        return epicTasks.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public boolean createTask(Task task) {
        if (task == null) {
            return false;
        }
        else {
            task.setId(id);
            tasks.put(id++, task);
            return tasks.containsValue(task);
        }
    }

    public boolean createEpicTask(EpicTask epicTask) {
        if (epicTask == null) {
            return false;
        } else {
            epicTask.setId(id);
            updateEpicStatus(epicTask);
            epicTasks.put(id++, epicTask);
            return epicTasks.containsValue(epicTask);
        }
    }

    public boolean createSubtask(Subtask subtask) {
        if (isSubtaskIsValid(subtask)) {
            subtask.setId(id);
            subtasks.put(id, subtask);
            addSubtaskToEpicTask(subtask);

            id++;
            return subtasks.containsValue(subtask);
        } else {
            return false;
        }
    }

    private void addSubtaskToEpicTask(Subtask subtask) {
        EpicTask epicTaskForCurrentSubtask = epicTasks.get(subtask.getEpicTaskID());
        epicTaskForCurrentSubtask.addSubtask(id);
        updateEpicStatus(epicTaskForCurrentSubtask);
    }

    private boolean isSubtaskIsValid(Subtask subtask) {
        return subtask != null && epicTasks.containsKey(subtask.getEpicTaskID()) && subtask.getStatus() != null;
    }

    private void removeAllSubtasksFromAllEpics() {
        for (EpicTask epicTask : epicTasks.values()) {
            epicTask.removeAllSubtasks();
            updateEpicStatus(epicTask);
        }
    }

    private void updateEpicStatus(EpicTask epicTask) {
        ArrayList<Integer> subtasksID = epicTask.getSubtasksID();
        TaskStatus currentStatus = getEpicStatusBySubtasksID(subtasksID);
        epicTask.setStatus(currentStatus);
    }

    private TaskStatus getEpicStatusBySubtasksID(ArrayList<Integer> subtasksID) {
        if (subtasksID == null || subtasksID.size() == 0)
            return TaskStatus.NEW;
        HashSet<TaskStatus> currentStatuses = new HashSet<>();
        for (Integer subtaskID : subtasksID) {
            if (subtaskID < 1)
                throw new TaskInvalidException("Обнаружен невалидный id");
            Subtask currentSubtask = subtasks.get(subtaskID);
            if (currentSubtask == null || currentSubtask.getStatus() == null)
                throw new TaskInvalidException("Обнаружена невалидная подзадача");
            if (currentSubtask.getStatus() == TaskStatus.IN_PROGRESS)
                return TaskStatus.IN_PROGRESS;
            else
                currentStatuses.add(currentSubtask.getStatus());
        }
        if (currentStatuses.size() != 1) {
            return TaskStatus.IN_PROGRESS;
        }
        else {
            TaskStatus[] taskStatuses = new TaskStatus[1];
            return currentStatuses.toArray(taskStatuses)[0];
        }
    }
}
