package spot.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Event}.
 */
class EventTest {

    @Test
    void getTypeIcon_returnsE() {
        Event e = new Event("meeting", "Mon 2pm", "3pm");
        assertEquals("[E]", e.getTypeIcon());
    }

    @Test
    void getFrom_getTo_returnGivenValues() {
        Event e = new Event("meeting", "Mon 2pm", "3pm");
        assertEquals("Mon 2pm", e.getFrom());
        assertEquals("3pm", e.getTo());
    }

    @Test
    void getDisplayString_includesDescriptionFromTo() {
        Event e = new Event("team meeting", "Mon 2pm", "3pm");
        String display = e.getDisplayString();
        assertTrue(display.contains("team meeting"));
        assertTrue(display.contains("from: Mon 2pm"));
        assertTrue(display.contains("to: 3pm"));
    }

    @Test
    void getDescription_returnsGivenDescription() {
        Event e = new Event("description", "a", "b");
        assertEquals("description", e.getDescription());
    }

    @Test
    void isDone_setDone_persists() {
        Event e = new Event("x", "a", "b");
        e.setDone(true);
        assertTrue(e.isDone());
    }
}
