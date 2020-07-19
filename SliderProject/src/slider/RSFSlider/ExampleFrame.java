/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slider.RSFSlider;

import slider.lib.SliderPanel;
import slider.lib.SliderParameters;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class ExampleFrame
        extends JFrame
{

    SliderPanel upperSlider;
    SliderPanel lowerSlider;

    public ExampleFrame()
    {
        initGUI();
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);
                System.exit(0);
            }
        });
    }

    private void initGUI()
    {
        Dimension dimension = new Dimension(400, 400);
        setSize(dimension);
        setMinimumSize(dimension);
        setTitle("Slider Example");

        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        createUpperSlider();
        gbc.gridx = 1;
        gbc.gridy = 1;
        this.add(upperSlider, gbc);


        gbc = new GridBagConstraints();
        createLowerSlider();
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(lowerSlider, gbc);

    }

    private void createUpperSlider()
    {
        SliderParameters sliderParameters = new SliderParameters();
        sliderParameters.setMaximum(22);
        sliderParameters.setMinimum(1);
        sliderParameters.setRange(1, 22);
        sliderParameters.setLabel("Example with 4 thumbs");
        sliderParameters.setShowDialog(true);
        sliderParameters.setNumberOfThumbs(4);
        sliderParameters.setSliderColors(2);

        upperSlider = new SliderPanel(sliderParameters);
        List<Integer> box = new ArrayList<>();
        box.add(1);
        box.add(5);
        box.add(9);
        List<Integer> numberInRange = new ArrayList<>();

        // the histogram values
        numberInRange.add(5); // this is fixed we need to change it to the number of the spirals
        numberInRange.add(10);
        numberInRange.add(7);

        upperSlider.setValue(13, 3);
        upperSlider.setValue(9, 2);
        upperSlider.setValue(5, 1);
        upperSlider.setNumberInRange(box, numberInRange);
    }

    private void createLowerSlider()
    {
        SliderParameters sliderParameters = new SliderParameters();
        sliderParameters.setMaximum(20);
        sliderParameters.setMinimum(1);
        sliderParameters.setRange(1, 20);
        sliderParameters.setLabel("Example with 5 thumbs");
        sliderParameters.setShowDialog(true);
        sliderParameters.setNumberOfThumbs(5);
        sliderParameters.setSliderColors(2);

        lowerSlider = new SliderPanel(sliderParameters);
        List<Integer> box = new ArrayList<>();

        // the position of the histogram values
        box.add(1);
        box.add(5);
        box.add(9);
        box.add(13);
        box.add(17);
        List<Integer> numberInRange = new ArrayList<>();

        // the histogram values
        numberInRange.add(5); // this is fixed we need to change it to the number of the spirals
        numberInRange.add(10);
        numberInRange.add(7);
        numberInRange.add(18);
        numberInRange.add(2);

        lowerSlider.setValue(19, 4);// the tick position, the thumb number
        lowerSlider.setValue(13, 3);
        lowerSlider.setValue(9, 2);
        lowerSlider.setValue(5, 1);
        lowerSlider.setNumberInRange(box, numberInRange);
    }
}
