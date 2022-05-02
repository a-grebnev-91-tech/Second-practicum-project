package manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import taskdata.*;
import util.Managers;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class TaskManagerTest <T extends TaskManager>{

    T manager;

    @MethodSource("test1MethodSource")
    @ParameterizedTest(name = "{index}. Check epic status with {2}")
    void test1_shouldCalculateEpicStatusBySubtasks(List<Subtask> subtasks, TaskStatus expected, String  kek) {
        EpicTask epic = new EpicTask("0", "0");
        manager.createTask(epic);
        for (Subtask subtask : subtasks) {
            manager.createTask(subtask);
        }
        TaskStatus epicStatus = manager.getEpicTask(1).getStatus();
        assertEquals(expected, epicStatus);
    }

    private static Stream<Arguments> test1MethodSource() {
        List<Subtask> emptyList = Collections.EMPTY_LIST;
        List<Subtask> newSubtasks = new ArrayList<>(List.of(
                new Subtask(1, "1", "1"),
                new Subtask(1, "2", "2"),
                new Subtask(1, "3", "3")
        ));
        List<Subtask> doneSubtasks = new ArrayList<>(List.of(
                new Subtask(0, 1, TaskStatus.DONE, "1", "1"),
                new Subtask(0, 1, TaskStatus.DONE, "2", "2"),
                new Subtask(0, 1, TaskStatus.DONE, "3", "3")
        ));
        List<Subtask> newDoneSubtasks = new ArrayList<>(List.of(
                new Subtask(0, 1, TaskStatus.DONE, "1", "1"),
                new Subtask(0, 1, TaskStatus.DONE, "2", "2"),
                new Subtask(0, 1, TaskStatus.NEW, "3", "3")
        ));
        List<Subtask> inProgressSubtasks = new ArrayList<>(List.of(
                new Subtask(0, 1, TaskStatus.IN_PROGRESS, "1", "1"),
                new Subtask(0, 1, TaskStatus.IN_PROGRESS, "2", "2"),
                new Subtask(0, 1, TaskStatus.IN_PROGRESS, "3", "3")
        ));
        List<Subtask> allStatusesSubtasks = new ArrayList<>(List.of(
                new Subtask(0, 1, TaskStatus.NEW, "1", "1"),
                new Subtask(0, 1, TaskStatus.IN_PROGRESS, "2", "12"),
                new Subtask(0, 1, TaskStatus.DONE, "3", "3")
        ));
        return Stream.of(
                Arguments.of(emptyList, TaskStatus.NEW, "empty subtasks"),
                Arguments.of(newSubtasks, TaskStatus.NEW, "new subtasks"),
                Arguments.of(doneSubtasks, TaskStatus.DONE, "done subtasks"),
                Arguments.of(newDoneSubtasks, TaskStatus.IN_PROGRESS, "new and done subtasks"),
                Arguments.of(inProgressSubtasks, TaskStatus.IN_PROGRESS, "in progress subtasks"),
                Arguments.of(allStatusesSubtasks, TaskStatus.IN_PROGRESS, "all types of statuses of subtasks")
        );
    }

    @MethodSource("test2MethodSource")
    @ParameterizedTest(name="{index}. Trying to create {2}")
    void test2_shouldCreateAndGetDifferentTasks(List<Task> tasksToCreate,
                                  List<Task> expectedCreatedTasks,
                                  String testResultDescription) {
        for (Task t : tasksToCreate) {;
            manager.createTask(t);
        }
        List<Task> createdTasks = new ArrayList<>(tasksToCreate.size());
        createdTasks.addAll(manager.getTasks());
        createdTasks.addAll(manager.getEpicTasks());
        createdTasks.addAll(manager.getSubtasks());
        createdTasks.sort(Comparator.comparingLong(Task::getID));
        assertEquals(expectedCreatedTasks, createdTasks, "Задачи создались не верно");
    }

    public static Stream<Arguments> test2MethodSource() {
        List<Task> nullTask = new ArrayList<>();
        nullTask.add(null);

        Task task = getTasks(1).get(0);
        EpicTask epic = getEpics(1).get(0);
        Subtask subtask = getSubtasks(1, 2).get(0);


        List<Task> oneTaskEachType = new ArrayList<>();
        oneTaskEachType.addAll(getTasks(1));
        oneTaskEachType.addAll(getEpics(1));
        oneTaskEachType.addAll(getSubtasks(1, 2));

        List<Task> expectedTasks = new ArrayList<>(oneTaskEachType);
        EpicTask expectedEpic = getEpics(1).get(0);
        expectedEpic.addSubtask(3);
        expectedEpic.setID(2);
        expectedTasks.remove(1);
        expectedTasks.add(1, expectedEpic);


        return Stream.of(
                Arguments.of(nullTask, new ArrayList<>(), "null task"),
                Arguments.of(oneTaskEachType, expectedTasks, "all type of Tasks")
        );
    }

    @Test
    void test3_shouldCreateAndGetTasks() {
        List<Task> tasks = getTasks(3);
        for (Task t : tasks) {
            manager.createTask(t);
        }
        assertEquals(tasks, manager.getTasks(), "Задачи не совпадают");
    }

    @Test
    void test4_shouldCreateAndGetEpics() {
        List<EpicTask> epics = getEpics(3);
        for (EpicTask epic : epics) {
            manager.createTask(epic);
        }
        assertEquals(epics, manager.getEpicTasks(), "Задачи не совпадают");
    }

    @Test
    void test5_shouldNotCreatingSubtasksWithoutEpic() {
        long invalidEpicId = 999;
        List<Subtask> subtasks = getSubtasks(3, invalidEpicId);
        for (Subtask subtask : subtasks) {
            manager.createTask(subtask);
        }
        assertEquals(Collections.EMPTY_LIST, manager.getSubtasks(), "Создаются задачи без эпика");
    }

    @Test
    void test6_shouldCreateSubtasksWithEpicAndGetSubtasks() {
        EpicTask epic = getEpics(1).get(0);
        manager.createTask(epic);
        List<Subtask> subtasks = getSubtasks(3, 1);
        for (Subtask subtask : subtasks) {
            manager.createTask(subtask);
        }
        epic.addSubtask(2);
        epic.addSubtask(3);
        epic.addSubtask(4);
        assertEquals(epic, manager.getEpicTask(1), "Эпик создан не верно");
        assertEquals(subtasks, manager.getSubtasks(), "Подзадачи не совпадают");
    }

    @MethodSource("test7MethodSource")
    @ParameterizedTest(name="{index}. Check epic creating behavior with id={0}")
    void test7_shouldReturnEpicTaskWhenIdIsValid(long id, EpicTask expectedEpic) {
        manager.createTask(getEpics(1).get(0));
        assertEquals(expectedEpic, manager.getEpicTask(id), "Возвращенная задача не совпадает с переданной");
    }

    public Stream<Arguments> test7MethodSource() {
        EpicTask epic = getEpics(1).get(0);
        epic.setID(1);
        return Stream.of(
                Arguments.of(1, epic),
                Arguments.of(0, null),
                Arguments.of(-999, null),
                Arguments.of(999, null)
        );
    }

    @MethodSource("test8MethodSource")
    @ParameterizedTest(name="{index}. Check subtask creating behavior with id={0}")
    void test8_shouldReturnSubtaskWhenIdIsValid(long id, Subtask expectedSubtask) {
        manager.createTask(getEpics(1).get(0));
        manager.createTask(getSubtasks(1,1).get(0));
        assertEquals(expectedSubtask, manager.getSubtask(id), "Возвращенная задача не совпадает с переданной");
    }

    public Stream<Arguments> test8MethodSource() {
        long subtaskId = 2;
        Subtask subtask = getSubtasks(1, 1).get(0);
        subtask.setID(subtaskId);
        return Stream.of(
                Arguments.of(subtaskId, subtask),
                Arguments.of(0, null),
                Arguments.of(-999, null),
                Arguments.of(999, null)
        );
    }

    @MethodSource("test9MethodSource")
    @ParameterizedTest(name="{index}. Check task creating and getting behavior with id={0}")
    void test9_shouldReturnTaskWhenIdIsValid(long id, Task expectedTask) {
        manager.createTask(getTasks(1).get(0));
        assertEquals(expectedTask, manager.getTask(id), "Возвращенная задача не совпадает с переданной");
    }

    public Stream<Arguments> test9MethodSource() {
        long taskId = 1;
        Task task = getTasks(1).get(0);
        task.setID(taskId);
        return Stream.of(
                Arguments.of(taskId, task),
                Arguments.of(0, null),
                Arguments.of(-999, null),
                Arguments.of(999, null)
        );
    }

    @Test
    void test10_shouldReturnSubtaskByEpicTask() {
        EpicTask epic = getEpics(1).get(0);
        List<Subtask> subtasks = getSubtasks(3, 1);
        manager.createTask(epic);
        for (Subtask subtask : subtasks) {
            manager.createTask(subtask);
        }
        List<Subtask> managerSubtasks = manager.getSubtasks();
        for (Subtask managerSubtask : managerSubtasks) {
            epic.addSubtask(managerSubtask.getID());
        }
        List<Subtask> returnedSubtasksByEpic = manager.getEpicTaskSubtasks(epic);
        assertEquals(subtasks, returnedSubtasksByEpic, "Возврщаенные задачи не совпдаают");
    }

    @Test
    void test11_getHistoryTest() {

    }

    public static List<Task> getTasks(int count) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tasks.add(new Task("a", "a"));
        }
        return List.copyOf(tasks);
    }

    public static List<EpicTask> getEpics(int count) {
        List<EpicTask> epics = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            epics.add(new EpicTask("a", "a"));
        }
        return List.copyOf(epics);
    }

    public static List<Subtask> getSubtasks(int count, long epicId) {
        List<Subtask> subtasks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            subtasks.add(new Subtask(epicId, "a", "a"));
        }
        return List.copyOf(subtasks);
    }

    public static Map<EpicTask, List<Subtask>> getOneEpicAndSomeSubtasks(int subtasksCount, int epicId) {
        Map<EpicTask, List<Subtask>> map = new HashMap<>();
        map.put(new EpicTask("a", "a"), getSubtasks(subtasksCount, epicId));
        return  map;
    }
}
