package tools.ui;

import utils.ConsoleUI;

/**
 * 🖥️ ConsoleUITools - Enhanced UI components for console applications 🖥️
 * 
 * This utility class builds on the ConsoleUI base functionality to provide
 * higher-level UI components for the pharmacy system. It includes templates
 * for common UI patterns like forms, confirmation dialogs, and selection menus.
 * 
 * 🔑 OOP Concepts Demonstrated:
 * - Composition: Builds on top of the base ConsoleUI class
 * - Template Method Pattern: Provides standard layouts for common UI needs
 * - Abstraction: Hides complex UI formatting details
 * 
 * 📚 Class Responsibilities:
 * - Create standardized form layouts for data entry
 * - Display confirmation dialogs with consistent styling
 * - Render selection menus with options
 * - Format and display tabular data
 * - Show progress and status indicators
 * 
 * 🌐 Role in System:
 * This utility enhances the user experience by providing standardized UI components
 * that maintain a consistent look and feel throughout the pharmacy application.
 */
public class ConsoleUITools {
    
    // Standard widths for UI components
    public static final int STANDARD_WIDTH = 50;
    public static final int WIDE_WIDTH = 70;
    public static final int NARROW_WIDTH = 30;
    
    /**
     * Display a form header with title
     * 
     * @param title The form title
     */
    public static void displayFormHeader(String title) {
        System.out.println();
        ConsoleUI.printHeader(title, STANDARD_WIDTH, ConsoleUI.BLUE);
        ConsoleUI.printLine(STANDARD_WIDTH, ConsoleUI.RESET);
    }
    
    /**
     * Display a form field label with formatting
     * 
     * @param label The field label
     * @param required True if field is required
     */
    public static void displayFormFieldLabel(String label, boolean required) {
        String requiredMark = required ? " *" : "";
        System.out.print(ConsoleUI.BOLD + label + requiredMark + ": " + ConsoleUI.RESET);
    }
    
    /**
     * Display a form section divider with subtitle
     * 
     * @param subtitle The section subtitle
     */
    public static void displayFormSection(String subtitle) {
        System.out.println();
        System.out.println(ConsoleUI.UNDERLINE + ConsoleUI.BOLD + subtitle + ConsoleUI.RESET);
    }
    
    /**
     * Display a confirmation dialog and get user response
     * 
     * @param message The confirmation message
     * @return True if user confirms, false otherwise
     */
    public static boolean showConfirmationDialog(String message) {
        System.out.println();
        ConsoleUI.printLine(STANDARD_WIDTH, ConsoleUI.YELLOW);
        System.out.println(ConsoleUI.YELLOW + "⚠ " + message + ConsoleUI.RESET);
        ConsoleUI.printLine(STANDARD_WIDTH, ConsoleUI.YELLOW);
        System.out.print(ConsoleUI.BOLD + "Confirm (y/n): " + ConsoleUI.RESET);
        
        String input = System.console() != null ? 
                System.console().readLine().trim().toLowerCase() : 
                new java.util.Scanner(System.in).nextLine().trim().toLowerCase();
                
        return input.equals("y") || input.equals("yes");
    }
    
