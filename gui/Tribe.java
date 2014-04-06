package creature.geeksquad.gui;

import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.genetics.Population;

/**
 *
 * @author Marcos
 */
public class Tribe extends Thread {

    public static final int POPULATION_SIZE = 1;
    private Population population;

    private boolean paused = true;
    private boolean running = false;
    private final Object lock = new Object();
    
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
        population = new Population(POPULATION_SIZE);
        running = true;
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
        System.out.println("i died");
    }

    public int getGenerations() {
        if(running) return population.getGenerations();
        else return 0;
    }

    /**
     * Returns the Tribes population.
     *
     * @param index
     *
     * @return hopper at index
     */
    public Hopper getHopper(int index) {
        if(running) return population.get(index);
        else return null;
    }

    public void addHopper(Hopper h) {
        if(running) population.add(h);
    }

    public int getSize() {
        if(running) return population.size();
        else return -1;
    }

    /**
     * Kills the thread.
     */
    public void kill() {
        running = false;
    }
}
