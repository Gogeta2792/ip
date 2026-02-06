package spot.task;

import java.time.LocalDateTime;
import java.time.LocalTime;
import spot.util.DateTimeFormats;

/**
 * A task with a due date/time (e.g. "submit report by 2025-02-01").
 */
public class Deadline extends Task {
    private final LocalDateTime by;

    /**
     * Creates a deadline with the given description and due date-time.
     *
     * @param description the task description
     * @param by          the due date and time
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public String getTypeIcon() {
        return "[D]";
    }

    @Override
    public String getDisplayString() {
        String datePart = by.format(DateTimeFormats.DISPLAY_DATE);
        if (by.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return getDescription() + " (by: " + datePart + ")";
        }
        return getDescription() + " (by: " + datePart + " " + by.format(DateTimeFormats.DISPLAY_TIME) + ")";
    }

    /**
     * Returns the due date-time for this deadline.
     *
     * @return the due date-time
     */
    public LocalDateTime getBy() {
        return by;
    }
}
