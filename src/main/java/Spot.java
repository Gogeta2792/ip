import java.util.Scanner;

public class Spot {
    public static void main(String[] args) {
        String line = "____________________________________________________________";
        int lineWidth = line.length();
        String alignFormat = "%" + lineWidth + "s";
        String logo =
                  "  ____    ____    ____   _____ \n"
                + " / ___|  |  _ \\  / _  \\ |_   _|\n"
                + " \\___ \\  | |_) || | | |   | |  \n"
                + "  ___) | |  __/ | |_| |   | |  \n"
                + " |____/  |_|     \\___/    |_|  \n";

        //Spot Intro
        System.out.println(line);
        System.out.println("Hello! I'm \n" + logo + "\nWhat tasks do you have today?");
        System.out.println(line + "\n");

        Scanner scanner = new Scanner(System.in);
        String userInput;

        //Assume no more than 100 tasks
        //Edge case: If more than 100 tasks are added, extra tasks are silently ignored.
        String[] tasks = new String[100];
        boolean[] isDone = new boolean[100];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            userInput = scanner.nextLine();
            String trimmed = userInput.trim();

            if (trimmed.equalsIgnoreCase("bye")) {
                break;
            }

            if (trimmed.isEmpty()) {
                continue;
            }

            if (trimmed.equalsIgnoreCase("list")) {
                System.out.println(line + "\n");
                System.out.println(String.format(alignFormat, "Spot: Here are your tasks"));

                for (int i = 0; i < taskCount; i++) {
                    String statusIcon = isDone[i] ? "[X]" : "[ ]";
                    System.out.println(String.format(alignFormat, (i + 1) + "." + statusIcon + " " + tasks[i]));
                }
                
                System.out.println("\n" + line + "\n");
                continue;
            }

            String[] parts = trimmed.split("\\s+", 2);
            String command = parts[0].toLowerCase();

            if (command.equals("mark") || command.equals("unmark")) {
                int oneBasedIndex;
                try {
                    //Edge case: "mark"/"unmark" without a valid numeric index (e.g. "mark two")
                    //falls into this NumberFormatException and is treated as a non-existent task.
                    oneBasedIndex = Integer.parseInt(parts.length > 1 ? parts[1].trim() : "");
                } catch (NumberFormatException e) {
                    String msg = "Spot: That task doesn't exist!";
                    System.out.println(line + "\n\n" + String.format(alignFormat, msg) + "\n" + line + "\n");
                    continue;
                }

                int idx = oneBasedIndex - 1;
                //Edge case: Valid number but out of range (e.g. "mark 0" or "mark 999")
                //is handled here as a non-existent task.
                if (idx < 0 || idx >= taskCount) {
                    String msg = "Spot: That task doesn't exist!";
                    System.out.println(line + "\n\n" + String.format(alignFormat, msg) + "\n" + line + "\n");
                    continue;
                }

                boolean markAsDone = command.equals("mark");
                isDone[idx] = markAsDone;
                String statusIcon = isDone[idx] ? "[X]" : "[ ]";

                if (markAsDone) {
                    System.out.println(line + "\n\n"
                            + String.format(alignFormat, "Spot: Nice! I've marked this task as done:")
                            + "\n"
                            + String.format(alignFormat, statusIcon + " " + tasks[idx])
                            + "\n"
                            + line + "\n");
                } else {
                    System.out.println(line + "\n\n"
                            + String.format(alignFormat, "Spot: I've unmarked the task:")
                            + "\n"
                            + String.format(alignFormat, statusIcon + " " + tasks[idx])
                            + "\n"
                            + line + "\n");
                }
                continue;
            }

            if (taskCount < tasks.length) {
                tasks[taskCount] = trimmed;
                taskCount++;
            }

            String spotMsg = "Spot: I've added: " + trimmed;
            String rightAlignedMsg = String.format(alignFormat, spotMsg);
            System.out.println(line + "\n" + "\n" + rightAlignedMsg + "\n" + line + "\n");
        }

        scanner.close();

        String farewellMsg = "Spot: Bye. Hope to see you again soon!";
        String rightAlignedFarewell = String.format(alignFormat, farewellMsg);
        System.out.println("\n" + rightAlignedFarewell);
        System.out.println(line);
    }
}
