package TaskData;

public class Subtask extends Task {

    private final int epicTaskID;

    public Subtask(String name, String description) {
        super(name,description);
        throw new TaskInvalidException("Подзадача не может существовать без эпика");
    }

    public Subtask(int id, TaskStatus status, String name, String description) {
        super(id, status, name, description);
        throw new TaskInvalidException("Подзадача не может существовать без эпика");
    }

    public Subtask(int epicTaskID, String name, String description) {
        super(name, description);
        this.epicTaskID = epicTaskID;
    }

    public Subtask(int id, int epicTaskID, TaskStatus status, String name, String description) {
        super(id, status, name, description);
        this.epicTaskID = epicTaskID;    }

    public int getEpicTaskID() {
        return  this.epicTaskID;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", epicTask=" + epicTaskID +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", description.length()='" + (description != null ? description.length() : null) + '\'' +
                '}';
    }
}
