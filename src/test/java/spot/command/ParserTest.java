package spot.command;

import org.junit.jupiter.api.Test;
import spot.task.Deadline;
import spot.task.Event;
import spot.task.Task;
import spot.task.Todo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Parser}.
 */
class ParserTest {

    // ---- parse() ----

    @Test
    void parse_bye_returnsByeCommand() {
        ParsedCommand cmd = Parser.parse("bye");
        assertEquals(CommandType.BYE, cmd.type());
        assertNull(cmd.argument());
    }

    @Test
    void parse_byeCaseInsensitive_returnsByeCommand() {
        assertEquals(CommandType.BYE, Parser.parse("BYE").type());
        assertEquals(CommandType.BYE, Parser.parse("Bye").type());
    }

    @Test
    void parse_list_returnsListCommand() {
        ParsedCommand cmd = Parser.parse("list");
        assertEquals(CommandType.LIST, cmd.type());
        assertNull(cmd.argument());
    }

    @Test
    void parse_help_returnsHelpCommand() {
        assertEquals(CommandType.HELP, Parser.parse("help").type());
    }

    @Test
    void parse_markWithIndex_returnsMarkWithArgument() {
        ParsedCommand cmd = Parser.parse("mark 1");
        assertEquals(CommandType.MARK, cmd.type());
        assertEquals("1", cmd.argument());
    }

    @Test
    void parse_unmarkWithIndex_returnsUnmarkWithArgument() {
        ParsedCommand cmd = Parser.parse("unmark 2");
        assertEquals(CommandType.UNMARK, cmd.type());
        assertEquals("2", cmd.argument());
    }

    @Test
    void parse_deleteWithIndex_returnsDeleteWithArgument() {
        ParsedCommand cmd = Parser.parse("delete 3");
        assertEquals(CommandType.DELETE, cmd.type());
        assertEquals("3", cmd.argument());
    }

    @Test
    void parse_todoWithDescription_returnsTodoWithArgument() {
        ParsedCommand cmd = Parser.parse("todo read book");
        assertEquals(CommandType.TODO, cmd.type());
        assertEquals("read book", cmd.argument());
    }

    @Test
    void parse_deadlineWithDescriptionAndBy_returnsDeadlineWithArgument() {
        ParsedCommand cmd = Parser.parse("deadline submit report /by 2025-02-01");
        assertEquals(CommandType.DEADLINE, cmd.type());
        assertEquals("submit report /by 2025-02-01", cmd.argument());
    }

    @Test
    void parse_eventWithFromAndTo_returnsEventWithArgument() {
        ParsedCommand cmd = Parser.parse("event meeting /from Mon 2pm /to 3pm");
        assertEquals(CommandType.EVENT, cmd.type());
        assertEquals("meeting /from Mon 2pm /to 3pm", cmd.argument());
    }

    @Test
    void parse_onWithDate_returnsOnWithArgument() {
        ParsedCommand cmd = Parser.parse("on 2025-02-01");
        assertEquals(CommandType.ON, cmd.type());
        assertEquals("2025-02-01", cmd.argument());
    }

