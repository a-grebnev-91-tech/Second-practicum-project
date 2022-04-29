package tasktracker.util.tasks;

import tasktracker.taskdata.EpicTask;
import tasktracker.taskdata.Subtask;
import tasktracker.taskdata.Task;
import tasktracker.taskdata.TaskType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TaskValidator {

    private final Map<Long, EpicTask> epicTasks;
    private final Map<Long, Subtask> subtasks;
    private final Map<Long, Task> tasks;

    public TaskValidator(Map<Long, EpicTask> epicTasks, Map<Long, Subtask> subtasks, Map<Long, Task> tasks) {
        this.epicTasks = epicTasks;
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
/*        if (isNoSubtasksForEpic(task)) {
            return new ValidationMessage(false, "Cannot create epic with bad subtasks id's");
        }*/ //TODO check this
        if (timeIsBusy(task)){
            return new ValidationMessage(false, "Cannot create intersect task");
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
            return new ValidationMessage(false, "Cannot create intersect task");
        }
        if (epicStatusIsChanged(task)) {
            return new ValidationMessage(false, "Cannot change epic status");
        }
        if (isNoSubtasksForEpic(task) || epicSubtasksIsChanged(task)) {
            return new ValidationMessage(false, "Cannot update epic with bad subtasks id's");
        }
        if (epicOfSubtaskIsChanged(task)) {
            return new ValidationMessage(false, "Cannot change epic for subtask");
        }
        return new ValidationMessage(true);
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

    private boolean epicSubtasksIsChanged(Task task) {
        if (task.getType() != TaskType.EPIC)
            return false;
        EpicTask newEpic = (EpicTask) task;
        EpicTask existEpic = epicTasks.get(newEpic.getID());
        List<Long> subtasksFromNew = newEpic.getSubtasksID();
        List<Long> subtasksFromExist = existEpic.getSubtasksID();
        Collections.sort(subtasksFromExist);
        Collections.sort(subtasksFromNew);
        return !subtasksFromExist.equals(subtasksFromNew);
    }

    private boolean isNoEpicForSubtask(Task task) {
        if (task.getType() != TaskType.SUBTASK)
            return false;
        Subtask subtask = (Subtask) task;
        return !epicTasks.containsKey(subtask.getEpicTaskID());
    }

    private boolean isNoSubtasksForEpic(Task task) {
        if (task.getType() != TaskType.EPIC)
            return false;
        EpicTask epicTask = (EpicTask) task;
        ArrayList<Long> subtasksID = epicTask.getSubtasksID();
        for (Long id : subtasksID) {
            if (!subtasks.containsKey(id))
                return false;
        }
        return true;
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
        return false; //TODO
    }
}
