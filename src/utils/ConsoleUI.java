package utils;

/**
 * ConsoleUI - Enhanced visual interface for the console environment
 * 
 * This utility class transforms the basic console into a rich, colorful, and
 * interactive interface for the EL-TA3BAN Pharmacy System. It implements ANSI
 * escape sequences to provide colors, animations, and styled text elements.
 * 
 * OOP Concepts Demonstrated:
 * - Utility Class Pattern: A stateless class with only static methods
 * - Encapsulation: Groups related UI functionality together
 * - Abstraction: Hides the complexity of console formatting behind simple methods
 * 
 * Class Responsibilities:
 * - Create visually appealing UI elements (headers, menus, tables)
 * - Display interactive animations (progress bars, spinners)
 * - Handle user input with formatted prompts
 * - Implement text effects like typing animations
 * 
 * Role in System:
 * This class enhances the user experience throughout the entire application,
 * making the console interface more intuitive and visually engaging. It creates
 * a professional appearance for the pharmacy system with minimal resource usage.
 */
public class ConsoleUI {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void printHeader(String title, int width, String color) {
        String line = "═".repeat(width);
        int padding = (width - title.length()) / 2;
        String paddedTitle = " ".repeat(Math.max(0, padding)) + title + " ".repeat(Math.max(0, padding));
        
        if (paddedTitle.length() < width) {
            paddedTitle += " ";
        }
        
        System.out.println(color + "╔" + line + "╗" + RESET);
        System.out.println(color + "║" + paddedTitle + "║" + RESET);
        System.out.println(color + "╚" + line + "╝" + RESET);
    }

    public static void printMenuItem(int number, String description, String emoji, String color) {
        System.out.println(color + "  [" + number + "] " + emoji + " " + description + RESET);
    }

    public static void printProgressBar(int current, int total, int width, String color) {
        int progress = (int) ((double) current / total * width);
        System.out.print("\r[");
        for (int i = 0; i < width; i++) {
            if (i < progress) {
                System.out.print(color + "■" + RESET);
            } else {
                System.out.print(" ");
            }
        }
        System.out.print("] " + current + "/" + total);
        if (current == total) {
            System.out.println();
        }
    }

    public static void showLoadingSpinner(String message, int seconds, String color) {
        char[] spinChars = {'◴', '◷', '◶', '◵'};
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (seconds * 1000);

        int i = 0;
        while (System.currentTimeMillis() < endTime) {
            System.out.print("\r" + color + spinChars[i % spinChars.length] + " " + message + RESET);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            i++;
        }
        System.out.println();
    }

    /**
     * Print a message with a specific status icon and color
     * 
     * @param message Message to print
     * @param icon Status icon to prefix
     * @param color Color to use
     */
    private static void printStatusMessage(String message, String icon, String color) {
        System.out.println(color + icon + " " + message + RESET);
    }
    
    public static void printSuccess(String message) {
        printStatusMessage(message, "✓", GREEN);
    }

    public static void printError(String message) {
        printStatusMessage(message, "✗", RED);
    }

    public static void printInfo(String message) {
        printStatusMessage(message, "ℹ", BLUE);
    }

    public static void printWarning(String message) {
        printStatusMessage(message, "⚠", YELLOW);
    }

    public static void printLine(int width, String color) {
        System.out.println(color + "─".repeat(width) + RESET);
    }

    public static boolean confirmPrompt(String message) {
        System.out.print(YELLOW + "⚠ " + message + " (y/n): " + RESET);
        String input = System.console().readLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }

    public static void typeText(String message, int delayMs, String color) {
        for (char c : message.toCharArray()) {
            System.out.print(color + c + RESET);
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
    }

    /**
     * Print a table row (used for both header and data rows)
     * 
     * @param data String array of data to print
     * @param widths Array of column widths
     * @param color Color to use
     */
    private static void printTableRowGeneric(String[] data, int[] widths, String color) {
        System.out.print(color);
        for (int i = 0; i < data.length; i++) {
            System.out.print("│ " + padRight(data[i], widths[i]) + " ");
        }
        System.out.println("│" + RESET);
    }
    
    /**
     * Print a table header with column names
     * 
     * @param columns String array of column names
     * @param widths Array of column widths
     * @param color Color to use
     */
    public static void printTableHeader(String[] columns, int[] widths, String color) {
        // Print the header row
        printTableRowGeneric(columns, widths, color);
        
        // Print the separator line
        System.out.print(color + "├");
        for (int width : widths) {
            System.out.print("─".repeat(width + 2) + "┼");
        }
        System.out.print("\b" + "┤" + RESET);
        System.out.println();
    }

    /**
     * Print a table data row
     * 
     * @param data String array of data to print
     * @param widths Array of column widths
     * @param color Color to use
     */
    public static void printTableRow(String[] data, int[] widths, String color) {
        printTableRowGeneric(data, widths, color);
    }

    private static String padRight(String s, int width) {
        return String.format("%-" + width + "s", s);
    }

    public static String promptInput(String prompt, String color) {
        System.out.print(color + prompt + RESET);
        return System.console() != null ? 
               System.console().readLine() : 
               new java.util.Scanner(System.in).nextLine();
    }
    
    /**
     * Print text in specified color
     * 
     * @param text Text to print
     * @param color Color to use
     */
    public static void printColoredText(String text, String color) {
        System.out.println(color + text + RESET);
    }
    
    /**
     * Read an integer input within a range
     * 
     * @param prompt The prompt to display
     * @param min The minimum value (inclusive)
     * @param max The maximum value (inclusive)
     * @return The integer input
     */
    public static int readIntInput(String prompt, int min, int max) {
        int input = 0;
        boolean valid = false;
        
        while (!valid) {
            try {
                System.out.print(prompt);
                input = Integer.parseInt(new java.util.Scanner(System.in).nextLine().trim());
                
                if (input >= min && input <= max) {
                    valid = true;
                } else {
                    System.out.println(RED + "Input must be between " + min + " and " + max + RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid input. Please enter a number." + RESET);
            }
        }
        
        return input;
    }
    
    /**
     * Read a double input within a range
     * 
     * @param prompt The prompt to display
     * @param min The minimum value (inclusive)
     * @param max The maximum value (inclusive)
     * @return The double input
     */
    public static double readDoubleInput(String prompt, double min, double max) {
        double input = 0;
        boolean valid = false;
        
        while (!valid) {
            try {
                System.out.print(prompt);
                input = Double.parseDouble(new java.util.Scanner(System.in).nextLine().trim());
                
                if (input >= min && input <= max) {
                    valid = true;
                } else {
                    System.out.println(RED + "Input must be between " + min + " and " + max + RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid input. Please enter a number." + RESET);
            }
        }
        
        return input;
    }
    
    /**
     * Read a string input
     * 
     * @param prompt The prompt to display
     * @return The string input
     */
    public static String readStringInput(String prompt) {
        System.out.print(prompt);
        return new java.util.Scanner(System.in).nextLine().trim();
    }
    
    /**
     * Wait for the user to press Enter
     */
    public static void pressEnterToContinue() {
        System.out.print(YELLOW + "Press Enter to continue..." + RESET);
        new java.util.Scanner(System.in).nextLine();
    }
}