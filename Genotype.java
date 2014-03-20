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
	private ArrayList<Gene> chromosome;
	private static Random random = new Random();
	
	/**
	 * Instantiate a new Genotype from a passed chromosome list.
	 * 
	 * @param chromosome ArrayList<Gene> containing the chromosomes for this
	 *                   Genotype.
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
		
		// Iterate over the list and pick a random allele from each parent.
		if (sizeA == sizeB) {
			for (int i = 0; i < size; i++) {
				Gene parentGeneA  = chromosomeA.get(i);
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
		} else {
			// TODO
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
		// TODO
		Block[] body = null;
		Vector3 rootForwardStart = null;
		Vector3 rootUpStart = null;
		// Return a new Creature (phenotype) with the calculated values.
		return new Creature(body, rootForwardStart, rootUpStart);
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
	 * Override of clone. Creates a deep clone of this Genotype.
	 * 
	 * @return Deep clone of this Genotype.
	 */
	@Override
	public Object clone() {
		// TODO
		return null;
	}
}
