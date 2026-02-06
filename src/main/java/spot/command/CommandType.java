package spot.command;

/**
 * Supported command types for Spot (list, find, mark, unmark, delete, todo, deadline, event, on, bye, help, cheer).
 */
public enum CommandType {
    LIST,
    CHEER,
    FIND,
    MARK,
    UNMARK,
    DELETE,
    TODO,
    DEADLINE,
    EVENT,
    ADD,
    ON,
    BYE,
    HELP,
    UNKNOWN
}
