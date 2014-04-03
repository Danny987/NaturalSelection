package creature.geeksquad.gui;

import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.hillclimbing.TribeBrain;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Marcos
 */
public class Tribe extends Thread {

    public static final int POPULATION_SIZE = 1000;
    private String name;
    private List<Hopper> hoppers = new ArrayList<>();
    private boolean paused = false;
    private boolean running = true;

    private TribeBrain brain;

    public Tribe(String name) {
        brain = new TribeBrain();

        this.name = name;

        String hopperName;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            hoppers.add(generateHopper());
        }
    }

    /**
     * Call things to run hill climbing and cross over.
     */
    public void nextGeneration() {
        hoppers.add(0, brain.performHillClimbing(hoppers.get(0)));
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

    /**
     * Returns the Tribes population.
     *
     * @return List of Hopper
     */
    public synchronized List<Hopper> getList() {
        return hoppers;
    }

    /**
     * generates a functional hopper.
     *
     * @return new Hopper
     */
    private Hopper generateHopper() {
        Hopper newBorn = null;

        try {
            newBorn = new Hopper();
        } catch (GeneticsException ex) {
            System.out.println(ex);
        }

        return newBorn;
    }

    /**
     * Kills the thread.
     */
    public void kill() {
        running = false;
    }
}
