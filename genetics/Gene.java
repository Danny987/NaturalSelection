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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import creature.geeksquad.genetics.Allele.Trait;
import creature.phenotype.Block;
import creature.phenotype.EnumJointType;

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
	 * Instantiate a new Gene as a deep clone of a source Gene.
	 * 
	 * @param source Gene to clone.
	 */
	public Gene(Gene source) {
		this(source.getAlleles());
	}
	
	/**
	 * Instantiate a new Gene with two copies of the passed Allele.
	 * 
	 * @param source Allele to clone.
	 */
	public Gene(Allele source) {
		this(source, source);
	}

	/**
	 * Instantiate a Gene with provided Alleles. The traits of the two Alleles
	 * should match (or one can be empty).
	 * 
	 * @param Allele alleleA First allele - trait must match second allele.
	 * @param Allele alleleB Second allele - trait must match first allele.
	 * @throws IllegalArgumentException if allele traits don't match.
	 */
	public Gene(Allele alleleA, Allele alleleB) throws
				IllegalArgumentException {
		if (alleleA == null || alleleB == null) {
			throw new IllegalArgumentException(
					"Gene constructor Alleles cannot be null");
		}
		Trait traitA = alleleA.getTrait();
		Trait traitB = alleleB.getTrait();
		// The traits of the Genes should never be mismatched. Check if the
		// Alleles' traits differ and if both Alleles are nonempty.
		if (!traitA.equals(traitB)) {
			if (traitA.equals(Trait.DOF_MARKER) && traitB.equals(Trait.EMPTY)) {
				alleles[0] = new Allele(alleleA);
				alleles[1] = new Allele(alleleA);
				dominant = alleles[0];
				trait = dominant.getTrait();
				value = dominant.getValue();
			} else if (traitB.equals(Trait.DOF_MARKER) &&
					traitA.equals(Trait.EMPTY)) {
				alleles[0] = new Allele(alleleB);
				alleles[1] = new Allele(alleleB);
				dominant = alleles[0];
				trait = dominant.getTrait();
				value = dominant.getValue();
			} else if (!alleleA.isEmpty() && !alleleB.isEmpty()) {
				throw new IllegalArgumentException(
						"Cannot pair Allele of Trait." + alleleA.getTrait()
								+ " with Allele of Trait." + alleleB.getTrait()
								+ ".");
			}
		} else {
			// Clone the Alleles.
			alleles[0] = new Allele(alleleA);
			alleles[1] = new Allele(alleleB);
			dominant = (alleles[0].getWeight() >= alleles[1].getWeight()
					? alleles[0] : alleles[1]);
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
	 * Check if the Gene is empty (if both Alleles are empty).
	 * 
	 * @return True if both Alleles are empty; false otherwise.
	 */
	public boolean isEmpty() {
		// Short-circuits if alleles.length != 2, preventing null pointer.
		if (alleles.length == 2 && alleles[0] != null && alleles[1] != null) {
			return alleles[0].isEmpty() && alleles[1].isEmpty();
		} else {
			return true;
		}
	}

	/**
	 * Getter for alleles.
	 * 
	 * @return Two-element Allele array (both null if traits are mismatched).
	 */
	public Allele[] getAlleles() {
		return alleles;
	}

	/**
	 * Getter for dominant allele.
	 * 
	 * @return The dominant Allele being expressed, with the highest weight
	 *         (null if traits of Alleles are mismatched).
	 */
	public Allele getDominant() {
		return dominant;
	}

	/**
	 * Getter for this Gene's trait. The traits of the two Alleles should always
	 * be the same. If they're mismatched on instantiation, the trait gets set
	 * to null.
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
	 * Creates an ArrayList of Genes from a passed List of Alleles. Doesn't
	 * check for trait matching, so if Allele pairs don't match, the Gene
	 * constructor will throw an exception. If the list contains an odd number
	 * of Alleles, the final element is ignored.
	 * 
	 * @param alleles List of Alleles. Allele pairs must be trait matched.
	 * @return ArrayList<Gene> containing the generated Genes. Returns null if
	 *         there was problem trait matching any of the Genes.
	 * @throws IllegalArgumentException if there was a problem converting.
	 */
	public static ArrayList<Gene> allelesToGenes(List<Allele> alleles)
					throws IllegalArgumentException{
		ArrayList<Gene> genes = new ArrayList<Gene>();

		try {
			for (Iterator<Allele> it = alleles.iterator(); it.hasNext();) {
				Allele allele = it.next();
				if (it.hasNext()) {
					genes.add(new Gene(allele, it.next()));
				}
			}
		} catch (IllegalArgumentException ex) {
			throw ex;
		}

		return genes;
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
	 * Check if either or both of this Gene's Alleles are null.
	 * 
	 * @return True if either or both of this Gene's Alleles are null, false
	 *             otherwise.
	 */
	public boolean isNull() {
		return alleles[0] == null || alleles[1] == null;
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
		return new String("[" + alleles[0] + alleles[1]	+ "]");
	}

	/**
	 * Main method for testing purposes.
	 * 
	 * @param args
	 *            Command-line arguments.
	 */
	public static void main(String[] args) {
		java.util.ArrayList<Allele> alleles = new java.util.ArrayList<Allele>();
		// Adding some dummy Alleles to the list.
		alleles.add(new Allele(Trait.HEIGHT, 42.5f, 0.5f));
		alleles.add(new Allele(Trait.HEIGHT, 20.5f, 0.35f));
		alleles.add(new Allele(Trait.INDEX_TO_PARENT, Block.PARENT_INDEX_NONE,
				0.4f));
		alleles.add(new Allele(Trait.INDEX_TO_PARENT, 5, 0.63f));
		alleles.add(new Allele(Trait.JOINT_TYPE, EnumJointType.RIGID, 0.3f));
		alleles.add(new Allele(Trait.JOINT_TYPE, EnumJointType.HINGE, 0.2f));

		// Build some Genes from the Alleles.
		for (int i = 0; i < alleles.size(); i++) {
			Gene gene = new Gene(alleles.get(i), alleles.get(++i));
			System.out.println("Gene " + gene + " --> (Dominant) "
					+ gene.getDominant());
		}

		// Test to make sure mismatched trait exception handling is working.
		try {
			@SuppressWarnings("unused")
			Gene badGene = new Gene(alleles.get(0), alleles.get(2));
		} catch (IllegalArgumentException ex) {
			System.out.println();
			System.out.println("***** THIS EXCEPTION MEANS IT'S WORKING *****");
			ex.printStackTrace();
		}
	}

}
