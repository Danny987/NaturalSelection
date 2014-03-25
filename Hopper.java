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
	private int age;
	private final Genotype genotype;
	private final Creature phenotype;
	private Attractor attractor;
	private float fitness;
	
	/**
	 * Instantiates a new Hopper according to the passed Genotype.
	 * 
	 * @param genotype Genotype of the new Hopper.
	 */
	public Hopper(Genotype genotype) {
		this.genotype = genotype;
		this.phenotype = genotype.getPhenotype();
		if (phenotype == null) {
			System.err.println("Error: phenotype invalid.");
		}
		//
		// TODO
		//
		name = "";
		age = 0;
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
	 * Increment age.
	 * 
	 * @return Hopper's new age as an int.
	 */
	public int grow() {
		return ++age;
	}
	
	/**
	 * Getter for age.
	 * 
	 * @return Hopper's age as an int.
	 */
	public int getAge() {
		return age;
	}
	
	/**
	 * Setter for age.
	 * 
	 * @param age Hopper's new age as an int.
	 */
	public void setAge(int age) {
		this.age = age;
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
	 * Getter for attractor.
	 * 
	 * @return The Hopper's Attractor.
	 */
	public Attractor getAttractor() {
		return attractor;
	}
	
	/**
	 * Setter for attractor.
	 * 
	 * @param attractor New Attractor to give this Hopper.
	 */
	public void setAttractor(Attractor attractor) {
		this.attractor = attractor;
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
	
	/**
	 * Nested Attractor class for helping with two-stage selection. Primary
	 * selection is always based on fitness.
	 * 
	 * @author Ramon A. Lovato
	 */
	public static enum Attractor {
		SURFACE_AREA,
		TOP_BOTTOM_RATIO,
		WEIGHT,
		//
		// Duplicates of Allele.Trait
		//
		EMPTY, // E (empty Allele)
		LENGTH, // L (length)
		WIDTH, // W (width)
		HEIGHT, // H (height)
		INDEX_TO_PARENT, // I (index to parent)
		JOINT_TYPE, // T (joint Type)
		JOINT_ORIENTATION, // O (joint orientation)
		JOINT_SITE_ON_PARENT, // P (joint site on Parent)
		JOINT_SITE_ON_CHILD, // C (joint site on Child)
		RULE_INPUT_A, // a (the five inputs to a rule)
		RULE_INPUT_B, // b (the five inputs to a rule)
		RULE_INPUT_C, // c (the five inputs to a rule)
		RULE_INPUT_D, // d (the five inputs to a rule)
		RULE_INPUT_E, // e (the five inputs to a rule)
		BINARY_OPERATOR_1, // 1 (binary operator in the 1st neuron of a rule)
		UNARY_OPERATOR_2, // 2 (unary operator in the 1st neuron of a rule)
		BINARY_OPERATOR_3, // 3 (binary operator in the 2nd neuron of a rule)
		UNARY_OPERATOR_4; // 4 (unary operator in the 2nd neuron of a rule)
		
		/**
		 * Override of toString.
		 * 
		 * @return Trait identifier as a String.
		 */
		@Override
		public String toString() {
			return name();
		}
	}
	
	/**
	 * Main method for testing.
	 */
	public static void main(String[] args) {
		//
		// TODO
		//
	}
	
}
