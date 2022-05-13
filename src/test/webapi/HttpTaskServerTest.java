package webapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
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
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static manager.TaskManagerTest.TEN_O_CLOCK;
import static org.junit.jupiter.api.Assertions.*;
import static webapi.HttpTaskServer.DEFAULT_CHARSET;

class HttpTaskServerTest {

    private HttpClient client;
    private HttpTaskServer server;
    private KVServer kvServer;
    private Gson gson;
    private URI uri;
    private URI taskUri;
    private URI epicUri;
    private URI subtaskUri;
    private URI historyUri;

    @BeforeEach
    public void startServerAndConfigClient() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
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

    @AfterEach
    public void stopServer() {
        server.stop(0);
        kvServer.stop();
    }

    @Test
    public void test1_serverShouldReturn204StatusCodeWhenCreatingAnyTypeOfTask()
            throws IOException, InterruptedException {
        HttpResponse<String> taskCreateResponse = sendPostRequest(taskUri, gson.toJson(getTask()));
        assertEquals(204, taskCreateResponse.statusCode(), "Creating Task is fail");

        HttpResponse<String> epicCreateResponse = sendPostRequest(epicUri, gson.toJson(getEpicTask()));
        assertEquals(204, epicCreateResponse.statusCode(), "Creating Epic is fail");

        HttpResponse<String> subtaskCreateResponse = sendPostRequest(subtaskUri, gson.toJson(getSubtask(2)));
        assertEquals(204, subtaskCreateResponse.statusCode(), "Creating Subtask is fail");
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
        HttpResponse<String> taskCreateResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
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
    public void test5_shouldReturn404ForBadUri() throws IOException, InterruptedException {
        HttpResponse<String> badUriResponse = sendPostRequest(uri.resolve("foo"), gson.toJson(getTask()));
        assertEquals(404, badUriResponse.statusCode(), "Bad uri test is fail");
    }

    @Test
    public void test6_shouldGetTasks() throws IOException, InterruptedException {
        sendPostRequest(taskUri, gson.toJson(getTask()));
        sendPostRequest(epicUri, gson.toJson(getEpicTask()));
        sendPostRequest(subtaskUri, gson.toJson(getSubtask(2)));
        long id = 1;
        HttpResponse<String> taskReturnResponse = sendGetRequest(uri.resolve("task/?id=" + id));
        assertEquals(200, taskReturnResponse.statusCode(), "Status code for task is unexpected");
        String acceptedTaskJson = new String(taskReturnResponse.body().getBytes(), DEFAULT_CHARSET);
        Task acceptedTask = gson.fromJson(acceptedTaskJson, Task.class);
        Task expectedTask = getTask();
        expectedTask.setID(id);
        assertEquals(expectedTask, acceptedTask, "Returned task is different");

        id = 2;
        HttpResponse<String> epicReturnResponse = sendGetRequest(uri.resolve("epic/?id=" + id));
        assertEquals(200, epicReturnResponse.statusCode(), "Status code for epic is unexpected");
        String acceptedEpicJson = new String(epicReturnResponse.body().getBytes(), DEFAULT_CHARSET);
        EpicTask acceptedEpic = gson.fromJson(acceptedEpicJson, EpicTask.class);
        EpicTask expectedEpic = getEpicTask();
        expectedEpic.setID(id);
        expectedEpic.addSubtask(3);
        assertEquals(expectedEpic, acceptedEpic, "Returned epic is different");

        id = 3;
        HttpResponse<String> subtaskReturnResponse = sendGetRequest(uri.resolve("subtask/?id=" + id));
        assertEquals(200, subtaskReturnResponse.statusCode(), "Status code for epic is unexpected");
        String acceptedSubtaskJson = new String(subtaskReturnResponse.body().getBytes(), DEFAULT_CHARSET);
        Subtask acceptedSubtask = gson.fromJson(acceptedSubtaskJson, Subtask.class);
        Subtask expectedSubtask = getSubtask(2);
        expectedSubtask.setID(id);
        assertEquals(expectedSubtask, acceptedSubtask, "Returned subtask is different");

        HttpResponse<String> allTaskReturnResponse = sendGetRequest(taskUri);
        assertEquals(200, allTaskReturnResponse.statusCode(), "Status code for all task is unexpected");
        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(expectedTask);
        String acceptedTasksJson = new String(allTaskReturnResponse.body().getBytes(), DEFAULT_CHARSET);
        List<Task> acceptedTasks = gson.fromJson(acceptedTasksJson, new TypeToken<List<Task>>(){}.getType());
        assertEquals(expectedTasks, acceptedTasks, "List of tasks are different");

        HttpResponse<String> allEpicsReturnResponse = sendGetRequest(epicUri);
        assertEquals(200, allEpicsReturnResponse.statusCode(), "Status code for all epics is " +
                "unexpected");
        List<EpicTask> expectedEpics = new ArrayList<>();
        expectedEpics.add(expectedEpic);
        String acceptedEpicsJson = new String(allEpicsReturnResponse.body().getBytes(), DEFAULT_CHARSET);
        List<EpicTask> acceptedEpics = gson.fromJson(acceptedEpicsJson, new TypeToken<List<EpicTask>>(){}.getType());
        assertEquals(expectedEpics, acceptedEpics, "List of epics are different");

        HttpResponse<String> allSubtasksReturnResponse = sendGetRequest(subtaskUri);
        assertEquals(200, allSubtasksReturnResponse.statusCode(), "Status code for all epics is " +
                "unexpected");
        List<Subtask> expectedSubtasks = new ArrayList<>();
        expectedSubtasks.add(expectedSubtask);
        String acceptedSubtasksJson = new String(allSubtasksReturnResponse.body().getBytes(), DEFAULT_CHARSET);
        List<Subtask> acceptedSubtasks = gson.fromJson(acceptedSubtasksJson,
                new TypeToken<List<Subtask>>(){}.getType());
        assertEquals(expectedSubtasks, acceptedSubtasks, "List of subtasks are different");

        //todo add subtasks of epic
        //todo add get wrong id all type of tasks
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