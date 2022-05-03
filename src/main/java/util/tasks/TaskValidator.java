package  util.tasks;

import taskdata.EpicTask;
import taskdata.Subtask;
import taskdata.Task;
import taskdata.TaskType;

import java.time.LocalDateTime;
import java.util.*;

public class TaskValidator {

    private final Map<Long, EpicTask> epicTasks;
    private final Map<Long, Subtask> subtasks;
    private final Map<Long, Task> tasks;
    private final TreeSet<Task> prioritizedTasks;

    public TaskValidator(Map<Long, EpicTask> epicTasks,
                         Map<Long, Subtask> subtasks,
                         Map<Long, Task> tasks,
                         TreeSet<Task> prioritizedTasks) {
        this.epicTasks = epicTasks;
        this.subtasks = subtasks;
        this.tasks = tasks;
        this.prioritizedTasks = prioritizedTasks;
    }

    public ValidationMessage canCreate(Task task) {
        if (isNull(task)) {
            return new ValidationMessage(false, "Cannot create null task");
        }
        if (isNoEpicForSubtask(task)) {
            return new ValidationMessage(false, "Cannot create subtask without epic");
        }
        if (timeIsBusy(task)){
            return new ValidationMessage(false, "Cannot create intersects task");
        }
        return new ValidationMessage(true);
    }

    public ValidationMessage canUpdate(Task task) {
        if (isNull(task)) {
            return new ValidationMessage(false, "Cannot create null task");
        }
        if (taskNotExist(task)) {
            return new ValidationMessage(false, "There are no task with id=" + task.getID());
        }
        if (timeIsBusy(task)) {
            return new ValidationMessage(false, "Cannot create intersects task");
        }
        if (epicStatusIsChanged(task)) {
            return new ValidationMessage(false, "Cannot change epic status");
        }
        if (isEpicStateIsWrong(task)) {
            return new ValidationMessage(false, "Cannot update epic with wrong state");
        }
        if (epicOfSubtaskIsChanged(task)) {
            return new ValidationMessage(false, "Cannot change epic for subtask");
        }
        return new ValidationMessage(true);
    }

    private boolean epicsSubtasksDontMatch(EpicTask newEpic, EpicTask existEpic) {
        List<Long> subtasksFromNew = newEpic.getSubtasksID();
        List<Long> subtasksFromExist = existEpic.getSubtasksID();
        Collections.sort(subtasksFromExist);
        Collections.sort(subtasksFromNew);
        return !subtasksFromExist.equals(subtasksFromNew);
    }

    private boolean epicsTimeDontMatch(EpicTask newEpic, EpicTask existEpic) {
        return !Objects.equals(newEpic.getTaskDateTime(), existEpic.getTaskDateTime());
    }

    private boolean epicOfSubtaskIsChanged(Task task) {
        if (task.getType() != TaskType.SUBTASK)
            return false;
        Subtask existSubtask = subtasks.get(task.getID());
        Subtask changedSubtask = (Subtask) task;
        return existSubtask.getEpicTaskID() != changedSubtask.getEpicTaskID();
    }

    private boolean epicStatusIsChanged(Task task) {
        if (task.getType() != TaskType.EPIC)
            return false;
        EpicTask existEpic = epicTasks.get(task.getID());
        return existEpic.getStatus() != task.getStatus();
    }

    private boolean isEpicStateIsWrong(Task task) {
        if (task.getType() != TaskType.EPIC)
            return false;
        EpicTask newEpic = (EpicTask) task;
        EpicTask existEpic = epicTasks.get(newEpic.getID());
        boolean epicSubtasksMismatch = epicsSubtasksDontMatch(newEpic, existEpic);
        boolean epicTimeMismatch = epicsTimeDontMatch(newEpic, existEpic);
        return epicSubtasksMismatch || epicTimeMismatch;
    }

    private boolean isNoEpicForSubtask(Task task) {
        if (task.getType() != TaskType.SUBTASK)
            return false;
        Subtask subtask = (Subtask) task;
        return !epicTasks.containsKey(subtask.getEpicTaskID());
    }

    private boolean isNull(Object obj) {
        return obj == null;
    }

    private boolean taskNotExist(Task task) {
        Long id = task.getID();
        if (tasks.containsKey(id))
            return false;
        if (epicTasks.containsKey(id))
            return false;
        return !subtasks.containsKey(id);
    }

    private boolean timeIsBusy(Task task) {
        if (task.getType() == TaskType.EPIC) {
            task.setTime(null, null);
            return false;
        }
        LocalDateTime newStartTime = task.getStartTime();
        if (newStartTime == null)
            return false;
        LocalDateTime newEndTime = task.getEndTime();

        Task ceilingTask = prioritizedTasks.ceiling(task);
        Task floorTask = prioritizedTasks.floor(task);

        Task trvCeilingTask = ceilingTask;
        Task trvFloorTask = floorTask;

        boolean isCeilingTheSameTask = ceilingTask != null && task.getID() == ceilingTask.getID();
        boolean isFloorTheSameTask = floorTask != null && task.getID() == floorTask.getID();

        if (isCeilingTheSameTask) {
            prioritizedTasks.remove(ceilingTask);
            ceilingTask = prioritizedTasks.ceiling(task);
        }
        if (isFloorTheSameTask) {
            prioritizedTasks.remove(floorTask);
            floorTask = prioritizedTasks.floor(task);
        }

        boolean isCeilingIntersect = false;
        boolean isFlorIntersect = false;
        if (ceilingTask != null && ceilingTask.getStartTime() != null) {
            isCeilingIntersect = newEndTime.isAfter(ceilingTask.getStartTime());
        }
        if (floorTask != null && floorTask.getStartTime() != null) {
            isFlorIntersect = newStartTime.isBefore(floorTask.getEndTime());
        }

        if (isFloorTheSameTask) {
            prioritizedTasks.add(trvFloorTask);
        }
        if (isCeilingTheSameTask) {
            prioritizedTasks.add(trvCeilingTask);
        }

        return isCeilingIntersect || isFlorIntersect;
    }
}
