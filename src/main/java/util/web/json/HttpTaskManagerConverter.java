package util.web.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import manager.HistoryManager;
import manager.HttpTaskManager;
import manager.InMemoryHistoryManager;
import taskdata.EpicTask;
import taskdata.Subtask;
import taskdata.Task;
import util.HistoryManagerUpdater;
import util.tasks.TaskValidator;
import util.tasks.TasksVault;
import util.web.json.adapters.EpicTaskAdapter;
import util.web.json.adapters.SubtaskAdapter;
import util.web.json.adapters.TaskAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class HttpTaskManagerConverter {

    private final Gson gson;
    private final HttpTaskManager manager;
    private final String epicsName = "epics";
    private final String historyName = "history";
    private final String idName = "manager ID for next task";
    private final String subtasksName = "subtasks";
    private final String tasksName = "tasks";

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

        TasksVault thisVault = (TasksVault) getManagerFieldValue("vault");

        long id = (long) getManagerFieldValue("id");
        String jsonId = gson.toJson(id);
        JsonElement elementId = JsonParser.parseString(jsonId);
        stateRepresentation.add(idName, elementId);

        Map<Long, Task> tasks = thisVault.getTasks();
        String jsonTasks = gson.toJson(tasks);
        JsonElement elementTasks = JsonParser.parseString(jsonTasks);
        stateRepresentation.add(tasksName, elementTasks);

        Map<Long, EpicTask> epics = thisVault.getEpics();
        String jsonEpics = gson.toJson(epics);
        JsonElement elementEpics = JsonParser.parseString(jsonEpics);
        stateRepresentation.add(epicsName, elementEpics);

        Map<Long, Subtask> subtasks = thisVault.getSubtasks();
        String jsonSubtasks = gson.toJson(subtasks);
        JsonElement elementSubtasks = JsonParser.parseString(jsonSubtasks);
        stateRepresentation.add(subtasksName, elementSubtasks);

        String jsonHistory = gson.toJson(getIdsFromHistory());
        JsonElement elementHistory = JsonParser.parseString(jsonHistory);
        stateRepresentation.add(historyName, elementHistory);

        return stateRepresentation.toString();
    }

    public void updateManagerFromJson(String managerState) {
        long id = 0;
        Map<Long, Task> tasks = null;
        Map<Long, EpicTask> epics = null;
        Map<Long, Subtask> subtasks = null;
        TasksVault vault = null;
        TaskValidator validator = null;
        List<Long> historyIds = null;
        HistoryManager historyManager = null;

        JsonElement elementManagerState = JsonParser.parseString(managerState);
        JsonObject jsonManagerState = elementManagerState.getAsJsonObject();

        JsonElement elementId = jsonManagerState.get(idName);
        id = elementId.getAsLong();

        JsonElement elementTasks = jsonManagerState.get(tasksName);
        Type tasksMapType = new TypeToken<Map<Long, Task>>(){}.getType();
        tasks = gson.fromJson(elementTasks, tasksMapType);

        JsonElement elementEpics = jsonManagerState.get(epicsName);
        Type epicsMapType = new TypeToken<Map<Long, EpicTask>>(){}.getType();
        epics = gson.fromJson(elementEpics, epicsMapType);

        JsonElement elementSubtasks = jsonManagerState.get(subtasksName);
        Type subtasksMapType = new TypeToken<Map<Long, Subtask>>(){}.getType();
        subtasks = gson.fromJson(elementSubtasks, subtasksMapType);

        JsonElement elementHistory = jsonManagerState.get(historyName);
        Type longListType = new TypeToken<List<Long>>(){}.getType();
        historyIds = gson.fromJson(elementHistory, longListType);

        vault = new TasksVault(epics,subtasks, tasks);
        validator = new TaskValidator(epics, subtasks, tasks, vault.getPrioritizedTasks());
        historyManager = new InMemoryHistoryManager();

        HistoryManagerUpdater.updateHistoryManager(historyManager, historyIds, tasks, epics, subtasks);

        updateManager(id, vault, validator, historyManager);
    }

    private void updateManager(long lastId, TasksVault vault, TaskValidator validator, HistoryManager history) {
        Field id = getManagerField("id");
        Field tasksVault = getManagerField("vault");
        Field taskValidator = getManagerField("validator");
        Field historyManager = getManagerField("historyManager");
        try {
            id.set(manager, lastId);
            tasksVault.set(manager, vault);
            taskValidator.set(manager, validator);
            historyManager.set(manager, history);
        } catch (IllegalAccessException e) {
            throw new ReflectionAccessException("Cannot set value to current manager object");
        }
    }

    private Field getManagerField(String name) {
        try {
            Class inMemoryHistoryManager = manager.getClass().getSuperclass().getSuperclass();
            Field field = inMemoryHistoryManager.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException | IllegalStateException e) {
            throw new ReflectionParseException("Cannot get " + name + " field from super class");
        }
    }

    private Object getManagerFieldValue(String name) {
        try {
            Class inMemoryHistoryManager = manager.getClass().getSuperclass().getSuperclass();
            Field field = inMemoryHistoryManager.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(manager);
        } catch (NoSuchFieldException | IllegalStateException e) {
            throw new ReflectionParseException("Cannot get " + name + " field from super class");
        } catch (IllegalAccessException e) {
            throw new ReflectionAccessException("Cannot get " + name + " field value from super class");
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

    private class ReflectionAccessException extends RuntimeException {
        public ReflectionAccessException(String message) {
            super(message);
        }
    }
}
