package tasktracker.manager;

import tasktracker.taskdata.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Long, Task> tasks;
    private final HashMap<Long, EpicTask> epicTasks;
    private final HashMap<Long, Subtask> subtasks;
    private final HistoryManager historyManager;
    private long id;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = new InMemoryHistoryManager();
        id = 1;
    }

    @Override
    public List<Task> history() {
        List<Long> IDs = historyManager.getHistory();
        List<Task> history = new ArrayList<>();
        for (Long id : IDs) {
            if (tasks.containsKey(id))
                history.add(tasks.get(id).clone());
            else if (epicTasks.containsKey(id))
                history.add(epicTasks.get(id).clone());
            else if (subtasks.containsKey(id))
                history.add(subtasks.get(id).clone());
        }
        return history;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<EpicTask> getEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllTasks() {
        historyManager.remove(tasks.keySet());
        tasks.clear();
    }

    @Override
    public void removeAllEpicTasks() {
        historyManager.remove(subtasks.keySet());
        historyManager.remove(epicTasks.keySet());
        subtasks.clear();
        epicTasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        historyManager.remove(subtasks.keySet());
        for (Subtask subtask : subtasks.values())
            removeSubtaskFromEpicTask(subtask);
        subtasks.clear();
    }

    @Override
    public Task getTask(long id) {
        Task task = tasks.get(id);
        if (task == null)
            return null;
        historyManager.add(id);
        return task.clone();
    }

    @Override
    public EpicTask getEpicTask(long id) {
        EpicTask task = epicTasks.get(id);
        if (task == null)
            return null;
        historyManager.add(id);
        return task.clone();
    }

    @Override
    public Subtask getSubtask(long id) {
        Subtask task = subtasks.get(id);
        if (task == null)
            return null;
        historyManager.add(id);
        return task.clone();
    }

    @Override
    public boolean createTask(Task task) {
        if (isTaskIsValid(task)) {
            long currentID = generateId();
            task.setID(currentID);
            task = task.clone();
            tasks.put(currentID, task);
            return tasks.containsValue(task);
        }
        return false;
    }

    @Override
    public boolean createEpicTask(EpicTask epicTask) {
        if (isEpicTaskIsValid(epicTask)) {
            long currentID = generateId();
            epicTask.setID(currentID);
            updateEpicStatus(epicTask);
            epicTask = epicTask.clone();
            epicTasks.put(currentID, epicTask);
            return epicTasks.containsValue(epicTask);
        }
        return false;
    }

    @Override
    public boolean createSubtask(Subtask subtask) {
        if (isSubtaskIsValid(subtask)) {
            long currentID = generateId();
            subtask.setID(currentID);
            subtask = subtask.clone();
            subtasks.put(currentID, subtask);
            addSubtaskToEpicTask(subtask);
            return subtasks.containsValue(subtask);
        }
        return false;
    }

    @Override
    public boolean updateTask(Task task) {
        if (isTaskIsValid(task) && tasks.containsKey(task.getID())) {
            tasks.put(task.getID(), task.clone());
            return true;
        }
        return false;
    }

    @Override
    public boolean updateEpicTask(EpicTask epicTask) {
        if (isEpicTaskIsValid(epicTask) && epicTasks.containsKey(epicTask.getID())) {
            removeOrphanedSubtasks(epicTask);
            epicTasks.put(epicTask.getID(), epicTask.clone());
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        if (isSubtaskIsValid(subtask) && subtasks.containsKey(subtask.getID())) {
            subtask = subtask.clone();
            subtasks.put(subtask.getID(), subtask);
            updateEpicStatus(epicTasks.get(subtask.getEpicTaskID()));
            return true;
        }
        return false;
    }

    @Override
    public boolean removeTask(long id) {
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            tasks.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeEpicTask(long id) {
        if (epicTasks.containsKey(id)) {
            ArrayList<Long> subtasksId = epicTasks.get(id).getSubtasksID();
            historyManager.remove(subtasksId);
            historyManager.remove(id);
            epicTasks.remove(id);
            for (Long subtaskID : subtasksId) {
                removeSubtask(subtaskID);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean removeSubtask(long id) {
        if (subtasks.containsKey(id)) {
            removeSubtaskFromEpicTask(subtasks.get(id));
            historyManager.remove(id);
            subtasks.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Subtask> getEpicTaskSubtasks(EpicTask epicTask) {
        ArrayList<Subtask> result = new ArrayList<>();
        if (epicTask != null && epicTasks.containsKey(epicTask.getID())) {
            for (Long subtaskID : epicTask.getSubtasksID()) {
                result.add(subtasks.get(subtaskID));
            }
        }
        return result;
    }

    private long generateId() {
        return id++;
    }

    private boolean addSubtaskToEpicTask(Subtask subtask) {
        if (isSubtaskIsValid(subtask) && subtasks.containsKey(subtask.getID())) {
            EpicTask epicTaskForCurrentSubtask = epicTasks.get(subtask.getEpicTaskID());
            epicTaskForCurrentSubtask.addSubtask(subtask.getID());
            updateEpicStatus(epicTaskForCurrentSubtask);
            return true;
        }
        return false;
    }

    private void removeSubtaskFromEpicTask(Subtask subtask) {
        long subtaskID = subtask.getID();
        long epicID = subtask.getEpicTaskID();
        if (epicTasks.containsKey(epicID)) {
            epicTasks.get(epicID).removeSubtask(subtaskID);
            updateEpicStatus(epicTasks.get(epicID));
        }
    }

    private void removeOrphanedSubtasks(EpicTask epicTask) {
        long epicID = epicTask.getID();
        ArrayList<Long> epicSubtasksID = epicTask.getSubtasksID();
        ArrayList<Long> subtasksIDInMemory = new ArrayList<>();
        for (Subtask subtaskInMemory : subtasks.values()) {
            if (subtaskInMemory.getEpicTaskID() == epicID) {
                subtasksIDInMemory.add(subtaskInMemory.getID());
            }
        }
        for (Long IDInMemory : subtasksIDInMemory) {
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
        ArrayList<Long> subtasksID = epicTask.getSubtasksID();
        for (Long id : subtasksID) {
            if (!subtasks.containsKey(id))
                return false;
        }
        return epicTask.getStatus() == getEpicStatusBySubtasksID(subtasksID);
    }

    private void updateEpicStatus(EpicTask epicTask) {
        ArrayList<Long> subtasksID = epicTask.getSubtasksID();
        TaskStatus currentStatus = getEpicStatusBySubtasksID(subtasksID);
        epicTask.setStatus(currentStatus);
    }

    private TaskStatus getEpicStatusBySubtasksID(ArrayList<Long> subtasksID) {
        if (subtasksID == null || subtasksID.size() == 0)
            return TaskStatus.NEW;
        HashSet<TaskStatus> currentStatuses = new HashSet<>();
        for (Long subtaskID : subtasksID) {
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
