package slider.lib;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import slider.lib.mThumbSlider.MThumbSlider;
import slider.lib.edit.EditSliderDialog;
import slider.lib.intervalCount.IntervalCountPanel;

@SuppressWarnings("serial")
/**
 */
public class SliderPanel
    extends JPanel
    implements SliderAction {

    // Name of the panel
    private JLabel text;

    // edit button and panel
    private JButton bEdit;
    private EditSliderDialog dialog = null;

    // slider parameters
    private SliderParameters sliderParameters = null;

    // slider
    private MThumbSlider slider = null;

    // interval count panel
    private IntervalCountPanel intervalCounts = null;

    /**
     * @param sliderParameters
     */
    public SliderPanel(
        SliderParameters sliderParameters
    ) {
        this.sliderParameters = sliderParameters;
        initGUI();
    }
    
    private void initGUI()
    {
        this.setLayout(new BorderLayout());
        this.setBorder(new LineBorder(Color.BLACK));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        this.text = new JLabel(sliderParameters.getLabel());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        centerPanel.add(text, gbc);

        if (sliderParameters.isShowDialog()) {
            this.dialog = new EditSliderDialog(sliderParameters, this);

            this.bEdit = new JButton("Edit");
            this.bEdit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editButtonClicked(e);
                }
            });

            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.insets = new Insets(0, 0, 0, 0);
            centerPanel.add(bEdit, gbc);
        }

        this.slider = new MThumbSlider(sliderParameters);
        sliderParameters.setMouseListener(slider);
        sliderParameters.addAction(this);

        //gbc.weighty = 1.0;   //request any extra vertical space
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.ipady = 120;
        gbc.ipadx = 300; //250;
        //gbc.gridwidth = 5;//2 columns wide
        centerPanel.add(slider, gbc);
        this.add(centerPanel, BorderLayout.CENTER);

        legends(gbc);

        Dimension dimension = new Dimension(900, 200);

        setSize(dimension);
        setMinimumSize(dimension);
        setPreferredSize(dimension);
    }

    private void legends(GridBagConstraints gbc)
    {
        intervalCounts = new IntervalCountPanel("Interval Counts");
        // Show summary values of intervals
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 5, 5, 5);
        // comment out if legend should not be shown
         this.add(intervalCounts, BorderLayout.EAST);
    }

        public void updateNumberOfThumbs(
            int numberOfThumbs
        )
    {
        sliderParameters.updateColors(numberOfThumbs);
        sliderParameters.setNumberOfThumbs(numberOfThumbs);

        slider.updateNumberOfThumbs(numberOfThumbs);
    }

    public void updateColors(
        List<Color> colors
    ) {
        intervalCounts.setColors(sliderParameters.getSliderColors());
        slider.updateColors();
    }

    public double getMinimum() {
        return sliderParameters.sliderValueToValue(slider.getMinimumValue());
    }

    public double getMaximum() {
        return sliderParameters.sliderValueToValue(slider.getMaximumValue());
    }

    public int getTicks() {
        return slider.getTicks();
    }

    public SliderParameters getSliderParameters() {
        return sliderParameters;
    }

    public void setValue(double value,
                         int index) {
        slider.setValueAt((int) sliderParameters.valueToSliderValue(value), index, true);
    }

    public double getValue(int index) {
        return sliderParameters.sliderValueToValue(this.slider.getValueAt(index));
    }

    public int getIntValue(int index) {
        return (int) slider.getValueAt(index);
    }

    public void setRange(
        double minimum,
        double maximum
    )
    {
        sliderParameters.setRange(minimum, maximum);

        try {
            invalidate();
            repaint();
        } catch (Exception ex) {
            System.err.println("SliderPanel.setRange -> invalidate, repaint");
        }
    }

    public void setMinimum(double minimum) {
        sliderParameters.setMinimum(minimum);

        try {
            invalidate();
            repaint();
        } catch (Exception ex) {
            System.err.println("SliderPanel.setMinimum -> invalidate, repaint");
        }
    }

    public void setMaximum(double maximum) {
        sliderParameters.setMaximum(maximum);

        try {
            invalidate();
            repaint();
        } catch (Exception ex) {
            System.err.println("SliderPanel.setMaximum -> invalidate, repaint");
        }
    }

    public void setNumberInRange(
        List<Integer> box,
        List<Integer> numberInRange
    ) {
        slider.setNumberInRange(box, numberInRange);
        slider.updateRangeCounts();
        setIntervalCounts(slider.getRangeCounts());

        invalidate();
        repaint();
    }

    /**
     * Set interval counts.
     *
     * @param rangeCounts range counts
     */
    public void setIntervalCounts(
        int[] rangeCounts
    ) {
        if (sliderParameters.getOrder() == SliderOrder.ASCENDING) {
            intervalCounts.addEntry(getBackground(), rangeCounts[0]);
            for (int i = 1; i < rangeCounts.length - 1; ++i) {
                intervalCounts.addEntry(sliderParameters.getSliderColorAt(i - 1), rangeCounts[i]);
            }
            if (rangeCounts.length > 1) {
                intervalCounts.addEntry(getBackground(), rangeCounts[rangeCounts.length - 1]);
            }
        } else {
            if (rangeCounts.length > 1) {
                intervalCounts.addEntry(getBackground(), rangeCounts[rangeCounts.length - 1]);
                for (int i = rangeCounts.length - 2; i >= 1; --i) {
                    intervalCounts.addEntry(sliderParameters.getSliderColorAt(i - 1), rangeCounts[i]);
                }
            }
            intervalCounts.addEntry(getBackground(), rangeCounts[0]);
        }
    }

    public int getNumberInRange(int box) {
        return slider.getNumberInRange(box);
    }

    public void paint(Graphics g) {
        super.paint(g);
    }

    private void editButtonClicked(ActionEvent e) {
        if (sliderParameters.isShowDialog()) {
            dialog.setLocation(400, 0);// to have the edit dialog next to the slider dialog not above it
            dialog.setVisible(true);
        }
    }

    public List<Integer> getTickValues() {
        return slider.getTickValues();
    }

    @Override
    public void sendMessage() {
        intervalCounts.clearEntries();
        setIntervalCounts(slider.getRangeCounts());
        invalidate();
        repaint();
    }
}
