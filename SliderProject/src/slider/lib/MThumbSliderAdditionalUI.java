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
 * @see The published paper for this work can be found at: https://www.cgv-conf.org/wp-content/uploads/2020/07/02_202011L010_F074.pdf
 */
package slider.lib;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * This class provides the user interface behavior that is independent of the look and feel.
 */
public class MThumbSliderAdditionalUI
{
    /**
     * MThumbSlider mSlider                      is the multithumb slider from Java Swing
     * BasicSliderUI ui                          is the user interface
     * Rectangle[] thumbsBodyBoundingBoxes       are the bounding boxes of all thumbs
     * int thumbNum                              is the thumb number starts from 0
     * ThumbMovementHandler thumbMovementHandler is the handler of the mouse events
     */
    ThumbMovementHandler thumbMovementHandler;
    private MThumbSlider mSlider;
    private BasicSliderUI ui;
    private Rectangle[] thumbsBodyBoundingBoxes;
    private int numberOfThumbs;

    /**
     * Constructor: stores the user interface
     *
     * @param ui is the user interface
     */
    public MThumbSliderAdditionalUI(BasicSliderUI ui)
    {
        this.ui = ui;
    }

    /**
     * Sets the number of thumbs, thumbs body bounding boxes, thumb movement handler
     *
     * @param c is the parent component
     */
    public void installUI(JComponent c)
    {
        mSlider = (MThumbSlider) c;
        numberOfThumbs = mSlider.getNumberOfThumbs();
        thumbsBodyBoundingBoxes = new Rectangle[numberOfThumbs];
        for (int i = 0; i < numberOfThumbs; i++)
        {
            thumbsBodyBoundingBoxes[i] = new Rectangle();
        }
        thumbMovementHandler = new MThumbSliderAdditionalUI.ThumbMovementHandler(mSlider);
    }

    /**
     * Unsets the thumbs body bounding boxes and the thumb movement handler (invalidating them).
     *
     * @param c  is the parent component
     */
    public void uninstallUI(JComponent c)
    {
        thumbsBodyBoundingBoxes = null;
        thumbMovementHandler = null;
    }

    /**
     * Sets the bounding boxs of all thumbs
     */
    protected void setThumbsBodyBoundingBoxes()
    {
        Dimension size = ((MThumbSliderAdditional) ui).getThumbSize();
        for (int currentThumb = 0; currentThumb < numberOfThumbs; currentThumb++)
        {
            thumbsBodyBoundingBoxes[currentThumb].setSize(size.width, size.height);
        }
    }

    /**
     * Assures that the thumbtips are on the ticks of the slider and not some where in between.
     *
     * <B> Description </B>
     *
     * Gets the orientation of the slider, either vertical or horizontal
     *  - Vertical case is not implemented yet
     *
     */
    protected void calculateThumbsLocation()
    {
        for (int currentThumb = 0; currentThumb < numberOfThumbs; currentThumb++)
        {
            /// Assures that the thumbtips are on the ticks of the slider and not some where in between.
            if (mSlider.getSnapToTicks())
            {
                int tickSpacing = mSlider.getMinorTickSpacing();
                if (tickSpacing == 0)
                {
                    tickSpacing = mSlider.getMajorTickSpacing();
                }
                if (tickSpacing != 0)
                {
                    int sliderValue = mSlider.getValueAt(currentThumb);
                    int min = mSlider.getMinimum();
                    if ((sliderValue - min) % tickSpacing != 0)
                    {
                        float temp = (float) (sliderValue - min) / (float) tickSpacing;
                        int whichTick = Math.round(temp);
                        int snappedValue = min + (whichTick * tickSpacing);
                        mSlider.setValueAt(snappedValue, currentThumb, true);
                    }
                }
            }

            Rectangle sliderBodyBoundingBox = getSliderBodyBoundingBox();

            /// Gets the orientation of the slider, either vertical or horizontal (Vertical case **Not implemented yet**)
            switch (mSlider.getOrientation())
            {
                case JSlider.VERTICAL:
                {
                    //Vertical case **Not implemented**
                }
                break;
                case JSlider.HORIZONTAL:
                {
                    /// Gets the current thumb value
                    int currentThumbValue = mSlider.getValueAt(currentThumb);

                    /// Gets the x position of thumb tip for that value on the slider body
                    int xPositionOfThumbTip = ((MThumbSliderAdditional) ui).xPositionForValue(currentThumbValue);

                    /// Sets the position of the left side of the current thumb to the position of the tip - half the width of the thumb
                    thumbsBodyBoundingBoxes[currentThumb].x = xPositionOfThumbTip - (thumbsBodyBoundingBoxes[currentThumb].width / 2);

                    /// Distinguishs if we have an odd number of thumbs or an even number of thumbs
                    if (numberOfThumbs % 2 == 0)
                    {
                        /** - case of even: the left half of the thumbs position will be below the slider and the right half of the thumbs position will be over the slider
                         *   +  Example: 8-> 16 -> 8 (for four thumbs)
                         */
                        if (currentThumb < numberOfThumbs / 2)
                        {
                            thumbsBodyBoundingBoxes[currentThumb].y = sliderBodyBoundingBox.y - (currentThumb - numberOfThumbs / 2) * 8;
                        }
                        else
                        {
                            thumbsBodyBoundingBoxes[currentThumb].y = sliderBodyBoundingBox.y - (currentThumb - numberOfThumbs / 2) * 8 - 8;
                        }
                    }
                    else
                    { /** - case of odd: the left half of the thumbs position will be below the slider, the middle thumb is in the middle of the slider body,
                     and the right half of the thumbs position will be over the slider
                     *      + Example: 8-> 8-> 8 -> 8 (for five thumbs)
                     */
                        if (currentThumb < numberOfThumbs / 2) // Example: 8-> 8-> 8 -> 8 (for five thumbs)
                        {
                            //the left half of the thumbs position will be below the slider
                            thumbsBodyBoundingBoxes[currentThumb].y = sliderBodyBoundingBox.y - ((currentThumb - numberOfThumbs / 2) * 8);
                        }
                        else if (currentThumb == numberOfThumbs / 2)
                        {
                            // the middle thumb is in the middle of the slider body
                            thumbsBodyBoundingBoxes[currentThumb].y = sliderBodyBoundingBox.y;
                        }
                        else
                        {
                            // the right half of the thumbs position will be over the slider
                            thumbsBodyBoundingBoxes[currentThumb].y = sliderBodyBoundingBox.y - (currentThumb - numberOfThumbs / 2) * 8;
                        }
                    }
                    break;
                }
            }
        }
    }//end calculateThumbsLocation

