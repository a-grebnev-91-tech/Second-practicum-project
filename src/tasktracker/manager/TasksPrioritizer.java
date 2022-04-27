package tasktracker.manager;

import tasktracker.taskdata.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

public class TasksPrioritizer {

    private final TreeSet<Task> tasks;

    public TasksPrioritizer() {
        this.tasks = new TreeSet<>((t1, t2) -> t1.getStartTime().compareTo(t2.getStartTime()));
    }

    public TasksPrioritizer(Comparator<Task> comparator) {
        this.tasks = new TreeSet<>(comparator);
    }

    public ArrayList<Task> getTasks() {
        return null;
    }

    public boolean addTask(Task task) {
        return false;
    }

    public boolean removeTask(Task task) {
        return false;
    }

    private boolean isIntersect(Task task) {
        return true;
    }
}
