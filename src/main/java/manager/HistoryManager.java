package manager;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import taskdata.Task;

public interface HistoryManager extends Cloneable {

    void add(Task task);

    HistoryManager clone();

    List<Task> getHistory();

    void remove(Long id);

    void remove(Collection<Long> ids);

    void update(Task task);

    void update(Collection<Task> tasks);
}
