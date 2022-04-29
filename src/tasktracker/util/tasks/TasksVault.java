package tasktracker.util.tasks;

import tasktracker.taskdata.*;

import java.util.*;

public class TasksVault {

    private final Map<Long, EpicTask> epicTasks;
    private final Map<Long, Subtask> subtasks;
    private final Map<Long, Task> tasks;

    public TasksVault(Map<Long, EpicTask> epicTasks, Map<Long, Subtask> subtasks, Map<Long, Task> tasks) {
        this.epicTasks = epicTasks;
        this.subtasks = subtasks;
        this.tasks = tasks;
    }

    public void add(long currentID, Task task) {
        task.setID(currentID);
        TaskType type = task.getType();
        switch (type) {
            case TASK:
                tasks.put(currentID, task.clone());
                break;
            case EPIC:
                EpicTask epic = (EpicTask) task;
                epicTasks.put(currentID, epic.clone());
                break;
            case SUBTASK:
                Subtask subtask = (Subtask) task;
                subtasks.put(currentID, subtask.clone());
                addSubtaskToEpic(subtask);
        }
    }

    public Task get(Long id) {
        Task task = tasks.get(id);
        if (task != null)
            return task;
        else
            task = epicTasks.get(id);
        if (task != null)
            return task;
        else
            return subtasks.get(id);
    }

    public Collection<EpicTask> getAllEpics() {
        return epicTasks.values();
    }

    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    public Collection<Task> getAllTasks() {
        return tasks.values();
    }

    public List<Subtask> getEpicTaskSubtasks(EpicTask epic) {
        ArrayList<Subtask> result = new ArrayList<>();
        for (Long subtaskID : epic.getSubtasksID()) {
                result.add(subtasks.get(subtaskID));
        }
        return result;
    }

    public Collection<Long> removeAllEpics() {
        Collection<Long> removedIds = epicTasks.keySet();
        removedIds.addAll(subtasks.keySet());
        epicTasks.clear();
        subtasks.clear();
        return removedIds;
    }

    public Collection<Long> removeAllSubtasks() {
        Collection<Long> removedIds = subtasks.keySet();
        for (EpicTask epicToUpdate : epicTasks.values()) {
            epicToUpdate.setStatus(TaskStatus.NEW);
        }
        subtasks.clear();
        return removedIds;
    }

    public Collection<Long> removeAllTask() {
        Collection<Long> removedIds = tasks.keySet();
        tasks.clear();
        return removedIds;
    }

    //TODO double check this
    public Collection<Long> remove(long id) {
        if (tasks.remove(id) != null) {
            return Collections.singleton(id);
        }
        EpicTask epic = epicTasks.remove(id);
        if (epic != null) {
            Collection<Long> removedIds = new ArrayList<>();
            removedIds.add(id);
            for (Long subtaskId : epic.getSubtasksID()) {
                removedIds.addAll(remove(subtaskId));
            }
            return removedIds;
        }
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            removeSubtaskFromEpic(subtask);
            return Collections.singleton(id);
            // при удалении подтаски, в истории нужно удалить эти подтаски, и отредактировать эпик - это проблемка
            //TODO проверить это, проблему решил путем отсутсвия клонов в истории
        }
        return null;
    }

    public void update(Task task) {
        switch (task.getType()) {
            case EPIC:
                epicTasks.put(task.getID(), (EpicTask) task);
                break;
            case SUBTASK:
                Subtask subtask = (Subtask) task;
                subtasks.put(subtask.getID(), subtask);
                updateEpicStatus(subtask.getEpicTaskID());
                break;
            case TASK:
                tasks.put(task.getID(), task);
                break;
        }
    }

    private void addSubtaskToEpic(Subtask subtask) {
        EpicTask currentEpic = epicTasks.get(subtask.getEpicTaskID());
        currentEpic.addSubtask(subtask.getID());
        updateEpicStatus(currentEpic);
    }

    private TaskStatus getEpicStatusBySubtasksID(ArrayList<Long> subtasksID) {
        if (subtasksID == null || subtasksID.size() == 0)
            return TaskStatus.NEW;
        HashSet<TaskStatus> currentStatuses = new HashSet<>();
        for (Long subtaskID : subtasksID) {
            Subtask currentSubtask = subtasks.get(subtaskID);
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

    private Long removeSubtaskFromEpic(Subtask subtask) {
        long subtaskID = subtask.getID();
        long epicID = subtask.getEpicTaskID();
        if (epicTasks.containsKey(epicID)) {
            epicTasks.get(epicID).removeSubtask(subtaskID);
            updateEpicStatus(epicTasks.get(epicID));
        }
        return epicID;
    }

    private void updateEpicStatus(EpicTask epic) {
        TaskStatus currentStatus = getEpicStatusBySubtasksID(epic.getSubtasksID());
        epic.setStatus(currentStatus);
        //TODO update epic in history manager when update epic, and when delete subtask(s)
    }

    private void updateEpicStatus(Long id) {
        updateEpicStatus(epicTasks.get(id));
    }
}
