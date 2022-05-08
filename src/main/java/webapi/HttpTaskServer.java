package webapi;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.TaskManager;
import util.Managers;
import util.exceptions.MethodUnavailableException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

public class HttpTaskServer {
    private final HttpServer server;
    private final int PORT = 8080;
    private final TaskManager manager;

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        manager = Managers.getDefault();
        server.createContext("/tasks/task", new TaskHandler());
        server.start();
        //TODO server.start()
    }

    public static class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
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
                    throw new MethodUnavailableException("Method " + method + " is not supported");
            }
        }

        private void handleDeleteRequest(HttpExchange exchange) {

        }

        private void handleGetRequest(HttpExchange exchange) {

        }

        private void handlePostRequest(HttpExchange exchange) {

        }
    }

    //TODO delete (for test purposes)
    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
    }

}
