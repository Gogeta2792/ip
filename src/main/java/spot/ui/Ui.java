package spot.ui;

import java.time.LocalDate;
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

    private static final String LOGO =
            "  ____    ____    ____   _____ \n"
                    + " / ___|  |  _ \\  / _  \\ |_   _|\n"
                    + " \\___ \\  | |_) || | | |   | |  \n"
                    + "  ___) | |  __/ | |_| |   | |  \n"
                    + " |____/  |_|     \\___/    |_|  \n";

    private static final String SPOT_ASCII =
            "  __      _\n"
                    + "o'')}____//\n"
                    + " `_/      )\n"
                    + " (_(_/-(_/\n";

    private final String borderLine;
    private final String rightAlignFormat;
    private final Scanner scanner;

    /**
     * Creates a UI that reads from the given scanner and uses a fixed line width for framing.
     *
     * @param scanner input source for user commands
     */
    public Ui(Scanner scanner) {
        this.borderLine = HORIZONTAL_LINE;
        int lineWidth = borderLine.length();
        this.rightAlignFormat = "%" + lineWidth + "s";
        this.scanner = scanner;
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
        System.out.println(borderLine);
        System.out.println("Hello! I'm \n" + LOGO + SPOT_ASCII + "\nWhat tasks do you have today?");
        System.out.println(borderLine + "\n");
    }

    /** Prints the farewell message and border. */
    public void showFarewell() {
        String farewellMsg = "Spot: Bye. Hope to see you again soon!";
        String rightAlignedFarewell = String.format(rightAlignFormat, farewellMsg);
        System.out.println("\n" + rightAlignedFarewell);
        System.out.println(borderLine);
    }

    /**
     * Prints the full task list (or an empty-list message) inside borders.
     *
     * @param tasks the task list to display
     */
    public void showList(TaskList tasks) {
        System.out.println(borderLine + "\n");
        if (tasks.isEmpty()) {
            System.out.println(String.format(rightAlignFormat, "Spot: Your list is empty. Add a task to get started!"));
        } else {
            System.out.println(String.format(rightAlignFormat, "Spot: Here are your tasks, good luck!"));
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                String taskLine = (i + 1) + "." + formatTask(task);
                System.out.println(String.format(rightAlignFormat, taskLine));
            }
        }
        System.out.println("\n" + borderLine + "\n");
    }

    /**
     * Prints the tasks that match the search keyword (or a "no matching tasks" message).
     *
     * @param matching list of tasks whose description matches the keyword
     */
    public void showMatchingTasks(List<Task> matching) {
        System.out.println(borderLine + "\n");
        if (matching.isEmpty()) {
            System.out.println(String.format(rightAlignFormat, "Spot: No matching tasks in your list."));
        } else {
            System.out.println(String.format(rightAlignFormat, "Here are the matching tasks in your list:"));
            System.out.println();
            for (int i = 0; i < matching.size(); i++) {
                Task task = matching.get(i);
                String taskLine = (i + 1) + "." + formatTask(task);
                System.out.println(String.format(rightAlignFormat, taskLine));
            }
        }
        System.out.println("\n" + borderLine + "\n");
    }

    /**
     * Prints the deadlines that fall on the given date (or a "no deadlines" message).
     *
     * @param tasksOnDate  list of deadline tasks on that date
     * @param queriedDate  the date that was queried (for display)
     */
    public void showDeadlinesOn(List<Task> tasksOnDate, LocalDate queriedDate) {
        System.out.println(borderLine + "\n");
        if (tasksOnDate.isEmpty()) {
            System.out.println(String.format(rightAlignFormat,
                    "Spot: No deadlines on " + queriedDate.format(DateTimeFormats.DISPLAY_DATE) + "."));
        } else {
            System.out.println(String.format(rightAlignFormat,
                    "Spot: Deadlines on " + queriedDate.format(DateTimeFormats.DISPLAY_DATE) + ":"));
            for (int i = 0; i < tasksOnDate.size(); i++) {
                Task task = tasksOnDate.get(i);
                String taskLine = (i + 1) + "." + formatTask(task);
                System.out.println(String.format(rightAlignFormat, taskLine));
            }
        }
        System.out.println("\n" + borderLine + "\n");
    }

    /** Prints the help text listing all supported commands. */
    public void showHelp() {
        int lineWidth = borderLine.length();
        int cmdWidth = 36;
        int descWidth = lineWidth - cmdWidth - 2;
        String rowFormat = "  %-" + cmdWidth + "s  %-" + descWidth + "s";

        String[][] commands = {
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

        System.out.println(borderLine + "\n");
        System.out.println(String.format(rightAlignFormat, "Spot: Here are the commands I understand:"));
        System.out.println(String.format(rightAlignFormat, ""));
        for (String[] cmd : commands) {
            String line = String.format(rowFormat, cmd[0], cmd[1]);
            System.out.println(String.format(rightAlignFormat, line));
        }
        System.out.println("\n" + borderLine + "\n");
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
                "Spot: Nice! I've marked this task as done:",
                formatTask(task)
        );
    }

    /** Prints confirmation that a task was unmarked. */
    public void showTaskUnmarked(Task task) {
        printFramedTwoLineMessage(
                "Spot: I've unmarked the task:",
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
        String coloredQuote = ANSI_CYAN + quote + ANSI_RESET;
        System.out.println(
                borderLine + "\n\n" + String.format(rightAlignFormat, coloredQuote) + "\n" + borderLine + "\n");
    }

    /** Builds a single-line display string for a task (e.g. "[T][X] buy milk"). */
    private String formatTask(Task task) {
        String statusIcon = task.isDone() ? STATUS_DONE_ICON : STATUS_NOT_DONE_ICON;
        return task.getTypeIcon() + statusIcon + " " + task.getDisplayString();
    }

    /** Prints one message line between top and bottom borders. */
    private void printFramedMessage(String message) {
        System.out.println(
                borderLine + "\n\n" + String.format(rightAlignFormat, message) + "\n" + borderLine + "\n");
    }

    /** Prints header and content lines between borders. */
    private void printFramedTwoLineMessage(String header, String content) {
        System.out.println(
                borderLine + "\n\n"
                        + String.format(rightAlignFormat, header) + "\n"
                        + String.format(rightAlignFormat, content) + "\n"
                        + borderLine + "\n"
        );
    }

    /** Prints header, content, and footer lines between borders (e.g. task added/deleted). */
    private void printFramedThreeLineMessage(String header, String content, String footer) {
        System.out.println(
                borderLine + "\n\n"
                        + String.format(rightAlignFormat, "Spot: " + header) + "\n"
                        + String.format(rightAlignFormat, content) + "\n"
                        + String.format(rightAlignFormat, footer) + "\n"
                        + borderLine + "\n"
        );
    }
}
