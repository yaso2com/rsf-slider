/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package slider.lib;


import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class EditSliderDialog
        extends JDialog
{

    //
    protected JButton bConfirm;
    //
    protected JLabel numberOfThumbs;
    protected JSpinner newNumberOfThumbs;
    //
    protected JCheckBox cbLogScale;
    //
    protected SliderParameters sliderParameters;
    protected SliderPanel sliderPanel;

    // Colors
    private JPanel pColors;// = new JPanel();
    private List<JButton> colorButtons = new ArrayList();
    private List<Color> colors;

    /**
     * Constructor
     *
     * @param sliderparam
     * @param sliderPanel
     */
    public EditSliderDialog(
            SliderParameters sliderparam,
            SliderPanel sliderPanel
    )
    {
        super();
        setTitle("Slider Editor");

        this.sliderParameters = sliderparam;
        this.sliderPanel = sliderPanel;

        this.setSize(400, 400);

        initGUI();
        updateGui();
    }

    /**
     * Confirm Button Action
     *
     *
     */
    protected void confirmActionPerformed()
    {

        SpinnerNumberModel model = (SpinnerNumberModel) newNumberOfThumbs.getModel();
        int numberOfThumbs = model.getNumber().intValue();
        sliderParameters.additionalColors(numberOfThumbs);
        sliderParameters.setNumberOfThumbs(numberOfThumbs);

        updateGui();

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
        //
        this.bConfirm = new JButton("Confirm");
        this.bConfirm.addActionListener((ActionEvent e) ->
        {
            confirmActionPerformed();
        });

        this.numberOfThumbs = new JLabel("Number of Thumbs");
        this.newNumberOfThumbs = new JSpinner(new SpinnerNumberModel(sliderParameters.getNumberOfThumbs(), 2, Integer.MAX_VALUE, 1));

        this.cbLogScale = new JCheckBox("Logaritmic", sliderParameters.isLogScale());
        cbLogScale.addActionListener((ActionEvent e) ->
        {
            sliderParameters.setLogScale(cbLogScale.isSelected());
        });

        GridBagConstraints gbc = new GridBagConstraints();
        this.setLayout(new GridBagLayout());

        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridy = 0;
        gbc.gridx = 1;
        this.add(numberOfThumbs, gbc);
        gbc.gridx = 2;
        this.add(newNumberOfThumbs, gbc);

        if (sliderParameters.hasColor())
        {
            pColors = new JPanel();

            pColors.setLayout(new GridBagLayout());
            gbc.gridy = 3;
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.BOTH;
            this.add(pColors, gbc);
        }

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridy = 10;
        gbc.gridx = 1;
        this.add(cbLogScale, gbc);

        gbc.gridy = 11;
        gbc.gridx = 1;
        gbc.gridwidth = 2; //makes it in the middle
        this.add(bConfirm, gbc);
    }

    /**
     * Color choosing action
     *
     * @param ev is an action event that stores the action command when a button is pressed. is a string.
     */
    private void colorButtonActionPerformed(ActionEvent ev)
    {
        String ac = ev.getActionCommand();
        int i = Integer.parseInt(ac);
        Color color = colors.get(i);
        color = JColorChooser.showDialog(null, "Choose Color", color);
        colorButtons.get(i).setBackground(color);
        sliderParameters.setSliderColorAt(i, color);
        colors = sliderParameters.getSliderColors();
        sliderPanel.updateMThumbSliderColors(colors);
        sliderParameters.getSlider().updateRangeCounts();
        sliderParameters.sendMessage();
    }

    /**
     * Update content
     */
    protected void updateGui()
    {
        this.colors = sliderParameters.getSliderColors();

        if (pColors != null)
        {
            pColors.removeAll();
        }

        GridBagConstraints gbc = new GridBagConstraints();

        for (int i = 0; i < sliderParameters.getNumberOfThumbs() - 1; i++)
        {
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridy = i;
            gbc.gridx = 1;
            pColors.add(new JLabel("Field " + Integer.toString(i + 1)), gbc);

            gbc.gridx = 2;

            colorButtons.add(i, new JButton());
            colorButtons.get(i).setBackground(colors.get(i));
            colorButtons.get(i).setActionCommand((new Integer(i)).toString());
            colorButtons.get(i).addActionListener((ActionEvent e) ->
            {
                colorButtonActionPerformed(e);
            });
            pColors.add(colorButtons.get(i), gbc);
        }

        pColors.invalidate();
        validate();
        pColors.repaint();
    }
}
