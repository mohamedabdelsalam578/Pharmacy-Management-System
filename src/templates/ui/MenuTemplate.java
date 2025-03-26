package templates.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import utils.ConsoleUI;
import tools.ui.ConsoleUITools;

/**
 * 📝 MenuTemplate - Template for creating interactive console menus 📝
 * 
 * This template provides a standardized way to create and render
 * interactive menus in the EL-TA3BAN Pharmacy System. It implements
 * a builder pattern for easy configuration and includes support for
 * multiple menu styles, animations, and action handlers.
 * 
 * 🔑 OOP Concepts Demonstrated:
 * - Builder Pattern: Fluent API for menu construction
 * - Command Pattern: Menu options execute actions
 * - Single Responsibility: Focused solely on menu rendering
 * 
 * 📚 Class Features:
 * - Multiple menu styles (boxed, inline, tabbed)
 * - Emoji support for menu items
 * - Customizable colors and animations
 * - Action handlers for menu selections
 * - Input validation and error handling
 * 
 * 🌐 How to Use:
 * 1. Create a new menu with MenuTemplate.builder()
 * 2. Configure the menu with title, style, and options
 * 3. Add actions for each menu option
 * 4. Call show() to display the menu and handle input
 */
public class MenuTemplate {
    private String title;
    private List<MenuItem> menuItems;
    private String style;
    private boolean animateTitle;
    private int width;
    private String headerColor;
    private String footerMessage;
    
    /**
     * Private constructor used by the builder
     */
    private MenuTemplate() {
        this.menuItems = new ArrayList<>();
        this.style = "boxed";
        this.animateTitle = false;
        this.width = 50;
        this.headerColor = ConsoleUI.BLUE;
    }
    
    /**
     * Create a new menu builder
     * 
     * @return A new MenuTemplateBuilder
     */
    public static MenuTemplateBuilder builder() {
        return new MenuTemplateBuilder();
    }
    
    /**
     * Show the menu and handle user input
     * 
     * @return The selected option number or -1 if exit
     */
    public int show() {
        renderMenu();
        return handleInput();
    }
    
    /**
     * Render the menu based on the configured style
     */
    private void renderMenu() {
        switch (style) {
            case "boxed":
                renderBoxedMenu();
                break;
            case "inline":
                renderInlineMenu();
                break;
            case "tabbed":
                renderTabbedMenu();
                break;
            default:
                renderBoxedMenu();
        }
    }
    
    /**
     * Render a boxed menu style with borders
     */
    private void renderBoxedMenu() {
        // Title with animation if enabled
        if (animateTitle) {
            ConsoleUI.typeText(title, 30, headerColor);
        } else {
            ConsoleUI.printHeader(title, width, headerColor);
        }
        
        // Menu items
        ConsoleUI.printLine(width, ConsoleUI.RESET);
        for (int i = 0; i < menuItems.size(); i++) {
            MenuItem item = menuItems.get(i);
            ConsoleUI.printMenuItem(i + 1, item.getLabel(), item.getIcon(), ConsoleUI.RESET);
        }
        ConsoleUI.printLine(width, ConsoleUI.RESET);
        
        // Footer message if present
        if (footerMessage != null && !footerMessage.isEmpty()) {
            System.out.println(ConsoleUI.ITALIC + footerMessage + ConsoleUI.RESET);
            System.out.println();
        }
    }
    
    /**
     * Render an inline menu style without borders
     */
    private void renderInlineMenu() {
        // Title
        System.out.println(headerColor + ConsoleUI.BOLD + title + ConsoleUI.RESET);
        System.out.println();
        
        // Menu items in a horizontal line
        for (int i = 0; i < menuItems.size(); i++) {
            MenuItem item = menuItems.get(i);
            System.out.print("[" + (i + 1) + "] " + item.getIcon() + " " + item.getLabel());
            
            // Add separator except for last item
            if (i < menuItems.size() - 1) {
                System.out.print(" | ");
            }
        }
        
        System.out.println("\n");
        
        // Footer message if present
        if (footerMessage != null && !footerMessage.isEmpty()) {
            System.out.println(ConsoleUI.ITALIC + footerMessage + ConsoleUI.RESET);
            System.out.println();
        }
    }
    
