package util.web.json.adapters;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import taskdata.Subtask;
import taskdata.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.jar.JarException;

import static util.web.json.adapters.TaskAdapterHelper.*;

public class SubtaskAdapter extends TypeAdapter<Subtask> {

    @Override
    public void write(JsonWriter writer, Subtask task) throws IOException {
        if (task == null) {
            writer.nullValue();
            return;
        }
        writer.beginObject();
        writer.name("id").value(task.getID());
        writer.name("epicTaskId").value(task.getEpicTaskID());
        writer.name("name").value(task.getName());
        writer.name("description").value(task.getDescription());
        writer.name("status").value(task.getStatus().name());
        writeTaskDateTime(writer, task.getTaskDateTime());
        writer.endObject();
    }

    @Override
    public Subtask read(JsonReader reader) throws IOException {
        long id = 0;
        String taskName = null;
        String description = null;
        TaskStatus status = null;
        long epicId = 0;
        LocalDateTime startTime = null;
        int duration = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                id = reader.nextLong();
            } else if (name.equals("epicTaskId")) {
                epicId = reader.nextLong();
            } else if (name.equals("name")) {
                taskName = reader.nextString();
            } else if (name.equals("description")) {
                description = reader.nextString();
            } else if (name.equals("status")) {
                status = readStatus(reader);
            } else if (name.equals("startTime")) {
                startTime = readStartTime(reader);
            } else if (name.equals("duration")) {
                duration = reader.nextInt();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        if (epicId < 1) {
            throw new JsonParseException("Cannot parse subtask without epic");
        }
        if (taskName == null || taskName.isBlank()) {
            throw new JsonParseException("Cannot parse subtask with null or blank name");
        }
        if (startTime == null ^ duration < 1) {
            throw new JarException("Cannot parse subtask with bad combination of start time and duration");
        }
        if (status == null && startTime == null) {
            return new Subtask(id, epicId, TaskStatus.NEW, taskName, description);
        } else if (startTime == null) {
            return new Subtask(id, epicId, status, taskName, description);
        } else if (status == null) {
            return new Subtask(id,
                    epicId,
                    TaskStatus.NEW,
                    taskName,
                    description,
                    startTime,
                    Duration.ofMinutes(duration)
            );
        }
        return new Subtask(id, epicId, status, taskName, description, startTime, Duration.ofMinutes(duration));
    }
}
