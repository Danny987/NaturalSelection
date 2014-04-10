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

import creature.geeksquad.library.Helper;

/**
 * A simple nested helper class for addBlock to get weights for the new
 * Alleles.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
public class WeightHelper {
	private boolean random;
	
	/**
	 * Instantiate a new weight helper to produce Allele weights of either
	 * random or Helper.MIN_WEIGHT.
	 * 
	 * @param random Boolean determining whether generated weights are random
	 *            or Helper.MINWEIGHT.
	 */
	public WeightHelper(boolean random) {
		this.random = random;
	}
	
	/**
	 * Get a random weight.
	 * 
	 * @return Random float between 0 (inclusive) and 1 (exclusive).
	 */
	public float weight() {
		return (random ? Helper.RANDOM.nextFloat() : Helper.MAX_WEIGHT);
	}
	
}
