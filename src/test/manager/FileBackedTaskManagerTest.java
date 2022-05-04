package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskdata.EpicTask;
import taskdata.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static util.csv.CsvFileSaver.FILE_HEADER;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private static String file = "src"
            + File.separator
            + "test"
            + File.separator
            + "manager"
            + File.separator
            + "file.csv";
    private static BufferedReader reader;

    @BeforeEach
    void init() {
        setManager(new FileBackedTaskManager(file));
        try {
            reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void test1_shouldWriteAndReadEmptyTaskManager() throws IOException {
        Task task = new Task("a", "a");
        getManager().createTask(task);
        getManager().removeAllTasks();
        StringBuilder builder = new StringBuilder();
        while (reader.ready()) {
            builder.append(reader.readLine());
        }
        String fileContent = builder.toString().trim();
        assertEquals(FILE_HEADER, fileContent, "Пустой список задач записывается не верно");
    }

    @Test
    void test2_shouldWriteEpicWithoutSubtasks() throws IOException {
        EpicTask epic = new EpicTask("a", "a");
        getManager().createTask(epic);
        reader.readLine();
        String epicFromFile = reader.readLine();
        StringBuilder builder = new StringBuilder();
        builder.append(epic.getID());
        builder.append(",");
        builder.append(epic.getType());
        builder.append(",");
        builder.append(epic.getName());
        builder.append(",");
        builder.append(epic.getStatus());
        builder.append(",");
        builder.append(epic.getDescription());
        assertEquals(builder.toString(), epicFromFile, "Эпик без подзадач записывается не верно");
    }

    @Test
    void test3_shouldWriteEmptyHistory() throws IOException {
        Task task = new Task("a", "a");
        getManager().createTask(task);
        getManager().removeAllTasks();
        String header = reader.readLine();
        String history = reader.readLine();
        assertTrue(history.isBlank(), "История записывается не верно");
        assertFalse(reader.ready());
    }

    @Test
    void test4_shouldWriteAndCreateTaskTimeInAFile() throws IOException {
        Task task = new Task("a", "a", TEN_O_CLOCK, Duration.ofHours(1));
        getManager().createTask(task);
        setManager(FileBackedTaskManager.loadFromFile(new File(file)));
        Task taskFromFile = getManager().getTask(1);
        assertEquals(TEN_O_CLOCK, taskFromFile.getStartTime(), "Время старта записано/загружено не верно");
        assertEquals(TEN_O_CLOCK.plusHours(1), taskFromFile.getEndTime(), "Длительность записана/загружена " +
                "не верно");
    }
}