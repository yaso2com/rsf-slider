/**
 * \mainpage
 * LICENSE: CC BY
 * <p>
 * This license lets others distribute, remix, adapt, and build upon your work, even commercially,
 * as long as they credit you for the original creation. This is the most accommodating of licenses offered.
 * Recommended for maximum dissemination and use of licensed materials.
 * https://creativecommons.org/licenses/by/4.0/
 *
 * @author Yasmin Al-Zokari
 * @version 1.0
 * @see https://gitlab.com/yaso2com/rsf-slider
 * @see https://www.youtube.com/playlist?list=PLZ9SRRNo8TbbJa8cjqxdKuYjwshlH_IGI
 */
package slider.lib;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * This class ???
 */
public class MThumbSliderAdditionalUI
{
    private static Rectangle unionRect = new Rectangle();
    TrackListener trackListener;
    /**
     * MThumbSlider mSlider:
     * BasicSliderUI ui:
     * Rectangle[] thumbRects:
     * int thumbNum: the thumb number starts from 0
     * Rectangle trackRect:
     * TrackListener trackListener: handles the mouse events
     * Rectangle unionRect:
     */
    private MThumbSlider mSlider;
    private BasicSliderUI ui;
    private Rectangle[] thumbRects;
    private int numberOfThumbs;
    private Rectangle trackRect;

    public MThumbSliderAdditionalUI(BasicSliderUI ui)
    {
        this.ui = ui;
    }

    /**
     * @param c
     */
    public void installUI(JComponent c)
    {

        mSlider = (MThumbSlider) c;
        numberOfThumbs = mSlider.getNumberOfThumbs();
        thumbRects = new Rectangle[numberOfThumbs];
        for (int i = 0; i < numberOfThumbs; i++)
        {
            thumbRects[i] = new Rectangle();
        }
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
        for (int i = 0; i < numberOfThumbs; i++)
        {
            thumbRects[i].setSize(size.width, size.height);
        }
    }

