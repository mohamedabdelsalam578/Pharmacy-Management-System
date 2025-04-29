package gui.components;

import gui.theme.ThemeColors;
import gui.theme.ThemeFonts;
import gui.theme.ThemeSizes;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class StyledTable<T> extends JTable {
    private List<T> data;
    private final String[] columnNames;
    private final RowMapper<T> rowMapper;
    
    public interface RowMapper<T> {
        Object[] map(T item);
    }
    
    /**
     * Constructor that accepts a DefaultTableModel
     * 
     * @param model The table model to use
     */
    public StyledTable(DefaultTableModel model) {
        super(model);
        this.columnNames = new String[model.getColumnCount()];
        for (int i = 0; i < model.getColumnCount(); i++) {
            this.columnNames[i] = model.getColumnName(i);
        }
        this.rowMapper = null;
        
        setupTableAppearance();
    }
    
    public StyledTable(String[] columnNames, RowMapper<T> rowMapper) {
        this.columnNames = columnNames;
        this.rowMapper = rowMapper;
        
        // Set table properties
        setModel(new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        
        setupTableAppearance();
    }
    
    /**
     * Setup the visual appearance of the table
     */
    private void setupTableAppearance() {
        setFont(ThemeFonts.REGULAR_MEDIUM);
        setForeground(ThemeColors.TEXT_PRIMARY);
        setBackground(ThemeColors.SURFACE);
        setGridColor(ThemeColors.BORDER);
        setShowGrid(true);
        setRowHeight(ThemeSizes.TABLE_ROW_HEIGHT);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setSelectionBackground(ThemeColors.HOVER);
        setSelectionForeground(ThemeColors.TEXT_PRIMARY);
        
        // Customize header
        JTableHeader header = getTableHeader();
        header.setFont(ThemeFonts.BOLD_MEDIUM);
        header.setBackground(ThemeColors.PRIMARY);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        
        // Set column widths
        for (int i = 0; i < getColumnCount(); i++) {
            getColumnModel().getColumn(i).setPreferredWidth(100);
        }
        
        // Customize cell renderer
        setDefaultRenderer(Object.class, new TableCellRenderer() {
            private final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            
            @Override
            public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column
            ) {
                Component c = renderer.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column
                );
                
                // Set cell properties
                c.setFont(ThemeFonts.REGULAR_MEDIUM);
                c.setForeground(isSelected ? ThemeColors.TEXT_PRIMARY : ThemeColors.TEXT_PRIMARY);
                c.setBackground(isSelected ? ThemeColors.HOVER : ThemeColors.SURFACE);
                
                // Add padding
                if (c instanceof JComponent) {
                    ((JComponent) c).setBorder(new EmptyBorder(
                        ThemeSizes.PADDING_SMALL,
                        ThemeSizes.PADDING_MEDIUM,
                        ThemeSizes.PADDING_SMALL,
                        ThemeSizes.PADDING_MEDIUM
                    ));
                }
                
                return c;
            }
        });
    }
    
    public void setData(List<T> data) {
        this.data = data;
        DefaultTableModel model = (DefaultTableModel) getModel();
        model.setRowCount(0);
        
        if (data != null && rowMapper != null) {
            for (T item : data) {
                model.addRow(rowMapper.map(item));
            }
        }
    }
    
    public List<T> getData() {
        return data;
    }
    
    public T getSelectedItem() {
        int selectedRow = getSelectedRow();
        if (selectedRow >= 0 && data != null && selectedRow < data.size()) {
            return data.get(selectedRow);
        }
        return null;
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
} 