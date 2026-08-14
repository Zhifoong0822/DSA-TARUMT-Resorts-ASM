package utility;

import java.util.Scanner;

/**
 * Reusable console-input methods for the system's menu interfaces.
 */
public final class InputHelper {

    private InputHelper() {
        // Utility class
    }

    public static String readNonBlankLine(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (ValidationHelper.isNonBlank(input)) {
                return input;
            }

            System.out.println("Input cannot be blank.");
        }
    }

    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    public static int readIntInRange(
            Scanner scanner,
            String prompt,
            int minimum,
            int maximum) {

        while (true) {
            int value = readInt(scanner, prompt);

            if (value >= minimum && value <= maximum) {
                return value;
            }

            System.out.println(
                    "Please enter a number from " + minimum + " to " + maximum + "."
            );
        }
    }

    public static double readNonNegativeDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                double value = Double.parseDouble(input);

                if (ValidationHelper.isNonNegative(value)) {
                    return value;
                }
            } catch (NumberFormatException exception) {
                // Display the shared validation message below.
            }

            System.out.println("Please enter a non-negative amount.");
        }
    }
}
