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
     * @param name
     * @param min
     * @param max
     * @param init
     */
    public Slider(String name, int min, int max, int init){
        super(JSlider.VERTICAL, min, max, init);
        
        Dimension size = new Dimension(140, 235);
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        
        setPaintTicks(true);
        setMajorTickSpacing(100);
        setMinorTickSpacing(0);
        
        
        setBackground(new Color(55, 55, 55));
        setForeground(new Color(205, 205, 205));
    };
    
}
