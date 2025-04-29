package gui.admin;

import gui.MainFrame;
import gui.components.BasePanel;
import gui.components.StyledButton;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeIcons;
import services.PharmacyService;
import utils.PDFGenerator;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Panel for generating various reports in the pharmacy system
 */
public class ReportsPanel extends BasePanel {
    
    private JTextArea reportTextArea;
    private JComboBox<String> reportTypeComboBox;
    private JComboBox<String> timeRangeComboBox;
    private JButton generateButton;
    private JButton exportButton;
    private JButton printButton;
    private JButton backButton;
    
    public ReportsPanel(MainFrame mainFrame) {
        super(mainFrame);
        setBackground(ThemeColors.BACKGROUND);
    }
    
    @Override
    protected void initializeComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create main content panel
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
        
        // Create footer panel
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Report Generator");
        titleLabel.setFont(ThemeFonts.BOLD_XXLARGE);
        titleLabel.setForeground(ThemeColors.PRIMARY);
        titleLabel.setIcon(ThemeIcons.REPORT);
        
        // Controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        controlsPanel.setBackground(ThemeColors.SURFACE);
        
        // Report type combo box
        JLabel reportTypeLabel = new JLabel("Report Type:");
        reportTypeLabel.setFont(ThemeFonts.BOLD_MEDIUM);
        
        reportTypeComboBox = new JComboBox<>(new String[] {
            "Sales Report", 
            "Inventory Report", 
            "Financial Report",
            "User Activity Report",
            "Low Stock Report"
        });
        reportTypeComboBox.setFont(ThemeFonts.REGULAR_MEDIUM);
        
        // Time range combo box
        JLabel timeRangeLabel = new JLabel("Time Range:");
        timeRangeLabel.setFont(ThemeFonts.BOLD_MEDIUM);
        
        timeRangeComboBox = new JComboBox<>(new String[] {
            "Today",
            "This Week",
            "This Month",
            "Last 3 Months",
            "This Year",
            "All Time"
        });
        timeRangeComboBox.setFont(ThemeFonts.REGULAR_MEDIUM);
        
        // Generate button
        generateButton = new StyledButton("Generate Report", ThemeIcons.REFRESH);
        generateButton.addActionListener(e -> generateReport());
        
        controlsPanel.add(reportTypeLabel);
        controlsPanel.add(reportTypeComboBox);
        controlsPanel.add(timeRangeLabel);
        controlsPanel.add(timeRangeComboBox);
        controlsPanel.add(generateButton);
        
        // Back button
        backButton = new StyledButton("Back to Dashboard", ThemeIcons.BACK);
        backButton.addActionListener(e -> mainFrame.navigateTo("DASHBOARD"));
        
