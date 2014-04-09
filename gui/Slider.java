//
// @author Marcos Lemus
// CS351
// Creature Creation Project
//
package creature.geeksquad.gui;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JSlider;

/**
 *
 * @author Marcos
 */
public class Slider extends JSlider{

    /**
     *
     * @param name Slider Name
     * @param min Min number
     * @param max max number
     * @param init initial number
     */
    public Slider(String name, int min, int max, int init){
        super(JSlider.VERTICAL, min, max, init);
        
        Dimension size = new Dimension(140, 230);
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        
        setMajorTickSpacing(100);
        setPaintTicks(true);
        setPaintLabels(true);
        setBackground(new Color(55, 55, 55));
        setForeground(new Color(205, 205, 205));
    };
    
    @Override
    public void setMaximum(int size){
        super.setMaximum(size);
        setMajorTickSpacing(size/10);
    }
}