    /**
     * Render a tabbed menu style with tab-like headers
     */
    private void renderTabbedMenu() {
        // Title
        System.out.println(headerColor + ConsoleUI.BOLD + title + ConsoleUI.RESET);
        
        // Tab line
        System.out.print(" ");
        for (int i = 0; i < menuItems.size(); i++) {
            System.out.print("┌" + "─".repeat(menuItems.get(i).getLabel().length() + 6) + "┐ ");
        }
        System.out.println();
        
        // Menu items in tabs
        System.out.print(" ");
        for (int i = 0; i < menuItems.size(); i++) {
            MenuItem item = menuItems.get(i);
            System.out.print("│ " + (i + 1) + ". " + item.getIcon() + " " + item.getLabel() + " │ ");
        }
        System.out.println();
        
        // Bottom of tabs
        System.out.print(" ");
        for (int i = 0; i < menuItems.size(); i++) {
            System.out.print("└" + "─".repeat(menuItems.get(i).getLabel().length() + 6) + "┘ ");
        }
        System.out.println("\n");
        
        // Footer message if present
        if (footerMessage != null && !footerMessage.isEmpty()) {
            System.out.println(ConsoleUI.ITALIC + footerMessage + ConsoleUI.RESET);
            System.out.println();
        }
    }
    
    /**
     * Handle user input for menu selection
     * 
     * @return The selected option number or -1 if exit
     */
    private int handleInput() {
        System.out.print(ConsoleUI.BOLD + "Enter your choice: " + ConsoleUI.RESET);
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();
        
        try {
            int choice = Integer.parseInt(input);
            
            if (choice >= 1 && choice <= menuItems.size()) {
                // Execute the action for this menu item
                MenuItem selectedItem = menuItems.get(choice - 1);
                if (selectedItem.getAction() != null) {
                    selectedItem.getAction().run();
                }
                return choice;
            } else {
                ConsoleUI.printError("Invalid option. Please choose between 1 and " + menuItems.size());
                return -1;
            }
        } catch (NumberFormatException e) {
            ConsoleUI.printError("Invalid input. Please enter a number.");
            return -1;
        }
    }
    
    /**
     * Inner class representing a menu item
     */
    private static class MenuItem {
        private String label;
        private String icon;
        private Runnable action;
        
        public MenuItem(String label, String icon, Runnable action) {
            this.label = label;
            this.icon = icon;
            this.action = action;
        }
        
        public String getLabel() {
            return label;
        }
        
        public String getIcon() {
            return icon;
        }
        
        public Runnable getAction() {
            return action;
        }
    }
    
    /**
     * Builder class for creating MenuTemplate instances
     */
    public static class MenuTemplateBuilder {
        private MenuTemplate menu;
        
        public MenuTemplateBuilder() {
            menu = new MenuTemplate();
        }
        
        /**
         * Set the menu title
         * 
         * @param title Menu title
         * @return Builder instance
         */
        public MenuTemplateBuilder withTitle(String title) {
            menu.title = title;
            return this;
        }
        
        /**
         * Add a menu item
         * 
         * @param label Item label
         * @param icon Item icon (emoji)
         * @param action Action to execute when selected
         * @return Builder instance
         */
        public MenuTemplateBuilder addItem(String label, String icon, Runnable action) {
            menu.menuItems.add(new MenuItem(label, icon, action));
            return this;
        }
        
        /**
         * Set the menu style
         * 
         * @param style Menu style (boxed, inline, tabbed)
         * @return Builder instance
         */
        public MenuTemplateBuilder withStyle(String style) {
            menu.style = style;
            return this;
        }
        
        /**
         * Enable or disable title animation
         * 
         * @param animate True to animate, false otherwise
         * @return Builder instance
         */
        public MenuTemplateBuilder animateTitle(boolean animate) {
            menu.animateTitle = animate;
            return this;
        }
        
        /**
         * Set the menu width
         * 
         * @param width Menu width in characters
         * @return Builder instance
         */
        public MenuTemplateBuilder withWidth(int width) {
            menu.width = width;
            return this;
        }
        
        /**
         * Set the header color
         * 
         * @param color ANSI color code
         * @return Builder instance
         */
        public MenuTemplateBuilder withHeaderColor(String color) {
            menu.headerColor = color;
            return this;
        }
        
        /**
         * Set a footer message
         * 
         * @param message Footer message
         * @return Builder instance
         */
        public MenuTemplateBuilder withFooterMessage(String message) {
            menu.footerMessage = message;
            return this;
        }
        
        /**
         * Build the final menu
         * 
         * @return Configured MenuTemplate instance
         */
        public MenuTemplate build() {
            return menu;
        }
    }
    
    /**
     * Example usage of the menu template
     */
    public static void exampleUsage() {
        // Create a sample menu using the builder
        MenuTemplate mainMenu = MenuTemplate.builder()
            .withTitle("SAMPLE MENU")
            .withStyle("boxed")
            .withWidth(60)
            .withHeaderColor(ConsoleUI.BLUE)
            .addItem("View Items", "📋", () -> System.out.println("Viewing items..."))
            .addItem("Add Item", "➕", () -> System.out.println("Adding new item..."))
            .addItem("Exit", "🚪", () -> System.out.println("Exiting..."))
            .withFooterMessage("Enter your choice or 'q' to quit")
            .build();
        
        // Show the menu
        int selection = mainMenu.show();
        System.out.println("Selected option: " + selection);
    }
}