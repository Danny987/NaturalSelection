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
 * A Gene class for the creatures.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
public class Gene {
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
	private Allele[] alleles = new Allele[2];
	private Allele dominant;
	
	/**
	 * Instantiate a Gene with provided Alleles.
	 * 
	 * @param Allele allele1 First allele.
	 * @param Allele allele2 Second allele.
	 */
	public Gene(Allele allele1, Allele allele2) {
		alleles[0] = allele1;
		alleles[1] = allele2;
		dominant = (alleles[0].getWeight() >= alleles[1].getWeight() ?
				    alleles[0] : alleles[1]);
	}
	
	/**
	 * Instantiate a Gene with provided Allele array.
	 * 
	 * @param Allele allele1 First allele.
	 * @param Allele allele2 Second allele.
	 */
	public Gene(Allele[] alleles) {
		this.alleles[0] = alleles[0];
		this.alleles[1] = alleles[1];
		dominant = (alleles[0].getWeight() >= alleles[1].getWeight() ?
				    alleles[0] : alleles[1]);
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
	 * Override of equals.
	 * 
	 * @param other Gene to compare to.
	 * @return True if Alleles' traits and values match; false otherwise.
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof Gene) {
			Allele[] otherAlleles = ((Gene) other).getAlleles();
			
			
			return alleles[0].getTrait() == otherAlleles[0].getTrait() &&
				   alleles[0].getValue() == otherAlleles[0].getValue() &&
				   alleles[1].getTrait() == otherAlleles[1].getTrait() &&
				   alleles[1].getValue() == otherAlleles[1].getValue();
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
