package webapi.kv;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Постман: https://www.getpostman.com/collections/a83b61d9e1c81c10575c
 */
public class KVServer {
    public static final int PORT = 8078;
    private final String apiToken;
    private HttpServer server;
    private Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        apiToken = generateApiKey();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", new HttpRegisterHandler());
        server.createContext("/save", new HttpSaveHandler());
        server.createContext("/load", new HttpLoadHandler());
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        System.out.println("API_TOKEN: " + apiToken);
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private String generateApiKey() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), "UTF-8");
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes("UTF-8");
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    private class HttpRegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                System.out.println("\n/register");
                switch (exchange.getRequestMethod()) {
                    case "GET":
                        sendText(exchange, apiToken);
                        break;
                    default:
                        System.out.println("/register ждёт GET-запрос, а получил " + exchange.getRequestMethod());
                        exchange.sendResponseHeaders(405, 0);
                }
            } finally {
                exchange.close();
            }
        }
    }

    private class HttpSaveHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                System.out.println("\n/save");
                if (!hasAuth(exchange)) {
                    System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                    exchange.sendResponseHeaders(403, 0);
                    return;
                }
                switch (exchange.getRequestMethod()) {
                    case "POST":
                        String key = exchange.getRequestURI().getPath().substring("/save/".length());
                        if (key.isEmpty()) {
                            System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                            exchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        String value = readText(exchange);
                        if (value.isEmpty()) {
                            System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                            exchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        data.put(key, value);
                        System.out.println("Значение для ключа " + key + " успешно обновлено!");
                        exchange.sendResponseHeaders(200, 0);
                        break;
                    default:
                        System.out.println("/save ждёт POST-запрос, а получил: " + exchange.getRequestMethod());
                        exchange.sendResponseHeaders(405, 0);
                }
            } finally {
                exchange.close();
            }
        }

    }

    private class HttpLoadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                System.out.println("\n/load");
                if (!hasAuth(exchange)) {
                    System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                    exchange.sendResponseHeaders(403, 0);
                    return;
                }
                switch (exchange.getRequestMethod()) {
                    case "GET":
                        String key = exchange.getRequestURI().getPath().substring("/load/".length());
                        if (key.isEmpty()) {
                            System.out.println("Key для получения пустой. key указывается в пути: /load/{key}");
                            exchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        String value = data.get(key);
                        if (value == null) {
                            System.out.println("По указанному key ничего нет.");
                            exchange.sendResponseHeaders(404, 0);
                            return;
                        }
                        System.out.println("Значение для ключа " + key + " успешно получено!");
                        sendText(exchange, value);
                        break;
                    default:
                        System.out.println("/save ждёт GET-запрос, а получил: " + exchange.getRequestMethod());
                        exchange.sendResponseHeaders(405, 0);
                }
            } finally {
                exchange.close();
            }
        }

    }
}
