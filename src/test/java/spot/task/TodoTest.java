package spot.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Todo}.
 */
class TodoTest {

    @Test
    void getTypeIcon_returnsT() {
        Todo todo = new Todo("description");
        assertEquals("[T]", todo.getTypeIcon());
    }

    @Test
    void getDescription_returnsGivenDescription() {
        Todo todo = new Todo("read book");
        assertEquals("read book", todo.getDescription());
    }

    @Test
    void getDisplayString_returnsDescription() {
        Todo todo = new Todo("task");
        assertEquals("task", todo.getDisplayString());
    }

    @Test
    void isDone_initiallyFalse() {
        Todo todo = new Todo("x");
        assertFalse(todo.isDone());
    }

    @Test
    void setDone_changesState() {
        Todo todo = new Todo("x");
        todo.setDone(true);
        assertTrue(todo.isDone());
        todo.setDone(false);
        assertFalse(todo.isDone());
    }
}
