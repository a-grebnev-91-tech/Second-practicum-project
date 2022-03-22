package tasktracker.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;

public class InMemoryHistoryManager implements HistoryManager {

    private final Set<Long> historyID;

    private final int MAX_HISTORY_SIZE = 10;

    public InMemoryHistoryManager() {
        historyID = new LinkedHashSet<>();
    }

    @Override
    public void add(Long id) {
        if (historyID.contains(id)) {
            historyID.remove(id);
        }
        if (historyID.size() >= MAX_HISTORY_SIZE) {
            historyID.remove(historyID.iterator().next());
        }
        historyID.add(id);
    }

    @Override
    public void remove(Long id) {
        historyID.removeIf(aLong -> aLong.equals(id));
    }

    @Override
    public void remove(Collection<Long> IDs) {
        historyID.removeAll(IDs);
    }

    @Override
    public List<Long> getHistory() {
        return new ArrayList<>(historyID);
    }
}
