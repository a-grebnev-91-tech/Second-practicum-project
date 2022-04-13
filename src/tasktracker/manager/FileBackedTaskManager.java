package tasktracker.manager;

import tasktracker.taskdata.*;
import tasktracker.manager.util.csv.CsvFileLoader;
import tasktracker.manager.util.csv.CsvFileSaver;

import java.io.*;
import java.util.HashMap;

public class FileBackedTaskManager extends InMemoryTaskManager {
    CsvFileSaver saver;

    public FileBackedTaskManager(String file) {
        super();
        this.saver = new CsvFileSaver(file, this);
    }

    public FileBackedTaskManager(HashMap<Long, Task> tasks,
                                 HashMap<Long, EpicTask> epics,
                                 HashMap<Long, Subtask> subtasks,
                                 HistoryManager historyManager,
                                 long id,
                                 String file) {
        super(tasks, epics, subtasks, historyManager, id);
        this.saver = new CsvFileSaver(file, this);
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpicTasks() {
        super.removeAllEpicTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public Task getTask(long id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public EpicTask getEpicTask(long id) {
        EpicTask task = super.getEpicTask(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtask(long id) {
        Subtask task = super.getSubtask(id);
        save();
        return task;
    }

    @Override
    public boolean createTask(Task task) {
        boolean result = super.createTask(task);
        save();
        return result;
    }

    @Override
    public boolean createEpicTask(EpicTask epicTask) {
        boolean result = super.createEpicTask(epicTask);
        save();
        return result;
    }

    @Override
    public boolean createSubtask(Subtask subtask) {
        boolean result = super.createSubtask(subtask);
        save();
        return result;
    }

    @Override
    public boolean updateTask(Task task) {
        boolean result = super.updateTask(task);
        save();
        return result;
    }

    @Override
    public boolean updateEpicTask(EpicTask epicTask) {
        boolean result = super.updateEpicTask(epicTask);
        save();
        return result;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        boolean result = super.updateSubtask(subtask);
        save();
        return result;
    }

    @Override
    public boolean removeTask(long id) {
        boolean result = super.removeTask(id);
        save();
        return result;
    }

    @Override
    public boolean removeEpicTask(long id) {
        boolean result = super.removeEpicTask(id);
        save();
        return result;
    }

    @Override
    public boolean removeSubtask(long id) {
        boolean result = super.removeSubtask(id);
        save();
        return result;
    }

    private void save() {
        saver.save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        return CsvFileLoader.load(file);
    }

    public static void main(String[] args) {
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(
                new File("resources" + File.separator + "file.txt"));
        Task task = new Task("Полей цветы", "Полей уже эти чертовы бегонии");
        EpicTask epic = new EpicTask("Генеральная уборка", "Весна - время приключений");
        manager.createTask(task);
        manager.createEpicTask(epic);
        Subtask subtask = new Subtask(epic.getID(), "Кладовая", "Убери в кладовой");
        EpicTask epic1 = new EpicTask("Выучи принцыпы кастыльно-ориентированного программирования",
                "Инкастыляция, накастыливание и поликастылизм");
        manager.createSubtask(subtask);
        manager.createEpicTask(epic1);
        epic1.setStatus(TaskStatus.DONE);
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        Task task1 = manager.getTask(2);
        task1.setStatus(TaskStatus.DONE);
        manager.updateTask(task1);
    }
}
