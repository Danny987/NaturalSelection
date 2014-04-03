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
import creature.geeksquad.library.Helper;

/**
 * A class for a Population of creatures in a PriorityQueue.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
public class Population extends ArrayList<Hopper> {
	private Attractor attractor;
	private HashMap<Attractor, Float> attractorTable =
				new HashMap<Attractor, Float>();
	private ArrayList<Hopper> breeders;
	private ArrayList<Hopper> climbers;
	
	/**
	 * The default constructor creates an empty Population.
	 */
	public Population() {
		super();
		for (Attractor a : Attractor.values()) {
			attractorTable.put(a, 0.0f);
		}
	}
	
	/**
	 * Instantiate a new Population containing randomly generated seed Hoppers.
	 * 
	 * @param num Number of random Hoppers to create.
	 */
	public Population(int num) {
		super();
		for (int i = 0; i < num; i++) {
			try {
				add(new Hopper());
			} catch (IllegalArgumentException | GeneticsException ex) {
				ex.printStackTrace();
				i--;
			}
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
	 * An optional age Comparator for Hoppers.
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
	 * Reverse the order of the Population.
	 * 
	 * @return The fitness of the Hopper at index 0.
	 */
	public float reverse() {
		Collections.sort(this, Collections.reverseOrder());
		return get(0).getFitness();
	}
	
	/**
	 * Update the population.
	 */
	public void update() {
		// TODO
	}
	
	/**
	 * Remove a Hopper from the Population by reference and discard it.
	 */
	public void kill(Hopper victim) {
		remove(victim);
	}
	
	/**
	 * Remove a Hopper from the Population by index.
	 */
	public void kill(int index) {
		remove(index);
	}
	
	/**
	 * Kill off the least-desirable n individuals in the Population.
	 * 
	 * @param n Number of individuals to kill off.
	 */
	public void cull(int n) {
		for (int i = 0; i < n; i++) {
			// TODO
			remove(0);
		}
	}
	
	/**
	 * Kill off the provided list of individuals from the Population.
	 * 
	 * @param victims List<Hoppers> list of Hoppers to kill off.
	 */
	public void cull(List<Hopper> victims) {
		for (Hopper h : victims) {
			remove(h);
		}
	}
	
	/**
	 * Perform selection and crossover within the population.
	 */
	public void selection() {
		// TODO
	}
	
	/**
	 * Gets the lowest-fitness Hopper.
	 * 
	 * @return Hopper with the lowest fitness or null if the list is empty.
	 */
//	public Hopper getUnderachiever() {
		// TODO
//		Collections.sort(hoppers, new HopperFitnessComparator());
		
//	}
	
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
		for (Hopper h : this) {
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
		//
		// TODO
		//
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
