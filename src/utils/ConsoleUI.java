package utils;

/**
 * 💻 ConsoleUI - Enhanced visual interface for the console environment 💻
 * 
 * This utility class transforms the basic console into a rich, colorful, and
 * interactive interface for the EL-TA3BAN Pharmacy System. It implements ANSI
 * escape sequences to provide colors, animations, and styled text elements.
 * 
 * 🔑 OOP Concepts Demonstrated:
 * - Utility Class Pattern: A stateless class with only static methods
 * - Encapsulation: Groups related UI functionality together
 * - Abstraction: Hides the complexity of console formatting behind simple methods
 * 
 * 📚 Class Responsibilities:
 * - Create visually appealing UI elements (headers, menus, tables)
 * - Display interactive animations (progress bars, spinners)
 * - Provide consistent styling for different message types (success, error, etc.)
 * - Handle user input with formatted prompts
 * - Implement text effects like typing animations
 * 
 * 🌐 Role in System:
 * This class enhances the user experience throughout the entire application,
 * making the console interface more intuitive and visually engaging. It creates
 * a professional appearance for the pharmacy system with minimal resource usage.
 */
public class ConsoleUI {
    // ANSI color codes for console text coloring
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    
    // ANSI background color codes
    public static final String BG_BLACK = "\u001B[40m";
    public static final String BG_RED = "\u001B[41m";
    public static final String BG_GREEN = "\u001B[42m";
    public static final String BG_YELLOW = "\u001B[43m";
    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_PURPLE = "\u001B[45m";
    public static final String BG_CYAN = "\u001B[46m";
    public static final String BG_WHITE = "\u001B[47m";
    
    // ANSI text style codes
    public static final String BOLD = "\u001B[1m";
    public static final String UNDERLINE = "\u001B[4m";
    
    /**
     * Clear the console screen
     * Note: This may not work in all environments
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    /**
     * 🎨 printHeader - Creates a beautifully styled title container
     * 
     * This method creates a visually appealing boxed header with Unicode box
     * drawing characters. It automatically centers the text for a professional
     * appearance and applies the specified ANSI color for easy visual identification.
     * 
     * @param title The header text (typically the section or page title)
     * @param width Width of the header box in characters
     * @param color ANSI color code for the header's appearance
     */
    public static void printHeader(String title, int width, String color) {
        String line = "═".repeat(width);
        int padding = (width - title.length()) / 2;
        String paddedTitle = " ".repeat(Math.max(0, padding)) + title + " ".repeat(Math.max(0, padding));
        
        // Adjust if odd length
        if (paddedTitle.length() < width) {
            paddedTitle += " ";
        }
        
        System.out.println(color + "╔" + line + "╗" + RESET);
        System.out.println(color + "║" + paddedTitle + "║" + RESET);
        System.out.println(color + "╚" + line + "╝" + RESET);
    }
    
    /**
     * Print a boxed menu item
     * 
     * @param number Item number
     * @param description Item description
     * @param emoji Emoji for the item
     * @param color Color for the item (ANSI color code)
     */
    public static void printMenuItem(int number, String description, String emoji, String color) {
        System.out.println(color + "  [" + number + "] " + emoji + " " + description + RESET);
    }
    
    /**
     * Print a simple progress bar
     * 
     * @param current Current progress value
     * @param total Total progress value
     * @param width Width of the progress bar
     * @param color Color for the progress bar (ANSI color code)
     */
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
    
    /**
     * 🔄 showLoadingSpinner - Creates dynamic animation during processing
     * 
     * This method implements a non-blocking loading animation using Unicode 
     * characters to create a spinning effect. It's used during time-consuming
     * operations (file loading, data processing) to provide visual feedback
     * and improve the perceived performance of the application.
     * 
     * @param message The loading status message to display
     * @param seconds Duration to display the animation
     * @param color ANSI color code for the spinner
     */
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
     * ✅ printSuccess - Provides positive feedback on completed operations
     * 
     * This method visually indicates successful operations with green text
     * and a checkmark symbol. It's used throughout the system to confirm
     * completed actions like successful logins, completed transactions,
     * and saved data to provide clear user feedback.
     * 
     * @param message Success message describing the completed action
     */
    public static void printSuccess(String message) {
        System.out.println(GREEN + "✓ " + message + RESET);
    }
    
