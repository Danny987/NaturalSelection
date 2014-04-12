package creature.geeksquad.gui;

import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.genetics.Population;
import creature.geeksquad.library.Helper;

/**
 *
 * @author Marcos
 */
public class Tribe extends Thread {

    public static final int POPULATION_SIZE = 501;
    private final Population population;

    private volatile boolean paused = true;
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
//        population.hillClimb();
    }

    @Override
    public void run() {

        while (running) {
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
        synchronized(population){
            return population.get(index);
        }
    }

    public Hopper randomHopper(){
        synchronized(population){
            return population.get(Helper.RANDOM.nextInt(population.size()-1));
        }
    }
    
    public Population getPopulation() {
            return population;
    }

    public void addHopper(Hopper h) {
        population.add(h);
    }

    public int getSize() {
        return population.size();
    }

    public Hopper getOverachiever() {
        return population.getOverachiever();
    }

    /**
     * Kills the thread.
     */
    public void kill() {
        running = false;
    }

    public float getFitness() {
        return population.getAverageFitness();
    }
    
    public long getFails(){
        return population.getLifetimeDeadChildren();
    }
    
    public long gethillFails(){
        return population.getLifetimeFailedHillClimbs();
    }
    
    public long gethills(){
        return population.getLifetimeHillClimbs();
    }
    
    public long getcross(){
        return population.getLifetimeOffspring();
    }
}
