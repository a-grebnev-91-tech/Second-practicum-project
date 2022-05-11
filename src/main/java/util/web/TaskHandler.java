package util.web;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import taskdata.EpicTask;
import taskdata.Subtask;
import taskdata.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.time.DateTimeException;
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
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();

        if (pathIsValid(path)) {
            String method = exchange.getRequestMethod();

            String taskType = path.split("/")[2];

            Map<String, String> queryPairs = UriParser.splitQuery(uri);
            String shouldBeId = queryPairs.get("id");

            switch (method) {
                case "GET":
                    exchange.getResponseHeaders().add("Content-type", "application/json; charset=" + CHARSET_NAME);
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
        } else {
            exchange.sendResponseHeaders(404, 0);
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

    private boolean pathIsValid(String path) {
        if (path.equals(TASKS_PATH) || path.equals(TASKS_PATH + "/"))
            return true;
        if (path.equals(EPICS_PATH) || path.equals(EPICS_PATH + "/"))
            return true;
        return path.equals(SUBTASKS_PATH) || path.equals(SUBTASKS_PATH + "/");
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
        } catch (JsonParseException | DateTimeException ex) {
            exchange.sendResponseHeaders(422, 0);
            exchange.close();
        }
    }

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
        switch (taskType) {
            case "task":
                task = manager.getTask(id);
                break;
            case "epic":
                task = manager.getEpicTask(id);
                break;
            case "subtask":
                task = manager.getSubtask(id);
                break;
            default:
                task = null;
        }
        if (task == null) {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        } else {
            String jsonTask = gson.toJson(task);
            exchange.sendResponseHeaders(200, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(jsonTask.getBytes(DEFAULT_CHARSET));
            }
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
