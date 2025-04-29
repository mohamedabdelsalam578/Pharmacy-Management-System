package gui.theme;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ThemeFonts {
    // Font selection logic
    private static final String[] FUTURISTIC_FONTS = {
        "Segoe UI", "Roboto", "Poppins", "Orbitron", "Audiowide", "Exo", "Teko", "Rajdhani", "Quantico", "Titillium Web", 
        "Agency FB", "Conthrax", "Anurati", "Industry", "Prototype", "Nasalization",
        "Eurostile", "Chakra Petch", "Roboto Mono", "Inconsolata", "SF Pro Display",
        "Inter", "Montserrat", "Consolas", "Arial"
    };
    
    private static final String PRIMARY_FONT;
    private static final String SECONDARY_FONT;
    
    static {
        // Get available fonts
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Set<String> availableFonts = new HashSet<>(Arrays.asList(ge.getAvailableFontFamilyNames()));
        
        // Find first available futuristic font
        String primary = "Arial"; // Default fallback
        String secondary = "Arial"; // Default fallback
        boolean foundPrimary = false;
        
        for (String font : FUTURISTIC_FONTS) {
            if (!foundPrimary && availableFonts.contains(font)) {
                primary = font;
                foundPrimary = true;
            } else if (foundPrimary && availableFonts.contains(font)) {
                secondary = font;
                break;
            }
        }
        
        PRIMARY_FONT = primary;
        SECONDARY_FONT = secondary;
        
        System.out.println("Using fonts: Primary=" + PRIMARY_FONT + ", Secondary=" + SECONDARY_FONT);
    }
    
    // Regular fonts
    public static final Font REGULAR_SMALL = new Font(PRIMARY_FONT, Font.PLAIN, 12);
    public static final Font REGULAR_MEDIUM = new Font(PRIMARY_FONT, Font.PLAIN, 14);
    public static final Font REGULAR_LARGE = new Font(PRIMARY_FONT, Font.PLAIN, 16);
    public static final Font REGULAR_XLARGE = new Font(PRIMARY_FONT, Font.PLAIN, 18);
    public static final Font REGULAR_XXLARGE = new Font(PRIMARY_FONT, Font.PLAIN, 24);
    
    // Bold fonts
    public static final Font BOLD_SMALL = new Font(PRIMARY_FONT, Font.BOLD, 12);
    public static final Font BOLD_MEDIUM = new Font(PRIMARY_FONT, Font.BOLD, 14);
    public static final Font BOLD_LARGE = new Font(PRIMARY_FONT, Font.BOLD, 16);
    public static final Font BOLD_XLARGE = new Font(PRIMARY_FONT, Font.BOLD, 18);
    public static final Font BOLD_XXLARGE = new Font(PRIMARY_FONT, Font.BOLD, 24);
    
    // Title fonts - using secondary font for variation
    public static final Font TITLE_SMALL = new Font(SECONDARY_FONT, Font.BOLD, 20);
    public static final Font TITLE_MEDIUM = new Font(SECONDARY_FONT, Font.BOLD, 24);
    public static final Font TITLE_LARGE = new Font(SECONDARY_FONT, Font.BOLD, 32);
    
    // Italic Fonts
    public static final Font ITALIC_SMALL = new Font(PRIMARY_FONT, Font.ITALIC, 12);
    public static final Font ITALIC_MEDIUM = new Font(PRIMARY_FONT, Font.ITALIC, 14);
    public static final Font ITALIC_LARGE = new Font(PRIMARY_FONT, Font.ITALIC, 16);
    
    // Bold Italic Fonts
    public static final Font BOLD_ITALIC_SMALL = new Font(PRIMARY_FONT, Font.BOLD | Font.ITALIC, 12);
    public static final Font BOLD_ITALIC_MEDIUM = new Font(PRIMARY_FONT, Font.BOLD | Font.ITALIC, 14);
    public static final Font BOLD_ITALIC_LARGE = new Font(PRIMARY_FONT, Font.BOLD | Font.ITALIC, 16);
    
    // Extra futuristic elements
    public static final Font FUTURISTIC_LARGE = new Font(SECONDARY_FONT, Font.BOLD, 28);
    public static final Font FUTURISTIC_HEADER = new Font(SECONDARY_FONT, Font.BOLD, 36);
    
    // Additional modern hierarchy fonts
    public static final Font BOLD_TITLE = new Font(PRIMARY_FONT, Font.BOLD, 26);
    public static final Font BOLD_SECTION = new Font(PRIMARY_FONT, Font.BOLD, 18);
    public static final Font REGULAR_TEXT = new Font(PRIMARY_FONT, Font.PLAIN, 15);
} 