package tasktracker.taskdata;

public class Task implements Cloneable{

    protected int id;
    protected TaskStatus status;
    protected String name;
    protected String description;

    public Task(String name, String description) {
        if (name == null || name.isBlank())
            throw new TaskInvalidException("Cannot create unnamed task");
        if (description == null)
            description = "";
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(int id, TaskStatus status, String name, String description) {
        this(name, description);
        if (status == null)
            throw new TaskInvalidException("Cannot create task with null status");
        if (id < 0)
            throw new TaskInvalidException("Cannot create task with ID less than 0");
        this.id = id;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && name.length() > 0)
            this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        if (id >= 0)
            this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        if (status != null)
            this.status = status;
    }

    @Override
    public Task clone() {
        return new Task(this.id, this.status, this.name, this .description);
    }

    @Override
    public String toString() {
        return "taskManagement.Task{" +
                "id=" + id +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", description.length()='" + (description != null ? description.length() : null) + '\'' +
                '}';
    }
}
