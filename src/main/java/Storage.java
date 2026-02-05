import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private static final String STORAGE_DELIMITER = " | ";

    private final Path dataPath;

    public Storage(String filePath) {
        this.dataPath = Paths.get(filePath);
    }

    /**
     * Loads tasks from the data file.
     * Returns an empty list if error like the file does not exist, not a regular file, cannot be read
     */
    public List<Task> load() {
        if (Files.notExists(dataPath) || !Files.isRegularFile(dataPath)) {
            return new ArrayList<>();
        }
        List<Task> tasks = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(dataPath, StandardCharsets.UTF_8);
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
        } catch (IOException ioException) {
            return new ArrayList<>();
        }
        return tasks;
    }

    /**
     * Saves the task list to the data file.
     * Creates the parent directory if it does not exist.
     */
    public void save(TaskList tasks) {
        save(tasks.asUnmodifiableList());
    }

    private void save(List<Task> tasks) {
        try {
            if (dataPath.getParent() != null) {
                Files.createDirectories(dataPath.getParent());
            }
            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(encodeTask(task));
            }
            Files.write(dataPath, lines, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            //silently ignore
        }
    }

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
                Todo todo = new Todo(parts[2].trim());
                todo.setDone(isDone);
                return todo;
            }
            if ("D".equals(type) && parts.length == 4) {
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
                Event event = new Event(parts[2].trim(), parts[3].trim(), parts[4].trim());
                event.setDone(isDone);
                return event;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException | DateTimeParseException parseException) {
            // skip -> corrupted file handling
        }
        return null;
    }

    private static String encodeTask(Task task) {
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

