package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import taskdata.Task;
import util.Managers;
import webapi.kv.KVServer;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager>{
    private KVServer server;
    private final String uri = "http://localhost:8078";

    @BeforeEach
    public void init() throws IOException {
        server = new KVServer();
        server.start();
        setManager(new HttpTaskManager(uri));
    }

    @AfterEach
    public void stopKVServer() {
        server.stop();
    }

    @MethodSource("test22MethodSource")
    @ParameterizedTest(name="{index}. Testing {2}")
    public void test22_shouldSaveAndLoadManagerState(List<Task> tasks,
                                                     List<Long> idsForHistory,
                                                     String description
    ) {
        getManager().save();
        for (Task task : tasks) {
            getManager().createTask(task);
        }
        for (long id : idsForHistory) {
            getManager().getTask(id);
            getManager().getEpicTask(id);
            getManager().getSubtask(id);
        }
        HttpTaskManager restored = new HttpTaskManager(uri);
        restored.load();
        assertEquals(getManager().getTasks(), restored.getTasks(), "Tasks are saving/loading incorrectly");
        assertEquals(getManager().getEpicTasks(), restored.getEpicTasks(), "Epics are saving/loading " +
                "incorrectly");
        assertEquals(getManager().getSubtasks(), restored.getSubtasks(), "Subtasks are saving/loading " +
                "incorrectly");
        assertEquals(getManager().history(), restored.history(), "History are saving/loading incorrectly");
        assertEquals(getManager().getPrioritizedTasks(), restored.getPrioritizedTasks(), "Prioritized tasks " +
                "are saving/loading incorrectly");
    }

    private Stream<Arguments> test22MethodSource() {
        List<Task> emptyTasks = Collections.EMPTY_LIST;
        List<Long> emptyHistory = Collections.EMPTY_LIST;

        List<Task> onlyThreeTask = getTasks(3);
        List<Long> twoOfThreeInHistory = List.of(1L, 2L);

        List<Task> emptySubtasks = new ArrayList<>(getTasks(3));
        emptySubtasks.addAll(getEpics(3));
        List<Long> historyOfTwoTasksAndOneEpic = List.of(1L, 2L, 3L);

        List<Task> allTypeOfTasks = new ArrayList<>(getTasks(3));
        allTypeOfTasks.addAll(getEpics(2));
        allTypeOfTasks.addAll(getSubtasks(3, 4));

        List<Long> notEmptyHistoryForAllTasks = List.of(1L, 2L, 3L, 4L, 5L, 8L);

        List<Task> timedTasks = new ArrayList<>(getTasks(3));
        timedTasks.get(0).setTime(TEN_O_CLOCK, Duration.ofHours(1));
        timedTasks.get(1).setTime(TEN_O_CLOCK.plusHours(1), Duration.ofHours(1));
        List<Long> historyForTimedTasks = List.of(1L, 3L);

        return Stream.of(
                Arguments.of(emptyTasks, emptyHistory, "empty manager"),
                Arguments.of(onlyThreeTask, twoOfThreeInHistory, "only tasks in manager"),
                Arguments.of(emptySubtasks, historyOfTwoTasksAndOneEpic, "tasks and epics in manager"),
                Arguments.of(allTypeOfTasks, emptyHistory, "all types of tasks and empty history"),
                Arguments.of(allTypeOfTasks, notEmptyHistoryForAllTasks, "all types of tasks"),
                Arguments.of(timedTasks, historyForTimedTasks, "timed tasks")
        );
    }
}