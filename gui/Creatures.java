/**
 * @author Marcos Lemus
 * CS351
 * Creature Creation Project
 */
package creature.geeksquad.gui;

import creature.geeksquad.genetics.Population;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
            @Override
            public void run() {
                Log.initialize(Log.NUMB_CORES);
                List<Tribe> tribeList = new ArrayList<>();
                List<String> nameList = new ArrayList<>();
                List<Future<Population>> future = new ArrayList<>();
                
                ExecutorService executor = Executors.newFixedThreadPool(Log.NUMB_CORES);
                
                CallMe call = new CallMe();
                for(int i = 0; i < Log.NUMB_CORES; i++){
                    future.add(executor.submit(call));
                }
                
                Tribe tribe;
                for(Future<Population> f: future){
                    try {
                        String name = Names.getTribeName();
                        while(nameList.contains(name)) name = Names.getTribeName();
                        nameList.add(name);
                        tribe = new Tribe(name, f.get());
                        tribe.start();
                        tribeList.add(tribe);
                    } catch (InterruptedException | ExecutionException ex) {
                        Log.error(ex.toString());
                    }
                }
                
                executor.shutdown();
                
                gui = new GUI(tribeList, nameList);
                gui.setVisible(true);
            }
        });
    }
}
