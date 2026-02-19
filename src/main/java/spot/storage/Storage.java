package spot.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import spot.task.Deadline;
import spot.task.Event;
import spot.task.Task;
import spot.task.TaskList;
import spot.task.Todo;

/**
 * Persists and loads the task list to/from a text file using a pipe-delimited format.
 */
public class Storage {
    private static final String STORAGE_DELIMITER = " | ";
    /** Regex to split storage lines by the delimiter (literal " | "). */
    private static final String STORAGE_DELIMITER_REGEX = Pattern.quote(STORAGE_DELIMITER);

    private final Path dataPath;

    /**
     * Creates storage that uses the given file path for reading and writing tasks.
     *
     * @param filePath path to the data file (e.g. "data/spot.txt")
     */
    public Storage(String filePath) {
        assert filePath != null : "storage file path must not be null";
        this.dataPath = Paths.get(filePath);
    }

    /**
     * Loads tasks from the data file.
     *
     * @return list of tasks; empty if file does not exist, is not a regular file, or cannot be read
     */
    public List<Task> load() {
        if (Files.notExists(dataPath) || !Files.isRegularFile(dataPath)) {
            return List.of();
        }
        try {
            return Files.readAllLines(dataPath, StandardCharsets.UTF_8).stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(Storage::parseTaskLine)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IOException ioException) {
            return List.of();
        }
    }

    /**
     * Saves the task list to the data file.
     * Creates the parent directory if it does not exist.
     *
     * @param tasks the task list to persist
     */
    public void save(TaskList tasks) {
        assert tasks != null : "task list to save must not be null";
        save(tasks.asUnmodifiableList());
    }

    /**
     * Writes the given task list to the data file, creating parent directories if needed.
     *
     * @param tasks the list of tasks to write
     */
    private void save(List<Task> tasks) {
        assert tasks != null : "task list to write must not be null";
        try {
            if (dataPath.getParent() != null) {
                Files.createDirectories(dataPath.getParent());
            }
            List<String> lines = tasks.stream()
                    .map(Storage::encodeTask)
                    .toList();
            Files.write(dataPath, lines, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            // Silently ignore write errors.
        }
    }

    /**
     * Parses a single storage line (T|0|desc or D|0|desc|by or E|0|desc|from|to) into a Task.
     *
     * @param line one line from the data file
     * @return the parsed task, or null if the line is invalid or corrupted
     */
    private static Task parseTaskLine(String line) {
        try {
            String[] parts = line.split(STORAGE_DELIMITER_REGEX, -1);
            if (parts.length < 3) {
                return null;
            }
            String type = parts[0].trim();
            int done = Integer.parseInt(parts[1].trim());
            boolean isDone = (done == 1);

            if ("T".equals(type) && parts.length == 3) {
                assert parts.length >= 3 : "todo line must have at least type, done, description";
                Todo todo = new Todo(parts[2].trim());
                todo.setDone(isDone);
                return todo;
            }
            if ("D".equals(type) && parts.length == 4) {
                assert parts.length >= 4 : "deadline line must have type, done, description, by";
                String byStr = parts[3].trim();
                LocalDateTime by;
                if (byStr.contains("T")) {
                    by = LocalDateTime.parse(byStr);
                } else {
                    by = LocalDate.parse(byStr).atStartOfDay();
                }
                Deadline deadline = new Deadline(parts[2].trim(), by);
                deadline.setDone(isDone);
                return deadline;
            }
            if ("E".equals(type) && parts.length == 5) {
                assert parts.length >= 5 : "event line must have type, done, description, from, to";
                Event event = new Event(parts[2].trim(), parts[3].trim(), parts[4].trim());
                event.setDone(isDone);
                return event;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException | DateTimeParseException parseException) {
            // Corrupted or invalid line; skip.
        }
        return null;
    }

    /**
     * Encodes a single task to a storage line (T|0|desc or D|0|desc|by or E|0|desc|from|to).
     *
     * @param task the task to encode
     * @return the line string, or empty string for unknown task types
     */
    private static String encodeTask(Task task) {
        assert task != null : "task to encode must not be null";
        int done = task.isDone() ? 1 : 0;
        if (task instanceof Todo) {
            return "T" + STORAGE_DELIMITER + done + STORAGE_DELIMITER + task.getDescription();
        }
        if (task instanceof Deadline deadline) {
            String byIso = deadline.getBy().toString();
            return "D" + STORAGE_DELIMITER + done + STORAGE_DELIMITER + task.getDescription()
                    + STORAGE_DELIMITER + byIso;
        }
        if (task instanceof Event event) {
            return "E" + STORAGE_DELIMITER + done + STORAGE_DELIMITER + task.getDescription()
                    + STORAGE_DELIMITER + event.getFrom() + STORAGE_DELIMITER + event.getTo();
        }
        return "";
    }
}