    /**
     * ❌ printError - Communicates problems requiring user attention
     * 
     * This method clearly highlights error conditions with red text and
     * an X symbol. It's used to indicate failed operations, invalid inputs,
     * and system errors with high visibility to ensure users notice problems
     * and can take appropriate action to resolve them.
     * 
     * @param message Error message describing the problem
     */
    public static void printError(String message) {
        System.out.println(RED + "✗ " + message + RESET);
    }
    
    /**
     * Print info message with blue color
     * 
     * @param message Info message
     */
    public static void printInfo(String message) {
        System.out.println(BLUE + "ℹ " + message + RESET);
    }
    
    /**
     * Print warning message with yellow color
     * 
     * @param message Warning message
     */
    public static void printWarning(String message) {
        System.out.println(YELLOW + "⚠ " + message + RESET);
    }
    
    /**
     * Print a horizontal line for visual separation
     * 
     * @param width Width of the line
     * @param color Color for the line (ANSI color code)
     */
    public static void printLine(int width, String color) {
        System.out.println(color + "─".repeat(width) + RESET);
    }
    
    /**
     * Show a yes/no confirmation prompt
     * 
     * @param message Confirmation message
     * @return true if user confirms, false otherwise
     */
    public static boolean confirmPrompt(String message) {
        System.out.print(YELLOW + "⚠ " + message + " (y/n): " + RESET);
        String input = System.console().readLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }
    
    /**
     * ⌨️ typeText - Simulates human typing for a more engaging interface
     * 
     * This method creates a realistic typing animation by printing each character
     * with a small delay, creating the illusion of real-time typing. It's used
     * for important messages, welcome screens, and story elements to make the
     * console interface feel more dynamic and interactive.
     * 
     * @param message The text content to be displayed character by character
     * @param delayMs Millisecond delay between characters (typically 25-70ms)
     * @param color ANSI color code for the text
     */
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
     * Print a table header with column titles
     * 
     * @param columns Column titles
     * @param widths Column widths
     * @param color Color for the header (ANSI color code)
     */
    public static void printTableHeader(String[] columns, int[] widths, String color) {
        // Print header row
        System.out.print(color);
        for (int i = 0; i < columns.length; i++) {
            System.out.print("│ " + padRight(columns[i], widths[i]) + " ");
        }
        System.out.println("│" + RESET);
        
        // Print separator line
        System.out.print(color + "├");
        for (int width : widths) {
            System.out.print("─".repeat(width + 2) + "┼");
        }
        // Replace last '┼' with '┤'
        System.out.print("\b" + "┤" + RESET);
        System.out.println();
    }
    
    /**
     * Print a table row with data
     * 
     * @param data Row data
     * @param widths Column widths
     * @param color Color for the row (ANSI color code)
     */
    public static void printTableRow(String[] data, int[] widths, String color) {
        System.out.print(color);
        for (int i = 0; i < data.length; i++) {
            System.out.print("│ " + padRight(data[i], widths[i]) + " ");
        }
        System.out.println("│" + RESET);
    }
    
    /**
     * Pad a string to a specific length with spaces
     * 
     * @param s String to pad
     * @param width Total width after padding
     * @return Padded string
     */
    private static String padRight(String s, int width) {
        return String.format("%-" + width + "s", s);
    }
    
    /**
     * Create an input prompt with color
     * 
     * @param prompt Prompt message
     * @param color Color for the prompt (ANSI color code)
     * @return User input string
     */
    public static String promptInput(String prompt, String color) {
        System.out.print(color + prompt + RESET);
        return System.console() != null ? 
               System.console().readLine() : 
               new java.util.Scanner(System.in).nextLine();
    }
}