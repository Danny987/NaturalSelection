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
public class Hopper implements Comparable<Hopper> {
	private final String name;
	private int age;
	private Genotype genotype;
	private Creature phenotype;
	private int timesHillClimbed;
	private int timesBred;
	private int children;
	private float fitness;
	private boolean realFitness;
//	private Attractor attractor;

	/**
	 * Instantiate a new Hopper with random Genotype and name.
	 * 
	 * @throws GeneticsException if thrown by Genotype.
	 * @throws IllegalArgumentException if there was a problem creating the
	 *             Genotype or Phenotype.
	 */
	public Hopper() throws IllegalArgumentException, GeneticsException {
		this(new Genotype(), randomName());
	}
	
	/**
	 * Instantiate a new Hopper with the passed Genotype and provided name.
	 * 
	 * @param genotype Genotype of the new Hopper.
	 * @param name String to use as the Hopper's name.
	 * @throws GeneticsException if thrown by Genotype.
	 * @throws IllegalArgumentException if there was a problem creating the
	 *             Genotype or Phenotype.
	 */
	public Hopper(Genotype genotype, String name)
			throws IllegalArgumentException, GeneticsException {
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
	 * @param crossover Crossover instance to use for this breed session.
	 * @return An array of twin child Hoppers resulting from the crossover of
	 *             the parents' genotypes.
	 * @throws IllegalArgumentException if generated Genotypes/Phenotypes
	 *             are invalid.
	 * @throws GeneticsException if thrown by Genotype constructor.
	 */
	public static Hopper[] breed(Hopper parentA, Hopper parentB,
			Strategy strategy, Crossover crossover) throws 
			IllegalArgumentException, GeneticsException {
		try {
			Genotype[] genotypes = crossover.crossover(parentA.getGenotype(),
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
	 * Sets the realFitness flag to true.
	 */
	public void setRealFitness() {
		realFitness = true;
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

//	/**
//	 * Getter for attractor.
//	 * 
//	 * @return The Hopper's Attractor.
//	 */
//	public Attractor getAttractor() {
//		return attractor;
//	}
//
//	/**
//	 * Setter for attractor.
//	 * 
//	 * @param attractor New Attractor to give this Hopper.
//	 */
//	public void setAttractor(Attractor attractor) {
//		this.attractor = attractor;
//	}

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
	 * Override of compareTo: allows for easy sorting of Hoppers by fitness.
	 * 
	 * @param other Hopper to compare fitness with.
	 * @return -1, 0, or 1 if this Hopper's fitness is less than, equal to, or
	 *             greater than the other Hopper's fitness.
	 */
	@Override
	public int compareTo(Hopper other) {
		float otherFitness = other.getFitness();
		if (fitness < otherFitness) {
			return -1;
		} else if (fitness > otherFitness) {
			return 1;
		} else {
			return 0;
		}
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
	 * Main method for testing.
	 * 
	 * @param args Command-line arguments.
	 */
	public static void main(String[] args) {
		try {
			Hopper hopper1 = new Hopper();
			System.out.println("---Hopper 1---");
			System.out.println(hopper1);
			System.out.println("---Phenotype 1---");
			System.out.println(hopper1.getPhenotype());
		} catch (IllegalArgumentException | GeneticsException ex) {
			ex.printStackTrace();
		}
	}

}
