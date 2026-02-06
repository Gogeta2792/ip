package spot.task;

/**
 * Base type for a single task (todo, deadline, or event) with description and done state.
 */
public abstract class Task {
    private final String description;
    private boolean done;

    /**
     * Subclass constructor. Initializes description and sets done to false.
     *
     * @param description task description (non-null)
     */
    protected Task(String description) {
        this.description = description;
        this.done = false;
    }

    /**
     * Returns a short type icon for display (e.g. "[T]", "[D]", "[E]").
     *
     * @return the type icon string
     */
    public abstract String getTypeIcon();

    /**
     * Returns the task description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the string to show in the list (description plus any date/time info).
     *
     * @return the display string
     */
    public String getDisplayString() {
        return getDescription();
    }

    /**
     * Returns whether the task is marked done.
     *
     * @return true if done, false otherwise
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Sets the done state of the task.
     *
     * @param done true to mark done, false to mark not done
     */
    public void setDone(boolean done) {
        this.done = done;
    }
}
