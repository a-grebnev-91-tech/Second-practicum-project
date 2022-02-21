import Manager.TaskManager;
import TaskData.*;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

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

        System.out.println("firstTask = " + firstTask);
        System.out.println("secondTask = " + secondTask);
        System.out.println("firstEpic = " + firstEpic);
        System.out.println("firstSubtaskFirstEpic = " + firstSubtaskForFirstEpic);
        System.out.println("secondSubtaskFirstEpic = " + secondSubtaskForFirstEpic);
        System.out.println("secondEpic = " + secondEpic);
        System.out.println("subtaskForSecondEpic = " + subtaskForSecondEpic);
        System.out.println("manager.getTasks() = " + manager.getTasks());
        System.out.println("manager.getEpicTasks() = " + manager.getEpicTasks());
        System.out.println("manager.getSubtasks() = " + manager.getSubtasks());
    }
}
