package creature.geeksquad.gui;

import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.genetics.Population;
import creature.geeksquad.library.Helper;
import java.util.ArrayList;
import java.util.List;

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
    
    private List<Hopper> list = new ArrayList<>();
    private Hopper overachiever;
    private Hopper randomHopper;
    private long totalHillClimbs = 0;
    private long totalCrossover = 0;
    private long lifeTimeFailes = 0;
    private long hillClimbFails = 0;
    private float fitness = 0;
    private long generations = 0;
    private float min = 0;
    private float max = 0;
    private int size = 0;
    
    public Tribe(String name, Population population) {
        this.setName(name);
        this.population = population;                                               
        
        overachiever = population.getOverachiever();
        totalHillClimbs = population.getLifetimeHillClimbs();
        hillClimbFails = population.getLifetimeFailedHillClimbs();
        lifeTimeFailes = population.getLifetimeDeadChildren();
        totalCrossover = population.getLifetimeOffspring();
        fitness = population.getAverageFitness();
        generations = population.getGenerations();
        min = population.getUnderachiever().getFitness();
        max = population.getHighestFitness();
        size = population.size();
        list.clear();
        list.addAll(population);
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
        generations = population.getGenerations();
        min = population.getUnderachiever().getFitness();
        max = population.getHighestFitness();
        size = population.size();
        list.clear();
        list.addAll(population);
        
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

    /**
     * 
     * @return  
     */
    public long getGenerations() {
        return generations;
    }

    /**
     * Returns the Tribes population.
     *
     * @param index
     *
     * @return hopper at index
     */
    public Hopper getHopper(int index) {
        return list.get(index);
    }

    /**
     * 
     * @return random hopper
     */
    public Hopper randomHopper() {
        random = false;
        return randomHopper;
    }

    /**
     * 
     * @return the population 
     */
    public Population getPopulation() {
        return population;
    }

    /**
     * Add hopper to population
     * @param h 
     */
    public void addHopper(Hopper h) {
        population.add(h);
    }

    /**
     * 
     * @return population size
     */
    public int getSize() {
        return size;
    }

    /**
     * 
     * @return best hopper in population
     */
    public Hopper getOverachiever() {
        return overachiever;
    }

    /**
     * Kills the thread.
     */
    public void kill() {
        running = false;
    }

    /**
     * 
     * @return tribe fitness
     */
    public float getFitness() {
        return fitness;
    }

    /**
     * 
     * @return crossover fails
     */
    public long getFails() {
        return lifeTimeFailes;
    }

    /**
     * 
     * @return hill climb fails
     */
    public long gethillFails() {
        return hillClimbFails;
    }

    /**
     * 
     * @return total hill climbs
     */
    public long gethills() {
        return totalHillClimbs;
    }

    /**
     * 
     * @return total number of cross over generations 
     */
    public long getcross() {
        return totalCrossover;
    }
    
    /**
     * the worse fitness in the population.
     * @return 
     */
    public float min(){
        return min;
    }
    
    /**
     * best fitness in population
     * @return 
     */
    public float max(){
        return max;
    }
}
