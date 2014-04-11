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
    //main!
    public static void main(String args[]) {
        try {
            Names.loadFiles();
            Log.initialize();

            final List<Tribe> tribeList = new ArrayList<>();
            final List<String> nameList = new ArrayList<>();
            List<CallMe> populations = new ArrayList<>();
            
            for(int i = 0; i < Log.NUMB_CORES; i++){
                CallMe c = new CallMe();
                c.start();
                
                populations.add(c);
            }
            
            
            Tribe tribe;
            int i = 0;
            for (CallMe p : populations) {
                String name = Names.getTribeName();
                while (nameList.contains(name)) {
                    name = Names.getTribeName();
                }
                nameList.add(i++ + ": " + name);
                tribe = new Tribe(name, p.getPopulation());
                tribe.start();
                tribeList.add(tribe);
            }

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    GUI gui;

                    gui = new GUI(tribeList, nameList);
                    gui.setVisible(true);
                }
            });
        } catch (Exception ex) {
            Log.error(ex.toString());
            Log.popup(null, "Something went terribly wrong.");
            System.exit(0);
        }

    }
}
