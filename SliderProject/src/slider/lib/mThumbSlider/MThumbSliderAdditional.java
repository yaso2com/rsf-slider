package slider.lib.mThumbSlider;

import java.awt.*;

/**
 * This interface should be implemented by the following look and feel classes: BasicMThumbSliderUI, MetalMThumbSliderUI, and MotifMThumbSliderUI.
 * Nevertheless, currently only the MetalMThumbSliderUI is implemented.
 */
public interface MThumbSliderAdditional
{
    /**
     * Gets the bounding box of the slider
     * @return the bounding box of the slider
     */
    Rectangle getTrackRect();

    /**
     * Sets the bounding boxs of all thumbs
     */
    Dimension getThumbSize();

    /// Gets the x position of thumb tip for that value on the slider body
    int xPositionForValue(int value);

    /// Gets the y position of thumb tip for that value on the slider body
    int yPositionForValue(int value);
}

