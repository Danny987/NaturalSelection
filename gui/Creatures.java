/**
 * @author Marcos Lemus
 * CS351
 * Creature Creation Project
 */
package creature.geeksquad.gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * main
 */
public class Creatures {

    //Tribes names used for the different threads.
    private static GUI gui;

    //main!
    public static void main(String args[]) {
        Names.loadFiles();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int numberofcores = Runtime.getRuntime().availableProcessors();
                List<Tribe> tribeList = new ArrayList<>();
                List<String> nameList = new ArrayList<>();
                for (int i = 0; i < numberofcores; i++) {
                    String name = i + ": " + Names.getTribeName();
                    tribeList.add(new Tribe(name));
                    nameList.add(name);
                }

                for (Tribe t : tribeList) {
                    t.start();
                }
                
                gui = new GUI(tribeList, nameList);
            }
        });
    }
}
