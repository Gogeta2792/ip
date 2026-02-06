package spot.task;

/**
 * A simple todo task with only a description (no date or time).
 */
public class Todo extends Task {
    /**
     * Creates a todo with the given description.
     *
     * @param description the todo text
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String getTypeIcon() {
        return "[T]";
    }
}
