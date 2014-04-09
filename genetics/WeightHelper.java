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
 * Alleles. If passed a Crossover object on instantiation, it clones
 * that Crossover's weight table and uses it for the weights; otherwise,
 * it generates random weights.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
public class WeightHelper {
	private Crossover cross;
	private boolean random;
	
	/**
	 * Default constructor makes the weight methods always return a random
	 * float.
	 */
	public WeightHelper() {
		random = true;
	}
	
	/**
	 * The constructor that takes a Crossover object makes weight(Allele)
	 * return a weight based on that Allele in the weight table.
	 * 
	 * @param cross Crossover from which to clone the weight table.
	 */
	public WeightHelper(Crossover cross) {
		this.cross = cross;
		random = false;
	}
	
	/**
	 * Get a random weight.
	 * 
	 * @return Random float between 0 (inclusive) and 1 (exclusive).
	 */
	public float weight() {
		return Helper.RANDOM.nextFloat();
	}
	
	/**
	 * Get a weight from the weight table based on the passed Allele. If
	 * the table wasn't set (default constructor used, random = false),
	 * return a random float. If the weight wasn't in the table, it's a new
	 * Allele (as from hill climbing), so it gets Helper.MAX_WEIGHT, since
	 * we already know hill climbed Alleles are beneficial.
	 * 
	 * @param allele Allele to look up in the weight table.
	 * @return The float assigned to allele in the weight table. If the
	 *             weight table wasn't set, returns a random float. If the
	 *             weight table was set but the allele isn't in the weight
	 *             table, returns Helper.MAX_WEIGHT (1.0f).
	 */
	public float weight(Allele allele) {
		if (random) {
			return weight();
		} else if (cross.getMap().containsKey(allele.key)) {
			// Substantially boost the weight of the Allele. If it exceeds
			// Helper.MAX_WEIGHT, setWeight will cap it at MAX_WEIGHT.
			float newWeight = cross.getMap().get(allele.key).getWeight()
					+ Helper.MEDIAN_WEIGHT;
			cross.setWeight(allele, newWeight);
			return cross.getMap().get(allele.key).getWeight();
		} else {
			return Helper.MAX_WEIGHT;
		}
	}
}
