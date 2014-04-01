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
import java.util.Random;

import creature.geeksquad.genetics.Allele.Trait;
import creature.geeksquad.library.Helper;

/**
 * A Crossover class for performing Genotype crossover.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
public class Crossover {
	private static Random rand = Helper.RANDOM;
	
	/**
	 * Perform crossover on two parents based on a provided strategy.
	 * 
	 * @param Genotype parentA Genotype from parent A.
	 * @param Genotype parentB Genotype from parent B.
	 * @param Strategy strategy Strategy to use for crossover.
	 * @return Two-element array of Genotypes for children. If there were
	 *         problems creating any of the genes (e.g. if the alleles didn't
	 *         trait match properly), returns null.
	 * @throws IllegalArgumentException from Genotype instantiation.
	 * @throws GeneticsException from Genotype instantiation.
	 */
	public static Genotype[] crossover(Genotype parentA, Genotype parentB,
			Strategy strategy) throws IllegalArgumentException,
			GeneticsException {
		Genotype[] children = null;
		
		try {
			switch (strategy) {
				case SINGLE_POINT:
					children = singlePoint(parentA, parentB);
					break;
				case DOUBLE_POINT:
					children = doublePoint(parentA, parentB);
					break;
				case CUT_AND_SPLICE:
					children = cutAndSplice(parentA, parentB);
					break;
				case RANDOM:
					children = randomCross(parentA, parentB);
					break;
				default:
					// Fall through.
			}
		} catch (IllegalArgumentException | GeneticsException ex) {
			throw ex;
		}
		
		return children;
	}
	
	/**
	 * Perform single-point crossover on two parents to create twin children.
	 * 
	 * @param Genotype parentA Genotype from parent A.
	 * @param Genotype parentB Genotype from parent B.
	 * @return Two-element array of Genotypes for children. If there were
	 *         problems creating any of the genes (e.g. if the alleles didn't
	 *         trait match properly), returns null.
	 * @throws IllegalArgumentException from Genotype instantiation.
	 * @throws GeneticsException from Genotype instantiation.
	 */
	private static Genotype[] singlePoint(Genotype parentA, Genotype parentB)
				throws IllegalArgumentException, GeneticsException {
		ArrayList<Gene> chromosomeA = parentA.getChromosome();
		ArrayList<Gene> chromosomeB = parentB.getChromosome();
		// Get the size of the larger chromosome.
		int sizeA = chromosomeA.size();
		int sizeB = chromosomeB.size();
		int size = (sizeA >= sizeB ? sizeA : sizeB);
		// Create the chromosomes for the twin children.
		ArrayList<Gene> childA = new ArrayList<Gene>();
		ArrayList<Gene> childB = new ArrayList<Gene>();
		
		// Align the key genes.
		ArrayList<ArrayList<Gene>> newChromosomes = align(chromosomeA,
				chromosomeB);
		
		// Randomly choose the starting parent.
		if (rand.nextInt(2) > 0) {
			chromosomeA = newChromosomes.get(0);
			chromosomeB = newChromosomes.get(1);			
		} else {
			chromosomeA = newChromosomes.get(1);
			chromosomeB = newChromosomes.get(0);
		}
		
		// Randomly choose the transition point.
		int transition = rand.nextInt(size);
		
		// Iterate over the lists and pick a random allele from each parent.
		for (int i = 0; i < size; i++) {
			Gene parentGeneA = new Gene(chromosomeA.get(i));
			Gene parentGeneB = new Gene(chromosomeB.get(i));
			Gene childGeneA;
			Gene childGeneB;

			// Create deep clones of the genes for the children.
			try {
				if (i < transition) {
					childGeneA = new Gene(parentGeneA);
					childGeneB = new Gene(parentGeneB);
				} else {
					childGeneA = new Gene(parentGeneB);
					childGeneB = new Gene(parentGeneA);
				}
			
				childA.add(childGeneA);
				childB.add(childGeneB);
			// If there were problems creating any of the Genes, return null.
			} catch (IllegalArgumentException ex) {
				return null;
			}
		}
		
		// If the child Gene pulled a matched pair of empty Alleles, trim it
		// from the final strand.
		trimEmpty(childA);
		trimEmpty(childB);

		try {
			Genotype[] children = {new Genotype(childA), new Genotype(childB)};
			return children;
		} catch (IllegalArgumentException | GeneticsException ex) {
			throw ex;
		}
	}
	
	/**
	 * Perform double-point crossover on two parents to create twin children.
	 * 
	 * @param Genotype parentA Genotype from parent A.
	 * @param Genotype parentB Genotype from parent B.
	 * @return Two-element array of Genotypes for children. If there were
	 *         problems creating any of the genes (e.g. if the alleles didn't
	 *         trait match properly), returns null.
	 * @throws IllegalArgumentException from Genotype instantiation.
	 * @throws GeneticsException from Genotype instantiation.
	 */
	private static Genotype[] doublePoint(Genotype parentA, Genotype parentB)
			throws IllegalArgumentException, GeneticsException {
		ArrayList<Gene> chromosomeA = parentA.getChromosome();
		ArrayList<Gene> chromosomeB = parentB.getChromosome();
		// Get the size of the larger chromosome.
		int sizeA = chromosomeA.size();
		int sizeB = chromosomeB.size();
		int size = (sizeA >= sizeB ? sizeA : sizeB);
		// Create the chromosomes for the twin children.
		ArrayList<Gene> childA = new ArrayList<Gene>();
		ArrayList<Gene> childB = new ArrayList<Gene>();
		
		// Align the key genes.
		ArrayList<ArrayList<Gene>> newChromosomes = align(chromosomeA,
				chromosomeB);
		
		// Randomly choose the starting parent.
		if (rand.nextInt(2) > 0) {
			chromosomeA = newChromosomes.get(0);
			chromosomeB = newChromosomes.get(1);			
		} else {
			chromosomeA = newChromosomes.get(1);
			chromosomeB = newChromosomes.get(0);
		}
		
		// Randomly choose the transition points.
		int transition1 = rand.nextInt(size);
		int transition2 = transition1 + rand.nextInt(size - transition1);
		
		// Iterate over the lists and pick a random allele from each parent.
		for (int i = 0; i < size; i++) {
			Gene parentGeneA = new Gene(chromosomeA.get(i));
			Gene parentGeneB = new Gene(chromosomeB.get(i));
			Gene childGeneA;
			Gene childGeneB;

			// Create deep clones of the genes for the children.
			try {
				if (i < transition1 || i >= transition2) {
					childGeneA = new Gene(parentGeneA);
					childGeneB = new Gene(parentGeneB);
				} else {
					childGeneA = new Gene(parentGeneB);
					childGeneB = new Gene(parentGeneA);
				}
			
				childA.add(childGeneA);
				childB.add(childGeneB);
			// If there were problems creating any of the Genes, return null.
			} catch (IllegalArgumentException ex) {
				return null;
			}
		}
		
		// If the child Gene pulled a matched pair of empty Alleles, trim it
		// from the final strand.
		trimEmpty(childA);
		trimEmpty(childB);

		try {
			Genotype[] children = {new Genotype(childA), new Genotype(childB)};
			return children;
		} catch (IllegalArgumentException | GeneticsException ex) {
			throw ex;
		}
	}
	
	/**
	 * Perform cut-and-splice crossover on two parents to create twin children.
	 * 
	 * @param Genotype parentA Genotype from parent A.
	 * @param Genotype parentB Genotype from parent B.
	 * @return Two-element array of Genotypes for children. If there were
	 *         problems creating any of the genes (e.g. if the alleles didn't
	 *         trait match properly), returns null.
	 * @throws IllegalArgumentException from Genotype instantiation.
	 * @throws GeneticsException from Genotype instantiation.
	 */
	private static Genotype[] cutAndSplice(Genotype parentA, Genotype parentB)
			throws IllegalArgumentException, GeneticsException {
		ArrayList<Gene> chromosomeA = parentA.getChromosome();
		ArrayList<Gene> chromosomeB = parentB.getChromosome();
		// Get the size of the larger chromosome.
		int sizeA = chromosomeA.size();
		int sizeB = chromosomeB.size();
		int size = (sizeA >= sizeB ? sizeA : sizeB);
		// Create the chromosomes for the twin children.
		ArrayList<Gene> childA = new ArrayList<Gene>();
		ArrayList<Gene> childB = new ArrayList<Gene>();
		
		// Align the key genes.
		ArrayList<ArrayList<Gene>> newChromosomes = align(chromosomeA,
				chromosomeB);
		
		// Randomly choose the starting parent.
		if (rand.nextInt(2) > 0) {
			chromosomeA = newChromosomes.get(0);
			chromosomeB = newChromosomes.get(1);			
		} else {
			chromosomeA = newChromosomes.get(1);
			chromosomeB = newChromosomes.get(0);
		}
		
		// Iterate over the lists and pick a random allele from each parent.
		for (int i = 0; i < size; i++) {
			Gene parentGeneA = new Gene(chromosomeA.get(i));
			Gene parentGeneB = new Gene(chromosomeB.get(i));
			Gene childGeneA;
			Gene childGeneB;

			// Create deep clones of the genes for the children.
			try {
				if (rand.nextInt(4) > 2) {
					childGeneA = new Gene(parentGeneA);
					childGeneB = new Gene(parentGeneB);
				} else {
					childGeneA = new Gene(parentGeneB);
					childGeneB = new Gene(parentGeneA);
				}
			
				childA.add(childGeneA);
				childB.add(childGeneB);
			// If there were problems creating any of the Genes, return null.
			} catch (IllegalArgumentException ex) {
				return null;
			}
		}
		
		// If the child Gene pulled a matched pair of empty Alleles, trim it
		// from the final strand.
		trimEmpty(childA);
		trimEmpty(childB);

		try {
			Genotype[] children = {new Genotype(childA), new Genotype(childB)};
			return children;
		} catch (IllegalArgumentException | GeneticsException ex) {
			throw ex;
		}
	}
	
	/**
	 * Perform 50/50 random crossover on two parents to create twin children.
	 * Unlike the other crossover methods, which grab whole Genes from one
	 * parent or the other, 50/50 random crossover grabs individual Alleles
	 * and combines them into new Genes.
	 * 
	 * @param Genotype parentA Genotype from parent A.
	 * @param Genotype parentB Genotype from parent B.
	 * @return Two-element array of Genotypes for children. If there were
	 *         problems creating any of the genes (e.g. if the alleles didn't
	 *         trait match properly), returns null.
	 * @throws IllegalArgumentException from Genotype instantiation.
	 * @throws GeneticsException from Genotype instantiation.
	 */
	private static Genotype[] randomCross(Genotype parentA, Genotype parentB)
			throws IllegalArgumentException, GeneticsException {
		ArrayList<Gene> chromosomeA = parentA.getChromosome();
		ArrayList<Gene> chromosomeB = parentB.getChromosome();
		// Get the size of the larger chromosome.
		int sizeA = chromosomeA.size();
		int sizeB = chromosomeB.size();
		int size = (sizeA >= sizeB ? sizeA : sizeB);
		// Create the chromosomes for the twin children.
		ArrayList<Gene> childA = new ArrayList<Gene>();
		ArrayList<Gene> childB = new ArrayList<Gene>();
		// Align the key genes.
		ArrayList<ArrayList<Gene>> newChromosomes = align(chromosomeA,
				chromosomeB);
		chromosomeA = newChromosomes.get(0);
		chromosomeB = newChromosomes.get(1);
		
		if (chromosomeA == null || chromosomeB == null) {
			return null;
		}
		
		// Iterate over the lists and pick a random allele from each parent.
		for (int i = 0; i < size; i++) {
			Gene parentGeneA = new Gene(chromosomeA.get(i));
			Gene parentGeneB = new Gene(chromosomeB.get(i));
			int a1 = rand.nextInt(2);
			int b1 = rand.nextInt(2);
			int a2 = (a1 == 1 ? 0 : 1);
			int b2 = (b1 == 1 ? 0 : 1);
			// Create deep clones of the genes for the children.
			try {
				Gene childGeneA = new Gene(parentGeneA.getAlleles()[a1],
						                   parentGeneB.getAlleles()[b1]);
				Gene childGeneB = new Gene(parentGeneA.getAlleles()[a2],
						                   parentGeneB.getAlleles()[b2]);
				childA.add(childGeneA);
				childB.add(childGeneB);
			// If there were problems creating any of the Genes, return null.
			} catch (IllegalArgumentException ex) {
				ex.printStackTrace();
				return null;
			}
		}
		// If the child Gene pulled a matched pair of empty Alleles, it will
		// be trimmed when instantiating the new Genotypes.
		try {
			Genotype[] children = {new Genotype(childA), new Genotype(childB)};
			return children;
		} catch (IllegalArgumentException | GeneticsException ex) {
			throw ex;
		}
	}
	
	/**
	 * Helper method for Crossover that matches the locations of the key genes
	 * on two strands.
	 * 
	 * @param strandA ArrayList<Gene> of first strand.
	 * @param strandB ArrayList<Gene> of second strand.
	 * @return ArrayList<ArrayList<Gene>> containing key-gene-matched copies of
	 *         the strands.
	 */
	public static ArrayList<ArrayList<Gene>> align(ArrayList<Gene> strandA,
								 ArrayList<Gene> strandB) {
		ArrayList<ArrayList<Gene>> strands =
					new ArrayList<ArrayList<Gene>>();
		strands.add(new ArrayList<Gene>());
		strands.add(new ArrayList<Gene>());
		
		ArrayList<Gene> bigger;
		ArrayList<Gene> smaller;
		
		int sizeA = strandA.size();
		int sizeB = strandB.size();
		int bigSize;
		
		if (sizeA >= sizeB) {
			bigger = strandA;
			smaller = strandB;
			bigSize = sizeA;
		} else {
			bigger = strandB;
			smaller = strandA;
			bigSize = sizeB;
		}
		
		// For smaller, we need to clone each section individually and pad with
		// empty genes where the strands don't line up.
		int sI = 0;
		for (int bI = 0; bI < bigSize; bI++) {
			ArrayList<Gene> strand0 = strands.get(0);
			ArrayList<Gene> strand1 = strands.get(1);
			Gene gB = bigger.get(bI);
			Gene gS = smaller.get(sI);
			Trait tB = gB.getTrait();
			Trait tS = gS.getTrait();
			
			// Clone the GEne from bigger.
			strand0.add(new Gene(gB));
			
			// If the traits are the same at the two indices, add the gene from
			// smaller.
			if (tB.equals(tS)) {
				strand1.add(new Gene(gS));
				sI++;
			} else {
				strand1.add(new Gene());
			}
		}
		
		return strands;
	}
	
	/**
	 * Trim empty Genes from the chromosome.
	 * 
	 * @param chromosome ArrayList<Gene> to trim.
	 */
	public static void trimEmpty(ArrayList<Gene> chromosome) {
		for (Iterator<Gene> it = chromosome.iterator(); it.hasNext(); ) {
			if (it.next().isEmpty()) {
				it.remove();
			}
		}
	}
	
	/**
	 * A nested enum representing the strategy of Crossover to use.
	 */
	public enum Strategy {
		SINGLE_POINT, DOUBLE_POINT, CUT_AND_SPLICE, RANDOM;
	}

}
