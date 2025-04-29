package gui.navigation;

import gui.MainFrame;
import gui.components.BasePanel;
import gui.components.StyledButton;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeIcons;
import gui.theme.ThemeSizes;
import models.User;
import models.UserRole;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SidebarPanel extends BasePanel {
    private Map<String, StyledButton> menuButtons;
    private JPanel menuPanel;
    
    public SidebarPanel(MainFrame mainFrame) {
        super(mainFrame);
        menuButtons = new HashMap<>();
        setPreferredSize(new Dimension(ThemeSizes.SIDEBAR_WIDTH, 0));
        setBackground(ThemeColors.SIDEBAR_BACKGROUND);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeColors.BORDER));
        setLayout(new BorderLayout());
    }
    
    @Override
    protected void initializeComponents() {
        // Logo Panel
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(ThemeColors.SIDEBAR_BACKGROUND);
        JLabel logoLabel = new JLabel("EL-TA3BAN");
        logoLabel.setFont(ThemeFonts.REGULAR_XLARGE);
        logoLabel.setForeground(ThemeColors.PRIMARY);
        logoPanel.add(logoLabel);
        add(logoPanel, BorderLayout.NORTH);
        
        // Menu Panel
        menuPanel = new JPanel();
        menuPanel.setBackground(ThemeColors.SIDEBAR_BACKGROUND);
        menuPanel.setLayout(new GridBagLayout());
        add(menuPanel, BorderLayout.CENTER);
        
        // Add menu items based on user role
        if (mainFrame != null && mainFrame.getCurrentUser() != null) {
            UserRole role = mainFrame.getCurrentUser().getRole();
            switch (role) {
                case ADMIN:
                    addMenuItem("Dashboard", "DASHBOARD", ThemeIcons.DASHBOARD);
                    addMenuItem("Users", "USERS", ThemeIcons.USERS);
                    addMenuItem("Medicines", "MEDICINES", ThemeIcons.MEDICINE);
                    addMenuItem("Orders", "ORDERS", ThemeIcons.ORDER);
                    addMenuItem("Reports", "REPORTS", ThemeIcons.REPORT);
                    break;
                case DOCTOR:
                    addMenuItem("Dashboard", "DASHBOARD", ThemeIcons.DASHBOARD);
                    addMenuItem("Patients", "PATIENTS", ThemeIcons.USERS);
                    addMenuItem("Prescriptions", "PRESCRIPTIONS", ThemeIcons.PRESCRIPTION);
                    break;
                case PHARMACIST:
                    addMenuItem("Dashboard", "DASHBOARD", ThemeIcons.DASHBOARD);
                    addMenuItem("Medicines", "MEDICINES", ThemeIcons.MEDICINE);
                    addMenuItem("Orders", "ORDERS", ThemeIcons.ORDER);
                    addMenuItem("Prescriptions", "PRESCRIPTIONS", ThemeIcons.PRESCRIPTION);
                    break;
                case PATIENT:
                    addMenuItem("Dashboard", "DASHBOARD", ThemeIcons.DASHBOARD);
                    addMenuItem("Medicines", "MEDICINES", ThemeIcons.MEDICINE);
                    addMenuItem("Orders", "ORDERS", ThemeIcons.ORDER);
                    addMenuItem("Prescriptions", "PRESCRIPTIONS", ThemeIcons.PRESCRIPTION);
                    break;
            }
        }
        
        // Logout button at the bottom
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoutPanel.setBackground(ThemeColors.SIDEBAR_BACKGROUND);
        StyledButton logoutButton = new StyledButton("Logout", ThemeIcons.LOGOUT);
        logoutButton.addActionListener(e -> {
            if (mainFrame != null) {
                mainFrame.navigateTo("LOGOUT");
            }
        });
        logoutPanel.add(logoutButton);
        add(logoutPanel, BorderLayout.SOUTH);
    }
    
    private void addMenuItem(String label, String route, Icon icon) {
        StyledButton button = new StyledButton(label, icon);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setPreferredSize(new Dimension(ThemeSizes.SIDEBAR_WIDTH - 20, 40));
        button.addActionListener(e -> {
            if (mainFrame != null) {
                mainFrame.navigateTo(route);
                setActiveButton(route);
            }
        });
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = menuPanel.getComponentCount();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        
        menuPanel.add(button, gbc);
        menuButtons.put(route, button);
    }
    
    public void setActiveButton(String route) {
        menuButtons.values().forEach(button -> button.setActive(false));
        StyledButton activeButton = menuButtons.get(route);
        if (activeButton != null) {
            activeButton.setActive(true);
        }
    }
} 