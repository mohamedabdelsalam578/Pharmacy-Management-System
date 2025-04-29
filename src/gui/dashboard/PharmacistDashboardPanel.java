package gui.dashboard;

import gui.MainFrame;
import gui.admin.BaseDashboardPanel;
import gui.components.StyledButton;
import gui.components.StyledTable;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeIcons;
import gui.theme.ThemeSizes;
import models.Order;
import models.Medicine;
import models.Prescription;
import models.Patient;
import models.Doctor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PharmacistDashboardPanel extends BaseDashboardPanel {
    private StyledTable<Order> ordersTable;
    private StyledTable<Medicine> medicinesTable;
    private StyledTable<Prescription> prescriptionsTable;

    public PharmacistDashboardPanel(MainFrame frame) {
        super(frame);
    }
    
    @Override
    protected void initializeComponents() {
        // BaseDashboardPanel already handles the initialization
    }
    
    @Override
    protected JPanel createRoleSpecificPanel() {
        // Create tabs for different sections
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(ThemeColors.BACKGROUND);
        tabbedPane.setForeground(ThemeColors.TEXT_PRIMARY);
        tabbedPane.setFont(ThemeFonts.REGULAR_MEDIUM);

        // Orders Tab
        JPanel ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.setBackground(ThemeColors.BACKGROUND);
        
        // Create orders table
        String[] orderColumns = {"ID", "Patient", "Date", "Status", "Total Amount"};
        ordersTable = new StyledTable<Order>(orderColumns, order -> {
            Object[] row = new Object[5];
            row[0] = order.getId();
            // Get patient name through service
            Patient patient = mainFrame.getService().getPatients().stream()
                .filter(p -> p.getId() == order.getPatientId())
                .findFirst()
                .orElse(null);
            row[1] = patient != null ? patient.getName() : "Unknown Patient";
            row[2] = order.getOrderDate().toString();
            row[3] = order.getStatus();
            row[4] = String.format("L.E %.2f", order.calculateTotal());
            return row;
        });
        
        // Add table to scroll pane
        JScrollPane ordersScrollPane = new JScrollPane(ordersTable);
        ordersScrollPane.setBackground(ThemeColors.BACKGROUND);
        ordersPanel.add(ordersScrollPane, BorderLayout.CENTER);
        
        // Add buttons panel for orders
        JPanel orderButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        orderButtonsPanel.setBackground(ThemeColors.BACKGROUND);
        
        StyledButton viewOrderButton = new StyledButton("View Order");
        viewOrderButton.addActionListener(e -> {
            Order selected = ordersTable.getSelectedItem();
            if (selected != null) {
                mainFrame.navigateTo("ORDER_DETAILS", selected);
            }
        });
        
        StyledButton processOrderButton = new StyledButton("Process Order");
        processOrderButton.addActionListener(e -> {
            Order selected = ordersTable.getSelectedItem();
            if (selected != null) {
                mainFrame.navigateTo("PROCESS_ORDER", selected);
            }
        });
        
        StyledButton exportOrdersButton = new StyledButton("Export History", ThemeIcons.EXPORT);
        exportOrdersButton.addActionListener(e -> exportOrderHistory());
        
        orderButtonsPanel.add(exportOrdersButton);
        orderButtonsPanel.add(viewOrderButton);
        orderButtonsPanel.add(processOrderButton);
        ordersPanel.add(orderButtonsPanel, BorderLayout.SOUTH);
        
        // Medicines Tab
        JPanel medicinesPanel = new JPanel(new BorderLayout());
        medicinesPanel.setBackground(ThemeColors.BACKGROUND);
        
        // Create medicines table
        String[] medicineColumns = {"Name", "Description", "Price", "Stock"};
        medicinesTable = new StyledTable<Medicine>(medicineColumns, medicine -> {
            Object[] row = new Object[4];
            row[0] = medicine.getName();
            row[1] = medicine.getDescription();
            row[2] = String.format("L.E %.2f", medicine.getPrice());
            row[3] = String.valueOf(medicine.getStock());
            return row;
        });
        
        // Add table to scroll pane
        JScrollPane medicinesScrollPane = new JScrollPane(medicinesTable);
        medicinesScrollPane.setBackground(ThemeColors.BACKGROUND);
        medicinesPanel.add(medicinesScrollPane, BorderLayout.CENTER);
        
        // Add buttons panel for medicines
        JPanel medicineButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        medicineButtonsPanel.setBackground(ThemeColors.BACKGROUND);
        
        StyledButton addMedicineButton = new StyledButton("Add Medicine");
        addMedicineButton.addActionListener(e -> mainFrame.navigateTo("ADD_MEDICINE", null));
        
        StyledButton editMedicineButton = new StyledButton("Edit Medicine");
        editMedicineButton.addActionListener(e -> {
            Medicine selected = medicinesTable.getSelectedItem();
            if (selected != null) {
                mainFrame.navigateTo("EDIT_MEDICINE", selected);
            }
        });
        
        StyledButton exportInventoryButton = new StyledButton("Export Inventory", ThemeIcons.EXPORT);
        exportInventoryButton.addActionListener(e -> exportInventoryReport());
        
        medicineButtonsPanel.add(exportInventoryButton);
        medicineButtonsPanel.add(addMedicineButton);
        medicineButtonsPanel.add(editMedicineButton);
        medicinesPanel.add(medicineButtonsPanel, BorderLayout.SOUTH);
        
        // Prescriptions Tab
        JPanel prescriptionsPanel = new JPanel(new BorderLayout());
        prescriptionsPanel.setBackground(ThemeColors.BACKGROUND);
        
        // Create prescriptions table
        String[] prescriptionColumns = {"ID", "Patient", "Doctor", "Date", "Status"};
        prescriptionsTable = new StyledTable<Prescription>(prescriptionColumns, prescription -> {
            Object[] row = new Object[5];
            row[0] = prescription.getId();
            // Get patient name through service
            Patient patient = mainFrame.getService().getPatients().stream()
                .filter(p -> p.getId() == prescription.getPatientId())
                .findFirst()
                .orElse(null);
            row[1] = patient != null ? patient.getName() : "Unknown Patient";
            // Get doctor name through service
            Doctor doctor = mainFrame.getService().getDoctors().stream()
                .filter(d -> d.getId() == prescription.getDoctorId())
                .findFirst()
                .orElse(null);
            row[2] = doctor != null ? doctor.getName() : "Unknown Doctor";
            row[3] = prescription.getIssueDate().toString();
            row[4] = prescription.getStatus();
            return row;
        });
        
        // Add table to scroll pane
        JScrollPane prescriptionsScrollPane = new JScrollPane(prescriptionsTable);
        prescriptionsScrollPane.setBackground(ThemeColors.BACKGROUND);
        prescriptionsPanel.add(prescriptionsScrollPane, BorderLayout.CENTER);
        
        // Add view button
        StyledButton viewPrescriptionButton = new StyledButton("View Prescription");
        viewPrescriptionButton.addActionListener(e -> {
            Prescription selected = prescriptionsTable.getSelectedItem();
            if (selected != null) {
                mainFrame.navigateTo("PRESCRIPTION_DETAILS", selected);
            }
        });
        prescriptionsPanel.add(viewPrescriptionButton, BorderLayout.SOUTH);
        
        // Add tabs to tabbed pane
        tabbedPane.addTab("Orders", ordersPanel);
        tabbedPane.addTab("Medicines", medicinesPanel);
        tabbedPane.addTab("Prescriptions", prescriptionsPanel);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.BACKGROUND);
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        // Load data
        loadData();
        
        return panel;
    }
    
    @Override
    protected DashboardStat[] getQuickStats() {
        // Get data from service
        int orderCount = mainFrame.getService().getOrders().size();
        int medicineCount = mainFrame.getService().getMedicines().size();
        int lowStockCount = (int) mainFrame.getService().getMedicines().stream()
            .filter(m -> m.getStock() < 10)
            .count();
        
        // Create stats
        return new DashboardStat[] {
            new DashboardStat("Orders", 
                String.valueOf(orderCount), 
                ThemeIcons.ORDER, 
                ThemeColors.PRIMARY),
            new DashboardStat("Medicines", 
                String.valueOf(medicineCount), 
                ThemeIcons.MEDICINE, 
                ThemeColors.SUCCESS),
            new DashboardStat("Low Stock Items", 
                String.valueOf(lowStockCount), 
                ThemeIcons.WARNING, 
                ThemeColors.WARNING)
        };
    }
    
    @Override
    protected DashboardActivity[] getRecentActivities() {
        List<DashboardActivity> activities = new ArrayList<>();
        
        // Add recent orders
        List<Order> orders = mainFrame.getService().getOrders();
        if (orders != null && !orders.isEmpty()) {
            for (int i = 0; i < Math.min(2, orders.size()); i++) {
                Order order = orders.get(i);
                
                // Get patient name
                String patientName = "Unknown Patient";
                Patient patient = mainFrame.getService().getPatients().stream()
                    .filter(p -> p.getId() == order.getPatientId())
                    .findFirst()
                    .orElse(null);
                
                if (patient != null) {
                    patientName = patient.getName();
                }
                
                activities.add(new DashboardActivity(
                    "New Order", 
                    "From " + patientName, 
                    order.getOrderDate().toString(),
                    ThemeIcons.ORDER));
            }
        }
        
        // Add medicines with low stock
        List<Medicine> lowStockMeds = mainFrame.getService().getMedicines().stream()
            .filter(m -> m.getStock() < 10)
            .limit(2)
            .toList();
        
        for (Medicine med : lowStockMeds) {
            activities.add(new DashboardActivity(
                "Low Stock Alert", 
                med.getName() + " (" + med.getStock() + " left)", 
                "Check inventory",
                ThemeIcons.WARNING));
        }
        
        // If we don't have enough activities, add some placeholders
        while (activities.size() < 4) {
            activities.add(new DashboardActivity(
                "Welcome", 
                "Welcome to your pharmacist dashboard", 
                "Just now",
                ThemeIcons.NOTIFICATION));
        }
        
        return activities.toArray(new DashboardActivity[0]);
    }
    
    private void loadData() {
        // Load orders
        List<Order> orders = mainFrame.getService().getOrders();
        ordersTable.setData(orders);
        
        // Load medicines
        List<Medicine> medicines = mainFrame.getService().getMedicines();
        medicinesTable.setData(medicines);
        
        // Load prescriptions
        List<Prescription> prescriptions = mainFrame.getService().getPrescriptions();
        prescriptionsTable.setData(prescriptions);
    }

    private void exportOrderHistory() {
        List<Order> orders = mainFrame.getService().getOrders();
        
        StringBuilder content = new StringBuilder();
        content.append("ORDER HISTORY\n");
        content.append("=============\n\n");
        content.append("Total Orders: ").append(orders.size()).append("\n\n");
        
        double totalRevenue = 0.0;
        for (Order order : orders) {
            content.append("Order #").append(order.getId()).append("\n");
            Patient patient = mainFrame.getService().getPatients().stream()
                .filter(p -> p.getId() == order.getPatientId())
                .findFirst()
                .orElse(null);
            content.append("Patient: ").append(patient != null ? patient.getName() : "Unknown Patient").append("\n");
            content.append("Date: ").append(order.getOrderDate()).append("\n");
            content.append("Status: ").append(order.getStatus()).append("\n");
            content.append("Total: L.E ").append(String.format("%.2f", order.calculateTotal())).append("\n");
            content.append("------------------------\n");
            totalRevenue += order.calculateTotal();
        }
        
        content.append("\nTotal Revenue: L.E ").append(String.format("%.2f", totalRevenue));
        
        utils.PDFGenerator.generatePDF("Order History", content.toString(), this);
    }

    private void exportInventoryReport() {
        List<Medicine> medicines = mainFrame.getService().getMedicines();
        
        StringBuilder content = new StringBuilder();
        content.append("INVENTORY REPORT\n");
        content.append("================\n\n");
        content.append("Total Items: ").append(medicines.size()).append("\n\n");
        
        // Count low stock items
        long lowStockCount = medicines.stream()
            .filter(m -> m.getStock() < 10)
            .count();
        content.append("Low Stock Items (< 10): ").append(lowStockCount).append("\n\n");
        
        // Calculate total inventory value
        double totalValue = medicines.stream()
            .mapToDouble(m -> m.getPrice() * m.getStock())
            .sum();
        content.append("Total Inventory Value: L.E ").append(String.format("%.2f", totalValue)).append("\n\n");
        
        content.append("INVENTORY DETAILS\n");
        content.append("=================\n\n");
        
        for (Medicine medicine : medicines) {
            content.append("Medicine ID: ").append(medicine.getId()).append("\n");
            content.append("Name: ").append(medicine.getName()).append("\n");
            content.append("Category: ").append(medicine.getCategory()).append("\n");
            content.append("Price: L.E ").append(String.format("%.2f", medicine.getPrice())).append("\n");
            content.append("Stock: ").append(medicine.getStock());
            if (medicine.getStock() < 10) {
                content.append(" (LOW STOCK)");
            }
            content.append("\n");
            content.append("Value: L.E ").append(String.format("%.2f", medicine.getPrice() * medicine.getStock())).append("\n");
            content.append("------------------------\n");
        }
        
        utils.PDFGenerator.generatePDF("Inventory Report", content.toString(), this);
    }
} 