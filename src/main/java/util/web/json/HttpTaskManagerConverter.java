package util.web.json;

import com.google.gson.*;
import manager.HttpTaskManager;
import taskdata.EpicTask;
import taskdata.Subtask;
import taskdata.Task;
import util.tasks.TasksVault;
import util.web.json.adapters.EpicTaskAdapter;
import util.web.json.adapters.SubtaskAdapter;
import util.web.json.adapters.TaskAdapter;

import java.lang.reflect.Field;
import java.util.Map;

public class HttpTaskManagerConverter {

    private final Gson gson;
    private final HttpTaskManager manager;

    public HttpTaskManagerConverter(HttpTaskManager manager) {
        this.manager = manager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskAdapter())
                .registerTypeAdapter(EpicTask.class, new EpicTaskAdapter())
                .registerTypeAdapter(Subtask.class, new SubtaskAdapter())
                .create();
    }

    public String convertStateToJson() {
        JsonObject stateRepresentation = new JsonObject();

        TasksVault thisVault = getTaskVaultFromManager();

        long id = getIdFromManager();
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

        String jsonHistory = gson.toJson(getIdsFromHistory());
        JsonElement elementHistory = JsonParser.parseString(jsonHistory);
        stateRepresentation.add("history", elementHistory);

        return stateRepresentation.toString();
    }

    public void updateManager(String jsonState) {

    }

    private long getIdFromManager() {
        try {
            Class inMemoryHistoryManager = manager.getClass().getSuperclass().getSuperclass();
            Field id = inMemoryHistoryManager.getDeclaredField("id");
            id.setAccessible(true);
            return (long) id.get(manager);
        } catch (NoSuchFieldException | IllegalStateException | IllegalAccessException e) {
            throw new ReflectionParseException("Cannot get id from super class");
        }
    }

    private TasksVault getTaskVaultFromManager() {
        try {
            Class inMemoryHistoryManager = manager.getClass().getSuperclass().getSuperclass();
            Field vault = inMemoryHistoryManager.getDeclaredField("vault");
            vault.setAccessible(true);
            return (TasksVault) vault.get(manager);
        } catch (NoSuchFieldException | IllegalStateException | IllegalAccessException e) {
            throw new RuntimeException("Cannot get task vault from super class");
        }
    }

    private long[] getIdsFromHistory() {
        return manager.history().stream().mapToLong(Task::getID).toArray();
    }


    private class ReflectionParseException extends RuntimeException {
        public ReflectionParseException(String message) {
            super(message);
        }
    }
}
