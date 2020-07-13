/* (swing1.1.1) */
package slider.lib;

import java.awt.*;


/**
 * @version 1.0 09/08/99
 */
//
// MThumbSliderAdditionalUI <--> BasicMThumbSliderUI
//                          <--> MetalMThumbSliderUI
//                          <--> MotifMThumbSliderUI
//
public interface MThumbSliderAdditional
{

    Rectangle getTrackRect();

    Dimension getThumbSize();

    int xPositionForValue(int value);

    int yPositionForValue(int value);

}

