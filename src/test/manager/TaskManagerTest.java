package manager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import taskdata.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class TaskManagerTest<T extends TaskManager> {

    T manager;
    public final static LocalDateTime TEN_O_CLOCK = LocalDateTime.of(
            1991,
            Month.AUGUST,
            31,
            10,
            0);


    @MethodSource("test1MethodSource")
    @ParameterizedTest(name = "{index}. Check epic status with {2}")
    void test1_shouldCalculateEpicStatusBySubtasks(List<Subtask> subtasks,
                                                   TaskStatus expected,
                                                   String testResultDescription) {
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

    @Test
    void test2_shouldNotChangeEpicStatusByItself() {
        EpicTask epic = getEpics(1).get(0);
        manager.createTask(epic);
        EpicTask attemptMakeDoneEpic = manager.getEpicTask(1);
        attemptMakeDoneEpic.setStatus(TaskStatus.DONE);
        manager.updateTask(attemptMakeDoneEpic);
        assertEquals(TaskStatus.NEW, manager.getEpicTask(1).getStatus(), "Статус изменился");
    }

//TODO del
//    @MethodSource("test2MethodSource")
//    @ParameterizedTest(name="{index}. Trying to create {2}")
//    void test2_shouldCreateAndGetDifferentTasks(List<Task> tasksToCreate,
//                                  List<Task> expectedCreatedTasks,
//                                  String testResultDescription) {
//        for (Task t : tasksToCreate) {;
//            manager.createTask(t);
//        }
//        List<Task> createdTasks = new ArrayList<>(tasksToCreate.size());
//        createdTasks.addAll(manager.getTasks());
//        createdTasks.addAll(manager.getEpicTasks());
//        createdTasks.addAll(manager.getSubtasks());
//        createdTasks.sort(Comparator.comparingLong(Task::getID));
//        assertEquals(expectedCreatedTasks, createdTasks, "Задачи создались не верно");
//    }
//
//    public static Stream<Arguments> test2MethodSource() {
//        List<Task> nullTask = new ArrayList<>();
//        nullTask.add(null);
//
//        Task task = getTasks(1).get(0);
//        EpicTask epic = getEpics(1).get(0);
//        Subtask subtask = getSubtasks(1, 2).get(0);
//
//
//        List<Task> oneTaskEachType = new ArrayList<>();
//        oneTaskEachType.addAll(getTasks(1));
//        oneTaskEachType.addAll(getEpics(1));
//        oneTaskEachType.addAll(getSubtasks(1, 2));
//
//        List<Task> expectedTasks = new ArrayList<>(oneTaskEachType);
//        EpicTask expectedEpic = getEpics(1).get(0);
//        expectedEpic.addSubtask(3);
//        expectedEpic.setID(2);
//        expectedTasks.remove(1);
//        expectedTasks.add(1, expectedEpic);
//
//
//        return Stream.of(
//                Arguments.of(nullTask, new ArrayList<>(), "null task"),
//                Arguments.of(oneTaskEachType, expectedTasks, "all type of Tasks")
//        );
//    }

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
    @ParameterizedTest(name = "{index}. Check epic creating behavior with id={0}")
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
    @ParameterizedTest(name = "{index}. Check subtask creating behavior with id={0}")
    void test8_shouldReturnSubtaskWhenIdIsValid(long id, Subtask expectedSubtask) {
        manager.createTask(getEpics(1).get(0));
        manager.createTask(getSubtasks(1, 1).get(0));
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
    @ParameterizedTest(name = "{index}. Check task creating and getting behavior with id={0}")
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
        manager.getTask(999);
        assertEquals(Collections.EMPTY_LIST, manager.history(), "История задач создается не пустой");
        List<Task> expectedHistory = getTasks(3);
        for (Task t : expectedHistory) {
            manager.createTask(t);
        }
        for (int i = 1; i <= expectedHistory.size(); i++) {
            manager.getTask(i);
        }
        assertEquals(expectedHistory, manager.history(), "История отображается не верно");

    }

    @Test
    void test12_shouldRemoveTasks() {
        List<Task> tasks = new ArrayList<>(getTasks(3));
        for (Task task : tasks) {
            manager.createTask(task);
            manager.getTask(task.getID());
        }
        manager.removeTask(-99);
        manager.removeTask(99);
        assertEquals(tasks, manager.getTasks(), "Была удалена задача по неверному идентефикатору");
        assertEquals(tasks, manager.history(), "Была удалена задача по неверному идентефикатору из истории");
        assertEquals(tasks, new ArrayList<>(manager.getPrioritizedTasks()), "Была удалена задача по неверному" +
                " идентефикатору из списка задач по приоритету");
        Task taskToRemove = tasks.get(1);
        tasks.remove(taskToRemove);
        manager.removeTask(taskToRemove.getID());
        assertEquals(tasks, manager.getTasks(), "Задача была не удалена или удалена не та задача");
        assertEquals(tasks, manager.history(), "Задача была не удалена из истории или была удалена " +
                "не та задача");
        assertEquals(tasks, new ArrayList<>(manager.getPrioritizedTasks()), "Задача была не удалена " +
                "или была удалена не та задача из списка задач по приоритету");


        manager.removeAllTasks();
        assertEquals(Collections.EMPTY_LIST, manager.getTasks(), "Не все задачи были удалены");
        assertEquals(Collections.EMPTY_LIST, manager.history(), "Не все задачи были удалены из истории");
        assertEquals(Collections.EMPTY_LIST, new ArrayList<>(manager.getPrioritizedTasks()), " Не все задачи" +
                "были удалены из списка задач по приоритету");
    }

    @Test
    void test13_shouldRemoveSubtasks() {
        EpicTask epic = getEpics(1).get(0);
        manager.createTask(epic);
        List<Subtask> subtasks = new ArrayList<>(getSubtasks(3, 1));
        for (Subtask subtask : subtasks) {
            manager.createTask(subtask);
            epic.addSubtask(subtask.getID());
        }

        Subtask subtaskToRemove = subtasks.get(1);
        manager.getSubtask(subtaskToRemove.getID());
        manager.removeTask(subtaskToRemove.getID());
        assertEquals(Collections.EMPTY_LIST, manager.history(), "Подзадача не удалились из истории");


        subtasks.remove(subtaskToRemove);

        List<Long> expectedIds = new ArrayList<>();
        for (Subtask subtask : subtasks) {
            expectedIds.add(subtask.getID());
        }

        assertEquals(subtasks, manager.getSubtasks(), "Подзадача была не удалена или удалена не та подзадача");
        assertEquals(expectedIds, manager.getEpicTask(1).getSubtasksID(), "Подзадача была не удалена " +
                "из эпика или была удалена не та подзадача"
        );

        manager.getSubtask(subtasks.get(0).getID());
        manager.removeAllSubtasks();
        List<Task> prioritizedTasks = new ArrayList<>(manager.getPrioritizedTasks());
        assertEquals(Collections.EMPTY_LIST, manager.getSubtasks(), "Не все подзадачи удалены");
        assertEquals(Collections.EMPTY_LIST, manager.getEpicTask(1).getSubtasksID(), "Не все задачи были " +
                "удалены из эпика");
        assertEquals(manager.getEpicTasks(), manager.history(), "Подзадачи не удалились из истории");
        assertEquals(manager.getEpicTasks(), prioritizedTasks, "Подзадачи неверно удаляются из списка задач " +
                "по приоритету");
    }

    @Test
    void test14_shouldRemoveEpicTasksAndAllTheirSubtasks() {
        List<EpicTask> epics = getEpics(3);
        for (EpicTask epic : epics) {
            manager.createTask(epic);
        }
        List<Subtask> subtasks = new ArrayList<>(getSubtasks(1, 1));
        subtasks.addAll(getSubtasks(2, 2));
        subtasks.addAll(getSubtasks(3, 3));
        for (Subtask subtask : subtasks) {
            manager.createTask(subtask);
            long epicId = subtask.getEpicTaskID();
        }
        epics = manager.getEpicTasks();

        EpicTask epicToRemove = epics.get(1);

        manager.getTask(epicToRemove.getID());
        manager.getSubtask(epicToRemove.getSubtasksID().get(0));

        manager.removeTask(epicToRemove.getID());
        assertEquals(Collections.EMPTY_LIST, manager.history(), "Эпик и подзадачи не удалились из истории");

        epics.remove(epicToRemove);
        subtasks.removeIf(subtask -> subtask.getEpicTaskID() == epicToRemove.getID());

        List<Task> allTasks = new ArrayList<>(epics);
        allTasks.addAll(subtasks);

        assertEquals(allTasks, new ArrayList<>(manager.getPrioritizedTasks()), "Эпики и/или подзадачи " +
                "неверно удаляются из списка задач по приоритету");

        assertEquals(epics, manager.getEpicTasks(), "Эпик был не удален или был удален не тот эпик");
        assertEquals(subtasks, manager.getSubtasks(), "Подзадачи эпика были не удалены или были удалены " +
                "не те подзадачи");

        manager.removeAllEpicTasks();
        assertEquals(Collections.EMPTY_LIST, manager.getEpicTasks(), "Не все эпики удалены");
        assertEquals(Collections.EMPTY_LIST, manager.getSubtasks(), "Не все подзадачи эпиков были удалены");
        assertEquals(Collections.EMPTY_SET, manager.getPrioritizedTasks(), "Эпики и/или подзадачи " +
                "неверно удаляются из списка задач по приоритету");
        assertEquals(Collections.EMPTY_LIST, manager.history(), "Подзадачи/эпики не удалились из истории");
    }

//    @MethodSource("test15MethodSource")
//    @ParameterizedTest(name = "{index}. Should {2}")
//    void test15_shouldUpdateTaskIfItIsValid(List<Task> tasksToAdd, Task taskToUpdate, String testResultDescription) {
//        Comparator<Task> comparator = (t1, t2) -> Long.compare(t1.getID(), t2.getID());
//        for (Task t : tasksToAdd) {
//            manager.createTask(t);
//            manager.getTask(t.getID());
//        }
//        manager.updateTask(taskToUpdate);
//        List<Task> allTasksAfterUpdate = new ArrayList<>(manager.getTasks());
//        allTasksAfterUpdate.addAll(manager.getSubtasks());
//        allTasksAfterUpdate.addAll(manager.getEpicTasks());
//        allTasksAfterUpdate.sort(comparator);
//        tasksToAdd = new ArrayList<>(tasksToAdd);
//        tasksToAdd.sort(comparator);
//        assertEquals(tasksToAdd, manager.history(), "Обновление таски в истории происходит не корректно");
//        assertEquals(tasksToAdd, new ArrayList<>(manager.getPrioritizedTasks()), "Обновление таски " +
//                "в списке задач по приоритету происходит не корректно");
//        if (taskToUpdate == null) {
//            assertEquals(tasksToAdd, allTasksAfterUpdate, "Обновление таски со значением null " +
//                    "происходит не корректно");
//            return;
//        }
//        Long id = taskToUpdate.getID();
//        switch (taskToUpdate.getType()) {
//            case TASK:
//                assertEquals(manager.getTask(id), taskToUpdate, "Задача изменена не верно");
//                break;
//            case EPIC:
//                assertEquals(manager.getEpicTask(id), (EpicTask) taskToUpdate, "Задача изменена не верно");
//                break;
//            case SUBTASK:
//                assertEquals(manager.getSubtask(id), (Subtask) taskToUpdate, "Задача изменена не верно");
//        }
//    }
//
//    Stream<Arguments> test15MethodSource() {
//        List<Task> tasksToSimpleUpdate = getTasks(3);
//        Task taskToSimpleUpdate = tasksToSimpleUpdate.get(1).clone();
//        taskToSimpleUpdate.setID(2);
////        taskToSimpleUpdate.
//        return Stream.of(
//                Arguments.of(getTasks(3), null, "not update null task"),
//                Arguments.of(getTasks(3)));
//    }

    @Test
    void test15_shouldNotUpdateNullTaskOrNullStatus() {
        Comparator<Task> comparator = (t1, t2) -> Long.compare(t1.getID(), t2.getID());
        List<Task> tasksToAdd = getTasks(3);
        for (Task t : tasksToAdd) {
            long id = manager.createTask(t);
            manager.getTask(id);
        }
        Task taskToUpdate = tasksToAdd.get(1);
        long idUpdatingTask = taskToUpdate.getID();
        taskToUpdate.setStatus(null);

        manager.updateTask(taskToUpdate);
        manager.updateTask(null);
        List<Task> allTasks = new ArrayList<>(manager.getTasks());
        allTasks.addAll(manager.getSubtasks());
        allTasks.addAll(manager.getEpicTasks());
        allTasks.sort(comparator);
        tasksToAdd = new ArrayList<>(tasksToAdd);
        tasksToAdd.sort(comparator);
        List<Task> prioritizedTasks = new ArrayList<>(manager.getPrioritizedTasks());
        prioritizedTasks.sort(comparator);
        List<Task> tasksFromHistory = new ArrayList<>(manager.history());
        tasksFromHistory.sort(comparator);

        assertEquals(TaskStatus.NEW, manager.getTask(idUpdatingTask).getStatus(), "Произошло обновление " +
                "статуса задачи, при передачи null вместо статуса");
        assertEquals(tasksToAdd, allTasks, "Обновление таски со значениеми null происходит не корректно");
        assertEquals(tasksToAdd, tasksFromHistory, "Обновление таски в истории со значениями null " +
                "происходит не корректно");
        assertEquals(tasksToAdd, prioritizedTasks, "Обновление таски " +
                "со значениями null в списке задач по приоритету происходит не корректно");
    }

    @Test
    void test16_shouldUpdateTaskStatusAndDescriptionAndTime() {
        Comparator<Task> comparator = (t1, t2) -> Long.compare(t1.getID(), t2.getID());
        List<Task> tasksToAdd = getTasks(3);
        for (Task t : tasksToAdd) {
            long id = manager.createTask(t);
            manager.getTask(id);
        }
        Task taskToUpdate = tasksToAdd.get(1);

        taskToUpdate.setStatus(TaskStatus.DONE);
        taskToUpdate.setDescription("new description");
        taskToUpdate.setTime(TEN_O_CLOCK, Duration.ofHours(1));
        manager.updateTask(taskToUpdate);

        List<Task> allTasks = new ArrayList<>(manager.getTasks());
        allTasks.addAll(manager.getSubtasks());
        allTasks.addAll(manager.getEpicTasks());
        allTasks.sort(comparator);
        tasksToAdd = new ArrayList<>(tasksToAdd);
        tasksToAdd.sort(comparator);
        List<Task> prioritizedTasks = new ArrayList<>(manager.getPrioritizedTasks());
        prioritizedTasks.sort(comparator);
        assertEquals(tasksToAdd, allTasks, "Обновление таски происходит не корректно");
        assertEquals(tasksToAdd, manager.history(), "Обновление таски в истории " +
                "происходит не корректно");
        assertEquals(tasksToAdd, prioritizedTasks, "Обновление таски " +
                "в списке задач по приоритету происходит не корректно");
    }

    @Test
    void test17_shouldUpdateSubtaskStatusAndDescriptionAndChangeEpicStatus() {
        EpicTask epic = getEpics(1).get(0);
        manager.createTask(epic);
        manager.getTask(1);
        Comparator<Task> comparator = (t1, t2) -> Long.compare(t1.getID(), t2.getID());
        List<Subtask> subtasksToAdd = getSubtasks(3, 1);
        for (Task t : subtasksToAdd) {
            long id = manager.createTask(t);
            manager.getTask(id);
        }
        Subtask subtaskToUpdate = subtasksToAdd.get(1);

        long updatingSubtasksId = subtaskToUpdate.getID();
        subtaskToUpdate.setStatus(TaskStatus.DONE);
        subtaskToUpdate.setDescription("new description");
        manager.updateTask(subtaskToUpdate);

        List<Task> allTasks = new ArrayList<>(manager.getTasks());
        allTasks.addAll(manager.getSubtasks());
        allTasks.addAll(manager.getEpicTasks());
        allTasks.sort(comparator);

        subtasksToAdd = new ArrayList<>(subtasksToAdd);
        subtasksToAdd.sort(comparator);
        List<Task> prioritizedTasks = new ArrayList<>(manager.getPrioritizedTasks());
        prioritizedTasks.sort(comparator);
        List<Task> tasksFromHistory = new ArrayList<>(manager.history());
        tasksFromHistory.sort(comparator);

        assertEquals(subtaskToUpdate, manager.getSubtask(updatingSubtasksId), "Обновление таски происходит " +
                "не корректно");
        assertEquals(allTasks, tasksFromHistory, "Обновление статуса таски в истории " +
                "происходит не корректно");
        assertEquals(allTasks, prioritizedTasks, "Обновление статуса таски " +
                "в списке задач по приоритету происходит не корректно");
    }

    @Test
    void testNN_shouldCalculateEpicTimeBySubtasks() {
        EpicTask epic = getEpics(1).get(0);
        epic.setTime(TEN_O_CLOCK, Duration.ofHours(2));
        manager.createTask(epic);
        System.out.println(manager.getEpicTasks());
        List<Subtask> subtasks = getSubtasks(4, 1);
        subtasks.get(0).setTime(TEN_O_CLOCK.minusHours(3), Duration.ofHours(1));
        manager.createTask(subtasks.get(0));
        System.out.println(manager.getEpicTasks());
        subtasks.get(1).setTime(TEN_O_CLOCK, Duration.ofHours(1));
        manager.createTask(subtasks.get(1));
        System.out.println(manager.getEpicTasks());
        subtasks.get(2).setTime(TEN_O_CLOCK.minusHours(4), Duration.ofMinutes(5));
        manager.createTask(subtasks.get(2));
        System.out.println(manager.getEpicTasks());
        subtasks.get(3).setTime(TEN_O_CLOCK.minusHours(5), Duration.ofHours(10));
        manager.createTask(subtasks.get(3));
        System.out.println(manager.getEpicTasks());
        epic = manager.getEpicTask(1);
        epic.setDescription("kek");
        manager.updateTask(epic);
        System.out.println(manager.getEpicTasks());
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
        return map;
    }
}
