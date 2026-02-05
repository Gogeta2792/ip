package spot;

import java.util.List;
import java.util.Scanner;
import spot.command.CommandType;
import spot.command.ParsedCommand;
import spot.command.Parser;
import spot.storage.Storage;
import spot.task.Task;
import spot.task.TaskList;
import spot.ui.Ui;

public class Spot {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    public Spot(String filePath) {
        ui = new Ui(new Scanner(System.in));
        storage = new Storage(filePath);
        List<Task> loaded = storage.load();
        tasks = new TaskList(loaded);
    }

    public static void main(String[] args) {
        new Spot("data/spot.txt").run();
    }

    public void run() {
        ui.showWelcome();
        try {
            runCommandLoop();
        } finally {
            ui.close();
        }
        ui.showFarewell();
    }

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
            case ON:
                handleOn(parsedCommand);
                break;
            case UNKNOWN:
                ui.showFramedMessage("Spot: I don't know what you mean :( Type \"help\" to view a list of functions.");
                break;
            default:
                break;
            }
        }
    }

    private void handleOn(ParsedCommand parsedCommand) {
        String dateArg = parsedCommand.argument() == null ? "" : parsedCommand.argument();
        var queriedDate = Parser.parseDate(dateArg);
        if (queriedDate == null) {
            ui.showFramedMessage("Spot: I couldn't understand that date. Use yyyy-mm-dd or d/M/yyyy (e.g. 2019-12-02 or 2/12/2019).");
            return;
        }

        ui.showDeadlinesOn(tasks.getDeadlinesOn(queriedDate), queriedDate);
    }

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
