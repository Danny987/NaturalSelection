package creature.geeksquad.gui;

import creature.geeksquad.genetics.GeneticsException;
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

    private boolean random = false;
    private boolean paused = true;
    private boolean running = true;
    
    private Hopper overachiever;
    private Hopper randomHopper;
    private long totalHillClimbs;
    private long totalCrossover;
    private long lifeTimeFailes;
    private long hillClimbFails;
    private float fitness;
    
    public Tribe(String name, Population population) {
        this.setName(name);
        this.population = population;
    }

    /**
     * Call things to run hill climbing and cross over.
     */
    public synchronized void nextGeneration() {
        if(paused){
            try{
                wait();
            } catch(InterruptedException ex){paused = !paused;}
        }
        
        population.update();
        overachiever = population.getOverachiever();
        totalHillClimbs = population.getLifetimeHillClimbs();
        hillClimbFails = population.getLifetimeFailedHillClimbs();
        lifeTimeFailes = population.getLifetimeDeadChildren();
        totalCrossover = population.getLifetimeOffspring();
        fitness = population.getAverageFitness();
        
        if(!random) try {
            randomHopper = new Hopper(population.get(Helper.RANDOM.nextInt(population.size() - 1)));
            random = true;
        } catch (IllegalArgumentException | GeneticsException ex) {
            Log.error("Random Hopper Broke: " + ex);
        }
    }

    @Override
    public void run() {
        while(running){
            if(isInterrupted()) paused = !paused;
            nextGeneration();
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

    public Hopper randomHopper() {
        random = false;
        return randomHopper;
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
        return overachiever;
    }

    /**
     * Kills the thread.
     */
    public void kill() {
        running = false;
    }

    public float getFitness() {
        return fitness;
    }

    public long getFails() {
        return lifeTimeFailes;
    }

    public long gethillFails() {
        return hillClimbFails;
    }

    public long gethills() {
        return totalHillClimbs;
    }

    public long getcross() {
        return totalCrossover;
    }
}
