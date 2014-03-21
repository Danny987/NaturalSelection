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
	private final String name;
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
		// TODO
		name = "";
	}
	
	/**
	 * Getter for name.
	 * 
	 * @return Hopper's name as a String.
	 */
	public String getName() {
		return name;
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
	 * @param parentA First parent Hopper.
	 * @param parentB Second parent Hopper.
	 * @return An array of twin child Hoppers resulting from the crossover of
	 *         the parents' genotypes.
	 */
	public static Hopper[] breed(Hopper parentA, Hopper parentB) {
		Genotype[] children = Genotype.crossover(parentA.getGenotype(),
												 parentB.getGenotype());
		Hopper[] hoppers = {new Hopper(children[0]), new Hopper(children[1])};

		return hoppers;
	}
	
	/**
	 * Override of toString.
	 * 
	 * @return String representation of this Hopper.
	 */
	@Override
	public String toString() {		
		return "<creature>" +
			   "    <name>" + name + "</name>" +
			   "    <genotype>" + genotype.toString() + "</genotype>" +
			   "</creature>";
	}
}
