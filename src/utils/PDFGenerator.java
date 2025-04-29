package utils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PDFGenerator {
    
    /**
     * Generate a PDF report with the given content
     * 
     * @param title The title of the report
     * @param content The content of the report
     * @param parentComponent The parent component for dialog boxes
     */
    public static void generatePDF(String title, String content, Component parentComponent) {
        try {
            // Create file chooser for saving text file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Report");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
            
            // Show save dialog
            if (fileChooser.showSaveDialog(parentComponent) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            
            // Get selected file and ensure it has .txt extension
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".txt")) {
                filePath += ".txt";
            }
            
            // Create a string for the report
            StringBuilder report = new StringBuilder();
            report.append("EL-TA3BAN PHARMACY SYSTEM\n\n");
            report.append(title).append("\n");
            report.append("Generated On: ")
                  .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")))
                  .append("\n\n");
            report.append(content);
            
            // Write to file
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(report.toString());
            }
            
            // Show success message
            JOptionPane.showMessageDialog(
                parentComponent,
                "Report exported successfully as text file to:\n" + filePath + 
                "\n\nNote: PDF functionality is not available. Text format used instead.",
                "Export Complete",
                JOptionPane.INFORMATION_MESSAGE
            );
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                parentComponent,
                "Error exporting report: " + e.getMessage(),
                "Export Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
    
    /**
     * Generate a report with a table
     * 
     * @param title The title of the report
     * @param headers The table headers
     * @param data The table data
     * @param parentComponent The parent component for dialog boxes
     */
    public static void generateTablePDF(String title, String[] headers, Object[][] data, Component parentComponent) {
        StringBuilder content = new StringBuilder();
        
        // Add title
        content.append(title).append("\n\n");
        
        // Add table headers
        String headerFormat = "%-20s".repeat(headers.length);
        content.append(String.format(headerFormat, (Object[]) headers)).append("\n");
        
        // Add separator
        content.append("-".repeat(20 * headers.length)).append("\n");
        
        // Add data rows
        for (Object[] row : data) {
            content.append(String.format(headerFormat, row)).append("\n");
        }
        
        // Generate text file with the formatted content
        generatePDF(title, content.toString(), parentComponent);
    }
} 