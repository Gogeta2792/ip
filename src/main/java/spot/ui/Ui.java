package spot.ui;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import spot.task.Task;
import spot.task.TaskList;
import spot.util.DateTimeFormats;

/**
 * Handles all console I/O: reading commands, showing welcome/farewell, lists, and framed messages.
 */
public class Ui {
    private static final String HORIZONTAL_LINE = "____________________________________________________________";
    private static final String STATUS_DONE_ICON = "[X]";
    private static final String STATUS_NOT_DONE_ICON = "[ ]";
    /** ANSI escape for cyan text (e.g. cheer quote). */
    private static final String ANSI_CYAN = "\033[36m";
    /** ANSI escape to reset formatting. */
    private static final String ANSI_RESET = "\033[0m";

    private static final String LOGO = "SPOT the Dog \n";

    /** Help table: command column width for console layout. */
    private static final int HELP_CMD_WIDTH = 36;
    /** Help table: gap between command and description columns. */
    private static final int HELP_COLUMN_GAP = 2;

    private static final String SPOT_ASCII =
            "  __      _\n"
                    + "o'')}____//\n"
                    + " `_/      )\n"
                    + " (_(_/-(_/\n";

    private static final String[][] HELP_COMMANDS = {
        { "list", "show all tasks" },
        { "cheer", "show a random motivational quote" },
        { "find <keyword>", "search tasks by keyword" },
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

    private final String borderLine;
    private final String rightAlignFormat;
    private final Scanner scanner;
    /** When non-null, output is written here instead of System.out (e.g. for GUI). */
    private final Appendable output;

    /**
     * Creates a UI that reads from the given scanner and uses a fixed line width for framing.
     * Output is printed to System.out.
     *
     * @param scanner input source for user commands
     */
    public Ui(Scanner scanner) {
        this(scanner, null);
    }

    /**
     * Creates a UI that reads from the given scanner and optionally writes to an Appendable.
     * When output is null, messages are printed to System.out.
     *
     * @param scanner input source for user commands
     * @param output  optional; when non-null, all showXxx output is appended here (for GUI)
     */
    public Ui(Scanner scanner, Appendable output) {
        assert scanner != null : "scanner must not be null";
        this.borderLine = HORIZONTAL_LINE;
        int lineWidth = borderLine.length();
        assert lineWidth > 0 : "border line width must be positive";
        this.rightAlignFormat = "%" + lineWidth + "s";
        this.scanner = scanner;
        this.output = output;
    }

    /** True when output goes to an Appendable (GUI); use plain formatting without borders. */
    private boolean isGuiMode() {
        return output != null;
    }

    /** Writes a line to the configured output (System.out or Appendable). */
    private void println(String line) {
        try {
            if (output != null) {
                output.append(line).append("\n");
            } else {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Reads the next line of input.
     *
     * @return the next line, or null if no more input (e.g. EOF)
     */
    public String readCommand() {
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine();
    }

    /** Closes the underlying scanner. */
    public void close() {
        scanner.close();
    }

    /** Prints the welcome banner with logo and prompt. */
    public void showWelcome() {
        if (isGuiMode()) {
            println("Hello! I'm");
            println(LOGO + SPOT_ASCII);
            println("What tasks do you have today?");
            return;
        }
        println(borderLine);
        println("Hello! I'm \n" + LOGO + SPOT_ASCII + "\nWhat tasks do you have today?");
        println(borderLine + "\n");
    }

    /** Prints the farewell message and border. */
    public void showFarewell() {
        String farewellMsg = "Bye. Hope to see you again soon!";
        if (isGuiMode()) {
            println(farewellMsg);
            return;
        }
        String rightAlignedFarewell = String.format(rightAlignFormat, "Spot: " + farewellMsg);
        println("\n" + rightAlignedFarewell);
        println(borderLine);
    }

    /**
     * Prints the full task list (or an empty-list message) inside borders.
     *
     * @param tasks the task list to display
     */
    public void showList(TaskList tasks) {
        printTaskListBlock(
                "Your list is empty. Add a task to get started!",
                "Here are your tasks, good luck!",
                tasks.asUnmodifiableList()
        );
    }

    /**
     * Prints the tasks that match the search keyword (or a "no matching tasks" message).
     *
     * @param matching list of tasks whose description matches the keyword
     */
    public void showMatchingTasks(List<Task> matching) {
        printTaskListBlock(
                "No matching tasks in your list.",
                "Here are the matching tasks in your list:",
                matching
        );
    }

    /**
     * Prints the deadlines that fall on the given date (or a "no deadlines" message).
     *
     * @param tasksOnDate  list of deadline tasks on that date
     * @param queriedDate  the date that was queried (for display)
     */
    public void showDeadlinesOn(List<Task> tasksOnDate, LocalDate queriedDate) {
        assert queriedDate != null : "queried date must not be null";
        String dateStr = queriedDate.format(DateTimeFormats.DISPLAY_DATE);
        printTaskListBlock(
                "No deadlines on " + dateStr + ".",
                "Deadlines on " + dateStr + ":",
                tasksOnDate
        );
    }

    /**
     * Prints a block of tasks with an empty-message or header, in GUI or bordered console style.
     *
     * @param emptyMessage  message when the list is empty
     * @param headerMessage message when the list is non-empty
     * @param taskList      tasks to display (numbered from 1)
     */
    private void printTaskListBlock(String emptyMessage, String headerMessage, List<Task> taskList) {
        if (isGuiMode()) {
            if (taskList.isEmpty()) {
                println(emptyMessage);
            } else {
                println(headerMessage);
                for (int i = 0; i < taskList.size(); i++) {
                    println((i + 1) + ". " + formatTask(taskList.get(i)));
                }
            }
            return;
        }
        println(borderLine + "\n");
        if (taskList.isEmpty()) {
            println(String.format(rightAlignFormat, "Spot: " + emptyMessage));
        } else {
            println(String.format(rightAlignFormat, "Spot: " + headerMessage));
            for (int i = 0; i < taskList.size(); i++) {
                String taskLine = (i + 1) + "." + formatTask(taskList.get(i));
                println(String.format(rightAlignFormat, taskLine));
            }
        }
        println("\n" + borderLine + "\n");
    }

    /** Prints the help text listing all supported commands. */
    public void showHelp() {
        if (isGuiMode()) {
            println("Here are the commands I understand:");
            Arrays.stream(HELP_COMMANDS).forEach(cmd -> {
                assert cmd != null && cmd.length == 2 : "each help row must be [command, description]";
                println("  " + cmd[0] + " - " + cmd[1]);
            });
            return;
        }
        int lineWidth = borderLine.length();
        int descWidth = lineWidth - HELP_CMD_WIDTH - HELP_COLUMN_GAP;
        String rowFormat = "  %-" + HELP_CMD_WIDTH + "s  %-" + descWidth + "s";

        println(borderLine + "\n");
        println(String.format(rightAlignFormat, "Spot: Here are the commands I understand:"));
        println(String.format(rightAlignFormat, ""));
        Arrays.stream(HELP_COMMANDS).forEach(cmd -> {
            assert cmd != null && cmd.length == 2 : "each help row must be [command, description]";
            String line = String.format(rowFormat, cmd[0], cmd[1]);
            println(String.format(rightAlignFormat, line));
        });
        println("\n" + borderLine + "\n");
    }

    /**
     * Prints confirmation that a task was added and the new total count.
     *
     * @param newTask    the task that was added
     * @param taskCount  the number of tasks after adding
     */
    public void showTaskAdded(Task newTask, int taskCount) {
        String taskLine = formatTask(newTask);
        String countLine = "Now you have " + taskCount + " task" + (taskCount == 1 ? "" : "s") + " in the list.";
        printFramedThreeLineMessage(
                "Got it. I've added this task:",
                taskLine,
                countLine
        );
    }

    /**
     * Prints confirmation that a task was removed and the remaining count.
     *
     * @param removedTask the task that was removed
     * @param taskCount   the number of tasks after removal
     */
    public void showTaskDeleted(Task removedTask, int taskCount) {
        String taskLine = formatTask(removedTask);
        String countLine = "Now you have " + taskCount + " task" + (taskCount == 1 ? "" : "s") + " in the list.";
        printFramedThreeLineMessage(
                "Noted. I've removed this task:",
                taskLine,
                countLine
        );
    }

    /** Prints confirmation that a task was marked as done. */
    public void showTaskMarked(Task task) {
        printFramedTwoLineMessage(
                "Nice! I've marked this task as done:",
                formatTask(task)
        );
    }

    /** Prints confirmation that a task was unmarked. */
    public void showTaskUnmarked(Task task) {
        printFramedTwoLineMessage(
                "I've unmarked the task:",
                formatTask(task)
        );
    }

    /**
     * Prints a single message inside the standard border (e.g. for errors).
     *
     * @param message the message to display
     */
    public void showFramedMessage(String message) {
        printFramedMessage(message);
    }

    /**
     * Prints a motivational quote inside the standard border, with the quote in cyan for emphasis.
     *
     * @param quote the quote to display (e.g. from cheer command)
     */
    public void showCheer(String quote) {
        if (isGuiMode()) {
            println(quote);
            return;
        }
        String coloredQuote = ANSI_CYAN + quote + ANSI_RESET;
        println(
                borderLine + "\n\n" + String.format(rightAlignFormat, coloredQuote) + "\n" + borderLine + "\n");
    }

    /** Builds a single-line display string for a task (e.g. "[T][X] buy milk"). */
    private String formatTask(Task task) {
        assert task != null : "task to format must not be null";
        String statusIcon = task.isDone() ? STATUS_DONE_ICON : STATUS_NOT_DONE_ICON;
        return task.getTypeIcon() + statusIcon + " " + task.getDisplayString();
    }

    /** Prints one message line between top and bottom borders. */
    private void printFramedMessage(String message) {
        if (isGuiMode()) {
            println(message);
            return;
        }
        println(
                borderLine + "\n\n" + String.format(rightAlignFormat, "Spot: " + message) + "\n" + borderLine + "\n");
    }

    /** Prints header and content lines between borders. */
    private void printFramedTwoLineMessage(String header, String content) {
        if (isGuiMode()) {
            println(header);
            println(content);
            return;
        }
        println(
                borderLine + "\n\n"
                        + String.format(rightAlignFormat, "Spot: " + header) + "\n"
                        + String.format(rightAlignFormat, content) + "\n"
                        + borderLine + "\n"
        );
    }

    /** Prints header, content, and footer lines between borders (e.g. task added/deleted). */
    private void printFramedThreeLineMessage(String header, String content, String footer) {
        if (isGuiMode()) {
            println(header);
            println(content);
            println(footer);
            return;
        }
        println(
                borderLine + "\n\n"
                        + String.format(rightAlignFormat, "Spot: " + header) + "\n"
                        + String.format(rightAlignFormat, content) + "\n"
                        + String.format(rightAlignFormat, footer) + "\n"
                        + borderLine + "\n"
        );
    }
}
