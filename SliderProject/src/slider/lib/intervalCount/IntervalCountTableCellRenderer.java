package slider.lib.intervalCount;

// Import
import java.awt.Color;
import java.awt.Component;
import java.text.Format;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;

/** Render a cell containing numbers.
 *
 */
public class IntervalCountTableCellRenderer
    extends DefaultTableCellRenderer {

    final private JTextArea textArea = new JTextArea();

    // Format used for rendering
    private Format format = null;

    private IntervalCountTableModel tm = null;

    /** Constructor.
     *
     */
    IntervalCountTableCellRenderer(
        IntervalCountTableModel tm
    ) {
        super();

        this.tm = tm;
    }

    /** set format.
     *
     * @param format number format
     */
    void setFormat(
        Format format
    ) {
        this.format = format;
    }

    /** set formatted value. overrides parent method.
     *
     * @param value value to set
     */
    @Override
    protected void setValue(
        Object value
    ) {
        if ((format != null)
            && (value != null)
            && (value instanceof Double)) {
            super.setValue(format.format((Double) value));
        } else {
            super.setValue(value);
        }
    }

    /** Overload getTableCellRendererComponent.
     *
     * @param table table
     * @param value value
     * @param isSelected true iff cell should be rendered as selected
     * @param hasFocus true iff cell has focus
     * @param row row
     * @param column column
     * @return 
     */
    @Override
    public Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column
    ) {
        // Get component
        Component tcrc = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setCellColor(table, row, column, isSelected, tcrc);

        return tcrc;
    }

    /**
     * Set cell color
     * @param table table
     * @param row row
     * @param column column
     * @param isSelected true iff cell should be rendered as selected
     * @param tcrc cell renderer component
     */
    void setCellColor(
        JTable table,
        int row,
        int column,
        boolean isSelected,
        Component tcrc
    ) {
        Color cellColor = tm.getCellColour(row, column);
        Color textColor = tm.getTextColour(row, column);

        // Set color of cell
        if (isSelected) {
            tcrc.setBackground(Color.yellow);
            tcrc.setForeground(textColor);
        } else {
            tcrc.setBackground(cellColor);
            tcrc.setForeground(textColor);
        }
    }
}
