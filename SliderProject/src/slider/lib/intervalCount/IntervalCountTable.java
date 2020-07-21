/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package slider.lib.intervalCount;

import java.awt.Color;
import java.util.List;
import javax.swing.JTable;
import javax.swing.border.LineBorder;

/**
 *
 * @author zeckzer
 */
public class IntervalCountTable
    extends JTable {

    private IntervalCountTableModel tableModel = null;

    /**
     * Constructor.
     *
     */
    IntervalCountTable() {
        super();
        initTable();
    }

    private void initTable() {
        setAutoCreateRowSorter(false);
        tableModel = new IntervalCountTableModel(this);
        setRowSelectionAllowed(false);
        setEnabled(true);
        setBorder(new LineBorder(Color.black, 1));
        setTableHeader(null);

        //setMinimumSize(new Dimension(60,60));
        //setPreferredSize(new Dimension(60,60));
        //setMaximumSize(new Dimension(60,60));
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    void addEntry(
        final Color color,
        int count
    ) {
        tableModel.set(color, count, tableModel.getRowCount());
    }

    void clearEntries() {
        tableModel.removeAll();
    }

    void setColors(List<Color> colors) {
        tableModel.setColors(colors);
        invalidate();
        repaint();
    }
}
