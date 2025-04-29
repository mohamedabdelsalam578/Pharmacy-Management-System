package gui.admin;

import gui.MainFrame;
import gui.components.BasePanel;
import gui.components.StyledButton;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeIcons;
import models.Admin;
import models.Doctor;
import models.Patient;
import models.Pharmacy;
import models.Pharmacist;
import models.User;
import models.UserRole;
import services.PharmacyService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel for managing users in the pharmacy system
 */
public class UserManagementPanel extends BasePanel {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> userTypeComboBox;
    private StyledButton addButton;
    private StyledButton editButton;
    private StyledButton deleteButton;
    private StyledButton backButton;
    
    public UserManagementPanel(MainFrame mainFrame) {
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
        
        // Create footer panel with actions
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
        
        // Load data
        loadUserData();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(15, 15, 25, 15)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(ThemeFonts.BOLD_XXLARGE);
        titleLabel.setForeground(ThemeColors.PRIMARY);
        titleLabel.setIcon(ThemeIcons.USER);
        
        // Search and filter panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        controlsPanel.setBackground(ThemeColors.SURFACE);
        
        // User type filter
        userTypeComboBox = new JComboBox<>(new String[]{"All Users", "Patients", "Doctors", "Pharmacists", "Admins"});
        userTypeComboBox.setFont(ThemeFonts.REGULAR_MEDIUM);
        userTypeComboBox.addActionListener(e -> filterUsers());
        
        // Search field
        searchField = new JTextField(20);
        searchField.setFont(ThemeFonts.REGULAR_MEDIUM);
        searchField.setBorder(new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        searchField.setToolTipText("Search by name, username, or email");
        
        // Make search field respond to Enter key
        searchField.addActionListener(e -> searchUsers(searchField.getText()));
        
        // Enhanced search button
        StyledButton searchButton = new StyledButton("Search", ThemeIcons.SEARCH);
        searchButton.addActionListener(e -> searchUsers(searchField.getText()));
        
        // Add a clear button to reset search
        StyledButton clearButton = new StyledButton("Clear", ThemeIcons.CANCEL);
        clearButton.addActionListener(e -> {
            searchField.setText("");
            loadUserData();
        });
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(ThemeFonts.BOLD_MEDIUM);
        
        controlsPanel.add(new JLabel("Filter by:"));
        controlsPanel.add(userTypeComboBox);
        controlsPanel.add(searchLabel);
        controlsPanel.add(searchField);
        controlsPanel.add(searchButton);
        controlsPanel.add(clearButton);
        
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
            new EmptyBorder(15, 15, 20, 15)
        ));
        
