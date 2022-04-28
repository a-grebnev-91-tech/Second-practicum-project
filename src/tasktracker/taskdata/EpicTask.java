package tasktracker.taskdata;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class EpicTask extends Task {
    private final ArrayList<Long> subtasksID;

    public EpicTask(String name, String description) {
        super(name, description);
        subtasksID = new ArrayList<>();
    }

    public EpicTask(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        subtasksID = new ArrayList<>();
    }

    public EpicTask(long id, TaskStatus status, String name, String description) {
        super(id, status, name, description);
        subtasksID = new ArrayList<>();
    }

    public EpicTask(long id,
                    TaskStatus status,
                    String name,
                    String description,
                    LocalDateTime startTime,
                    Duration duration) {
        super(id, status, name, description, startTime, duration);
        subtasksID = new ArrayList<>();
    }

    public EpicTask(long id,
                    ArrayList<Long> subtasksID,
                    TaskStatus status,
                    String name,
                    String description,
                    LocalDateTime startTime,
                    Duration duration) {
        super(id, status, name, description, startTime, duration);
        this.subtasksID = new ArrayList<>();
        if (subtasksID != null)
            this.subtasksID.addAll(subtasksID);
    }


    public void addSubtask(long subtaskID) {
        if (subtaskID > 0)
            subtasksID.add(subtaskID);
    }

    @Override
    public EpicTask clone() {
        return new EpicTask(
                getID(),
                getSubtasksID(),
                getStatus(),
                getName(),
                getDescription(),
                getStartTime(),
                getDuration());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        EpicTask epicTask = (EpicTask) o;

        return Objects.equals(subtasksID, epicTask.subtasksID);
    }

    public ArrayList<Long> getSubtasksID() {
        return new ArrayList<>(subtasksID);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (subtasksID != null ? subtasksID.hashCode() : 0);
        return result;
    }

    public void removeAllSubtasks() {
        subtasksID.clear();
    }

    public boolean removeSubtask(long subtaskID) {
        return subtasksID.remove(subtaskID);
    }

}
