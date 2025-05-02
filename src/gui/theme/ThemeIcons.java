package gui.theme;

import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.io.File;

public class ThemeIcons {
    // Create icons programmatically or load from file
    public static final ImageIcon LOGO = createIcon(48, 48, ThemeColors.PRIMARY);
    public static final ImageIcon LOGO_SMALL = createIcon(32, 32, ThemeColors.PRIMARY);
    public static final ImageIcon DASHBOARD = loadIconFromFile("dashboard.png", 24, 24);
    public static final ImageIcon USERS = loadIconFromFile("users.png", 24, 24);
    public static final ImageIcon MEDICINES = loadIconFromFile("medicine.png", 24, 24);
    public static final ImageIcon ORDERS = loadIconFromFile("orders.png", 24, 24);
    public static final ImageIcon PRESCRIPTIONS = loadIconFromFile("prescriptions.png", 24, 24);
    public static final ImageIcon NOTIFICATIONS = createNotificationIcon(24, 24);
    public static final ImageIcon MESSAGES = createMessageIcon(24, 24);
    public static final ImageIcon SETTINGS = loadIconFromFile("settings.png", 24, 24);
    public static final ImageIcon LOGOUT = loadIconFromFile("logout.png", 24, 24);
    
    // Role-specific icons (using generic icons for missing ones)
    public static final ImageIcon DOCTOR = createRoleIcon(24, 24, "D");
    public static final ImageIcon CONSULTATION = createRoleIcon(24, 24, "C");
    public static final ImageIcon CHART = createRoleIcon(24, 24, "G");
    
    // Status Icons
    public static final ImageIcon SUCCESS = loadIconFromFile("success.png", 24, 24);
    public static final ImageIcon ERROR = loadIconFromFile("error.png", 24, 24);
    public static final ImageIcon WARNING = loadIconFromFile("warning.png", 24, 24);
    public static final ImageIcon INFO = loadIconFromFile("info.png", 24, 24);
    
    // Action Icons
    public static final ImageIcon ADD = loadIconFromFile("add.png", 24, 24);
    public static final ImageIcon EDIT = loadIconFromFile("edit.png", 24, 24);
    public static final ImageIcon DELETE = loadIconFromFile("delete.png", 24, 24);
    public static final ImageIcon SEARCH = loadIconFromFile("search.png", 24, 24);
    public static final ImageIcon REFRESH = loadIconFromFile("refresh.png", 24, 24);
    public static final ImageIcon PRINT = loadIconFromFile("print.png", 24, 24);
    public static final ImageIcon EXPORT = loadIconFromFile("export.png", 24, 24);
    public static final ImageIcon IMPORT = createImportIcon(24, 24);
    public static final ImageIcon FILTER = loadIconFromFile("filter.png", 24, 24);
    public static final ImageIcon SORT = createSortIcon(24, 24);
    public static final ImageIcon VIEW = loadIconFromFile("view.png", 24, 24);
    public static final ImageIcon DOWNLOAD = createDownloadIcon(24, 24);
    public static final ImageIcon UPLOAD = createUploadIcon(24, 24);
    public static final ImageIcon SAVE = createActionIcon(24, 24, "✓", ThemeColors.SUCCESS);
    public static final ImageIcon CANCEL = createActionIcon(24, 24, "✕", ThemeColors.DANGER);
    public static final ImageIcon CLOSE = createActionIcon(24, 24, "×", ThemeColors.DANGER);
    public static final ImageIcon BACK = loadIconFromFile("back.png", 24, 24);
    public static final ImageIcon NEXT = createActionIcon(24, 24, "→", ThemeColors.PRIMARY);
    public static final ImageIcon MEDICINE = loadIconFromFile("medicine.png", 24, 24);
    public static final ImageIcon ORDER = loadIconFromFile("order.png", 24, 24);
    public static final ImageIcon PRESCRIPTION = loadIconFromFile("prescription.png", 24, 24);
    public static final ImageIcon REPORT = loadIconFromFile("report.png", 24, 24);
    public static final ImageIcon NOTIFICATION = createNotificationIcon(24, 24);
    public static final ImageIcon LOGIN = createLoginIcon(24, 24);
    public static final ImageIcon PROFILE = createProfileIcon(24, 24);
    public static final ImageIcon USER = loadIconFromFile("patient.png", 24, 24);
    
