import TaskData.*;

public class Main {
    public static void main(String[] args) {
        EpicTask task = new EpicTask("Задача", "Task");
        EpicTask another = task.clone();
        System.out.println(task);
        System.out.println(another);
        System.out.println(task == another);
        System.out.println(task.equals(another));
    }
}
