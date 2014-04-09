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

import java.util.HashMap;
import java.util.Map;

import creature.geeksquad.genetics.Allele.Key;
import creature.geeksquad.genetics.Allele.Value;
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
	private boolean random;
	private Map<Key, Value> weightMap = new HashMap<Key, Value>();
	
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
		random = false;
		// Deep clone the map.
		for (Map.Entry<Key, Value> e : cross.getMap().entrySet()) {
			weightMap.put(e.getKey(), new Value(e.getValue()));
		}
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
	 * return a random float.
	 * 
	 * @param allele Allele to look up in the weight table.
	 * @return The float assigned to allele in the weight table. If the
	 *             weight table wasn't set, returns a random float. If the
	 *             weight table was set but the allele isn't in the weight
	 *             table, returns Helper.MEDIAN_WEIGHT (5.0f).
	 */
	public float weight(Allele allele) {
		if (random) {
			return weight();
		} else if (weightMap.containsKey(allele)) {
			return weightMap.get(allele).getWeight();
		} else {
			return Helper.MEDIAN_WEIGHT;
		}
	}
}
