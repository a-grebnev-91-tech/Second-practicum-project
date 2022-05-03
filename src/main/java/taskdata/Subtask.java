package taskdata;

import util.tasks.TaskToString;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private final long epicTaskID;

    public Subtask(long epicTaskID, String name, String description) {
        super(name, description);
        if (epicTaskID < 1)
            throw new TaskInvalidException("Cannot create subtask with invalid epicTaskID");
        this.epicTaskID = epicTaskID;
    }

    public Subtask(long epicTaskID, String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        if (epicTaskID < 1)
            throw new TaskInvalidException("Cannot create subtask with invalid epicTaskID");
        this.epicTaskID = epicTaskID;
    }

    public Subtask(long id,
                   long epicTaskID,
                   TaskStatus status,
                   String name,
                   String description) {
        super(id, status, name, description);
        if (epicTaskID < 1)
            throw new TaskInvalidException("Cannot create subtask with invalid epicTaskID");
        this.epicTaskID = epicTaskID;
    }

    public Subtask(long id,
                   long epicTaskID,
                   TaskStatus status,
                   String name,
                   String description,
                   LocalDateTime startTime,
                   Duration duration) {
        super(id, status, name, description, startTime, duration);
        if (epicTaskID < 1)
            throw new TaskInvalidException("Cannot create subtask with invalid epicTaskID");
        this.epicTaskID = epicTaskID;
    }

    public long getEpicTaskID() {
        return this.epicTaskID;
    }

    @Override
    public Subtask clone() {
        return new Subtask(getID(),
                this.epicTaskID,
                getStatus(),
                getName(),
                getDescription(),
                getStartTime(),
                getDuration()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Subtask subtask = (Subtask) o;

        return epicTaskID == subtask.epicTaskID;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (epicTaskID ^ (epicTaskID >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return TaskToString.getString(this);
    }
}
