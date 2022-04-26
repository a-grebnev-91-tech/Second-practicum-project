package tasktracker.taskdata;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Cloneable{

    private Duration duration;
    private long id;
    private TaskStatus status;
    private LocalDateTime startTime;
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

    //if users create task manually, its start time cannot be in the past.
    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this(name, description);
        if (startTime == null || duration == null) {
            this.startTime = null;
            this.duration = null;
        } else if (isInPast(startTime)) {
            throw new TaskInvalidException("Cannot create task with a start time in the past");
        } else {
            this.startTime = startTime;
            this.duration = duration;
        }
    }

    //constructor for loading task from anywhere. Start time can be in the past.

    public Task(
            long id,
            TaskStatus status,
            String name,
            String description,
            LocalDateTime startTime,
            Duration duration
    ) {
        this(name, description);
        if (status == null)
            throw new TaskInvalidException("Cannot create task with null status");
        if (id < 0)
            throw new TaskInvalidException("Cannot create task with ID less than 0");
        this.id = id;
        if (startTime == null || duration == null) {
            this.startTime = null;
            this.duration = null;
        } else {
            this.startTime = startTime;
            this.duration = duration;
        }
    }
    @Override
    public Task clone() {
        return new Task(this.id, this.status, this.name, this .description, this.startTime, this.duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (id != task.id) return false;
        if (status != task.status) return false;
        if (!Objects.equals(name, task.name)) return false;
        if (!Objects.equals(description, task.description)) return false;
        if (!Objects.equals(startTime, task.startTime)) return false;
        return Objects.equals(duration, task.duration);
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getEndTime() {
        if(startTime == null) return null;
        return startTime.plus(duration);
    }

    public long getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        return result;
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public void setID(long id) {
        if (id >= 0)
            this.id = id;
    }

    public void setName(String name) {
        if (name != null && !name.isBlank())
            this.name = name;
    }

    public void setStatus(TaskStatus status) {
        if (status != null)
            this.status = status;
    }

    public void setStartTime(LocalDateTime time) {
        if (time == null) return;
        if (isInPast(time)) throw new UnsupportedOperationException("Cannot set a start time in the past");
        this.startTime = time;
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

    private boolean isInPast(LocalDateTime startTime) {
        return startTime.isBefore(LocalDateTime.now());
    }
}
