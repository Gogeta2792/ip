import java.time.LocalDateTime;
import java.time.LocalTime;

public class Deadline extends Task {
    private final LocalDateTime by;

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

    public LocalDateTime getBy() {
        return by;
    }
}

