package util.web;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import taskdata.EpicTask;
import taskdata.TaskStatus;
import util.tasks.TaskDateTime;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.jar.JarException;

import static util.web.TaskAdapterHelper.*;

public class EpicTaskAdapter extends TypeAdapter<EpicTask> {

    //TODO implement this
    @Override
    public EpicTask read(JsonReader reader) throws IOException {
        long id = 0;
        String taskName = null;
        String description = null;
        TaskStatus status = null;
        ArrayList<Long> subtasksId = null;
        LocalDateTime startTime = null;
        int duration = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                id = reader.nextLong();
            } else if (name.equals("name")) {
                taskName = reader.nextString();
            } else if (name.equals("description")) {
                description = reader.nextString();
            } else if (name.equals("status")) {
                status = readStatus(reader);
            } else if (name.equals("subtasksId") && reader.peek() != JsonToken.NULL) {
                subtasksId = readLongsArray(reader);
            } else if (name.equals("startTime")) {
                startTime = readStartTime(reader);
            } else if (name.equals("duration")) {
                duration = reader.nextInt();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        if (taskName == null || taskName.isBlank()) {
            throw new JsonParseException("Cannot parse epic with null or blank name");
        }
        if (startTime == null ^ duration < 1) {
            throw new JarException("Cannot parse epic with bad combination of start time and duration");
        }

        EpicTask epic = new EpicTask(taskName, description);
        if (status != null) {
            epic.setStatus(status);
        }
        if (startTime != null) {
            epic.setTime(startTime, Duration.ofMinutes(duration));
        }
        if (subtasksId != null && !subtasksId.isEmpty()) {
            for (Long subtaskId : subtasksId) {
                epic.addSubtask(subtaskId);
            }
        }
        if (id != 0) {
            epic.setID(id);
        }
        return epic;
    }

    @Override
    public void write(JsonWriter writer, EpicTask epic) throws IOException {
        if (epic == null) {
            writer.nullValue();
            return;
        }
        writer.beginObject();
        writer.name("id").value(epic.getID());
        writer.name("name").value(epic.getName());
        writer.name("description").value(epic.getDescription());
        writer.name("status").value(epic.getStatus().name());
        writeTaskDateTime(writer, epic.getTaskDateTime());
        writeSubtasksIds(writer, epic.getSubtasksID());
        writer.endObject();
    }
}
