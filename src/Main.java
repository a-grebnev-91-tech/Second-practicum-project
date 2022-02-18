import TaskData.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        ArrayList<Integer> ints = new ArrayList<>();
        ints.add(1);
        Task task = new EpicTask(1, ints, TaskStatus.NEW, "Полить цветы", "Полей уже наконец эти чертовы бегонии");
        System.out.println(task);
    }
}
