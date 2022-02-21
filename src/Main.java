import Manager.TaskManager;
import TaskData.*;

public class Main {

    private static TaskManager manager;

    private static Task firstTask;
    private static Task secondTask;
    private static EpicTask firstEpic;
    private static EpicTask secondEpic;
    private static Subtask firstSubtaskForFirstEpic;
    private static Subtask secondSubtaskForFirstEpic;
    private static Subtask subtaskForSecondEpic;

    public static void main(String[] args) {
        manager = new TaskManager();
        createSomeTestingTasks();
        printAllManagerTasks();
        changeSomeTasksStatus();
        printAllManagerTasks();
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
        System.out.println();
        System.out.println("------------------------------");
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

        secondEpic.setStatus(TaskStatus.DONE);
        manager.updateEpicTask(secondEpic);
    }
}