    // Pharmacist Action Icons
    public static final ImageIcon PROCESS = createActionIcon(24, 24, "⚙", ThemeColors.PRIMARY);
    public static final ImageIcon COMPLETE = createActionIcon(24, 24, "✓", ThemeColors.SUCCESS);
    public static final ImageIcon VALIDATE = createActionIcon(24, 24, "✓", ThemeColors.SUCCESS);
    public static final ImageIcon REJECT = createActionIcon(24, 24, "✕", ThemeColors.DANGER);
    public static final ImageIcon REMOVE = createActionIcon(24, 24, "−", ThemeColors.DANGER);

    // File chooser and navigation icons
    public static final ImageIcon HOME = createHomeIcon(24, 24);
    public static final ImageIcon LIST = createListIcon(24, 24);

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
            // First try to load from classpath resources
            URL resourceUrl = ThemeIcons.class.getResource("/icons/" + filename);
            
            // If not found in classpath, try multiple fallback locations
            if (resourceUrl == null) {
                // Try relative to current directory
                resourceUrl = ThemeIcons.class.getResource("../../../icons/" + filename);
            }
            
            if (resourceUrl == null) {
                // Try absolute path in icons directory
                resourceUrl = new File("icons/" + filename).toURI().toURL();
            }
            
            if (resourceUrl == null) {
                throw new Exception("Icon not found: " + filename);
            }
            
            // Load and verify the image
            ImageIcon originalIcon = new ImageIcon(resourceUrl);
            if (originalIcon.getIconWidth() <= 0 || originalIcon.getIconHeight() <= 0) {
                throw new Exception("Invalid icon dimensions for: " + filename);
            }
            
            // Resize if necessary
            if (originalIcon.getIconWidth() != width || originalIcon.getIconHeight() != height) {
                Image scaledImage = originalIcon.getImage().getScaledInstance(
                    width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
            
            return originalIcon;
            
        } catch (Exception e) {
            // Log the error with more detail
            System.err.println("Failed to load icon: " + filename);
            System.err.println("Error details: " + e.getMessage());
            System.err.println("Attempted locations:");
            System.err.println("- classpath:/icons/" + filename);
            System.err.println("- classpath:../../../icons/" + filename);
            System.err.println("- file:icons/" + filename);
            
            // Create a more distinctive fallback icon
            return createFallbackIcon(width, height, filename);
        }
    }
    
    private static ImageIcon createFallbackIcon(int width, int height, String filename) {
        // Choose color based on filename or icon type
        Color color = ThemeColors.PRIMARY;
        if (filename.contains("success")) color = ThemeColors.SUCCESS;
        else if (filename.contains("error") || filename.contains("danger")) color = ThemeColors.DANGER;
        else if (filename.contains("warning")) color = ThemeColors.WARNING;
        else if (filename.contains("info")) color = ThemeColors.INFO;
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        // Enable antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a more distinctive fallback icon (circle with border)
        g2.setColor(color);
        g2.fillOval(2, 2, width - 4, height - 4);
        g2.setColor(color.darker());
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(2, 2, width - 4, height - 4);
        
        // Add a small "!" in the center for missing icons
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Dialog", Font.BOLD, height / 2));
        FontMetrics fm = g2.getFontMetrics();
        String mark = "!";
        int textX = (width - fm.stringWidth(mark)) / 2;
        int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(mark, textX, textY);
        
        g2.dispose();
        return new ImageIcon(image);
    }

    private static ImageIcon createRoleIcon(int width, int height, String letter) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        // Enable antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw circle background
        g2.setColor(ThemeColors.PRIMARY);
        g2.fillOval(2, 2, width - 4, height - 4);
        
        // Add letter in the center
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Dialog", Font.BOLD, height / 2));
        FontMetrics fm = g2.getFontMetrics();
        int textX = (width - fm.stringWidth(letter)) / 2;
        int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(letter, textX, textY);
        
