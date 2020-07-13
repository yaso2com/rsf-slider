package slider.lib;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

@SuppressWarnings("serial")
/**
 */
public class SliderPanel
        extends JPanel
{

    // Gui elements
    private JLabel text;
    private JButton bEdit;
    private EditSliderDialog dialog = null;
    // slider
    private SliderParameters sliderParameters = null;
    private MThumbSlider slider = null;

    /**
     * @param sliderParameters
     */
    public SliderPanel(
            SliderParameters sliderParameters)
    {
        this.sliderParameters = sliderParameters;

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        this.text = new JLabel(sliderParameters.getLabel());
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 0);
        this.add(text, c);

        if (sliderParameters.isShowDialog())
        {
            this.dialog = new EditSliderDialog(sliderParameters);

            this.bEdit = new JButton("Edit");
            this.bEdit.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    editButtonClicked(e);
                }
            });

            c.gridx = 1;
            c.gridy = 0;
            c.anchor = GridBagConstraints.EAST;
            c.insets = new Insets(0, 0, 0, 0);
            this.add(bEdit, c);
        }

        this.slider = new MThumbSlider(sliderParameters);
        sliderParameters.setSlider(slider);
        sliderParameters.setMouseListener(slider);

        //c.weighty = 1.0;   //request any extra vertical space
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.SOUTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 0, 0);
        c.ipady = 120;
        c.ipadx = 250;
        //c.gridwidth = 5;//2 columns wide
        this.add(slider, c);
    }

    public void setMThumbSliderColors(int colorCode)
    {
        this.sliderParameters.setSliderColors(colorCode);
    }

    public double getMinimum()
    {
        return this.sliderParameters.sliderValueToValue(this.slider.getMinimumValue());
    }

    public double getMaximum()
    {
        return this.sliderParameters.sliderValueToValue(this.slider.getMaximumValue());
    }

    public int getTicks()
    {
        return this.slider.getTicks();
    }

    public SliderParameters getSliderParameters()
    {
        return sliderParameters;
    }

    public void setValue(double value,
                         int index)
    {
        this.slider.setValueAt((int) sliderParameters.valueToSliderValue(value), index, true);
    }

    public double getValue(int index)
    {
        return sliderParameters.sliderValueToValue(this.slider.getValueAt(index));
    }

    public int getIntValue(int index)
    {
        return (int) this.slider.getValueAt(index);
    }

    public void setRange(
            double minimum,
            double maximum)
    {
        this.sliderParameters.setRange(minimum, maximum);
        /*
    this.slider.setMinimum(this.sliderParameters.getSliderMinimum());
    this.slider.setMaximum(this.sliderParameters.getSliderMaximum());

    try {
      this.slider.createLabels();
    } catch (Exception ex) {
      System.err.println("SliderPanel.setRange -> createLabels");
    }
    try {
      this.slider.updateUI();
    } catch (Exception ex) {
      System.err.println("SliderPanel.setRange -> updateUI");
    }
         */

        try
        {
            invalidate();
            repaint();
        }
        catch (Exception ex)
        {
            System.err.println("SliderPanel.setRange -> invalidate, repaint");
        }
    }

    public void setMinimum(double minimum)
    {
        this.sliderParameters.setMinimum(minimum);
        /*
    this.slider.setMinimum(this.sliderParameters.getSliderMinimum());
    this.slider.setMaximum(this.sliderParameters.getSliderMaximum());

    try {
      this.slider.createLabels();
    } catch (Exception ex) {
      System.err.println("SliderPanel.setMinimum -> createLabels");
    }
    try {
      this.slider.updateUI();
    } catch (Exception ex) {
      System.err.println("SliderPanel.setMinimum -> updateUI");
    }
         */

        try
        {
            invalidate();
            repaint();
        }
        catch (Exception ex)
        {
            System.err.println("SliderPanel.setMinimum -> invalidate, repaint");
        }
    }

    public void setMaximum(double maximum)
    {
        this.sliderParameters.setMaximum(maximum);
        /*
    this.slider.setMinimum(this.sliderParameters.getSliderMinimum());
    this.slider.setMaximum(this.sliderParameters.getSliderMaximum());

    try {
      this.slider.createLabels();
    } catch (Exception ex) {
      System.err.println("SliderPanel.setMaximum -> createLabels");
    }
    try {
      this.slider.updateUI();
    } catch (Exception ex) {
      System.err.println("SliderPanel.setMaximum -> updateUI");
    }
         */

        try
        {
            invalidate();
            repaint();
        }
        catch (Exception ex)
        {
            System.err.println("SliderPanel.setMaximum -> invalidate, repaint");
        }
    }

    public void setNumberInRange(
            List<Integer> box,
            List<Integer> numberInRange
    )
    {
        this.slider.setNumberInRange(box, numberInRange);
        invalidate();
        repaint();
    }

    public int getNumberInRange(int box)
    {
        return this.slider.getNumberInRange(box);
    }

    public void paint(Graphics g)
    {
        super.paint(g);
    }

    private void editButtonClicked(ActionEvent e)
    {
        if (sliderParameters.isShowDialog())
        {
            sliderParameters.setSlider(slider);
            dialog.setLocation(400, 0);// to have the edit dialog next to the slider dialog not above it
            dialog.setVisible(true);
        }
    }

    public List<Integer> getTickValues()
    {
        return slider.getTickValues();
    }
}
