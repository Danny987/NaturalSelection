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
import java.util.HashMap;
import java.util.Random;

import creature.phenotype.*;

/**
 * A Genotype class for the creatures.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
public class Genotype implements Cloneable {
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
	private ArrayList<Gene> chromosome;
	private static Random random = new Random();

	/**
	 * Instantiate a new Genotype from a passed chromosome list.
	 * 
	 * @param chromosome ArrayList<Gene> containing the chromosomes for this
	 * 					 Genotype.
	 */
	public Genotype(ArrayList<Gene> chromosome) {
		this.chromosome = chromosome;
	}

	/**
	 * Perform crossover on two parents to create twin children.
	 * 
	 * @param Genotype parentA Genotype from parent A.
	 * @param Genotype parentB Genotype from parent B.
	 * @return Two-element array of Genotypes for children.
	 */
	public static Genotype[] crossover(Genotype parentA, Genotype parentB) {
		ArrayList<Gene> chromosomeA = parentA.getChromosome();
		ArrayList<Gene> chromosomeB = parentB.getChromosome();
		// Get the size of the larger chromosome.
		int sizeA = chromosomeA.size();
		int sizeB = chromosomeB.size();
		int size = (sizeA >= sizeB ? sizeA : sizeB);
		// Create the chromosomes for the twin children.
		ArrayList<Gene> childA = new ArrayList<Gene>(size);
		ArrayList<Gene> childB = new ArrayList<Gene>(size);
		
		// If the sizes differ, we need to pad out the smaller one before
		// performing crossover. We do this by lining up the "L" Genes in
		// each chromosome as far as we can and then padding with empty Genes.
		if (sizeA != sizeB) {
			int smallSize = (sizeA > sizeB ? sizeB : sizeA);
			ArrayList<Gene> chromosomePadded = new ArrayList<Gene>();
			ArrayList<Gene> bigger = (sizeA == smallSize ?
									  chromosomeB : chromosomeA);
			ArrayList<Gene> smaller = (bigger.equals(chromosomeA) ?
									   chromosomeB : chromosomeA);
			// If the smaller strand reaches an "L", denoting the start of a
			// new Block, before the bigger stand, or if the smaller strand
			// reaches its end first, pad.
			int index = 0;
			for (Gene bigGene : bigger) {
				Gene smallGene = smaller.get(index);
				if ((smallGene.getTrait().equals("L") &&
						!bigGene.getTrait().equals("L")) ||
						index >= smallSize) {
					chromosomePadded.add(new Gene());
				} else {
					chromosomePadded.add(bigGene);
					index++;
				}
			}
			// Lastly, replace the pointer to the smaller of the chromosomes
			// with the new, padded version.
			if (smaller.equals(chromosomeA)) {
				chromosomeA = chromosomePadded;
			} else {
				chromosomeB = chromosomePadded;
			}
		}

		// Iterate over the list and pick a random allele from each parent.
		for (int i = 0; i < size; i++) {
			Gene parentGeneA = chromosomeA.get(i);
			Gene parentGeneB = chromosomeB.get(i);
			int a1 = random.nextInt(2);
			int b1 = random.nextInt(2);
			int a2 = (a1 == 1 ? 0 : 1);
			int b2 = (b1 == 1 ? 0 : 1);
			// Create the genes for the children.
			Gene childGeneA = new Gene(parentGeneA.getAlleles()[a1],
					                   parentGeneB.getAlleles()[b1]);
			Gene childGeneB = new Gene(parentGeneA.getAlleles()[a2],
					                   parentGeneB.getAlleles()[b2]);
			childA.set(i, childGeneA);
			childB.set(i, childGeneB);
		}

		Genotype[] children = {new Genotype(childA), new Genotype(childB)};

		return children;
	}

	/**
	 * Evaluates the Genotype to create the Creature (phenotype).
	 * 
	 * @return Creature (phenotype) of the Genotype.
	 */
	public Creature getPhenotype() {
		Block[] body = getBody();
		Vector3 rootForwardStart = Vector3.FORWARD;
		Vector3 rootUpStart = Vector3.UP;
		// Return a new Creature (phenotype) with the calculated values.
		return new Creature(body, rootForwardStart, rootUpStart);
	}

	/**
	 * Gets the Block array representing this Genotype's body.
	 * 
	 * @return Block array representing this Genotype's body.
	 */
	public Block[] getBody() {
		ArrayList<Block> body = new ArrayList<Block>();
		// Number of blocks added so far.
		int count = 0;
		// Set once we find the root Block (where indexOfParent is null).
		boolean rootFound = false;
		// Map of trait (String) to value (float) used for building the blocks.
		HashMap<String, Float> blocklets = new HashMap<String, Float>();
		Joint jointToParent = null;
		int indexOfParent = Block.PARENT_INDEX_NONE;

		// Iterate over the list and grab the metadata.
		for (Gene gene : chromosome) {
			String trait = gene.getTrait();
			float value = gene.getValue();

			// Trait 'L' is a special case: it marks the beginning of the
			// encoding for a new Block, so close construction of the previous
			// block and add it to the list.
			if (trait == "L") {
				if (count > 0) {
					body.add(new Block(indexOfParent, jointToParent,
							 blocklets.get("L"), blocklets.get("W"),
							 blocklets.get("H")));
				}
				count++;
			// If the trait is 'I', it marks the indexOfParent. Since the body
			// must have exactly one root, whose indexOfParent is null, we need
			// to check if rootFound has already been set.
			} else if (trait == "I") {
				if (rootFound) {
					// If multiple roots, the Genotype is invalid. Return null.
					return null;
				} else {
					rootFound = true;
				}
			}
			
			blocklets.put(trait, value);
		}
		// Add the final, unclosed Block to the list.
		body.add(new Block(indexOfParent, jointToParent, blocklets.get('L'),
				 blocklets.get('W'), blocklets.get('H')));

		return (Block[]) body.toArray();
	}

	/**
	 * Getter for chromosome.
	 * 
	 * @return ArrayList<Gene> containing this Genotype's chromosome list.
	 */
	public ArrayList<Gene> getChromosome() {
		return chromosome;
	}

	/**
	 * Override of toString. Formats the returned String as the genes list
	 * enclosed in square brackets.
	 * 
	 * @return String containing genes list enclosed in curly braces:
	 *         {([alleleA1][alleleA2])([alleleB1][alleleB2])...}.
	 */
	@Override
	public String toString() {
		StringBuilder gString = new StringBuilder("");
		gString.append('{');
		for (Gene g : chromosome) {
			gString.append(g.toString());
		}
		gString.append('}');

		return gString.toString();
	}

	/**
	 * Override of clone. Creates a clone of this Genotype.
	 * 
	 * @return Gene-level clone of this Genotype.
	 */
	@Override
	public Object clone() {
		return new ArrayList<Gene>(chromosome);
	}

}
