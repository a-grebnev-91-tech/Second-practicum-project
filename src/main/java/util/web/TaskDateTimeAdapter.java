package util.web;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import util.tasks.TaskDateTime;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static util.tasks.TaskToString.FORMATTER;
//TODO remove this class
public class TaskDateTimeAdapter extends TypeAdapter<TaskDateTime> {

    @Override
    public void write(JsonWriter jsonWriter, TaskDateTime taskDateTime) throws IOException {
        if (taskDateTime.getStartTime() == null || taskDateTime.getDuration() == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.beginObject();
        jsonWriter.name("startTime").value(taskDateTime.getStartTime().format(FORMATTER));
        jsonWriter.name("duration").value(taskDateTime.getDuration().toMinutes());
        jsonWriter.endObject();
    }

    @Override
    public TaskDateTime read(JsonReader jsonReader) throws IOException {
        String dateTime = null;
        int duration = 0;
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (name.equals("startTime")) {
                dateTime = jsonReader.nextString();
            } else if (name.equals("duration")) {
                duration = jsonReader.nextInt();
            } else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        if (dateTime == null) {
            return new TaskDateTime(null, null);
        } else {
            return new TaskDateTime(LocalDateTime.parse(dateTime, FORMATTER), Duration.ofMinutes(duration));
        }
    }
}
