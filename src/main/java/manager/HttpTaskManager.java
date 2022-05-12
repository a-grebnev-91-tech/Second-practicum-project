package manager;

import com.google.gson.*;
import taskdata.EpicTask;
import taskdata.Subtask;
import taskdata.Task;
import util.tasks.TasksVault;
import util.web.EpicTaskAdapter;
import util.web.SubtaskAdapter;
import util.web.TaskAdapter;
import webapi.kv.KVTaskClient;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

public class HttpTaskManager extends FileBackedTaskManager {

    private final String key = this.getClass().getName();
    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(String url) {
        super("");
        client = new KVTaskClient(url);
        gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskAdapter())
                .registerTypeAdapter(EpicTask.class, new EpicTaskAdapter())
                .registerTypeAdapter(Subtask.class, new SubtaskAdapter())
                .create();
    }

    @Override
    public void save() {
        String jsonState = convertStateToJson();
        try {
            client.put(key, jsonState);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e); //TODO change exception
        }
    }

    private String convertStateToJson() {
        JsonObject stateRepresentation = new JsonObject();

        TasksVault thisVault = getCurrentTasksVault();

        long id = getCurrentId();
        String jsonId = gson.toJson(id);
        JsonElement elementId = JsonParser.parseString(jsonId);
        stateRepresentation.add("manager ID for next task", elementId);

        Map<Long, Task> tasks = thisVault.getTasks();
        String jsonTasks = gson.toJson(tasks);
        JsonElement elementTasks = JsonParser.parseString(jsonTasks);
        stateRepresentation.add("tasks", elementTasks);

        Map<Long, EpicTask> epics = thisVault.getEpics();
        String jsonEpics = gson.toJson(epics);
        JsonElement elementEpics = JsonParser.parseString(jsonEpics);
        stateRepresentation.add("epics", elementEpics);

        Map<Long, Subtask> subtasks = thisVault.getSubtasks();
        String jsonSubtasks = gson.toJson(subtasks);
        JsonElement elementSubtasks = JsonParser.parseString(jsonSubtasks);
        stateRepresentation.add("subtasks", elementSubtasks);

        String jsonHistory = gson.toJson(history());
        JsonElement elementHistory = JsonParser.parseString(jsonHistory);
        stateRepresentation.add("history", elementHistory);


        return stateRepresentation.toString();
    }

    private long getCurrentId() {
        try {
            Class inMemoryHistoryManager = this.getClass().getSuperclass().getSuperclass();
            Field id = inMemoryHistoryManager.getDeclaredField("id");
            id.setAccessible(true);
            return (long) id.get(this);
        } catch (NoSuchFieldException | IllegalStateException | IllegalAccessException e) {
            throw new RuntimeException("Cannot get id from super class");
        }
    }

    private TasksVault getCurrentTasksVault() {
        try {
            Class inMemoryHistoryManager = this.getClass().getSuperclass().getSuperclass();
            Field vault = inMemoryHistoryManager.getDeclaredField("vault");
            vault.setAccessible(true);
            return (TasksVault) vault.get(this);
        } catch (NoSuchFieldException | IllegalStateException | IllegalAccessException e) {
            throw new RuntimeException("Cannot get task vault form super class");
        }
    }
}
