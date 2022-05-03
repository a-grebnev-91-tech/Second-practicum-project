package test;

import manager.FileBackedTaskManager;
import util.Managers;
import manager.TaskManager;
import taskdata.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.TreeSet;

//TODO удалить эти классы
public class FifthSprintTest {

    private static TaskManager manager;
    private static String file = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "file.csv";

    public static void main(String[] args) throws IOException{
        System.out.println("Создадим мэнеджер, пишущий в файл и немного его потыкаем\n");
        manager = Managers.getFileBackedManager(file);
        createSomeTasks();
        getSomeTasks();
        finishEpic();
        printFile();

        System.out.println("Открываем менеджер из файла и его тоже потыкаем\n");
        manager = FileBackedTaskManager.loadFromFile(new File(file));
        changeSomeManagerState();
        printFile();
        TreeSet<Task> priorTasks = manager.getPrioritizedTasks();
        Task task = new Task("intersect", "test", LocalDateTime.now(), Duration.ofMinutes(12));
        manager.createTask(task);
        Task birth = new Task("births", "test", LocalDateTime.of(1991, Month.AUGUST, 31, 16, 45), Duration.ofMinutes(10));
        manager.createTask(birth);
        for (Task taskk
                : priorTasks)
            System.out.println(taskk);
    }

    private static void printFile() {
        System.out.println("Содержание файла:");
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                System.out.println(reader.readLine());
            }
        } catch (IOException ex) {
            System.out.println("Метод тестирования потерпел фиаско, братан");
        }
        System.out.println();
    }

    private static void createSomeTasks() {
        //id 1
        Task task = new Task("Сделай паузу", "Скушай \"Twix\"", LocalDateTime.now(), Duration.ofHours(1));
        //id 2
        EpicTask epic = new EpicTask("Генеральная уборка", "Весна - время приключений");
        //id 3
        Subtask subtask = new Subtask(2, "Кладовая", "Убери в кладовой", LocalDateTime.now().minusHours(2), Duration.ofHours(1));
        //id 4
        EpicTask epic1 = new EpicTask("Выучи принципы кастыльно-ориентированного программирования",
                "Инкастыляция, накастыливание и поликастылизм");
        System.out.println(manager.createTask(task));
        System.out.println(manager.createTask(epic));
        System.out.println(manager.createTask(subtask));
        System.out.println(manager.createTask(epic1));
    }

    private static void getSomeTasks() {
        manager.getTask(1);
        manager.getSubtask(3);
        manager.getEpicTask(2);
        manager.getEpicTask(4);
    }

    private static void finishEpic() {
        Subtask subTask = manager.getSubtask(3);
        subTask.setStatus(TaskStatus.DONE);
        manager.updateTask(subTask);
    }

    private static void changeSomeManagerState() {
        Task task = manager.getTask(1);
        task.setStatus(TaskStatus.DONE);
        manager.updateTask(task);

        Subtask subtask = manager.getSubtask(3);
        subtask.setDescription("Я сделяль");
        manager.updateTask(subtask);
    }
}