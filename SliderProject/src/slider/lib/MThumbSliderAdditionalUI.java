/* (swing1.1.1) */
package slider.lib;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @version 1.0 09/08/99
 */
public class MThumbSliderAdditionalUI
{

    private MThumbSlider mSlider;
    private BasicSliderUI ui;
    private Rectangle[] thumbRects;
    private int thumbNum;
    private transient boolean isDragging;
    private Rectangle trackRect;
    TrackListener trackListener;
    private static Rectangle unionRect = new Rectangle();

    public MThumbSliderAdditionalUI(BasicSliderUI ui)
    {
        this.ui = ui;
    }

    public void installUI(JComponent c)
    {
        mSlider = (MThumbSlider) c;
        thumbNum = mSlider.getNumberOfThumbs();
        thumbRects = new Rectangle[thumbNum];
        for (int i = 0; i < thumbNum; i++)
        {
            thumbRects[i] = new Rectangle();
        }
        isDragging = false;
        trackListener = new MThumbSliderAdditionalUI.TrackListener(mSlider);
    }

    public void uninstallUI(JComponent c)
    {
        thumbRects = null;
        trackListener = null;
    }

    protected void calculateThumbsSize()
    {
        Dimension size = ((MThumbSliderAdditional) ui).getThumbSize();
        for (int i = 0; i < thumbNum; i++)
        {
            thumbRects[i].setSize(size.width, size.height);
        }
    }

    protected void calculateThumbsLocation()
    {
        for (int i = 0; i < thumbNum; i++)
        {
            if (mSlider.getSnapToTicks())
            {
                int tickSpacing = mSlider.getMinorTickSpacing();
                if (tickSpacing == 0)
                {
                    tickSpacing = mSlider.getMajorTickSpacing();
                }
                if (tickSpacing != 0)
                {
                    int sliderValue = mSlider.getValueAt(i);
                    int min = mSlider.getMinimum();
                    if ((sliderValue - min) % tickSpacing != 0)
                    {
                        float temp = (float) (sliderValue - min) / (float) tickSpacing;
                        int whichTick = Math.round(temp);
                        int snappedValue = min + (whichTick * tickSpacing);
                        mSlider.setValueAt(snappedValue, i, true);
                    }
                }
            }
            trackRect = getTrackRect();
            if (mSlider.getOrientation() == JSlider.HORIZONTAL)
            {
                int value = mSlider.getValueAt(i);
                int valuePosition = ((MThumbSliderAdditional) ui).xPositionForValue(
                        value);
                thumbRects[i].x = valuePosition - (thumbRects[i].width / 2);
                if (thumbNum % 2 == 0)
                {
                    if (i < thumbNum / 2)
                    {
                        thumbRects[i].y = trackRect.y - (i - thumbNum / 2) * 8;
                    }
                    else
                    {
                        thumbRects[i].y = trackRect.y - (i - thumbNum / 2) * 8 - 8;
                    }
                    //12 - 8 * i;// it was 20-10
                }
                else
                {
                    if (i < thumbNum / 2)
                    {
                        thumbRects[i].y = trackRect.y - ((i - thumbNum / 2) * 8 - 4);
                    }
                    else if (i == thumbNum / 2)
                    {
                        thumbRects[i].y = trackRect.y;
                    }
                    else
                    {
                        thumbRects[i].y = trackRect.y - (i - thumbNum / 2) * 8;
                    }
                }
            }
            else
            {
                int valuePosition = ((MThumbSliderAdditional) ui).yPositionForValue(mSlider.getValueAt(i));     // need
                thumbRects[i].x = trackRect.x;
                thumbRects[i].y = valuePosition - (thumbRects[i].height / 2);
            }
        }
    }//end calculateThumbsLocation

    public int getThumbNum()
    {
        return thumbNum;
    }

    public Rectangle[] getThumbRects()
    {
        return thumbRects;
    }

