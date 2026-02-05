package spot.util;

import java.time.format.DateTimeFormatter;

public class DateTimeFormats {
    public static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM d yyyy");
    public static final DateTimeFormatter DISPLAY_TIME = DateTimeFormatter.ofPattern("h:mm a");

    private DateTimeFormats() {
        // utility class
    }
}
