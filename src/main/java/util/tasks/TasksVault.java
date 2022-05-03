package util.tasks;

import  taskdata.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class TasksVault {

    private final Comparator<Task> timeComparator
            = Comparator.comparing(
                    Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getID);
    private final Map<Long, EpicTask> epicTasks;
    private final TreeSet<Task> prioritizedTasks;
    private final Map<Long, Subtask> subtasks;
    private final Map<Long, Task> tasks;

    public TasksVault(Map<Long, EpicTask> epicTasks, Map<Long, Subtask> subtasks, Map<Long, Task> tasks) {
        this.epicTasks = epicTasks;
        this.subtasks = subtasks;
        this.tasks = tasks;
        this.prioritizedTasks = new TreeSet<>(timeComparator);
        if (!epicTasks.isEmpty()) {
            prioritizedTasks.addAll(epicTasks.values());
        }
        if (!subtasks.isEmpty()) {
            prioritizedTasks.addAll(subtasks.values());
        }
        if (!tasks.isEmpty()) {
            prioritizedTasks.addAll(tasks.values());
        }
    }

    public void add(Task task) {
        long currentID = task.getID();
        TaskType type = task.getType();
        switch (type) {
            case TASK:
                tasks.put(currentID, task);
                break;
            case EPIC:
                EpicTask epic = (EpicTask) task;
                epic.setTime(null, null);
                epicTasks.put(currentID, epic);
                break;
            case SUBTASK:
                Subtask subtask = (Subtask) task;
                subtasks.put(currentID, subtask);
                addSubtaskToEpic(subtask);
        }
        prioritizedTasks.add(task);
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

    public Map<Long, EpicTask> getEpics() {
        return epicTasks;
    }

    public Map<Long, Subtask> getSubtasks() {
        return subtasks;
    }

    public Map<Long, Task> getTasks() {
        return tasks;
    }

    public List<Subtask> getEpicTaskSubtasks(EpicTask epic) {
        ArrayList<Subtask> result = new ArrayList<>();
        for (Long subtaskID : epic.getSubtasksID()) {
                result.add(subtasks.get(subtaskID));
        }
        return result;
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    public Collection<Long> removeAllEpics() {
        Collection<Long> removedIds = new ArrayList<>(epicTasks.keySet());
        removedIds.addAll(subtasks.keySet());
        prioritizedTasks.removeAll(epicTasks.values());
        prioritizedTasks.removeAll(subtasks.values());
        epicTasks.clear();
        subtasks.clear();
        return removedIds;
    }

    public Collection<Long> removeAllSubtasks() {
        Collection<Long> removedIds = new ArrayList<>(subtasks.keySet());
        prioritizedTasks.removeAll(subtasks.values());
        for (EpicTask epicToUpdate : epicTasks.values()) {
            epicToUpdate.setStatus(TaskStatus.NEW);
            epicToUpdate.removeAllSubtasks();
        }
        subtasks.clear();
        return removedIds;
    }

    public Collection<Long> removeAllTask() {
        Collection<Long> removedIds = new ArrayList<>(tasks.keySet());
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
        return removedIds;
    }

    //TODO double check this
    public Collection<Long> remove(long id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            return Collections.singleton(id);
        }
        EpicTask epic = epicTasks.remove(id);
        if (epic != null) {
            Collection<Long> removedIds = new ArrayList<>();
            removedIds.add(id);
            for (Long subtaskId : epic.getSubtasksID()) {
                removedIds.addAll(remove(subtaskId));
            }
            prioritizedTasks.remove(epic);
            return removedIds;
        }
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            removeSubtaskFromEpic(subtask);
            prioritizedTasks.remove(subtask);
            return Collections.singleton(id);
            // при удалении подтаски, в истории нужно удалить эти подтаски, и отредактировать эпик - это проблемка
            //TODO проверить это, проблему решил путем отсутсвия клонов в истории
        }
        return null;
    }

    public Collection<Task> update(Task task) {
        Collection<Task> updatingTasks = null;
        Task oldTask = null;
        switch (task.getType()) {
            case EPIC:
                EpicTask epic = (EpicTask) task;
                updateEpicTime(epic);
                oldTask = epicTasks.put(task.getID(), epic);
                updatingTasks = Collections.singleton(epic);
                break;
            case SUBTASK:
                Subtask subtask = (Subtask) task;
                long epicId = subtask.getEpicTaskID();
                oldTask = subtasks.put(subtask.getID(), subtask);
                updateEpicStatus(epicId);
                updateEpicTime(epicTasks.get(epicId));
                updatingTasks = new ArrayList<>();
                updatingTasks.add(subtask);
                updatingTasks.add(epicTasks.get(epicId));
                break;
            case TASK:
                oldTask = tasks.put(task.getID(), task);
                updatingTasks = Collections.singleton(task);
                break;
        }
        prioritizedTasks.remove(oldTask);
        prioritizedTasks.add(task);
        return updatingTasks;
    }

    private void addSubtaskToEpic(Subtask subtask) {
        EpicTask currentEpic = epicTasks.get(subtask.getEpicTaskID());
        currentEpic.addSubtask(subtask.getID());
        updateEpicStatus(currentEpic);
        if (subtask.getStartTime() != null) {
            updateEpicTime(currentEpic);
        }
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

    private void updateEpicTime(EpicTask currentEpic) {
        List<Long> subtasksIds = currentEpic.getSubtasksID();
        LocalDateTime startTime = LocalDateTime.MAX;
        LocalDateTime endTime = LocalDateTime.MIN;
        for (Long id : subtasksIds) {
            Subtask subtask = subtasks.get(id);
            if (subtask.getStartTime() == null)
                continue;
            if (subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
            if (subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
        }
        if (startTime.isAfter(endTime)) {
            currentEpic.setTime(null, null);
            return;
        }
        Duration duration = Duration.between(startTime, endTime);
        currentEpic.setTime(startTime, duration);
    }

    private void updateEpicStatus(Long id) {
        updateEpicStatus(epicTasks.get(id));
    }
}