    public void setThumbLocationAt(int x,
                                   int y,
                                   int index,
                                   MThumbSlider slider)
    {
        unionRect.setBounds(thumbRects[index]);

        thumbRects[index].setLocation(x, y);
        SwingUtilities.computeUnion(thumbRects[index].x, thumbRects[index].y, thumbRects[index].width, thumbRects[index].height,
                unionRect);
        mSlider.repaint(unionRect.x, unionRect.y, unionRect.width, unionRect.height);

        switch (slider.getOrientation())
        {
            case JSlider.VERTICAL:
                if (index < thumbRects.length - 1 && unionRect.y > thumbRects[index + 1].y)
                {
                    //if (index < thumbRects.length - 1 && rect.y > thumbRects[index + 1].y) {
                    setThumbLocationAt(y, thumbRects[index + 1].x, index + 1, slider);
          /*
          System.err.println("MThumbSliderAdditionalUI.setThumbLocationAt " + ui.valueForYPosition(y) + " - " + (index + 1));
          if (ui.valueForYPosition(y) != mSlider.getIntValueAt(index + 1)) {
            mSlider.setIntValueAt(ui.valueForYPosition(y),
                                  index + 1);
          }
          */
                }

                if (index > 0 && unionRect.y < thumbRects[index - 1].y)
                {
                    setThumbLocationAt(y, thumbRects[index - 1].x, index - 1, slider);
          /*
          System.err.println("MThumbSliderAdditionalUI.setThumbLocationAt " + ui.valueForYPosition(y) + " - " + (index - 1));
          if (ui.valueForYPosition(y) != mSlider.getIntValueAt(index - 1)) {
            mSlider.setIntValueAt(ui.valueForYPosition(y),
                                  index - 1);
          }
          */
                }
                break;

            case JSlider.HORIZONTAL:
                //if (index < thumbRects.length - 1 && unionRect.x > thumbRects[index + 1].x) {
                if (index < thumbRects.length - 1 && thumbRects[index].x > thumbRects[index + 1].x)
                {
                    setThumbLocationAt(x, thumbRects[index + 1].y, index + 1, slider);
          /*
          System.err.println("MThumbSliderAdditionalUI.setThumbLocationAt " + ui.valueForXPosition(x) + " - " + (index + 1));
          if (ui.valueForXPosition(x) != mSlider.getIntValueAt(index + 1)) {
            mSlider.setIntValueAt(ui.valueForXPosition(x),
                                  index + 1);
          }
          */
                }

                //if (index > 0 && unionRect.x < thumbRects[index - 1].x) {
                if (index > 0 && thumbRects[index].x < thumbRects[index - 1].x)
                {
                    setThumbLocationAt(x, thumbRects[index - 1].y, index - 1, slider);
          /*
          System.err.println("MThumbSliderAdditionalUI.setThumbLocationAt " + ui.valueForXPosition(x) + " - " + (index - 1));
          if (ui.valueForXPosition(x) != mSlider.getIntValueAt(index - 1)) {
            mSlider.setIntValueAt(ui.valueForXPosition(x),
                                  index - 1);
          }
          */
                }
                break;
        }
    }

    public Rectangle getTrackRect()
    {
        return ((MThumbSliderAdditional) ui).getTrackRect();
    }

