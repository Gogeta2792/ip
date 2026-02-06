package spot.util;

import java.time.format.DateTimeFormatter;

/**
 * Shared date/time formatters for user-facing output (e.g. "Feb 6 2025", "2:30 PM").
 */
public final class DateTimeFormats {

    /** Format for date only: "MMM d yyyy". */
    public static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM d yyyy");
    /** Format for time only: "h:mm a". */
    public static final DateTimeFormatter DISPLAY_TIME = DateTimeFormatter.ofPattern("h:mm a");

    private DateTimeFormats() {
        // Utility class; prevent instantiation.
    }
}
