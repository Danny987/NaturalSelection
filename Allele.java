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
public class Allele {
	/*
	 * 16 gene types (from project specs):
	 * L (length)
	 * W (width)
	 * H (height)
	 * I (index to parent)
	 * T (joint Type)
	 * O (joint orientation)
	 * P (joint site on Parent)
	 * C (joint site on Child)
	 * a, b, c, d, e (the five inputs to a rule)
	 * 1 (binary operator in the 1st neuron of a rule)
	 * 2 (unary operator in the 1st neuron of a rule)
	 * 3 (binary operator in the 2nd neuron of a rule)
	 * 2 (unary operator in the 2nd neuron of a rule)
	 */
	private char trait;
	private float value;
	private float weight;
	
	/**
	 * Instantiates Allele with the passed trait and weight values.
	 * 
	 * @param trait The trait for this Allele as a char.
	 * @param weight The Allele's dominance as a float (between 0 and 1).
	 *               smaller = more recessive; larger = more dominant.
	 */
	public Allele(char trait, float value, float weight) {
		this.trait = trait;
		this.value = value;
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
	 * Getter for the Allele's value.
	 * 
	 * @return The Allele's value.
	 */
	public float getValue() {
		return value;
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
	
	/**
	 * Override of equals.
	 * 
	 * @param other Allele to compare to.
	 * @return True if same trait and value match; false otherwise.
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof Allele) {
			return trait == ((Allele) other).getTrait() &&
				   value == ((Allele) other).getValue();
		} else {
			return false;
		}
	}
	
	/**
	 * Override of hashCode.
	 * 
	 * @return hashCode of this Gene's alleles array.
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	/**
	 * Override of toString.
	 * 
	 * @return String representation as "[Trait:Value:Weight]".
	 */
	@Override
	public String toString() {
		return new String("[" + trait + ":" + value + ":" + weight + "]");
	}
}
