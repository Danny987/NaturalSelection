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

import creature.phenotype.*;

/**
 * A wrapper class for the Genotype and associated Creature (phenotype),
 * containing all the data needed to represent a full creature.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
public class Hopper {
	private final Genotype genotype;
	private final Creature phenotype;
	private float fitness;
	private Attractor attractor;
	
	/**
	 * Instantiates a new Hopper according to the passed Genotype.
	 * 
	 * @param genotype Genotype of the new Hopper.
	 */
	public Hopper(Genotype genotype) {
		this.genotype = genotype;
		this.phenotype = genotype.getPhenotype();
	}
	
	/**
	 * Getter for genotype.
	 * 
	 * @return The Hoppper's Genotype.
	 */
	public Genotype getGenotype() {
		return genotype;
	}
	
	/**
	 * Getter for phenotype.
	 * 
	 * @return The Hopper's Creature (phenotype).
	 */
	public Creature getPhenotype() {
		return phenotype;
	}
	
	/**
	 * Breed two Hoppers.
	 * 
	 * @param parent1 First parent Hopper.
	 * @param parent2 Second parent Hopper.
	 * @return A child Hopper resulting from the crossover of the parents'
	 *         genotypes.
	 */
	public static Hopper breed(Hopper parent1, Hopper parent2) {
		return new Hopper(new Genotype(parent1.getGenotype(),
				                       parent2.getGenotype()));
	}
	
	/**
	 * Override of toString.
	 * 
	 * @return String representation of this Hopper.
	 */
	@Override
	public String toString() {
		// TODO
		return "";
	}
}
