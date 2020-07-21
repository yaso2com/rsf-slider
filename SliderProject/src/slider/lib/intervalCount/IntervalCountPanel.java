package slider.lib.intervalCount;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Panel for selecting elements.
 *
 */
public class IntervalCountPanel
    extends JPanel {

    // Table displayed in this panel
    private IntervalCountTable table = null;

    // Caption
    //private final JLabel caption = new JLabel();
    /**
     * Constructor.
     *
     * @param componentCaption caption
     */
    public IntervalCountPanel(
        String componentCaption
    ) {
        initGUI();
    }

    private void initGUI() {
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(50, 5, 5, 5));

        /*
    setCaption(componentCaption);
    caption.setHorizontalAlignment(SwingConstants.CENTER);
    this.add(caption, BorderLayout.NORTH);
         */
        table = new IntervalCountTable();
        //table.setRowSorter(getSorter(table));

        JScrollPane jsp = new JScrollPane(table);
        jsp.setColumnHeaderView(table.getTableHeader());
        jsp.setViewportView(table);
        jsp.setMinimumSize(new Dimension(80, 50));
        jsp.setPreferredSize(new Dimension(80, 50));
        jsp.setMaximumSize(new Dimension(80, 50));
        jsp.setBorder(new EmptyBorder(0, 0, 0, 0));

        this.add(jsp, BorderLayout.CENTER);

        /*
    Dimension size = new Dimension(30, 10);
    this.setMinimumSize(size);
    this.setPreferredSize(size);
    this.setMaximumSize(size);
         */
        this.fitHeight();
    }

    /**
     * Add table entry.
     * @param color
     * @param count
     */
    public void addEntry(
        final Color color,
        final int count
    ) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                table.addEntry(color, count);
            }
        });
    }

    /**
     * Fit height of table columns.
     */
    public void fitHeight() {
        /*
    JTableHeader header = this.table.getTableHeader();
    assert (header.getColumnModel().getColumnCount() > 0);
    TableColumn column = header.getColumnModel().getColumn(0);
    TableCellRenderer headerRenderer = column.getHeaderRenderer();
    if (headerRenderer == null) {
      headerRenderer = header.getDefaultRenderer();
    }

    int height = 0;

    Component comp = headerRenderer.getTableCellRendererComponent(table,
                                                                  column.getHeaderValue(),
                                                                  false, false,
                                                                  0, 0);
    height = comp.getPreferredSize().height
            + table.getIntercellSpacing().height;
    header.getPreferredSize().height = height;
    header.getMinimumSize().height = height;
    header.getMaximumSize().height = height;

    this.table.setRowHeight(height);
         */
    }

    /**
     * Remove all entries.
     */
    public void clearEntries() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                table.clearEntries();
            }
        });
    }

    public void setCaption(String componentCaption) {
        //caption.setText(componentCaption);
    }

    public void setColors(List<Color> colors) {
        table.setColors(colors);
    }
}
