import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Ui {
    private static final String HORIZONTAL_LINE = "____________________________________________________________";
    private static final String STATUS_DONE_ICON = "[X]";
    private static final String STATUS_NOT_DONE_ICON = "[ ]";

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

    public Ui(Scanner scanner) {
        this.borderLine = HORIZONTAL_LINE;
        int lineWidth = borderLine.length();
        this.rightAlignFormat = "%" + lineWidth + "s";
        this.scanner = scanner;
    }

    public String readCommand() {
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine();
    }

    public void close() {
        scanner.close();
    }

    public void showWelcome() {
        System.out.println(borderLine);
        System.out.println("Hello! I'm \n" + LOGO + SPOT_ASCII + "\nWhat tasks do you have today?");
        System.out.println(borderLine + "\n");
    }

    public void showFarewell() {
        String farewellMsg = "Spot: Bye. Hope to see you again soon!";
        String rightAlignedFarewell = String.format(rightAlignFormat, farewellMsg);
        System.out.println("\n" + rightAlignedFarewell);
        System.out.println(borderLine);
    }

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

    public void showHelp() {
        int lineWidth = borderLine.length();
        int cmdWidth = 36;
        int descWidth = lineWidth - cmdWidth - 2;
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

    public void showTaskAdded(Task newTask, int taskCount) {
        String taskLine = formatTask(newTask);
        String countLine = "Now you have " + taskCount + " task" + (taskCount == 1 ? "" : "s") + " in the list.";
        printFramedThreeLineMessage(
                "Got it. I've added this task:",
                taskLine,
                countLine
        );
    }

    public void showTaskDeleted(Task removedTask, int taskCount) {
        String taskLine = formatTask(removedTask);
        String countLine = "Now you have " + taskCount + " task" + (taskCount == 1 ? "" : "s") + " in the list.";
        printFramedThreeLineMessage(
                "Noted. I've removed this task:",
                taskLine,
                countLine
        );
    }

    public void showTaskMarked(Task task) {
        printFramedTwoLineMessage(
                "Spot: Nice! I've marked this task as done:",
                formatTask(task)
        );
    }

    public void showTaskUnmarked(Task task) {
        printFramedTwoLineMessage(
                "Spot: I've unmarked the task:",
                formatTask(task)
        );
    }

    public void showFramedMessage(String message) {
        printFramedMessage(message);
    }

    private String formatTask(Task task) {
        String statusIcon = task.isDone() ? STATUS_DONE_ICON : STATUS_NOT_DONE_ICON;
        return task.getTypeIcon() + statusIcon + " " + task.getDisplayString();
    }

    private void printFramedMessage(String message) {
        System.out.println(borderLine + "\n\n" + String.format(rightAlignFormat, message) + "\n" + borderLine + "\n");
    }

    private void printFramedTwoLineMessage(String header, String content) {
        System.out.println(
                borderLine + "\n\n"
                        + String.format(rightAlignFormat, header) + "\n"
                        + String.format(rightAlignFormat, content) + "\n"
                        + borderLine + "\n"
        );
    }

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

