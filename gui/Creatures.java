/**
* @author Marcos Lemus
* CS351
* Creature Creation Project
*/
package creature.geeksquad.gui;

import creature.geeksquad.gui.GUI;

/**
 * main
 */
public class Creatures {
    
    //Tribes names used for the different threads.
    private GUI gui;
    
    /**
     * Initializes the gui and starts the thread
     */
    public Creatures(){
        Names.loadFiles();
        gui = new GUI();
        Thread guiThread = new Thread(gui);
        
        guiThread.start();
    }
    
    //main!
    public static void main(String args[]){
        Creatures creature = new Creatures();
    }
}
