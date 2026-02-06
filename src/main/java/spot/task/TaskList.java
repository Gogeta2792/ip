package spot.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mutable list of tasks with helpers for storage and date-based queries.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list backed by a copy of the given list (or empty if null).
     *
     * @param tasks initial tasks; may be null
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks == null ? List.of() : tasks);
    }

    /**
     * Appends a task to the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task at the given 0-based index.
     *
     * @param index 0-based index
     * @return the task at that index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the given 0-based index.
     *
     * @param index 0-based index
     * @return the removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks.
     *
     * @return the size of the list
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list has no tasks.
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns an unmodifiable view of the task list (e.g. for storage).
     *
     * @return unmodifiable list of tasks
     */
    public List<Task> asUnmodifiableList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns all tasks whose description (or full display string) contains the given keyword.
     * Matching is case-insensitive.
     *
     * @param keyword the search keyword (non-null; empty matches no tasks)
     * @return list of matching tasks (may be empty)
     */
    public List<Task> findTasks(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return List.of();
        }
        String lowerKeyword = keyword.toLowerCase();
        List<Task> matching = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDisplayString().toLowerCase().contains(lowerKeyword)) {
                matching.add(task);
            }
        }
        return matching;
    }

    /**
     * Returns all deadlines whose due date is on the given date.
     *
     * @param date the date to filter by
     * @return list of matching deadline tasks (may be empty)
     */
    public List<Task> getDeadlinesOn(LocalDate date) {
        List<Task> onDate = new ArrayList<>();
        for (Task task : tasks) {
            if (task instanceof Deadline deadline) {
                if (deadline.getBy().toLocalDate().equals(date)) {
                    onDate.add(task);
                }
            }
        }
        return onDate;
    }
}
