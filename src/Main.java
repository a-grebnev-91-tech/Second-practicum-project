import TaskData.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Task task = new Task("g", "Полей уже эти чертовы бегонии");
        task.setStatus(null);
        task.setId(-10);
        task.setName("");
        task.setDescription(null);
        System.out.println(task);
    }
}
