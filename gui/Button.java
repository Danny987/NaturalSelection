package creature.geeksquad.gui;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JButton;

/**
 *
 * @author Marcos
 */
public class Button extends JButton
{

    /**
     *
     * @param width
     * @param height
     * @param name
     */
    public Button(int width, int height, String name)
    {
        super(name);
        
        Dimension size = new Dimension(width, height);
        
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setActionCommand(name);
        setBackground(new Color(35, 35, 35));
        setForeground(new Color(205, 205, 205));
    }
}
