package gui.admin;

import gui.MainFrame;
import gui.components.BasePanel;
import gui.components.StyledButton;
import gui.components.LogoutButton;
import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeIcons;
import gui.components.RoundedBorder;
import gui.theme.ThemeSizes;
import models.User;
import models.UserRole;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base class for all dashboard panels
 * Provides common layout and functionality for all dashboards
 */
public abstract class BaseDashboardPanel extends BasePanel {
    protected JPanel headerPanel;
    protected JPanel contentPanel;
    protected JPanel statsPanel;
    protected JPanel recentActivityPanel;
    
    public BaseDashboardPanel(MainFrame mainFrame) {
        super(mainFrame);
        setBackground(ThemeColors.BACKGROUND);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initializeDashboard();
    }
    
    /**
     * Initialize the dashboard layout with common elements
     */
    private void initializeDashboard() {
        // Create header panel
        createHeaderPanel();
        
        // Create main content panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(15, 15));
        contentPanel.setBackground(ThemeColors.BACKGROUND);
        
        // Create quick stats panel
        createStatsPanel();
        
        // Create recent activity panel
        createRecentActivityPanel();
        
        // Add panels to content
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(createRoleSpecificPanel(), BorderLayout.CENTER);
        contentPanel.add(recentActivityPanel, BorderLayout.EAST);
        
        // Add to main layout
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Create the header panel with welcome message and date/time
     */
    private void createHeaderPanel() {
        headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(ThemeColors.SURFACE);
        headerPanel.setBorder(new CompoundBorder(
            new RoundedBorder(ThemeSizes.CARD_BORDER_RADIUS_LARGE, ThemeColors.BORDER_LIGHT),
            new EmptyBorder(ThemeSizes.PADDING_MEDIUM, ThemeSizes.PADDING_LARGE, ThemeSizes.PADDING_MEDIUM, ThemeSizes.PADDING_LARGE)
        ));
        
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

        // Add logout button using the new LogoutButton component
        LogoutButton logoutButton = new LogoutButton(e -> mainFrame.navigateTo("LOGOUT"));
        logoutButton.setFont(ThemeFonts.BOLD_MEDIUM);
        
        // Create right panel for logout button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(ThemeColors.SURFACE);
        rightPanel.add(logoutButton);
        
        headerPanel.add(roleIconLabel, BorderLayout.WEST);
        headerPanel.add(welcomePanel, BorderLayout.CENTER);
        headerPanel.add(rightPanel, BorderLayout.EAST);
    }
    
    /**
     * Create the quick stats panel
     */
    private void createStatsPanel() {
        statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(1, 3, 15, 0));
        statsPanel.setBackground(ThemeColors.BACKGROUND);
        statsPanel.setPreferredSize(new Dimension(0, 120));
        
        // Add stat cards (will be customized by subclasses)
        for (DashboardStat stat : getQuickStats()) {
            statsPanel.add(createStatCard(stat));
        }
    }
    
    /**
     * Create a single stat card
     * 
     * @param stat The stat to display
     * @return The panel containing the stat
     */
    private JPanel createStatCard(DashboardStat stat) {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBackground(ThemeColors.SURFACE);
        cardPanel.setBorder(new CompoundBorder(
            new RoundedBorder(ThemeSizes.CARD_BORDER_RADIUS, ThemeColors.BORDER_LIGHT),
            new EmptyBorder(ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING)
        ));
        
        // Title
        JLabel titleLabel = new JLabel(stat.getTitle());
        titleLabel.setFont(ThemeFonts.BOLD_MEDIUM);
        titleLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        
        // Value
        JLabel valueLabel = new JLabel(stat.getValue());
        valueLabel.setFont(ThemeFonts.FUTURISTIC_LARGE);
        valueLabel.setForeground(stat.getColor());
        
        // Icon
        JLabel iconLabel = new JLabel(stat.getIcon());
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(ThemeColors.SURFACE);
        titlePanel.add(iconLabel, BorderLayout.WEST);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        cardPanel.add(titlePanel, BorderLayout.NORTH);
        cardPanel.add(valueLabel, BorderLayout.CENTER);
        
        return cardPanel;
    }
    
