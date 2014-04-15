
package creature.geeksquad.gui;

import creature.geeksquad.genetics.Population;
/**
 *
 * @author Marcos
 */
public class CallMe extends Thread {

    private Population population;

    @Override
    public void run() {
        synchronized (this) {
            population = new Population(Tribe.POPULATION_SIZE);
        }
    }
    
    public Population getPopulation(){
        synchronized(this){
            return population;
        }
    }

}
