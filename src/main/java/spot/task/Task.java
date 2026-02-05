package spot.task;

public abstract class Task {
    private final String description;
    private boolean done;

    protected Task(String description) {
        this.description = description;
        this.done = false;
    }

    public abstract String getTypeIcon();

    public String getDescription() {
        return description;
    }

    public String getDisplayString() {
        return getDescription();
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
