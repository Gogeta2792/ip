package spot.task;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Deadline}.
 */
class DeadlineTest {

    @Test
    void getTypeIcon_returnsD() {
        Deadline d = new Deadline("x", LocalDateTime.now());
        assertEquals("[D]", d.getTypeIcon());
    }

    @Test
    void getBy_returnsGivenDateTime() {
        LocalDateTime by = LocalDateTime.of(2025, 2, 1, 14, 30);
        Deadline d = new Deadline("submit", by);
        assertEquals(by, d.getBy());
    }

    @Test
    void getDisplayString_withTime_includesDateAndTime() {
        LocalDateTime by = LocalDateTime.of(2025, 2, 1, 14, 30);
        Deadline d = new Deadline("submit report", by);
        String display = d.getDisplayString();
        assertTrue(display.contains("submit report"));
        assertTrue(display.contains("by:"));
        assertTrue(display.contains("Feb"));
        assertTrue(display.contains("2025"));
        // Time format is locale-dependent (e.g. "2:30 PM" or "2:30 pm"); just check non-midnight shows time
        assertTrue(display.contains("30") && (display.contains("2") || display.contains("14")));
    }

    @Test
    void getDisplayString_midnight_showsDateOnly() {
        LocalDateTime by = LocalDateTime.of(2025, 2, 1, 0, 0);
        assertTrue(by.toLocalTime().equals(LocalTime.MIDNIGHT));
        Deadline d = new Deadline("due", by);
        String display = d.getDisplayString();
        assertTrue(display.contains("due"));
        assertTrue(display.contains("by:"));
        assertTrue(display.contains("Feb"));
        assertFalse(display.contains("12:00 AM")); // no time when midnight
    }

    @Test
    void isDone_setDone_persists() {
        Deadline d = new Deadline("x", LocalDateTime.now());
        d.setDone(true);
        assertTrue(d.isDone());
    }
}
