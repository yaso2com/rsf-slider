/**
 */
package slider.lib;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 *
 */
public class SkinMThumbSliderUI
        extends BasicSliderUI
        implements MThumbSliderAdditional {

  protected final int TICK_BUFFER = 4;
  protected boolean filledSlider = false;

  MThumbSliderAdditionalUI additionalUi;
  MouseInputAdapter mThumbTrackListener;
  final MThumbSlider parent;

  public static ComponentUI createUI(JComponent c) {
    return new SkinMThumbSliderUI((MThumbSlider) c);
  }

  public SkinMThumbSliderUI(MThumbSlider parent) {
    super(parent);
    this.parent = parent;
  }

  @Override
  public void installUI(JComponent c) {
    additionalUi = new MThumbSliderAdditionalUI(this);
    additionalUi.installUI(c);
    mThumbTrackListener = createMThumbTrackListener((JSlider) c);
    super.installUI(c);
  }

  @Override
  public void uninstallUI(JComponent c) {
    super.uninstallUI(c);
    additionalUi.uninstallUI(c);
    additionalUi = null;
    mThumbTrackListener = null;
  }

  protected MouseInputAdapter createMThumbTrackListener(JSlider slider) {
    return additionalUi.trackListener;
  }

  @Override
  protected TrackListener createTrackListener(JSlider slider) {
    return null;
  }

  @Override
  protected ChangeListener createChangeListener(JSlider slider) {
    return additionalUi.changeHandler;
  }

  @Override
  protected void installListeners(JSlider slider) {
    slider.addMouseListener(mThumbTrackListener);
    slider.addMouseMotionListener(mThumbTrackListener);
    slider.addFocusListener(focusListener);
    slider.addComponentListener(componentListener);
    slider.addPropertyChangeListener(propertyChangeListener);
    slider.getModel().addChangeListener(changeListener);
  }

  @Override
  protected void uninstallListeners(JSlider slider) {
    slider.removeMouseListener(mThumbTrackListener);
    slider.removeMouseMotionListener(mThumbTrackListener);
    slider.removeFocusListener(focusListener);
    slider.removeComponentListener(componentListener);
    slider.removePropertyChangeListener(propertyChangeListener);
    slider.getModel().removeChangeListener(changeListener);
  }

  @Override
  public void calculateGeometry() {
    super.calculateGeometry();
    additionalUi.calculateThumbsSize();
    additionalUi.calculateThumbsLocation();
  }

  @Override
  protected void calculateThumbLocation() {
  }

  Icon thumbRenderer;

  @Override
  public void paint(Graphics g,
                    JComponent c) {
    Rectangle clip = g.getClipBounds();
    if (slider == null || clip == null) {
      return;
    }

    Rectangle[] thumbRects = additionalUi.getThumbRects();
    thumbRect = thumbRects[0];
    int thumbNum = additionalUi.getThumbNum();

    // Paint colored lines on the slider
    if (slider.getPaintTrack() && clip.intersects(trackRect)) {
      boolean filledSlider_tmp = filledSlider;
      filledSlider = false;
      paintTrack(g);
      filledSlider = filledSlider_tmp;

      if (filledSlider) {
        g.translate(trackRect.x, trackRect.y);

        Point t1 = new Point(0, 0);
        Point t2 = new Point(0, 0);
        Rectangle maxThumbRect = new Rectangle(thumbRect);
        thumbRect = maxThumbRect;

        if (slider.getOrientation() == JSlider.HORIZONTAL) {
          t2.y = (trackRect.height - 1) - getThumbOverhang();
          t1.y = t2.y - (getTrackWidth() - 1);
          //t2.x = trackRect.width - 1;
          t2.x = thumbRect.width - 1;
          int maxPosition = xPositionForValue(slider.getMaximum());

          thumbRect.x = maxPosition - (thumbRect.width / 2) - 2;
          thumbRect.y = trackRect.y;
        } else {
          t1.x = (trackRect.width - getThumbOverhang()) - getTrackWidth();
          t2.x = (trackRect.width - getThumbOverhang()) - 1;
          t2.y = trackRect.height - 1;
          int maxPosition = yPositionForValue(slider.getMaximum());

          thumbRect.x = trackRect.x;
          thumbRect.y = maxPosition - (thumbRect.height / 2) - 2;
        }

        if (slider instanceof MThumbSlider) {
          MThumbSlider mThumbSlider = (MThumbSlider) slider;
          SliderParameters sliderParameters = mThumbSlider.getSliderParameters();
          Color fillColor = sliderParameters.getTrackFillColor();
          fillTrack(g, t1, t2, fillColor);

          for (int i = thumbNum - 1; 1 <= i; i--) {
            thumbRect = thumbRects[i];
            fillColor = sliderParameters.getSliderColorAt(i - 1);
            fillTrack(g, t1, t2, fillColor);
          }

          thumbRect = thumbRects[0];
          fillColor = sliderParameters.getTrackFillColor();
          fillTrack(g, t1, t2, fillColor);
        } else {
          fillTrack(g, t1, t2, MetalLookAndFeel.getControlShadow());
        }
        g.translate(-trackRect.x, -trackRect.y);
      }
    }

    // Draw ticks, boxes (histogram)
    if (slider.getPaintTicks() && clip.intersects(tickRect)) {
      paintTicks(g);
      paintHistogram(g);
    }

    // Draw labels
    if (slider.getPaintLabels() && clip.intersects(labelRect)) {
      paintLabels(g);
    }

    // Draw the thumbs
    for (int i = thumbNum - 1; 0 <= i; i--) {
      thumbRect = thumbRects[i];
      if (clip.intersects(thumbRect)) {
        thumbRenderer = ((MThumbSlider) slider).getThumbRendererAt(i);
        if (thumbRenderer != null) {
          paintThumb(g);
        }
      }
    }
  }

  @Override
  public void paintThumb(Graphics g) {
    thumbRenderer.paintIcon(slider, g, thumbRect.x, thumbRect.y);
  }

  public void fillTrack(Graphics g,
                        Point t1,
                        Point t2,
                        Color fillColor) {
    //                               t1-------------------
    //                               |                   |
    //                               --------------------t2
    int middleOfThumb = 0;

    if (slider.getOrientation() == JSlider.HORIZONTAL) {
      middleOfThumb = thumbRect.x + (thumbRect.width / 2) - trackRect.x;
      if (slider.isEnabled()) {
        g.setColor(fillColor);
        g.fillRect(t1.x + 2,
                   t1.y + 2,
                   middleOfThumb - t1.x - 1,
                   t2.y - t1.y - 3);
        g.setColor(fillColor.brighter());
        g.drawLine(t1.x + 1, t1.y + 1, middleOfThumb, t1.y + 1);
        g.drawLine(t1.x + 1, t1.y + 1, t1.x + 1, t2.y - 2);
      } else {
        g.setColor(fillColor);
        g.fillRect(t1.x,
                   t1.y,
                   middleOfThumb - t1.x + 2,
                   t2.y - t1.y);
      }
    } else {
      middleOfThumb = thumbRect.y + (thumbRect.height / 2) - trackRect.y;
      if (slider.isEnabled()) {
        g.setColor(slider.getBackground());
        g.drawLine(t1.x + 1, middleOfThumb, t2.x - 2, middleOfThumb);
        g.drawLine(t1.x + 1, middleOfThumb, t1.x + 1, t2.y - 2);
        g.setColor(fillColor);
        g.fillRect(t1.x + 2,
                   middleOfThumb + 1,
                   t2.x - t1.x - 3,
                   t2.y - 2 - middleOfThumb);
      } else {
        g.setColor(fillColor);
        g.fillRect(t1.x,
                   middleOfThumb + 2,
                   t2.x - 1 - t1.x,
                   t2.y - t1.y);
      }
    }
  }

  /**
   * Histogram
   *
   */
  private void paintHistogram(Graphics g) {
    Rectangle tickBounds = tickRect;
    g.translate(0, tickBounds.y);

    int value = slider.getMinimum();
    int xPos = 0;
    int xPos2 = 0;
    if (slider.getMinorTickSpacing() > 0) {
      while (value < slider.getMaximum()) {
        xPos = xPositionForValue(value);
        xPos2 = xPositionForValue(value + slider.getMinorTickSpacing());

        //Get height of the box
        int height = this.parent.getBoxHeight(value);
        // function to get mcsno
        try {
          Integer numberInRange = this.parent.getNumberInRange(value);
          if (numberInRange != null) {
            paintHistogramBar(g, TICK_BUFFER - 30, xPos, xPos2, -height,//-1 to begin from the lower end and not from the upper end
                              numberInRange.intValue());
          }
        } catch (Throwable thr) {
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
          int numberInRange) {
    if (height != 0) {
      g.setColor(Color.black);
      g.fillRect(xFrom, y + height, (xTo - xFrom), -height);
      String numberInRangeString = Integer.toString(numberInRange);
      int strWidth = g.getFontMetrics().stringWidth(numberInRangeString);
      int xOffset = (xTo - xFrom - strWidth) / 2;
      int strXPos = xFrom;
      int strYPos = height - 28;// the hight is -ve so i am going over the box by 4
      if (xOffset > 0) {
        strXPos += xOffset;
      }
      g.drawString(numberInRangeString, strXPos, strYPos);
    }
  }

  /**
   *
   * @param direction
   */
  @Override
  public void scrollByBlock(int direction) {
  }

  /**
   *
   * @param direction
   */
  @Override
  public void scrollByUnit(int direction) {
  }

  //
  //  MThumbSliderAdditional
  //
  /**
   *
   * @return
   */
  @Override
  public Rectangle getTrackRect() {
    return trackRect;
  }

  /**
   *
   * @return
   */
  @Override
  public Dimension getThumbSize() {
    return super.getThumbSize();
  }

  /**
   *
   * @param value
   * @return
   */
  @Override
  public int xPositionForValue(int value) {
    return super.xPositionForValue(value);
  }

  /**
   *
   * @param value
   * @return
   */
  @Override
  public int yPositionForValue(int value) {
    return super.yPositionForValue(value);
  }

  public int getOrientation() {
    return slider.getOrientation();
  }

    /**
     * Returns the shorter dimension of the track.
     */
    protected int getTrackWidth() {
        // This strange calculation is here to keep the
        // track in proportion to the thumb.
        final double kIdealTrackWidth = 7.0;
        final double kIdealThumbHeight = 16.0;
        final double kWidthScalar = kIdealTrackWidth / kIdealThumbHeight;

        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            return (int)(kWidthScalar * thumbRect.height);
        }
        else {
            return (int)(kWidthScalar * thumbRect.width);
        }
    }

    /**
     * Returns the longer dimension of the slide bar.  (The slide bar is only the
     * part that runs directly under the thumb)
     */
    protected int getTrackLength() {
        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            return trackRect.width;
        }
        return trackRect.height;
    }

    /**
     * Returns the amount that the thumb goes past the slide bar.
     */
    protected int getThumbOverhang() {
        return (int)(getThumbSize().getHeight()-getTrackWidth())/2;
    }
}
