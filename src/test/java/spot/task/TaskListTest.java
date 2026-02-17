package spot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TaskList}.
 */
class TaskListTest {

    @Test
    void constructor_default_createsEmptyList() {
        TaskList list = new TaskList();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void constructor_withNull_createsEmptyList() {
        TaskList list = new TaskList(null);
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void constructor_withTasks_copiesTasks() {
        Todo t = new Todo("task");
        TaskList list = new TaskList(List.of(t));
        assertEquals(1, list.size());
        assertSame(t, list.get(0));
    }

    @Test
    void add_increasesSize() {
        TaskList list = new TaskList();
        list.add(new Todo("a"));
        assertEquals(1, list.size());
        list.add(new Todo("b"));
        assertEquals(2, list.size());
    }

    @Test
    void get_returnsCorrectTask() {
        Todo t1 = new Todo("first");
        Todo t2 = new Todo("second");
        TaskList list = new TaskList();
        list.add(t1);
        list.add(t2);
        assertSame(t1, list.get(0));
        assertSame(t2, list.get(1));
    }

    @Test
    void remove_returnsAndRemovesTask() {
        Todo t = new Todo("only");
        TaskList list = new TaskList(List.of(t));
        Task removed = list.remove(0);
        assertSame(t, removed);
        assertTrue(list.isEmpty());
    }

    @Test
    void asUnmodifiableList_returnsUnmodifiableView() {
        TaskList list = new TaskList();
        list.add(new Todo("x"));
        List<Task> view = list.asUnmodifiableList();
        assertThrows(UnsupportedOperationException.class, () -> view.add(new Todo("y")));
        assertEquals(1, view.size());
    }

    @Test
    void getDeadlinesOn_emptyList_returnsEmpty() {
        TaskList list = new TaskList();
        List<Task> onDate = list.getDeadlinesOn(LocalDate.of(2025, 2, 1));
        assertTrue(onDate.isEmpty());
    }

    @Test
    void getDeadlinesOn_onlyTodos_returnsEmpty() {
        TaskList list = new TaskList();
        list.add(new Todo("a"));
        list.add(new Todo("b"));
        List<Task> onDate = list.getDeadlinesOn(LocalDate.of(2025, 2, 1));
        assertTrue(onDate.isEmpty());
    }

    @Test
    void getDeadlinesOn_matchingDeadline_returnsIt() {
        LocalDate target = LocalDate.of(2025, 2, 1);
        Deadline d = new Deadline("submit", target.atStartOfDay());
        TaskList list = new TaskList(List.of(d));
        List<Task> onDate = list.getDeadlinesOn(target);
        assertEquals(1, onDate.size());
        assertSame(d, onDate.get(0));
    }

    @Test
    void getDeadlinesOn_nonMatchingDate_returnsEmpty() {
        LocalDate target = LocalDate.of(2025, 2, 1);
        Deadline d = new Deadline("submit", LocalDate.of(2025, 2, 2).atStartOfDay());
        TaskList list = new TaskList(List.of(d));
        List<Task> onDate = list.getDeadlinesOn(target);
        assertTrue(onDate.isEmpty());
    }

    @Test
    void getDeadlinesOn_mixedTasks_returnsOnlyDeadlinesOnDate() {
        LocalDate target = LocalDate.of(2025, 2, 1);
        Todo todo = new Todo("todo");
        Deadline onTarget = new Deadline("on date", target.atStartOfDay());
        Deadline otherDay = new Deadline("other", LocalDate.of(2025, 3, 1).atStartOfDay());
        Event event = new Event("e", "from", "to");

        TaskList list = new TaskList();
        list.add(todo);
        list.add(onTarget);
        list.add(otherDay);
        list.add(event);

        List<Task> onDate = list.getDeadlinesOn(target);
        assertEquals(1, onDate.size());
        assertSame(onTarget, onDate.get(0));
    }

    @Test
    void getDeadlinesOn_multipleDeadlinesSameDay_returnsAll() {
        LocalDate target = LocalDate.of(2025, 2, 1);
        LocalDateTime start = target.atStartOfDay();
        Deadline d1 = new Deadline("first", start);
        Deadline d2 = new Deadline("second", start.plusHours(1));

        TaskList list = new TaskList(List.of(d1, d2));
        List<Task> onDate = list.getDeadlinesOn(target);
        assertEquals(2, onDate.size());
        assertTrue(onDate.contains(d1));
        assertTrue(onDate.contains(d2));
    }

    // ---- findTasks() ----

    /**
     * Positive case: findTasks returns only tasks whose display string contains the keyword,
     * and matching is case-insensitive so "REPORT" finds "submit report".
     */
    @Test
    void findTasks_withMatchingKeyword_returnsMatchingTasks() {
        Todo t1 = new Todo("read book");
        Todo t2 = new Todo("submit report");
        Todo t3 = new Todo("write report");
        Event e = new Event("team meeting", "Mon 2pm", "3pm");
        TaskList list = new TaskList(List.of(t1, t2, t3, e));

        List<Task> report = list.findTasks("report");
        assertEquals(2, report.size());
        assertTrue(report.stream().anyMatch(t -> t.getDescription().equals("submit report")));
        assertTrue(report.stream().anyMatch(t -> t.getDescription().equals("write report")));

        List<Task> meeting = list.findTasks("MEETING");
        assertEquals(1, meeting.size());
        assertEquals("team meeting", meeting.get(0).getDescription());
    }

    /**
     * Negative case: findTasks with null, empty, or non-matching keyword returns an empty list
     * and does not throw.
     */
    @Test
    void findTasks_nullEmptyOrNoMatch_returnsEmptyList() {
        TaskList list = new TaskList(List.of(new Todo("only task")));

        assertTrue(list.findTasks(null).isEmpty());
        assertTrue(list.findTasks("").isEmpty());
        assertTrue(list.findTasks("   ").isEmpty());
        assertTrue(list.findTasks("nonexistent").isEmpty());
    }
}
