package tasktracker.util.tasks;

import tasktracker.taskdata.EpicTask;
import tasktracker.taskdata.Subtask;
import tasktracker.taskdata.Task;
import tasktracker.taskdata.TaskType;

import java.util.Map;

public class TaskValidator {

    private final Map<Long, EpicTask> epics;
    private final Map<Long, Subtask> subtasks;
    private final Map<Long, Task> tasks;

    public TaskValidator(Map<Long, EpicTask> epics, Map<Long, Subtask> subtasks, Map<Long, Task> tasks) {
        this.epics = epics;
        this.subtasks = subtasks;
        this.tasks = tasks;
    }

    public ValidationMessage canCreate(Task task) {
        if (isNull(task)) {
            return new ValidationMessage(false, "Cannot create null task");
        }
        if (isNoEpicForSubtask(task)) {
            return new ValidationMessage(false, "Cannot create subtask without epic");
        }
        if (timeIsBusy(task)){
            return new ValidationMessage(false, "Cannot create intersect task");
        }
        return new ValidationMessage(true);
    }

    public ValidationMessage canUpdate(Task task) {
        if (isNull(task)) {
            return new ValidationMessage(false, "Cannot create null task");
        }
        if (isTaskNotExist(task)) {
            return new ValidationMessage(false, "There are no task with id=" + task.getID());
        }
        if (timeIsBusy(task)) {
            return new ValidationMessage(false, "Cannot create intersect task");
        }
        if (epicStatusIsChanged(task)) {
            return new ValidationMessage(false, "Cannot change epic status");
        }
        if (epicIsChanged(task)) {
            return new ValidationMessage(false, "Cannot change epic for subtask");
        }
        return new ValidationMessage(true);
    }

    private boolean epicIsChanged(Task task) {
        if (task.getType() != TaskType.SUBTASK)
            return false;
        Subtask existSubtask = subtasks.get(task.getID());
        Subtask changedSubtask = (Subtask) task;
        return existSubtask.getEpicTaskID() != changedSubtask.getEpicTaskID();
    }

    private boolean epicStatusIsChanged(Task task) {
        if (task.getType() != TaskType.EPIC)
            return false;
        EpicTask existEpic = epics.get(task.getID());
        return existEpic.getStatus() != task.getStatus();
    }

    private boolean isNoEpicForSubtask(Task task) {
        if (task.getType() != TaskType.SUBTASK)
            return false;
        Subtask subtask = (Subtask) task;
        return epics.containsKey(subtask.getEpicTaskID());
    }

    private boolean isNull(Object obj) {
        return obj == null;
    }

    private boolean isTaskNotExist(Task task) {
        Long id = task.getID();
        if (tasks.containsKey(id))
            return true;
        if (epics.containsKey(id))
            return true;
        return subtasks.containsKey(id);
    }

    private boolean timeIsBusy(Task task) {
        throw new RuntimeException("not implement");
    }
}
