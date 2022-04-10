package tasktracker.taskdata;

import java.util.ArrayList;

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

    public EpicTask(long id, TaskStatus status, String name, String description) {
        super(id, status, name, description);
        subtasksID = new ArrayList<>();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        EpicTask epicTask = (EpicTask) o;

        return subtasksID != null ? subtasksID.equals(epicTask.subtasksID) : epicTask.subtasksID == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (subtasksID != null ? subtasksID.hashCode() : 0);
        return result;
    }
}
