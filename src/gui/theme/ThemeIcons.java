package gui.theme;

import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class ThemeIcons {
    // Create icons programmatically or load from file
    public static final ImageIcon LOGO = createIcon(48, 48, ThemeColors.PRIMARY);
    public static final ImageIcon LOGO_SMALL = createIcon(32, 32, ThemeColors.PRIMARY);
    public static final ImageIcon DASHBOARD = loadIconFromFile("dashboard.png", 24, 24);
    public static final ImageIcon USERS = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon MEDICINES = loadIconFromFile("medicine.png", 24, 24);
    public static final ImageIcon ORDERS = loadIconFromFile("order.png", 24, 24);
    public static final ImageIcon PRESCRIPTIONS = loadIconFromFile("prescription.png", 24, 24);
    public static final ImageIcon NOTIFICATIONS = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon MESSAGES = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon SETTINGS = loadIconFromFile("setting.png", 24, 24);
    public static final ImageIcon LOGOUT = loadIconFromFile("logout.png", 24, 24);
    
    // Added missing icons
    public static final ImageIcon DOCTOR = loadIconFromFile("doctor.png", 24, 24);
    public static final ImageIcon CONSULTATION = loadIconFromFile("consultation.png", 24, 24);
    public static final ImageIcon CHART = loadIconFromFile("chart.png", 24, 24);
    
    // Status Icons
    public static final ImageIcon SUCCESS = createIcon(24, 24, ThemeColors.SUCCESS);
    public static final ImageIcon ERROR = createIcon(24, 24, ThemeColors.DANGER);
    public static final ImageIcon WARNING = createIcon(24, 24, ThemeColors.WARNING);
    public static final ImageIcon INFO = createIcon(24, 24, ThemeColors.INFO);
    
    // Action Icons
    public static final ImageIcon ADD = createIcon(24, 24, ThemeColors.SUCCESS);
    public static final ImageIcon EDIT = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon DELETE = createIcon(24, 24, ThemeColors.DANGER);
    public static final ImageIcon SEARCH = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon REFRESH = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon PRINT = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon EXPORT = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon IMPORT = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon FILTER = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon SORT = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon VIEW = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon DOWNLOAD = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon UPLOAD = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon SAVE = createIcon(24, 24, ThemeColors.SUCCESS);
    public static final ImageIcon CANCEL = createIcon(24, 24, ThemeColors.DANGER);
    public static final ImageIcon CLOSE = createIcon(24, 24, ThemeColors.DANGER);
    public static final ImageIcon BACK = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon NEXT = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon MEDICINE = loadIconFromFile("medicine.png", 24, 24);
    public static final ImageIcon ORDER = loadIconFromFile("order.png", 24, 24);
    public static final ImageIcon PRESCRIPTION = loadIconFromFile("prescription.png", 24, 24);
    public static final ImageIcon REPORT = loadIconFromFile("report.png", 24, 24);
    public static final ImageIcon NOTIFICATION = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon LOGIN = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon PROFILE = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon USER = loadIconFromFile("patient.png", 24, 24);
    
    // Pharmacist Action Icons
    public static final ImageIcon PROCESS = createIcon(24, 24, ThemeColors.PRIMARY);
    public static final ImageIcon COMPLETE = createIcon(24, 24, ThemeColors.SUCCESS);
    public static final ImageIcon VALIDATE = createIcon(24, 24, ThemeColors.SUCCESS);
    public static final ImageIcon REJECT = createIcon(24, 24, ThemeColors.DANGER);
    public static final ImageIcon REMOVE = createIcon(24, 24, ThemeColors.DANGER);

    private static ImageIcon createIcon(int width, int height, Color color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        // Enable antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a simple circle as placeholder
        g2.setColor(color);
        g2.fillOval(2, 2, width - 4, height - 4);
        
        g2.dispose();
        return new ImageIcon(image);
    }
    
    /**
     * Load an icon from a file in the icons directory
     * @param filename The name of the icon file
     * @param width Target width for the icon
     * @param height Target height for the icon
     * @return The loaded icon, or a fallback circle if loading fails
     */
    private static ImageIcon loadIconFromFile(String filename, int width, int height) {
        try {
            // Try to load from icons directory
            String path = "/icons/" + filename;
            URL resourceUrl = ThemeIcons.class.getResource(path);
            
            // If not found in classpath, try direct file path
            if (resourceUrl == null) {
                resourceUrl = new URL("file:icons/" + filename);
            }
            
            ImageIcon originalIcon = new ImageIcon(resourceUrl);
            
            // Resize if necessary
            if (originalIcon.getIconWidth() != width || originalIcon.getIconHeight() != height) {
                Image scaledImage = originalIcon.getImage().getScaledInstance(
                    width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
            
            return originalIcon;
        } catch (Exception e) {
            // Fallback to colored circle if loading fails
            System.err.println("Error loading icon: " + filename + " - " + e.getMessage());
            Color fallbackColor = ThemeColors.PRIMARY;
            
            // Choose fallback color based on filename
            if (filename.contains("success")) fallbackColor = ThemeColors.SUCCESS;
            else if (filename.contains("error") || filename.contains("danger")) fallbackColor = ThemeColors.DANGER;
            else if (filename.contains("warning")) fallbackColor = ThemeColors.WARNING;
            else if (filename.contains("info")) fallbackColor = ThemeColors.INFO;
            
            return createIcon(width, height, fallbackColor);
        }
    }
} 