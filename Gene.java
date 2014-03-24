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

import creature.geeksquad.genetics.Allele.Trait;

/**
 * A Gene class for the creatures.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
public class Gene {
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
	private final Allele[] alleles = new Allele[2];
	private Allele dominant;
	private Trait trait;
	private Object value;
	
	/**
	 * Instantiate an empty Gene. Used as padding for strands of different
	 * length during crossover.
	 */
	public Gene() {
		this(new Allele(), new Allele());
	}
	
	/**
	 * Instantiate a Gene with provided Alleles. The traits of the two Alleles
	 * should match.
	 * 
	 * @param Allele alleleA First allele - trait must match second allele.
	 * @param Allele alleleB Second allele - trait must match first allele.
	 */
	public Gene(Allele alleleA, Allele alleleB) {
		// The traits of the Genes should never be mismatched.
		if (!alleleA.getTrait().equals(alleleB.getTrait())) {
			alleles[0] = null;
			alleles[1] = null;
			dominant = null;
			trait = null;
			value = 0;
			System.err.println("Error: Invalid mismatched alleles in gene.");
		} else {
			alleles[0] = alleleA;
			alleles[1] = alleleB;
			dominant = (alleles[0].getWeight() >= alleles[1].getWeight() ?
					    alleles[0] : alleles[1]);
			trait = dominant.getTrait();
			value = dominant.getValue();
		}
	}
	
	/**
	 * Instantiate a Gene with provided Allele array. The traits of the two
	 * Alleles should match.
	 * 
	 * @param Allele allele1 First allele - trait must match second allele.
	 * @param Allele allele2 Second allele - trait must match first allele.
	 */
	public Gene(Allele[] alleles) {
		this(alleles[0], alleles[1]);
	}

	/**
	 * Getter for alleles.
	 * 
	 * @return Two-element Allele array.
	 */
	public Allele[] getAlleles() {
		return alleles;
	}
	
	/**
	 * Getter for dominant allele.
	 * 
	 * @return The dominant Allele being expressed (with the highest weight).
	 */
	public Allele getDominant() {
		return dominant;
	}
	
	/**
	 * Getter for this Gene's trait. The traits of the two Alleles should
	 * always be the same. If they're mismatched on instantiation, the trait
	 * gets set to null.
	 * 
	 * @return Returns trait as Trait (null if two Alleles are mismatched).
	 */
	public Trait getTrait() {
		return trait;
	}
	
	/**
	 * Getter for the value of this Gene's dominant trait.
	 * 
	 * @return The value of this Gene's dominant Allele as an Object.
	 */
	public Object getValue() {
		return value;
	}
	
	/**
	 * Override of equals.
	 * 
	 * @param other Gene to compare to.
	 * @return True if Alleles' traits and values match; false otherwise.
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof Gene) {
			Allele[] otherAlleles = ((Gene) other).getAlleles();
			boolean firstPair = false;
			boolean secondPair = false;
			// Evaluate across and diagonally.
			if (alleles[0].equals(otherAlleles[0])) {
				firstPair = true;
				if (alleles[1].equals(otherAlleles[1])) {
					secondPair = true;
				}
			} else if (alleles[0].equals(otherAlleles[1])) {
				firstPair = true;
				if (alleles[1].equals(otherAlleles[0])) {
					secondPair = true;
				}
			}
			return firstPair && secondPair;
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
	 * @return String representation as "[(alleles[0])(alleles[1])]".
	 */
	@Override
	public String toString() {
		return new String("[" + alleles[0].toString() +
				                alleles[1].toString() + "]");
	}
	
}
