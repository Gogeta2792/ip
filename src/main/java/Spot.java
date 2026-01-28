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
        System.out.println("Hello! I'm \n" + logo + "\nWhat can I do for you?");
        System.out.println(line + "\n");

        Scanner scanner = new Scanner(System.in);
        String userInput;

        //Assume no more than 100 tasks
        String[] tasks = new String[100];
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
                System.out.println(String.format(alignFormat, "Spot:"));

                for (int i = 0; i < taskCount; i++) {
                    System.out.println(String.format(alignFormat, (i + 1) + ". " + tasks[i]));
                }
                
                System.out.println("\n" + line + "\n");
                continue;
            }

            if (taskCount < tasks.length) {
                tasks[taskCount] = trimmed;
                taskCount++;
            }

            String spotMsg = "Spot: added: " + trimmed;
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
