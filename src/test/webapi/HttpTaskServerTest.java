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
    private static URI taskUri;
    private static URI epicUri;
    private static URI subtaskUri;
    private static URI historyUri;

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
        taskUri = uri.resolve("task");
        epicUri = uri.resolve("epic");
        subtaskUri = uri.resolve("subtask");
        historyUri = uri.resolve("history");
    }

    @AfterAll
    static void stopServer() {
        server.stop(0);
    }

    @Test
    public void test1_serverShouldReturn204StatusCodeWhenCreatingAnyTypeOfTask()
            throws IOException, InterruptedException {
        HttpResponse<String> taskCreateResponse = sendPostRequest(taskUri, gson.toJson(getTask()));
        assertEquals(204, taskCreateResponse.statusCode(), "Creating Task is fail");

        HttpResponse<String> epicCreateResponse = sendPostRequest(epicUri, gson.toJson(getEpicTask()));
        assertEquals(204, epicCreateResponse.statusCode(), "Creating Epic is fail");

        HttpResponse<String> subtaskCreateResponse = sendPostRequest(subtaskUri, gson.toJson(getSubtask(2)));
        assertEquals(204, epicCreateResponse.statusCode(), "Creating Subtask is fail");
    }

    @Test
    public void test2_serverShouldReturn422StatusCodeForBadJsonBodyToPost() throws IOException, InterruptedException {
        HttpResponse<String> taskCreateResponse = sendPostRequest(taskUri, "{bad json}");
        assertEquals(422, taskCreateResponse.statusCode(), "Creating Task with bad json is fail");

        HttpResponse<String> epicCreateResponse = sendPostRequest(epicUri, "{bad json}");
        assertEquals(422, epicCreateResponse.statusCode(), "Creating Epic with bad json is fail");

        HttpResponse<String> subtaskCreateResponse = sendPostRequest(subtaskUri, "{bad json}");
        assertEquals(422, subtaskCreateResponse.statusCode(), "Creating Subtask with bad json is fail");
    }

    @Test
    public void test3_shouldReturn405StatusCodeForUnsupportedMethod() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(taskUri)
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(getTask())))
                .build();
        HttpResponse<String> taskCreateResponse =client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, taskCreateResponse.statusCode(), "Unsupported method test is fail");
    }

    @Test
    public void test4_shouldReturn422StatusCodeForEmptyJsonBodyForPostMethod() throws IOException, InterruptedException {
        HttpResponse<String> emptyBodyTaskResponse = sendPostRequest(taskUri, "");
        assertEquals(422, emptyBodyTaskResponse.statusCode(), "Status code for task is unexpected");

        HttpResponse<String> emptyBodyEpicResponse = sendPostRequest(epicUri, "");
        assertEquals(422, emptyBodyEpicResponse.statusCode(), "Status code for epic is unexpected");

        HttpResponse<String> emptyBodySubtaskResponse = sendPostRequest(subtaskUri, "");
        assertEquals(422, emptyBodySubtaskResponse.statusCode(), "Status code for subtask is " +
                "unexpected");
    }

    @Test
    public void test5_

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