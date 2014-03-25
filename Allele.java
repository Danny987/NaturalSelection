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

import creature.phenotype.*;

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
		value = "#";
		weight = 0.0f;
	}
	
	/**
	 * Instantiate Allele with value as a float (length, width, height, joint
	 * orientation).
	 * 
	 * @param trait The Trait for this Allele.
	 * @param value The value for this Allele as a float.
	 * @param weight The Allele's dominance as a float (between 0 and 1).
	 *               smaller = more recessive; larger = more dominant.
	 */
	public Allele(Trait trait, float value, float weight) {
		this(trait, new Float(value), weight);
	}
	
	/**
	 * Instantiate Allele with value as an int (index to parent).
	 * 
	 * @param trait The Trait for this Allele.
	 * @param value The value for this Allele as an int.
	 * @param weight The Allele's dominance as a float (between 0 and 1).
	 *               smaller = more recessive; larger = more dominant.
	 */
	public Allele(Trait trait, int value, float weight) {
		this(trait, new Integer(value), weight);
	}
	
	/**
	 * Instantiate Allele with value as an EnumJointType (joint type).
	 * 
	 * @param trait The Trait for this Allele.
	 * @param value The value for this Allele as an EnumJointType.
	 * @param weight The Allele's dominance as a float (between 0 and 1).
	 *               smaller = more recessive; larger = more dominant.
	 */
	public Allele(Trait trait, EnumJointType value, float weight) {
		this(trait, (Object) value, weight);
	}
	
	/**
	 * Instantiate Allele with value as an EnumJointSite (joint site on parent,
	 * joint site on child).
	 * 
	 * @param trait The Trait for this Allele.
	 * @param value The value for this Allele as an EnumJointSite.
	 * @param weight The Allele's dominance as a float (between 0 and 1).
	 *               smaller = more recessive; larger = more dominant.
	 */
	public Allele(Trait trait, EnumJointSite value, float weight) {
		this(trait, (Object) value, weight);
	}
	
	/**
	 * Instantiate Allele with value as an EnumOperatorBinary (binary operator).
	 * 
	 * @param trait The Trait for this Allele.
	 * @param value The value for this Allele as an EnumOperatorBinary.
	 * @param weight The Allele's dominance as a float (between 0 and 1).
	 *               smaller = more recessive; larger = more dominant.
	 */
	public Allele(Trait trait, EnumOperatorBinary value, float weight) {
		this(trait, (Object) value, weight);
	}
	
	/**
	 * Instantiate Allele with value as an EnumOperatorUnary (unary operator).
	 * 
	 * @param trait The Trait for this Allele.
	 * @param value The value for this Allele as an EnumOperatorUnary.
	 * @param weight The Allele's dominance as a float (between 0 and 1).
	 *               smaller = more recessive; larger = more dominant.
	 */
	public Allele(Trait trait, EnumOperatorUnary value, float weight) {
		this(trait, (Object) value, weight);
	}
	
	/**
	 * Instantiate Allele with value as a NeuronInput (neuron inputs).
	 * 
	 * @param trait The Trait for this Allele.
	 * @param value The value for this Allele as an NeuronInput.
	 * @param weight The Allele's dominance as a float (between 0 and 1).
	 *               smaller = more recessive; larger = more dominant.
	 */
	public Allele(Trait trait, NeuronInput value, float weight) {
		this(trait, (Object) value, weight);
	}
	
	/**
	 * A private constructor for Allele with the passed trait, value, and
	 * weight. Called by other constructors only since there need to be bounds
	 * on what type of Object value can be.
	 * 
	 * @param trait The Trait for this Allele.
	 * @param value The value for this Allele as any one of a number of
	 *              different Objects.
	 * @param weight The Allele's dominance as a float.
	 *               Smaller = more recessive; larger = more dominant.
	 * @throws IllegalArgumentException if arguments invalid for given trait.
	 */
	private Allele(Trait trait, Object value, float weight) throws
					IllegalArgumentException {
		// Check for valid input.
		switch (trait) {
			case LENGTH: case WIDTH: case HEIGHT:
				if ((Float) value < 1.0f) {
					throw new IllegalArgumentException(
							"Length, width, and height must be >= 1.0.");
				}
				break;
			case INDEX_TO_PARENT:
				if ((Integer) value < Block.PARENT_INDEX_NONE) {
					throw new IllegalArgumentException(
							"Index to parent must be >= -1.");
				}
				break;
			default:
				// Fall through.
		}
		
		this.trait = trait;
		this.value = value;
		this.weight = weight;
	}
	
	/**
	 * Check if this Allele is empty.
	 * 
	 * @return True if Allele is empty; false otherwise.
	 */
	public boolean isEmpty() {
		return trait.equals(Trait.EMPTY);
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
	 * A float setter for the Allele's value.
	 * 
	 * @param value Float to assign to this Allele's value.
	 * @throws IllegalArgumentException if input is invalid for this type.
	 */
	public void setValue(float value) throws IllegalArgumentException {
		if (trait.equals(Trait.HEIGHT) || trait.equals(Trait.WIDTH) ||
			trait.equals(Trait.LENGTH) ||
			trait.equals(Trait.JOINT_ORIENTATION)) {
				this.value = new Float(value);
		} else {
			throw new IllegalArgumentException("Allele of type Trait." + trait +
				" cannot be assigned value of type float.");
		}
	}
	
	/**
	 * An int setter for the Allele's value.
	 * 
	 * @param value Int to assign to this Allele's value.
	 * @throws IllegalArgumentException if input is invalid for this type.
	 */
	public void setValue(int value) throws IllegalArgumentException {
		if (trait.equals(Trait.INDEX_TO_PARENT)) {
				this.value = new Integer(value);
		} else {
			throw new IllegalArgumentException("Allele of type Trait." + trait +
				" cannot be assigned value of type int.");
		}
	}
	
	/**
	 * An EnumJointType setter for the Allele's value.
	 * 
	 * @param value EnumJointType to assign to this Allele's value.
	 * @throws IllegalArgumentException if input is invalid for this type.
	 */
	public void setValue(EnumJointType value) throws IllegalArgumentException {
		if (trait.equals(Trait.JOINT_TYPE)) {
			this.value = value;
		} else {
			throw new IllegalArgumentException("Allele of type Trait." + trait +
				" cannot be assigned value of type EnumJointType.");
		}
	}
	
	/**
	 * An EnumJointSite setter for the Allele's value.
	 * 
	 * @param value EnumJointSite to assign to this Allele's value.
	 * @throws IllegalArgumentException if input is invalid for this type.
	 */
	public void setValue(EnumJointSite value) throws IllegalArgumentException {
		if (trait.equals(Trait.JOINT_SITE_ON_PARENT) ||
				trait.equals(Trait.JOINT_SITE_ON_CHILD)) {
			this.value = value;
		} else {
			throw new IllegalArgumentException("Allele of type Trait." + trait +
				" cannot be assigned value of type EnumJointSite.");
		}
	}
	
	/**
	 * An EnumOperatorBinary setter for the Allele's value.
	 * 
	 * @param value EnumOperatorBinary to assign to this Allele's value.
	 * @throws IllegalArgumentException if input is invalid for this type.
	 */
	public void setValue(EnumOperatorBinary value)
								throws IllegalArgumentException {
		if (trait.equals(Trait.BINARY_OPERATOR_1) ||
				trait.equals(Trait.BINARY_OPERATOR_3)) {
			this.value = value;
		} else {
			throw new IllegalArgumentException("Allele of type Trait." + trait +
				" cannot be assigned value of type EnumOperatorBinary.");
		}
	}
	
	/**
	 * An EnumOperatorUnary setter for the Allele's value.
	 * 
	 * @param value EnumOperatorUnary to assign to this Allele's value.
	 * @throws IllegalArgumentException if input is invalid for this type.
	 */
	public void setValue(EnumOperatorUnary value)
								throws IllegalArgumentException {
		if (trait.equals(Trait.UNARY_OPERATOR_2) ||
				trait.equals(Trait.UNARY_OPERATOR_4)) {
			this.value = value;
		} else {
			throw new IllegalArgumentException("Allele of type Trait." + trait +
				" cannot be assigned value of type EnumOperatorUnary.");
		}
	}
	
	/**
	 * An NeuronInput setter for the Allele's value.
	 * 
	 * @param value NeuronInput to assign to this Allele's value.
	 * @throws IllegalArgumentException if input is invalid for this type.
	 */
	public void setValue(NeuronInput value)
								throws IllegalArgumentException {
		if (trait.equals(Trait.RULE_INPUT_A) ||
				trait.equals(Trait.RULE_INPUT_B) ||
				trait.equals(Trait.RULE_INPUT_C) ||
				trait.equals(Trait.RULE_INPUT_D) ||
				trait.equals(Trait.RULE_INPUT_E)) {
			this.value = value;
		} else {
			throw new IllegalArgumentException("Allele of type Trait." + trait +
				" cannot be assigned value of type EnumJointType.");
		}
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
		return new String("(" + trait + ":" + value + ":" + weight + ")");
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
		BINARY_OPERATOR_1, // 1 (binary operator in the 1st neuron of a rule)
		UNARY_OPERATOR_2, // 2 (unary operator in the 1st neuron of a rule)
		BINARY_OPERATOR_3, // 3 (binary operator in the 2nd neuron of a rule)
		UNARY_OPERATOR_4; // 4 (unary operator in the 2nd neuron of a rule)
		
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
	
	/**
	 * Allele main method for testing.
	 */
	public static void main(String[] args) {
		java.util.ArrayList<Allele> alleles = new java.util.ArrayList<Allele>();
		// Adding some dummy Alleles to the list.
		alleles.add(new Allele(Trait.HEIGHT, 42.5f, 0.5f));
		alleles.add(new Allele(Trait.INDEX_TO_PARENT, Block.PARENT_INDEX_NONE,
				               0.4f));
		alleles.add(new Allele(Trait.JOINT_TYPE, EnumJointType.RIGID, 0.3f));
		
		// Iterating over the list with for-each.
		for (Allele a : alleles) {
			Allele.Trait trait = a.getTrait();
			// Switch off the Allele's trait.
			switch (trait) {
				case HEIGHT: case WIDTH: case LENGTH:
					float f = (Float) a.getValue();
					System.out.println("Allele " + a + " --> (Float) " + f);
					break;
				case INDEX_TO_PARENT:
					int i = (Integer) a.getValue();
					System.out.println("Allele " + a + " --> (Integer) " + i);
					break;
				case JOINT_TYPE:
					EnumJointType j = (EnumJointType) a.getValue();
					System.out.println("Allele " + a + " --> (EnumJointType) "
									    + j);
					break;
				default:
					break;
			}
		}
		
		// Check to make sure the setters throw exceptions properly.
		try {
			alleles.get(0).setValue(EnumJointType.HINGE);
		} catch (IllegalArgumentException ex) {
			System.out.println();
			System.out.println("***** THIS EXCEPTION MEANS IT'S WORKING *****");
			ex.printStackTrace();
		}
		
	}
	
}
