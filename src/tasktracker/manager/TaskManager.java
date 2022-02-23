package tasktracker.manager;

import tasktracker.taskdata.*;

import java.util.*;

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
        for (Subtask subtask : subtasks.values())
            removeSubtaskFromEpicTask(subtask);
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
        if (isTaskIsValid(task)) {
            task.setID(id);
            task = task.clone();
            tasks.put(id++, task);
            return tasks.containsValue(task);
        }
        return false;
    }

    public boolean createEpicTask(EpicTask epicTask) {
        if (isEpicTaskIsValid(epicTask)) {
            epicTask.setID(id);
            updateEpicStatus(epicTask);
            epicTask = epicTask.clone();
            epicTasks.put(id++, epicTask);
            return epicTasks.containsValue(epicTask);
        }
        return false;
    }

    public boolean createSubtask(Subtask subtask) {
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
        if (isTaskIsValid(task) && tasks.containsKey(task.getID())) {
            tasks.put(task.getID(), task.clone());
            return true;
        }
        return false;
    }

    public boolean updateEpicTask(EpicTask epicTask) {
        if (isEpicTaskIsValid(epicTask) && epicTasks.containsKey(epicTask.getID())) {
            removeOrphanedSubtasks(epicTask);
            epicTasks.put(epicTask.getID(), epicTask.clone());
            return true;
        }
        return false;
    }

    public boolean updateSubtask(Subtask subtask) {
        if (isSubtaskIsValid(subtask) && subtasks.containsKey(subtask.getID())) {
            subtask = subtask.clone();
            subtasks.put(subtask.getID(), subtask);
            updateEpicStatus(epicTasks.get(subtask.getEpicTaskID()));
            return true;
        }
        return false;
    }

    public boolean removeTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            return true;
        }
        return false;
    }

    public boolean removeEpicTask(int id) {
        if (epicTasks.containsKey(id)) {
            ArrayList<Integer> subtasksId = epicTasks.get(id).getSubtasksID();
            epicTasks.remove(id);
            for (Integer subtaskID : subtasksId) {
                removeSubtask(subtaskID);
            }
            return true;
        }
        return false;
    }

    public boolean removeSubtask(int id) {
        if (subtasks.containsKey(id)) {
            removeSubtaskFromEpicTask(subtasks.get(id));
            subtasks.remove(id);
            return true;
        }
        return false;
    }

    public List<Subtask> getEpicTaskSubtasks(EpicTask epicTask) {
        ArrayList<Subtask> result = new ArrayList<>();
        if (epicTask != null && epicTasks.containsKey(epicTask.getID())) {
            for (Integer subtaskID : epicTask.getSubtasksID()) {
                result.add(subtasks.get(subtaskID));
            }
        }
        return result;
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

    private void removeSubtaskFromEpicTask(Subtask subtask) {
        int subtaskID = subtask.getID();
        int epicID = subtask.getEpicTaskID();
        if (epicTasks.containsKey(epicID)) {
            epicTasks.get(epicID).removeSubtask(subtaskID);
            updateEpicStatus(epicTasks.get(epicID));
        }
    }

    private void removeOrphanedSubtasks(EpicTask epicTask) {
        int epicID = epicTask.getID();
        ArrayList<Integer> epicSubtasksID = epicTask.getSubtasksID();
        ArrayList<Integer> subtasksIDInMemory = new ArrayList<>();
        for (Subtask subtaskInMemory : subtasks.values()) {
            if (subtaskInMemory.getEpicTaskID() == epicID) {
                subtasksIDInMemory.add(subtaskInMemory.getID());
            }
        }
        for (Integer IDInMemory : subtasksIDInMemory) {
            if (!(epicSubtasksID.contains(IDInMemory))) {
                subtasks.remove(IDInMemory);
            }
        }
    }

    private boolean isTaskIsValid(Task task) {
        return task != null;
    }

    private boolean isSubtaskIsValid(Subtask subtask) {
        return subtask != null && epicTasks.containsKey(subtask.getEpicTaskID());
    }

    private boolean isEpicTaskIsValid(EpicTask epicTask) {
        if (epicTask == null)
            return false;
        ArrayList<Integer> subtasksID = epicTask.getSubtasksID();
        for (Integer id : subtasksID) {
            if (!subtasks.containsKey(id))
                return false;
        }
        return epicTask.getStatus() == getEpicStatusBySubtasksID(subtasksID);
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