    @Test
    void parse_unknownCommand_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, Parser.parse("xyz").type());
        assertEquals(CommandType.UNKNOWN, Parser.parse("").type());
    }

    @Test
    void parse_markWithNoArgument_returnsEmptyArgument() {
        ParsedCommand cmd = Parser.parse("mark");
        assertEquals(CommandType.MARK, cmd.type());
        assertEquals("", cmd.argument());
    }

    // ---- createTask() ----

    @Test
    void createTask_todoValid_returnsTodo() {
        Task task = Parser.createTask(new ParsedCommand(CommandType.TODO, "read book"));
        assertNotNull(task);
        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getDescription());
    }

    @Test
    void createTask_todoEmptyArgument_returnsNull() {
        assertNull(Parser.createTask(new ParsedCommand(CommandType.TODO, "")));
        assertNull(Parser.createTask(new ParsedCommand(CommandType.TODO, null)));
    }

    @Test
    void createTask_deadlineValid_returnsDeadline() {
        Task task = Parser.createTask(new ParsedCommand(CommandType.DEADLINE, "submit /by 2025-02-01"));
        assertNotNull(task);
        assertInstanceOf(Deadline.class, task);
        Deadline d = (Deadline) task;
        assertEquals("submit", task.getDescription());
        assertEquals(LocalDate.of(2025, 2, 1), d.getBy().toLocalDate());
    }

    @Test
    void createTask_deadlineWithTime_returnsDeadlineWithTime() {
        Task task = Parser.createTask(new ParsedCommand(CommandType.DEADLINE, "meeting /by 1/2/2025 1430"));
        assertNotNull(task);
        assertInstanceOf(Deadline.class, task);
        Deadline d = (Deadline) task;
        assertEquals(LocalDateTime.of(2025, 2, 1, 14, 30), d.getBy());
    }

    @Test
    void createTask_deadlineNoBy_returnsNull() {
        assertNull(Parser.createTask(new ParsedCommand(CommandType.DEADLINE, "submit report")));
    }

    @Test
    void createTask_deadlineEmptyDescription_returnsNull() {
        assertNull(Parser.createTask(new ParsedCommand(CommandType.DEADLINE, " /by 2025-02-01")));
    }

    @Test
    void createTask_deadlineEmptyBy_returnsNull() {
        assertNull(Parser.createTask(new ParsedCommand(CommandType.DEADLINE, "submit /by ")));
    }

    @Test
    void createTask_eventValid_returnsEvent() {
        Task task = Parser.createTask(new ParsedCommand(CommandType.EVENT, "team meeting /from Mon 2pm /to 3pm"));
        assertNotNull(task);
        assertInstanceOf(Event.class, task);
        Event e = (Event) task;
        assertEquals("team meeting", task.getDescription());
        assertEquals("Mon 2pm", e.getFrom());
        assertEquals("3pm", e.getTo());
    }

    @Test
    void createTask_eventMissingFrom_returnsNull() {
        assertNull(Parser.createTask(new ParsedCommand(CommandType.EVENT, "meeting /to 3pm")));
    }

    @Test
    void createTask_eventToBeforeFrom_returnsNull() {
        assertNull(Parser.createTask(new ParsedCommand(CommandType.EVENT, "x /to a /from b")));
    }

    @Test
    void createTask_eventEmptyDescription_returnsNull() {
        assertNull(Parser.createTask(new ParsedCommand(CommandType.EVENT, " /from 2pm /to 3pm")));
    }

    @Test
    void createTask_unknownType_returnsNull() {
        assertNull(Parser.createTask(new ParsedCommand(CommandType.LIST, null)));
        assertNull(Parser.createTask(new ParsedCommand(CommandType.UNKNOWN, "x")));
    }

    // ---- parseDate() ----

    @Test
    void parseDate_isoDate_returnsLocalDate() {
        assertEquals(LocalDate.of(2025, 2, 1), Parser.parseDate("2025-02-01"));
    }

    @Test
    void parseDate_slashFormat_returnsLocalDate() {
        assertEquals(LocalDate.of(2025, 2, 1), Parser.parseDate("1/2/2025"));
    }

    @Test
    void parseDate_nullOrBlank_returnsNull() {
        assertNull(Parser.parseDate(null));
        assertNull(Parser.parseDate(""));
        assertNull(Parser.parseDate("   "));
    }

    @Test
    void parseDate_invalidFormat_returnsNull() {
        assertNull(Parser.parseDate("not-a-date"));
        assertNull(Parser.parseDate("32/13/2025"));
    }

    // ---- getAddTaskErrorMessage() ----

    @Test
    void getAddTaskErrorMessage_deadline_returnsDeadlineMessage() {
        String msg = Parser.getAddTaskErrorMessage(CommandType.DEADLINE);
        assertTrue(msg.contains("Deadline"));
        assertTrue(msg.contains("/by"));
    }

    @Test
    void getAddTaskErrorMessage_event_returnsEventMessage() {
        String msg = Parser.getAddTaskErrorMessage(CommandType.EVENT);
        assertTrue(msg.contains("Event"));
        assertTrue(msg.contains("/from"));
        assertTrue(msg.contains("/to"));
    }

    @Test
    void getAddTaskErrorMessage_other_returnsDefaultMessage() {
        String msg = Parser.getAddTaskErrorMessage(CommandType.TODO);
        assertTrue(msg.contains("details"));
    }
}