        g2.dispose();
        return new ImageIcon(image);
    }

    private static ImageIcon createActionIcon(int width, int height, String symbol, Color color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw circle background
        g2.setColor(color);
        g2.fillOval(2, 2, width - 4, height - 4);
        
        // Add symbol in the center
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Dialog", Font.BOLD, height / 2));
        FontMetrics fm = g2.getFontMetrics();
        int textX = (width - fm.stringWidth(symbol)) / 2;
        int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(symbol, textX, textY);
        
        g2.dispose();
        return new ImageIcon(image);
    }

    private static ImageIcon createNotificationIcon(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw bell shape
        g2.setColor(ThemeColors.PRIMARY);
        int[] xPoints = {width/2, width-4, width-6, 4, 6};
        int[] yPoints = {4, height-8, height-4, height-4, height-8};
        g2.fillPolygon(xPoints, yPoints, 5);
        
        // Draw small circle at top
        g2.fillOval(width/2-2, 2, 4, 4);
        
        g2.dispose();
        return new ImageIcon(image);
    }

    private static ImageIcon createMessageIcon(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw message bubble
        g2.setColor(ThemeColors.PRIMARY);
        g2.fillRoundRect(2, 2, width-8, height-8, 8, 8);
        
        // Draw pointer
        int[] xPoints = {width-6, width-2, width-6};
        int[] yPoints = {height-6, height-2, height-2};
        g2.fillPolygon(xPoints, yPoints, 3);
        
        g2.dispose();
        return new ImageIcon(image);
    }

    private static ImageIcon createImportIcon(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw arrow pointing down into a box
        g2.setColor(ThemeColors.PRIMARY);
        g2.setStroke(new BasicStroke(2));
        
        // Box
        g2.drawRect(4, height-8, width-8, 6);
        
        // Arrow
        g2.drawLine(width/2, 2, width/2, height-8);
        g2.fillPolygon(
            new int[]{width/2-4, width/2+4, width/2},
            new int[]{height-12, height-12, height-8},
            3
        );
        
        g2.dispose();
        return new ImageIcon(image);
    }

    private static ImageIcon createSortIcon(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(ThemeColors.PRIMARY);
        
        // Up arrow
        g2.fillPolygon(
            new int[]{4, width/2, width-4},
            new int[]{height/2, 4, height/2},
            3
        );
        
        // Down arrow
        g2.fillPolygon(
            new int[]{4, width/2, width-4},
            new int[]{height/2, height-4, height/2},
            3
        );
        
        g2.dispose();
        return new ImageIcon(image);
    }

    private static ImageIcon createUploadIcon(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(ThemeColors.PRIMARY);
        g2.setStroke(new BasicStroke(2));
        
        // Box at top
        g2.drawRect(4, 2, width-8, 6);
        
        // Arrow pointing up
        g2.drawLine(width/2, height-2, width/2, 8);
        g2.fillPolygon(
            new int[]{width/2-4, width/2+4, width/2},
            new int[]{12, 12, 8},
            3
        );
        
        g2.dispose();
        return new ImageIcon(image);
    }

    private static ImageIcon createDownloadIcon(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(ThemeColors.PRIMARY);
        g2.setStroke(new BasicStroke(2));
        
        // Box at bottom
        g2.drawRect(4, height-8, width-8, 6);
        
        // Arrow pointing down
        g2.drawLine(width/2, 2, width/2, height-8);
        g2.fillPolygon(
            new int[]{width/2-4, width/2+4, width/2},
            new int[]{height-12, height-12, height-8},
            3
        );
        
        g2.dispose();
        return new ImageIcon(image);
    }

    private static ImageIcon createLoginIcon(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(ThemeColors.PRIMARY);
        
        // Door frame
        g2.drawRect(width/2, 2, width/2-4, height-4);
        
        // Arrow pointing right
        g2.drawLine(4, height/2, width/2, height/2);
        g2.fillPolygon(
            new int[]{width/2-4, width/2-4, width/2},
            new int[]{height/2-4, height/2+4, height/2},
            3
        );
        
        g2.dispose();
        return new ImageIcon(image);
    }

    private static ImageIcon createProfileIcon(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(ThemeColors.PRIMARY);
        
        // Head
        g2.fillOval(width/4, 2, width/2, height/2);
        
        // Body
        g2.fillArc(2, height/2-2, width-4, height, 0, 180);
        
        g2.dispose();
        return new ImageIcon(image);
    }

    private static ImageIcon createHomeIcon(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(ThemeColors.PRIMARY);
        
        // Roof
        g2.fillPolygon(
            new int[]{2, width/2, width-2},
            new int[]{height/2, 2, height/2},
            3
        );
        
        // House body
        g2.fillRect(4, height/2, width-8, height/2-2);
        
        g2.dispose();
        return new ImageIcon(image);
    }

    private static ImageIcon createListIcon(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(ThemeColors.PRIMARY);
        
        // Draw three lines with bullets
        for (int i = 0; i < 3; i++) {
            int y = 6 + i * (height-8)/2;
            g2.fillOval(4, y-2, 4, 4);
            g2.fillRect(12, y-1, width-16, 2);
        }
        
        g2.dispose();
        return new ImageIcon(image);
    }
} 