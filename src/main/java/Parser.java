import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Parser {
    private static final String CMD_LIST = "list";
    private static final String CMD_BYE = "bye";
    private static final String CMD_MARK = "mark";
    private static final String CMD_UNMARK = "unmark";
    private static final String CMD_TODO = "todo";
    private static final String CMD_DEADLINE = "deadline";
    private static final String CMD_EVENT = "event";
    private static final String CMD_DELETE = "delete";
    private static final String CMD_HELP = "help";
    private static final String CMD_ON = "on";

    public static ParsedCommand parse(String trimmedInput) {
        if (trimmedInput.equalsIgnoreCase(CMD_BYE)) {
            return new ParsedCommand(CommandType.BYE, null);
        }

        if (trimmedInput.equalsIgnoreCase(CMD_LIST)) {
            return new ParsedCommand(CommandType.LIST, null);
        }

        if (trimmedInput.equalsIgnoreCase(CMD_HELP)) {
            return new ParsedCommand(CommandType.HELP, null);
        }

        String[] parts = trimmedInput.split("\\s+", 2);
        String rawCommand = parts[0];
        String lowerCommand = rawCommand.toLowerCase();

        if (lowerCommand.equals(CMD_ON)) {
            String argument = parts.length > 1 ? parts[1].trim() : "";
            return new ParsedCommand(CommandType.ON, argument);
        }

        if (lowerCommand.equals(CMD_MARK) || lowerCommand.equals(CMD_UNMARK)) {
            String argument = parts.length > 1 ? parts[1].trim() : "";
            CommandType type = lowerCommand.equals(CMD_MARK) ? CommandType.MARK : CommandType.UNMARK;
            return new ParsedCommand(type, argument);
        }

        if (lowerCommand.equals(CMD_DELETE)) {
            String argument = parts.length > 1 ? parts[1].trim() : "";
            return new ParsedCommand(CommandType.DELETE, argument);
        }

        if (lowerCommand.equals(CMD_TODO)) {
            String argument = parts.length > 1 ? parts[1].trim() : "";
            return new ParsedCommand(CommandType.TODO, argument);
        }

        if (lowerCommand.equals(CMD_DEADLINE)) {
            String argument = parts.length > 1 ? parts[1].trim() : "";
            return new ParsedCommand(CommandType.DEADLINE, argument);
        }

        if (lowerCommand.equals(CMD_EVENT)) {
            String argument = parts.length > 1 ? parts[1].trim() : "";
            return new ParsedCommand(CommandType.EVENT, argument);
        }

        return new ParsedCommand(CommandType.UNKNOWN, null);
    }

    public static String getAddTaskErrorMessage(CommandType type) {
        return switch (type) {
        case DEADLINE -> "Deadline must have a description and /by <date>. Example: deadline submit report /by 2025-02-01";
        case EVENT -> "Event must have a description, /from <start>, and /to <end>. Example: event team meeting /from Mon 2pm /to 3pm";
        default -> "I need more details. Use: deadline <description> /by <date>, or event <description> /from <start> /to <end>";
        };
    }

    public static Task createTask(ParsedCommand parsedCommand) {
        String argument = parsedCommand.argument() == null ? "" : parsedCommand.argument();
        return switch (parsedCommand.type()) {
        case TODO, ADD -> argument.isEmpty() ? null : new Todo(argument);
        case DEADLINE -> {
            int byIndex = argument.indexOf(" /by ");
            if (byIndex < 0) {
                yield null;
            }
            String description = argument.substring(0, byIndex).trim();
            String byStr = argument.substring(byIndex + 5).trim();
            if (description.isEmpty() || byStr.isEmpty()) {
                yield null;
            }
            LocalDateTime by = parseDateTime(byStr);
            yield by == null ? null : new Deadline(description, by);
        }
        case EVENT -> {
            int fromIndex = argument.indexOf(" /from ");
            int toIndex = argument.indexOf(" /to ");
            if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex) {
                yield null;
            }
            String description = argument.substring(0, fromIndex).trim();
            String from = argument.substring(fromIndex + 7, toIndex).trim();
            String to = argument.substring(toIndex + 5).trim();
            yield description.isEmpty() || from.isEmpty() || to.isEmpty() ? null : new Event(description, from, to);
        }
        default -> null;
        };
    }

    public static LocalDate parseDate(String input) {
        LocalDateTime ldt = parseDateTime(input);
        return ldt == null ? null : ldt.toLocalDate();
    }

    private static LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isBlank()) {
            return null;
        }
        String trimmedInput = dateTimeString.trim();

        try {
            LocalDate parsedDate = LocalDate.parse(trimmedInput);
            return parsedDate.atStartOfDay();
        } catch (DateTimeParseException ignored) {
            // try next format
        }

        DateTimeFormatter withTime = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
        try {
            return LocalDateTime.parse(trimmedInput, withTime);
        } catch (DateTimeParseException ignored) {
            // try next format
        }

        DateTimeFormatter dateOnly = DateTimeFormatter.ofPattern("d/M/yyyy");
        try {
            LocalDate parsedDate = LocalDate.parse(trimmedInput, dateOnly);
            return parsedDate.atStartOfDay();
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }
}

