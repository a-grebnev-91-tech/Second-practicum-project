package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import taskdata.Task;
import taskdata.TaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    HistoryManager history;

    @BeforeEach
    void createHistory() {
        history = new InMemoryHistoryManager();
    }

    @MethodSource("test1MethodSource")
    @ParameterizedTest(name = "{index}. Check history with {2}")
    void test1_shouldAddTasks(List<Task> tasks, List<Task> expectedTasks, String testResultDescription) {
        for (Task task : tasks) {
            history.add(task);
        }
        List<Task> tasksFromHistory = history.getHistory();
        assertNotNull(tasksFromHistory, "Задачи не возвращаются");
        assertEquals(expectedTasks, tasksFromHistory, "Задачи не совпадают");
    }

    private static Stream<Arguments> test1MethodSource() {
        int taskCount = 12;
        List<Task> simpleTasks = getSimpleTasks(taskCount);

        List<Task> repeatingTasks = new ArrayList<>();
        repeatingTasks.add(simpleTasks.get(0));
        repeatingTasks.add(simpleTasks.get(1));
        repeatingTasks.add(simpleTasks.get(1));
        repeatingTasks.add(simpleTasks.get(2));

        List<Task> expectedForRepeatingTasks = new ArrayList<>();
        expectedForRepeatingTasks.add(simpleTasks.get(0));
        expectedForRepeatingTasks.add(simpleTasks.get(1));
        expectedForRepeatingTasks.add(simpleTasks.get(2));

        List<Task> tenTasks = new ArrayList<>();
        for (int i = taskCount - 10; i < taskCount; i++) {
            tenTasks.add(simpleTasks.get(i));
        }

        taskCount = 3;
        List<Task> threeTasks = getSimpleTasks(3);

        return Stream.of(
                Arguments.of(new ArrayList<>(), new ArrayList<>(), "empty history"),
                Arguments.of(threeTasks, threeTasks, "not repeating and less than 10 tasks"),
                Arguments.of(repeatingTasks, expectedForRepeatingTasks, "repeating tasks"),
                Arguments.of(simpleTasks, tenTasks, "more then 10 tasks")
        );
    }

    @Test
    void test2_shouldCloneHistory() {
        for (Task task : getSimpleTasks(4)) {
            history.add(task);
        }
        HistoryManager clone = history.clone();
        assertNotNull(clone, "История не клонируется");
        assertEquals(clone.getHistory(), history.getHistory(), "История склонировалась не корректно");
    }

    @Test
    void test3_shouldReturnHistory() {
        List<Task> tasksToAdd = getSimpleTasks(3);
        for (Task t : tasksToAdd) {
            history.add(t);
        }
        assertNotNull(history.getHistory(), "Задачи не возвращаются");
        assertEquals(tasksToAdd, history.getHistory(), "Возвращаенные задачи не совпадают");
    }

    @MethodSource("test4MethodSource")
    @ParameterizedTest(name = "{index}. Check removing {3} history")
    void test4_shouldRemoveTasks(List<Task> tasks,
                                  List<Task> expectedTasks,
                                  int idToRemove,
                                  String testResultDescription) {
        for (Task task : tasks) {
            history.add(task);
        }
        history.remove((long) idToRemove);
        assertEquals(expectedTasks, history.getHistory(), "Задачи не совпадают");
    }

    private static Stream<Arguments> test4MethodSource() {
        int count = 5;
        int lastIndex = count - 1;
        int middleIndex = lastIndex / 2;
        List<Task> tasks = getSimpleTasks(count);

        List<Task> firstRemoved = new ArrayList<>(tasks);
        firstRemoved.remove(0);
        List<Task> middleRemoved = new ArrayList<>(tasks);
        middleRemoved.remove(middleIndex);
        List<Task> lastRemoved = new ArrayList<>(tasks);
        lastRemoved.remove(lastIndex);

        return Stream.of(
                Arguments.of(tasks, List.copyOf(firstRemoved), 0, "from beginning of"),
                Arguments.of(tasks, List.copyOf(middleRemoved), middleIndex, "from middle of"),
                Arguments.of(tasks, List.copyOf(lastRemoved), lastIndex, "from the end of"),
                Arguments.of(tasks, tasks, 222, "tasks that isn't exist in")
        );
    }

    @Test
    void test5_shouldRemoveBunchOfTasks() {
        List<Task> tasksToAdd = getSimpleTasks(5);
        List<Long> idsToRemove = List.of(2L, 3L);
        for (Task t : tasksToAdd) {
            history.add(t);
        }
        history.remove(idsToRemove);
        List<Task> expectedHistory = new ArrayList<>(tasksToAdd);
        expectedHistory.removeIf(task -> task.getID() == 2 || task.getID() == 3);
        assertEquals(expectedHistory, history.getHistory(), "Задачи не удалены");
    }

    @MethodSource("test6MethodSource")
    @ParameterizedTest(name = "{index}. Try to update {3} task")
    void test6_shouldUpdateTask(List<Task> tasks,
                                 Task taskToUpdate,
                                 List<Task> expectedTasksAfterUpd,
                                 String testResultDescription) {
        for (Task t : tasks) {
            history.add(t);
        }
        history.update(taskToUpdate);
        assertEquals(expectedTasksAfterUpd, history.getHistory(), "Требуемая задача не обновлена");
    }

    private static Stream<Arguments> test6MethodSource() {
        List<Task> tasks = getSimpleTasks(5);
        List<Task> expectedTasksAfterUpd = new ArrayList<>(tasks);

        Task taskToUpd = tasks.get(2).clone();
        taskToUpd.setStatus(TaskStatus.DONE);
        expectedTasksAfterUpd.remove(2);
        expectedTasksAfterUpd.add(2, taskToUpd);

        Task taskToUpdThatIsntExist = new Task(1000, TaskStatus.IN_PROGRESS, "a", "a");

        return Stream.of(
                Arguments.of(tasks, taskToUpd, expectedTasksAfterUpd, "existing"),
                Arguments.of(tasks, taskToUpdThatIsntExist, tasks, "not existing"));
    }

    @Test
    void test7_shouldUpdateBunchOfTask() {
        List<Task> tasks = getSimpleTasks(5);
        List<Task> tasksAfterUpdate = new ArrayList<>();
        for (Task task : tasks) {
            history.add(task);
            tasksAfterUpdate.add(task.clone());
        }

        for(Task task : tasksAfterUpdate) {
            task.setStatus(TaskStatus.DONE);
        }
        history.update(tasksAfterUpdate);

        assertEquals(tasksAfterUpdate, history.getHistory(), "Обновление группы задач произошло не корректно");
    }

    private static List<Task> getSimpleTasks(int count) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tasks.add(new Task(i, TaskStatus.NEW, "name", "desc"));
        }
        return List.copyOf(tasks);
    }

}