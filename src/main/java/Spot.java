import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Spot {

    // UI consts
    private static final String HORIZONTAL_LINE = "____________________________________________________________";
    private static final String STATUS_DONE_ICON = "[X]";
    private static final String STATUS_NOT_DONE_ICON = "[ ]";

    // Command keywords
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

    // Date/time formats
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final DateTimeFormatter DISPLAY_TIME = DateTimeFormatter.ofPattern("h:mm a");

    // Storage
    private static final Path DATA_PATH = Paths.get("data", "spot.txt");
    private static final String STORAGE_DELIMITER = " | ";

    // Fixed logo displayed on startup
    private static final String LOGO =
              "  ____    ____    ____   _____ \n"
            + " / ___|  |  _ \\  / _  \\ |_   _|\n"
            + " \\___ \\  | |_) || | | |   | |  \n"
            + "  ___) | |  __/ | |_| |   | |  \n"
            + " |____/  |_|     \\___/    |_|  \n";

    public static void main(String[] args) {
        String borderLine = HORIZONTAL_LINE;
        int lineWidth = borderLine.length();
        String rightAlignFormat = "%" + lineWidth + "s";

        printWelcomeMessage(borderLine, rightAlignFormat);

        Scanner scanner = new Scanner(System.in);
        try {
            runCommandLoop(scanner, borderLine, rightAlignFormat);
        } finally {
            scanner.close();
        }

        printFarewell(borderLine, rightAlignFormat);
    }

    //Continuously reads user input, parses parsed commands
    private static void runCommandLoop(Scanner scanner, String borderLine, String rightAlignFormat) {
        List<Task> tasks = loadTasks();

        while (scanner.hasNextLine()) {
            String userInput = scanner.nextLine();
            String trimmedInput = userInput.trim();

            if (trimmedInput.isEmpty()) {
                continue;
            }

            ParsedCommand parsedCommand = parseCommand(trimmedInput);

            switch (parsedCommand.type) {
                case BYE:
                    return;
                case LIST:
                    handleListCommand(tasks, borderLine, rightAlignFormat);
                    break;
                case MARK:
                case UNMARK:
                    handleMarkCommand(tasks, parsedCommand, borderLine, rightAlignFormat);
                    break;
                case DELETE:
                    handleDeleteCommand(tasks, parsedCommand, borderLine, rightAlignFormat);
                    break;
                case TODO:
                case DEADLINE:
                case EVENT:
                case ADD:
                    handleAddTaskCommand(tasks, parsedCommand, borderLine, rightAlignFormat);
                    break;
                case HELP:
                    handleHelpCommand(borderLine, rightAlignFormat);
                    break;
                case ON:
                    handleOnCommand(tasks, parsedCommand, borderLine, rightAlignFormat);
                    break;
                case UNKNOWN:
                    printFramedMessage(borderLine, rightAlignFormat,
                            "Spot: I don't know what you mean :( Type \"help\" to view a list of functions.");
                    break;
                default:
                    break;
            }
        }
    }

    // Loads tasks from the data file. Returns an empty list if the file or directory does not exist
    private static List<Task> loadTasks() {
        if (Files.notExists(DATA_PATH) || !Files.isRegularFile(DATA_PATH)) {
            return new ArrayList<>();
        }
        List<Task> tasks = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(DATA_PATH, StandardCharsets.UTF_8);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                Task task = parseTaskLine(trimmed);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }
        return tasks;
    }

    // Parses one line from the data file into a Task
    private static Task parseTaskLine(String line) {
        try {
            String[] parts = line.split(" \\| ", -1);
            if (parts.length < 3) {
                return null;
            }
            String type = parts[0].trim();
            int done = Integer.parseInt(parts[1].trim());
            boolean isDone = (done == 1);

            if ("T".equals(type) && parts.length == 3) {
                Todo t = new Todo(parts[2].trim());
                t.setDone(isDone);
                return t;
            }
            if ("D".equals(type) && parts.length == 4) {
                String byStr = parts[3].trim();
                LocalDateTime by;
                if (byStr.contains("T")) {
                    by = LocalDateTime.parse(byStr);
                } else {
                    by = LocalDate.parse(byStr).atStartOfDay();
                }
                Deadline d = new Deadline(parts[2].trim(), by);
                d.setDone(isDone);
                return d;
            }
            if ("E".equals(type) && parts.length == 5) {
                Event e = new Event(parts[2].trim(), parts[3].trim(), parts[4].trim());
                e.setDone(isDone);
                return e;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException | DateTimeParseException e) {
            // skip - corrupted file handling
        }
        return null;
    }

    //Saves the task list to the data file. Creates the parent directory and file if they do not exist
    private static void saveTasks(List<Task> tasks) {
        try {
            if (DATA_PATH.getParent() != null) {
                Files.createDirectories(DATA_PATH.getParent());
            }
            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(encodeTask(task));
            }
            Files.write(DATA_PATH, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // Silently ignore
        }
    }

    private static String encodeTask(Task task) {
        int done = task.isDone() ? 1 : 0;
        if (task instanceof Todo) {
            return "T" + STORAGE_DELIMITER + done + STORAGE_DELIMITER + task.getDescription();
        }
        if (task instanceof Deadline) {
            String byIso = ((Deadline) task).getBy().toString();
            return "D" + STORAGE_DELIMITER + done + STORAGE_DELIMITER + task.getDescription()
                    + STORAGE_DELIMITER + byIso;
        }
        if (task instanceof Event) {
            Event e = (Event) task;
            return "E" + STORAGE_DELIMITER + done + STORAGE_DELIMITER + task.getDescription()
                    + STORAGE_DELIMITER + e.getFrom() + STORAGE_DELIMITER + e.getTo();
        }
        return "";
    }

    // Parses the command and returns a ParsedCommand object
    private static ParsedCommand parseCommand(String trimmedInput) {
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

        // Unknown command
        return new ParsedCommand(CommandType.UNKNOWN, null);
    }

    //List command
    private static void handleListCommand(List<Task> tasks, String borderLine, String rightAlignFormat) {
        System.out.println(borderLine + "\n");
        if (tasks.isEmpty()) {
            System.out.println(String.format(rightAlignFormat, "Spot: Your list is empty. Add a task to get started!"));
        } else {
            System.out.println(String.format(rightAlignFormat, "Spot: Here are your tasks, good luck!"));
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                String statusIcon = task.isDone() ? STATUS_DONE_ICON : STATUS_NOT_DONE_ICON;
                String taskLine = (i + 1) + "." + task.getTypeIcon() + statusIcon + " " + task.getDisplayString();
                System.out.println(String.format(rightAlignFormat, taskLine));
            }
        }
        System.out.println("\n" + borderLine + "\n");
    }

    // On <date> command – list deadlines (and events if dated) occurring on the given date
    private static void handleOnCommand(List<Task> tasks,
                                        ParsedCommand parsedCommand,
                                        String borderLine,
                                        String rightAlignFormat) {
        String dateArg = parsedCommand.argument == null ? "" : parsedCommand.argument;
        LocalDate queriedDate = parseDate(dateArg);
        if (queriedDate == null) {
            printFramedMessage(borderLine, rightAlignFormat,
                    "Spot: I couldn't understand that date. Use yyyy-mm-dd or d/M/yyyy (e.g. 2019-12-02 or 2/12/2019).");
            return;
        }
        List<Task> onDate = new ArrayList<>();
        for (Task task : tasks) {
            if (task instanceof Deadline) {
                if (((Deadline) task).getBy().toLocalDate().equals(queriedDate)) {
                    onDate.add(task);
                }
            }
        }
        System.out.println(borderLine + "\n");
        if (onDate.isEmpty()) {
            System.out.println(String.format(rightAlignFormat,
                    "Spot: No deadlines on " + queriedDate.format(DISPLAY_DATE) + "."));
        } else {
            System.out.println(String.format(rightAlignFormat,
                    "Spot: Deadlines on " + queriedDate.format(DISPLAY_DATE) + ":"));
            for (int i = 0; i < onDate.size(); i++) {
                Task task = onDate.get(i);
                String statusIcon = task.isDone() ? STATUS_DONE_ICON : STATUS_NOT_DONE_ICON;
                String taskLine = (i + 1) + "." + task.getTypeIcon() + statusIcon + " " + task.getDisplayString();
                System.out.println(String.format(rightAlignFormat, taskLine));
            }
        }
        System.out.println("\n" + borderLine + "\n");
    }

    // Help command – shows list of available functions
    private static void handleHelpCommand(String borderLine, String rightAlignFormat) {
        int lineWidth = borderLine.length();
        int cmdWidth = 36;   // fits longest command
        int descWidth = lineWidth - cmdWidth - 2;  // 2 spaces between columns
        String rowFormat = "  %-" + cmdWidth + "s  %-" + descWidth + "s";

        String[][] commands = {
            { "list", "show all tasks" },
            { "todo <description>", "add a todo task" },
            { "deadline <desc> /by <date>", "add a deadline" },
            { "event <desc> /from <start> /to <end>", "add an event" },
            { "on <date>", "list deadlines on that date" },
            { "mark <number>", "mark a task as done" },
            { "unmark <number>", "mark task as not done" },
            { "delete <number>", "remove a task" },
            { "help", "show this list" },
            { "bye", "exit (See you later!)" }
        };

        System.out.println(borderLine + "\n");
        System.out.println(String.format(rightAlignFormat, "Spot: Here are the commands I understand:"));
        System.out.println(String.format(rightAlignFormat, ""));
        for (String[] cmd : commands) {
            String line = String.format(rowFormat, cmd[0], cmd[1]);
            System.out.println(String.format(rightAlignFormat, line));
        }
        System.out.println("\n" + borderLine + "\n");
    }

    // Mark/unmark commands
    private static void handleMarkCommand(List<Task> tasks,
                                          ParsedCommand parsedCommand,
                                          String borderLine,
                                          String rightAlignFormat) {
        boolean markAsDone = parsedCommand.type == CommandType.MARK;

        int oneBasedIndex;
        try {
            oneBasedIndex = Integer.parseInt(parsedCommand.argument == null ? "" : parsedCommand.argument);
        } catch (NumberFormatException e) {
            printFramedMessage(borderLine, rightAlignFormat,
                    "Spot: You have to give me the task number!");
            return;
        }

        int taskIndex = oneBasedIndex - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            printFramedMessage(borderLine, rightAlignFormat,
                    "Spot: That task doesn't exist!");
            return;
        }

        Task task = tasks.get(taskIndex);
        task.setDone(markAsDone);
        String statusIcon = task.isDone() ? STATUS_DONE_ICON : STATUS_NOT_DONE_ICON;
        String taskLine = task.getTypeIcon() + statusIcon + " " + task.getDisplayString();

        if (markAsDone) {
            printFramedTwoLineMessage(
                    borderLine,
                    rightAlignFormat,
                    "Spot: Nice! I've marked this task as done:",
                    taskLine
            );
        } else {
            printFramedTwoLineMessage(
                    borderLine,
                    rightAlignFormat,
                    "Spot: I've unmarked the task:",
                    taskLine
            );
        }
        saveTasks(tasks);
    }

    // Delete task
    private static void handleDeleteCommand(List<Task> tasks,
                                            ParsedCommand parsedCommand,
                                            String borderLine,
                                            String rightAlignFormat) {
        int oneBasedIndex;
        try {
            oneBasedIndex = Integer.parseInt(parsedCommand.argument == null ? "" : parsedCommand.argument);
        } catch (NumberFormatException e) {
            printFramedMessage(borderLine, rightAlignFormat,
                    "Spot: You have to give me the task number!");
            return;
        }

        int taskIndex = oneBasedIndex - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            printFramedMessage(borderLine, rightAlignFormat,
                    "Spot: That task doesn't exist!");
            return;
        }

        Task removed = tasks.remove(taskIndex);
        String statusIcon = removed.isDone() ? STATUS_DONE_ICON : STATUS_NOT_DONE_ICON;
        String taskLine = removed.getTypeIcon() + statusIcon + " " + removed.getDisplayString();
        String countLine = "Now you have " + tasks.size() + " task" + (tasks.size() == 1 ? "" : "s") + " in the list.";
        printFramedThreeLineMessage(borderLine, rightAlignFormat,
                "Noted. I've removed this task:",
                taskLine,
                countLine);
        saveTasks(tasks);
    }

    // Add task command
    private static void handleAddTaskCommand(List<Task> tasks,
                                             ParsedCommand parsedCommand,
                                             String borderLine,
                                             String rightAlignFormat) {
        // Error empty todo description
        if (parsedCommand.type == CommandType.TODO) {
            String arg = parsedCommand.argument == null ? "" : parsedCommand.argument;
            if (arg.isEmpty()) {
                printFramedMessage(borderLine, rightAlignFormat,
                        "Spot: You can't todo nothing..");
                return;
            }
        }

        Task newTask = createTask(parsedCommand);
        if (newTask == null) {
            String errorMsg = getAddTaskErrorMessage(parsedCommand.type);
            printFramedMessage(borderLine, rightAlignFormat, "Spot: " + errorMsg);
            return;
        }
        tasks.add(newTask);

        String statusIcon = newTask.isDone() ? STATUS_DONE_ICON : STATUS_NOT_DONE_ICON;
        String taskLine = newTask.getTypeIcon() + statusIcon + " " + newTask.getDisplayString();
        String countLine = "Now you have " + tasks.size() + " task" + (tasks.size() == 1 ? "" : "s") + " in the list.";
        printFramedThreeLineMessage(borderLine, rightAlignFormat,
                "Got it. I've added this task:",
                taskLine,
                countLine);
        saveTasks(tasks);
    }

    // Switch case for type error messages
    private static String getAddTaskErrorMessage(CommandType type) {
        switch (type) {
            case DEADLINE:
                return "Deadline must have a description and /by <date>. Example: deadline submit report /by 2025-02-01";
            case EVENT:
                return "Event must have a description, /from <start>, and /to <end>. Example: event team meeting /from Mon 2pm /to 3pm";
            default:
                return "I need more details. Use: deadline <description> /by <date>, or event <description> /from <start> /to <end>";
        }
    }

    //Parses a date/time string into LocalDateTime. Tries yyyy-MM-dd first, then d/M/yyyy HHmm.
    private static LocalDateTime parseDateTime(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        String s = input.trim();
        // yyyy-MM-dd (e.g. 2019-10-15)
        try {
            LocalDate d = LocalDate.parse(s);
            return d.atStartOfDay();
        } catch (DateTimeParseException ignored) {
            // try next format
        }
        // d/M/yyyy HHmm (e.g. 2/12/2019 1800) – 2nd Dec 2019 6pm
        DateTimeFormatter withTime = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
        try {
            return LocalDateTime.parse(s, withTime);
        } catch (DateTimeParseException ignored) {
            // try next format
        }
        // d/M/yyyy (e.g. 2/12/2019) – date only
        DateTimeFormatter dateOnly = DateTimeFormatter.ofPattern("d/M/yyyy");
        try {
            LocalDate d = LocalDate.parse(s, dateOnly);
            return d.atStartOfDay();
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    //Parses a date string (for "on" command) into LocalDate. Reuses parseDateTime and takes date part.
    private static LocalDate parseDate(String input) {
        LocalDateTime ldt = parseDateTime(input);
        return ldt == null ? null : ldt.toLocalDate();
    }

    private static Task createTask(ParsedCommand parsedCommand) {
        String argument = parsedCommand.argument == null ? "" : parsedCommand.argument;
        switch (parsedCommand.type) {
            case TODO:
            case ADD:
                return argument.isEmpty() ? null : new Todo(argument);
            case DEADLINE: {
                int byIndex = argument.indexOf(" /by ");
                if (byIndex < 0) {
                    return null;
                }
                String description = argument.substring(0, byIndex).trim();
                String byStr = argument.substring(byIndex + 5).trim();
                if (description.isEmpty() || byStr.isEmpty()) {
                    return null;
                }
                LocalDateTime by = parseDateTime(byStr);
                return by == null ? null : new Deadline(description, by);
            }
            case EVENT: {
                int fromIndex = argument.indexOf(" /from ");
                int toIndex = argument.indexOf(" /to ");
                if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex) {
                    return null;
                }
                String description = argument.substring(0, fromIndex).trim();
                String from = argument.substring(fromIndex + 7, toIndex).trim();
                String to = argument.substring(toIndex + 5).trim();
                return description.isEmpty() || from.isEmpty() || to.isEmpty() ? null : new Event(description, from, to);
            }
            default:
                return null;
        }
    }

    //Utility method for chatbox "box" formatting
    private static void printFramedMessage(String borderLine, String rightAlignFormat, String message) {
        System.out.println(borderLine + "\n\n" + String.format(rightAlignFormat, message) + "\n" + borderLine + "\n");
    }

    // Prints two-line messages (header[Spot message] + content[Task description]) inside the chatbox "box"
    private static void printFramedTwoLineMessage(String borderLine,
                                                  String rightAlignFormat,
                                                  String header,
                                                  String content) {
        System.out.println(
                borderLine + "\n\n"
                        + String.format(rightAlignFormat, header) + "\n"
                        + String.format(rightAlignFormat, content) + "\n"
                        + borderLine + "\n"
        );
    }

    // Prints three-line messages (header[Spot message] + content[Task description] + footer[Task count]) inside the chatbox "box"
    private static void printFramedThreeLineMessage(String borderLine,
                                                    String rightAlignFormat,
                                                    String header,
                                                    String content,
                                                    String footer) {
        System.out.println(
                borderLine + "\n\n"
                        + String.format(rightAlignFormat, "Spot: " + header) + "\n"
                        + String.format(rightAlignFormat, content) + "\n"
                        + String.format(rightAlignFormat, footer) + "\n"
                        + borderLine + "\n"
        );
    }

    // Spot intro
    private static void printWelcomeMessage(String borderLine, String rightAlignFormat) {
        System.out.println(borderLine);
        System.out.println("Hello! I'm \n" + LOGO + "\nWhat tasks do you have today?");
        System.out.println(borderLine + "\n");
    }

    // Spot goodbye on "bye"
    private static void printFarewell(String borderLine, String rightAlignFormat) {
        String farewellMsg = "Spot: Bye. Hope to see you again soon!";
        String rightAlignedFarewell = String.format(rightAlignFormat, farewellMsg);
        System.out.println("\n" + rightAlignedFarewell);
        System.out.println(borderLine);
    }

    // Easily accessible command types
    private enum CommandType {
        LIST,
        MARK,
        UNMARK,
        DELETE,
        TODO,
        DEADLINE,
        EVENT,
        ADD,
        ON,
        BYE,
        HELP,
        UNKNOWN
    }

    // Simple value object containing a parsed command and its argument
    private static class ParsedCommand {
        private final CommandType type;
        private final String argument;

        private ParsedCommand(CommandType type, String argument) {
            this.type = type;
            this.argument = argument;
        }
    }

    // Task
    private abstract static class Task {
        private final String description;
        private boolean done;

        Task(String description) {
            this.description = description;
            this.done = false;
        }

        abstract String getTypeIcon();

        String getDescription() {
            return description;
        }

        String getDisplayString() {
            return getDescription();
        }

        boolean isDone() {
            return done;
        }

        void setDone(boolean done) {
            this.done = done;
        }
    }

    // Todo
    private static class Todo extends Task {
        private Todo(String description) {
            super(description);
        }

        @Override
        String getTypeIcon() {
            return "[T]";
        }
    }

    // Deadline
    private static class Deadline extends Task {
        private final LocalDateTime by;

        private Deadline(String description, LocalDateTime by) {
            super(description);
            this.by = by;
        }

        @Override
        String getTypeIcon() {
            return "[D]";
        }

        @Override
        String getDisplayString() {
            String datePart = by.format(DISPLAY_DATE);
            if (by.toLocalTime().equals(LocalTime.MIDNIGHT)) {
                return getDescription() + " (by: " + datePart + ")";
            }
            return getDescription() + " (by: " + datePart + " " + by.format(DISPLAY_TIME) + ")";
        }

        LocalDateTime getBy() {
            return by;
        }
    }

    // Event
    private static class Event extends Task {
        private final String from;
        private final String to;

        private Event(String description, String from, String to) {
            super(description);
            this.from = from;
            this.to = to;
        }

        @Override
        String getTypeIcon() {
            return "[E]";
        }

        @Override
        String getDisplayString() {
            return getDescription() + " (from: " + from + " to: " + to + ")";
        }

        String getFrom() {
            return from;
        }

        String getTo() {
            return to;
        }
    }
}
