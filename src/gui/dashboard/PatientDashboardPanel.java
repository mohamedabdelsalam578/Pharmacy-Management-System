package gui.dashboard;

import gui.MainFrame;
import gui.admin.BaseDashboardPanel;
import gui.components.StyledButton;
import gui.components.StyledTable;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeIcons;
import gui.theme.ThemeSizes;
import models.Prescription;
import models.Order;
import models.Medicine;
import models.Doctor;
import models.Patient;
import models.OrderItem;
import gui.dashboard.ShoppingCartPanel;
import models.Wallet;
import gui.dashboard.PatientConsultationsPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class PatientDashboardPanel extends BaseDashboardPanel {
    private StyledTable<Prescription> prescriptionsTable;
    private StyledTable<Order> ordersTable;
    private StyledTable<Medicine> medicinesTable;
    private ShoppingCartPanel cartPanel;
    private StyledTable<Wallet.Transaction> transactionsTable;
    private JLabel balanceLabel;

    public PatientDashboardPanel(MainFrame frame) {
        super(frame);
    }
    
    @Override
    protected void initializeComponents() {
        // BaseDashboardPanel already handles the initialization
    }
    
    @Override
    protected JPanel createRoleSpecificPanel() {
        // Get current patient for later use
        Patient currentPatient = (Patient) mainFrame.getCurrentUser();
        
        // Create tabs for different sections
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(ThemeColors.BACKGROUND);
        tabbedPane.setForeground(ThemeColors.TEXT_PRIMARY);
        tabbedPane.setFont(ThemeFonts.REGULAR_MEDIUM);

        // Profile Tab
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBackground(ThemeColors.BACKGROUND);
        GridBagConstraints gbcProfile = new GridBagConstraints();
        gbcProfile.insets = new Insets(5,5,5,5);
        gbcProfile.anchor = GridBagConstraints.WEST;

        String[][] infoPairs = {
            {"Name", currentPatient.getName()},
            {"Username", currentPatient.getUsername()},
            {"Email", currentPatient.getEmail()},
            {"Phone", currentPatient.getPhoneNumber()},
            {"Address", currentPatient.getAddress()},
            {"Wallet Balance", String.format("L.E %.2f", currentPatient.getWallet().getBalance())}
        };
        int idx=0;
        for(String[] pair:infoPairs){
            gbcProfile.gridx=0; gbcProfile.gridy=idx;
            JLabel key = new JLabel(pair[0]+":");
            key.setFont(ThemeFonts.BOLD_MEDIUM);
            profilePanel.add(key, gbcProfile);

            gbcProfile.gridx=1;
            JLabel val = new JLabel(pair[1]);
            val.setFont(ThemeFonts.REGULAR_MEDIUM);
            profilePanel.add(val, gbcProfile);
            idx++;
        }


        // Prescriptions Tab
        JPanel prescriptionsPanel = new JPanel(new BorderLayout());
        prescriptionsPanel.setBackground(ThemeColors.BACKGROUND);
        
        // Create prescriptions table
        String[] prescriptionColumns = {"ID", "Doctor", "Date", "Status"};
        prescriptionsTable = new StyledTable<Prescription>(prescriptionColumns, prescription -> {
            Object[] row = new Object[4];
            row[0] = prescription.getId();
            // Get doctor name through service
            Doctor doctor = mainFrame.getService().getDoctors().stream()
                .filter(d -> d.getId() == prescription.getDoctorId())
                .findFirst()
                .orElse(null);
            row[1] = doctor != null ? doctor.getName() : "Unknown Doctor";
            row[2] = prescription.getIssueDate().toString();
            row[3] = prescription.getStatus();
            return row;
        });
        
        // Add table to scroll pane
        JScrollPane prescriptionsScrollPane = new JScrollPane(prescriptionsTable);
        prescriptionsScrollPane.setBackground(ThemeColors.BACKGROUND);
        prescriptionsPanel.add(prescriptionsScrollPane, BorderLayout.CENTER);
        
        // Add buttons panel
        JPanel prescriptionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        prescriptionButtonsPanel.setBackground(ThemeColors.BACKGROUND);
        
        StyledButton viewPrescriptionButton = new StyledButton("View Prescription");
        viewPrescriptionButton.addActionListener(e -> {
            Prescription selected = prescriptionsTable.getSelectedItem();
            if (selected != null) {
                mainFrame.navigateTo("PRESCRIPTION_DETAILS", selected);
            }
        });
        
        StyledButton exportPrescriptionsButton = new StyledButton("Export History", ThemeIcons.EXPORT);
        exportPrescriptionsButton.addActionListener(e -> exportPrescriptionHistory());
        
        prescriptionButtonsPanel.add(exportPrescriptionsButton);
        prescriptionButtonsPanel.add(viewPrescriptionButton);
        prescriptionsPanel.add(prescriptionButtonsPanel, BorderLayout.SOUTH);
        
        // Orders Tab
        JPanel ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.setBackground(ThemeColors.BACKGROUND);
        
        // Create orders table
        String[] orderColumns = {"ID", "Date", "Status", "Total Amount"};
        ordersTable = new StyledTable<Order>(orderColumns, order -> {
            Object[] row = new Object[4];
            row[0] = order.getId();
            row[1] = order.getOrderDate().toString();
            row[2] = order.getStatus();
            row[3] = String.format("L.E %.2f", order.calculateTotal());
            return row;
        });
        
        // Add table to scroll pane
        JScrollPane ordersScrollPane = new JScrollPane(ordersTable);
        ordersScrollPane.setBackground(ThemeColors.BACKGROUND);
        ordersPanel.add(ordersScrollPane, BorderLayout.CENTER);
        
        // Add buttons panel
        JPanel orderButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        orderButtonsPanel.setBackground(ThemeColors.BACKGROUND);
        
        StyledButton viewOrderButton = new StyledButton("View Order");
        viewOrderButton.addActionListener(e -> {
            Order selected = ordersTable.getSelectedItem();
            if (selected != null) {
                mainFrame.navigateTo("ORDER_DETAILS", selected);
            }
        });
        
        StyledButton exportOrdersButton = new StyledButton("Export History", ThemeIcons.EXPORT);
        exportOrdersButton.addActionListener(e -> exportOrderHistory());
        
        orderButtonsPanel.add(exportOrdersButton);
        orderButtonsPanel.add(viewOrderButton);
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
        
        // Add order button
        StyledButton addToCartButton = new StyledButton("Add to Cart", ThemeIcons.ADD);
        addToCartButton.addActionListener(e -> {
            Medicine selected = medicinesTable.getSelectedItem();
            if (selected != null) {
                // Get current patient's cart order, create if needed
                Order cartOrder = currentPatient.getCartOrder();
                if (cartOrder == null) {
                    System.out.println("Creating new cart for patient");
                    cartOrder = new Order(0, currentPatient.getId());
                    currentPatient.setCartOrder(cartOrder);
                }
                
                // Add the selected medicine
                cartOrder.addItem(new OrderItem(selected, 1));
                
                // Force refresh cart panel UI
                if (cartPanel != null) {
                    System.out.println("Refreshing cart panel with new item: " + selected.getName());
                    cartPanel.refreshData();
                    
                    // Select the cart tab to show the addition
                    int cartTabIndex = 3; // Usually the 4th tab
                    JTabbedPane parentTabbedPane = (JTabbedPane) cartPanel.getParent();
                    if (parentTabbedPane != null) {
                        parentTabbedPane.setSelectedIndex(cartTabIndex);
                    }
                }
                
                // Show success message with details
                String message = selected.getName() + " added to cart.\n" + 
                                "Price: L.E " + selected.getPrice() + 
                                "\nQuantity: 1";
                JOptionPane.showMessageDialog(this, 
                    message,
                    "Added to Cart",
                    JOptionPane.INFORMATION_MESSAGE,
                    ThemeIcons.SUCCESS);
            } else {
                // User didn't select a medicine first
                JOptionPane.showMessageDialog(this,
                    "Please select a medicine from the list first.",
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        medicinesPanel.add(addToCartButton, BorderLayout.SOUTH);
        
        // Add tabs to tabbed pane
        tabbedPane.addTab("Medicines", medicinesPanel);
        tabbedPane.addTab("Prescriptions", prescriptionsPanel);
        tabbedPane.addTab("Consultations", new PatientConsultationsPanel(mainFrame));
        tabbedPane.addTab("Orders", ordersPanel);
        
        // Wallet Tab
        JPanel walletPanel = new JPanel(new BorderLayout());
        walletPanel.setBackground(ThemeColors.BACKGROUND);

        // Balance section at top
        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        balancePanel.setBackground(ThemeColors.SURFACE);
        balancePanel.setBorder(new CompoundBorder(new LineBorder(ThemeColors.BORDER,1),new EmptyBorder(10,15,10,15)));
        balanceLabel = new JLabel();
        balanceLabel.setFont(ThemeFonts.BOLD_LARGE);
        balancePanel.add(balanceLabel);
        walletPanel.add(balancePanel, BorderLayout.NORTH);

        // Transactions table
        String[] txnCols = {"ID","Type","Amount","Desc","Date"};
        transactionsTable = new StyledTable<>(txnCols, txn -> new Object[]{
            txn.getId().substring(0,6)+"…",
            txn.getType(),
            String.format("L.E %.2f", txn.getAmount()),
            txn.getDescription(),
            txn.getTimestamp()
        });
        walletPanel.add(new JScrollPane(transactionsTable), BorderLayout.CENTER);

        // Deposit button at bottom
        StyledButton depositBtn = new StyledButton("Add Funds", ThemeIcons.ADD);
        depositBtn.addActionListener(e->{
            String input = JOptionPane.showInputDialog(this, "Enter amount to deposit:", "Add Funds", JOptionPane.PLAIN_MESSAGE);
            if(input==null) return;
            try{
                double amt = Double.parseDouble(input);
                if(amt<=0) throw new NumberFormatException();
                currentPatient.getWallet().deposit(amt,"User deposit");
                mainFrame.getService().saveDataToFiles();
                refreshWallet();
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this,"Invalid amount","Error",JOptionPane.ERROR_MESSAGE);
            }
        });
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(ThemeColors.SURFACE);
        btnPanel.add(depositBtn);
        walletPanel.add(btnPanel, BorderLayout.SOUTH);

        // Create Shopping Cart tab LAST to ensure cartOrder exists
        cartPanel = new ShoppingCartPanel(mainFrame);
        tabbedPane.addTab("Shopping Cart", cartPanel);
        
        // Add wallet tab before cart
        tabbedPane.addTab("Wallet", walletPanel);
        tabbedPane.addTab("Profile", ThemeIcons.PROFILE, profilePanel);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.BACKGROUND);
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        // Load data
        loadData();
        refreshWallet();
        
        return panel;
    }
    
    @Override
    protected DashboardStat[] getQuickStats() {
        // Get current patient
        Patient currentPatient = (Patient) mainFrame.getCurrentUser();
        
        // Count completed orders
        List<Order> allOrders = currentPatient.getOrders();
        long completedOrdersCount = 0;
        if (allOrders != null) {
            completedOrdersCount = allOrders.stream()
                .filter(order -> order.getStatus() == Order.Status.COMPLETED || 
                                 order.getStatus() == Order.Status.DELIVERED)
                .count();
        }
        
        // Create stats
        return new DashboardStat[] {
            new DashboardStat("Prescriptions", 
                String.valueOf(currentPatient.getPrescriptions().size()), 
                ThemeIcons.PRESCRIPTION, 
                ThemeColors.PRIMARY),
            new DashboardStat("Orders", 
                String.valueOf(currentPatient.getOrders().size()), 
                ThemeIcons.ORDER, 
                ThemeColors.SUCCESS),
            new DashboardStat("Completed Orders", 
                String.valueOf(completedOrdersCount), 
                ThemeIcons.COMPLETE, 
                ThemeColors.INFO)
        };
    }
    
    @Override
    protected DashboardActivity[] getRecentActivities() {
        List<DashboardActivity> activities = new ArrayList<>();
        
        // Get current patient
        Patient currentPatient = (Patient) mainFrame.getCurrentUser();
        
        // Add recent prescriptions
        List<Prescription> prescriptions = currentPatient.getPrescriptions();
        if (prescriptions != null && !prescriptions.isEmpty()) {
            for (int i = 0; i < Math.min(2, prescriptions.size()); i++) {
                Prescription p = prescriptions.get(i);
                activities.add(new DashboardActivity(
                    "Prescription Received",
                    "Prescription #" + p.getId(), 
                    p.getIssueDate().toString(),
                    ThemeIcons.PRESCRIPTION));
            }
        }
        
        // Add recent orders
        List<Order> orders = currentPatient.getOrders();
        if (orders != null && !orders.isEmpty()) {
            for (int i = 0; i < Math.min(2, orders.size()); i++) {
                Order o = orders.get(i);
                activities.add(new DashboardActivity(
                    "Order Placed", 
                    "Order #" + o.getId(), 
                    o.getOrderDate().toString(),
                    ThemeIcons.ORDER));
            }
        }
        
        // If we don't have enough activities, add some placeholders
        while (activities.size() < 4) {
            activities.add(new DashboardActivity(
                "Welcome", 
                "Welcome to your patient dashboard", 
                "Just now",
                ThemeIcons.NOTIFICATION));
        }
        
        return activities.toArray(new DashboardActivity[0]);
    }
    
    private void loadData() {
        // Get current patient
        Patient currentPatient = (Patient) mainFrame.getCurrentUser();
        
        // Load prescriptions from patient's prescriptions
        List<Prescription> prescriptions = currentPatient.getPrescriptions();
        prescriptionsTable.setData(prescriptions);
        
        // Load orders from patient's orders
        List<Order> orders = currentPatient.getOrders();
        ordersTable.setData(orders);
        
        // Load medicines
        List<Medicine> medicines = mainFrame.getService().getMedicines();
        medicinesTable.setData(medicines);
    }

    private void exportPrescriptionHistory() {
        Patient currentPatient = (Patient) mainFrame.getCurrentUser();
        List<Prescription> prescriptions = currentPatient.getPrescriptions();
        
        StringBuilder content = new StringBuilder();
        content.append("PRESCRIPTION HISTORY\n");
        content.append("===================\n\n");
        content.append("Patient: ").append(currentPatient.getName()).append("\n");
        content.append("Total Prescriptions: ").append(prescriptions.size()).append("\n\n");
        
        for (Prescription prescription : prescriptions) {
            content.append("Prescription #").append(prescription.getId()).append("\n");
            Doctor doctor = mainFrame.getService().getDoctors().stream()
                .filter(d -> d.getId() == prescription.getDoctorId())
                .findFirst()
                .orElse(null);
            content.append("Doctor: ").append(doctor != null ? doctor.getName() : "Unknown Doctor").append("\n");
            content.append("Date: ").append(prescription.getIssueDate()).append("\n");
            content.append("Status: ").append(prescription.getStatus()).append("\n");
            content.append("------------------------\n");
        }
        
        utils.PDFGenerator.generatePDF("Prescription History", content.toString(), this);
    }

    private void exportOrderHistory() {
        Patient currentPatient = (Patient) mainFrame.getCurrentUser();
        List<Order> orders = currentPatient.getOrders();
        
        StringBuilder content = new StringBuilder();
        content.append("ORDER HISTORY\n");
        content.append("=============\n\n");
        content.append("Patient: ").append(currentPatient.getName()).append("\n");
        content.append("Total Orders: ").append(orders.size()).append("\n\n");
        
        double totalSpent = 0.0;
        for (Order order : orders) {
            content.append("Order #").append(order.getId()).append("\n");
            content.append("Date: ").append(order.getOrderDate()).append("\n");
            content.append("Status: ").append(order.getStatus()).append("\n");
            content.append("Total: L.E ").append(String.format("%.2f", order.calculateTotal())).append("\n");
            content.append("------------------------\n");
            totalSpent += order.calculateTotal();
        }
        
        content.append("\nTotal Amount Spent: L.E ").append(String.format("%.2f", totalSpent));
        
        utils.PDFGenerator.generatePDF("Order History", content.toString(), this);
    }

    private void refreshWallet(){
        Patient p=(Patient)mainFrame.getCurrentUser();
        if(p==null||balanceLabel==null) return;
        balanceLabel.setText("Balance: L.E "+String.format("%.2f", p.getWallet().getBalance()));
        transactionsTable.setData(p.getWallet().getTransactions());
    }
} 