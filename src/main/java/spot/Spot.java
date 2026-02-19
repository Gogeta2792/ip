package spot;

import java.time.LocalDate;
import java.util.List;
import java.util.OptionalInt;
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
    private static final String DEFAULT_FILE_PATH = "data/spot.txt";

    private final Storage storage;
    private final TaskList tasks;
    private Ui ui;

    /**
     * No-argument constructor for use by JavaFX GUI (uses default data file path).
     */
    public Spot() {
        this(DEFAULT_FILE_PATH);
    }

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

            if (runOneCommand(trimmedInput)) {
                return;
            }
        }
    }

    /**
     * Processes a single command and writes the response to the current UI.
     *
     * @param trimmedInput non-empty trimmed user input
     * @return true if the command was "bye" (caller should exit), false otherwise
     */
    public boolean runOneCommand(String trimmedInput) {
        assert trimmedInput != null && !trimmedInput.isEmpty() : "runOneCommand expects non-empty trimmed input";
        ParsedCommand parsedCommand = Parser.parse(trimmedInput);

        switch (parsedCommand.type()) {
        case BYE:
            return true;
        case LIST:
            ui.showList(tasks);
            break;
        case FIND:
            handleFind(parsedCommand);
            break;
        case MARK:
        case UNMARK:
            handleMark(parsedCommand);
            break;
        case DELETE:
            handleDelete(parsedCommand);
            break;
        case TODO:
        case DEADLINE:
        case EVENT:
        case ADD:
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
                    "I don't know what you mean :( Type \"help\" to view a list of functions.");
            break;
        default:
            break;
        }
        return false;
    }

    /**
     * Returns the welcome message (for GUI startup).
     */
    public String getWelcomeMessage() {
        StringBuilder sb = new StringBuilder();
        Ui welcomeUi = new Ui(new Scanner(""), sb);
        welcomeUi.showWelcome();
        return sb.toString().trim();
    }

    /**
     * Generates a response for the given user input (for GUI). Runs one command and returns
     * all output that would have been shown to the user as a single string.
     *
     * @param input the user's command (e.g. "list", "todo buy milk")
     * @return the response text to display, or empty string if input is blank
     */
    public String getResponse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Ui responseUi = new Ui(new Scanner(""), sb);
        Ui prev = ui;
        ui = responseUi;
        try {
            boolean exit = runOneCommand(input.trim());
            if (exit) {
                responseUi.showFarewell();
            }
            return sb.toString().trim();
        } finally {
            ui = prev;
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
            assert !quotes.isEmpty() : "random index only used when quotes is non-empty";
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
                    "I couldn't understand that date. Use yyyy-mm-dd or d/M/yyyy "
                            + "(e.g. 2019-12-02 or 2/12/2019).");
            return;
        }

        ui.showDeadlinesOn(tasks.getDeadlinesOn(queriedDate), queriedDate);
    }

    /**
     * Parses the command argument as a 1-based task index and validates it against the task list.
     * Shows an error message and returns empty if the argument is not a number or out of range.
     *
     * @param argument the raw argument (e.g. "1" or "2")
     * @return the 0-based task index, or empty if invalid
     */
    private OptionalInt parseAndValidateTaskIndex(String argument) {
        String arg = argument == null ? "" : argument.trim();
        int oneBased;
        try {
            oneBased = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            ui.showFramedMessage("You have to give me the task number!");
            return OptionalInt.empty();
        }
        int index = oneBased - 1;
        if (index < 0 || index >= tasks.size()) {
            ui.showFramedMessage("That task doesn't exist!");
            return OptionalInt.empty();
        }
        return OptionalInt.of(index);
    }

    /**
     * Handles mark or unmark: sets the task at the given 1-based index and persists.
     *
     * @param parsedCommand parsed MARK or UNMARK command with task number
     */
    private void handleMark(ParsedCommand parsedCommand) {
        OptionalInt opt = parseAndValidateTaskIndex(parsedCommand.argument());
        if (opt.isEmpty()) {
            return;
        }
        int taskIndex = opt.getAsInt();
        boolean markAsDone = parsedCommand.type() == CommandType.MARK;
        assert taskIndex >= 0 && taskIndex < tasks.size() : "task index must be valid after range check";

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
        OptionalInt opt = parseAndValidateTaskIndex(parsedCommand.argument());
        if (opt.isEmpty()) {
            return;
        }
        int taskIndex = opt.getAsInt();
        assert taskIndex >= 0 && taskIndex < tasks.size() : "task index must be valid after range check";

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
                ui.showFramedMessage("You can't todo nothing..");
                return;
            }
        }

        Task newTask = Parser.createTask(parsedCommand);
        if (newTask == null) {
            String errorMsg = Parser.getAddTaskErrorMessage(parsedCommand.type());
            ui.showFramedMessage(errorMsg);
            return;
        }
        assert newTask != null : "newTask must be non-null when adding after createTask success";

        tasks.add(newTask);
        ui.showTaskAdded(newTask, tasks.size());
        storage.save(tasks);
    }
}
