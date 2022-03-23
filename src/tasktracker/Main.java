package tasktracker;

import tasktracker.historydata.HistoryNode;
import tasktracker.manager.*;
import tasktracker.taskdata.EpicTask;
import tasktracker.taskdata.Subtask;
import tasktracker.taskdata.Task;
import tasktracker.taskdata.TaskStatus;

import java.util.List;

public class Main {

    private static TaskManager manager;

    private static Task firstTask;
    private static Task secondTask;
    private static EpicTask firstEpic;
    private static EpicTask secondEpic;
    private static Subtask firstSubtaskForFirstEpic;
    private static Subtask secondSubtaskForFirstEpic;
    private static Subtask subtaskForSecondEpic;
    private static Task thirdTask;
    private static Task fourthTask;
    private static EpicTask thirdEpic;
    private static EpicTask fourthEpic;
    private static Subtask firstSubtaskForThirdEpic;
    private static Subtask secondSubtaskForThirdEpic;

    public static void main(String[] args) {
        manager = Managers.getDefault();
        printMessage("Создаем задачи");
        createSomeTestingTasks();
        printAllManagerTasks();
        printMessage("Меняем статусы задач");
        changeSomeTasksStatus();
        printAllManagerTasks();
        printMessage("Делаем один эпик завершенным через завершение сабтасков");
        makeDoneOneOfEpics();
        printAllManagerTasks();

        printMessage("Проверяем пустую историю");
        checkHistory();

        printMessage("Получаем три задачи для тестирования истории, размер которой меньше 10");
        getThreeTasks();
        checkHistory();

        //тест 4го спринта
        printMessage("Добавляем больше задач для тестирования 4го спринта");
        addMoreTasks();
        printAllManagerTasks();

        printMessage("Добавляем больше задач в историю");
        getSomeTasks();
        secondSubtaskForThirdEpic.setStatus(TaskStatus.DONE);
        firstSubtaskForThirdEpic.setStatus(TaskStatus.DONE);
        manager.updateSubtask(secondSubtaskForThirdEpic);
        manager.updateSubtask(firstSubtaskForThirdEpic);
        checkHistory();

        printMessage("Удаляем один эпик, одну задачу и одну подзадачу (в другом эпике) (айдишники 1, 6, 4 (у 6 эпика " +
                "должна удалиться сабтаска с id 7");
        removeSomeTasks();
        printAllManagerTasks();

        printMessage("Проверяем историю");
        checkHistory();
    }

    private static void addMoreTasks() {
        thirdTask = new Task("Сделай паузу", "Скушай твикс");
        manager.createTask(thirdTask);
        fourthTask = new Task("Сходи в магазин", "В пятерочке у дома появился сахар!");
        manager.createTask(fourthTask);

        thirdEpic = new EpicTask("Готовься к эмиграции", "Пока не оказался в Северной Корее");
        manager.createEpicTask(thirdEpic);
        firstSubtaskForThirdEpic = new Subtask(thirdEpic.getID(), "Собирай манатки", "Пять пар трусов, "
                + " пять пар носков. Загран паспорт.");
        manager.createSubtask(firstSubtaskForThirdEpic);
        secondSubtaskForThirdEpic = new Subtask(thirdEpic.getID(), "Угони трактор", "Нужно транспортное"
                + " средство.");
        manager.createSubtask(secondSubtaskForThirdEpic);


        fourthEpic = new EpicTask("Пустой эпик", "Просто для тестирования.");
        manager.createEpicTask(fourthEpic);
    }

    private static void checkHistory() {
        List<Task> history = manager.history();
        System.out.println("Список просмотренных задач, 1 - самая старая, 10 - самая последняя:");
        for (int i = 0; i < history.size(); i++) {
            System.out.println(i + 1 + ". " + history.get(i));
        }
    }

    private static void getThreeTasks() {
        manager.getTask(1);
        manager.getEpicTask(3);
        manager.getSubtask(5);
        manager.getTask(1);
    }

    private static void getSomeTasks() {
        manager.getTask(2);
        manager.getEpicTask(6);
        manager.getSubtask(7);
        manager.getTask(1);
        manager.getTask(2);
        manager.getEpicTask(3);
        manager.getTask(22);   //ошибочная запись
        manager.getEpicTask(1); //ошибочная запись
        manager.getSubtask(4);
        manager.getSubtask(5);
        manager.getEpicTask(3);
        manager.getEpicTask(6);
        manager.getTask(1);
        manager.getTask(8);
        manager.getTask(9);
        manager.getTask(8);
        manager.getEpicTask(10);
        manager.getEpicTask(13);
        manager.getSubtask(11);
        manager.getSubtask(12);
        manager.getSubtask(12);
    }

    private static void printMessage(String message) {
        System.out.println();
        System.out.println("----------------");
        System.out.println(message);
        System.out.println("----------------");
        System.out.println();
    }

    private static void createSomeTestingTasks() {
        firstTask = new Task("Полить цветы", "Полей ты уже эти чертовы бегонии");
        manager.createTask(firstTask);
        secondTask = new Task("Придумать имя новому коту", "Жена скзала, что меховой мешок не подходит!");
        manager.createTask(secondTask);

        firstEpic = new EpicTask("Самосовершенствование", "Стань лучшей версией себя");
        manager.createEpicTask(firstEpic);
        firstSubtaskForFirstEpic = new Subtask(firstEpic.getID(), "Изучи Java", "Хоть жаба уродлива и медлительна"
                + ", но кто из нас идеален");
        manager.createSubtask(firstSubtaskForFirstEpic);
        secondSubtaskForFirstEpic = new Subtask(firstEpic.getID(), "Переведи бабушку через дорогу", "До кучи");
        manager.createSubtask(secondSubtaskForFirstEpic);


        secondEpic = new EpicTask("Саморазрушение", "Не перетрудись с самосовершенствованием "
                + "а то еще станешь сверхчеловеком.");
        manager.createEpicTask(secondEpic);
        subtaskForSecondEpic = new Subtask(secondEpic.getID(), "Уйди в запой", "На неделю");
        manager.createSubtask(subtaskForSecondEpic);
    }

    private static void printAllManagerTasks() {
        for (Task task : manager.getTasks()) {
            System.out.println("task = " + task);
        }

        for (EpicTask epicTask : manager.getEpicTasks()) {
            System.out.println("epicTask = " + epicTask);
        }

        for (Subtask subtask : manager.getSubtasks()) {
            System.out.println("subtask = " + subtask);
        }
    }

    private static void changeSomeTasksStatus() {
        firstTask.setStatus(TaskStatus.DONE);
        manager.updateTask(firstTask);

        secondTask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(secondTask);

        firstSubtaskForFirstEpic.setStatus(TaskStatus.DONE);
        manager.updateSubtask(firstSubtaskForFirstEpic);

        subtaskForSecondEpic.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtaskForSecondEpic);
    }

    private static void makeDoneOneOfEpics() {
        secondSubtaskForFirstEpic.setStatus(TaskStatus.DONE);
        manager.updateSubtask(secondSubtaskForFirstEpic);
    }

    private static void removeSomeTasks() {
        manager.removeEpicTask(secondEpic.getID());
        manager.removeTask(firstTask.getID());
        manager.removeSubtask(firstSubtaskForFirstEpic.getID());
    }
}