    /**
     * Internal class for tracking mouse events.
     */
    public class TrackListener
            extends MouseInputAdapter
    {

        // offset and mousePosition
        protected transient int offset;
        protected transient int currentMouseX, currentMouseY;
        // thumb and slider
        protected Rectangle adjustingThumbRect = null;
        protected int adjustingThumbIndex;
        protected MThumbSlider slider;
        protected Rectangle trackRect;

        /**
         * Constructor.
         *
         * @param slider slider
         */
        TrackListener(MThumbSlider slider)
        {
            this.slider = slider;
        }

        @Override
        /** Mouse was pressed.
         *
         * @param e mouse event
         */
        public void mousePressed(MouseEvent e)
        {
            // check if slider is active
            if (!slider.isEnabled())
            {
                return;
            }

            // get current mouse position
            currentMouseX = e.getX();
            currentMouseY = e.getY();
            slider.requestFocus();

            // for all thumbs
            for (int i = 0; i < thumbNum; i++)
            {
                Rectangle rect = thumbRects[i];
                if (rect.contains(currentMouseX, currentMouseY))
                {
                    // mouse is over thumb
                    // compute offset
                    switch (slider.getOrientation())
                    {
                        case JSlider.VERTICAL:
                            offset = currentMouseY - rect.y;
                            break;
                        case JSlider.HORIZONTAL:
                            offset = currentMouseX - rect.x;
                            break;
                    }
                    // set flags and active thumb number
                    isDragging = true;
                    slider.setValueIsAdjusting(true);
                    adjustingThumbRect = rect;
                    adjustingThumbIndex = i;
                    return;
                }
            }
        }

        @Override
        /** Mouse was dragged
         *
         * @param e mouse event
         */
        public void mouseDragged(MouseEvent e)
        {
            // check if event should be handled
            if (!slider.isEnabled()
                    || !isDragging
                    || !slider.getValueIsAdjusting()
                    || adjustingThumbRect == null)
            {
                return;
            }

            // get rectangle of dragged thumb
            Rectangle rect = thumbRects[adjustingThumbIndex];
            trackRect = getTrackRect();

            // branch on orientation
            switch (slider.getOrientation())
            {
                case JSlider.VERTICAL:
                    // compute and set new position
                    int halfThumbHeight = rect.height / 2;
                    int thumbTop = e.getY() - offset;
                    int trackTop = trackRect.y;
                    int trackBottom = trackRect.y + (trackRect.height - 1);

                    thumbTop = Math.max(thumbTop, trackTop - halfThumbHeight);
                    thumbTop = Math.min(thumbTop, trackBottom - halfThumbHeight);

                    setThumbLocationAt(rect.x, thumbTop, adjustingThumbIndex, slider);
                    break;

                case JSlider.HORIZONTAL:
                    // compute and set new position
                    int halfThumbWidth = rect.width / 2;
                    int thumbLeft = e.getX() - offset;
                    int trackLeft = trackRect.x;
                    int trackRight = trackRect.x + (trackRect.width - 1);

                    thumbLeft = Math.max(thumbLeft, trackLeft - halfThumbWidth);
                    thumbLeft = Math.min(thumbLeft, trackRight - halfThumbWidth);

                    setThumbLocationAt(thumbLeft, rect.y, adjustingThumbIndex, slider);
                    break;
            }
        }

        @Override
        /** Mouse was released
         *
         * @param e mouse event
         */
        public void mouseReleased(MouseEvent e)
        {
            if (!slider.isEnabled())
            {
                // if slider is not enabled -> return
                return;
            }

            // from smallest value to the slider that was manipulated (excluded)
            for (int thumbIndex = 0; thumbIndex < adjustingThumbIndex; ++thumbIndex)
            {
                // set new thumb value
                setThumbValue(thumbIndex);
            }

            // from largest value to the slider that was manipulated (included)
            for (int thumbIndex = thumbNum - 1; thumbIndex >= adjustingThumbIndex; --thumbIndex)
            {
                // set new thumb value
                setThumbValue(thumbIndex);
            }

            // calculate the thumb geometries
            mSlider.calculateGeometry();

            // reset offset, dragging, value adjusting
            offset = 0;
            isDragging = false;
            mSlider.setValueIsAdjusting(false);

            // repaint slider
            mSlider.repaint();
        }

        /**
         * Set thumb value
         *
         * @param thumbIndex thumb index
         */
        private void setThumbValue(int thumbIndex)
        {
            int thumbMiddle;

            // get the thumb rectangle and the track rectangle
            Rectangle rect = thumbRects[thumbIndex];
            trackRect = getTrackRect();

            // branch for slide orientation
            switch (slider.getOrientation())
            {
                case JSlider.VERTICAL:
                    // compute thumb position and set value
                    int halfThumbHeight = rect.height / 2;
                    int thumbTop = rect.y;
                    int trackTop = trackRect.y;
                    int trackBottom = trackRect.y + (trackRect.height - 1);

                    thumbTop = Math.max(thumbTop, trackTop - halfThumbHeight);
                    thumbTop = Math.min(thumbTop, trackBottom - halfThumbHeight);

                    thumbMiddle = thumbTop + halfThumbHeight;
                    if (ui.valueForYPosition(thumbMiddle) != mSlider.getValueAt(thumbNum))
                    {
                        mSlider.setValueAt(ui.valueForYPosition(thumbMiddle),
                                thumbIndex, false);
                    }
                    break;

                case JSlider.HORIZONTAL:
                    // compute thumb position and set value
                    int halfThumbWidth = rect.width / 2;
                    int thumbLeft = rect.x;
                    int trackLeft = trackRect.x;
                    int trackRight = trackRect.x + (trackRect.width - 1);

                    thumbLeft = Math.max(thumbLeft, trackLeft - halfThumbWidth);
                    thumbLeft = Math.min(thumbLeft, trackRight - halfThumbWidth);

                    thumbMiddle = thumbLeft + halfThumbWidth;
                    if (ui.valueForXPosition(thumbMiddle) != mSlider.getValueAt(thumbIndex))
                    {
                        mSlider.setValueAt(ui.valueForXPosition(thumbMiddle),
                                thumbIndex, false);
                    }
                    break;
            }
        }

        public boolean shouldScroll(int direction)
        {
            return false;
        }
    }
}
