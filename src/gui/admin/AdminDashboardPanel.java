package gui.admin;

import gui.MainFrame;
import gui.components.ActionButton;
import gui.components.StyledButton;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeIcons;
import models.Admin;
import models.Medicine;
import models.Order;
import models.OrderItem;
import models.Patient;
import models.User;
import services.PharmacyService;
import utils.DateUtils;
import utils.PDFGenerator;
import gui.components.RoundedBorder;
import gui.theme.ThemeSizes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Dashboard panel for Admin users
 * Displays system status, user management, and admin actions
 */
public class AdminDashboardPanel extends BaseDashboardPanel {

    private JPanel summaryPanel;
    private JPanel revenuePanel;
    
    public AdminDashboardPanel(MainFrame mainFrame) {
        super(mainFrame);
        initializeComponents();
    }
    
    @Override
    protected void initializeComponents() {
        createSummaryPanel();
        createRevenuePanel();
    }
    
    @Override
    protected JPanel createRoleSpecificPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(ThemeColors.BACKGROUND);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 10, 5);
        gbc.fill = GridBagConstraints.BOTH;
        
        // Summary panel (left)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.65;
        gbc.weighty = 1.0;
        panel.add(summaryPanel, gbc);
        
        // Revenue panel (right)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.35;
        gbc.weighty = 1.0;
        panel.add(revenuePanel, gbc);
        
        return panel;
    }
    
    /**
     * Create summary panel with action buttons and user management
     */
    private void createSummaryPanel() {
        summaryPanel = new JPanel();
        summaryPanel.setLayout(new BorderLayout(0, ThemeSizes.DEFAULT_SPACING));
        summaryPanel.setBackground(ThemeColors.SURFACE);
        summaryPanel.setBorder(new EmptyBorder(ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING));
        
        // Panel title
        JLabel titleLabel = new JLabel("Admin Controls");
        titleLabel.setFont(ThemeFonts.BOLD_TITLE);
        titleLabel.setForeground(ThemeColors.PRIMARY);
        titleLabel.setIcon(ThemeIcons.SETTINGS);
        titleLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
        
        // Action buttons panel with improved layout
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(3, 1, 0, 6));
        buttonsPanel.setBackground(ThemeColors.SURFACE);
        
        // Simplified button design with more space-efficient layout
        ActionButton userManagementBtn = new ActionButton(
            "User Management",
            "Add, edit or delete users",
            ThemeIcons.PROFILE
        );
        userManagementBtn.addActionListener(e -> {
            mainFrame.navigateTo("USER_MANAGEMENT");
        });
        
        ActionButton inventoryManagementBtn = new ActionButton(
            "Inventory Management",
            "Manage medicines and stock levels",
            ThemeIcons.MEDICINE
        );
        inventoryManagementBtn.addActionListener(e -> {
            mainFrame.navigateTo("INVENTORY_MANAGEMENT");
        });
        
        ActionButton reportsBtn = new ActionButton(
            "Reports",
            "Generate and view system reports",
            ThemeIcons.REPORT
        );
        reportsBtn.addActionListener(e -> {
            mainFrame.navigateTo("REPORTS");
        });
        
        // Add buttons to panel
        buttonsPanel.add(userManagementBtn);
        buttonsPanel.add(inventoryManagementBtn);
        buttonsPanel.add(reportsBtn);
        
        // Add to summary panel
        summaryPanel.add(titleLabel, BorderLayout.NORTH);
        summaryPanel.add(buttonsPanel, BorderLayout.CENTER);
    }
    
    /**
     * Create revenue panel with financial information
     */
    private void createRevenuePanel() {
        revenuePanel = new JPanel();
        revenuePanel.setLayout(new BorderLayout(0, ThemeSizes.DEFAULT_SPACING));
        revenuePanel.setBackground(ThemeColors.SURFACE);
        revenuePanel.setBorder(new EmptyBorder(ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING));
        
        // Panel title
        JLabel titleLabel = new JLabel("Financial Overview");
        titleLabel.setFont(ThemeFonts.BOLD_TITLE);
        titleLabel.setForeground(ThemeColors.PRIMARY);
        titleLabel.setIcon(ThemeIcons.ORDER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
        
        // Statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        statsPanel.setBackground(ThemeColors.SURFACE);
        
        // Calculate financial metrics
        double totalRevenue = calculateTotalRevenue();
        int ordersThisMonth = getOrdersThisMonth();
        
        // Calculate monthly revenue
        double monthlyRevenue = calculateMonthlyRevenue();
        
        // Total revenue
        JPanel revenueStatsPanel = createRevenueStatPanel(
            "Total Revenue",
            String.format("L.E %.2f", totalRevenue),
            ThemeColors.SUCCESS
        );
        
        // Monthly revenue (this month)
        JPanel monthlyRevenuePanel = createRevenueStatPanel(
            "Revenue This Month",
            String.format("L.E %.2f", monthlyRevenue),
            ThemeColors.INFO
        );
        
        // Orders this month
        JPanel ordersStatsPanel = createRevenueStatPanel(
            "Orders This Month",
            String.valueOf(ordersThisMonth),
            ThemeColors.PRIMARY
        );
        
        // Average order value
        double avgOrderValue = 0.0;
        if (ordersThisMonth > 0) {
            avgOrderValue = monthlyRevenue / ordersThisMonth;
        } else {
            // Calculate avg for all orders if no orders this month
            PharmacyService service = mainFrame.getPharmacyService();
            if (service != null && service.getOrders() != null && !service.getOrders().isEmpty()) {
                avgOrderValue = totalRevenue / service.getOrders().size();
            }
        }
        
        JPanel avgStatsPanel = createRevenueStatPanel(
            "Average Order Value",
            String.format("L.E %.2f", avgOrderValue),
            ThemeColors.WARNING
        );
        
        statsPanel.add(revenueStatsPanel);
        statsPanel.add(monthlyRevenuePanel);
        statsPanel.add(ordersStatsPanel);
        statsPanel.add(avgStatsPanel);
        
        // Add components to panel
        revenuePanel.add(titleLabel, BorderLayout.NORTH);
        revenuePanel.add(statsPanel, BorderLayout.CENTER);
        
        // Export button
        JButton exportButton = new StyledButton("Export Report", ThemeIcons.REPORT);
        exportButton.addActionListener(e -> exportFinancialReport());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(ThemeColors.SURFACE);
        buttonPanel.add(exportButton);
        
        revenuePanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Calculate revenue for the current month only
     */
    private double calculateMonthlyRevenue() {
        PharmacyService service = mainFrame.getPharmacyService();
        if (service == null) {
            return 0.0;
        }
        
        double monthlyRevenue = 0.0;
        try {
            List<Order> orders = service.getOrders();
            if (orders == null || orders.isEmpty()) {
                return 0.0;
            }
            
            LocalDateTime now = LocalDateTime.now();
            System.out.println("Calculating revenue for " + now.getMonth() + " " + now.getYear());
            
            // Calculate revenue only for orders from the current month
            for (Order order : orders) {
                Date date = order.getOrderDate();
                if (date != null) {
                    LocalDateTime orderDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    
                    if (orderDate.getMonth() == now.getMonth() && 
                        orderDate.getYear() == now.getYear() &&
                        (order.getStatus().toString().equals("COMPLETED") || 
                         order.getStatus().toString().equals("PAID") ||
                         order.getStatus().toString().equals("DELIVERED"))) {
                        
                        for (OrderItem item : order.getItems()) {
                            monthlyRevenue += item.getTotalPrice();
                        }
                    }
                }
            }
            
            System.out.println("Monthly revenue for " + now.getMonth() + ": " + 
                              String.format("%.2f", monthlyRevenue) + " LE");
            
        } catch (Exception e) {
            System.err.println("Error calculating monthly revenue: " + e.getMessage());
            e.printStackTrace();
        }
        
        return monthlyRevenue;
    }
    
    /**
     * Create a revenue statistics panel
     */
    private JPanel createRevenueStatPanel(String title, String value, Color valueColor) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(ThemeColors.SURFACE_VARIANT);
        panel.setBorder(new EmptyBorder((int)(ThemeSizes.CARD_PADDING * 1.2), ThemeSizes.CARD_PADDING, (int)(ThemeSizes.CARD_PADDING * 1.2), ThemeSizes.CARD_PADDING));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.LEFT);
        titleLabel.setFont(ThemeFonts.BOLD_MEDIUM);
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        
        JLabel valueLabel = new JLabel(value, SwingConstants.RIGHT);
        valueLabel.setFont(ThemeFonts.FUTURISTIC_LARGE);
        valueLabel.setForeground(valueColor);
        valueLabel.setPreferredSize(new Dimension(100, valueLabel.getPreferredSize().height));
        
        // Reduce font sizes by 25% for compact look
        titleLabel.setFont(titleLabel.getFont().deriveFont(titleLabel.getFont().getSize2D() * 0.75f));
        valueLabel.setFont(valueLabel.getFont().deriveFont(valueLabel.getFont().getSize2D() * 0.75f));
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Calculate total revenue from all orders with detailed breakdown
     */
    private double calculateTotalRevenue() {
        PharmacyService service = mainFrame.getPharmacyService();
        if (service == null) {
            showError("PharmacyService is not available");
            return 0.0;
        }
        
        double totalRevenue = 0.0;
        try {
            List<Order> orders = service.getOrders();
            if (orders == null || orders.isEmpty()) {
                System.out.println("No orders found in the system");
                return 0.0;
            }
            
            System.out.println("Calculating revenue from " + orders.size() + " orders");
            
            // Calculate revenue by iterating through all orders and their items
            for (Order order : orders) {
                if (order.getStatus().toString().equals("COMPLETED") || order.getStatus().toString().equals("PAID")) {
                    for (OrderItem item : order.getItems()) {
                        double itemTotal = item.getTotalPrice();
                        totalRevenue += itemTotal;
                        System.out.println("Order #" + order.getId() + " - Item: " + 
                                          item.getMedicineName() + " - Quantity: " + 
                                          item.getQuantity() + " - Unit Price: " + 
                                          String.format("%.2f", item.getUnitPrice()) + " - Total: " + 
                                          String.format("%.2f", itemTotal) + " LE");
                    }
                }
            }
            
            System.out.println("Total calculated revenue: " + String.format("%.2f", totalRevenue) + " LE");
        } catch (Exception e) {
            System.err.println("Error calculating revenue: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to calculate revenue: " + e.getMessage());
        }
        
        return totalRevenue;
    }
    
    /**
     * Get the count of orders this month with detailed breakdown by status
     */
    private int getOrdersThisMonth() {
        PharmacyService service = mainFrame.getPharmacyService();
        if (service == null) {
            showError("PharmacyService is not available");
            return 0;
        }
        
        Map<String, Integer> orderStatusCounts = new HashMap<>();
        int count = 0;
        try {
            List<Order> orders = service.getOrders();
            if (orders == null || orders.isEmpty()) {
                System.out.println("No orders found in the system");
                return 0;
            }
            
            LocalDateTime now = LocalDateTime.now();
            System.out.println("Counting orders for " + now.getMonth() + " " + now.getYear());
            
            // Define the start and end of current month for more precise filtering
            LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            
            // Calculate total revenue for this month while counting orders
            double monthlyRevenue = 0.0;
            
            // Count orders for current month with status breakdown
            for (Order order : orders) {
                // Convert Date to LocalDateTime
                Date date = order.getOrderDate();
                if (date != null) {
                    Instant instant = date.toInstant();
                    LocalDateTime orderDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                    
                    if (orderDate.getMonth() == now.getMonth() && 
                        orderDate.getYear() == now.getYear()) {
                        count++;
                        
                        // Track counts by status
                        String status = order.getStatus().toString();
                        orderStatusCounts.put(status, orderStatusCounts.getOrDefault(status, 0) + 1);
                        
                        // Calculate revenue for COMPLETED and PAID orders
                        if ("COMPLETED".equals(status) || "PAID".equals(status)) {
                            for (OrderItem item : order.getItems()) {
                                monthlyRevenue += item.getTotalPrice();
                            }
                        }
                        
                        System.out.println("Order #" + order.getId() + " - Date: " + 
                                          orderDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + 
                                          " - Status: " + status);
                    }
                }
            }
            
            // Log detailed breakdown by status
            System.out.println("Orders this month by status:");
            for (Map.Entry<String, Integer> entry : orderStatusCounts.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
            System.out.println("Total orders this month: " + count);
            System.out.println("Monthly revenue: " + String.format("%.2f", monthlyRevenue) + " LE");
            
        } catch (Exception e) {
            System.err.println("Error counting monthly orders: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to count monthly orders: " + e.getMessage());
        }
        
        return count;
    }
    
    /**
     * Calculate additional financial metrics
     */
    private Map<String, Double> calculateFinancialMetrics() {
        Map<String, Double> metrics = new HashMap<>();
        PharmacyService service = mainFrame.getPharmacyService();
        
        if (service == null) {
            return metrics;
        }
        
        try {
            List<Order> orders = service.getOrders();
            List<Medicine> inventory = service.getMedicines();
            
            if (orders == null || orders.isEmpty()) {
                return metrics;
            }
            
            // Calculate total inventory value
            double inventoryValue = 0.0;
            for (Medicine medicine : inventory) {
                inventoryValue += medicine.getPrice() * medicine.getStock();
            }
            metrics.put("inventoryValue", inventoryValue);
            
            // Calculate yesterday's revenue
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime yesterday = now.minusDays(1);
            double yesterdayRevenue = 0.0;
            
            for (Order order : orders) {
                Date date = order.getOrderDate();
                LocalDateTime orderDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
                
                if (orderDate.toLocalDate().equals(yesterday.toLocalDate()) && 
                    (order.getStatus().equals("COMPLETED") || order.getStatus().equals("PAID"))) {
                    for (OrderItem item : order.getItems()) {
                        yesterdayRevenue += item.getTotalPrice();
                    }
                }
            }
            metrics.put("yesterdayRevenue", yesterdayRevenue);
            
            // Calculate weekly revenue
            LocalDateTime weekStart = now.minusDays(now.getDayOfWeek().getValue() - 1).withHour(0).withMinute(0);
            double weeklyRevenue = 0.0;
            
            for (Order order : orders) {
                Date date = order.getOrderDate();
                LocalDateTime orderDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
                
                if (orderDate.isAfter(weekStart) && 
                    (order.getStatus().equals("COMPLETED") || order.getStatus().equals("PAID"))) {
                    for (OrderItem item : order.getItems()) {
                        weeklyRevenue += item.getTotalPrice();
                    }
                }
            }
            metrics.put("weeklyRevenue", weeklyRevenue);
            
            // Calculate profit estimation (assuming 30% profit margin)
            double estimatedProfit = 0.0;
            for (Order order : orders) {
                if (order.getStatus().equals("COMPLETED") || order.getStatus().equals("PAID")) {
                    double orderTotal = 0.0;
                    for (OrderItem item : order.getItems()) {
                        orderTotal += item.getTotalPrice();
                    }
                    estimatedProfit += orderTotal * 0.3; // 30% profit margin
                }
            }
            metrics.put("estimatedProfit", estimatedProfit);
            
            System.out.println("Financial metrics calculated:");
            System.out.println("  Inventory Value: " + String.format("%.2f", inventoryValue) + " LE");
            System.out.println("  Yesterday's Revenue: " + String.format("%.2f", yesterdayRevenue) + " LE");
            System.out.println("  Weekly Revenue: " + String.format("%.2f", weeklyRevenue) + " LE");
            System.out.println("  Estimated Profit: " + String.format("%.2f", estimatedProfit) + " LE");
            
        } catch (Exception e) {
            System.err.println("Error calculating financial metrics: " + e.getMessage());
            e.printStackTrace();
        }
        
        return metrics;
    }
    
    /**
     * Export financial report with detailed revenue data
     */
    private void exportFinancialReport() {
        StringBuilder content = new StringBuilder();
        content.append("FINANCIAL REPORT\n");
        content.append("================\n\n");
        
        // Add current date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy - h:mm a");
        content.append("Generated on: ").append(now.format(formatter)).append("\n\n");
        
        // Add financial data
        double totalRevenue = calculateTotalRevenue();
        int ordersThisMonth = getOrdersThisMonth();
        
        // Calculate average order value with appropriate handling for zero values
        double avgOrderValue = 0.0;
        PharmacyService service = mainFrame.getPharmacyService();
        int totalOrders = 0;
        
        if (service != null && service.getOrders() != null) {
            totalOrders = service.getOrders().size();
            if (totalOrders > 0) {
                avgOrderValue = totalRevenue / totalOrders;
            }
        }
        
        // Add summary data
        content.append("SUMMARY\n");
        content.append("-------\n");
        content.append("Total Revenue: L.E ").append(String.format("%.2f", totalRevenue)).append("\n");
        content.append("Total Orders: ").append(totalOrders).append("\n");
        content.append("Orders This Month: ").append(ordersThisMonth).append("\n");
        content.append("Average Order Value: L.E ").append(String.format("%.2f", avgOrderValue)).append("\n\n");
        
        // Add additional financial metrics
        Map<String, Double> metrics = calculateFinancialMetrics();
        if (!metrics.isEmpty()) {
            content.append("ADDITIONAL METRICS\n");
            content.append("-----------------\n");
            
            if (metrics.containsKey("inventoryValue")) {
                content.append("Current Inventory Value: L.E ").append(String.format("%.2f", metrics.get("inventoryValue"))).append("\n");
            }
            
            if (metrics.containsKey("yesterdayRevenue")) {
                content.append("Yesterday's Revenue: L.E ").append(String.format("%.2f", metrics.get("yesterdayRevenue"))).append("\n");
            }
            
            if (metrics.containsKey("weeklyRevenue")) {
                content.append("Weekly Revenue: L.E ").append(String.format("%.2f", metrics.get("weeklyRevenue"))).append("\n");
            }
            
            if (metrics.containsKey("estimatedProfit")) {
                content.append("Estimated Profit: L.E ").append(String.format("%.2f", metrics.get("estimatedProfit"))).append("\n");
            }
            
            content.append("\n");
        }
        
        // Add medicine sales data if available
        if (service != null && service.getOrders() != null && !service.getOrders().isEmpty()) {
            // Calculate top selling medicines
            Map<String, Integer> medicineSales = new HashMap<>();
            Map<String, Double> medicineRevenue = new HashMap<>();
            
            for (Order order : service.getOrders()) {
                if (order.getStatus().equals("COMPLETED") || order.getStatus().equals("PAID")) {
                    for (OrderItem item : order.getItems()) {
                        String medicineName = item.getMedicineName();
                        int quantity = item.getQuantity();
                        double revenue = item.getTotalPrice();
                        
                        // Update sales count
                        medicineSales.put(medicineName, medicineSales.getOrDefault(medicineName, 0) + quantity);
                        
                        // Update revenue
                        medicineRevenue.put(medicineName, medicineRevenue.getOrDefault(medicineName, 0.0) + revenue);
                    }
                }
            }
            
            // Sort medicines by sales
            List<Map.Entry<String, Integer>> sortedSales = new ArrayList<>(medicineSales.entrySet());
            sortedSales.sort((a, b) -> b.getValue().compareTo(a.getValue()));
            
            // Add top selling medicines to report
            content.append("TOP SELLING MEDICINES\n");
            content.append("-------------------\n");
            
            int count = 0;
            for (Map.Entry<String, Integer> entry : sortedSales) {
                String medicineName = entry.getKey();
                int sales = entry.getValue();
                double revenue = medicineRevenue.getOrDefault(medicineName, 0.0);
                
                content.append(String.format("%d. %s - %d units (L.E %.2f)\n", 
                        ++count, medicineName, sales, revenue));
                
                if (count >= 5) break; // Limit to top 5
            }
        }
        
        // Generate PDF
        PDFGenerator.generatePDF("Financial Report", content.toString(), this);
    }
    
    @Override
    protected DashboardStat[] getQuickStats() {
        DashboardStat[] stats = new DashboardStat[3];
        
        try {
            PharmacyService service = mainFrame.getPharmacyService();
            
            // Total Users stat
            int totalUsers = 0;
            if (service != null) {
                totalUsers += service.getPatients().size();
                totalUsers += service.getDoctors().size();
                totalUsers += service.getPharmacists().size();
                // Admin count would be added here if we had a method to get them
            }
            
            stats[0] = new DashboardStat(
                "Total Users", 
                String.valueOf(totalUsers), 
                ThemeIcons.PROFILE, 
                ThemeColors.PRIMARY
            );
            
            // Total Medicines stat
            int totalMedicines = service != null ? service.getMedicines().size() : 0;
            stats[1] = new DashboardStat(
                "Medicines", 
                String.valueOf(totalMedicines), 
                ThemeIcons.MEDICINE, 
                ThemeColors.SUCCESS
            );
            
            // Low Stock Items stat
            int lowStockCount = 0;
            if (service != null) {
                for (Medicine medicine : service.getMedicines()) {
                    if (medicine.getStock() < 10) {
                        lowStockCount++;
                    }
                }
            }
            
            stats[2] = new DashboardStat(
                "Low Stock Items", 
                String.valueOf(lowStockCount), 
                ThemeIcons.WARNING, 
                lowStockCount > 0 ? ThemeColors.WARNING : ThemeColors.SUCCESS
            );
            
        } catch (Exception e) {
            System.err.println("Error loading quick stats: " + e.getMessage());
            
            // Provide empty stats if there's an error
            stats[0] = new DashboardStat("Total Users", "0", ThemeIcons.PROFILE, ThemeColors.PRIMARY);
            stats[1] = new DashboardStat("Medicines", "0", ThemeIcons.MEDICINE, ThemeColors.SUCCESS);
            stats[2] = new DashboardStat("Low Stock Items", "0", ThemeIcons.WARNING, ThemeColors.SUCCESS);
        }
        
        return stats;
    }
    
    @Override
    protected DashboardActivity[] getRecentActivities() {
        DashboardActivity[] activities = new DashboardActivity[5];
        
        try {
            // Get real data from service when possible
            PharmacyService service = mainFrame.getPharmacyService();
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
            
            // Get a random user for our activity if available
            List<User> allUsers = new ArrayList<>();
            if (service != null) {
                allUsers.addAll(service.getPatients());
                allUsers.addAll(service.getDoctors());
                allUsers.addAll(service.getPharmacists());
            }
            
            // Get low stock count
            int lowStockCount = 0;
            if (service != null) {
                for (Medicine medicine : service.getMedicines()) {
                    if (medicine.getStock() < 10) {
                        lowStockCount++;
                    }
                }
            }
            
            String userName = "System User";
            if (!allUsers.isEmpty()) {
                Collections.shuffle(allUsers);
                userName = allUsers.get(0).getName();
            }
            
            activities[0] = new DashboardActivity(
                "User Login", 
                "Admin logged in", 
                now.minusMinutes(2).format(timeFormatter), 
                ThemeIcons.PROFILE
            );
            
            activities[1] = new DashboardActivity(
                "System Update", 
                "Inventory data refreshed", 
                now.minusMinutes(15).format(timeFormatter), 
                ThemeIcons.REFRESH
            );
            
            activities[2] = new DashboardActivity(
                "New User", 
                userName + " registered", 
                now.minusHours(1).format(timeFormatter), 
                ThemeIcons.SUCCESS
            );
            
            activities[3] = new DashboardActivity(
                "Low Stock Alert", 
                lowStockCount + " items below threshold", 
                now.minusHours(3).format(timeFormatter), 
                ThemeIcons.WARNING
            );
            
            activities[4] = new DashboardActivity(
                "System Backup", 
                "Automatic backup completed", 
                now.minusHours(6).format(timeFormatter), 
                ThemeIcons.SAVE
            );
            
        } catch (Exception e) {
            System.err.println("Error loading recent activities: " + e.getMessage());
            
            // Provide minimal activities if there's an error
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
            
            activities[0] = new DashboardActivity(
                "User Login", 
                "Admin logged in", 
                now.minusMinutes(2).format(timeFormatter), 
                ThemeIcons.PROFILE
            );
            
            for (int i = 1; i < 5; i++) {
                activities[i] = new DashboardActivity(
                    "System Activity", 
                    "No details available", 
                    now.minusHours(i).format(timeFormatter), 
                    ThemeIcons.INFO
                );
            }
        }
        
        return activities;
    }

    private void createHeaderPanel() {
        headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(ThemeColors.SURFACE);
        headerPanel.setBorder(new EmptyBorder(ThemeSizes.PADDING_MEDIUM, ThemeSizes.PADDING_LARGE, ThemeSizes.PADDING_MEDIUM, ThemeSizes.PADDING_LARGE));
        
        // Welcome message
        JLabel welcomeLabel = new JLabel(getWelcomeMessage());
        welcomeLabel.setFont(ThemeFonts.FUTURISTIC_LARGE);
        welcomeLabel.setForeground(ThemeColors.PRIMARY);
        
        // Date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy - h:mm a");
        JLabel dateTimeLabel = new JLabel(now.format(formatter));
        dateTimeLabel.setFont(ThemeFonts.REGULAR_MEDIUM);
        dateTimeLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(ThemeColors.SURFACE);
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);
        welcomePanel.add(dateTimeLabel, BorderLayout.SOUTH);
        
        // Role icon
        JLabel roleIconLabel = new JLabel(getRoleIcon());
        roleIconLabel.setBorder(new EmptyBorder(0, 0, 0, 15));

        // Add logout button
        StyledButton logoutButton = new StyledButton("Logout", ThemeIcons.LOGOUT);
        logoutButton.addActionListener(e -> mainFrame.navigateTo("LOGOUT"));
        logoutButton.setFont(ThemeFonts.BOLD_MEDIUM);
        logoutButton.setBackground(ThemeColors.DANGER);
        logoutButton.setForeground(Color.WHITE);
        
        // Create right panel for logout button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(ThemeColors.SURFACE);
        rightPanel.add(logoutButton);
        
        headerPanel.add(roleIconLabel, BorderLayout.WEST);
        headerPanel.add(welcomePanel, BorderLayout.CENTER);
        headerPanel.add(rightPanel, BorderLayout.EAST);
    }
} 