    protected void calculateThumbsLocation()
    {
        for (int i = 0; i < numberOfThumbs; i++)
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
                if (numberOfThumbs % 2 == 0)
                {
                    if (i < numberOfThumbs / 2)
                    {
                        thumbRects[i].y = trackRect.y - (i - numberOfThumbs / 2) * 8;
                    }
                    else
                    {
                        thumbRects[i].y = trackRect.y - (i - numberOfThumbs / 2) * 8 - 8;
                    }
                    //12 - 8 * i;// it was 20-10
                }
                else
                {
                    if (i < numberOfThumbs / 2)
                    {
                        thumbRects[i].y = trackRect.y - ((i - numberOfThumbs / 2) * 8 - 4);
                    }
                    else if (i == numberOfThumbs / 2)
                    {
                        thumbRects[i].y = trackRect.y;
                    }
                    else
                    {
                        thumbRects[i].y = trackRect.y - (i - numberOfThumbs / 2) * 8;
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

    public int getNumberOfThumbs()
    {
        return numberOfThumbs;
    }

    public Rectangle[] getThumbRects()
    {
        return thumbRects;
    }

    /**
     * This is the method for the cascading effect, there are two tyes of sliders, one vertical and another horizontal.
     * The horizontal is used for the examples
     *
     * @param leftThumbBodyBoundingBox      is the leftThumbBodyBoundingBox location of the thumb number indexOfCurrentThumb
     * @param upperThumbBodyBoundingBox      is the upperThumbBodyBoundingBox location of the thumb number indexOfCurrentThumb
     * @param indexOfCurrentThumb  is the indexOfCurrentThumb of the thumb whose location should be changed
     * @param slider is the slider to change
     */

    public void setThumbLocationAt(int leftThumbBodyBoundingBox,
                                   int upperThumbBodyBoundingBox,
                                   int indexOfCurrentThumb,
                                   MThumbSlider slider)
    {
        unionRect.setBounds(thumbRects[indexOfCurrentThumb]);

        thumbRects[indexOfCurrentThumb].setLocation(leftThumbBodyBoundingBox, upperThumbBodyBoundingBox);
        SwingUtilities.computeUnion(thumbRects[indexOfCurrentThumb].x, thumbRects[indexOfCurrentThumb].y, thumbRects[indexOfCurrentThumb].width, thumbRects[indexOfCurrentThumb].height,
                unionRect);
        mSlider.repaint(unionRect.x, unionRect.y, unionRect.width, unionRect.height);

        switch (slider.getOrientation())
        {
            // this is not used in the examples "a vertical slider, but it is possible"
            case JSlider.VERTICAL:
                if (indexOfCurrentThumb < thumbRects.length - 1 && unionRect.y > thumbRects[indexOfCurrentThumb + 1].y)
                {
                    setThumbLocationAt(upperThumbBodyBoundingBox, thumbRects[indexOfCurrentThumb + 1].x, indexOfCurrentThumb + 1, slider);
                }

                if (indexOfCurrentThumb > 0 && unionRect.y < thumbRects[indexOfCurrentThumb - 1].y)
                {
                    setThumbLocationAt(upperThumbBodyBoundingBox, thumbRects[indexOfCurrentThumb - 1].x, indexOfCurrentThumb - 1, slider);
                }
                break;

            case JSlider.HORIZONTAL:
                if (indexOfCurrentThumb < thumbRects.length - 1 && thumbRects[indexOfCurrentThumb].x > thumbRects[indexOfCurrentThumb + 1].x)
                {
                    setThumbLocationAt(leftThumbBodyBoundingBox, thumbRects[indexOfCurrentThumb + 1].y, indexOfCurrentThumb + 1, slider);
                }

                if (indexOfCurrentThumb > 0 && thumbRects[indexOfCurrentThumb].x < thumbRects[indexOfCurrentThumb - 1].x)
                {
                    setThumbLocationAt(leftThumbBodyBoundingBox, thumbRects[indexOfCurrentThumb - 1].y, indexOfCurrentThumb - 1, slider);
                }
                break;
        }
    }

    public Rectangle getTrackRect()
    {
        return ((MThumbSliderAdditional) ui).getTrackRect();
    }

    /**
     * Internal class for handling mouse events
     */
    public class TrackListener
            extends MouseInputAdapter
    {
        /// Transient boolean isDragging: if the thumb of the slider is being dragged
        private transient boolean isDragging;

        /// The difference between the upper left corner of the bounding rectangle and the mouse position
        protected transient int offset;

        /// The bounding box of the thumb being adjusted
        protected Rectangle adjustingThumbBodyBoundingBox = null;

        /// The index of the thumb being adjusted
        protected int adjustingThumbIndex;

        /// The associated slider
        protected MThumbSlider slider;

        protected Rectangle sliderBodyBoundingBox;

        /**
         * Constructor.
         * Here the slider is stored.
         *
         * @param slider slider
         */
        public TrackListener(MThumbSlider slider)
        {
            this.slider = slider;
            initOrReset();
        }

        @Override
        /**
         * Mouse was pressed.
         * @param e mouse event
         *
         * <B> Description </B>
         */
        public void mousePressed(MouseEvent e)
        {
            /** Checks
             * - if slider is enabled, i.e., can interact with the slider thumbs.
             *   + Gets the x position of the mouse when pressed from the mouse event
             *   + Gets the y position of the mouse when pressed from the mouse event
             *   + Put focus on the slider
             *   + For all thumbs
             *    + gets the bounding box of the thumb
             *    + if the current mouse position is inside the bounding box of the thumb
             *     + Compute the offset (i.e., the difference in the x and the y positions) of the mouse with respect to the
             *     upper left corner of the bounding box of the thumb. Depending whether the slider is vertical or horizontal.
             *     + set the dragging flag to true
             *     + store the bounding box of the thumb and the index of the thumb being adjusted
             *     + if the thumb is already found do not go through all thumbs
             */
            if (slider.isEnabled())
            {
                // Gets the x position of the mouse when pressed from the mouse event
                int currentMouseX = e.getX();

                // Gets the y position of the mouse when pressed from the mouse event
                int currentMouseY = e.getY();

                // Put focus on the slider
                slider.requestFocus();

                // For all thumbs,
                for (int i = 0; i < numberOfThumbs; i++)
                {
                    // - gets the bounding box of the thumb,
                    Rectangle thumbBodyBoundingBox = thumbRects[i];

                    //  if the current mouse position is inside the bounding box of the thumb
                    //   compute the offset (i.e., the difference in the x and the y positions) of the mouse with respect to the
                     //  upper left corner of the bounding box of the thumb. Depending whether the slider is vertical or horizontal.
                     //    set the dragging flag to true
                     //    store the bounding box of the thumb and the index of the thumb being adjusted
                     //    if the thumb is already found do not go through all thumbs
                    if (thumbBodyBoundingBox.contains(currentMouseX, currentMouseY))
                    {
                        switch (slider.getOrientation())
                        {
                            case JSlider.VERTICAL:
                                offset = currentMouseY - thumbBodyBoundingBox.y;
                                break;
                            case JSlider.HORIZONTAL:
                                offset = currentMouseX - thumbBodyBoundingBox.x;
                                break;
                        }
                        isDragging = true;
                        slider.setValueIsAdjusting(true);
                        adjustingThumbBodyBoundingBox = thumbBodyBoundingBox;
                        adjustingThumbIndex = i;
                        return;
                    }
                }
            }
        }

        @Override
        /**
         * Mouse was dragged.
         *
         * @param e mouse event
         *
         *  <B> Description </B>
         */
        public void mouseDragged(MouseEvent e)
        {
            /** Checks
             * - if slider is enabled, i.e., can interact with the slider thumbs.
             * - if a thumb is dragged.
             * - if the bounding box of the thumb is set
             *   + Gets the bounding box of the dragged thumb
             *   + Gets the bounding box of the body (i.e., called Track) of the slider
             *   + Gets the orientation of the slider: either vertical or horizontal
             *   + Calculates the new position of the currently dragged thumb
             *    + Condition: The tip should not be positioned out of the slider body
             *      + if the tip position is smaller than the left of the slider body, position it to be on the left slider body position
             *      + if the tip position is larger than the right of the slider body, position it to be on the right slider body position
             *
             * @verbatim

             *  Example: HORIZONTAL Slider orientation:
             *
             *        upper left
             *             *-----*        |
             *              \   /         |--> Thumb
             *                !           |
             *            thumb tip
             *              --------------------
             *              |    slider body   |
             *              --------------------
             *             left                right
             * @endverbatim
             */
            if (slider.isEnabled()
                    && isDragging
                    && slider.getValueIsAdjusting()
                    && adjustingThumbBodyBoundingBox != null)
            {
                Rectangle thumbBodyBoundingBox = thumbRects[adjustingThumbIndex];
                sliderBodyBoundingBox = getTrackRect();

                // branch on orientation
                switch (slider.getOrientation())
                {
                    case JSlider.VERTICAL:
                        // compute and set new position

                        int topOfThumbBodyBoundingBox = computeTopOfThumbBodyBoundingBox(thumbBodyBoundingBox, e.getY() - offset);
                        setThumbLocationAt(thumbBodyBoundingBox.x, topOfThumbBodyBoundingBox, adjustingThumbIndex, slider);
                        break;

                    case JSlider.HORIZONTAL:
                        // compute and set new position
                        int leftOfThumbBodyBoundingBox = computeLeftOfThumbBodyBoundingBox(thumbBodyBoundingBox, e.getX() - offset);
                        setThumbLocationAt(leftOfThumbBodyBoundingBox, thumbBodyBoundingBox.y, adjustingThumbIndex, slider);
                        break;
                }
            }
        }

        private int computeLeftOfThumbBodyBoundingBox(Rectangle thumbBodyBoundingBox, int initLeftOfThumbBodyBoundingBox)
        {
            int halfThumbWidth = thumbBodyBoundingBox.width / 2;// from the middle of the bounding box of the thumb, where the tip is

            int leftOfThumbBodyBoundingBox = initLeftOfThumbBodyBoundingBox; //
            int tipOfThumbBody = leftOfThumbBodyBoundingBox + halfThumbWidth;
            int leftOfSliderBodyBoundingBox = sliderBodyBoundingBox.x;
            int rightOfSliderBodyBoundingBox = sliderBodyBoundingBox.x + (sliderBodyBoundingBox.width - 1);

            // Conditions: The tip should not be positioned out of the slider body
            //  if the tip position is smaller than the left of the slider body, position it to be on the left slider body position
            //  if the tip position is larger than the right of the slider body, position it to be on the right slider body position
            tipOfThumbBody = Math.max(tipOfThumbBody, leftOfSliderBodyBoundingBox);
            tipOfThumbBody = Math.min(tipOfThumbBody, rightOfSliderBodyBoundingBox);

            leftOfThumbBodyBoundingBox = tipOfThumbBody - halfThumbWidth;
            return leftOfThumbBodyBoundingBox;
        }

        private int computeTopOfThumbBodyBoundingBox(Rectangle thumbBodyBoundingBox, int initTopOfThumbBodyBoundingBox)
        {
            int halfThumbHeight = thumbBodyBoundingBox.height / 2; // from the middle of the bounding box of the thumb
            int topOfThumbBodyBoundingBox = initTopOfThumbBodyBoundingBox; //e.getY() - offset;
            int tipOfThumbBody = topOfThumbBodyBoundingBox + halfThumbHeight;
            int topOfSliderBodyBoundingBox = sliderBodyBoundingBox.y;
            int bottomOfSliderBodyBoundingBox = sliderBodyBoundingBox.y + (sliderBodyBoundingBox.height - 1);

            tipOfThumbBody = Math.max(tipOfThumbBody, topOfSliderBodyBoundingBox);
            tipOfThumbBody = Math.min(tipOfThumbBody, bottomOfSliderBodyBoundingBox);

            topOfThumbBodyBoundingBox = tipOfThumbBody - halfThumbHeight;
            return topOfThumbBodyBoundingBox;
        }

        @Override
        /**
         * Mouse was released.
         *
         * @param e mouse event
         *
         * <B> Description </B>
         *
         */
        public void mouseReleased(MouseEvent e)
        {
            /**
             * Checks
             *  - if slider is enabled, i.e., can interact with the slider thumbs.
             *  - Set the value of all thumbs
             *      - for all thumbs before the released (dragged) thumb, set the new thumbs values (e.g., \f$10^-13\f$ in MCSs analysis or any label being shown), excluding the current released thumb
             *      - for all thumbs after the released (dragged) thumb, set the new thumbs values, including the current released thumb
             *  - Calculate the thumbs sizes and their positions
             *  - Reset offset, dragging, value adjusting to the initial values. to have a clean start for the next mouse action.
             *  - Repaint the slider
             */
            if (slider.isEnabled())
            {
                //  for all thumbs before the released (dragged) thumb, set the new thumbs values. The value of the current released thumb is not set
                for (int thumbIndex = 0; thumbIndex < adjustingThumbIndex; ++thumbIndex)
                {
                    setThumbValue(thumbIndex);
                }

                // for all thumbs after the released (dragged) thumb, set the new thumbs values, including the current released thumb
                // from largest value to the slider that was manipulated (included)
                for (int thumbIndex = numberOfThumbs - 1; thumbIndex >= adjustingThumbIndex; --thumbIndex)
                {
                    setThumbValue(thumbIndex);
                }

                // Calculate the thumbs sizes and their positions
                slider.calculateGeometry();
                // Reset offset, dragging, value adjusting to the initial values. to have a clean start for the next mouse action.
                initOrReset();

                // repaint slider
                slider.repaint();
            }
        }

        /**
         * Reset offset, dragging, value_adjusting
         */
        private void initOrReset()
        {
            offset = 0;
            isDragging = false;
            slider.setValueIsAdjusting(false);
        }

        /**
         * Set current thumb value.
         *
         * @param thumbIndex thumb index
         *
         *  <B> Description </B>
         *
         *  Gets the thumb body bounding box and the track slider body bounding box
         *  Gets the orientation of the slider Vertical of horizontal
         *  Calculates the new position of the current thumb
         *
         */
        private void setThumbValue(int thumbIndex)
        {
            // The tip of the current thumb
            int thumbTip;

            // Gets the thumb body bounding box and the track slider body bounding box
            Rectangle thumbBodyBoundingBox = thumbRects[thumbIndex];
            sliderBodyBoundingBox = getTrackRect();

            // Gets the orientation of the slider Vertical of horizontal
            switch (slider.getOrientation())
            {
                case JSlider.VERTICAL:
                    // Calculates the new position of the current thumb
                    int halfThumbHeight = thumbBodyBoundingBox.height / 2;
                    int topOfThumbBodyBoundingBox = computeTopOfThumbBodyBoundingBox(thumbBodyBoundingBox, thumbBodyBoundingBox.y);
                    thumbTip = topOfThumbBodyBoundingBox + halfThumbHeight;

                    //
                    if (ui.valueForYPosition(thumbTip) != slider.getValueAt(numberOfThumbs))
                    {
                        slider.setValueAt(ui.valueForYPosition(thumbTip), thumbIndex, false);
                    }
                    break;

                case JSlider.HORIZONTAL:
                    // Calculates the new position of the current thumb
                    int halfThumbWidth = thumbBodyBoundingBox.width / 2;
                    int thumbLeft = thumbBodyBoundingBox.x;
                    int trackLeft = sliderBodyBoundingBox.x;
                    int trackRight = sliderBodyBoundingBox.x + (sliderBodyBoundingBox.width - 1);

                    thumbLeft = Math.max(thumbLeft, trackLeft - halfThumbWidth);
                    thumbLeft = Math.min(thumbLeft, trackRight - halfThumbWidth);

                    thumbTip = thumbLeft + halfThumbWidth;
                    if (ui.valueForXPosition(thumbTip) != mSlider.getValueAt(thumbIndex))
                    {
                        mSlider.setValueAt(ui.valueForXPosition(thumbTip),
                                thumbIndex, false);
                    }
                    break;
            }
        }
    }
}
