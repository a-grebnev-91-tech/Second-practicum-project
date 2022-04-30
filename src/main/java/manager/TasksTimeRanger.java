package manager;

import taskdata.EpicTask;
import taskdata.Subtask;
import taskdata.Task;

import java.util.*;

public class TasksTimeRanger {

    private final TreeSet<Task> tasks;

    public TasksTimeRanger() {
        Comparator<Task> defaultComparator
                = Comparator.nullsFirst((t1, t2) -> t1.getTaskDateTime().getStartTime().compareTo(t2.getStartTime()));
        this.tasks = new TreeSet<>(defaultComparator);
    }

    public TasksTimeRanger(Comparator<Task> comparator) {
        this.tasks = new TreeSet<>(comparator);
    }

    public TasksTimeRanger(Map<Long, EpicTask> epicsToAdd,
                           Map<Long, Subtask> subtasksToAdd,
                           Map<Long, Task> tasksToAdd) {
        this();
        loadTasks(epicsToAdd.values().toArray(new Task[0]));
        loadTasks(subtasksToAdd.values().toArray(new Task[0]));
        loadTasks(tasksToAdd.values().toArray(new Task[0]));
    }

    public boolean addTask(Task task) {
        return false;
    }

    private void loadTasks(Task[] tasks) {
        this.tasks.addAll(List.of(tasks));
    }

    private boolean isIntersect(Task task) {
        return true;
    }

    public TreeSet<Task> getTasks() {
        return this.tasks;
    }

    public boolean removeTask(Task task) {
        return false;
    }

    //ПРИ ДОБАВЛЕНИИ ПОДТАСКИ РАСЧИТЫВАТЬ И ОБНОВЛЯТЬ ВРЕМЯ ЭПИКА
}
