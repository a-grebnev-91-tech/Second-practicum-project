package util.web;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import taskdata.TaskStatus;
import util.tasks.TaskDateTime;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static util.tasks.TaskToString.FORMATTER;

public class TaskAdapterHelper {

    public static ArrayList<Long> readLongsArray(JsonReader reader) throws IOException {
        ArrayList<Long> longs = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            longs.add(reader.nextLong());
        }
        reader.endArray();
        return longs;
    }

    public static LocalDateTime readStartTime(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.skipValue();
            return null;
        }
        String dateTime = reader.nextString();
        if (dateTime == null) {
            return null;
        }
        return LocalDateTime.parse(dateTime, FORMATTER);
    }

    public static TaskStatus readStatus(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL)
            return null;
        String status = reader.nextString();
        return TaskStatus.valueOf(status);
    }

    public static void writeSubtasksIds(JsonWriter writer, ArrayList<Long> subtasksID) throws IOException {
        writer.name("subtasksId");
        if (subtasksID == null || subtasksID.isEmpty()) {
            writer.nullValue();
            return;
        }
        writer.beginArray();
        for (Long id : subtasksID) {
            writer.value(id);
        }
        writer.endArray();
    }

    public static void writeTaskDateTime(JsonWriter writer, TaskDateTime taskDateTime) throws IOException {
        if (taskDateTime == null || taskDateTime.getStartTime() == null) {
            writer.name("startTime").nullValue();
            writer.name("duration").nullValue();
            return;
        }
        writer.name("startTime").value(taskDateTime.getStartTime().format(FORMATTER));
        writer.name("duration").value(taskDateTime.getDuration().toMinutes());
    }

}
