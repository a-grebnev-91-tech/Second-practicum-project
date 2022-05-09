package util.web;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import taskdata.EpicTask;
import taskdata.Subtask;
import taskdata.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

import static webapi.HttpTaskServer.*;

public class TaskHandler implements HttpHandler {

    private final Gson gson;
    private final TaskManager manager;

    public TaskHandler(TaskManager manager, Gson gson) {
        this.gson = gson;
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-type", "application/json; charset=" + CHARSET_NAME);

        String method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        if ((!path.equals(TASKS_PATH) && !path.equals(TASKS_PATH + "/"))
                && (!path.equals(EPICS_PATH) && !path.equals(EPICS_PATH + "/"))
                && (!path.equals(SUBTASKS_PATH) && !path.equals(SUBTASKS_PATH + "/"))
        ) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
        }

        String taskType = path.split("/")[2];

        Map<String, String> queryPairs = UriParser.splitQuery(uri);
        String shouldBeId = queryPairs.get("id");

        switch (method) {
            case "GET":
                if (shouldBeId == null)
                    sendAllTasks(exchange, taskType);
                else
                    sendTaskById(exchange, taskType, shouldBeId);
                break;
            case "POST":
                postTask(exchange, taskType);
                break;
            case "DELETE":
                if (shouldBeId == null)
                    deleteAllTasks(exchange, taskType);
                else
                    deleteTaskById(exchange, shouldBeId);
                break;
            default:
                exchange.sendResponseHeaders(405, 0);
                exchange.close();
        }
    }

    private void createTask(HttpExchange exchange, Task task) throws IOException {
        long createdId = manager.createTask(task);
        if (createdId > 0) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
        } else {
            exchange.sendResponseHeaders(422, 0);
            exchange.close();
        }
    }

    private void deleteAllTasks(HttpExchange exchange, String taskType) throws IOException {
        if (taskType.equals("task")) {
            manager.removeAllTasks();
        } else if (taskType.equals("epic")) {
            manager.removeAllEpicTasks();
        } else {
            manager.removeAllSubtasks();
        }
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
    }

    private void deleteTaskById(HttpExchange exchange, String shouldBeId) throws IOException {
        long id = Long.parseLong(shouldBeId);
        boolean isDelete = manager.removeTask(id);
        if (isDelete) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
        } else {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
        }
    }

    private void postTask(HttpExchange exchange, String taskType) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        try {
            Task task = parseBody(body, taskType);
            long id = task.getID();
            if (manager.containsTask(id)) {
                updateTask(exchange, task);
            } else {
                createTask(exchange, task);
            }
        } catch (JsonParseException ex) {
            exchange.sendResponseHeaders(422, 0);
            exchange.close();
        }
    }
//                    Subtask subtask = gson.fromJson(body, Subtask.class);
//                    long subtaskId = subtask.getID();
//                    if (manager.containsTask(subtaskId)) {
//                        isUpdateOperation = true;
//                        isUpdated = manager.updateTask(subtask);
//                    } else {
//                        createdId = manager.createTask(subtask);
//                    }
//                    break;
//                case "epic":
//                    EpicTask epic = gson.fromJson(body, EpicTask.class);
//                    long epicId = epic.getID();
//                    if (manager.containsTask(epicId)) {
//                        isUpdateOperation =true;
//                        isUpdated = manager.updateTask(epic);
//                    } else {
//                        createdId = manager.createTask(epic);
//                    }
//                    break;
//                case "task":
//                    Task task = gson.fromJson(body, Task.class);
//                    long taskId = task.getID();
//                    if (manager.containsTask(taskId)) {
//                        isUpdateOperation = true;
//                        isUpdated = manager.updateTask(task);
//                    } else {
//                        createdId = manager.createTask(task);
//                    }
//                    break;
//            }
//            if (isUpdated || createdId > 0) {
//                exchange.sendResponseHeaders(204, -1);
//                exchange.close();
//            } else if (isUpdateOperation) {
//                exchange.sendResponseHeaders(404, 0);
//                exchange.close();
//            }


    private Task parseBody(String body, String taskType) {
        Task task = null;
        switch (taskType) {
            case "subtask":
                task = gson.fromJson(body, Subtask.class);
                break;
            case "epic":
                task = gson.fromJson(body, EpicTask.class);
                break;
            case "task":
                task = gson.fromJson(body, Task.class);
                break;
        }
        return task;
    }

    private void sendTaskById(HttpExchange exchange, String taskType, String shouldBeId) throws IOException {
        long id = Long.parseLong(shouldBeId);
        Task task;
        if (taskType.equals("task")) {
            task = manager.getTask(id);
        } else if (taskType.equals("epic")) {
            task = manager.getEpicTask(id);
        } else {
            task = manager.getSubtask(id);
        }
        String jsonTask = gson.toJson(task);
        exchange.sendResponseHeaders(200, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonTask.getBytes(DEFAULT_CHARSET));
        }
    }

    private void sendAllTasks(HttpExchange exchange, String taskType) throws IOException {
        String json;
        if (taskType.equals("task")) {
            json = gson.toJson(manager.getTasks());
        } else if (taskType.equals("epic")) {
            json = gson.toJson(manager.getEpicTasks());
        } else {
            json = gson.toJson(manager.getSubtasks());
        }
        exchange.sendResponseHeaders(200, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json.getBytes(DEFAULT_CHARSET));
        }
    }

    private void updateTask(HttpExchange exchange, Task task) throws IOException {
        if (manager.updateTask(task)) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
        } else {
            exchange.sendResponseHeaders(422, 0);
            exchange.close();
        }
    }
}
