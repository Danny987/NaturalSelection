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
 * An Allele class for the Genotype.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
public class Allele {
	/*
	 * 16 gene types (from project specs):
	 *   L (length)
	 *   W (width)
	 *   H (height)
	 *   I (index to parent)
	 *   T (joint Type)
	 *   O (joint orientation)
	 *   P (joint site on Parent)
	 *   C (joint site on Child)
	 *   a, b, c, d, e (the five inputs to a rule)
	 *   1 (binary operator in the 1st neuron of a rule)
	 *   2 (unary operator in the 1st neuron of a rule)
	 *   3 (binary operator in the 2nd neuron of a rule)
	 *   4 (unary operator in the 2nd neuron of a rule)
	 *   ...etc.
	 */
	private Trait trait;
	private Object value;
	private float weight;
	
	/**
	 * Instantiates an empty Allele. Used for padding when strand lengths
	 * differ between parent.
	 */
	public Allele() {
		trait = Trait.EMPTY;
		value = "N/A";
		weight = 0.0f;
	}
	
	/**
	 * Instantiates Allele with the passed trait, value, and weight.
	 * 
	 * @param trait The Trait for this Allele.
	 * @param value The value for this Allele as any one of a number of
	 *              different Objects.
	 * @param weight The Allele's dominance as a float (between 0 and 1).
	 *               smaller = more recessive; larger = more dominant.
	 */
	public Allele(Trait trait, Object value, float weight) {
		this.trait = trait;
		this.value = value;
		this.weight = weight;
	}
	
	/**
	 * Getter for the Allele's trait.
	 * 
	 * @return The Allele's trait.
	 */
	public Trait getTrait() {
		return trait;
	}
	
	/**
	 * Getter for the Allele's value.
	 * 
	 * @return The Allele's value.
	 */
	public Object getValue() {
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
			return trait.equals(((Allele) other).getTrait()) &&
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
	
	/**
	 * A nested Trait enum for the Alleles.
	 * 
	 * @author Ramon A. Lovato
	 */
	public static enum Trait {
		EMPTY, // E (empty Allele)
		LENGTH, // L (length)
		WIDTH, // W (width)
		HEIGHT, // H (height)
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
		BINARY_OPERATOR, // 1 (binary operator for a neuron of a rule)
		UNARY_OPERATOR; // 2 (unary operator for a neuron of a rule)
		
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
	
}
