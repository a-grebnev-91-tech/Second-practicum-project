package tasktracker.manager;

import java.util.*;

import tasktracker.taskdata.Task;
import tasktracker.historydata.HistoryNode;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Long, HistoryNode> history;
    private final LinkedHistoryList list;

    private final int MAX_HISTORY_SIZE = 10;

    public InMemoryHistoryManager() {
        history = new HashMap<>();
        list = new LinkedHistoryList();
    }

    @Override
    public void add(final Task task) {
        final Task taskToAdd = task.clone();
        long id = taskToAdd.getID();
        remove(id);
        if (list.size() >= MAX_HISTORY_SIZE) {
            remove(list.getFirstNodeID());
        }
        history.put(id, list.linkLast(task));
    }

    @Override
    public void remove(Long id) {
        if (history.containsKey(id)) {
            list.removeNode(history.remove(id));
        }
    }

    @Override
    public void remove(Collection<Long> IDs) {
        for (Long id : IDs) {
            remove(id);
        };
    }

     @Override
    public List<Task> getHistory() {
        return list.getTasks();
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

        public HistoryNode linkLast(Task task) {
            HistoryNode newNode = new HistoryNode(task);
            if(first == null) {
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

        public List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();
            fillTasksList(tasks, last);
            return tasks;
        }

        public long getFirstNodeID() {
            if (first != null)
                return first.getData().getID();
            else
                return 0;
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

        private void fillTasksList(List<Task> tasks, HistoryNode node) {
            if (node == null)
                return;
            fillTasksList(tasks, node.getPrev());
            tasks.add(node.getData().clone());
        }
    }
}
