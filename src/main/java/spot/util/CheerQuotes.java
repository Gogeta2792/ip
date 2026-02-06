package spot.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Loads motivational quotes from a plain text file (one quote per line).
 * Used by the cheer command to display a random quote.
 */
public final class CheerQuotes {

    private CheerQuotes() {
    }

    /**
     * Loads non-empty trimmed lines from the given file path.
     *
     * @param filePath path to the quotes file (e.g. "data/cheer.txt")
     * @return list of quote lines; empty if the file is missing or unreadable
     */
    public static List<String> load(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.isRegularFile(path)) {
            return new ArrayList<>();
        }
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8)
                    .stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toList());
        } catch (IOException ignored) {
            return new ArrayList<>();
        }
    }
}
