package webapi;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.TaskManager;
import taskdata.EpicTask;
import taskdata.Task;
import util.Managers;
import util.UriParser;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

public class HttpTaskServer {
    private final Gson gson;
    private final HttpServer server;
    private final int PORT = 8080;
    private static final String charsetName = "utf-8";
    private static final Charset DEFAULT_CHARSET = Charset.forName(charsetName);
    private final TaskManager manager;
    private final String tasksPath = "/tasks/task";
    private final String epicsPath = "/tasks/epic";
    private final String subtasksPath = "/tasks/subtask";
    private final String historyPath = "/tasks/history";
    private final String prioritizedPath = "/tasks";

    public HttpTaskServer() throws IOException {
        gson = new Gson();
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        manager = Managers.getDefault();
        //TODO del setupManager
        setupManager();
        //^^^^^^^^^^^^^
        server.createContext(tasksPath, new TaskHandler());
        server.createContext(epicsPath, new TaskHandler());
        server.createContext(subtasksPath, new TaskHandler());
        server.createContext(historyPath, new HistoryHandler());
        server.createContext(prioritizedPath, new PrioritizedHandler());
        server.start();
    }

    private class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Content-type", "application/json; charset=" + charsetName);
            if (exchange.getRequestMethod().equals("GET")) {
                String history = gson.toJson(manager.history());
                exchange.sendResponseHeaders(200, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(history.getBytes(DEFAULT_CHARSET));
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.close();
            }
        }
    }
    private class PrioritizedHandler implements  HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Content-type", "application/json; charset=" + charsetName);
            if (exchange.getRequestMethod().equals("GET")) {
                String tasks = gson.toJson(manager.getPrioritizedTasks());
                exchange.sendResponseHeaders(200, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(tasks.getBytes(DEFAULT_CHARSET));
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.close();
            }
        }

    }
    private class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Content-type", "application/json; charset=" + charsetName);
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
        private void getEpic(HttpExchange exchange) {

        }

        private void getSubtask(HttpExchange exchange) {
        }

        private void getTask(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (!path.equals(tasksPath) && !path.equals(tasksPath + "/")) {
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
            }
            URI uri = exchange.getRequestURI();
            Map<String, String> queryPairs = UriParser.splitQuery(uri);
            if (queryPairs.get("id") == null) {
                String json = gson.toJson(manager.getTasks());
                exchange.sendResponseHeaders(200, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(json.getBytes(DEFAULT_CHARSET));
                }
            } else {
                String mustBeId = queryPairs.get("id");
                long id = Long.parseLong(mustBeId);
                Task task = manager.getTask(id);
                String jsonTask;
                if (task == null) {
                    jsonTask = JsonNull.INSTANCE.toString();
                } else {
                    jsonTask = gson.toJson(task);
                }
                exchange.sendResponseHeaders(200, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(jsonTask.getBytes(DEFAULT_CHARSET));
                }
            }
        }

        private void handleDeleteRequest(HttpExchange exchange) {

        }

        private void handleGetRequest(HttpExchange exchange) throws IOException {
            URI uri = exchange.getRequestURI();
            String[] pathsPart = uri.getPath().split("/");
            switch (pathsPart[2]) {
                case "task":
                    getTask(exchange);
                    break;
                case "epic":
                    getEpic(exchange);
                    break;
                case "subtask":
                    getSubtask(exchange);
                    break;
            }

        }

        private void handlePostRequest(HttpExchange exchange) {

        }

    }
    //TODO delete (for test purposes)

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
    }

    private void setupManager() {
        manager.createTask(new Task("a", "a"));
        manager.createTask(new EpicTask("b", "b"));
        manager.getTask(1);
        manager.getTask(2);
    }
}
