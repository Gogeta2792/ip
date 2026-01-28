import java.util.Scanner;

public class Spot {
    public static void main(String[] args) {
        String line = "____________________________________________________________";
        int lineWidth = line.length();
        String logo =
                  "  ____    ____    ____   _____ \n"
                + " / ___|  |  _ \\  / _  \\ |_   _|\n"
                + " \\___ \\  | |_) || | | |   | |  \n"
                + "  ___) | |  __/ | |_| |   | |  \n"
                + " |____/  |_|     \\___/    |_|  \n";

        System.out.println(line);
        System.out.println("Hello! I'm \n" + logo + "\nWhat can I do for you?");
        System.out.println(line + "\n");

        Scanner scanner = new Scanner(System.in);
        String userInput;

        //Scanner
        while (true) {
            userInput = scanner.nextLine();
            // Right align "Spot: " + userInput to the width of the line
            String spotMsg = "Spot: " + userInput;
            String rightAlignedMsg = String.format("%" + lineWidth + "s", spotMsg);
            System.out.println(line + "\n" + "\n" + rightAlignedMsg + "\n" + line + "\n");

            if (userInput.trim().equalsIgnoreCase("bye")) {
                break;
            }
        }

        scanner.close();

        String farewellMsg = "Spot: Bye. Hope to see you again soon!";
        String rightAlignedFarewell = String.format("%" + lineWidth + "s", farewellMsg);
        System.out.println("\n" + rightAlignedFarewell);
        System.out.println(line);
    }
}
