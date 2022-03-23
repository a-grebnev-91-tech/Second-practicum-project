package tasktracker.manager;

import java.util.Collection;
import java.util.List;
import tasktracker.taskdata.Task;

public interface HistoryManager {

    void add(Task task);

    void remove(Long id);

    void remove(Collection<Long> ids);

    List<Task> getHistory();

    void updateTask(Task task);
}
