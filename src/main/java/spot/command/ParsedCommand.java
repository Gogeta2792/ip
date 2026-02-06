package spot.command;

/**
 * Immutable result of parsing a user command: the command type and optional argument string.
 */
public class ParsedCommand {
    private final CommandType type;
    private final String argument;

    /**
     * Creates a parsed command with the given type and argument.
     *
     * @param type    the command type
     * @param argument the argument (e.g. task number, description, date); may be null
     */
    public ParsedCommand(CommandType type, String argument) {
        this.type = type;
        this.argument = argument;
    }

    /**
     * Returns the command type.
     *
     * @return the command type
     */
    public CommandType type() {
        return type;
    }

    /**
     * Returns the argument string, or null if none.
     *
     * @return the argument, or null
     */
    public String argument() {
        return argument;
    }
}
