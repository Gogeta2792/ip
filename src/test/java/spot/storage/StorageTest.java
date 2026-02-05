package spot.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spot.task.Deadline;
import spot.task.Event;
import spot.task.Task;
import spot.task.TaskList;
import spot.task.Todo;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Storage}.
 */
class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    void load_fileDoesNotExist_returnsEmptyList() {
        Storage storage = new Storage(tempDir.resolve("nonexistent.txt").toString());
        List<Task> tasks = storage.load();
        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
    }

    @Test
    void load_emptyFile_returnsEmptyList() throws Exception {
        Path file = tempDir.resolve("data.txt");
        java.nio.file.Files.writeString(file, "");
        Storage storage = new Storage(file.toString());
        List<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
    }

    @Test
    void saveAndLoad_todo_roundTrips() throws Exception {
        Path file = tempDir.resolve("data.txt");
        Storage storage = new Storage(file.toString());
        TaskList list = new TaskList();
        Todo todo = new Todo("read book");
        todo.setDone(true);
        list.add(todo);
        storage.save(list);

        List<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        Task t = loaded.get(0);
        assertInstanceOf(Todo.class, t);
        assertEquals("read book", t.getDescription());
        assertTrue(t.isDone());
    }

    @Test
    void saveAndLoad_deadline_roundTrips() throws Exception {
        Path file = tempDir.resolve("data.txt");
        Storage storage = new Storage(file.toString());
        TaskList list = new TaskList();
        Deadline d = new Deadline("submit", LocalDateTime.of(2025, 2, 1, 14, 30));
        d.setDone(true);
        list.add(d);
        storage.save(list);

        List<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        Task t = loaded.get(0);
        assertInstanceOf(Deadline.class, t);
        assertEquals("submit", t.getDescription());
        assertTrue(t.isDone());
        assertEquals(LocalDateTime.of(2025, 2, 1, 14, 30), ((Deadline) t).getBy());
    }

    @Test
    void saveAndLoad_event_roundTrips() throws Exception {
        Path file = tempDir.resolve("data.txt");
        Storage storage = new Storage(file.toString());
        TaskList list = new TaskList();
        Event e = new Event("meeting", "Mon 2pm", "3pm");
        e.setDone(true);
        list.add(e);
        storage.save(list);

        List<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        Task t = loaded.get(0);
        assertInstanceOf(Event.class, t);
        assertEquals("meeting", t.getDescription());
        assertTrue(t.isDone());
        assertEquals("Mon 2pm", ((Event) t).getFrom());
        assertEquals("3pm", ((Event) t).getTo());
    }

    @Test
    void load_invalidLine_skipsLine() throws Exception {
        Path file = tempDir.resolve("data.txt");
        java.nio.file.Files.writeString(file, "T | 1 | valid todo\ninvalid\nT | 0 | another\n");
        Storage storage = new Storage(file.toString());
        List<Task> loaded = storage.load();
        assertEquals(2, loaded.size());
        assertEquals("valid todo", loaded.get(0).getDescription());
        assertEquals("another", loaded.get(1).getDescription());
    }

    @Test
    void load_emptyLines_skipped() throws Exception {
        Path file = tempDir.resolve("data.txt");
        java.nio.file.Files.writeString(file, "\n  \nT | 0 | only\n\n");
        Storage storage = new Storage(file.toString());
        List<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("only", loaded.get(0).getDescription());
    }
}
