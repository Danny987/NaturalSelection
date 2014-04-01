package creature.geeksquad.tribe;

import creature.geeksquad.genetics.Hopper;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Marcos
 */
public class Tribe extends Thread {

    public String name;
    private static final int POPULATION_SIZE = 1000;
    private List<Hopper> hoppers = new ArrayList<>();
    private boolean paused = false;
    private boolean running = true;

    public Tribe(String name) {
        this.name = name;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            hoppers.add(generateHopper());
        }
    }

    /**
     *
     */
    public void nextGeneration() {

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
     * @return List<Hopper>
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

        return new Hopper(null);
    }

    /**
     * Kills the thread.
     */
    public void kill() {
        running = false;
    }
}
