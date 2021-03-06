package slider.lib.intervalCount;

// Import
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.SwingConstants;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * TableModel for Data table.
 *
 */
public class IntervalCountTableModel
    extends DefaultTableModel {

    // parent
    private IntervalCountTable tabelle = null;

    // colors
    private List<Color> colors = new ArrayList<>();

    /**
     * Constructor.
     *
     */
    IntervalCountTableModel() {
        super();
    }

    /**
     * Constructor.
     *
     * @param tabelle table
     */
    IntervalCountTableModel(
        IntervalCountTable tabelle
    ) {
        super();

        this.tabelle = tabelle;
        initTable();
    }

    /**
     * initialize table
     */
    private void initTable() {
        if (tabelle != null) {
            tabelle.setModel(this);
            
            // add columns for range and for count
            addColumn("Range");
            addColumn("Count");

            // set sizes for the two new columns
            TableColumn rangeColumn = tabelle.getColumnModel().getColumn(0);
            rangeColumn.setMinWidth(30);
            rangeColumn.setMaxWidth(60);
            rangeColumn.setPreferredWidth(30);

            TableColumn countColumn = tabelle.getColumnModel().getColumn(1);
            countColumn.setMinWidth(30);
            countColumn.setMaxWidth(60);
            countColumn.setPreferredWidth(30);

            // create renderer for colors
            IntervalCountTableCellRenderer tcr = new IntervalCountTableCellRenderer(this);
            tcr.setFormat(new DecimalFormat("#0"));
            tcr.setHorizontalAlignment(SwingConstants.RIGHT);

            // set renderer for colors
            rangeColumn.setCellRenderer(tcr);
            countColumn.setCellRenderer(tcr);
        }
    }

    /**
     * return whether cell is editable.
     *
     * @param row row
     * @param col column
     * @return true iff cell is editable
     */
    @Override
    public boolean isCellEditable(
        int row,
        int col
    ) {
        // cells should not be editable.
        return false;
    }

    /**
     * Get class of elements renderer in column.
     *
     * @param column column to get class for
     * @return class
     */
    @Override
    public Class<?> getColumnClass(
        int column
    ) {
        return Integer.class;
    }

    /**
     * Add a row to the table.
     *
     * @param color color
     * @param count count
     */
    void add(
        Color color,
        int count
    ) {
        // TODO Auto-generated method stub
        Vector<Object> row = new Vector<>();
        colors.add(color);
        row.add("");
        row.add(count);
        super.addRow(row);
    }

    /**
     * Set values of a row.
     *
     * @param color color
     * @param count count
     * @param row row
     */
    void set(
        Color color,
        int count,
        int row
    ) {
        // TODO Auto-generated method stub
        Vector<Object> rowData = new Vector<>();
        if (colors.size() <= row) {
            colors.add(row, color);
        } else {
            colors.set(row, color);
        }
        rowData.add("");
        rowData.add(new Integer(count));
        insertRow(row, rowData);
    }

    /**
     * Get background color for row.
     *
     * @param row row to get color for
     * @param column column to get color for
     * @return color
     */
    Color getCellColour(
        int row,
        int column
    ) {
        if (column == 0 && row < colors.size()) {
            return colors.get(row);
        }

        return new Color(238, 238, 238);
    }

    /**
     * Get text color for row.
     *
     * @param row row to get color for
     * @param column column to get color for
     * @return color
     */
    Color getTextColour(
        int row,
        int column
    ) {
        return Color.black;
    }

    void removeAll() {
        while (getRowCount() > 0) {
            removeRow(0);
        }
    }

    void setColors(
        List<Color> newColors
    ) {
        for (int oldColor = 1, newColor = newColors.size() - 1;
             newColor >= 0 && oldColor < colors.size() - 1;
             ++oldColor, --newColor)
        {
            colors.set(oldColor, newColors.get(newColor));
        }
    }
}
