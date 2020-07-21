/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slider.lib.edit;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import slider.lib.SliderPanel;
import slider.lib.SliderParameters;

/**
 *
 * @author zeckzer
 */
public class ColorPanel
    extends JPanel
{
    // GUI elements
    private List<JButton> colorButtons = new ArrayList();
    private List<Color> colors;

    // Objects for actions: sliderPanel and sliderParameters
    protected SliderParameters sliderParameters;
    protected SliderPanel sliderPanel;

    public ColorPanel(
        SliderParameters sliderParameters,
        SliderPanel sliderPanel
    )    
    {
        this.sliderParameters = sliderParameters;
        this.sliderPanel = sliderPanel;
        
        updateColors();
    }

    /**
     * Update available colors in color panel
     */
    void updateColors()
    {
        this.colors = sliderParameters.getSliderColors();

        removeAll();

        GridBagConstraints gbc = new GridBagConstraints();

        for (int i = 0;
             i < sliderParameters.getNumberOfThumbs() - 1;
             i++)
        {
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridy = i;
            
            // add color label
            gbc.gridx = 1;
            add(new JLabel("Range " + Integer.toString(i + 1)), gbc);

            // add color button
            gbc.gridx = 2;
            colorButtons.add(i, new JButton());
            colorButtons.get(i).setBackground(colors.get(i));
            colorButtons.get(i).setActionCommand((new Integer(i)).toString());
            colorButtons.get(i).addActionListener((ActionEvent e) ->
            {
                colorButtonActionPerformed(e);
            });
            add(colorButtons.get(i), gbc);
        }
    }

    /**
     * Color choosing action
     *
     * @param ev is an action event that stores the action command when a button is pressed. is a string.
     */
    private void colorButtonActionPerformed(ActionEvent ev)
    {
        // get number of color to change
        String ac = ev.getActionCommand();
        int colorNumber = Integer.parseInt(ac);

        // Get color
        Color color = colors.get(colorNumber);

        // Open color choosing dialog and retrieve new color
        color = JColorChooser.showDialog(null, "Choose Color", color);

        // update color of button in panel
        colorButtons.get(colorNumber).setBackground(color);
        colors.set(colorNumber, color);

        // update slider colors
        sliderPanel.updateColors(colors);
    }
}
