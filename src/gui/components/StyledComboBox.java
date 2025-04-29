package gui.components;

import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeSizes;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
// Removed UI imports
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
// Removed MouseAdapter/MouseEvent imports as hover effect is removed
import java.util.List;

public class StyledComboBox<T> extends JComboBox<T> {
    // Placeholder remains but painting logic is removed
    private String placeholder;

    public StyledComboBox() {
        this((List<T>) null);
    }

    public StyledComboBox(List<T> items) {
        this(items, null);
    }

    public StyledComboBox(List<T> items, String placeholder) {
        // Use the corrected super constructor
        super(items == null ? (T[]) new Object[0] : items.toArray((T[]) new Object[0]));
        this.placeholder = placeholder;

        // Set basic combo box properties
        setFont(ThemeFonts.REGULAR_MEDIUM);
        setOpaque(true); // Recommended for JComboBox
        setBorder(createBorder());
        setPreferredSize(new Dimension(200, ThemeSizes.COMBO_BOX_HEIGHT));

        // DO NOT set custom UI

        // Add focus listener for border change
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(createFocusedBorder());
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(createBorder());
            }
        });
    }

    private Border createBorder() {
        return new CompoundBorder(
            new LineBorder(ThemeColors.BORDER, 1),
            new EmptyBorder(
                ThemeSizes.PADDING_SMALL,
                ThemeSizes.PADDING_MEDIUM,
                ThemeSizes.PADDING_SMALL,
                ThemeSizes.PADDING_MEDIUM
            )
        );
    }

    private Border createFocusedBorder() {
        return new CompoundBorder(
            new LineBorder(ThemeColors.PRIMARY, 1), // Use PRIMARY for focus border color
            new EmptyBorder(
                ThemeSizes.PADDING_SMALL,
                ThemeSizes.PADDING_MEDIUM,
                ThemeSizes.PADDING_SMALL,
                ThemeSizes.PADDING_MEDIUM
            )
        );
    }

    // Override setEnabled - removed custom appearance changes, rely on L&F
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    // Custom paintComponent removed entirely to avoid conflicts
} 