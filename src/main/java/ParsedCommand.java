public class ParsedCommand {
    private final CommandType type;
    private final String argument;

    public ParsedCommand(CommandType type, String argument) {
        this.type = type;
        this.argument = argument;
    }

    public CommandType type() {
        return type;
    }

    public String argument() {
        return argument;
    }
}

