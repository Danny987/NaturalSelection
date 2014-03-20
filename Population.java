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

import creature.phenotype.*;

/**
 * A class for a Population of creatures.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
public class Population {
	private final Collection<Hopper> hoppers;
	
	/**
	 * The default constructor creates an empty Population.
	 */
	public Population() {
		hoppers = new ArrayList<Hopper>();
	}
	
	/**
	 * Instantiates a new Population containing the provided Collection of
	 * Hoppers.
	 * 
	 * @param creatures A Collection of Hoppers.
	 */
	public Population(Collection<Hopper> hoppers) {
		this.hoppers = hoppers;
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
	 * Getter for the size of the population.
	 * 
	 * @return Population size as a long.
	 */
	public long size() {
		return hoppers.size();
	}
	
	/**
	 * A static method that performs crossover selection (interbreeding) for
	 * two Populations. Returns nothing since it modifies the Populations
	 * directly.
	 * 
	 * @param pop1 First Population to interbreed.
	 * @param pop2 Second Population to interbreed.
	 */
	public static void selection(Population pop1, Population pop2) {
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
