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
import models.PrescriptionStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PharmacistDashboardPanel extends BaseDashboardPanel {
    // Make tables static to ensure they're created only once and persist
    private static StyledTable<Order> ordersTable;
    private static StyledTable<Medicine> medicinesTable;
    private static StyledTable<Prescription> prescriptionsTable;

    // Static initialization block to ensure tables are created before any instance
    static {
        try {
            // Initialize medicine table
            String[] medicineColumns = {"ID", "Name", "Stock", "Price"};
            medicinesTable = new StyledTable<Medicine>(medicineColumns, medicine -> new Object[]{
                medicine.getId(),
                medicine.getName(),
                medicine.getStock(),
                String.format("%.2f", medicine.getPrice())
            });
        
            // Initialize order table
            String[] orderColumns = {"ID", "Patient", "Date", "Status", "Total"};
            ordersTable = new StyledTable<Order>(orderColumns, order -> new Object[]{
                order.getId(),
                order.getPatientId(),
                order.getOrderDate(),
                order.getStatus(),
                String.format("%.2f", order.calculateTotal())
        });
        
            // Initialize prescription table
            String[] prescriptionColumns = {"ID", "Patient", "Doctor", "Date", "Status"};
            prescriptionsTable = new StyledTable<Prescription>(prescriptionColumns, prescription -> new Object[]{
                prescription.getId(),
                prescription.getPatientId(),
                prescription.getDoctorId(),
                prescription.getIssueDate(),
                prescription.getStatus()
            });
            
            System.out.println("Static initialization of PharmacistDashboardPanel tables completed successfully");
        } catch (Exception e) {
            System.err.println("Error in static initialization of PharmacistDashboardPanel tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public PharmacistDashboardPanel(MainFrame frame) {
        super(frame);
        
        // Let's make absolutely sure tables are initialized before anything else
        ensureTablesInitialized();
        
        System.out.println("PharmacistDashboardPanel constructor completed");
            }
    
    /**
     * Make absolutely sure tables are initialized
     */
    private void ensureTablesInitialized() {
        if (medicinesTable == null || ordersTable == null || prescriptionsTable == null) {
            System.out.println("Tables not initialized in constructor, reinitializing...");
        
            // Initialize medicine table if needed
            if (medicinesTable == null) {
                String[] medicineColumns = {"ID", "Name", "Stock", "Price"};
                medicinesTable = new StyledTable<Medicine>(medicineColumns, medicine -> new Object[]{
                    medicine.getId(),
                    medicine.getName(),
                    medicine.getStock(),
                    String.format("%.2f", medicine.getPrice())
                });
            }
            
            // Initialize order table if needed
            if (ordersTable == null) {
                String[] orderColumns = {"ID", "Patient", "Date", "Status", "Total"};
                ordersTable = new StyledTable<Order>(orderColumns, order -> new Object[]{
                    order.getId(),
                    order.getPatientId(),
                    order.getOrderDate(),
                    order.getStatus(),
                    String.format("%.2f", order.calculateTotal())
                });
            }
            
            // Initialize prescription table if needed
            if (prescriptionsTable == null) {
        String[] prescriptionColumns = {"ID", "Patient", "Doctor", "Date", "Status"};
                prescriptionsTable = new StyledTable<Prescription>(prescriptionColumns, prescription -> new Object[]{
                    prescription.getId(),
                    prescription.getPatientId(),
                    prescription.getDoctorId(),
                    prescription.getIssueDate(),
                    prescription.getStatus()
                });
            }
        }
    }
    
    @Override
    protected void initializeComponents() {
        // Do nothing here - we'll control initialization in createRoleSpecificPanel
        System.out.println("PharmacistDashboardPanel.initializeComponents called");
    }
    
    @Override
    protected JPanel createRoleSpecificPanel() {
        System.out.println("PharmacistDashboardPanel.createRoleSpecificPanel called");
        
        // Ensure tables are initialized one more time
        ensureTablesInitialized();
        
        // Verify tables are not null before proceeding
        if (medicinesTable == null || ordersTable == null || prescriptionsTable == null) {
            System.err.println("CRITICAL: Tables still null in createRoleSpecificPanel");
            // Return a panel with error message instead of crashing
            JPanel errorPanel = new JPanel(new BorderLayout());
            errorPanel.setBackground(Color.WHITE);
            JLabel errorLabel = new JLabel("Error: Tables failed to initialize. Please restart the application.");
            errorLabel.setForeground(Color.RED);
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            errorPanel.add(errorLabel, BorderLayout.CENTER);
            return errorPanel;
        }
        
        // Create a container panel
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(ThemeColors.BACKGROUND);
        
        // Create tabs for different sections
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(ThemeColors.BACKGROUND);
        tabbedPane.setForeground(ThemeColors.TEXT_PRIMARY);
        tabbedPane.setFont(ThemeFonts.REGULAR_MEDIUM);
        
        // Add inventory panel
        JPanel medicinesPanel = createInventoryPanel();
        tabbedPane.addTab("Inventory", ThemeIcons.MEDICINE, medicinesPanel);

        // Add prescriptions panel
        JPanel prescriptionsPanel = createPrescriptionsPanel();
        tabbedPane.addTab("Prescriptions", ThemeIcons.PRESCRIPTION, prescriptionsPanel);
        
        // Add orders panel
        JPanel ordersPanel = createOrdersPanel();
        tabbedPane.addTab("Orders", ThemeIcons.ORDER, ordersPanel);

        containerPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Load data after all UI components are created
        loadData();
        
        return containerPanel;
    }
    
    @Override
    protected DashboardStat[] getQuickStats() {
        // Get data from service
        int orderCount = mainFrame.getService().getOrders().size();
        int medicineCount = mainFrame.getService().getMedicines().size();
        int lowStockCount = (int) mainFrame.getService().getMedicines().stream()
            .filter(m -> m.getStock() < 10)
            .count();
        int pendingPrescriptions = (int) mainFrame.getService().getPrescriptions().stream()
            .filter(p -> p.getStatus().equals("Pending"))
            .count();
        
        // Create stats
        return new DashboardStat[] {
            new DashboardStat("Pending Prescriptions", 
                String.valueOf(pendingPrescriptions), 
                ThemeIcons.PRESCRIPTION, 
                ThemeColors.WARNING),
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
        
        // Add recent prescriptions
        List<Prescription> prescriptions = mainFrame.getService().getPrescriptions();
        if (prescriptions != null && !prescriptions.isEmpty()) {
            for (int i = 0; i < Math.min(2, prescriptions.size()); i++) {
                Prescription prescription = prescriptions.get(i);
                
                // Get patient and doctor names
                Patient patient = mainFrame.getService().getPatients().stream()
                    .filter(p -> p.getId() == prescription.getPatientId())
                    .findFirst()
                    .orElse(null);
                Doctor doctor = mainFrame.getService().getDoctors().stream()
                    .filter(d -> d.getId() == prescription.getDoctorId())
                    .findFirst()
                    .orElse(null);
                
                String patientName = patient != null ? patient.getName() : "Unknown Patient";
                String doctorName = doctor != null ? doctor.getName() : "Unknown Doctor";
                
                activities.add(new DashboardActivity(
                    "New Prescription", 
                    "For " + patientName + " from Dr. " + doctorName, 
                    prescription.getIssueDate().toString(),
                    ThemeIcons.PRESCRIPTION));
            }
        }
        
        // Add recent orders
        List<Order> orders = mainFrame.getService().getOrders();
        if (orders != null && !orders.isEmpty()) {
            for (int i = 0; i < Math.min(1, orders.size()); i++) {
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
            .limit(1)
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
    
    /**
     * Load data into the tables
     */
    private void loadData() {
        try {
            // Make sure tables are initialized before loading data
            ensureTablesInitialized();
            
            // Check if tables are still null after ensuring initialization
            if (medicinesTable == null || prescriptionsTable == null || ordersTable == null) {
                System.err.println("CRITICAL: Tables still null in loadData after ensureTablesInitialized");
                return;
            }
            
            // Load data from service
        List<Medicine> medicines = mainFrame.getService().getMedicines();
        List<Prescription> prescriptions = mainFrame.getService().getPrescriptions();
            List<Order> orders = mainFrame.getService().getOrders();
            
            // Log data counts for debugging
            System.out.println("PharmacistDashboard: Loaded " + medicines.size() + " medicines");
            System.out.println("PharmacistDashboard: Loaded " + prescriptions.size() + " prescriptions");
            System.out.println("PharmacistDashboard: Loaded " + orders.size() + " orders");
            
            // Set data to tables
            medicinesTable.setData(medicines);
        prescriptionsTable.setData(prescriptions);
            ordersTable.setData(orders);
        } catch (Exception e) {
            System.err.println("Error loading pharmacist dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void refreshData() {
        loadData();
    }

    private void exportOrderHistory() {
        List<Order> orders = mainFrame.getService().getOrders();
        
        StringBuilder content = new StringBuilder();
        content.append("ORDER HISTORY REPORT\n");
        content.append("===================\n\n");
        content.append("Total Orders: ").append(orders.size()).append("\n\n");
        
        // Count orders by status
        long pendingCount = orders.stream()
            .filter(o -> o.getStatus() == Order.Status.PENDING)
            .count();
        long completedCount = orders.stream()
            .filter(o -> o.getStatus() == Order.Status.COMPLETED)
            .count();
        
        content.append("Status Summary:\n");
        content.append("- Pending: ").append(pendingCount).append("\n");
        content.append("- Completed: ").append(completedCount).append("\n\n");
        
        // Calculate total revenue
        double totalRevenue = 0.0;
        for (Order order : orders) {
            if (order.getStatus() == Order.Status.COMPLETED) {
                totalRevenue += order.calculateTotal();
            }
        }
        content.append("Total Revenue: L.E ").append(String.format("%.2f", totalRevenue)).append("\n\n");
        
        content.append("ORDER DETAILS\n");
        content.append("============\n\n");
        
        for (Order order : orders) {
            content.append("Order #").append(order.getId()).append("\n");
            Patient patient = mainFrame.getService().getPatients().stream()
                .filter(p -> p.getId() == order.getPatientId())
                .findFirst()
                .orElse(null);
            content.append("Patient: ").append(patient != null ? patient.getName() : "Unknown Patient").append("\n");
            content.append("Date: ").append(order.getOrderDate()).append("\n");
            content.append("Status: ").append(order.getStatus().getDisplayName()).append("\n");
            
            // Include order items
            content.append("Items:\n");
            for (Map.Entry<Medicine, Integer> entry : order.getMedicines().entrySet()) {
                Medicine medicine = entry.getKey();
                int quantity = entry.getValue();
                double itemTotal = medicine.getPrice() * quantity;
                
                content.append("  - ").append(medicine.getName())
                    .append(" (").append(quantity).append(" × L.E ")
                    .append(String.format("%.2f", medicine.getPrice())).append(")")
                    .append(" = L.E ").append(String.format("%.2f", itemTotal))
                    .append("\n");
            }
            
            content.append("Total: L.E ").append(String.format("%.2f", order.calculateTotal())).append("\n");
            content.append("------------------------\n");
        }
        
        utils.PDFGenerator.generatePDF("Order History", content.toString(), this);
        utils.FileHandler.saveOrders(mainFrame.getService().getOrders());
        refreshData();
        
        JOptionPane.showMessageDialog(this,
            "Order history report has been exported successfully",
            "Export Complete",
            JOptionPane.INFORMATION_MESSAGE);
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
        utils.FileHandler.saveMedicines(mainFrame.getService().getMedicines());
        refreshData();
    }

    public void exportPrescriptionReport() {
        List<Prescription> prescriptions = mainFrame.getService().getPrescriptions();
        
        StringBuilder content = new StringBuilder();
        content.append("PRESCRIPTION REPORT\n");
        content.append("===================\n\n");
        content.append("Total Prescriptions: ").append(prescriptions.size()).append("\n\n");
        
        // Count prescriptions by status
        long pendingCount = prescriptions.stream()
            .filter(p -> p.getStatus().equals("Pending"))
            .count();
        long completedCount = prescriptions.stream()
            .filter(p -> p.getStatus().equals("Completed"))
            .count();
        
        content.append("Status Summary:\n");
        content.append("- Pending: ").append(pendingCount).append("\n");
        content.append("- Completed: ").append(completedCount).append("\n\n");
        
        content.append("PRESCRIPTION DETAILS\n");
        content.append("====================\n\n");
        
        for (Prescription prescription : prescriptions) {
            content.append("Prescription ID: ").append(prescription.getId()).append("\n");
            
            // Get patient and doctor names
            Patient patient = mainFrame.getService().getPatients().stream()
                .filter(p -> p.getId() == prescription.getPatientId())
                .findFirst()
                .orElse(null);
            Doctor doctor = mainFrame.getService().getDoctors().stream()
                .filter(d -> d.getId() == prescription.getDoctorId())
                .findFirst()
                .orElse(null);
                
            content.append("Patient: ").append(patient != null ? patient.getName() : "Unknown Patient").append("\n");
            content.append("Doctor: ").append(doctor != null ? doctor.getName() : "Unknown Doctor").append("\n");
            content.append("Issue Date: ").append(prescription.getIssueDate()).append("\n");
            content.append("Status: ").append(prescription.getStatus().getDisplayName()).append("\n");
            content.append("Medicines:\n");
            
            for (Map.Entry<Medicine, Integer> entry : prescription.getMedicines().entrySet()) {
                content.append("- ").append(entry.getKey().getName())
                    .append(" (Qty: ").append(entry.getValue()).append(")\n");
            }
            content.append("------------------------\n");
        }
        
        utils.PDFGenerator.generatePDF("Prescription Report", content.toString(), this);
        utils.FileHandler.savePrescriptions(mainFrame.getService().getPrescriptions());
        refreshData();
    }

    private JPanel createOrdersPanel() {
        // Orders Tab
        JPanel ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.setBackground(ThemeColors.BACKGROUND);
        
        // Add table to scroll pane
        JScrollPane ordersScrollPane = new JScrollPane(ordersTable);
        ordersScrollPane.setBackground(ThemeColors.BACKGROUND);
        ordersPanel.add(ordersScrollPane, BorderLayout.CENTER);
        
        // Add buttons panel for orders
        JPanel orderButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        orderButtonsPanel.setBackground(ThemeColors.BACKGROUND);
        
        StyledButton viewOrderButton = new StyledButton("View Order", ThemeIcons.VIEW);
        viewOrderButton.addActionListener(e -> {
            Order selectedOrder = ordersTable.getSelectedItem();
            if (selectedOrder != null) {
                showOrderDetailsDialog(selectedOrder);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select an order to view",
                    "Selection Required",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        StyledButton processOrderButton = new StyledButton("Process Order", ThemeIcons.PROCESS);
        processOrderButton.addActionListener(e -> {
            Order selectedOrder = ordersTable.getSelectedItem();
            if (selectedOrder != null) {
                processOrder(selectedOrder);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select an order to process",
                    "Selection Required",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        StyledButton exportOrdersButton = new StyledButton("Export History", ThemeIcons.EXPORT);
        exportOrdersButton.addActionListener(e -> exportOrderHistory());
        
        orderButtonsPanel.add(exportOrdersButton);
        orderButtonsPanel.add(viewOrderButton);
        orderButtonsPanel.add(processOrderButton);
        ordersPanel.add(orderButtonsPanel, BorderLayout.SOUTH);
        
        return ordersPanel;
    }

    /**
     * Display detailed order information in a dialog
     * 
     * @param order The order to display
     */
    private void showOrderDetailsDialog(Order order) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Get patient information
        Patient patient = mainFrame.getService().getPatients().stream()
            .filter(p -> p.getId() == order.getPatientId())
            .findFirst()
            .orElse(null);
            
        String patientName = patient != null ? patient.getName() : "Unknown Patient";
        
        // Add order details
        JLabel titleLabel = new JLabel("Order #" + order.getId());
        titleLabel.setFont(ThemeFonts.BOLD_LARGE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel patientLabel = new JLabel("Patient: " + patientName);
        patientLabel.setFont(ThemeFonts.REGULAR_MEDIUM);
        patientLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel dateLabel = new JLabel("Order Date: " + order.getOrderDate());
        dateLabel.setFont(ThemeFonts.REGULAR_MEDIUM);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel statusLabel = new JLabel("Status: " + order.getStatus().getDisplayName());
        statusLabel.setFont(ThemeFonts.BOLD_MEDIUM);
        statusLabel.setForeground(order.getStatus() == Order.Status.COMPLETED ? 
                              ThemeColors.SUCCESS : ThemeColors.WARNING);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(patientLabel);
        panel.add(dateLabel);
        panel.add(statusLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Add medicines list
        JLabel itemsLabel = new JLabel("Order Items:");
        itemsLabel.setFont(ThemeFonts.BOLD_MEDIUM);
        itemsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(itemsLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Create a table for order items
        String[] columns = {"Medicine", "Quantity", "Price", "Total"};
        Object[][] data = new Object[order.getMedicines().size()][4];
        
        int i = 0;
        double totalCost = 0;
        for (Map.Entry<Medicine, Integer> entry : order.getMedicines().entrySet()) {
            Medicine medicine = entry.getKey();
            int quantity = entry.getValue();
            double itemPrice = medicine.getPrice();
            double total = itemPrice * quantity;
            totalCost += total;
            
            data[i][0] = medicine.getName();
            data[i][1] = quantity;
            data[i][2] = String.format("L.E %.2f", itemPrice);
            data[i][3] = String.format("L.E %.2f", total);
            i++;
        }
        
        gui.components.StyledTable<Object> itemsTable = new gui.components.StyledTable<>(new javax.swing.table.DefaultTableModel(data, columns));
        itemsTable.setEnabled(false);
        itemsTable.setPreferredScrollableViewportSize(new Dimension(400, 100));
        
        JScrollPane tableScrollPane = new JScrollPane(itemsTable);
        tableScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(tableScrollPane);
        
        // Add total cost
        JLabel totalLabel = new JLabel("Total Amount: L.E " + String.format("%.2f", totalCost));
        totalLabel.setFont(ThemeFonts.BOLD_MEDIUM);
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(totalLabel);
        
        // Add shipping information if available
        if (order.getPatientAddress() != null && !order.getPatientAddress().isEmpty()) {
            JLabel shippingLabel = new JLabel("Delivery Address: " + order.getPatientAddress());
            shippingLabel.setFont(ThemeFonts.REGULAR_MEDIUM);
            shippingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(shippingLabel);
        }
        
        // Add notes if available
        if (order.getNotes() != null && !order.getNotes().isEmpty()) {
            JLabel notesLabel = new JLabel("Notes: " + order.getNotes());
            notesLabel.setFont(ThemeFonts.REGULAR_MEDIUM);
            notesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(notesLabel);
        }
        
        // Show dialog
        JOptionPane.showMessageDialog(
            this,
            panel,
            "Order Details",
            JOptionPane.PLAIN_MESSAGE
        );
    }
    
    /**
     * Process an order - update status and adjust inventory
     * 
     * @param order The order to process
     */
    private void processOrder(Order order) {
        // Check if already completed
        if (order.getStatus() == Order.Status.COMPLETED) {
            JOptionPane.showMessageDialog(this,
                "This order has already been processed",
                "Already Completed",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Check if we have enough stock for all items
        boolean sufficientStock = true;
        StringBuilder insufficientItems = new StringBuilder();
        
        for (Map.Entry<Medicine, Integer> entry : order.getMedicines().entrySet()) {
            Medicine medicine = entry.getKey();
            int requiredQuantity = entry.getValue();
            
            if (medicine.getStock() < requiredQuantity) {
                sufficientStock = false;
                insufficientItems.append("- ").append(medicine.getName())
                    .append(" (Need: ").append(requiredQuantity)
                    .append(", Available: ").append(medicine.getStock())
                    .append(")\n");
            }
        }
        
        // If insufficient stock, show error
        if (!sufficientStock) {
            JOptionPane.showMessageDialog(this,
                "Insufficient stock for the following items:\n" + insufficientItems.toString(),
                "Stock Issue",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirm processing
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to process this order? This will reduce stock for the medicines.",
            "Confirm Processing",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Update medicine stocks
        for (Map.Entry<Medicine, Integer> entry : order.getMedicines().entrySet()) {
            Medicine medicine = entry.getKey();
            int quantity = entry.getValue();
            medicine.setStock(medicine.getStock() - quantity);
        }
        
        // Update order status
        order.setStatus(Order.Status.COMPLETED);
        
        // Save changes
        mainFrame.getService().saveDataToFiles();
        
        // Refresh the tables
        refreshData();
        
        JOptionPane.showMessageDialog(this,
            "Order #" + order.getId() + " has been processed successfully",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.BACKGROUND);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(medicinesTable);
        scrollPane.setBackground(ThemeColors.BACKGROUND);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(ThemeColors.BACKGROUND);
        
        StyledButton addButton = new StyledButton("Add Medicine", ThemeIcons.ADD);
        addButton.addActionListener(e -> showAddMedicineDialog());
        
        StyledButton updateButton = new StyledButton("Update Stock", ThemeIcons.EDIT);
        updateButton.addActionListener(e -> {
            Medicine selectedMedicine = medicinesTable.getSelectedItem();
            if (selectedMedicine != null) {
                showUpdateStockDialog(selectedMedicine);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select a medicine to update", 
                    "Selection Required", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        StyledButton exportButton = new StyledButton("Export Report", ThemeIcons.EXPORT);
        exportButton.addActionListener(e -> exportInventoryReport());
        
        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(exportButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Show dialog to add a new medicine
     */
    private void showAddMedicineDialog() {
        // Create panel with form fields
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JTextField nameField = new JTextField(20);
        JTextField descriptionField = new JTextField(20);
        JTextField manufacturerField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField stockField = new JTextField(20);
        JTextField categoryField = new JTextField(20);
        JCheckBox prescriptionCheckbox = new JCheckBox();
        
        // Add components to panel
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Manufacturer:"));
        panel.add(manufacturerField);
        panel.add(new JLabel("Price (L.E):"));
        panel.add(priceField);
        panel.add(new JLabel("Stock Quantity:"));
        panel.add(stockField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Requires Prescription:"));
        panel.add(prescriptionCheckbox);
        
        // Set up the dialog
        int result = JOptionPane.showConfirmDialog(
            this, 
            panel, 
            "Add New Medicine", 
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE
        );
        
        // Process the result
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Get values from fields
                String name = nameField.getText().trim();
                String description = descriptionField.getText().trim();
                String manufacturer = manufacturerField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int stock = Integer.parseInt(stockField.getText().trim());
                String category = categoryField.getText().trim();
                boolean requiresPrescription = prescriptionCheckbox.isSelected();
                
                // Validate inputs
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Name cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (price < 0) {
                    JOptionPane.showMessageDialog(this, "Price cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (stock < 0) {
                    JOptionPane.showMessageDialog(this, "Stock cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create new medicine with temporary ID (will be assigned by service)
                Medicine newMedicine = new Medicine(
                    0, // Temporary ID
                    name,
                    description,
                    manufacturer,
                    price,
                    stock,
                    category,
                    requiresPrescription
                );
                
                try {
                    // Get the current medicines list
                    List<Medicine> medicines = mainFrame.getService().getMedicines();
                    
                    // Assign a new unique ID 
                    int maxId = 0;
                    for (Medicine m : medicines) {
                        if (m.getId() > maxId) {
                            maxId = m.getId();
                        }
                    }
                    newMedicine.setId(maxId + 1);
                    
                    // Add to the list
                    medicines.add(newMedicine);
                    
                    // Save and refresh
                    mainFrame.getService().saveDataToFiles();
                    refreshData();
                    
                    JOptionPane.showMessageDialog(this, 
                        "Medicine added successfully", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to add medicine: " + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter valid numbers for price and stock", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error adding medicine: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Show dialog to update medicine stock
     * 
     * @param medicine The medicine to update
     */
    private void showUpdateStockDialog(Medicine medicine) {
        // Create panel with form fields
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Display current information
        JLabel nameLabel = new JLabel(medicine.getName());
        JLabel currentStockLabel = new JLabel(String.valueOf(medicine.getStock()));
        JTextField newStockField = new JTextField(10);
        
        // Add fields for update options
        JRadioButton setButton = new JRadioButton("Set to specific value");
        JRadioButton addButton = new JRadioButton("Add to current stock");
        ButtonGroup group = new ButtonGroup();
        group.add(setButton);
        group.add(addButton);
        setButton.setSelected(true);
        
        // Add components to panel
        panel.add(new JLabel("Medicine:"));
        panel.add(nameLabel);
        panel.add(new JLabel("Current Stock:"));
        panel.add(currentStockLabel);
        panel.add(setButton);
        panel.add(new JLabel()); // Empty cell for alignment
        panel.add(addButton);
        panel.add(new JLabel()); // Empty cell for alignment
        panel.add(new JLabel("New Stock:"));
        panel.add(newStockField);
        
        // Set up the dialog
        int result = JOptionPane.showConfirmDialog(
            this, 
            panel, 
            "Update Stock for " + medicine.getName(), 
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE
        );
        
        // Process the result
        if (result == JOptionPane.OK_OPTION) {
            try {
                int currentStock = medicine.getStock();
                int newStock;
                int stockValue = Integer.parseInt(newStockField.getText().trim());
                
                if (setButton.isSelected()) {
                    // Set to specific value
                    newStock = stockValue;
                } else {
                    // Add to current value
                    newStock = currentStock + stockValue;
                }
                
                // Validate new stock
                if (newStock < 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Stock cannot be negative", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update medicine
                medicine.setStock(newStock);
                
                // Save changes - no need for a separate saveMedicine call
                mainFrame.getService().saveDataToFiles();
                
                JOptionPane.showMessageDialog(this, 
                    "Stock updated successfully", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh display
                refreshData();
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid number for stock", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error updating stock: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createPrescriptionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.BACKGROUND);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(prescriptionsTable);
        scrollPane.setBackground(ThemeColors.BACKGROUND);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(ThemeColors.BACKGROUND);
        
        StyledButton viewButton = new StyledButton("View Details", ThemeIcons.VIEW);
        viewButton.addActionListener(e -> {
            Prescription selectedPrescription = prescriptionsTable.getSelectedItem();
            if (selectedPrescription != null) {
                showPrescriptionDetailsDialog(selectedPrescription);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select a prescription to view",
                    "Selection Required",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        StyledButton processButton = new StyledButton("Process", ThemeIcons.PROCESS);
        processButton.addActionListener(e -> {
            Prescription selectedPrescription = prescriptionsTable.getSelectedItem();
            if (selectedPrescription != null) {
                processPrescription(selectedPrescription);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select a prescription to process",
                    "Selection Required",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        StyledButton convertButton = new StyledButton("Convert to Order", ThemeIcons.ORDER);
        convertButton.addActionListener(e -> {
            Prescription selectedPrescription = prescriptionsTable.getSelectedItem();
            if (selectedPrescription != null) {
                convertPrescriptionToOrder(selectedPrescription);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select a prescription to convert",
                    "Selection Required",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        StyledButton exportButton = new StyledButton("Export Report", ThemeIcons.EXPORT);
        exportButton.addActionListener(e -> exportPrescriptionReport());
        
        buttonsPanel.add(viewButton);
        buttonsPanel.add(processButton);
        buttonsPanel.add(convertButton);
        buttonsPanel.add(exportButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Convert a prescription to an order
     *
     * @param prescription The prescription to convert
     */
    private void convertPrescriptionToOrder(Prescription prescription) {
        // Check if prescription is already completed
        if (prescription.getStatus() == PrescriptionStatus.COMPLETED) {
            JOptionPane.showMessageDialog(this,
                "This prescription has already been processed",
                "Already Processed",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Check if we have enough stock
        boolean sufficientStock = true;
        StringBuilder insufficientItems = new StringBuilder();
        
        for (Map.Entry<Medicine, Integer> entry : prescription.getMedicines().entrySet()) {
            Medicine medicine = entry.getKey();
            int requiredQuantity = entry.getValue();
            
            if (medicine.getStock() < requiredQuantity) {
                sufficientStock = false;
                insufficientItems.append("- ").append(medicine.getName())
                    .append(" (Need: ").append(requiredQuantity)
                    .append(", Available: ").append(medicine.getStock())
                    .append(")\n");
            }
        }
        
        // If insufficient stock, show error
        if (!sufficientStock) {
            JOptionPane.showMessageDialog(this,
                "Insufficient stock for the following items:\n" + insufficientItems.toString(),
                "Stock Issue",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get patient
        Patient patient = mainFrame.getService().getPatients().stream()
            .filter(p -> p.getId() == prescription.getPatientId())
            .findFirst()
            .orElse(null);
            
        if (patient == null) {
            JOptionPane.showMessageDialog(this,
                "Could not find the patient for this prescription",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create new order
        int newOrderId = mainFrame.getService().generateOrderId();
        Order newOrder = new Order(newOrderId, prescription.getPatientId());
        
        // Add prescription medicines to order
        for (Map.Entry<Medicine, Integer> entry : prescription.getMedicines().entrySet()) {
            Medicine medicine = entry.getKey();
            int quantity = entry.getValue();
            newOrder.addMedicine(medicine, quantity);
        }
        
        // Add order reference to prescription
        prescription.setInstructions("Converted to Order #" + newOrderId);
        
        // Update prescription status
        prescription.setStatus(PrescriptionStatus.COMPLETED);
        
        // Add order to service
        List<Order> orders = mainFrame.getService().getOrders();
        orders.add(newOrder);
        
        // Save changes
        mainFrame.getService().saveDataToFiles();
        
        // Refresh the tables
        refreshData();
        
        // Show success message
        JOptionPane.showMessageDialog(this,
            "Prescription #" + prescription.getId() + " has been converted to Order #" + newOrderId,
            "Conversion Complete",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show details of a prescription in a dialog
     * 
     * @param prescription The prescription to show
     */
    private void showPrescriptionDetailsDialog(Prescription prescription) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Get patient and doctor information
        Patient patient = mainFrame.getService().getPatients().stream()
            .filter(p -> p.getId() == prescription.getPatientId())
            .findFirst()
            .orElse(null);
            
        Doctor doctor = mainFrame.getService().getDoctors().stream()
            .filter(d -> d.getId() == prescription.getDoctorId())
            .findFirst()
            .orElse(null);
        
        String patientName = patient != null ? patient.getName() : "Unknown Patient";
        String doctorName = doctor != null ? doctor.getName() : "Unknown Doctor";
        
        // Add prescription details
        JLabel titleLabel = new JLabel("Prescription #" + prescription.getId());
        titleLabel.setFont(ThemeFonts.BOLD_LARGE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel patientLabel = new JLabel("Patient: " + patientName);
        patientLabel.setFont(ThemeFonts.REGULAR_MEDIUM);
        patientLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel doctorLabel = new JLabel("Doctor: " + doctorName);
        doctorLabel.setFont(ThemeFonts.REGULAR_MEDIUM);
        doctorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel dateLabel = new JLabel("Issue Date: " + prescription.getIssueDate());
        dateLabel.setFont(ThemeFonts.REGULAR_MEDIUM);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel statusLabel = new JLabel("Status: " + prescription.getStatus().getDisplayName());
        statusLabel.setFont(ThemeFonts.BOLD_MEDIUM);
        statusLabel.setForeground(PrescriptionStatus.COMPLETED.equals(prescription.getStatus()) ? 
                                ThemeColors.SUCCESS : ThemeColors.WARNING);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(patientLabel);
        panel.add(doctorLabel);
        panel.add(dateLabel);
        panel.add(statusLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Add medicines list
        JLabel medicinesLabel = new JLabel("Prescribed Medicines:");
        medicinesLabel.setFont(ThemeFonts.BOLD_MEDIUM);
        medicinesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(medicinesLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Create a table for medicines
        String[] columns = {"Name", "Quantity", "Price", "Total", "In Stock"};
        Object[][] data = new Object[prescription.getMedicines().size()][5];
        
        int i = 0;
        double totalCost = 0;
        for (Map.Entry<Medicine, Integer> entry : prescription.getMedicines().entrySet()) {
            Medicine medicine = entry.getKey();
            int quantity = entry.getValue();
            double total = medicine.getPrice() * quantity;
            totalCost += total;
            
            data[i][0] = medicine.getName();
            data[i][1] = quantity;
            data[i][2] = String.format("L.E %.2f", medicine.getPrice());
            data[i][3] = String.format("L.E %.2f", total);
            
            // Check if enough stock is available
            boolean hasStock = medicine.getStock() >= quantity;
            data[i][4] = hasStock ? "Yes (" + medicine.getStock() + ")" : "No (" + medicine.getStock() + ")";
            
            i++;
        }
        
        gui.components.StyledTable<Object> medicinesTable = new gui.components.StyledTable<>(new javax.swing.table.DefaultTableModel(data, columns));
        medicinesTable.setEnabled(false);
        medicinesTable.setPreferredScrollableViewportSize(new Dimension(500, 100));
        
        JScrollPane tableScrollPane = new JScrollPane(medicinesTable);
        tableScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(tableScrollPane);
        
        // Add total cost
        JLabel totalLabel = new JLabel("Total Cost: L.E " + String.format("%.2f", totalCost));
        totalLabel.setFont(ThemeFonts.BOLD_MEDIUM);
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(totalLabel);
        
        // Add instructions if available
        if (prescription.getInstructions() != null && !prescription.getInstructions().isEmpty()) {
            JLabel instructionsLabel = new JLabel("Instructions: " + prescription.getInstructions());
            instructionsLabel.setFont(ThemeFonts.REGULAR_MEDIUM);
            instructionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(instructionsLabel);
        }
        
        // Add patient contact information if available
        if (patient != null && patient.getPhoneNumber() != null && !patient.getPhoneNumber().isEmpty()) {
            JLabel phoneLabel = new JLabel("Patient Phone: " + patient.getPhoneNumber());
            phoneLabel.setFont(ThemeFonts.REGULAR_MEDIUM);
            phoneLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(phoneLabel);
        }
        
        // Show dialog
        JOptionPane.showMessageDialog(
            this,
            panel,
            "Prescription Details",
            JOptionPane.PLAIN_MESSAGE
        );
    }
    
    /**
     * Process a prescription by updating its status and adjusting stock
     * 
     * @param prescription The prescription to process
     */
    private void processPrescription(Prescription prescription) {
        // Check if already completed
        if (PrescriptionStatus.COMPLETED.equals(prescription.getStatus())) {
            JOptionPane.showMessageDialog(this,
                "This prescription has already been processed",
                "Already Completed",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Check if we have enough stock
        boolean sufficientStock = true;
        StringBuilder insufficientItems = new StringBuilder();
        
        for (Map.Entry<Medicine, Integer> entry : prescription.getMedicines().entrySet()) {
            Medicine medicine = entry.getKey();
            int requiredQuantity = entry.getValue();
            
            if (medicine.getStock() < requiredQuantity) {
                sufficientStock = false;
                insufficientItems.append("- ").append(medicine.getName())
                    .append(" (Need: ").append(requiredQuantity)
                    .append(", Available: ").append(medicine.getStock())
                    .append(")\n");
            }
        }
        
        // If insufficient stock, show error
        if (!sufficientStock) {
            JOptionPane.showMessageDialog(this,
                "Insufficient stock for the following items:\n" + insufficientItems.toString(),
                "Stock Issue",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirm processing
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to process this prescription? This will reduce stock for the medicines.",
            "Confirm Processing",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Choose processing method
        String[] options = {"Process Only", "Convert to Order"};
        int choice = JOptionPane.showOptionDialog(this,
            "How would you like to process this prescription?",
            "Processing Method",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
            
        if (choice == 1) {
            // Convert to order
            convertPrescriptionToOrder(prescription);
            return;
        }
        
        // Update medicine stocks
        for (Map.Entry<Medicine, Integer> entry : prescription.getMedicines().entrySet()) {
            Medicine medicine = entry.getKey();
            int quantity = entry.getValue();
            medicine.setStock(medicine.getStock() - quantity);
        }
        
        // Update prescription status
        prescription.setStatus(PrescriptionStatus.COMPLETED);
        
        // Save changes
        mainFrame.getService().saveDataToFiles();
        
        // Refresh the tables
        refreshData();
        
        JOptionPane.showMessageDialog(this,
            "Prescription #" + prescription.getId() + " has been processed successfully",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }
} 