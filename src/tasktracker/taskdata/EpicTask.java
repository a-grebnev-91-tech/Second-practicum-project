package tasktracker.taskdata;

import java.util.ArrayList;
import java.util.Objects;

public class EpicTask extends Task {
    private final ArrayList<Long> subtasksID;

    public EpicTask(String name, String description) {
        super(name, description);
        subtasksID = new ArrayList<>();
    }

    public EpicTask(String name, String description, ArrayList<Long> subtasksID) {
        super(name, description);
        this.subtasksID = new ArrayList<>();
        if (subtasksID != null)
            this.subtasksID.addAll(subtasksID);
    }

    public EpicTask(long id, ArrayList<Long> subtasksID, TaskStatus status, String name, String description) {
        super(id, status, name, description );
        this.subtasksID = new ArrayList<>();
        if (subtasksID != null)
            this.subtasksID.addAll(subtasksID);
    }

    public void addSubtask(long subtaskID) {
        if (subtaskID > 0)
            subtasksID.add(subtaskID);
    }

    public boolean removeSubtask(long subtaskID) {
        return subtasksID.remove(subtaskID);
    }

    public void removeAllSubtasks() {
        subtasksID.clear();
    }

    public ArrayList<Long> getSubtasksID() {
        return new ArrayList<>(subtasksID);
    }

    @Override
    public EpicTask clone() {
        return new EpicTask(getID(), getSubtasksID(), getStatus(), getName(), getDescription());
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "id=" + getID() +
                ", status=" + getStatus() +
                ", name='" + getName() + '\'' +
                ", description.length='" + (getDescription() != null ? getDescription().length() : null) + '\'' +
                ", subtasksID=" + subtasksID +
                '}';
    }
}
