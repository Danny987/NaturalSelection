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

import creature.geeksquad.gui.Names;
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
	// Class variables.
	private static int totalHoppers = 0;
	// Instance variables.
	private String name;
	private int age;
	private final long serial;
	private Genotype genotype;
	private Creature phenotype;
	private int timesHillClimbed;
	private int timesBred;
	private int children;
	private float fitness;
	private int fitnessEvaluations;

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
	 * Unique (non-cloned) Hoppers each get a unique serial number.
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
			this.genotype = new Genotype(genotype);
			this.phenotype = this.genotype.getPhenotype();
		} catch (IllegalArgumentException ex) {
			this.genotype = null;
			this.phenotype = null;
			throw ex;
		}
		if (genotype == null || phenotype == null) {
			System.err.println(
					"Error: Hopper genotype/phenotype instantiation was null.");
		}
		if (name != null) {
			this.name = name;
		} else {
			name = randomName();
		}
		age = 0;
		serial = totalHoppers++;
		timesHillClimbed = 0;
		timesBred = 0;
		children = 0;
		fitness = 0.0f;
		fitnessEvaluations = 0;
	}

	/**
	 * Instantiate a new deep clone of the passed Hopper. The cloned Hopper
	 * adopts the serial number from its source.
	 * 
	 * @param source Hopper to deep clone.
	 * @throws IllegalArgumentException if thrown by Genotype.
	 * @throws GeneticsException if thrown by Genotype.
	 */
	public Hopper(Hopper source) throws IllegalArgumentException,
			GeneticsException {
		try {
			genotype = new Genotype(source.getGenotype());
			this.phenotype = genotype.getPhenotype();
		} catch (IllegalArgumentException ex) {
			this.genotype = null;
			this.phenotype = null;
			throw ex;
		}
		if (genotype == null || phenotype == null) {
			System.err.println(
					"Error: Hopper genotype/phenotype instantiation was null.");
		}
		serial = source.getSerial();
		name = source.getName();
		age = source.age;
		timesHillClimbed = source.getTimesHillClimbed();
		timesBred = source.getTimesBred();
		children = source.getChildren();
		fitness = source.getFitness();
		fitnessEvaluations = source.getFitnessEvals();
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
		this(new Genotype(genotype), randomName());
	}

	/**
	 * Gets a random name for the Hopper.
	 * 
	 * @return String containing a random name for the Hopper.
	 */
	public static String randomName() {
		return Names.getHopperName();
	}
	
	/**
	 * Check the number of times this Hopper's fitness has been evaluated.
	 * 
	 * @return Number of times the fitness has been evaluated.
	 */
	public int getFitnessEvals() {
		return fitnessEvaluations;
	}
	
	/**
	 * Evaluate the Hopper's fitness using the physics simulator.
	 * 
	 * @return float Hopper's highest fitness achieved during the simulation.
	 */
	public float evalFitness() {
		float peak = 0.0f;
		boolean done = false;
		int steps = 0;
		float test1 = -1.0f;
		float test2 = -1.0f;
		float change = 0;
		phenotype.resetSimulation();
		
		while (!done) {			
			try {
				// If test1 < 0, this is the first time through the loop, so
				// run the simulation twice. Otherwise, just set test1 to the
				// previous value of test2 and run it once.
				if (test1 < 0) {
					test1 = phenotype.advanceSimulation();
				} else {
					test1 = test2;
				}
				test2 = phenotype.advanceSimulation();
				float currentMax = (test1 > test2 ? test1 : test2);
				if (currentMax > peak) {
					peak = currentMax;
				}
				
				change = test2 - test1;
			} catch (IllegalArgumentException ex) {
				throw ex;
			}
			
			// Descending or stationary - since the bounces are always lower
			// than the initial jump, we can stop the simulation as soon as the
			// creature starts to descend. However, if the creature spawns in
			// the air or its body settles before it starts its initial jump,
			// we want to let that happen so the jump can occur. A small amount
			// of padding is provided as a safety measure.
			if (change <= 0 && steps++ >= Helper.PADDING) {
				done = true;
			}
		}
		
		fitnessEvaluations++;
		fitness = peak;
		
		return peak;
	}
	
	/**
	 * Determine if Hopper is eligible for breeding. A hopper is considered
	 * eligible if it has (1) at least one non-rigid joint and (2) at least
	 * one rule.
	 */
	public boolean isEligible() {
		return genotype.hasRules() && genotype.hasNonRigidJoints();
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
		if (fitnessEvaluations < 1) {
			evalFitness();
		}
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
	 * Getter for chromosome - a pass-through method for the Genotype's
	 * getChromosome method.
	 * 
	 * @return ArrayList<Gene> containing the Hopper's chromosome.
	 */
	public ArrayList<Gene> getChromosome() {
		return genotype.getChromosome();
	}
	
	/**
	 * Increment timesHillClimbed.
	 */
	public void hillClimbed() {
		timesHillClimbed++;
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
	 * Increment timesBred.
	 */
	public void increaseBreedCount() {
		timesBred++;
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
	 * Getter for serialNumber.
	 * 
	 * @return Hopper's serial number as a long.
	 */
	public long getSerial() {
		return serial;
	}
	
	/**
	 * A static getter for the Class variable hopperCount, which represents
	 * the total number of unique (non-cloned) Hoppers ever created.
	 * 
	 * @return The total number of unique (non-cloned) Hoppers ever created.
	 */
	public static long getTotalHoppers() {
		return totalHoppers;
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
		float thisFitness = getFitness();
		float otherFitness = other.getFitness();
		if (thisFitness < otherFitness) {
			return -1;
		} else if (thisFitness > otherFitness) {
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
		Hopper hopper1 = null;
		while (hopper1 == null) {
			try {
				hopper1 = new Hopper();
			} catch (IllegalArgumentException | GeneticsException ex) {}
		}
		System.out.println("---Hopper 1---");
		System.out.println(hopper1);
		System.out.println("---Phenotype 1---");
		System.out.println(hopper1.getPhenotype());
		System.out.println("---Fitness 1---");
//		System.out.println("Peak Fitness: " + hopper1.getFitness());
		float peakFitness = 0;
		for (int i = 0; i < 10000; i++) {
			float fitness = hopper1.getPhenotype().advanceSimulation();
			if (fitness > peakFitness) {
				peakFitness = fitness;
			}
		}
		System.out.println("Peak Fitness: " + peakFitness);
		System.out.println("hopper1.getFitness(): " + hopper1.getFitness());
	}

}
