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
 * A Gene class for the creatures.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
public class Gene implements Cloneable {
	private Allele[] alleles = new Allele[2];
	private short expressed;
	
	/**
	 * Instantiate an empty Gene.
	 */
	public Gene() {
		// TODO
		alleles[0] = null;
		alleles[1] = null;
	}
	
	/**
	 * Instantiate a Gene with provided Alleles.
	 * 
	 * @param Allele allele1
	 * @param Allele allele2
	 */
	public Gene(Allele allele1, Allele allele2) {
		alleles[0] = allele1;
		alleles[1] = allele2;
	}
	
	/**
	 * Override of equals.
	 * 
	 * @param other Allele to compare to.
	 * @return True if same Gene; false otherwise.
	 */
	@Override
	public boolean equals(Object other) {
		// TODO
		return false;
	}
	
	/**
	 * Override of hashCode.
	 * 
	 * @return hashCode of this Gene.
	 */
	@Override
	public int hashCode() {
		// TODO
		return 0;
	}
	
	/**
	 * Override of clone.
	 * 
	 * @return Deep clone of this Gene.
	 */
	@Override
	public Object clone() {
		// TODO
		return null;
	}
	
}
