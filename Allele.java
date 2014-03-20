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

/**
 * An abstract Allele class for the Genotype.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
public abstract class Allele {
	protected char trait;
	protected float weight;
	protected float nextWeight;
	
	/**
	 * Instantiates Allele with the passed trait and weight values.
	 * 
	 * @param trait The trait for this Allele as a char.
	 * @param weight The Allele's dominance as a float (between 0 and 1).
	 *               smaller = more recessive; larger = more dominant.
	 */
	public Allele(char trait, float weight) {
		this.trait = trait;
		this.weight = weight;
	}
	
	/**
	 * Getter for the Allele's trait.
	 * 
	 * @return The Allele's traits.
	 */
	public char getTrait() {
		return trait;
	}
	
	/**
	 * Getter for the Allele's weight.
	 * 
	 * @return The Allele's weight.
	 */
	public float getWeight() {
		return weight;
	}
	
	/**
	 * Setter for the Allele's weight.
	 * 
	 * @param weight New weight to give this Allele as a float.
	 */
	public void setWeight(float weight) {
		this.weight = weight;
	}
	
}
