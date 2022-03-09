package tasktracker.taskdata;

public class Task implements Cloneable{

    /**
     * поля были объявлены протектед для простого доступа к ним из наследников.
     * если их делать приватными как тогда к ним получить доступ?
     */
    private long id; //сложно представить себе 2 миллиарда задач, ну да ладно, лонг - так лонг, сейчас перелопачу весь код ;)
    private TaskStatus status;
    private String name;
    private String description;

    public Task(String name, String description) {
        if (name == null || name.isBlank())
            throw new TaskInvalidException("Cannot create unnamed task");
        if (description == null)
            description = "";
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(TaskStatus status, String name, String description){
        this(name, description);
        if (status == null)
            throw new TaskInvalidException("Cannot create task with null status");
        this.status = status;
    }

    public Task(long id, TaskStatus status, String name, String description) {
        this(status, name, description);
        if (id < 0)
            throw new TaskInvalidException("Cannot create task with ID less than 0");
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.isBlank())
            this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public long getID() {
        return id;
    }

    public void setID(long id) {
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
        return "Task{" +
                "id=" + id +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", description.length()='" + (description != null ? description.length() : null) + '\'' +
                '}';
    }
}