    /**
     * Gets the number of thumbs
     * @return the number of thumbs
     */
    public int getNumberOfThumbs()
    {
        return numberOfThumbs;
    }

    /**
     * Gets the bounding boxes of all thumbs
     *
     * @return the bounding box of all thumbs
     */
    public Rectangle[] getThumbsBodyBoundingBoxes()
    {
        return thumbsBodyBoundingBoxes;
    }

    /**
     * This is the method for the cascading effect, there are two types of sliders, one vertical and another horizontal.
     *
     * @param leftThumbBodyBoundingBox  is the leftThumbBodyBoundingBox location of the thumb number indexOfCurrentThumb
     * @param upperThumbBodyBoundingBox is the upperThumbBodyBoundingBox location of the thumb number indexOfCurrentThumb
     * @param indexOfCurrentThumb       is the indexOfCurrentThumb of the thumb whose location should be changed
     * @param slider                    is the slider to change
     *
     *  <B>Description</B>
     *
     */

    public void setThumbLocationAt(int leftThumbBodyBoundingBox,
                                   int upperThumbBodyBoundingBox,
                                   int indexOfCurrentThumb,
                                   MThumbSlider slider)
    {
        /// Calculates the union of the old and the new bounding boxes of the thumbs to repaint the old one with the background and the new one with the thumb
        Rectangle unionOfTwoThumbBodyBoundingBoxes = new Rectangle();
        unionOfTwoThumbBodyBoundingBoxes.setBounds(thumbsBodyBoundingBoxes[indexOfCurrentThumb]);
        thumbsBodyBoundingBoxes[indexOfCurrentThumb].setLocation(leftThumbBodyBoundingBox, upperThumbBodyBoundingBox);
        SwingUtilities.computeUnion(thumbsBodyBoundingBoxes[indexOfCurrentThumb].x,
                                    thumbsBodyBoundingBoxes[indexOfCurrentThumb].y,
                                    thumbsBodyBoundingBoxes[indexOfCurrentThumb].width,
                                    thumbsBodyBoundingBoxes[indexOfCurrentThumb].height,
                                    unionOfTwoThumbBodyBoundingBoxes);

        mSlider.repaint(unionOfTwoThumbBodyBoundingBoxes);

        switch (slider.getOrientation())
        {
            // this is not used in the examples "a vertical slider, but it is possible"
            case JSlider.VERTICAL:
                if (indexOfCurrentThumb < thumbsBodyBoundingBoxes.length - 1 && thumbsBodyBoundingBoxes[indexOfCurrentThumb].y > thumbsBodyBoundingBoxes[indexOfCurrentThumb + 1].y)
                {
                    setThumbLocationAt(upperThumbBodyBoundingBox, thumbsBodyBoundingBoxes[indexOfCurrentThumb + 1].x, indexOfCurrentThumb + 1, slider);
                }

                if (indexOfCurrentThumb > 0 && thumbsBodyBoundingBoxes[indexOfCurrentThumb].y < thumbsBodyBoundingBoxes[indexOfCurrentThumb - 1].y)
                {
                    setThumbLocationAt(upperThumbBodyBoundingBox, thumbsBodyBoundingBoxes[indexOfCurrentThumb - 1].x, indexOfCurrentThumb - 1, slider);
                }
                break;

            case JSlider.HORIZONTAL:
                if (indexOfCurrentThumb < thumbsBodyBoundingBoxes.length - 1 && thumbsBodyBoundingBoxes[indexOfCurrentThumb].x > thumbsBodyBoundingBoxes[indexOfCurrentThumb + 1].x)
                {
                    setThumbLocationAt(leftThumbBodyBoundingBox, thumbsBodyBoundingBoxes[indexOfCurrentThumb + 1].y, indexOfCurrentThumb + 1, slider);
                }

                if (indexOfCurrentThumb > 0 && thumbsBodyBoundingBoxes[indexOfCurrentThumb].x < thumbsBodyBoundingBoxes[indexOfCurrentThumb - 1].x)
                {
                    setThumbLocationAt(leftThumbBodyBoundingBox, thumbsBodyBoundingBoxes[indexOfCurrentThumb - 1].y, indexOfCurrentThumb - 1, slider);
                }
                break;
        }
    }

