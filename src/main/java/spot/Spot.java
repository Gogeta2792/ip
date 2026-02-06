package spot;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import spot.command.CommandType;
import spot.command.ParsedCommand;
import spot.command.Parser;
import spot.storage.Storage;
import spot.task.Task;
import spot.task.TaskList;
import spot.ui.Ui;
import spot.util.CheerQuotes;

/**
 * Main application class for Spot, a command-line task manager.
 * Coordinates storage, task list, and UI to process user commands.
 */
public class Spot {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates a Spot instance with storage at the given path and initializes from disk.
     *
     * @param filePath path to the task data file (e.g. "data/spot.txt")
     */
    public Spot(String filePath) {
        ui = new Ui(new Scanner(System.in));
        storage = new Storage(filePath);
        List<Task> loaded = storage.load();
        tasks = new TaskList(loaded);
    }

    /**
     * Entry point. Runs Spot with default data file "data/spot.txt".
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        new Spot("data/spot.txt").run();
    }

    /**
     * Runs the main loop: shows welcome, processes commands until bye, then shows farewell.
     */
    public void run() {
        ui.showWelcome();
        try {
            runCommandLoop();
        } finally {
            ui.close();
        }
        ui.showFarewell();
    }

    /**
     * Reads and dispatches user commands until "bye" or end-of-input.
     */
    private void runCommandLoop() {
        while (true) {
            String userInput = ui.readCommand();
            if (userInput == null) {
                return;
            }

            String trimmedInput = userInput.trim();
            if (trimmedInput.isEmpty()) {
                continue;
            }

            ParsedCommand parsedCommand = Parser.parse(trimmedInput);

            switch (parsedCommand.type()) {
            case BYE:
                return;
            case LIST:
                ui.showList(tasks);
                break;
            case FIND:
                handleFind(parsedCommand);
                break;
            case MARK:
            case UNMARK:  // fall through: both use handleMark
                handleMark(parsedCommand);
                break;
            case DELETE:
                handleDelete(parsedCommand);
                break;
            case TODO:
            case DEADLINE:
            case EVENT:
            case ADD:  // fall through: all add-type commands use handleAddTask
                handleAddTask(parsedCommand);
                break;
            case HELP:
                ui.showHelp();
                break;
            case CHEER:
                handleCheer();
                break;
            case ON:
                handleOn(parsedCommand);
                break;
            case UNKNOWN:
                ui.showFramedMessage(
                        "Spot: I don't know what you mean :( Type \"help\" to view a list of functions.");
                break;
            default:
                break;
            }
        }
    }

    /**
     * Handles the "cheer" command: shows a random motivational quote from data/cheer.txt.
     */
    private void handleCheer() {
        List<String> quotes = CheerQuotes.load("data/cheer.txt");
        String quote;
        if (quotes.isEmpty()) {
            quote = "Keep going – even the best programmers started out writing 'Hello World'!";
        } else {
            quote = quotes.get(new Random().nextInt(quotes.size()));
        }
        ui.showCheer(quote);
    }

    /**
     * Handles the "find &lt;keyword&gt;" command: shows tasks whose description contains the keyword.
     *
     * @param parsedCommand parsed FIND command with keyword argument
     */
    private void handleFind(ParsedCommand parsedCommand) {
        String keyword = parsedCommand.argument() == null ? "" : parsedCommand.argument();
        ui.showMatchingTasks(tasks.findTasks(keyword));
    }

    /**
     * Handles the "on &lt;date&gt;" command: shows deadlines falling on the given date.
     *
     * @param parsedCommand parsed ON command with date argument
     */
    private void handleOn(ParsedCommand parsedCommand) {
        String dateArg = parsedCommand.argument() == null ? "" : parsedCommand.argument();
        LocalDate queriedDate = Parser.parseDate(dateArg);
        if (queriedDate == null) {
            ui.showFramedMessage(
                    "Spot: I couldn't understand that date. Use yyyy-mm-dd or d/M/yyyy (e.g. 2019-12-02 or 2/12/2019).");
            return;
        }

        ui.showDeadlinesOn(tasks.getDeadlinesOn(queriedDate), queriedDate);
    }

    /**
     * Handles mark or unmark: sets the task at the given 1-based index and persists.
     *
     * @param parsedCommand parsed MARK or UNMARK command with task number
     */
    private void handleMark(ParsedCommand parsedCommand) {
        boolean markAsDone = parsedCommand.type() == CommandType.MARK;

        int oneBasedIndex;
        try {
            oneBasedIndex = Integer.parseInt(parsedCommand.argument() == null ? "" : parsedCommand.argument());
        } catch (NumberFormatException numberFormatException) {
            ui.showFramedMessage("Spot: You have to give me the task number!");
            return;
        }

        int taskIndex = oneBasedIndex - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            ui.showFramedMessage("Spot: That task doesn't exist!");
            return;
        }

        Task task = tasks.get(taskIndex);
        task.setDone(markAsDone);

        if (markAsDone) {
            ui.showTaskMarked(task);
        } else {
            ui.showTaskUnmarked(task);
        }
        storage.save(tasks);
    }

    /**
     * Handles delete: removes the task at the given 1-based index and persists.
     *
     * @param parsedCommand parsed DELETE command with task number
     */
    private void handleDelete(ParsedCommand parsedCommand) {
        int oneBasedIndex;
        try {
            oneBasedIndex = Integer.parseInt(parsedCommand.argument() == null ? "" : parsedCommand.argument());
        } catch (NumberFormatException numberFormatException) {
            ui.showFramedMessage("Spot: You have to give me the task number!");
            return;
        }

        int taskIndex = oneBasedIndex - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            ui.showFramedMessage("Spot: That task doesn't exist!");
            return;
        }

        Task removed = tasks.remove(taskIndex);
        ui.showTaskDeleted(removed, tasks.size());
        storage.save(tasks);
    }

    /**
     * Handles todo/deadline/event/add: creates a task from the parsed command, adds it, and persists.
     *
     * @param parsedCommand parsed TODO, DEADLINE, EVENT, or ADD command
     */
    private void handleAddTask(ParsedCommand parsedCommand) {
        if (parsedCommand.type() == CommandType.TODO) {
            String arg = parsedCommand.argument() == null ? "" : parsedCommand.argument();
            if (arg.isEmpty()) {
                ui.showFramedMessage("Spot: You can't todo nothing..");
                return;
            }
        }

        Task newTask = Parser.createTask(parsedCommand);
        if (newTask == null) {
            String errorMsg = Parser.getAddTaskErrorMessage(parsedCommand.type());
            ui.showFramedMessage("Spot: " + errorMsg);
            return;
        }

        tasks.add(newTask);
        ui.showTaskAdded(newTask, tasks.size());
        storage.save(tasks);
    }
}
