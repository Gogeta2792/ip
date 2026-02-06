package spot.task;

/**
 * A task with a time range (e.g. "team meeting from Mon 2pm to 3pm").
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an event with description and start/end strings.
     *
     * @param description the event description
     * @param from        start time/date string (as entered by user)
     * @param to          end time/date string (as entered by user)
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String getTypeIcon() {
        return "[E]";
    }

    @Override
    public String getDisplayString() {
        return getDescription() + " (from: " + from + " to: " + to + ")";
    }

    /**
     * Returns the start time/date string.
     *
     * @return the start string
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the end time/date string.
     *
     * @return the end string
     */
    public String getTo() {
        return to;
    }
}
