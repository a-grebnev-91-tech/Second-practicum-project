package tasktracker.taskdata;

import java.util.ArrayList;
import java.util.Objects;

public class EpicTask extends Task {
    private final ArrayList<Integer> subtasksID;

    public EpicTask(String name, String description) {
        super(name, description);
        subtasksID = new ArrayList<>();
    }

    public EpicTask(String name, String description, ArrayList<Integer> subtasksID) {
        super(name, description);
        this.subtasksID = new ArrayList<>();
        if (subtasksID != null)
            this.subtasksID.addAll(subtasksID);
    }

    public EpicTask(int id, ArrayList<Integer> subtasksID, TaskStatus status, String name, String description) {
        super(id, status, name, description );
        this.subtasksID = new ArrayList<>();
        if (subtasksID != null)
            this.subtasksID.addAll(subtasksID);
    }

    public void addSubtask(int subtaskID) {
        if (subtaskID > 0)
            subtasksID.add(subtaskID);
    }

    public boolean removeSubtask(int subtaskID) {
        return subtasksID.remove(Integer.valueOf(subtaskID));
    }

    public void removeAllSubtasks() {
        subtasksID.clear();
    }

    public ArrayList<Integer> getSubtasksID() {
        return new ArrayList<>(subtasksID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        EpicTask epicTask = (EpicTask) o;

        if (!Objects.equals(subtasksID, epicTask.subtasksID)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (subtasksID != null ? subtasksID.hashCode() : 0);
        return result;
    }

    @Override
    public EpicTask clone() {
        return new EpicTask(this.id, getSubtasksID(), this.status, this.name, this.description);
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "id=" + id +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", description.length='" + (description != null ? description.length() : null) + '\'' +
                ", subtasksID=" + subtasksID +
                '}';
    }
}