    /**
     * Create the recent activity panel
     */
    private void createRecentActivityPanel() {
        recentActivityPanel = new JPanel();
        recentActivityPanel.setLayout(new BorderLayout());
        recentActivityPanel.setBackground(ThemeColors.SURFACE);
        recentActivityPanel.setBorder(new CompoundBorder(
            new RoundedBorder(ThemeSizes.CARD_BORDER_RADIUS_LARGE, ThemeColors.BORDER_LIGHT),
            new EmptyBorder(ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING, ThemeSizes.CARD_PADDING)
        ));
        recentActivityPanel.setPreferredSize(new Dimension(250, 0));
        
        // Title
        JLabel titleLabel = new JLabel("Recent Activity");
        titleLabel.setFont(ThemeFonts.BOLD_TITLE);
        titleLabel.setForeground(ThemeColors.PRIMARY);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Activity list
        JPanel activitiesPanel = new JPanel();
        activitiesPanel.setLayout(new BoxLayout(activitiesPanel, BoxLayout.Y_AXIS));
        activitiesPanel.setBackground(ThemeColors.SURFACE);
        
        // Add activities (will be customized by subclasses)
        for (DashboardActivity activity : getRecentActivities()) {
            activitiesPanel.add(createActivityItem(activity));
            activitiesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        JScrollPane scrollPane = new JScrollPane(activitiesPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(ThemeColors.SURFACE);
        
        recentActivityPanel.add(titleLabel, BorderLayout.NORTH);
        recentActivityPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Create a single activity item
     * 
     * @param activity The activity to display
     * @return The panel containing the activity
     */
    private JPanel createActivityItem(DashboardActivity activity) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 0));
        panel.setBackground(ThemeColors.SURFACE);
        panel.setBorder(new CompoundBorder(
            new RoundedBorder(ThemeSizes.CARD_BORDER_RADIUS, ThemeColors.BORDER_LIGHT),
            new EmptyBorder(ThemeSizes.PADDING_SMALL, ThemeSizes.PADDING_SMALL, ThemeSizes.PADDING_SMALL, ThemeSizes.PADDING_SMALL)
        ));
        
        // Icon
        JLabel iconLabel = new JLabel(activity.getIcon());
        
        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(ThemeColors.SURFACE);
        
        JLabel titleLabel = new JLabel(activity.getTitle());
        titleLabel.setFont(ThemeFonts.BOLD_SMALL);
        titleLabel.setForeground(ThemeColors.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel(activity.getDescription());
        descLabel.setFont(ThemeFonts.REGULAR_SMALL);
        descLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel timeLabel = new JLabel(activity.getTime());
        timeLabel.setFont(ThemeFonts.ITALIC_SMALL);
        timeLabel.setForeground(ThemeColors.TEXT_TERTIARY);
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        contentPanel.add(descLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        contentPanel.add(timeLabel);
        
        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Get the welcome message for the current user
     * 
     * @return The welcome message
     */
    protected String getWelcomeMessage() {
        User user = mainFrame.getCurrentUser();
        return "Welcome back, " + user.getName() + "!";
    }
    
    /**
     * Get the role icon for the current user
     * 
     * @return The role icon
     */
    protected ImageIcon getRoleIcon() {
        User user = mainFrame.getCurrentUser();
        switch (user.getRole()) {
            case ADMIN:
                return ThemeIcons.USERS;
            case DOCTOR:
                return ThemeIcons.PRESCRIPTION;
            case PHARMACIST:
                return ThemeIcons.MEDICINE;
            case PATIENT:
                return ThemeIcons.NOTIFICATION;
            default:
                return ThemeIcons.DASHBOARD;
        }
    }
    
    /**
     * Create a panel with role-specific content
     * 
     * @return The role-specific panel
     */
    protected abstract JPanel createRoleSpecificPanel();
    
    /**
     * Get the quick stats for this dashboard
     * 
     * @return Array of dashboard stats
     */
    protected abstract DashboardStat[] getQuickStats();
    
    /**
     * Get the recent activities for this dashboard
     * 
     * @return Array of dashboard activities
     */
    protected abstract DashboardActivity[] getRecentActivities();
    
    /**
     * Class representing a quick stat on the dashboard
     */
    protected static class DashboardStat {
        private String title;
        private String value;
        private ImageIcon icon;
        private Color color;
        
        public DashboardStat(String title, String value, ImageIcon icon, Color color) {
            this.title = title;
            this.value = value;
            this.icon = icon;
            this.color = color;
        }
        
        public String getTitle() { return title; }
        public String getValue() { return value; }
        public ImageIcon getIcon() { return icon; }
        public Color getColor() { return color; }
    }
    
    /**
     * Class representing a recent activity on the dashboard
     */
    protected static class DashboardActivity {
        private String title;
        private String description;
        private String time;
        private ImageIcon icon;
        
        public DashboardActivity(String title, String description, String time, ImageIcon icon) {
            this.title = title;
            this.description = description;
            this.time = time;
            this.icon = icon;
        }
        
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getTime() { return time; }
        public ImageIcon getIcon() { return icon; }
    }
} 