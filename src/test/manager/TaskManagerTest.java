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

    private T manager;

    public final static LocalDateTime TEN_O_CLOCK = LocalDateTime.of(
            1991,
            Month.AUGUST,
            31,
            10,
            0);
    public static final Comparator<Task> TASK_COMPARATOR_BY_ID = (t1, t2) -> Long.compare(t1.getID(), t2.getID());

    public void setManager(T manager) {
        this.manager = manager;
    }

    public T getManager() {
        return manager;
    }

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
        assertEquals(tasks, manager.getTasks(), "Была удалена задача по неверному идентификатору");
        assertEquals(tasks, manager.history(), "Была удалена задача по неверному идентификатору из истории");
        assertEquals(tasks, new ArrayList<>(manager.getPrioritizedTasks()), "Была удалена задача по неверному" +
                " идентификатору из списка задач по приоритету");
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

        manager.getEpicTask(epicToRemove.getID());
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

    @Test
    void test15_shouldNotUpdateNullTaskOrNullStatus() {
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
        allTasks.sort(TASK_COMPARATOR_BY_ID);
        tasksToAdd = new ArrayList<>(tasksToAdd);
        tasksToAdd.sort(TASK_COMPARATOR_BY_ID);
        List<Task> prioritizedTasks = new ArrayList<>(manager.getPrioritizedTasks());
        prioritizedTasks.sort(TASK_COMPARATOR_BY_ID);
        List<Task> tasksFromHistory = new ArrayList<>(manager.history());
        tasksFromHistory.sort(TASK_COMPARATOR_BY_ID);

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
        allTasks.sort(TASK_COMPARATOR_BY_ID);
        tasksToAdd = new ArrayList<>(tasksToAdd);
        tasksToAdd.sort(TASK_COMPARATOR_BY_ID);
        List<Task> prioritizedTasks = new ArrayList<>(manager.getPrioritizedTasks());
        prioritizedTasks.sort(TASK_COMPARATOR_BY_ID);
        assertEquals(tasksToAdd, allTasks, "Обновление таски происходит не корректно");
        assertEquals(tasksToAdd, manager.history(), "Обновление таски в истории " +
                "происходит не корректно");
        assertEquals(tasksToAdd, prioritizedTasks, "Обновление таски " +
                "в списке задач по приоритету происходит не корректно");
    }

    @Test
    void test17_shouldUpdateSubtaskStatusAndDescriptionAndChangeEpicStatus() {
        EpicTask epicForValidation = getEpics(1).get(0);
        manager.createTask(epicForValidation);
        manager.getEpicTask(1);
        List<Subtask> subtasksToAdd = getSubtasks(3, 1);
        for (Task t : subtasksToAdd) {
            long id = manager.createTask(t);
            manager.getSubtask(id);
            epicForValidation.addSubtask(id);
        }
        Subtask subtaskToUpdate = subtasksToAdd.get(1);

        long updatingSubtasksId = subtaskToUpdate.getID();
        subtaskToUpdate.setStatus(TaskStatus.DONE);
        subtaskToUpdate.setDescription("new description");
        subtaskToUpdate.setTime(TEN_O_CLOCK, Duration.ofHours(1));
        manager.updateTask(subtaskToUpdate);

        List<Task> allTasks = new ArrayList<>(subtasksToAdd);
        epicForValidation.setStatus(TaskStatus.IN_PROGRESS);
        epicForValidation.setTime(TEN_O_CLOCK, Duration.ofHours(1));
        allTasks.add(epicForValidation);
        allTasks.sort(TASK_COMPARATOR_BY_ID);

        subtasksToAdd = new ArrayList<>(subtasksToAdd);
        subtasksToAdd.sort(TASK_COMPARATOR_BY_ID);
        List<Task> prioritizedTasks = new ArrayList<>(manager.getPrioritizedTasks());
        prioritizedTasks.sort(TASK_COMPARATOR_BY_ID);
        List<Task> tasksFromHistory = new ArrayList<>(manager.history());
        tasksFromHistory.sort(TASK_COMPARATOR_BY_ID);

        EpicTask epicFromManager = manager.getEpicTask(epicForValidation.getID());
        assertEquals(TaskStatus.IN_PROGRESS, epicFromManager.getStatus(), "Статус эпика не обновился");
        assertEquals(TEN_O_CLOCK, epicFromManager.getStartTime(), "СтартТайм эпика не обновился");
        assertEquals(TEN_O_CLOCK.plusHours(1), epicFromManager.getEndTime(), "ЭндТайм эпика не обновился");
        assertEquals(subtaskToUpdate, manager.getSubtask(updatingSubtasksId), "Обновление таски происходит " +
                "не корректно");
        assertEquals(allTasks, tasksFromHistory, "Обновление статуса таски в истории " +
                "происходит не корректно");
        assertEquals(allTasks, prioritizedTasks, "Обновление статуса таски " +
                "в списке задач по приоритету происходит не корректно");
    }

    @Test
    void test18_shouldUpdateEpicDescriptionAndName() {
        EpicTask epic = getEpics(1).get(0);
        manager.createTask(epic);
        long epicID = epic.getID();
        manager.getEpicTask(epicID);
        List<Subtask> subtasksToAdd = getSubtasks(3, 1);
        for (Subtask subtask : subtasksToAdd) {
            manager.createTask(subtask);
            manager.getSubtask(subtask.getID());
            epic.addSubtask(subtask.getID());
        }

        epic.setName("new Name");
        epic.setDescription("new Description");
        manager.updateTask(epic);

        List<Task> expectedTasks = new ArrayList<>(subtasksToAdd);
        expectedTasks.add(epic);
        expectedTasks.sort(TASK_COMPARATOR_BY_ID);
        List<Task> prioritizedTasks = new ArrayList<>(manager.getPrioritizedTasks());
        prioritizedTasks.sort(TASK_COMPARATOR_BY_ID);
        List<Task> tasksFromHistory = new ArrayList<>(manager.history());
        tasksFromHistory.sort(TASK_COMPARATOR_BY_ID);

        assertEquals(epic, manager.getEpicTask(epicID), "Эпик обновился не верно");
    }

    @Test
    void test19_shouldNotChangeEpicStatusOrTime() {
        EpicTask epicToAdding = getEpics(1).get(0);
        long epicID = manager.createTask(epicToAdding);
        manager.getEpicTask(epicID);
        List<Subtask> subtasksToAdd = getSubtasks(3, 1);
        for (int i = 0; i < subtasksToAdd.size(); i++) {
            Subtask subtask = subtasksToAdd.get(i);
            subtask.setTime(TEN_O_CLOCK.plusHours(i), Duration.ofHours(1));
            manager.createTask(subtask);
            manager.getSubtask(subtask.getID());
            epicToAdding.addSubtask(subtask.getID());
        }

        EpicTask expectingEpic = manager.getEpicTask(epicID);
        epicToAdding = expectingEpic.clone();
        epicToAdding.setStatus(TaskStatus.IN_PROGRESS);
        epicToAdding.setTime(TEN_O_CLOCK, Duration.ofHours(1));
        manager.updateTask(epicToAdding);

        assertNotEquals(expectingEpic, epicToAdding, "Ошибка в тесте");
        assertEquals(expectingEpic, manager.getEpicTask(epicID), "Состояние эпика изменилось");
    }

    @Test
    void test20_shouldUpdateEpicTimeAndStatusByUpdatingSubtask() {
        EpicTask epicToAdding = getEpics(1).get(0);
        long epicID = manager.createTask(epicToAdding);
        manager.getEpicTask(epicID);
        List<Subtask> subtasksToAdd = getSubtasks(3, 1);
        List<Long> subtasksIds = new ArrayList<>();
        for (int i = 0; i < subtasksToAdd.size(); i++) {
            Subtask subtask = subtasksToAdd.get(i);
            subtasksIds.add(manager.createTask(subtask));
            manager.getSubtask(subtask.getID());
        }

        Subtask subtaskToChanging = manager.getSubtask(subtasksIds.get(1));
        subtaskToChanging.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(subtaskToChanging);
        assertEquals(subtaskToChanging, manager.getSubtask(subtaskToChanging.getID()), "Статус подзадачи не " +
                "изменился");
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicTask(epicID).getStatus(), "Статус эпика " +
                "не изменился");

        subtaskToChanging.setTime(TEN_O_CLOCK, Duration.ofHours(1));
        manager.updateTask(subtaskToChanging);
        assertEquals(subtaskToChanging, manager.getSubtask(subtaskToChanging.getID()), "Время подзадачи не " +
                "изменилось");
        assertEquals(TEN_O_CLOCK, manager.getEpicTask(epicID).getStartTime(), "Время старта эпика " +
                "не изменилось");
        assertEquals(TEN_O_CLOCK.plusHours(1), manager.getEpicTask(epicID).getEndTime(), "Время окончания " +
                        "эпика не изменилось");

        Subtask subtaskToExtendEpicTime = manager.getSubtask(subtasksIds.get(0));
        subtaskToExtendEpicTime.setTime(TEN_O_CLOCK.plusHours(10), Duration.ofHours(10));
        manager.updateTask(subtaskToExtendEpicTime);

        assertEquals(TEN_O_CLOCK.plusHours(20), manager.getEpicTask(epicID).getEndTime(), "Время окончания " +
                "эпика не изменилось после добавления еще одной подзадачи");
    }

    @MethodSource("test21MethodSource")
    @ParameterizedTest(name="{index}. {2}")
    void test21_shouldNotCreateIntersectTask(List<Task> tasksToCreate, List<Task> expectedTasks, String paramsDescr) {
        for (Task task : tasksToCreate) {
            manager.createTask(task);
        }
        assertEquals(expectedTasks, new ArrayList<>(manager.getPrioritizedTasks()), "Время добавлено не верно");
    }

    Stream<Arguments> test21MethodSource() {
//        time (start)intersect null
        List<Task> firstCase = getTaskWithId(3);
        firstCase.get(0).setTime(TEN_O_CLOCK, Duration.ofHours(1));
        firstCase.get(1).setTime(TEN_O_CLOCK.plusMinutes(10), Duration.ofHours(1));
        List<Task> firstCaseExpected = new ArrayList<>(firstCase);
        firstCaseExpected.remove(1);

        //intersect(end) time time
        List<Task> secondCase = getTaskWithId(3);
        secondCase.get(0).setTime(TEN_O_CLOCK, Duration.ofHours(1));
        secondCase.get(1).setTime(TEN_O_CLOCK.plusHours(1), Duration.ofHours(1));
        secondCase.get(2).setTime(TEN_O_CLOCK.minusHours(1), Duration.ofMinutes(90));
        List<Task> secondCaseExpected = new ArrayList<>(secondCase);
        secondCaseExpected.remove(2);

        //time (start)intersect time
        List<Task> thirdCase = getTaskWithId(3);
        thirdCase.get(0).setTime(TEN_O_CLOCK, Duration.ofHours(1));
        thirdCase.get(1).setTime(TEN_O_CLOCK.plusHours(2), Duration.ofHours(1));
        thirdCase.get(2).setTime(TEN_O_CLOCK.plusMinutes(30), Duration.ofHours(1));
        List<Task> thirdCaseExpected = new ArrayList<>(thirdCase);
        thirdCaseExpected.remove(2);

        //time intersect(end) time
        List<Task> fourthCase = getTaskWithId(3);
        fourthCase.get(0).setTime(TEN_O_CLOCK, Duration.ofHours(1));
        fourthCase.get(1).setTime(TEN_O_CLOCK.plusHours(2), Duration.ofHours(1));
        fourthCase.get(2).setTime(TEN_O_CLOCK.plusHours(1), Duration.ofHours(2));
        List<Task> fourthCaseExpected = new ArrayList<>(fourthCase);
        fourthCaseExpected.remove(2);

        //time (start)intersect(end) time
        List<Task> fifthCase = getTaskWithId(3);
        fifthCase.get(0).setTime(TEN_O_CLOCK, Duration.ofHours(1));
        fifthCase.get(1).setTime(TEN_O_CLOCK.plusHours(1), Duration.ofHours(1));
        fifthCase.get(2).setTime(TEN_O_CLOCK.plusMinutes(30), Duration.ofHours(2));
        List<Task> fifthCaseExpected = new ArrayList<>(fifthCase);
        fifthCaseExpected.remove(2);

        //time (expanding intersect) time
        List<Task> sixthCase = getTaskWithId(3);
        sixthCase.get(0).setTime(TEN_O_CLOCK, Duration.ofHours(1));
        sixthCase.get(1).setTime(TEN_O_CLOCK.plusHours(1), Duration.ofHours(1));
        sixthCase.get(2).setTime(TEN_O_CLOCK.minusHours(1), Duration.ofHours(10));
        List<Task> sixthCaseExpected = new ArrayList<>(sixthCase);
        sixthCaseExpected.remove(2);

        return Stream.of(
                Arguments.of(firstCase, firstCaseExpected, "Intersect start of task between null and task"),
                Arguments.of(secondCase, secondCaseExpected, "Intersect end time before task"),
                Arguments.of(thirdCase, thirdCaseExpected, "Intersect start time between tasks"),
                Arguments.of(fourthCase, fourthCaseExpected, "Intersect end time between tasks"),
                Arguments.of(fifthCase, fifthCaseExpected, "Intersect start and end time between tasks"),
                Arguments.of(sixthCase, sixthCaseExpected, "New task intersect all tasks")
                );
    }

    @Test
    void test22_shouldCalculateEpicTimeBySubtasks() {
        EpicTask epic = getEpics(1).get(0);
        List<Subtask> subtasks = getSubtasks(3, 1);

        manager.createTask(epic);
        assertNull(manager.getEpicTask(1).getStartTime(), "Время у эпика не нулевое");
        assertNull(manager.getEpicTask(1).getEndTime(), "Время у эпика не нулевое");

        subtasks.get(0).setTime(TEN_O_CLOCK, Duration.ofHours(1));
        subtasks.get(1).setTime(TEN_O_CLOCK.plusHours(10), Duration.ofHours(10));
        for (Subtask subtask : subtasks) {
            manager.createTask(subtask);
        }

        assertEquals(TEN_O_CLOCK, manager.getEpicTask(1).getStartTime(), "Время эпика не рассчиталось");
        assertEquals(TEN_O_CLOCK.plusHours(20), manager.getEpicTask(1).getEndTime(), "Время эпика не рассчиталось");
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

    private List<Task> getTaskWithId(int count) {
        List<Task> tasks = getTasks(count);
        for (int i = 1; i <= count; i++) {
            tasks.get(i - 1).setID((long) i);
        }
        return tasks;
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
