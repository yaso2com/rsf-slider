/**
 * Slider Parameters.
 */
package slider.lib;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Parameters for sliders
 */
public class SliderParameters
{

    protected MThumbSlider slider = null;
    protected int majorTickSpacing = 10;
    protected int minorTickSpacing = 1;
    protected String label = "";
    protected int numberOfThumbs = 2;
    protected ArrayList<Color> sliderColors = null;
    protected Color trackFillColor = MetalLookAndFeel.getControlShadow();
    protected boolean logScale;
    protected boolean showDialog = false;
    protected boolean categoricalData = false;
    protected List<MouseAdapter> mouseAdapters = new ArrayList<>();
    protected double sliderMinimum = 0.0;
    protected double sliderMaximum = 10.0;
    // Parameters for computing slider range
    protected double minimum = 0.0;
    protected double maximum = 10.0;

    /**
     * Constructor
     */
    public SliderParameters()
    {
    }

    public void setSliderColors(int colorCode)
    {
        if (sliderColors == null)
        {
            sliderColors = new ArrayList<>(numberOfThumbs - 1);
        }

        switch (colorCode)
        {
            case 0:
            {
                setSliderColorAt(0, Color.green);
                setSliderColorAt(1, Color.yellow);
                setSliderColorAt(2, Color.red);
            }
            break;
            case 1:
            {
                setSliderColorAt(0, Color.blue);
                setSliderColorAt(1, Color.yellow);
                setSliderColorAt(2, Color.pink);
            }
            break;
            case 2:
            {
                setSliderColorAt(0, Color.blue);
                setSliderColorAt(1, Color.green);
                setSliderColorAt(2, Color.red);
            }
            break;
            default:
            {
                setSliderColorAt(0, Color.green);
                setSliderColorAt(1, Color.yellow);
                setSliderColorAt(2, Color.red);
            }
            break;
        }

        for (int i = 3; i < numberOfThumbs - 1; ++i)
        {
            setSliderColorAt(i, Color.RED.darker()); // initial color is set to dark red
        }

    }

    /**
     * Returns number of thumbs of Slider
     *
     * @return numberOfThumbs
     */
    public int getNumberOfThumbs()
    {
        return numberOfThumbs;
    }

    /**
     * Sets the number of Thumbs of Slider
     *
     * @param numberOfThumbs
     */
    public void setNumberOfThumbs(int numberOfThumbs)
    {
        this.numberOfThumbs = numberOfThumbs;
    }

    /**
     * @return Colors Array
     */
    public ArrayList getSliderColors()
    {
        return sliderColors;
    }

    /**
     * Set Color of desired position
     *
     * @param index
     * @param color
     */
    public void setSliderColorAt(int index,
                                 Color color)
    {
        if (sliderColors != null)
        {
            while (index >= sliderColors.size())
            {
                sliderColors.add(null);
            }
            this.sliderColors.set(index, color);
        }
    }

    public Color getSliderColorAt(int index)
    {
        if (sliderColors != null
                && index < sliderColors.size()
                && sliderColors.get(index) != null)
        {
            return sliderColors.get(index);
        }
        else
        {
            return MetalLookAndFeel.getControlShadow();
        }
    }

    public void setTrackFillColor(Color color)
    {
        trackFillColor = color;
    }

    public Color getTrackFillColor()
    {
        if (trackFillColor != null)
        {
            return trackFillColor;
        }
        else
        {
            return MetalLookAndFeel.getControlShadow();
        }
    }

    /**
     * Set additional colors or delete unused colors.
     *
     * @param n
     */
    public void additionalColors(int n)
    {
        if (sliderColors != null)
        {
            if (n < numberOfThumbs)
            {
                for (int i = numberOfThumbs; i > n; i--)
                {
                    sliderColors.remove(i - 2);
                }
            }
            else
            {
                for (int i = numberOfThumbs; i < n; i++)
                {
                    sliderColors.ensureCapacity(n - 1);
                    sliderColors.add(i - 1, Color.white);
                }
            }
        }
    }

    /**
     * set parent slider
     *
     * @param slider
     */
    public void setSlider(MThumbSlider slider)
    {
        this.slider = slider;
    }

    /**
     * get parent slider
     *
     * @return
     */
    public MThumbSlider getSlider()
    {
        return slider;
    }

    /**
     * @return
     */
    public boolean isLogScale()
    {
        return logScale;
    }

    /**
     * @param logScale
     */
    public void setLogScale(boolean logScale)
    {
        this.logScale = logScale;
    }

    /**
     *
     */
    public boolean hasColor()
    {
        return (sliderColors != null);
    }

    public int getMajorTickSpacing()
    {
        return majorTickSpacing;
    }

    public void setMajorTickSpacing(int majorTickSpacing)
    {
        this.majorTickSpacing = majorTickSpacing;
    }

    public int getMinorTickSpacing()
    {
        return minorTickSpacing;
    }

    public void setMinorTickSpacing(int minorTickSpacing)
    {
        this.minorTickSpacing = minorTickSpacing;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public void addMouseAdapter(MouseAdapter mouseAdapter)
    {
        mouseAdapters.add(mouseAdapter);
    }

    public void setMouseListener(JSlider slider)
    {
        for (MouseAdapter mouseAdapter : mouseAdapters)
        {
            slider.addMouseListener(mouseAdapter);
        }
    }

    public boolean isShowDialog()
    {
        return showDialog;
    }

    public void setShowDialog(boolean showDialog)
    {
        this.showDialog = showDialog;
    }

    public void setRange(
            double minimum,
            double maximum)
    {
        this.minimum = minimum;
        this.maximum = maximum;

        this.sliderMinimum = minimum;
        this.sliderMaximum = maximum;
    }

    public void setMinimum(double minimum)
    {
        this.minimum = minimum;
        this.sliderMinimum = minimum;
    }

    public void setMaximum(double maximum)
    {
        this.maximum = maximum;
        this.sliderMaximum = maximum;
    }

    public double getMinimum()
    {
        return minimum;
    }

    public double getMaximum()
    {
        return maximum;
    }

    public int getSliderMinimum()
    {
        return (int) sliderMinimum;
    }

    public int getSliderMaximum()
    {
        return (int) sliderMaximum;
    }

    public Hashtable<Integer, JLabel> createLabels(
            int minimumValue,
            int maximumValue)
    {
        return null;
    }

    public double sliderValueToValue(double value)
    {
        if (logScale)
        {
            return Math.pow(10, value);
        }
        else
        {
            return value;
        }
    }

    public double valueToSliderValue(double value)
    {
        if (logScale)
        {
            return getExponent(value);
        }
        else
        {
            return value;
        }
    }

    public int getExponent(double value)
            throws NumberFormatException
    {
        int expValue = 0;
        if (value >= 1.0)
        {
            while (value >= 1.0)
            {
                value /= 10.0;
                ++expValue;
            }
            --expValue;
        }
        else
        {
            while (value < 1.0)
            {
                value *= 10.0;
                --expValue;
            }
        }
        return expValue;
    }

    public boolean isCategoricalData()
    {
        return categoricalData;
    }

    public void setCategoricalData(boolean categoricalData)
    {
        this.categoricalData = categoricalData;
    }

    public void updateSlider()
    {
        if (slider != null)
        {
            slider.setMinimum(sliderMinimum);
            slider.setMaximum(sliderMaximum);
            slider.setMajorTickSpacing(majorTickSpacing);
            slider.setMinorTickSpacing(minorTickSpacing);
            slider.createLabels();
            slider.updateUI();
        }
    }
}
