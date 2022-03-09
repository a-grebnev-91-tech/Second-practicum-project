package tasktracker.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Long> historyID;

    private final int MAX_HISTORY_SIZE = 10;

    public InMemoryHistoryManager() {
        historyID = new ArrayList<>();
    }

    /**
     * Сделал, как вы просили, однако в такой реализации есть некоторый недостаток. Например мы просмотрели 15 задач, а
     * потом из приложения удалили 3 задачи, которые попали в историю. С той реализацией, которая была у меня мы все
     * равно увидим 10 задач в истории, а в версии, как предлагаете вы, мы увидим историю только из 7 задач.
     * Хотя наверное в моей реализации стоило добавить ограничение на максимальный размер истории, условно в 100pcs,
     * при достижении которого удалялась какая-нибудь часть задач, я думал это сделать, но забыл.
     */
    @Override
    public void add(Long id) {
        if (historyID.size() >= MAX_HISTORY_SIZE) {
            historyID.remove(0);
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
