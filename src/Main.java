import Manager.TaskManager;
import TaskData.*;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        createSomeTestingTasks(manager);
        printAllManagerTasks(manager);

    }

    private static void createSomeTestingTasks(TaskManager manager) {
        Task firstTask = new Task("Полить цветы", "Полей ты уже эти чертовы бегонии");
        manager.createTask(firstTask);
        Task secondTask = new Task("Придумать имя новому коту", "Жена скзала, что меховой мешок не подходит!");
        manager.createTask(secondTask);

        EpicTask firstEpic = new EpicTask("Самосовершенствование", "Стань лучшей версией себя");
        manager.createEpicTask(firstEpic);
        Subtask firstSubtaskForFirstEpic = new Subtask(firstEpic.getID(), "Изучи Java", "Хоть жаба уродлива и медлительна"
                + ", но кто из нас идеален");
        manager.createSubtask(firstSubtaskForFirstEpic);
        Subtask secondSubtaskForFirstEpic = new Subtask(firstEpic.getID(), "Переведи бабушку через дорогу", "До кучи");
        manager.createSubtask(secondSubtaskForFirstEpic);


        EpicTask secondEpic = new EpicTask("Саморазрушение", "Не перетрудись с самосовершенствованием "
                + "а то еще станешь сверхчеловеком.");
        manager.createEpicTask(secondEpic);
        Subtask subtaskForSecondEpic = new Subtask(secondEpic.getID(), "Уйди в запой", "На неделю");
        manager.createSubtask(subtaskForSecondEpic);
    }

    private static void printAllManagerTasks(TaskManager manager) {
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

    // todo подумать над тем, использовать ли 2 метода, или 1
    private void changeTasksStatus(TaskManager manager) {
        manager.getTask(1).setStatus(TaskStatus.DONE);
        manager.getTask(2).setStatus(TaskStatus.IN_PROGRESS);
        manager.getSubtask(4).setStatus(TaskStatus.DONE);
        manager.getSubtask(7).setStatus(TaskStatus.IN_PROGRESS);
    }
}
