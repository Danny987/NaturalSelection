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

import java.util.ArrayList;

import creature.geeksquad.genetics.Crossover.Strategy;
import creature.geeksquad.library.Helper;
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
	private Genotype genotype;
	private Creature phenotype;
	private Attractor attractor;
	private int timesHillClimbed;
	private int timesBred;
	private int children;
	private float fitness;
	private boolean realFitness;

	/**
	 * Instantiate a new Hopper with the passed Genotype and provided name.
	 * 
	 * @param genotype Genotype of the new Hopper.
	 * @param name String to use as the Hopper's name.
	 * @throws IllegalArgumentException if there was a problem creating the
	 *             Genotype or Phenotype.
	 */
	public Hopper(Genotype genotype, String name)
			throws IllegalArgumentException {
		try {
			this.genotype = genotype;
			this.phenotype = genotype.getPhenotype();
		} catch (IllegalArgumentException ex) {
			this.genotype = null;
			this.phenotype = null;
			throw ex;
		}
		if (genotype == null || phenotype == null) {
			System.err.println("Error: genotype/phenotype invalid.");
		}
		this.name = name;
		age = 0;
		timesHillClimbed = 0;
		timesBred = 0;
		children = 0;
		realFitness = false;
	}

	/**
	 * Instantiate a new deep clone of the passed Hopper.
	 * 
	 * @param source Hopper to deep clone.
	 * @throws IllegalArgumentException if thrown by Genotype.
	 * @throws GeneticsException if thrown by Genotype.
	 */
	public Hopper(Hopper source) throws IllegalArgumentException,
			GeneticsException {
		this(new Genotype(source.getGenotype()), new String(source.getName()));
	}

	/**
	 * Instantiate a new Hopper with the passed Genotype and random name.
	 * 
	 * @param genotype Genotype of the new Hopper.
	 * @throws IllegalArgumentException if thrown by Genotype.
	 * @throws GeneticsException if thrown by Genotype.
	 */
	public Hopper(Genotype genotype) throws IllegalArgumentException,
			GeneticsException {
		this(genotype, randomName());
	}

	/**
	 * Gets a random name for the Hopper.
	 * 
	 * @return String containing a random name for the Hopper.
	 */
	public static String randomName() {
		// TODO
		return "";
	}

	/**
	 * Breed two Hoppers.
	 * 
	 * @param parentA First parent Hopper.
	 * @param parentB Second parent Hopper.
	 * @param strategy Strategy of Crossover to use in breeding.
	 * @return An array of twin child Hoppers resulting from the crossover of
	 *             the parents' genotypes.
	 * @throws IllegalArgumentException if generated Genotypes/Phenotypes
	 *             are invalid.
	 * @throws GeneticsException if thrown by Genotype constructor.
	 */
	public static Hopper[] breed(Hopper parentA, Hopper parentB,
			Strategy strategy) throws IllegalArgumentException,
			GeneticsException {
		try {
			Genotype[] genotypes = Genotype.crossover(parentA.getGenotype(),
					parentB.getGenotype(), strategy);
			ArrayList<Hopper> hoppers = new ArrayList<Hopper>();

			for (Genotype g : genotypes) {
				hoppers.add(new Hopper(g));
			}

			return (Hopper[]) hoppers.toArray();
		} catch (IllegalArgumentException | GeneticsException ex) {
			throw ex;
		}
	}
	
	/**
	 * A pass-through getter for the Hopper's body, which calls Genotype's
	 * getBody method.
	 * 
	 * @return The Hopper's body as a Block array as passed through from its
	 * 	       Genotype's getBody method.
	 */
	public Block[] getBody() {
		return genotype.getBody();
	}
	
	/**
	 * Getter for fitness.
	 * 
	 * @return Hopper's fitness as a float.
	 */
	public float getFitness() {
		return fitness;
	}
	
	/**
	 * Setter for fitness.
	 * 
	 * @param Hopper's new fitness as a float.
	 */
	public void setFitness(float fitness) {
		this.fitness = fitness;
	}
	
	/**
	 * Checks if the fitness value is the real fitness from the simulation or
	 * just an estimate.
	 * 
	 * @return True if fitness is the real fitness from the simulation, false
	 *             if it's just an estimate.
	 */
	public boolean isRealFitness() {
		return realFitness;
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
	 * @param age
	 *            Hopper's new age as an int.
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
	 * Getter for timesHillClimbed.
	 * 
	 * @return Number of times this Hopper has been through hill climbing.
	 */
	public int getTimesHillClimbed() {
		return timesHillClimbed;
	}

	/**
	 * Getter for timesBred.
	 * 
	 * @return Number of times this Hopper has been bred.
	 */
	public int getTimesBred() {
		return timesBred;
	}

	/**
	 * Getter for children.
	 * 
	 * @return Number of children this Hopper has sired.
	 */
	public int getChildren() {
		return children;
	}

	/**
	 * Override of toString.
	 * 
	 * @return String representation of this Hopper.
	 */
	@Override
	public String toString() {
		return "<creature>" + Helper.NEWLINE + "<name>" + Helper.NEWLINE + name
				+ Helper.NEWLINE + "</name>" + Helper.NEWLINE + "<genotype>"
				+ Helper.NEWLINE + genotype.toString() + Helper.NEWLINE
				+ "</genotype>" + Helper.NEWLINE + "</creature>";
	}

	/**
	 * Nested Attractor class for helping with two-stage selection. Primary
	 * selection is always based on fitness.
	 */
	public static enum Attractor {
		SURFACE_AREA, TOP_BOTTOM_RATIO, WEIGHT,
		//
		// Duplicates of Allele.Trait
		//
		EMPTY, // E (empty Allele)
		LENGTH, // L (length)
		HEIGHT, // H (height)
		WIDTH, // W (width)
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
	 * 
	 * @param args Command-line arguments.
	 */
	public static void main(String[] args) {
		//
		// TODO
		//
	}

}
