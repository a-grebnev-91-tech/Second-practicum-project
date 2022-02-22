package tasktracker.taskdata;

public class Subtask extends Task {

    private final int epicTaskID;

    public Subtask(String name, String description) {
        super(name,description);
        throw new TaskInvalidException("Cannot create subtask that isn't linked to a epicTask");
    }

    public Subtask(int id, TaskStatus status, String name, String description) {
        super(id, status, name, description);
        throw new TaskInvalidException("Cannot create subtask that isn't linked to a epicTask");
    }

    public Subtask(int epicTaskID, String name, String description) {
        super(name, description);
        if (epicTaskID < 1)
            throw new TaskInvalidException("Cannot create subtask with invalid epicTaskID");
        this.epicTaskID = epicTaskID;
    }

    public Subtask(int id, int epicTaskID, TaskStatus status, String name, String description) {
        super(id, status, name, description);
        this.epicTaskID = epicTaskID;
    }

    public int getEpicTaskID() {
        return  this.epicTaskID;
    }

    @Override
    public Subtask clone() {
        return new Subtask(this.id, this.epicTaskID, this.status, this.name, this.description);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", epicTaskID=" + epicTaskID +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", description.length()='" + (description != null ? description.length() : null) + '\'' +
                '}';
    }
}
