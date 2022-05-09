package util.web;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import taskdata.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

import static webapi.HttpTaskServer.TASKS_PATH;
import static webapi.HttpTaskServer.EPICS_PATH;
import static webapi.HttpTaskServer.SUBTASKS_PATH;
import static webapi.HttpTaskServer.DEFAULT_CHARSET;
import static webapi.HttpTaskServer.CHARSET_NAME;

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
        switch (method) {
            case "GET":
                handleGetRequest(exchange);
                break;
            case "POST":
                handlePostRequest(exchange);
                break;
            case "DELETE":
                handleDeleteRequest(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, 0);
                exchange.close();
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) {
        //TODO
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
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

        if (shouldBeId == null) {
            sendAllTasks(exchange, taskType);
        } else {
            sendTaskById(exchange, taskType, shouldBeId);
        }
    }

    private void handlePostRequest(HttpExchange exchange) {
        //TODO
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
