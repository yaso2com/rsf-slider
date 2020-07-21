/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package slider.lib.edit;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import slider.lib.SliderPanel;
import slider.lib.SliderParameters;

public class EditSliderDialog
        extends JDialog
{
    // Label and Spinner for showing and selection the number of thumbs
    protected JLabel lNumberOfThumbs;
    protected JSpinner spNumberOfThumbs;
    // Button for confirming change of number of thumbs
    protected JButton bNumberOfThumbs;
    // Checkbox for switching between decimal and log scale
    protected JCheckBox cbLogScale;
    // Objects for actions: sliderPanel and sliderParameters
    protected SliderParameters sliderParameters;
    protected SliderPanel sliderPanel;

    // Currently selected colors
    // Can be changed by pushing one of the color buttons
    private ColorPanel pColors;

    /**
     * Constructor
     *
     * @param sliderParameters
     * @param sliderPanel
     */
    public EditSliderDialog(
            SliderParameters sliderParameters,
            SliderPanel sliderPanel
    )
    {
        super();

        this.sliderParameters = sliderParameters;
        this.sliderPanel = sliderPanel;

        initGUI();
        updateColors();
    }
    
    /**
     * Confirm Button Action
     *
     *
     */
    protected void confirmActionPerformed()
    {
        // Get new number of thumbs from Spinner
        SpinnerNumberModel model = (SpinnerNumberModel) spNumberOfThumbs.getModel();
        int numberOfThumbs = model.getNumber().intValue();
        
        // update slider parameters colors and thumbs in sliderParameters
        sliderParameters.additionalColors(numberOfThumbs);
        sliderParameters.setNumberOfThumbs(numberOfThumbs);

        // update GUI
        updateColors();

        sliderParameters.getSlider().updateThumbs(numberOfThumbs);
        sliderParameters.getSlider().updateRangeCounts();
        sliderParameters.getSlider().sendMessage();
        sliderParameters.getSlider().updateUI();

        sliderParameters.getSlider().invalidate();
        sliderParameters.getSlider().repaint();
    }

    /**
     * Create Content
     */
    protected void initGUI()
    {
        setTitle("Slider Editor");

        setSize(400, 400);

        //
        lNumberOfThumbs = new JLabel("Number of Thumbs");
        spNumberOfThumbs = new JSpinner(new SpinnerNumberModel(sliderParameters.getNumberOfThumbs(), 2, Integer.MAX_VALUE, 1));
        bNumberOfThumbs = new JButton("Confirm");
        bNumberOfThumbs.addActionListener((ActionEvent e) ->
        {
            confirmActionPerformed();
        });

        cbLogScale = new JCheckBox("Logaritmic", sliderParameters.isLogScale());
        cbLogScale.addActionListener((ActionEvent e) ->
        {
            sliderParameters.setLogScale(cbLogScale.isSelected());
        });

        GridBagConstraints gbc = new GridBagConstraints();
        this.setLayout(new GridBagLayout());

        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridy = 0;
        gbc.gridx = 1;
        this.add(lNumberOfThumbs, gbc);
        gbc.gridx = 2;
        this.add(spNumberOfThumbs, gbc);
        gbc.gridx = 3;
        this.add(bNumberOfThumbs, gbc);

        if (sliderParameters.hasColor())
        {
            pColors = new ColorPanel(sliderParameters, sliderPanel);

            pColors.setLayout(new GridBagLayout());
            gbc.gridy = 3;
            gbc.gridx = 1;
            gbc.gridwidth = 3;
            gbc.fill = GridBagConstraints.BOTH;
            this.add(pColors, gbc);
            
            pColors.invalidate();
            validate();
            pColors.repaint();   
        }

        gbc.gridy = 4;
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        this.add(cbLogScale, gbc);
    }
    
    private void updateColors() {
        if (pColors != null) {
            pColors.updateColors();
            pColors.invalidate();
            validate();
            pColors.repaint();   
        }
    }
}
