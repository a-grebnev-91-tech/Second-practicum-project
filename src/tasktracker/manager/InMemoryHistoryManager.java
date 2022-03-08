package tasktracker.manager;

import tasktracker.taskdata.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Integer> historyID;

    private final int MAX_HISTORY_SIZE = 10;

    public InMemoryHistoryManager() {
        historyID = new ArrayList<>();
    }

    @Override
    public void add(Integer id) {
        historyID.add(id);
    }

    @Override
    public void add(Collection<Integer> IDs) {
        historyID.addAll(IDs);
    }

    @Override
    public void remove(Integer id) {
        historyID.remove(id);
    }

    @Override
    public void remove(Collection<Integer> IDs) {
        historyID.removeAll(IDs);
    }

    @Override
    public List<Integer> getHistory() {
        int startIndex = historyID.size() > MAX_HISTORY_SIZE ? historyID.size() - MAX_HISTORY_SIZE : 0;
        return historyID.subList(startIndex, historyID.size());
    }
}
