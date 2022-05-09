package util.web;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import static webapi.HttpTaskServer.PRIORITIZED_PATH;
import static webapi.HttpTaskServer.DEFAULT_CHARSET;
import static webapi.HttpTaskServer.CHARSET_NAME;

public class PrioritizedHandler implements HttpHandler {

    private final Gson gson;
    private final TaskManager manager;

    public PrioritizedHandler(TaskManager manager, Gson gson) {
        this.gson = gson;
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        if (path.equals(PRIORITIZED_PATH) || path.equals(PRIORITIZED_PATH + "/")) {
            exchange.getResponseHeaders().add("Content-type", "application/json; charset=" + CHARSET_NAME);
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
        } else {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
        }
    }
}
