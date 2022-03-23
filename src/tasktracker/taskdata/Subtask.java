package tasktracker.taskdata;

public class Subtask extends Task {

    private final long epicTaskID;

    public Subtask(String name, String description) {
        super(name,description);
        throw new TaskInvalidException("Cannot create subtask that isn't linked to a epicTask");
    }

    public Subtask(long id, TaskStatus status, String name, String description) {
        super(id, status, name, description);
        throw new TaskInvalidException("Cannot create subtask that isn't linked to a epicTask");
    }

    public Subtask(long epicTaskID, String name, String description) {
        super(name, description);
        if (epicTaskID < 1)
            throw new TaskInvalidException("Cannot create subtask with invalid epicTaskID");
        this.epicTaskID = epicTaskID;
    }

    public Subtask(long id, long epicTaskID, TaskStatus status, String name, String description) {
        super(id, status, name, description);
        this.epicTaskID = epicTaskID;
    }

    public long getEpicTaskID() {
        return  this.epicTaskID;
    }

    @Override
    public Subtask clone() {
        return new Subtask(getID(), this.epicTaskID, getStatus(), getName(), getDescription());
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getID() +
                ", epicTaskID=" + epicTaskID +
                ", status=" + getStatus() +
                ", name='" + getName() + '\'' +
                ", description.length()='" + (getDescription() != null ? getDescription().length() : null) + '\'' +
                '}';
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
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (epicTaskID ^ (epicTaskID >>> 32));
        return result;
    }
}
