package tasktracker.manager;

import tasktracker.taskdata.*;
import tasktracker.taskdata.TaskInvalidException;
import tasktracker.util.tasks.TaskValidator;
import tasktracker.util.tasks.ValidationMessage;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Long, EpicTask> epicTasks;
    private final HistoryManager historyManager;
    private long id;
    private final TaskValidator validator;
    private final TasksPrioritizer tasksPrioritizer;
    private final Map<Long, Subtask> subtasks;
    private final Map<Long, Task> tasks;

    public InMemoryTaskManager() {
        historyManager = new InMemoryHistoryManager();
        id = 1;
        epicTasks = new HashMap<>();
        tasksPrioritizer = new TasksPrioritizer();
        subtasks = new HashMap<>();
        tasks = new HashMap<>();
        validator = new TaskValidator(epicTasks, subtasks, tasks);

    }

    public InMemoryTaskManager(Map<Long, Task> tasks,
                               Map<Long, EpicTask> epicTasks,
                               Map<Long, Subtask> subtasks,
                               HistoryManager historyManager,
                               long id) {
        this.tasks = new HashMap<>();
        for (Map.Entry<Long, Task> taskEntry : tasks.entrySet()) {
            this.tasks.put(taskEntry.getKey(), taskEntry.getValue().clone());
        }
        this.epicTasks = new HashMap<>();
        for (Map.Entry<Long, EpicTask> epicEntry : epicTasks.entrySet()) {
            this.epicTasks.put(epicEntry.getKey(), epicEntry.getValue().clone());
        }
        this.subtasks = new HashMap<>();
        for (Map.Entry<Long, Subtask> subtaskEntry : subtasks.entrySet()) {
            this.subtasks.put(subtaskEntry.getKey(), subtaskEntry.getValue().clone());
        }
        this.historyManager = historyManager.clone();
        this.id = id;
        this.tasksPrioritizer = new TasksPrioritizer();
        this.validator = new TaskValidator(this.epicTasks, this.subtasks, this.tasks);
    }

    @Override
    public boolean createEpicTask(EpicTask epicTask) {
        ValidationMessage message = validator.canCreate(epicTask);
        if (message.isValid()) {
            long currentID = generateId();
            epicTask.setID(currentID);
            updateEpicStatus(epicTask);
            epicTask = epicTask.clone();
            epicTasks.put(currentID, epicTask);
        }
        return message.isValid();
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
    public EpicTask getEpicTask(long id) {
        EpicTask task = epicTasks.get(id);
        if (task == null)
            return null;
        historyManager.add(task);
        return task.clone();
    }

    @Override
    public List<EpicTask> getEpicTasks() {
        return new ArrayList<>(epicTasks.values());
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

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager.clone();
    }

    @Override
    public List<Task> getTasksPrioritizer() {
        return null;
    }

    @Override
    public Subtask getSubtask(long id) {
        Subtask task = subtasks.get(id);
        if (task == null)
            return null;
        historyManager.add(task);
        return task.clone();
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTask(long id) {
        Task task = tasks.get(id);
        if (task == null)
            return null;
        historyManager.add(task);
        return task.clone();
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
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
    public void removeAllTasks() {
        historyManager.remove(tasks.keySet());
        tasks.clear();
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
    public boolean removeTask(long id) {
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            tasks.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateEpicTask(EpicTask epicTask) {
        if (isEpicTaskIsValid(epicTask) && epicTasks.containsKey(epicTask.getID())) {
            removeOrphanedSubtasks(epicTask);
            epicTasks.put(epicTask.getID(), epicTask.clone());
            updateTaskInHistory(epicTask);
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
            updateTaskInHistory(subtask);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateTask(Task task) {
        if (isTaskIsValid(task) && tasks.containsKey(task.getID())) {
            tasks.put(task.getID(), task.clone());
            updateTaskInHistory(task);
            return true;
        }
        return false;
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

    private long generateId() {
        return id++;
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

    private boolean isSubtaskIsValid(Subtask subtask) {
        return subtask != null && epicTasks.containsKey(subtask.getEpicTaskID());
    }

    private boolean isTaskIsValid(Task task) {
        return task != null;
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

    private void removeSubtaskFromEpicTask(Subtask subtask) {
        long subtaskID = subtask.getID();
        long epicID = subtask.getEpicTaskID();
        if (epicTasks.containsKey(epicID)) {
            epicTasks.get(epicID).removeSubtask(subtaskID);
            updateEpicStatus(epicTasks.get(epicID));
        }
    }

    private void updateEpicStatus(EpicTask epicTask) {
        ArrayList<Long> subtasksID = epicTask.getSubtasksID();
        TaskStatus currentStatus = getEpicStatusBySubtasksID(subtasksID);
        epicTask.setStatus(currentStatus);
        historyManager.updateTask(epicTask);
    }

    private void updateTaskInHistory(Task task) {
        historyManager.updateTask(task);
    }
}
