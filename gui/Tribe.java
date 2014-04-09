package creature.geeksquad.gui;

import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.genetics.Population;

/**
 *
 * @author Marcos
 */
public class Tribe extends Thread{

    public static final int POPULATION_SIZE = 500;
    private final Population population;

    private boolean paused = true;
    private boolean running = true;

    public Tribe(String name, Population population) {
        this.setName(name);
        this.population = population;
    }

    /**
     * Call things to run hill climbing and cross over.
     */
    public void nextGeneration() {
        population.update();
    }

    @Override
    public void run() {
        
        while (running) {
            synchronized (this) {

                // if the thread is interupted pause or unpause
                if (Thread.interrupted()) {
                    paused = !paused;
                }

                // if not paused let them mutate
                if (!paused) {
                    nextGeneration();
                }
            }
        }
    }

    public int getGenerations() {
        return population.getGenerations();
    }

    /**
     * Returns the Tribes population.
     *
     * @param index
     *
     * @return hopper at index
     */
    public Hopper getHopper(int index) {
        return population.get(index);
    }

    public Population getPopulation(){
        synchronized(this){
            return population;
        }
    }
    
    public void addHopper(Hopper h) {
        population.add(h);
    }

    public int getSize() {
        return population.size();
    }

    public Hopper getOverachiever(){
        return population.getOverachiever();
    }
    /**
     * Kills the thread.
     */
    public void kill() {
        running = false;
    }
}