        // Add components to header
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(controlsPanel, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Report text area
        reportTextArea = new JTextArea();
        reportTextArea.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 14));
        reportTextArea.setEditable(false);
        reportTextArea.setMargin(new Insets(10, 10, 10, 10));
        reportTextArea.setBackground(Color.WHITE);
        reportTextArea.setForeground(ThemeColors.TEXT_PRIMARY);
        
        // Set some placeholder text
        reportTextArea.setText(
            "Select a report type and time range, then click Generate Report to view data.\n\n" +
            "Available Reports:\n" +
            "- Sales Report: View sales data and order statistics\n" +
            "- Inventory Report: Check current stock levels and medicine information\n" +
            "- Financial Report: View revenue, expenses, and profit information\n" +
            "- User Activity Report: Track user logins and system interactions\n" +
            "- Low Stock Report: Identify medicines that need to be restocked"
        );
        
        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(reportTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Export button
        exportButton = new StyledButton("Export to Text", ThemeIcons.EXPORT);
        exportButton.addActionListener(e -> exportReport());
        
        // Print button
        printButton = new StyledButton("Print Report", ThemeIcons.PRINT);
        printButton.addActionListener(e -> printReport());
        
        panel.add(exportButton);
        panel.add(printButton);
        
        return panel;
    }
    
    private void generateReport() {
        String reportType = (String) reportTypeComboBox.getSelectedItem();
        String timeRange = (String) timeRangeComboBox.getSelectedItem();
        
        // Reset text area
        reportTextArea.setText("");
        
        // Get the pharmacy service
        PharmacyService service = mainFrame.getPharmacyService();
        if (service == null) {
            reportTextArea.setText("Error: Pharmacy service is not available.");
            return;
        }
        
        // Generate report header
        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append("EL-TA3BAN PHARMACY SYSTEM\n");
        reportBuilder.append("=========================\n\n");
        reportBuilder.append("REPORT TYPE: ").append(reportType).append("\n");
        reportBuilder.append("TIME RANGE: ").append(timeRange).append("\n");
        reportBuilder.append("GENERATED ON: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))).append("\n");
        reportBuilder.append("=========================\n\n");
        
        // Generate report content based on type
        switch (reportType) {
            case "Sales Report":
                generateSalesReport(reportBuilder, service, timeRange);
                break;
            case "Inventory Report":
                generateInventoryReport(reportBuilder, service);
                break;
            case "Financial Report":
                generateFinancialReport(reportBuilder, service, timeRange);
                break;
            case "User Activity Report":
                generateUserActivityReport(reportBuilder, service, timeRange);
                break;
            case "Low Stock Report":
                generateLowStockReport(reportBuilder, service);
                break;
            default:
                reportBuilder.append("Invalid report type selected.");
        }
        
        // Display the report
        reportTextArea.setText(reportBuilder.toString());
        reportTextArea.setCaretPosition(0); // Scroll to top
    }
    
    private void generateSalesReport(StringBuilder report, PharmacyService service, String timeRange) {
        report.append("SALES REPORT\n");
        report.append("-----------\n\n");
        
        // Placeholder for real implementation
        report.append("Total Orders: ").append(service.getOrders().size()).append("\n");
        report.append("Total Sales: L.E ").append(String.format("%.2f", calculateTotalSales(service))).append("\n");
        report.append("Average Order Value: L.E ").append(String.format("%.2f", calculateAverageOrderValue(service))).append("\n\n");
        
        report.append("Top Selling Products:\n");
        report.append("1. Paramol - 120 units\n");
        report.append("2. Megamox - 87 units\n");
        report.append("3. Brufen - 65 units\n");
        report.append("4. Claritine - 42 units\n");
        report.append("5. Lopresor - 38 units\n\n");
        
        report.append("Sales by Time Period:\n");
        report.append("- Morning (6am-12pm): 35%\n");
        report.append("- Afternoon (12pm-6pm): 45%\n");
        report.append("- Evening (6pm-12am): 18%\n");
        report.append("- Night (12am-6am): 2%\n\n");
        
        report.append("Note: This is a placeholder report. In a real implementation, this would include actual sales data filtered by the selected time range.");
    }
    
    private void generateInventoryReport(StringBuilder report, PharmacyService service) {
        report.append("INVENTORY REPORT\n");
        report.append("---------------\n\n");
        
        report.append(String.format("%-5s %-20s %-15s %-10s %-10s %s\n", 
                "ID", "Name", "Category", "Price", "Stock", "Prescription"));
        report.append("------------------------------------------------------------------------------------\n");
        
        // Add actual inventory data
        service.getMedicines().forEach(medicine -> {
            report.append(String.format("%-5d %-20s %-15s L.E %-9.2f %-10d %s\n",
                    medicine.getId(),
                    medicine.getName(),
                    medicine.getCategory(),
                    medicine.getPrice(),
                    medicine.getStock(),
                    medicine.isPrescription() ? "Yes" : "No"));
        });
        
        report.append("\n\nInventory Summary:\n");
        report.append("Total Products: ").append(service.getMedicines().size()).append("\n");
        report.append("Total Stock Value: L.E ").append(String.format("%.2f", calculateTotalStockValue(service))).append("\n");
        
        int lowStockCount = (int) service.getMedicines().stream()
                .filter(m -> m.getStock() < 10)
                .count();
        
        report.append("Low Stock Items (< 10 units): ").append(lowStockCount).append("\n");
        report.append("Out of Stock Items: ").append(
                service.getMedicines().stream()
                        .filter(m -> m.getStock() <= 0)
                        .count()
        ).append("\n");
    }
    
    private void generateFinancialReport(StringBuilder report, PharmacyService service, String timeRange) {
        report.append("FINANCIAL REPORT\n");
        report.append("----------------\n\n");
        
        double totalRevenue = calculateTotalSales(service);
        double estimatedCosts = totalRevenue * 0.6; // Placeholder: assuming 60% cost
        double estimatedProfit = totalRevenue - estimatedCosts;
        
        report.append("Total Revenue: L.E ").append(String.format("%.2f", totalRevenue)).append("\n");
        report.append("Estimated Costs: L.E ").append(String.format("%.2f", estimatedCosts)).append("\n");
        report.append("Estimated Profit: L.E ").append(String.format("%.2f", estimatedProfit)).append("\n\n");
        
        report.append("Revenue by Category:\n");
        report.append("- Prescription Medicines: L.E ").append(String.format("%.2f", totalRevenue * 0.7)).append("\n");
        report.append("- Over-the-counter Medicines: L.E ").append(String.format("%.2f", totalRevenue * 0.3)).append("\n\n");
        
        report.append("Note: This is a placeholder report. In a real implementation, this would include actual financial data filtered by the selected time range.");
    }
    
    private void generateUserActivityReport(StringBuilder report, PharmacyService service, String timeRange) {
        report.append("USER ACTIVITY REPORT\n");
        report.append("-------------------\n\n");
        
        report.append("User Statistics:\n");
        report.append("- Total Admins: ").append(service.getAdmins().size()).append("\n");
        report.append("- Total Doctors: ").append(service.getDoctors().size()).append("\n");
        report.append("- Total Pharmacists: ").append(service.getPharmacists().size()).append("\n");
        report.append("- Total Patients: ").append(service.getPatients().size()).append("\n\n");
        
        report.append("Recent Activities:\n");
        report.append("- New User Registrations: 12\n");
        report.append("- Login Sessions: 145\n");
        report.append("- Orders Placed: 78\n");
        report.append("- Prescriptions Created: 36\n\n");
        
        report.append("Note: This is a placeholder report. In a real implementation, this would include actual user activity data filtered by the selected time range.");
    }
    
    private void generateLowStockReport(StringBuilder report, PharmacyService service) {
        report.append("LOW STOCK REPORT\n");
        report.append("---------------\n\n");
        
        report.append(String.format("%-5s %-20s %-15s %-10s %s\n", 
                "ID", "Name", "Category", "Stock", "Reorder Level"));
        report.append("----------------------------------------------------------------\n");
        
        // Add low stock items
        service.getMedicines().stream()
                .filter(m -> m.getStock() < 10)
                .forEach(medicine -> {
                    report.append(String.format("%-5d %-20s %-15s %-10d %s\n",
                            medicine.getId(),
                            medicine.getName(),
                            medicine.getCategory(),
                            medicine.getStock(),
                            medicine.getStock() <= 0 ? "URGENT" : "SOON"));
                });
        
        report.append("\n\nLow Stock Summary:\n");
        long outOfStock = service.getMedicines().stream()
                .filter(m -> m.getStock() <= 0)
                .count();
        
        long criticallyLow = service.getMedicines().stream()
                .filter(m -> m.getStock() > 0 && m.getStock() < 5)
                .count();
        
        long lowStock = service.getMedicines().stream()
                .filter(m -> m.getStock() >= 5 && m.getStock() < 10)
                .count();
        
        report.append("- Out of Stock (0 units): ").append(outOfStock).append("\n");
        report.append("- Critically Low (1-4 units): ").append(criticallyLow).append("\n");
        report.append("- Low Stock (5-9 units): ").append(lowStock).append("\n");
        report.append("- Total Items Needing Attention: ").append(outOfStock + criticallyLow + lowStock).append("\n");
    }
    
    private double calculateTotalSales(PharmacyService service) {
        // Calculate total sales from orders (placeholder implementation)
        return service.getOrders().stream()
                .mapToDouble(order -> order.getTotalAmount())
                .sum();
    }
    
    private double calculateAverageOrderValue(PharmacyService service) {
        // Calculate average order value (placeholder implementation)
        int orderCount = service.getOrders().size();
        return orderCount > 0 ? calculateTotalSales(service) / orderCount : 0;
    }
    
    private double calculateTotalStockValue(PharmacyService service) {
        // Calculate total value of current inventory
        return service.getMedicines().stream()
                .mapToDouble(medicine -> medicine.getPrice() * medicine.getStock())
                .sum();
    }
    
    private void exportReport() {
        try {
            // Create file chooser for saving text file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Report");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
            
            // Show save dialog
            if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            
            // Get selected file and ensure it has .txt extension
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".txt")) {
                filePath += ".txt";
            }
            
            // Write report content to file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(reportTextArea.getText().getBytes());
            }
            
            // Show success message
            JOptionPane.showMessageDialog(
                this,
                "Report exported successfully to:\n" + filePath,
                "Export Complete",
                JOptionPane.INFORMATION_MESSAGE,
                ThemeIcons.SUCCESS
            );
            
            // Open the text file
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(new File(filePath));
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error exporting report: " + e.getMessage(),
                "Export Error",
                JOptionPane.ERROR_MESSAGE,
                ThemeIcons.ERROR
            );
            e.printStackTrace();
        }
    }
    
    private void printReport() {
        // Placeholder for print functionality
        JOptionPane.showMessageDialog(
            this,
            "Print functionality will be implemented here.\nIn a real application, this would send the report to a printer.",
            "Print Report",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
} 