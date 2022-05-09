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
                System.out.println("get");
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
            boolean isCreated = false;
            long createdId = -1;
            if (body.contains("epicTaskID")) {
                Subtask subtask = gson.fromJson(body, Subtask.class);
                long id = subtask.getID();

                if (manager.containsTask(id)) {
                    isCreated = manager.updateTask(subtask);
                } else {
                    createdId = manager.createTask(subtask);
                }
            } else if (body.contains("subtasksID")) {
                EpicTask epic = gson.fromJson(body, EpicTask.class);
                long id = epic.getID();
                if (manager.containsTask(id)) {
                    isCreated = manager.updateTask(epic);
                } else {
                    createdId = manager.createTask(epic);
                }
            } else {
                Task task = gson.fromJson(body, Task.class);
                long id = task.getID();
                if (manager.containsTask(id)) {
                    isCreated = manager.updateTask(task);
                } else {
                    createdId = manager.createTask(task);
                }
            }
            if (isCreated || createdId > 0) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
            } else {
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
            }
        } catch (JsonParseException ex) {
            exchange.sendResponseHeaders(422, 0);
            exchange.close();
        } finally {
            System.out.println(manager.getSubtasks());
        }
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

}
