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

    /**
     * Remove all epic tasks and all subtasks, because subtask without epic task is no sense
     */
    public void removeAllEpicTasks() {
        subtasks.clear();
        epicTasks.clear();
    }

    /**
     * Remove all subtasks and update epic of all removed subtasks
     */
    public void removeAllSubtasks() {
        HashSet<Integer> epicsID = new HashSet<>();
        for (Subtask subtask : subtasks.values()) {
            int subtaskID = subtask.getID();
            int epicID = subtask.getEpicTaskID();
            epicsID.add(epicID);
            epicTasks.get(epicID).removeSubtask(subtaskID);
        }
        for (Integer epicID : epicsID) {
            updateEpicStatus(epicTasks.get(epicID));
        }
        subtasks.clear();
    }

    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task == null)
            return null;
        return task.clone();
    }

    public EpicTask getEpicTask(int id) {
        EpicTask task = epicTasks.get(id);
        if (task == null)
            return null;
        return task.clone();
    }

    public Subtask getSubtask(int id) {
        Subtask task = subtasks.get(id);
        if (task == null)
            return null;
        return task.clone();
    }

    public boolean createTask(Task task) {
        if (task == null || tasks.containsValue(task)) {
            return false;
        } else {
            task.setID(id);
            task = task.clone();
            tasks.put(id++, task);
            return tasks.containsValue(task);
        }
    }

    public boolean createEpicTask(EpicTask epicTask) {
        if (epicTask == null || epicTasks.containsValue(epicTask)) {
            return false;
        } else {
            epicTask.setID(id);
            updateEpicStatus(epicTask);
            epicTask = epicTask.clone();
            epicTasks.put(id++, epicTask);
            return epicTasks.containsValue(epicTask);
        }
    }

    public boolean createSubtask(Subtask subtask) {
        if (subtasks.containsValue(subtask))
            return false;
            if (isSubtaskIsValid(subtask)) {
                subtask.setID(id);
                subtask = subtask.clone();
                subtasks.put(id, subtask);
                addSubtaskToEpicTask(subtask);
                id++;
                return subtasks.containsValue(subtask);
        }
        return false;
    }

    public boolean updateTask(Task task) {
        if (tasks.containsKey(task.getID())) {
            tasks.put(task.getID(), task.clone());
            return true;
        } else {
            return false;
        }
    }

    public boolean updateEpicTask(EpicTask epicTask) {
        if (epicTasks.containsKey(epicTask.getID())) {
            epicTasks.put(epicTask.getID(), epicTask.clone());
            return true;
        } else {
            return false;
        }
    }

    public boolean updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getID()) && isSubtaskIsValid(subtask)) {
            subtask = subtask.clone();
            subtasks.put(subtask.getID(), subtask);
            updateEpicStatus(epicTasks.get(subtask.getEpicTaskID()));
            return true;
        } else {
            return false;
        }
    }

    public boolean removeTask(Task task) {
        if (tasks.containsKey(task.getID())) {
            tasks.remove(task.getID());
            return true;
        } else {
            return false;
        }
    }

    public boolean removeEpicTask(EpicTask epicTask) {
        if (epicTasks.containsKey(epicTask.getID())) {
            epicTasks.remove(epicTask.getID());
            ArrayList<Integer> subtasksId = epicTask.getSubtasksID();
            for (Integer subtaskID : subtasksId) {
                removeSubtask(subtasks.get(subtaskID));
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean removeSubtask(Subtask subtask) {
        return false;
    }

    private boolean addSubtaskToEpicTask(Subtask subtask) {
        if (isSubtaskIsValid(subtask) && subtasks.containsKey(subtask.getID())) {
            EpicTask epicTaskForCurrentSubtask = epicTasks.get(subtask.getEpicTaskID());
            epicTaskForCurrentSubtask.addSubtask(id);
            updateEpicStatus(epicTaskForCurrentSubtask);
            return true;
        }
        return false;
    }

    private boolean isSubtaskIsValid(Subtask subtask) {
        return subtask != null && epicTasks.containsKey(subtask.getEpicTaskID());
    }

    private void clearAllEpicsFromSubtasksID() {
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
                throw new TaskInvalidException("Detected invalid ID");
            Subtask currentSubtask = subtasks.get(subtaskID);
            if (currentSubtask == null || currentSubtask.getStatus() == null)
                throw new TaskInvalidException("Detected invalid subtask");
            if (currentSubtask.getStatus() == TaskStatus.IN_PROGRESS)
                return TaskStatus.IN_PROGRESS;
            else
                currentStatuses.add(currentSubtask.getStatus());
        }
        if (currentStatuses.size() != 1) {
            return TaskStatus.IN_PROGRESS;
        } else {
            TaskStatus[] taskStatuses = new TaskStatus[1];
            return currentStatuses.toArray(taskStatuses)[0];
        }
    }
}
