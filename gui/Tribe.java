package creature.geeksquad.gui;

import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.genetics.Population;

/**
 *
 * @author Marcos
 */
public class Tribe extends Thread {

    public static final int POPULATION_SIZE = 1000;
    private Population population;

    private boolean paused = true;
    private boolean running = false;

    public Tribe(String name) {
        this.setName(name);
    }

    /**
     * Call things to run hill climbing and cross over.
     */
    public void nextGeneration() {
        population.update();
    }

    @Override
    public void run() {
        
        synchronized (this) {
            population = new Population(POPULATION_SIZE);
            running = true;
        }

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

    public boolean isRunning(){
            return running;
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
