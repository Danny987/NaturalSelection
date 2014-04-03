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
	 *   H (height)
	 *   W (width)
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
	 * differ between parent. Declared deliberately as package-private so that
	 * other genetics modules can access it but hill-climbing cannot.
	 */
	Allele() {
		trait = Trait.EMPTY;
		value = "none";
		weight = 0.0f;
	}
	
	/**
	 * Instantiates an Allele as a deep copy of a passed source Allele.
	 * 
	 * @param source Allele to deep copy.
	 */
	public Allele(Allele source) {
		this(source.getTrait(), source.getValue(), source.getWeight());
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
	 * Instantiate Allele with value as an int (index to parent, degree of
	 * freedom marker).
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
		this(trait, (Object) copyNeuron(value), weight);
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
			case DOF_MARKER:
				int dof = (Integer) value;
				if (dof < EnumJointType.DOF_1 || dof > EnumJointType.DOF_2) {
					throw new IllegalArgumentException(
							"Degree of freedom outside acceptible range; " +
							"must be 0 or 1.");
				}
			default:
				// Fall through.
		}
		
		this.trait = trait;
		this.value = value;
		this.weight = weight;
	}
	
	/**
	 * A helper method for the constructor, which clones the passed NeuronInput.
	 * 
	 * @param original NeuronInput to clone.
	 */
	public static NeuronInput copyNeuron(NeuronInput original) {
		NeuronInput neuron;
		// Fields from original NeuronInput.
		EnumNeuronInputType type = original.getType();
		// Used only for CONSTANT type.
		float constantValue = original.getConstantValue();
		// Used for HEIGHT, TOUCH and JOINT types.
		int boxIndex = original.getBoxIndex();
		// Used only for JOINT type.
		int degreeOfFreedom = original.getDOF();

		switch (type) {
			case TIME:
				neuron = new NeuronInput(type);
				break;
			case CONSTANT:
				neuron = new NeuronInput(type, constantValue);
				break;
			case HEIGHT: case TOUCH:
				neuron = new NeuronInput(type, boxIndex);
				break;
			case JOINT:
				neuron = new NeuronInput(type, boxIndex, degreeOfFreedom);
				break;
			default:
				neuron = null;
		}
		
		return neuron;
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
	 * Getter for the Allele's value. Note that, to actually use this for
	 * anything useful, the Allele's trait must first be checked so the value
	 * can be cast to the appropriate type.
	 * 
	 * @return The Allele's value as an Object.
	 */
	public Object getValue() {
		return value;
	}
	
	/**
	 * Getter for the Allele's weight.
	 * 
	 * @return The Allele's weight as a float.
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
		if (trait.equals(Trait.INDEX_TO_PARENT) ||
					(trait.equals((Trait.DOF_MARKER)))) {
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
	 * A NeuronInput setter for the Allele's value.
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
	 * Create a completely fresh Allele built from an input String. Used
	 * when importing Hoppers from a file.
	 * 
	 * @param alleleString String representation from which to build the new
	 *                     Allele, formatted according to the toString rules:
	 *                         (trait:value:weight)
	 */
	public static Allele stringToAllele(String alleleString)
			throws GeneticsException {
		// Trim the parentheses off the string.
		String trimmedString = alleleString.substring(1,
					alleleString.length() - 1);
		// Split the string on the colon.
		//   substrings[0] trait
		//   substrings[1] value
		//   substrings[2] weight
		String[] substrings = trimmedString.split(":");
		Trait trait = Trait.valueOf(substrings[0]);
		float weight = Float.valueOf(substrings[2]);
		Object value;
		
		switch (trait) {
			case LENGTH: case WIDTH: case HEIGHT: case JOINT_ORIENTATION:
				value = new Float(Float.valueOf(substrings[1]));
				break;
			case INDEX_TO_PARENT: case DOF_MARKER:
				value = new Integer(Integer.valueOf(substrings[1]));
				break;
			case JOINT_TYPE:
				value = EnumJointType.valueOf(substrings[1]);
				break;
			case JOINT_SITE_ON_PARENT: case JOINT_SITE_ON_CHILD:
				value = EnumJointSite.valueOf(substrings[1]);
				break;
			case RULE_INPUT_A: case RULE_INPUT_B: case RULE_INPUT_C:
			case RULE_INPUT_D: case RULE_INPUT_E:
				value = stringToNeuron(substrings[1]);
				break;
			case BINARY_OPERATOR_1: case BINARY_OPERATOR_3:
				value = EnumOperatorBinary.valueOf(substrings[1]);
				break;
			case UNARY_OPERATOR_2: case UNARY_OPERATOR_4:
				value = EnumOperatorUnary.valueOf(substrings[1]);
				break;
			default:
				return null;
		}
		
		return new Allele(trait, value, weight);
	}
	
	/**
	 * Makes a NeuronInput from a String formatted according to NeuronInput's
	 * toString.
	 * 
	 * @param neuronString String representing a NeuronInput.
	 * @return NeuronInput indicated by the passed String.
	 */
	public static NeuronInput stringToNeuron(String neuronString) {
		NeuronInput neuron;
		// Trim parentheses.
		String trimmedString = neuronString.substring(1,
					neuronString.length() - 1);
		// Since the NeuronInput toString is formatted differently for the
		// different types, we need to use regex to split it.
		String[] strings = trimmedString.split("[\\W&&[^\\.]]+");
		
		EnumNeuronInputType inputType = EnumNeuronInputType.valueOf(strings[0]);
		int boxIndex;
		
		switch (inputType) {
			case TIME:
				neuron = new NeuronInput(inputType);
				break;
			case CONSTANT:
				float constantValue = Float.valueOf(strings[1]);
				neuron = new NeuronInput(inputType, constantValue);
				break;
			case HEIGHT: case TOUCH:
				boxIndex = Integer.valueOf(strings[1]);
				neuron = new NeuronInput(inputType, boxIndex);
				break;
			case JOINT:
				boxIndex = Integer.valueOf(strings[1]);
				int degreeOfFreedom = Integer.valueOf(strings[2]);
				neuron = new NeuronInput(inputType, boxIndex, degreeOfFreedom);
				break;
			default:
				neuron = null;
		}	
		
		return neuron;
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
	 */
	public static enum Trait {
		EMPTY, // E (empty Allele).
		LENGTH, // L (length).
		HEIGHT, // H (height).
		WIDTH, // W (width).
		INDEX_TO_PARENT, // I (index to parent).
		JOINT_TYPE, // T (joint Type).
		JOINT_ORIENTATION, // O (joint orientation).
		JOINT_SITE_ON_PARENT, // P (joint site on Parent).
		JOINT_SITE_ON_CHILD, // C (joint site on Child).
		RULE_INPUT_A, // a (the five inputs to a rule).
		RULE_INPUT_B, // b (the five inputs to a rule).
		RULE_INPUT_C, // c (the five inputs to a rule).
		RULE_INPUT_D, // d (the five inputs to a rule).
		RULE_INPUT_E, // e (the five inputs to a rule).
		BINARY_OPERATOR_1, // 1 (binary operator in the 1st neuron of a rule).
		UNARY_OPERATOR_2, // 2 (unary operator in the 1st neuron of a rule).
		BINARY_OPERATOR_3, // 3 (binary operator in the 2nd neuron of a rule).
		UNARY_OPERATOR_4, // 4 (unary operator in the 2nd neuron of a rule).
		DOF_MARKER; // End of a degree of freedom.
	}
	
	/**
	 * Allele main method for testing.
	 * 
	 * @param args Command-line arguments.
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
		
		System.out.println("-------------------------------------------------");
		Allele timeNeuron = new Allele(Trait.RULE_INPUT_A, new NeuronInput(
					EnumNeuronInputType.TIME), 0.5f);
		System.out.println("timeNeuron " + timeNeuron);
		Allele constantNeuron = new Allele(Trait.RULE_INPUT_B, new NeuronInput(
					EnumNeuronInputType.CONSTANT, 0.01f), 0.5f);
		System.out.println("constantNeuron " + constantNeuron);
		Allele heightNeuron = new Allele(Trait.RULE_INPUT_C, new NeuronInput(
					EnumNeuronInputType.HEIGHT, 0), 0.5f);
		System.out.println("heightNeuron " + heightNeuron);
		Allele touchNeuron = new Allele(Trait.RULE_INPUT_D, new NeuronInput(
					EnumNeuronInputType.TOUCH, 0), 0.5f);
		System.out.println("touchNeuron " + touchNeuron);
		Allele jointNeuron = new Allele(Trait.RULE_INPUT_E, new NeuronInput(
					EnumNeuronInputType.JOINT, 0, 0), 0.5f);
		System.out.println("jointNeuron " + jointNeuron);
		System.out.println("-------------------------------------------------");
		System.out.println();
		
	}
	
}
