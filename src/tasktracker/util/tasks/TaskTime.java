package tasktracker.util.tasks;

import tasktracker.taskdata.exceptions.TaskTimeException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class TaskTime {

    private final LocalDateTime startTime;

    private final Duration duration;

    private final LocalDateTime endTime;

    public TaskTime(LocalDateTime startTime, Duration duration) {
        if (startTime == null ^ duration == null)
            throw new TaskTimeException("Cannot create TaskTime instance with null field(s)");
        this.startTime = startTime;
        this.duration = duration;
        if (startTime == null) {
            this.endTime = null;
        } else {
            this.endTime = startTime.plus(duration);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskTime taskTime = (TaskTime) o;
        return Objects.equals(startTime, taskTime.startTime) && Objects.equals(duration, taskTime.duration);
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, duration);
    }

}
