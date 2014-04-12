/**
 * Evolving Virtual Jumping Creatures
 * CS 351, Project 2
 * 
 * Team members:
 * Ramon A. Lovato
 * Danny Gomez
 * Marcos Lemus
 */

package creature.geeksquad.library;

import java.util.Random;

import creature.geeksquad.gui.Tribe;
import creature.physics.Simulator;

/**
 * An Allele class for the Genotype.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
public class Helper {
	public static final Random RANDOM = new Random();
	public static final String NEWLINE = System.getProperty("line.separator");
	public static final String SEPARATOR = System.getProperty("file.separator");
	public static final int POPULATION_SIZE = Tribe.POPULATION_SIZE;
	public static final int MAX_BLOCKS = 50;
	// HASH_PRIME is a small prime number for Allele's hash code generation.
	public static final int HASH_PRIME = 31;
	// Use the top 20% of the Population for breeding each generation. This
	// mirrors the portion used in the original Karl Sims experiment.
	public static final float BREED_PERCENTAGE = 0.2f;
	// Allow up to 50 errors to accrue during random Genotype generation before
	// giving up.
	public static final int FAULT_TOLERENCE = 50;
	// Seed constants.
	public static final int SEED_MAX_BLOCKS = 12;
	public static final int SEED_MAX_SIZE = 10;
	public static final int SEED_MAX_CONSTANT = 20;
	public static final int SEED_MAX_RULES = 10;
	// Allele weight constants.
	public static final float MIN_WEIGHT = 0.0f;
	public static final float MEDIAN_WEIGHT = 0.5f;
	public static final float MAX_WEIGHT = 1.0f;
	// Percentage by which to increase/decrease Allele weights.
	public static final float WEIGHT_STEP_UP = 0.01f;
	public static final float WEIGHT_STEP_DOWN = 0.1f;
	// How many generations between seeding of new random Hoppers.
	public static final int SEED_NEW_RANDOMS_GAP = 10;
	public static final float RANDOM_RESEED_PERCENTAGE = 0.2f;
	// Amount of padding when evaluating fitness with the physics simulation.
	public static final float TIME_STEP = Simulator.DEFAULT_TIME_STEP;
	public static final int PADDING = 0;
	// Maximum permitted percentage similarity between a child and its parent.
	public static final float MAX_SIMILAR_PERCENTAGE = 0.9f;
	// How often to check the whole Population and eliminate excess duplicates.
	public static final float SIMILAR_CHECK_INTERVAL = 50;
	
	/**
	 * Sets the random number generator's seed.
	 * 
	 * @param seed Long to use as the RNG seed.
	 */
	public static void seed(long seed) {
		RANDOM.setSeed(seed);
	}
	
	/**
	 * Randomly choose 0 or 1.
	 * 
	 * @return 0 or 1.
	 */
	public static int choose() {
		return RANDOM.nextInt(2);
	}

}