    /**
     * Gets the bounding box of the slider
     * @return the bounding box of the slider
     */
    public Rectangle getSliderBodyBoundingBox()
    {
        return ((MThumbSliderAdditional) ui).getTrackRect();
    }

    /**
     * Internal class for handling mouse events
     */
    public class ThumbMovementHandler
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
        public ThumbMovementHandler(MThumbSlider slider)
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
                    Rectangle thumbBodyBoundingBox = thumbsBodyBoundingBoxes[i];

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
                Rectangle thumbBodyBoundingBox = thumbsBodyBoundingBoxes[adjustingThumbIndex];
                sliderBodyBoundingBox = getSliderBodyBoundingBox();

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

        /**
         * Computes and sets new position of the current thumb for the horizontally oriented slider
         *
         * @param thumbBodyBoundingBox:           The thumb body bounding box
         * @param initLeftOfThumbBodyBoundingBox: The initial left of the thumbs bounding box
         */

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

        /**
         * Computes and sets new position of the current thumb for the vertically oriented slider
         *
         * @param thumbBodyBoundingBox:          The thumb body bounding box
         * @param initTopOfThumbBodyBoundingBox: The initial top of the thumbs bounding box
         */
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
         *                   <B> Description </B>
         *                   <p>
         *                   Gets the thumb body bounding box and the track slider body bounding box
         *                   Gets the orientation of the slider Vertical of horizontal
         *                   Calculates the new position of the current thumb
         *                   Sets the value of the current thumb to the value of its tip (if they are not the same)
         */
        private void setThumbValue(int thumbIndex)
        {
            // The tip of the current thumb
            int thumbTip;

            // Gets the thumb body bounding box and the track slider body bounding box
            Rectangle thumbBodyBoundingBox = thumbsBodyBoundingBoxes[thumbIndex];
            sliderBodyBoundingBox = getSliderBodyBoundingBox();

            // Gets the orientation of the slider Vertical of horizontal
            switch (slider.getOrientation())
            {
                case JSlider.VERTICAL:
                    // Calculates the new position of the current thumb
                    int halfThumbHeight = thumbBodyBoundingBox.height / 2;
                    int topOfThumbBodyBoundingBox = computeTopOfThumbBodyBoundingBox(thumbBodyBoundingBox, thumbBodyBoundingBox.y);
                    thumbTip = topOfThumbBodyBoundingBox + halfThumbHeight;

                    // Sets the value of the current thumb to the value of its tip (if they are not the same)
                    if (ui.valueForYPosition(thumbTip) != slider.getValueAt(thumbIndex))
                    {
                        slider.setValueAt(ui.valueForYPosition(thumbTip), thumbIndex, false);
                    }
                    break;

                case JSlider.HORIZONTAL:
                    // Calculates the new position of the current thumb
                    int halfThumbWidth = thumbBodyBoundingBox.width / 2;
                    int leftOfThumbBodyBoundingBox = computeLeftOfThumbBodyBoundingBox(thumbBodyBoundingBox, thumbBodyBoundingBox.x);
                    thumbTip = leftOfThumbBodyBoundingBox + halfThumbWidth;

                    // Sets the value of the current thumb to the value of its tip (if they are not the same)
                    if (ui.valueForXPosition(thumbTip) != slider.getValueAt(thumbIndex))
                    {
                        slider.setValueAt(ui.valueForXPosition(thumbTip), thumbIndex, false);
                    }
                    break;
            }
        }
    }
}
