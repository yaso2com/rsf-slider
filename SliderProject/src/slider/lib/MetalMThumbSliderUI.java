/**
 *
 */
package slider.lib;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalSliderUI;
import java.awt.*;

/**
 *
 */
public class MetalMThumbSliderUI
        extends MetalSliderUI
        implements MThumbSliderAdditional
{

    MThumbSliderAdditionalUI additionalUi;
    MouseInputAdapter mThumbTrackListener;
    final MThumbSlider parent;

    public static ComponentUI createUI(JComponent c)
    {
        return new MetalMThumbSliderUI((MThumbSlider) c);
    }

    public MetalMThumbSliderUI(MThumbSlider parent)
    {
        this.parent = parent;
    }

    @Override
    public void installUI(JComponent c)
    {
        additionalUi = new MThumbSliderAdditionalUI(this);
        additionalUi.installUI(c);
        mThumbTrackListener = createMThumbTrackListener((JSlider) c);
        super.installUI(c);
    }

    @Override
    public void uninstallUI(JComponent c)
    {
        super.uninstallUI(c);
        additionalUi.uninstallUI(c);
        additionalUi = null;
        mThumbTrackListener = null;
    }

    protected MouseInputAdapter createMThumbTrackListener(JSlider slider)
    {
        return additionalUi.trackListener;
    }

    @Override
    protected TrackListener createTrackListener(JSlider slider)
    {
        return null;
    }

    @Override
    protected ChangeListener createChangeListener(JSlider slider)
    {
        return additionalUi.changeHandler;
    }

    @Override
    protected void installListeners(JSlider slider)
    {
        slider.addMouseListener(mThumbTrackListener);
        slider.addMouseMotionListener(mThumbTrackListener);
        slider.addFocusListener(focusListener);
        slider.addComponentListener(componentListener);
        slider.addPropertyChangeListener(propertyChangeListener);
        slider.getModel().addChangeListener(changeListener);
    }

    @Override
    protected void uninstallListeners(JSlider slider)
    {
        slider.removeMouseListener(mThumbTrackListener);
        slider.removeMouseMotionListener(mThumbTrackListener);
        slider.removeFocusListener(focusListener);
        slider.removeComponentListener(componentListener);
        slider.removePropertyChangeListener(propertyChangeListener);
        slider.getModel().removeChangeListener(changeListener);
    }

    @Override
    public void calculateGeometry()
    {
        try
        {
            super.calculateGeometry();
            additionalUi.calculateThumbsSize();
            additionalUi.calculateThumbsLocation();
        }
        catch (Exception ex)
        {
        }
    }

    @Override
    protected void calculateThumbLocation()
    {
    }

    Icon thumbRenderer;

    @Override
    public void paint(Graphics g,
                      JComponent c)
    {
        Rectangle clip = g.getClipBounds();
        if (slider == null || clip == null)
        {
            return;
        }

        Rectangle[] thumbRects = additionalUi.getThumbRects();
        if (thumbRects == null || thumbRects[0] == null)
        {
            System.err.println("MetalMThumbSliderUI.paint: thumbRects is null");
            return;
        }
        thumbRect = thumbRects[0];
        int thumbNum = additionalUi.getThumbNum();

        // Paint colored lines on the slider
        if (slider.getPaintTrack() && clip.intersects(trackRect))
        {
            boolean filledSlider_tmp = filledSlider;
            filledSlider = false;
            paintTrack(g);
            filledSlider = filledSlider_tmp;

            if (filledSlider)
            {
                g.translate(trackRect.x, trackRect.y);

                Point t1 = new Point(0, 0);
                Point t2 = new Point(0, 0);
                Rectangle maxThumbRect = new Rectangle(thumbRect);
                thumbRect = maxThumbRect;

                if (slider.getOrientation() == JSlider.HORIZONTAL)
                {
                    t2.y = (trackRect.height - 1) - getThumbOverhang();
                    t1.y = t2.y - (getTrackWidth() - 1);
                    //t2.x = trackRect.width - 1;
                    t2.x = thumbRect.width - 1;
                    int maxPosition = xPositionForValue(slider.getMaximum());

                    thumbRect.x = maxPosition - (thumbRect.width / 2) - 2;
                    thumbRect.y = trackRect.y;
                }
                else
                {
                    t1.x = (trackRect.width - getThumbOverhang()) - getTrackWidth();
                    t2.x = (trackRect.width - getThumbOverhang()) - 1;
                    t2.y = trackRect.height - 1;
                    int maxPosition = yPositionForValue(slider.getMaximum());

                    thumbRect.x = trackRect.x;
                    thumbRect.y = maxPosition - (thumbRect.height / 2) - 2;
                }

                if (slider instanceof MThumbSlider)
                {
                    MThumbSlider mThumbSlider = (MThumbSlider) slider;
                    SliderParameters sliderParameters = mThumbSlider.getSliderParameters();
                    Color fillColor = sliderParameters.getTrackFillColor();
                    t1 = computeX(computeY(t1, thumbRects[0], trackRect), thumbRects[0], trackRect);
                    t2 = computeX(computeY(t2, thumbRects[thumbNum - 1], trackRect), thumbRects[thumbNum - 1], trackRect);
                    fillTrack(g, t1, t2, fillColor);

                    for (int i = thumbNum - 1; 1 <= i; i--)
                    {
                        t1 = computeX(computeY(t1, thumbRects[i - 1], trackRect), thumbRects[i - 1], trackRect);
                        t2 = computeX(computeY(t2, thumbRects[i], trackRect), thumbRects[i], trackRect);
//            System.err.println("MetalTMThumbSliderUI.paint " + i + " " + t1.x + " " + t2.x + " " + sliderParameters.getLabel());
                        fillColor = sliderParameters.getSliderColorAt(i - 1);
                        fillTrack(g, t1, t2, fillColor);
                        g.setColor(MetalLookAndFeel.getControlDarkShadow());
                        g.drawLine(t2.x, t1.y, t2.x, t2.y - 1);
                    }
                    g.setColor(MetalLookAndFeel.getControlDarkShadow());
                    g.drawLine(t1.x, t1.y, t1.x, t2.y - 1);
                }
                else
                {
                    fillTrack(g, t1, t2, MetalLookAndFeel.getControlShadow());
                }
                g.translate(-trackRect.x, -trackRect.y);
            }
        }

        // Draw ticks, boxes (histogram)
        if (slider.getPaintTicks() && clip.intersects(tickRect))
        {
            paintTicks(g);
            paintHistogram(g);
        }

        // Draw labels
        if (slider.getPaintLabels() && clip.intersects(labelRect))
        {
            paintLabels(g);
        }

        // Draw the thumbs
        for (int i = thumbNum - 1; 0 <= i; i--)
        {
            thumbRect = thumbRects[i];
            if (clip.intersects(thumbRect))
            {
                thumbRenderer = ((MThumbSlider) slider).getThumbRendererAt(i);
                if (thumbRenderer != null)
                {
                    paintThumb(g);
                }
            }
        }
    }

    @Override
    public void paintThumb(Graphics g)
    {
        thumbRenderer.paintIcon(slider, g, thumbRect.x, thumbRect.y);
    }

    public Point computeX(Point pOrig,
                          Rectangle thumbRect,
                          Rectangle trackRect)
    {
        Point p = pOrig;
        int middleOfThumb = 0;

        if (slider.getOrientation() == JSlider.HORIZONTAL)
        {
            middleOfThumb = thumbRect.x + (thumbRect.width / 2) - trackRect.x;
            p.x = middleOfThumb;
        }
        return p;
    }

    public Point computeY(Point pOrig,
                          Rectangle thumbRect,
                          Rectangle trackRect)
    {
        Point p = pOrig;
        int middleOfThumb = 0;

        if (!(slider.getOrientation() == JSlider.HORIZONTAL))
        {
            middleOfThumb = thumbRect.y + (thumbRect.height / 2) - trackRect.y;
            p.y = middleOfThumb;
        }
        return p;
    }

    public void fillTrack(Graphics g,
                          Point t1,
                          Point t2,
                          Color fillColor)
    {
        //                               t1-------------------
        //                               |                   |
        //                               --------------------t2
//    System.err.println("MetalTMThumbSliderUI.fillTrack " + " " + t1.x + " " + t2.x + " ");
        if (slider.getOrientation() == JSlider.HORIZONTAL)
        {
            if (slider.isEnabled())
            {
                g.setColor(fillColor);
                g.fillRect(t1.x,
                        t1.y + 1,
                        t2.x - t1.x,
                        t2.y - t1.y - 2);
                // g.setColor(fillColor.brighter());
                // g.drawLine(t1.x + 1, t1.y + 1, t2.x + 1, t1.y + 1);
                // g.drawLine(t1.x + 1, t1.y + 1, t1.x + 1, t2.y - 2);
            }
            else
            {
                g.setColor(fillColor);
                g.fillRect(t1.x,
                        t1.y + 1,
                        t2.x - t1.x,
                        t2.y - t1.y - 2);
            }
        }
        else
        {
            if (slider.isEnabled())
            {
                g.setColor(fillColor);
                g.fillRect(t1.x + 1,
                        t1.y,
                        t2.x - t1.x - 2,
                        t2.y - t1.y);
                // g.setColor(slider.getBackground());
                // g.drawLine(t1.x + 1, t1.y, t2.x - 2, t1.y);
                // g.drawLine(t1.x + 1, t1.y, t1.x + 1, t2.y - 2);
            }
            else
            {
                g.setColor(fillColor);
                g.fillRect(t1.x + 1,
                        t1.y,
                        t2.x - t1.x - 2,
                        t2.y - t1.y);
            }
        }
    }

    /**
     * Histogram
     *
     */
    private void paintHistogram(Graphics g)
    {
        Rectangle tickBounds = tickRect;
        g.translate(0, tickBounds.y);

        int value = slider.getMinimum();
        int xPosStart = 0;
        int xPosMid = 0;
        int xPosEnd = 0;
        if (slider.getMinorTickSpacing() > 0)
        {
            while (value < slider.getMaximum())
            {
                if (this.parent.isCategoricalData())
                {
                    // Check for slider minimum and Maximum?
                    xPosStart = xPositionForValue(value - slider.getMinorTickSpacing());
                    xPosMid = xPositionForValue(value);
                    xPosEnd = xPositionForValue(value + slider.getMinorTickSpacing());
                    //xPosStart += (xPosMid - xPosStart) / 2;
                    xPosStart = xPosMid + 5;
                    xPosEnd -= (xPosEnd - xPosMid) / 2;
                }
                else
                {
                    xPosStart = xPositionForValue(value);
                    xPosEnd = xPositionForValue(value + slider.getMinorTickSpacing());
                }

                //Get height of the box
                int height = this.parent.getBoxHeight(value);
                // function to get number of elements
                try
                {
                    Integer numberInRange = this.parent.getNumberInRange(value);
                    if (numberInRange != null)
                    {
                        paintHistogramBar(g, TICK_BUFFER - 30, xPosStart, xPosEnd, -height,//-1 to begin from the lower end and not from the upper end
                                numberInRange.intValue());
                    }
                }
                catch (Throwable thr)
                {
                    System.err.println("Value: " + value);
                    System.err.println(thr.getMessage());
                }
                value += slider.getMinorTickSpacing();
            }
        }
        g.translate(0, -tickBounds.y);
    }

    private void paintHistogramBar(
            Graphics g,
            int y,
            int xFrom,
            int xTo,
            int height,
            int numberInRange)
    {
        if (height != 0)
        {
            g.setColor(Color.black);
            g.fillRect(xFrom, y + height, (xTo - xFrom), -height);
            String numberInRangeString = Integer.toString(numberInRange);
      /*
      //horizontal
      int strWidth = g.getFontMetrics().stringWidth(numberInRangeString);
      int xOffset = (xTo - xFrom - strWidth) / 2;
      int strXPos = xFrom;
      int strYPos = height - 28;// the height is -ve so i am going over the box by 4
      if (xOffset > 0) {
        strXPos += xOffset;
      }
      g.drawString(numberInRangeString, strXPos, strYPos);
      */
            // vertical
            int strXPos = xTo - (xTo - xFrom - g.getFontMetrics().getHeight()) / 2;
            int strYPos = height - 28;// the height is -ve so i am going over the box by 4
            Graphics2D g2d = (Graphics2D) g;
            int rotation = -90;
            g2d.rotate(Math.toRadians(rotation), strXPos, strYPos);
            g2d.drawString(numberInRangeString, strXPos, strYPos);
            g2d.rotate(Math.toRadians(-rotation), strXPos, strYPos);
        }
    }

    /**
     *
     * @param direction
     */
    @Override
    public void scrollByBlock(int direction)
    {
    }

    /**
     *
     * @param direction
     */
    @Override
    public void scrollByUnit(int direction)
    {
    }

    //
    //  MThumbSliderAdditional
    //

    /**
     *
     * @return
     */
    @Override
    public Rectangle getTrackRect()
    {
        return trackRect;
    }

    /**
     *
     * @return
     */
    @Override
    public Dimension getThumbSize()
    {
        return super.getThumbSize();
    }

    /**
     *
     * @param value
     * @return
     */
    @Override
    public int xPositionForValue(int value)
    {
        return super.xPositionForValue(value);
    }

    /**
     *
     * @param value
     * @return
     */
    @Override
    public int yPositionForValue(int value)
    {
        return super.yPositionForValue(value);
    }

    public int getOrientation()
    {
        return slider.getOrientation();
    }

    public Icon getVertThumbIcon()
    {
        return vertThumbIcon;
    }

    public Icon getHorizThumbIcon()
    {
        return horizThumbIcon;
    }
}
