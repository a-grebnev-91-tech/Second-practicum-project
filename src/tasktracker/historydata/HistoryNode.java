package tasktracker.historydata;

import tasktracker.taskdata.Task;

public class HistoryNode {
    private HistoryNode prev;
    private HistoryNode next;
    private Task data;

    public HistoryNode() {
    }

    public HistoryNode(Task task) {
        this.data = task.clone();
    }

    public HistoryNode getPrev() {
        return prev;
    }

    public void setPrev(HistoryNode prev) {
        this.prev = prev;
    }

    public HistoryNode getNext() {
        return next;
    }

    public void setNext(HistoryNode next) {
        this.next = next;
    }

    public Task getData() {
        return data.clone();
    }

    public void setData(Task data) {
        this.data = data.clone();
    }
}
