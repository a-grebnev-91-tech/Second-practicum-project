package historydata;

import taskdata.Task;

public class HistoryNode {
    private HistoryNode prev;
    private HistoryNode next;
    private Task data;

    public HistoryNode() {
    }

    public HistoryNode(Task task) {
        this.data = task.clone();
    }

    public Task getData() {
        return data.clone();
    }

    public HistoryNode getNext() {
        return next;
    }

    public HistoryNode getPrev() {
        return prev;
    }

    public void setData(Task data) {
        this.data = data.clone();
    }

    public void setNext(HistoryNode next) {
        this.next = next;
    }

    public void setPrev(HistoryNode prev) {
        this.prev = prev;
    }
}
