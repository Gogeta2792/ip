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
    private static final String CMD_HELP = "help";

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
        List<Task> tasks = new ArrayList<>();

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
                case TODO:
                case DEADLINE:
                case EVENT:
                case ADD:
                    handleAddTaskCommand(tasks, parsedCommand, borderLine, rightAlignFormat);
                    break;
                case HELP:
                    handleHelpCommand(borderLine, rightAlignFormat);
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

        if (lowerCommand.equals(CMD_MARK) || lowerCommand.equals(CMD_UNMARK)) {
            String argument = parts.length > 1 ? parts[1].trim() : "";
            CommandType type = lowerCommand.equals(CMD_MARK) ? CommandType.MARK : CommandType.UNMARK;
            return new ParsedCommand(type, argument);
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
        System.out.println(String.format(rightAlignFormat, "Spot: Here are your tasks, good luck!"));

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String statusIcon = task.isDone() ? STATUS_DONE_ICON : STATUS_NOT_DONE_ICON;
            String taskLine = (i + 1) + "." + task.getTypeIcon() + statusIcon + " " + task.getDisplayString();
            System.out.println(String.format(rightAlignFormat, taskLine));
        }

        System.out.println("\n" + borderLine + "\n");
    }

    // Help command – shows list of available functions
    private static void handleHelpCommand(String borderLine, String rightAlignFormat) {
        String[] lines = {
            "Spot: Here are the commands I understand:",
            "",
            "  list                    – show all tasks",
            "  todo <description>      – add a todo task",
            "  deadline <desc> /by <date>  – add a deadline",
            "  event <desc> /from <start> /to <end>  – add an event",
            "  mark <number>           – mark a task as done",
            "  unmark <number>         – mark a task as not done",
            "  help                    – show this list",
            "  bye                     – exit (See you later!)"
        };
        System.out.println(borderLine + "\n");
        for (String line : lines) {
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
                String by = argument.substring(byIndex + 5).trim();
                return description.isEmpty() || by.isEmpty() ? null : new Deadline(description, by);
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
        TODO,
        DEADLINE,
        EVENT,
        ADD,
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
        private final String by;

        private Deadline(String description, String by) {
            super(description);
            this.by = by;
        }

        @Override
        String getTypeIcon() {
            return "[D]";
        }

        @Override
        String getDisplayString() {
            return getDescription() + " (by: " + by + ")";
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
    }
}
