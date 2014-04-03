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

/**
 * A class for a Population of creatures in a PriorityQueue.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
@SuppressWarnings("serial")
public class Population extends ArrayList<Hopper> {
	// Sub-collections allow the Population to separate the Hoppers that need
	// special handling from those in the general population.
	private final ArrayList<Hopper> breeders;
	private final Queue<Hopper> climbers;
	// The Crossover module this Population will use.
	private Crossover crossover;
	
	/**
	 * The default constructor creates an empty Population.
	 */
	public Population() {
		super();
		breeders = new ArrayList<Hopper>();
		climbers = new LinkedList<Hopper>();
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
				i--;
			}
		}
	}
	
//	/**
//	 * A fitness Comparator for Hoppers.
//	 */
//	public static class HopperFitnessComparator implements Comparator<Hopper> {
//		/**
//		 * Override of Comparator's compare method. Compares the fitness of two
//		 * Hoppers.
//		 * 
//		 * @param hopperA First Hopper whose fitness should be compared.
//		 * @param hopperB Second Hopper whose fitness should be compared.
//		 * @return Negative int, 0, or positive int if hopperA's fitness is less
//		 *             than, equal to, or greater than hopperB's, respectively.
//		 */
//		@Override
//		public int compare(Hopper hopperA, Hopper hopperB) {
//			if (hopperA.getFitness() > hopperB.getFitness()) {
//				return -1;
//			} else if (hopperA.getFitness() > hopperB.getFitness()) {
//				return 1;
//			} else {
//				return 0;
//			}
//		}
//	}
//	
//	/**
//	 * An optional age Comparator for Hoppers.
//	 */
//	public static class HopperAgeComparator implements Comparator<Hopper> {
//		/**
//		 * Override of Comparator's compare method. Compares the ages of two
//		 * Hoppers.
//		 * 
//		 * @param hopperA First Hopper whose age should be compared.
//		 * @param hopperB Second Hopper whose age should be compared.
//		 * @return Negative int, 0, or positive int if hopperA's age is less
//		 *             than, equal to, or greater than hopperB's, respectively.
//		 */
//		@Override
//		public int compare(Hopper hopperA, Hopper hopperB) {
//			if (hopperA.getAge() > hopperB.getAge()) {
//				return -1;
//			} else if (hopperA.getAge() > hopperB.getAge()) {
//				return 1;
//			} else {
//				return 0;
//			}
//		}
//	}
	
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
		// TODO
		// Do we need to keep track of age?
	}
	
	/**
	 * Breed, perform selection and crossover, within the population.
	 */
	public void breed() {
		// TODO
	}
	
	/**
	 * Perform hill-climbing on the Population.
	 */
	public void hillClimb() {
		// TODO
	}
	
	/**
	 * Move a percentage of the highest-fitness Hoppers into the breeders
	 * list.
	 */
	public void moveBreeders(float f) {
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
	public void cull(int n) {
		sort();
		if (n < size()) {
			removeRange(0, n);
		} else {
			clear();
		}
	}
	
	/**
	 * Kill off a percentage of the lowest-fitness individuals in the general
	 * Population.
	 */
	public void cullPercent(float f) {
		if (f > 1.0f) {
			f = 1.0f;
		}
		cull((int) (f * size()));
	}
	
	/**
	 * Gets the average fitness of this Population. Takes n time since it has
	 * to iterate over the whole array.
	 * 
	 * Note: this method makes no guarantees about the accuracy of its result.
	 * At any given time, the majority of the Hoppers in the Population will
	 * have very rough estimates for their individual fitness.
	 * 
	 * @return Average fitness of Population as a float.
	 */
	public float getAverageFitness() {
		float sum = 0.0f;
		for (Hopper h : this) {
			sum += h.getFitness();
		}
		
		return sum / size();
	}
	
	/**
	 * Sort this Population according to its natural ordering: ascending Hopper
	 * fitness.
	 */
	public void sort() {
		Collections.sort(this);
	}
	
	/**
	 * Sort this Population according to the reverse of its natural ordering:
	 * descending Hopper fitness.
	 */
	public void reverse() {
		Collections.sort(this, Collections.reverseOrder());
	}
	
	/**
	 * Override of toString: returns an exportable representation of this
	 * population's current state.
	 * 
	 * @return String representation of this population's current state.
	 */
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder("<population>"
												 + Helper.NEWLINE);
		output.append("<hoppers>" + Helper.NEWLINE);
		for (Hopper h : this) {
			output.append(h.toString() + Helper.NEWLINE);
		}
		output.append("</hoppers> + Helper.NEWLINE");
		output.append("<breeders> + Helper.NEWLINE");
		for (Hopper h : breeders) {
			output.append(h.toString() + Helper.NEWLINE);
		}
		output.append("</breeders>" + Helper.NEWLINE);
		output.append("<climbers>" + Helper.NEWLINE);
		for (Hopper h : climbers) {
			output.append(h.toString() + Helper.NEWLINE);
		}
		output.append("</climbers>" + Helper.NEWLINE);
		output.append("</population>");
		
		return output.toString();
	}
	
	/**
	 * Main method for testing purposes.
	 * 
	 * @param args Command-line arguments.
	 */
	public static void main(String[] args) {
		Population pop = new Population(10);
	}
	
}
