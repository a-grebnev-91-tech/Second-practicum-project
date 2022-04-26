package tasktracker.manager;

import java.util.*;

import tasktracker.taskdata.Task;
import tasktracker.historydata.HistoryNode;

public class InMemoryHistoryManager implements HistoryManager, Cloneable {

    private final Map<Long, HistoryNode> history;

    private final LinkedHistoryList historyList;

    private final int MAX_HISTORY_SIZE = 10;

    public InMemoryHistoryManager() {
        history = new HashMap<>();
        historyList = new LinkedHistoryList();
    }

    public InMemoryHistoryManager(Map<Long, HistoryNode> history, LinkedHistoryList list) {
        this.history = history;
        this.historyList = list;
    }

    @Override
    public void add(final Task task) {
        final Task taskToAdd = task.clone();
        long id = taskToAdd.getID();
        remove(id);
        if (historyList.size() >= MAX_HISTORY_SIZE) {
            remove(historyList.getFirstNodeID());
        }
        history.put(id, historyList.linkLast(task));
    }

    @Override
    public InMemoryHistoryManager clone() {
        return new InMemoryHistoryManager(this.history, this.historyList);
    }

    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }

    @Override
    public void remove(Long id) {
        if (history.containsKey(id)) {
            historyList.removeNode(history.remove(id));
        }
    }

    @Override
    public void remove(Collection<Long> IDs) {
        for (Long id : IDs) {
            remove(id);
        };
    }

     @Override
     public void updateTask(final Task task) {
        if (task == null)
            return;
        final Task taskToAdd = task.clone();
        HistoryNode currentNode = history.get(taskToAdd.getID());
        if (currentNode != null) {
            currentNode.setData(taskToAdd);
        }
     }

    private static class LinkedHistoryList {
        private HistoryNode first;
        private HistoryNode last;
        private int size;

        public long getFirstNodeID() {
            if (first != null)
                return first.getData().getID();
            else
                return 0;
        }

        private void fillTasksList(List<Task> tasks, HistoryNode node) {
            if (node == null)
                return;
            fillTasksList(tasks, node.getPrev());
            tasks.add(node.getData().clone());
        }

        public List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();
            fillTasksList(tasks, last);
            return tasks;
        }

        public HistoryNode linkLast(Task task) {
            HistoryNode newNode = new HistoryNode(task);
            if(size == 0) {
                first = newNode;
                last = first;
            } else {
                newNode.setPrev(last);
                last.setNext(newNode);
                last = newNode;
            }
            size++;
            return newNode;
        }

        public int size(){
            return this.size;
        }

        public void removeNode(HistoryNode node) {
            if (node == null)
                return;
            HistoryNode prev = node.getPrev();
            HistoryNode next = node.getNext();
            if (prev != null)
                prev.setNext(next);
            else
                first = next;
            if (next != null)
                next.setPrev(prev);
            else
                last = prev;
            size--;
        }
    }
}
