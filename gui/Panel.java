/**
* @author Marcos Lemus
* CS351
* Creature Creation Project
*/
package creature.geeksquad.gui;

import java.awt.Dimension;
import javax.swing.JPanel;

/**
 * Used to create a JPanel and set all the sizes in one line
 */
public class Panel extends JPanel{

    /**
     *
     * @param width
     * @param height
     */
    public Panel(Dimension size){
        super();
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
    }
}
