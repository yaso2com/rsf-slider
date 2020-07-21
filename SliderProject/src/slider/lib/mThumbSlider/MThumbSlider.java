/**
 *
 */
package slider.lib.mThumbSlider;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JSlider;
import slider.lib.SliderParameters;

/**
 * Multi-Thumb Slider based on JSlider
 *
 */
public class MThumbSlider
    extends JSlider {

    private static final String uiClassID = "MThumbSliderUI";
    // Slider Parameters
    private SliderParameters sliderParameters = null;
    // Thumbs
    protected int numberOfThumbs;
    protected ArrayList<BoundedRangeModel> sliderModels;
    protected ArrayList<Icon> thumbRenderers;
    // Thumb has been moved
    protected int thumbMoved = -1;
    // histogram
    protected HashMap<Integer, Integer> boxHeight = new HashMap<>();
    protected HashMap<Integer, Integer> numberInRange = new HashMap<>();
    private boolean recomputeHistogram = false;
    private int maxHistogram = 1;
    protected int[] rangeCounts;

    /**
     * Constructor
     *
     * @param sliderParameters slider parameters
     */
    public MThumbSlider(
        SliderParameters sliderParameters) {// n= number of thumbs in the multithumb slider
        // store slider parameters
        this.sliderParameters = sliderParameters;

        // create thumbs
        createThumbs();

        // set properties
        setPaintTicks(true);
        setPaintLabels(true);
        setSnapToTicks(true);
        putClientProperty("JSlider.isFilled", Boolean.TRUE);
        setSnapToTicks(true);
        setMajorTickSpacing(sliderParameters.getMajorTickSpacing());
        setMinorTickSpacing(sliderParameters.getMinorTickSpacing());

        createLabels();
        updateUI();
    }

    /**
     * Create thumbs.
     *
     */
    private void createThumbs() {
        numberOfThumbs = sliderParameters.getNumberOfThumbs();

        // create array of range counts
        rangeCounts = new int[numberOfThumbs + 1];

        // create slider models and thumb renderer
        sliderModels = new ArrayList<>(numberOfThumbs);
        thumbRenderers = new ArrayList<>(numberOfThumbs);

        // compute minimum and maximum
        int minimum = sliderParameters.getSliderMinimum();
        int maximum = sliderParameters.getSliderMaximum();

//    System.err.println("Bounds " + minimum + " " + maximum);
        for (int i = 0; i < numberOfThumbs; i++) {
            sliderModels.add(i, new DefaultBoundedRangeModel(minimum, 0, minimum, maximum)); // (initial posion of the thumbs, extent, min, max)
            thumbRenderers.add(i, getThumbRenderer(i, numberOfThumbs));
        }

        super.setMinimum(minimum);
        super.setMaximum(maximum);
    }

    public void updateThumbs(int n) {
        if (n > numberOfThumbs) {
            sliderModels.ensureCapacity(n);
            thumbRenderers.ensureCapacity(n);
            BoundedRangeModel dbrm = sliderModels.get(0);
            for (int i = numberOfThumbs; i < n; i++) {
                sliderModels.add(i, new DefaultBoundedRangeModel(dbrm.getValue(), dbrm.getExtent(), dbrm.getMinimum(), dbrm.getMaximum()));
                thumbRenderers.add(i, getThumbRenderer(i, numberOfThumbs));
                setValueAt(getValueAt(numberOfThumbs - 1), // gives the value of the right most thumb
                           i, true); // this sets the new thumb to the most right side + should recalculate the geometry
            }
            numberOfThumbs = n;
            for (int i = 0; i < n; i++) {
                thumbRenderers.set(i, getThumbRenderer(i, numberOfThumbs));
            }
        } else {
            for (int i = numberOfThumbs; i > n; i--) {
                sliderModels.remove(i - 1);
                thumbRenderers.remove(i - 1);
            }
            numberOfThumbs = n;
            for (int i = 0; i < n; i++) {
                thumbRenderers.set(i, getThumbRenderer(i, numberOfThumbs));
            }
        }
        createLabels();
    }

    public int getThumbMoved() {
        int thumbMovedResult = thumbMoved;
        thumbMoved = -1;
        return thumbMovedResult;
    }

    public void setThumbMoved(int thumbMoved) {
        this.thumbMoved = thumbMoved;
    }

    public void updateUI() {
        // AssistantUIManager.setUIName(this);
        // super.updateUI();

        // a different way
        //
        updateLabelUIs();
        //setUI(AssistantUIManager.createUI(this));
        //setUI(new BasicMThumbSliderUI(this));
        //setUI(new MotifMThumbSliderUI(this));
        setUI(new MetalMThumbSliderUI(this));
    }

    public void createLabels() {
        Hashtable<Integer, JLabel> labels = sliderParameters.createLabels(super.getMinimum(), super.getMaximum());
        if (labels != null) {
            setLabelTable(labels);
        } else {
            setLabelTable(createStandardLabels(getMajorTickSpacing(), getMinimum()));
        }
    }

    public String getUIClassID() {
        return uiClassID;
    }

    /*
     * this is the number of thumbs in the multithumb slider
     */
    /**
     * *****************************************************
     */
    public int getNumberOfThumbs() {
        return numberOfThumbs;
    }

    public int getValueAt(int index) {
        return getModelAt(index).getValue();
    }

    public void setValueAt(
        int value,
        int index,
        boolean recalculateGeometry) {
        setThumbMoved(index);
        try {
            if (index > 0) {
                if (value < getModelAt(index - 1).getValue()) {
                    System.err.println("MThumbSlider.setValueAt " + index
                                       + " - value too small: " + value
                                       + " < " + getModelAt(index - 1).getValue()
                    );
                    return;
                }
            }
            if (index < numberOfThumbs - 1) {
                if (value > getModelAt(index + 1).getValue()) {
                    System.err.println("MThumbSlider.setValueAt " + index + " - value too large: " + value);
                    return;
                }
            }
//      System.err.println("MThumbSlider.setValueAt - set Value " + value + " - " + index);
            getModelAt(index).setValue(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (recalculateGeometry) {
            // should I fire?
            ((MetalMThumbSliderUI) getUI()).calculateGeometry();
        }
    }

    public void calculateGeometry() {
        ((MetalMThumbSliderUI) getUI()).calculateGeometry();
    }

    public void setMinimum(double minimum) {
        for (int i = 0; i < sliderModels.size(); i++) {
            if (sliderParameters.isLogScale()) {
                sliderModels.get(i).setMinimum(getExponent(minimum) - 1);//(initial position of the thumbs, extent ,min, max)
            } else {
                sliderModels.get(i).setMinimum((int) minimum);
            }
        }
        if (sliderParameters.isLogScale()) {
            super.setMinimum(getExponent(minimum));
        } else {
            super.setMinimum((int) minimum);
        }
    }

    public double getMinimumValue() {
        if (sliderParameters.isLogScale()) {
            return getModelAt(0).getMinimum();
        } else {
            return getModelAt(0).getMinimum();
        }
    }

    public void setMaximum(double maximum) {
        for (int i = 0; i < sliderModels.size(); i++) {
            if (sliderParameters.isLogScale()) {
                sliderModels.get(i).setMaximum(getExponent(maximum) - 1);//(initial posion of the thumbs, extent ,min, max)
            } else {
                sliderModels.get(i).setMaximum((int) maximum);
            }
        }
        if (sliderParameters.isLogScale()) {
            super.setMaximum(getExponent(maximum));
        } else {
            super.setMaximum((int) maximum);
        }
    }

    public double getMaximumValue() {
        if (sliderParameters.isLogScale()) {
            return getModelAt(0).getMaximum();
        } else {
            return getModelAt(0).getMaximum();
        }
    }

    private BoundedRangeModel getModelAt(int index) {
        return sliderModels.get(index);
    }

    /**
     *
     * @param index
     * @return thumb renderer
     */
    public Icon getThumbRendererAt(int index) {
        return thumbRenderers.get(index);
    }

    public void setThumbRendererAt(Icon icon,
                                   int index) {
        thumbRenderers.add(index, icon);
    }

    private int getExponent(double value)
        throws NumberFormatException {
        int expValue = 0;
        Pattern p = Pattern.compile(".*[eE](.*)");
        Matcher m = p.matcher((new Double(value)).toString());
        if (m.matches()) {
            expValue = Integer.parseInt(m.group(1));
        }
        return expValue;
    }

    /*
     * Returns an ImageIcon, or null if the path was invalid.
     */
    /**
     * ******************************************************
     */
    protected static ImageIcon createImageIcon(String path) {
        URL imgURL = MThumbSlider.class.getResource(path);// to find the path of the icon
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Could not find image in system: " + path);
            return null;
        }
    }

    private Icon getThumbRenderer(int number,
                                  int numberOfThumbs) {
        if (((MetalMThumbSliderUI) getUI()).getOrientation() == JSlider.HORIZONTAL) {
            return getThumbRendererBestHorizontal(number, numberOfThumbs);
        } // end if horizontal
        else {// the slider is vertical
            if (number == 1 || number == 4) {
                return ((MetalMThumbSliderUI) getUI()).getVertThumbIcon();
            } else {
                ImageIcon MyThumbIconVertical = createImageIcon(
                    "images/UpDownThumbVirticalSlider.png");
                return (Icon) MyThumbIconVertical;
            }
        }
    }

    private Icon getThumbRendererBestHorizontal(
        int thumbNumber,
        int numberOfThumbs) {
        if (numberOfThumbs % 2 == 0) {
            if (thumbNumber < numberOfThumbs / 2) {
                ImageIcon MyThumbIconHorizontalUp = createImageIcon(
                    "images/UpThumbHorizontalSlider.png");
                return (Icon) MyThumbIconHorizontalUp;
            } else {
                ImageIcon MyThumbIconHorizontalDown = createImageIcon(
                    "images/DownThumbHorizontalSlider.png");
                return (Icon) MyThumbIconHorizontalDown;
            }
        } else if (numberOfThumbs % 2 != 0) {
            if (thumbNumber < (numberOfThumbs - 1) / 2) {
                ImageIcon MyThumbIconHorizontalUp = createImageIcon(
                    "images/UpThumbHorizontalSlider.png");
                return (Icon) MyThumbIconHorizontalUp;
            } else if (thumbNumber > (numberOfThumbs - 1) / 2) {
                ImageIcon MyThumbIconHorizontalDown = createImageIcon(
                    "images/DownThumbHorizontalSlider.png");
                return (Icon) MyThumbIconHorizontalDown;
            } else {
                ImageIcon MyThumbIconHorizontalUpDown = createImageIcon(
                    "images/MiddleThumb.png");
                return (Icon) MyThumbIconHorizontalUpDown;
            }
        } else {
            return ((MetalMThumbSliderUI) getUI()).getHorizThumbIcon();
        }
    }

    public int getMaxHistogram() {
        return maxHistogram;
    }

    public void setMaxHistogram(int maxHistogram) {
        if (maxHistogram > this.maxHistogram) {
            this.maxHistogram = maxHistogram;
            recomputeHistogram = true;
        }
    }

    public boolean isCategoricalData() {
        return sliderParameters.isCategoricalData();
    }

    public int getBoxHeight(int n) {
        Integer height = boxHeight.get(n);
        if (height == null) {
            height = 1;
        }
        return height;
    }

    public void setBoxHeight(int box,
                             int value) {
        // System.err.println("setBoxHeigth: " + box + " " + value);
        this.boxHeight.put(box, new Integer(value));
    }

    public void setBoxHeightScaled(int box,
                                   int value) {
        setBoxHeight(box,
                     computeHistogramHeightScaledByMaximum(
                         this.getNumberInRange(box)));
    }

    public void setNumberInRange(
        List<Integer> box,
        List<Integer> numberInRange
    ) {
        int maxHistogram = 0;
        // Compute maximum number of MCSs
        for (Integer currentNumber : numberInRange) {
            if (maxHistogram < currentNumber.intValue()) {
                maxHistogram = currentNumber.intValue();
            }
        }
        this.maxHistogram = maxHistogram;

        // create histogram
        //System.out.println("box :" + box + "MCSnoInRange" + MCSnoInRange);
        for (int current = 0; current < box.size(); ++current) {
            this.numberInRange.put(box.get(current), numberInRange.get(current));
            this.setBoxHeightScaled(box.get(current), numberInRange.get(current));//relative to the Max(of the MCSsNoInRanges)
            // System.out.println("box :" + box.get(current) + "numberInRange: " + numberInRange.get(current));
        }
        invalidate();
        repaint();
    }

    public Integer getNumberInRange(int box) {
        return this.numberInRange.get(box);
    }

    public int computeHistogramHeightScaledByMaximum(int numberInRange) {
        int boxHeight = 0;

        double relativeNumberInRange = (double) numberInRange / (double) maxHistogram;

        if (relativeNumberInRange > 0.0 && relativeNumberInRange <= 1.0 / 5.0) {
            boxHeight = 5;
        } else if (relativeNumberInRange > 1.0 / 5.0 && relativeNumberInRange <= 2.0 / 5.0) {
            boxHeight = 10;
        } else if (relativeNumberInRange > 2.0 / 5.0 && relativeNumberInRange <= 3.0 / 5.0) {
            boxHeight = 15;
        } else if (relativeNumberInRange > 3.0 / 5.0 && relativeNumberInRange <= 4.0 / 5.0) {
            boxHeight = 20;
        } else if (relativeNumberInRange > 4.0 / 5.0 && relativeNumberInRange < 1.0) {
            boxHeight = 25;
        } else if (relativeNumberInRange >= 1.0) {
            boxHeight = 30;
        }

        return boxHeight;
    }

    public SliderParameters getSliderParameters() {
        return sliderParameters;
    }

    public int getTicks() {
        if (sliderModels != null) {
            return Math.abs(
                sliderModels.get(sliderModels.size() - 1).getMaximum()
                - sliderModels.get(0).getMinimum());
        }

        return 0;
    }

    public List<Integer> getTickValues() {
        List<Integer> tickValues = new ArrayList<Integer>();
        Dictionary labelDictionary = getLabelTable();

        Object label;
        for (Enumeration labels = labelDictionary.elements();
             labels.hasMoreElements();) {
            label = labels.nextElement();
            if (label instanceof JLabel) {
                tickValues.add(new Integer(((JLabel) label).getText()));
            }
        }

        Collections.sort(tickValues);
        return tickValues;
    }

        public void updateNumberOfThumbs(
            int numberOfThumbs
        )
    {
        updateThumbs(numberOfThumbs);
        updateColors();
        
        invalidate();
        repaint();
    }

    public void updateColors(
    ) {
        updateRangeCounts();
        sendMessage();
        updateUI();
    }


    /**
     * Compute range counts
     */
    public void updateRangeCounts(
    ) {
        if (numberOfThumbs != rangeCounts.length - 1) {
            rangeCounts = new int[numberOfThumbs + 1];
        } else {
            for (int i = 0; i < rangeCounts.length; ++i) {
                rangeCounts[i] = 0;
            }
        }

        // create histogram
        for (Map.Entry<Integer, Integer> box : numberInRange.entrySet()) {
            int count = box.getValue();
            if (count > 0) {
                int position = box.getKey();
                int thumbNumber;
                for (thumbNumber = 0; thumbNumber < numberOfThumbs; ++thumbNumber) {
                    //System.err.println("Check " + position + " < " + getValueAt(thumbNumber));
                    if (position < getValueAt(thumbNumber)) {
                        rangeCounts[thumbNumber] += count;
                        break;
                    }
                }
                if (thumbNumber == numberOfThumbs) {
                    rangeCounts[thumbNumber] += count;
                }
            }
        }

        /*
    for (int position = 0; position < rangeCounts.length; ++position) {
      System.err.println("Count " + position + " = " + rangeCounts[position]);
    }
         */
    }

    public int[] getRangeCounts() {
        return rangeCounts;
    }

    public void sendMessage() {
        sliderParameters.sendMessage();
    }
}
