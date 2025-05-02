package gui.components;

import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.function.Consumer;

/**
 * Modern dialog utility class for creating consistent, styled dialogs and components
 * that match the application theme.
 */
public class ModernDialog {

    /**
     * Style a dialog to ensure proper appearance
     * @param dialog The dialog to style
     */
    private static void styleDialog(JDialog dialog) {
        dialog.setBackground(ThemeColors.SURFACE);
        
        // Ensure all buttons in the dialog have proper styling
        styleAllButtonsInContainer(dialog);
        
        // Make sure components are styled
        JRootPane rootPane = dialog.getRootPane();
        rootPane.setBackground(ThemeColors.SURFACE);
    }
    
    /**
     * Style all buttons in a container to ensure proper appearance
     * @param container The container to search for buttons
     */
    private static void styleAllButtonsInContainer(Container container) {
        // Process all components in this container
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                
                // Make sure the button is opaque and shows its background
                button.setOpaque(true);
                button.setContentAreaFilled(true);
                
                // Apply specific styling to Cancel buttons
                if ("Cancel".equals(button.getText())) {
                    button.setBackground(new Color(80, 80, 80));
                    button.setForeground(ThemeColors.PRIMARY);
                }
            }
            // Recursively process nested containers
            else if (comp instanceof Container) {
                styleAllButtonsInContainer((Container) comp);
            }
        }
    }
    
    /**
     * Shows a modern-styled message dialog
     * 
     * @param parent The parent component
     * @param message The message to display
     * @param title The dialog title
     * @param messageType The type of message (JOptionPane constants)
     */
    public static void showMessage(Component parent, String message, String title, int messageType) {
        JOptionPane pane = ThemeManager.createStyledOptionPane(
            message, title, messageType, JOptionPane.DEFAULT_OPTION, null);
        
        JDialog dialog = pane.createDialog(parent, title);
        styleDialog(dialog);
        
        dialog.setVisible(true);
        dialog.dispose();
    }
    
    /**
     * Shows a modern-styled confirmation dialog
     * 
     * @param parent The parent component
     * @param message The message to display
     * @param title The dialog title
     * @param optionType The type of options (JOptionPane constants)
     * @return The user's selection (JOptionPane constants)
     */
    public static int showConfirmation(Component parent, String message, String title, int optionType) {
        JOptionPane pane = ThemeManager.createStyledOptionPane(
            message, title, JOptionPane.QUESTION_MESSAGE, optionType, null);
            
        JDialog dialog = pane.createDialog(parent, title);
        styleDialog(dialog);
        
        dialog.setVisible(true);
        dialog.dispose();
        
        Object value = pane.getValue();
        if (value == null || !(value instanceof Integer)) {
            return JOptionPane.CLOSED_OPTION;
        }
        
        return (Integer) value;
    }
    
    /**
     * Shows a modern-styled input dialog
     * 
     * @param parent The parent component
     * @param message The message to display
     * @param title The dialog title
     * @param initialValue The initial value for the input field
     * @return The user's input, or null if canceled
     */
    public static String showInputDialog(Component parent, String message, String title, String initialValue) {
        JTextField textField = new JTextField(initialValue);
        ThemeManager.applyTextComponentStyling(textField);
        textField.setColumns(30);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(ThemeColors.SURFACE);
        panel.add(new JLabel(message), BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);
        
        JOptionPane pane = ThemeManager.createStyledOptionPane(
            panel, title, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);
        
        JDialog dialog = pane.createDialog(parent, title);
        styleDialog(dialog);
        
        dialog.setVisible(true);
        dialog.dispose();
        
        Object value = pane.getValue();
        if (value == null || (Integer) value != JOptionPane.OK_OPTION) {
            return null;
        }
        
        return textField.getText();
    }
    
    /**
     * Shows a modern file chooser dialog for opening files
     * 
     * @param parent The parent component
     * @param title The dialog title
     * @param initialDirectory The initial directory to show
     * @param extensions Array of extensions to filter by (e.g. "pdf", "txt")
     * @param description Description for the file filter
     * @return The selected file, or null if canceled
     */
    public static File showOpenFileDialog(
            Component parent, 
            String title, 
            File initialDirectory, 
            String[] extensions, 
            String description) {
            
        JFileChooser chooser = new JFileChooser(initialDirectory);
        chooser.setDialogTitle(title);
        
        // Style the file chooser
        chooser.setBackground(ThemeColors.SURFACE);
        chooser.setFont(ThemeFonts.REGULAR_MEDIUM);
        
        // Add file filter
        if (extensions != null && extensions.length > 0) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extensions);
            chooser.setFileFilter(filter);
        }
        
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        
        return null;
    }
    
    /**
     * Shows a modern file chooser dialog for saving files
     * 
     * @param parent The parent component
     * @param title The dialog title
     * @param initialDirectory The initial directory to show
     * @param defaultFileName Default file name suggestion
     * @param extensions Array of extensions to filter by (e.g. "pdf", "txt")
     * @param description Description for the file filter
     * @return The selected file, or null if canceled
     */
    public static File showSaveFileDialog(
            Component parent, 
            String title, 
            File initialDirectory, 
            String defaultFileName, 
            String[] extensions, 
            String description) {
            
        JFileChooser chooser = new JFileChooser(initialDirectory);
        chooser.setDialogTitle(title);
        
        // Style the file chooser
        chooser.setBackground(ThemeColors.SURFACE);
        chooser.setFont(ThemeFonts.REGULAR_MEDIUM);
        
        // Set default file name
        if (defaultFileName != null && !defaultFileName.isEmpty()) {
            chooser.setSelectedFile(new File(initialDirectory, defaultFileName));
        }
        
        // Add file filter
        if (extensions != null && extensions.length > 0) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extensions);
            chooser.setFileFilter(filter);
        }
        
        int result = chooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            
            // Add extension if needed
            if (extensions != null && extensions.length > 0 && file.getName().indexOf('.') == -1) {
                file = new File(file.getAbsolutePath() + "." + extensions[0]);
            }
            
            return file;
        }
        
        return null;
    }
    
    /**
     * Creates a table with modern styling based on our StyledTable
     * 
     * @param <T> The type of data in the table
     * @param columnNames Array of column names
     * @param rowMapper Function to map data objects to table rows
     * @return A new StyledTable instance
     */
    public static <T> StyledTable<T> createTable(String[] columnNames, StyledTable.RowMapper<T> rowMapper) {
        StyledTable<T> table = new StyledTable<>(columnNames, rowMapper);
        
        // Apply additional modern styling
        table.getTableHeader().setBackground(ThemeColors.SURFACE_VARIANT);
        table.getTableHeader().setForeground(ThemeColors.TEXT_PRIMARY);
        table.getTableHeader().setFont(ThemeFonts.BOLD_MEDIUM);
        
        table.setRowHeight(32); // Increased row height for better readability
        table.setIntercellSpacing(new Dimension(8, 0)); // More spacing between columns
        table.setShowGrid(false); // Modern tables often hide grid lines
        table.setShowHorizontalLines(true); // But keep horizontal lines for readability
        
        // Customize selection colors
        table.setSelectionBackground(ThemeColors.PRIMARY_LIGHT);
        table.setSelectionForeground(ThemeColors.TEXT_PRIMARY);
        
        return table;
    }
} 