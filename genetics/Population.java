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

import creature.geeksquad.genetics.Hopper.Attractor;

/**
 * A class for a Population of creatures.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
public class Population {
	private final PriorityQueue<Hopper> hoppers;
	private Attractor attractor;
	private HashMap<Attractor, Float> attractorTable =
				new HashMap<Attractor, Float>();
	private Deque<Hopper> breeders;
	private Deque<Hopper> hillClimbers;
	
	/**
	 * The default constructor creates an empty Population with its Hoppers
	 * sorted by fitness.
	 */
	public Population() {
		hoppers = new PriorityQueue<Hopper>(0, new HopperFitnessComparator());
		for (Attractor a : Attractor.values()) {
			attractorTable.put(a, 0.0f);
		}
	}
	
	/**
	 * A fitness Comparator for Hoppers.
	 */
	public static class HopperFitnessComparator implements Comparator<Hopper> {
		/**
		 * Override of Comparator's compare method. Compares the fitness of two
		 * Hoppers.
		 * 
		 * @param hopperA First Hopper whose fitness should be compared.
		 * @param hopperB Second Hopper whose fitness should be compared.
		 * @return Negative int, 0, or positive int if hopperA's fitness is less
		 *             than, equal to, or greater than hopperB's, respectively.
		 */
		@Override
		public int compare(Hopper hopperA, Hopper hopperB) {
			if (hopperA.getFitness() > hopperB.getFitness()) {
				return -1;
			} else if (hopperA.getFitness() > hopperB.getFitness()) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	
	/**
	 * An age Comparator for Hoppers.
	 */
	public static class HopperAgeComparator implements Comparator<Hopper> {
		/**
		 * Override of Comparator's compare method. Compares the ages of two
		 * Hoppers.
		 * 
		 * @param hopperA First Hopper whose age should be compared.
		 * @param hopperB Second Hopper whose age should be compared.
		 * @return Negative int, 0, or positive int if hopperA's age is less
		 *             than, equal to, or greater than hopperB's, respectively.
		 */
		@Override
		public int compare(Hopper hopperA, Hopper hopperB) {
			if (hopperA.getAge() > hopperB.getAge()) {
				return -1;
			} else if (hopperA.getAge() > hopperB.getAge()) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	
	/**
	 * Instantiates a new Population containing the provided Collection of
	 * Hoppers.
	 * 
	 * @param creatures A PriorityQueue of Hoppers.
	 */
	public Population(PriorityQueue<Hopper> hoppers) {
		this.hoppers = hoppers;
	}
	
	/**
	 * Update the population.
	 */
	public void update() {
		// TODO
	}
	
	/**
	 * Add a Hopper to the Population.
	 * 
	 * @return True if successful; false otherwise.
	 */
	public void add(Hopper newbie) {
		hoppers.add(newbie);
	}
	
	/**
	 * Remove a Hopper from the Population by reference.
	 */
	public void kill(Hopper victim) {
		hoppers.remove(victim);
	}
	
	/**
	 * Kill off the least-desirable n individuals in the Population.
	 * 
	 * @param n Number of individuals to kill off.
	 */
	public void cull(int n) {
		// TODO
	}
	
	/**
	 * Kill off the provided list of individuals from the Population.
	 * 
	 * @param victims List<Hoppers> list of Hoppers to kill off.
	 */
	public void cull(List<Hopper> victims) {
		for (Hopper h : victims) {
			hoppers.remove(h);
		}
	}
	
	/**
	 * Getter for attractor.
	 * 
	 * @return This Hopper's Attractor.
	 */
	public Attractor getAttractor() {
		return attractor;
	}
	
	/**
	 * Sets the Attractor for this Population and goes through the hoppers list
	 * and updates it for each Hopper individually.
	 * 
	 * @param attractor Attractor to set for this population.
	 */
	public void setAttractor(Attractor attractor) {
		this.attractor = attractor;
		for (Hopper h : hoppers) {
			h.setAttractor(attractor);
		}
	}
	
	/**
	 * Getter for a particular value in the Attractor table.
	 * 
	 * @param attractor Attractor value to access in the Attractor table.
	 * @return The requested Attractor's weight from the table as a float.
	 */
	public float getAttractorWeight(Attractor attractor) {
		return attractorTable.get(attractor);
	}
	
	/**
	 * Setter for a particular value in the Attractor table.
	 * 
	 * @param attractor Attractor value to set in the Attractor table.
	 * @param weight Float to use as Attractor's weight in the table.
	 */
	public void setAttractorWeight(Attractor attractor, float weight) {
		attractorTable.put(attractor, weight);
	}
	
	/**
	 * Getter for the size of the population.
	 * 
	 * @return Population size as a long.
	 */
	public long size() {
		return hoppers.size();
	}
	
	/**
	 * Perform selection and crossover within the population.
	 */
	public void selection() {
		// TODO
	}
	
	/**
	 * A static method that performs interpopulation crossover selection for
	 * two Populations. Returns nothing since it modifies the Populations
	 * directly.
	 * 
	 * @param pop1 First Population to interbreed.
	 * @param pop2 Second Population to interbreed.
	 */
	public static void interbreed(Population pop1,
			Population pop2) {
		// TODO
	}
	
	/**
	 * Override of toString: returns an exportable representation of this
	 * population's current state.
	 * 
	 * @return String representation of this population's current state.
	 */
	@Override
	public String toString() {
		// TODO
		return "";
	}
	
}
