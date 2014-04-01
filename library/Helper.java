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
	public static int binary() {
		return RANDOM.nextInt(2);
	}

}
