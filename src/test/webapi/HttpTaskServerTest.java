package webapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import taskdata.EpicTask;
import taskdata.Subtask;
import taskdata.Task;
import util.web.json.adapters.EpicTaskAdapter;
import util.web.json.adapters.SubtaskAdapter;
import util.web.json.adapters.TaskAdapter;
import webapi.kv.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static manager.TaskManagerTest.TEN_O_CLOCK;
import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private static HttpClient client;
    private static HttpTaskServer server;
    private static Gson gson;
    private static URI uri;

    @BeforeAll
    static void startServerAndConfigClient() throws IOException {
        new KVServer().start();
        server = new HttpTaskServer();
        client = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskAdapter())
                .registerTypeAdapter(EpicTask.class, new EpicTaskAdapter())
                .registerTypeAdapter(Subtask.class, new SubtaskAdapter())
                .create();
        uri = URI.create("http://localhost:8080/tasks/");
    }

    @AfterAll
    static void stopServer() {
        server.stop(0);
    }

    @Test
    public void test1_serverShouldReturn204StatusCodeWhenCreatingAnyTypeOfTask()
            throws IOException, InterruptedException {
        HttpResponse<String> taskCreateResponse = sendPostRequest(uri.resolve("task"), gson.toJson(getTask()));
        assertEquals(204, taskCreateResponse.statusCode(), "Creating Task is fail");

        HttpResponse<String> epicCreateResponse = sendPostRequest(uri.resolve("epic"), gson.toJson(getEpicTask()));
        assertEquals(204, epicCreateResponse.statusCode(), "Creating Epic is fail");

        HttpResponse<String> subtaskCreateResponse =
                sendPostRequest(uri.resolve("subtask"), gson.toJson(getSubtask(2)));
        assertEquals(204, epicCreateResponse.statusCode(), "Creating Subtask is fail");
    }

    private Task getTask() {
        return new Task("task", "task descr", TEN_O_CLOCK, Duration.ofHours(1));
    }

    private EpicTask getEpicTask() {
        return new EpicTask("epic", "epic descr");
    }

    private Subtask getSubtask(long epicId) {
        return new Subtask(epicId, "subtask", "subtask descr");
    }

    private HttpResponse<String> sendGetRequest(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPostRequest(URI uri, String body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body)).uri(uri).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDeleteRequest(URI uri, String body) {
        return null;
    }
}