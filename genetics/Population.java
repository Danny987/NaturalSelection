/**
 * Evolving Virtual Jumping Creatures
 * CS 351, Project 2
 * 
 * Team members:
 * Ramon A. Lovato
 * Danny Gomez
 * Marcos Lemus
 */
package creature.geeksquad.genetics;

import java.util.*;

import creature.geeksquad.library.Helper;
import creature.geeksquad.hillclimbing.*;

/**
 * A class for a sortable Population of Hoppers in an extended ArrayList.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
@SuppressWarnings("serial")
public class Population extends ArrayList<Hopper> {
	private int generations;
	// Sub-collections allow the Population to separate the Hoppers that need
	// special handling from those in the general population.
	private final ArrayList<Hopper> breeders;
	// The Crossover module this Population will use.
	private Crossover crossover;
	// The hill-climbing Tribe brain for this Population.
	private TribeBrain brain = new TribeBrain();
	
	/**
	 * The default constructor creates an empty Population.
	 */
	public Population() {
		super();
		generations = 0;
		breeders = new ArrayList<Hopper>();
		crossover = new Crossover();
	}
	
	/**
	 * Instantiate a new Population containing randomly generated seed Hoppers.
	 * 
	 * @param num Number of random Hoppers to create.
	 */
	public Population(int num) {
		this();
		for (int i = 0; i < num; i++) {
			try {
				add(new Hopper());
			} catch (IllegalArgumentException | GeneticsException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * A static method that performs interpopulation crossover selection for
	 * two Populations. Returns nothing since it modifies the Populations
	 * directly.
	 * 
	 * @param pop1 First Population to interbreed.
	 * @param pop2 Second Population to interbreed.
	 */
	public static void interbreed(Population pop1, Population pop2) {
		// TODO
	}
	
	/**
	 * Update the population.
	 */
	public void update() {
		synchronized (this) {
			generations++;
			hillClimb();
			// Hill climbing the population will change the creatures, which
			// means their sorting will no longer be valid. However,
			// moveBreeders will sort them again, so it's fine.
			moveBreeders();
			int count = breed();
			// Like above, breeding will change the creatures in the collection,
			// but cull will sort them again first.
			cull(count);
		}
	}
	
	/**
	 * Breed, perform selection and crossover, within the population.
	 * 
	 * @return The number of new Hoppers that were added to the population.
	 *         Needed to tell how many Hoppers should be culled.
	 */
	private int breed() {
		int offspring = 0;
		//
		// TODO
		//
		return offspring;
	}
	
	/**
	 * Perform hill-climbing on all members of the Population.
	 */
	private void hillClimb() {
		Iterator<Hopper> i = iterator();
		while (i.hasNext()) {
			Hopper original = i.next();
			Hopper newHotness = brain.performHillClimbing(original);
			remove(original);
			add(newHotness);
		}
	}
	
	/**
	 * Move the top 20% most fit Hoppers into the breeders list.
	 */
	private void moveBreeders() {
		moveBreeders(Helper.BREED_PERCENTAGE);
	}
	
	/**
	 * Move a percentage of the highest-fitness Hoppers into the breeders
	 * list.
	 * 
	 * @param f Percentage of the highest-fitness Hoppers to move, as a float.
	 */
	private void moveBreeders(float f) {
		sort();
		int size = size();
		int stop = (int) (size - (f * size));
		if (stop >= size) {
			breeders.addAll(this);
			clear();
		} else {
			for (int i = size - 1; i >= stop; i--) {
				breeders.add(remove(i));
			}
		}
	}
	
	/**
	 * Kill off the lowest-fitness n individuals in the general Population.
	 * 
	 * @param n Number of individuals to kill off.
	 */
	private void cull(int n) {
		synchronized (this) {
			sort();
			if (n < size()) {
				removeRange(0, n);
			} else {
				clear();
			}
		}
	}
	
	/**
	 * Kill off a percentage of the lowest-fitness individuals in the general
	 * Population.
	 * 
	 * @param f Percentage of individuals to kill off (as a float).
	 */
	private void cull(float f) {
		if (f > 1.0f) {
			f = 1.0f;
		}
		cull((int) (f * size()));
	}
	
	/**
	 * Getter for the number of generations this population has gone through.
	 * 
	 * @return Number of generations this population has had.
	 */
	public int getGenerations() {
		return generations;
	}
	
	/**
	 * Get the average fitness of this Population. Takes n time since it has
	 * to iterate over the whole array. This method makes no guarantees about
	 * the accuracy of its result. At any given time, the majority of the
	 * Hoppers in the Population will have very rough estimates for their
	 * individual fitness, and not all Hoppers are guaranteed to be in the
	 * general population. The sub-collections are not automatically included.
	 * 
	 * @return Average fitness of Population as a float.
	 */
	public float getAverageFitness() {
		synchronized (this) {
			float sum = 0.0f;
			for (Hopper h : this) {
				sum += h.getFitness();
			}
			
			return sum / size();
		}
	}
	
	/**
	 * Sort this Population according to its natural ordering: ascending Hopper
	 * fitness.
	 */
	private void sort() {
		Collections.sort(this);
	}
	
	/**
	 * Sort this Population according to the reverse of its natural ordering:
	 * descending Hopper fitness.
	 */
	private void reverse() {
		Collections.sort(this, Collections.reverseOrder());
	}
	
	/**
	 * Override of get by index - returns a copy of the requested Hopper.
	 * 
	 * @param index Index of Hopper of which to return a copy.
	 * @return Deep clone of the Hopper at index.
	 */
	@Override
	public Hopper get(int index) {
		synchronized (this) {
			try {
				return new Hopper(super.get(index));
			} catch (IllegalArgumentException | GeneticsException e) {
				return null;
			}
		}
	}
	
	/**
	 * Override of toString: returns an exportable representation of this
	 * population's current state.
	 * 
	 * @return String representation of this population's current state.
	 */
	@Override
	public String toString() {
		synchronized (this) {
			StringBuilder output = new StringBuilder("<population>"
													 + Helper.NEWLINE);
			for (Hopper h : this) {
				output.append(h.toString() + Helper.NEWLINE);
			}
			output.append("</population>");

			return output.toString();
		}	
	}
	
	/**
	 * Main method for testing purposes.
	 * 
	 * @param args Command-line arguments.
	 */
	public static void main(String[] args) {
		Population pop = new Population(10);
		System.out.println(pop);
	}
	
}