    /**
     * Display a selection menu with numbered options
     * 
     * @param title The menu title
     * @param options Array of menu options
     * @param icons Array of icons for menu options (can be null)
     * @return The selected option index (1-based) or 0 if invalid
     */
    public static int showSelectionMenu(String title, String[] options, String[] icons) {
        ConsoleUI.printHeader(title, STANDARD_WIDTH, ConsoleUI.CYAN);
        ConsoleUI.printLine(STANDARD_WIDTH, ConsoleUI.RESET);
        
        for (int i = 0; i < options.length; i++) {
            String icon = (icons != null && i < icons.length) ? icons[i] : "•";
            ConsoleUI.printMenuItem(i + 1, options[i], icon, ConsoleUI.RESET);
        }
        
        ConsoleUI.printLine(STANDARD_WIDTH, ConsoleUI.RESET);
        String input = ConsoleUI.promptInput("Enter your choice: ", ConsoleUI.CYAN);
        
        try {
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= options.length) {
                return choice;
            } else {
                ConsoleUI.printError("Invalid option. Please choose between 1 and " + options.length);
                return 0;
            }
        } catch (NumberFormatException e) {
            ConsoleUI.printError("Invalid input. Please enter a number.");
            return 0;
        }
    }
    
    /**
     * Display a data table with headers and rows
     * 
     * @param headers Table headers
     * @param data Table data (rows of columns)
     * @param columnWidths Width for each column
     */
    public static void displayTable(String[] headers, String[][] data, int[] columnWidths) {
        // Print table header
        System.out.print(ConsoleUI.CYAN + "┌");
        for (int i = 0; i < columnWidths.length; i++) {
            System.out.print("─".repeat(columnWidths[i] + 2));
            System.out.print(i < columnWidths.length - 1 ? "┬" : "┐");
        }
        System.out.println(ConsoleUI.RESET);
        
        // Print headers
        System.out.print(ConsoleUI.CYAN + "│");
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            if (header.length() > columnWidths[i]) {
                header = header.substring(0, columnWidths[i] - 3) + "...";
            }
            System.out.print(" " + ConsoleUI.BOLD + padRight(header, columnWidths[i]) + ConsoleUI.RESET + ConsoleUI.CYAN + " │");
        }
        System.out.println(ConsoleUI.RESET);
        
        // Print separator
        System.out.print(ConsoleUI.CYAN + "├");
        for (int i = 0; i < columnWidths.length; i++) {
            System.out.print("─".repeat(columnWidths[i] + 2));
            System.out.print(i < columnWidths.length - 1 ? "┼" : "┤");
        }
        System.out.println(ConsoleUI.RESET);
        
        // Print data rows
        for (String[] row : data) {
            System.out.print(ConsoleUI.CYAN + "│" + ConsoleUI.RESET);
            
            for (int i = 0; i < row.length && i < columnWidths.length; i++) {
                String cell = row[i];
                if (cell == null) {
                    cell = "";
                } else if (cell.length() > columnWidths[i]) {
                    cell = cell.substring(0, columnWidths[i] - 3) + "...";
                }
                System.out.print(" " + padRight(cell, columnWidths[i]) + " " + ConsoleUI.CYAN + "│" + ConsoleUI.RESET);
            }
            
            System.out.println();
        }
        
        // Print bottom border
        System.out.print(ConsoleUI.CYAN + "└");
        for (int i = 0; i < columnWidths.length; i++) {
            System.out.print("─".repeat(columnWidths[i] + 2));
            System.out.print(i < columnWidths.length - 1 ? "┴" : "┘");
        }
        System.out.println(ConsoleUI.RESET);
    }
    
    /**
     * Show a status message with animated typing effect
     * 
     * @param message The status message
     * @param status The status type (success, info, warning, error)
     */
    public static void showStatusMessage(String message, String status) {
        String color;
        String symbol;
        
        switch (status.toLowerCase()) {
            case "success":
                color = ConsoleUI.GREEN;
                symbol = "✓";
                break;
            case "warning":
                color = ConsoleUI.YELLOW;
                symbol = "⚠";
                break;
            case "error":
                color = ConsoleUI.RED;
                symbol = "✗";
                break;
            case "info":
            default:
                color = ConsoleUI.BLUE;
                symbol = "ℹ";
                break;
        }
        
        System.out.print(color + symbol + " ");
        ConsoleUI.typeText(message, 30, color);
    }
    
    /**
     * Create a text-based card UI for displaying entity information
     * 
     * @param title The card title
     * @param data Map of field names to values
     */
    public static void displayInfoCard(String title, String[][] data) {
        int maxLabelLength = 0;
        for (String[] field : data) {
            if (field[0].length() > maxLabelLength) {
                maxLabelLength = field[0].length();
            }
        }
        
        int cardWidth = maxLabelLength + 30; // Allow space for values
        
        // Card title
        ConsoleUI.printHeader(title, cardWidth, ConsoleUI.PURPLE);
        
        // Card content
        for (String[] field : data) {
            String label = field[0];
            String value = field[1];
            
            System.out.println(ConsoleUI.BOLD + padRight(label + ":", maxLabelLength + 1) + 
                             ConsoleUI.RESET + " " + value);
        }
        
        // Card footer
        ConsoleUI.printLine(cardWidth, ConsoleUI.PURPLE);
        System.out.println();
    }
    
    /**
     * Show a password input field with masking
     * 
     * @param prompt The input prompt
     * @return The password entered
     */
    public static String getPasswordInput(String prompt) {
        if (System.console() != null) {
            System.out.print(ConsoleUI.BOLD + prompt + ConsoleUI.RESET);
            char[] passwordChars = System.console().readPassword();
            return new String(passwordChars);
        } else {
            // Fallback for environments without console (like IDEs)
            System.out.print(ConsoleUI.BOLD + prompt + 
                         " (input not masked in this environment): " + ConsoleUI.RESET);
            return new java.util.Scanner(System.in).nextLine();
        }
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
}