        // Create table model
        String[] columns = {"ID", "Name", "Username", "Role", "Email", "Phone", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table
        userTable = new JTable(tableModel);
        userTable.setFont(ThemeFonts.REGULAR_MEDIUM);
        userTable.setRowHeight(30);
        userTable.setShowGrid(true);
        userTable.setGridColor(ThemeColors.BORDER_LIGHT);
        userTable.setSelectionBackground(ThemeColors.PRIMARY_LIGHT);
        userTable.setSelectionForeground(ThemeColors.TEXT_PRIMARY);
        userTable.getTableHeader().setFont(ThemeFonts.BOLD_MEDIUM);
        userTable.getTableHeader().setBackground(ThemeColors.SURFACE_VARIANT);
        userTable.getTableHeader().setForeground(ThemeColors.TEXT_PRIMARY);
        
        // Create cell renderer for role column to colorize roles
        userTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String role = (String) value;
                if ("Admin".equals(role)) {
                    c.setForeground(ThemeColors.DANGER);
                } else if ("Doctor".equals(role)) {
                    c.setForeground(ThemeColors.PRIMARY);
                } else if ("Pharmacist".equals(role)) {
                    c.setForeground(ThemeColors.SUCCESS);
                } else {
                    c.setForeground(ThemeColors.INFO);
                }
                
                return c;
            }
        });
        
        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(userTable);
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
        
        // Add user button
        addButton = new StyledButton("Add User", ThemeIcons.ADD);
        addButton.addActionListener(e -> addUser());
        
        // Edit user button
        editButton = new StyledButton("Edit User", ThemeIcons.EDIT);
        editButton.addActionListener(e -> editUser());
        
        // Delete user button
        deleteButton = new StyledButton("Delete User", ThemeIcons.DELETE);
        deleteButton.addActionListener(e -> deleteUser());
        
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        
        return panel;
    }
    
    /**
     * Helper method to get all users from all categories
     * 
     * @param service The pharmacy service to get users from
     * @return A map of all users with their IDs as keys
     */
    private Map<Integer, User> getAllUsers(PharmacyService service) {
        Map<Integer, User> allUsers = new HashMap<>();
        
        // Debug information about the service
        System.out.println("===== LOADING ALL USERS =====");
        System.out.println("Service object: " + (service != null ? "Available" : "NULL"));
        
        try {
            // Debug DIRECT patient access and logging
            List<Patient> patients = service.getPatients();
            System.out.println("DIRECT Patients access: " + (patients != null ? patients.size() : "NULL"));
            if (patients != null) {
                for (Patient p : patients) {
                    System.out.println("DIRECT Patient found: [ID=" + p.getId() + 
                                      ", Name=" + p.getName() + 
                                      ", Username=" + p.getUsername() + 
                                      ", Role=" + p.getRole() + "]");
                }
            }
            
            // Debug DIRECT doctor access and logging
            List<Doctor> doctors = service.getDoctors();
            System.out.println("DIRECT Doctors access: " + (doctors != null ? doctors.size() : "NULL"));
            if (doctors != null) {
                for (Doctor d : doctors) {
                    System.out.println("DIRECT Doctor found: [ID=" + d.getId() + 
                                      ", Name=" + d.getName() + 
                                      ", Username=" + d.getUsername() + 
                                      ", Role=" + d.getRole() + "]");
                }
            } else {
                System.err.println("WARNING: Doctors list is NULL - forcing initialization");
                service.initialize();
                doctors = service.getDoctors();
                System.out.println("After initialization, doctors: " + (doctors != null ? doctors.size() : "still NULL"));
            }
                        
            // Add all user types to the map
            if (service.getPatients() != null) {
                System.out.println("Patients: " + service.getPatients().size() + " found");
                for (User user : service.getPatients()) {
                    System.out.println("  > Patient: [ID=" + user.getId() + 
                                      ", Name=" + user.getName() + 
                                      ", Class=" + user.getClass().getSimpleName() + "]");
                    allUsers.put(user.getId(), user);
                    System.out.println("  - Added Patient: " + user.getId() + " - " + user.getName());
                }
            } else {
                System.out.println("Patients list is NULL");
            }
            
            if (service.getDoctors() != null) {
                System.out.println("Doctors: " + service.getDoctors().size() + " found");
                for (User user : service.getDoctors()) {
                    System.out.println("  > Doctor: [ID=" + user.getId() + 
                                      ", Name=" + user.getName() + 
                                      ", Class=" + user.getClass().getSimpleName() + "]");
                    allUsers.put(user.getId(), user);
                    System.out.println("  - Added Doctor: " + user.getId() + " - " + user.getName());
                }
            } else {
                System.out.println("Doctors list is NULL - attempting to initialize");
                service.initialize();
                if (service.getDoctors() != null) {
                    System.out.println("After init: Doctors: " + service.getDoctors().size() + " found");
                    for (User user : service.getDoctors()) {
                        allUsers.put(user.getId(), user);
                        System.out.println("  - Added Doctor (after init): " + user.getId() + " - " + user.getName());
                    }
                } else {
                    System.err.println("CRITICAL ERROR: Doctors list is still NULL after initialization");
                }
            }
            
            if (service.getPharmacists() != null) {
                System.out.println("Pharmacists: " + service.getPharmacists().size() + " found");
                for (User user : service.getPharmacists()) {
                    System.out.println("  > Pharmacist: [ID=" + user.getId() + 
                                      ", Name=" + user.getName() + 
                                      ", Class=" + user.getClass().getSimpleName() + "]");
                    allUsers.put(user.getId(), user);
                    System.out.println("  - Added Pharmacist: " + user.getId() + " - " + user.getName());
                }
            } else {
                System.out.println("Pharmacists list is NULL");
            }
            
            if (service.getAdmins() != null) {
                System.out.println("Admins: " + service.getAdmins().size() + " found");
                for (User user : service.getAdmins()) {
                    System.out.println("  > Admin: [ID=" + user.getId() + 
                                      ", Name=" + user.getName() + 
                                      ", Class=" + user.getClass().getSimpleName() + "]");
                    allUsers.put(user.getId(), user);
                    System.out.println("  - Added Admin: " + user.getId() + " - " + user.getName());
                }
            } else {
                System.out.println("Admins list is NULL");
            }
            
            // Check for any pharmacies and their pharmacists
            if (service.getPharmacies() != null) {
                System.out.println("Checking pharmacies: " + service.getPharmacies().size() + " found");
                for (Pharmacy pharmacy : service.getPharmacies()) {
                    // Add pharmacists working at each pharmacy
                    if (pharmacy.getPharmacists() != null && !pharmacy.getPharmacists().isEmpty()) {
                        System.out.println("Adding " + pharmacy.getPharmacists().size() + 
                                          " pharmacists from pharmacy " + pharmacy.getName());
                        for (Pharmacist pharmacist : pharmacy.getPharmacists()) {
                            allUsers.put(pharmacist.getId(), pharmacist);
                            System.out.println("  - Added Pharmacy Pharmacist: " + 
                                              pharmacist.getId() + " - " + pharmacist.getName());
                        }
                    }
                }
            }
            
            // Double-check we've loaded all users by ID
            System.out.println("===== USER LOADING SUMMARY =====");
            System.out.println("Total unique users loaded: " + allUsers.size());
            System.out.println("User IDs in map: " + allUsers.keySet());
            System.out.println("User Types: ");
            for (User user : allUsers.values()) {
                System.out.println("  - ID " + user.getId() + ": " + user.getClass().getSimpleName());
            }
            
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return allUsers;
    }
    
    public void loadUserData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        PharmacyService service = mainFrame.getPharmacyService();
        if (service == null) {
            System.err.println("ERROR: PharmacyService is null!");
            JOptionPane.showMessageDialog(this, 
                "Cannot load users: Service is unavailable", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Force initialize sample data if needed
        if (service.getPatients() == null || service.getPatients().isEmpty() ||
            service.getDoctors() == null || service.getDoctors().isEmpty()) {
            System.out.println("No users found in service, initializing sample data");
            service.initialize();
            // Save to ensure data persists
            service.saveDataToFiles();
        }
        
        // Log directly what's available in the service
        String patientInfo = "Patients available: " + 
            (service.getPatients() != null ? service.getPatients().size() : "NULL");
        String doctorInfo = "Doctors available: " + 
            (service.getDoctors() != null ? service.getDoctors().size() : "NULL");
        String adminInfo = "Admins available: " + 
            (service.getAdmins() != null ? service.getAdmins().size() : "NULL");
        System.out.println(patientInfo + ", " + doctorInfo + ", " + adminInfo);
        
        // Use a map to ensure unique IDs (key is user ID, value is the User object)
        Map<Integer, User> userMap = new HashMap<>();
        
        // Get the selected filter
        String filter = (String) userTypeComboBox.getSelectedItem();
        System.out.println("Selected filter: " + filter);
        
        try {
            // CRITICAL FIX: Clear the map first
            userMap.clear();
            
            // Verify user lists BEFORE any filtering
            System.out.println("BEFORE FILTERING - Available data in PharmacyService:");
            System.out.println("  - Patients: " + (service.getPatients() != null ? service.getPatients().size() + " found" : "NULL"));
            System.out.println("  - Doctors: " + (service.getDoctors() != null ? service.getDoctors().size() + " found" : "NULL"));
            System.out.println("  - Pharmacists: " + (service.getPharmacists() != null ? service.getPharmacists().size() + " found" : "NULL"));
            System.out.println("  - Admins: " + (service.getAdmins() != null ? service.getAdmins().size() + " found" : "NULL"));
            
            // Collect users based on filter, ensuring unique IDs
            if ("All Users".equals(filter)) {
                // Use the getAllUsers helper method to get all users at once
                userMap = getAllUsers(service);
            } else if ("Patients".equals(filter)) {
                // Add only patients
                if (service.getPatients() != null) {
                    for (Patient patient : service.getPatients()) {
                        userMap.put(patient.getId(), patient);
                    }
                }
            } else if ("Doctors".equals(filter)) {
                // Add only doctors
                if (service.getDoctors() != null) {
                    for (Doctor doctor : service.getDoctors()) {
                        userMap.put(doctor.getId(), doctor);
                    }
                }
            } else if ("Pharmacists".equals(filter)) {
                // Add only pharmacists
                if (service.getPharmacists() != null) {
                    for (Pharmacist pharmacist : service.getPharmacists()) {
                        userMap.put(pharmacist.getId(), pharmacist);
                    }
                }
            } else if ("Admins".equals(filter)) {
                // Add only admins
                if (service.getAdmins() != null) {
                    for (Admin admin : service.getAdmins()) {
                        userMap.put(admin.getId(), admin);
                    }
                }
            }
            
            System.out.println("Total users after filtering: " + userMap.size());
            System.out.println("User IDs: " + userMap.keySet());
            
            // Count users by role for debugging
            int patientCount = 0, doctorCount = 0, pharmacistCount = 0, adminCount = 0, unknownCount = 0;
            for (User user : userMap.values()) {
                if (user instanceof Patient) patientCount++;
                else if (user instanceof Doctor) doctorCount++;
                else if (user instanceof Pharmacist) pharmacistCount++;
                else if (user instanceof Admin) adminCount++;
                else unknownCount++;
            }
            System.out.println("USERS BY ROLE: Patients=" + patientCount + 
                             ", Doctors=" + doctorCount + 
                             ", Pharmacists=" + pharmacistCount + 
                             ", Admins=" + adminCount +
                             ", Unknown=" + unknownCount);
            
            // CRITICAL FIX: Add all users from map to the table
            for (User user : userMap.values()) {
                String role = "Unknown";
                String email = "";
                String phoneNumber = "";
                
                if (user instanceof Patient) {
                    Patient patient = (Patient) user;
                    role = "Patient";
                    email = patient.getEmail();
                    phoneNumber = patient.getPhoneNumber();
                } else if (user instanceof Doctor) {
                    Doctor doctor = (Doctor) user;
                    role = "Doctor";
                    email = doctor.getEmail();
                    phoneNumber = doctor.getPhoneNumber();
                } else if (user instanceof Pharmacist) {
                    Pharmacist pharmacist = (Pharmacist) user;
                    role = "Pharmacist";
                    email = pharmacist.getEmail();
                    phoneNumber = pharmacist.getPhoneNumber();
                } else if (user instanceof Admin) {
                    Admin admin = (Admin) user;
                    role = "Admin";
                    email = admin.getEmail();
                    phoneNumber = admin.getPhoneNumber();
                }
                
                Object[] row = {
                    user.getId(),
                    user.getName(),
                    user.getUsername(),
                    role,
                    email,
                    phoneNumber,
                    "Active" // In a real app, this would be dynamic
                };
                
                System.out.println("Adding to table: " + user.getId() + " - " + user.getName() + " - " + role);
                tableModel.addRow(row);
            }
            
            // Sort table by ID for consistent display
            sortTableByID();
            
        } catch (Exception e) {
            System.err.println("Error loading user data: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading users: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Sort the table by user ID
     */
    private void sortTableByID() {
        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        int rowCount = model.getRowCount();
        
        // Convert to list for sorting
        List<Object[]> rows = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            Object[] row = new Object[model.getColumnCount()];
            for (int j = 0; j < model.getColumnCount(); j++) {
                row[j] = model.getValueAt(i, j);
            }
            rows.add(row);
        }
        
        // Sort by ID (first column)
        rows.sort((a, b) -> {
            Integer idA = (Integer) a[0];
            Integer idB = (Integer) b[0];
            return idA.compareTo(idB);
        });
        
        // Update table
        model.setRowCount(0);
        for (Object[] row : rows) {
            model.addRow(row);
        }
    }
    
    private void filterUsers() {
        loadUserData(); // Reload with filter applied
    }
    
    private void searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadUserData(); // If search is empty, reload all data
            return;
        }
        
        // Store the original data count for feedback
        int originalCount = tableModel.getRowCount();
        
        // Keep current rows
        int rowCount = originalCount;
        List<Object[]> keepRows = new ArrayList<>();
        
        // Search for matches
        query = query.toLowerCase().trim();
        for (int i = 0; i < rowCount; i++) {
            String name = ((String) tableModel.getValueAt(i, 1)).toLowerCase();
            String username = ((String) tableModel.getValueAt(i, 2)).toLowerCase();
            String role = ((String) tableModel.getValueAt(i, 3)).toLowerCase();
            String email = ((String) tableModel.getValueAt(i, 4)).toLowerCase();
            String phone = ((String) tableModel.getValueAt(i, 5)).toLowerCase();
            
            if (name.contains(query) || 
                username.contains(query) || 
                role.contains(query) ||
                email.contains(query) ||
                phone.contains(query)) {
                Object[] row = new Object[tableModel.getColumnCount()];
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    row[j] = tableModel.getValueAt(i, j);
                }
                keepRows.add(row);
            }
        }
        
        // Update table
        tableModel.setRowCount(0);
        for (Object[] row : keepRows) {
            tableModel.addRow(row);
        }
        
        // Show search results feedback
        int foundCount = keepRows.size();
        if (foundCount == 0) {
            JOptionPane.showMessageDialog(
                this,
                "No users found matching '" + query + "'",
                "Search Results",
                JOptionPane.INFORMATION_MESSAGE
            );
            loadUserData(); // Restore original data
        } else if (foundCount < originalCount) {
            String message = "Found " + foundCount + " user" + (foundCount > 1 ? "s" : "") + 
                             " matching '" + query + "'";
            JOptionPane.showMessageDialog(
                this,
                message,
                "Search Results",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    private void addUser() {
        // Create a dialog to display the AddUserPanel
        JDialog dialog = new JDialog(mainFrame, "Add New User", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(true);
        
        // Create AddUserPanel and add it to the dialog
        AddUserPanel addUserPanel = new AddUserPanel(mainFrame, this, dialog);
        dialog.add(addUserPanel);
        
        // Show the dialog
        dialog.setVisible(true);
    }
    
    private int getNextUserId() {
        // Find the highest ID in current users and add 1
        int maxId = 0;
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int id = (int) tableModel.getValueAt(i, 0);
            if (id > maxId) {
                maxId = id;
            }
        }
        
        return maxId + 1;
    }
    
    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a user to edit",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        // Get user details
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String userName = (String) tableModel.getValueAt(selectedRow, 1);
        String username = (String) tableModel.getValueAt(selectedRow, 2);
        String userRole = (String) tableModel.getValueAt(selectedRow, 3);
        String email = (String) tableModel.getValueAt(selectedRow, 4);
        String phone = (String) tableModel.getValueAt(selectedRow, 5);
        
        // Create panel for user input
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        // Fields for editing
        JTextField nameField = new JTextField(userName, 20);
        JTextField usernameField = new JTextField(username, 20);
        usernameField.setEnabled(false); // Don't allow username changes
        JPasswordField passwordField = new JPasswordField(20);
        JTextField emailField = new JTextField(email, 20);
        JTextField phoneField = new JTextField(phone, 20);
        
        // Add fields to panel
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Username (not editable):"));
        inputPanel.add(usernameField);
        inputPanel.add(new JLabel("New Password (leave blank to keep existing):"));
        inputPanel.add(passwordField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Phone:"));
        inputPanel.add(phoneField);
        
        // Show input dialog
        int result = JOptionPane.showConfirmDialog(
            this,
            inputPanel,
            "Edit " + userRole,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        // Process input if user clicked OK
        if (result == JOptionPane.OK_OPTION) {
            // Validate input
            if (nameField.getText().isEmpty() || emailField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all required fields",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            try {
                // Get user from service
                User user = findUserById(userId);
                if (user != null) {
                    // Update common fields
                    // user.setName(nameField.getText()); // Removed redundant name setting
                    
                    // Update password if provided
                    String newPassword = new String(passwordField.getPassword());
                    if (!newPassword.isEmpty()) {
                        user.setPassword(newPassword);
                    }
                    
                    // Update role-specific fields
                    switch (user.getRole()) {
                        case PATIENT:
                            Patient patient = (Patient) user;
                            patient.setName(nameField.getText());
                            patient.setEmail(emailField.getText());
                            patient.setPhoneNumber(phoneField.getText());
                            break;
                        case DOCTOR:
                            Doctor doctor = (Doctor) user;
                            doctor.setName(nameField.getText());
                            doctor.setEmail(emailField.getText());
                            doctor.setPhoneNumber(phoneField.getText());
                            break;
                        case PHARMACIST:
                            Pharmacist pharmacist = (Pharmacist) user;
                            pharmacist.setName(nameField.getText());
                            pharmacist.setEmail(emailField.getText());
                            pharmacist.setPhoneNumber(phoneField.getText());
                            break;
                        case ADMIN:
                            Admin admin = (Admin) user;
                            admin.setName(nameField.getText());
                            admin.setEmail(emailField.getText());
                            admin.setPhoneNumber(phoneField.getText());
                            break;
                    }
                    
                    // Update table
                    loadUserData();
                    
                    JOptionPane.showMessageDialog(
                        this,
                        "User updated successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "User not found",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error updating user: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    private User findUserById(int userId) {
        PharmacyService service = mainFrame.getService();
        
        // Check in all user lists
        for (Patient patient : service.getPatients()) {
            if (patient.getId() == userId) return patient;
        }
        
        for (Doctor doctor : service.getDoctors()) {
            if (doctor.getId() == userId) return doctor;
        }
        
        for (Pharmacist pharmacist : service.getPharmacists()) {
            if (pharmacist.getId() == userId) return pharmacist;
        }
        
        for (Admin admin : service.getAdmins()) {
            if (admin.getId() == userId) return admin;
        }
        
        return null;
    }
    
    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a user to delete",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        // Get user details
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String userName = (String) tableModel.getValueAt(selectedRow, 1);
        String userRole = (String) tableModel.getValueAt(selectedRow, 3);
        
        // Prevent current user from deleting themselves
        User currentUser = mainFrame.getCurrentUser();
        if (currentUser != null && currentUser.getId() == userId) {
            JOptionPane.showMessageDialog(
                this,
                "You cannot delete your own account while logged in",
                "Operation Not Allowed",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete " + userName + " (" + userRole + ")?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                PharmacyService service = mainFrame.getService();
                boolean deleted = false;
                
                // Delete user based on role
                switch (UserRole.valueOf(userRole.toUpperCase())) {
                    case PATIENT:
                        deleted = service.deletePatient(userId);
                        break;
                    case DOCTOR:
                        deleted = service.deleteDoctor(userId);
                        break;
                    case PHARMACIST:
                        deleted = service.deletePharmacist(userId);
                        break;
                    case ADMIN:
                        deleted = service.deleteAdmin(userId);
                        break;
                }
                
                if (deleted) {
                    // Refresh table
                    loadUserData();
                    
                    JOptionPane.showMessageDialog(
                        this,
                        "User deleted successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Failed to delete user",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error deleting user: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
} 