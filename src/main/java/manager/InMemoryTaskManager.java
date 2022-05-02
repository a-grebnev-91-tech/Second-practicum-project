package manager;

import taskdata.*;
import util.tasks.TaskValidator;
import util.tasks.TasksVault;
import util.tasks.ValidationMessage;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager;
    private long id;
    private final TaskValidator validator;
    private final TasksVault vault;

    public InMemoryTaskManager() {
        historyManager = new InMemoryHistoryManager();
        id = 1;
        Map<Long, EpicTask> epicTasks = new HashMap<>();
        Map<Long, Subtask> subtasks = new HashMap<>();
        Map<Long, Task> tasks = new HashMap<>();
        vault = new TasksVault(epicTasks, subtasks, tasks);
        validator = new TaskValidator(
                vault.getEpics(),
                vault.getSubtasks(),
                vault.getTasks(),
                vault.getPrioritizedTasks()
        );

    }

    public InMemoryTaskManager(Map<Long, Task> tasks,
                               Map<Long, EpicTask> epicTasks,
                               Map<Long, Subtask> subtasks,
                               HistoryManager historyManager,
                               long id) {
        Map<Long, Task> tasksToAdd = new HashMap<>();
        for (Map.Entry<Long, Task> taskEntry : tasks.entrySet()) {
            tasksToAdd.put(taskEntry.getKey(), taskEntry.getValue().clone());
        }
        Map<Long, EpicTask> epicsToAdd = new HashMap<>();
        for (Map.Entry<Long, EpicTask> epicEntry : epicTasks.entrySet()) {
            epicsToAdd.put(epicEntry.getKey(), epicEntry.getValue().clone());
        }
        Map<Long, Subtask> subtasksToAdd = new HashMap<>();
        for (Map.Entry<Long, Subtask> subtaskEntry : subtasks.entrySet()) {
            subtasksToAdd.put(subtaskEntry.getKey(), subtaskEntry.getValue().clone());
        }
        this.historyManager = historyManager.clone();
        this.id = id;
        this.vault = new TasksVault(epicsToAdd, subtasksToAdd, tasksToAdd);
        this.validator = new TaskValidator(epicTasks, subtasksToAdd, tasksToAdd, vault.getPrioritizedTasks());
    }

    @Override
    public long createTask(Task task) {
        ValidationMessage message = validator.canCreate(task);
        if (message.isValid()) {
            long id = generateId();
            task.setID(id);
            vault.add(task.clone());
            return id;
        }
        return -1L;
    }

    @Override
    public EpicTask getEpicTask(long id) {
        Task task = getTask(id);
        if (task != null && task.getType() == TaskType.EPIC) {
            return (EpicTask) task;
        }
        return null;
    }

    @Override
    public List<EpicTask> getEpicTasks() {
        return new ArrayList<>(vault.getEpics().values());
    }

    @Override
    public List<Subtask> getEpicTaskSubtasks(EpicTask epicTask) {
        return vault.getEpicTaskSubtasks(epicTask);
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager.clone();
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return vault.getPrioritizedTasks();
    }

    @Override
    public Subtask getSubtask(long id) {
        Task task = getTask(id);
        if (task != null && task.getType() == TaskType.SUBTASK) {
            return (Subtask) task;
        }
        return null;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(vault.getSubtasks().values());
    }

    @Override
    public Task getTask(long id) {
        Task task = vault.get(id);
        if (task != null) {
            historyManager.add(task);
            return task.clone();
        } else {
            return null;
        }
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(vault.getTasks().values());
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
    }

    @Override
    public void removeAllEpicTasks() {
        historyManager.remove(vault.removeAllEpics());
    }

    @Override
    public void removeAllSubtasks() {
        Collection<Long> idsToRemove = vault.removeAllSubtasks();
        historyManager.remove(idsToRemove);
    }

    @Override
    public void removeAllTasks() {
        Collection<Long> idsToRemove = vault.removeAllTask();
        historyManager.remove(idsToRemove);
    }

    @Override
    public boolean removeTask(long id) {
        Collection<Long> removedIds = vault.remove(id);
        //TODO check this, dont like, why null?
        if (removedIds != null) {
            historyManager.remove(removedIds);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateTask(Task task) {
        ValidationMessage validMessage = validator.canUpdate(task);
        if (validMessage.isValid()) {
            Task taskToUpdate = task.clone();
            vault.update(taskToUpdate);
            historyManager.update(taskToUpdate);
            return true;
        } else {
            return false;
        }
    }

    private long generateId() {
        return id++;
    }
}
