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
	 * Perform crossover on two parents based on a provided strategy. Since the
	 * positions of blocks don't matter and we need the key genes to line up,
	 * first align the strands and shift the root block to the beginning.
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
		@SuppressWarnings("unchecked")
		ArrayList<Gene>[] children = new ArrayList[2];
		
		ArrayList<Gene> chromosomeA = parentA.getChromosome();
		ArrayList<Gene> chromosomeB = parentB.getChromosome();
		
		// Align the key genes.
		ArrayList<Gene>[] newChromosomes = align(chromosomeA, chromosomeB);
		
		// Randomly choose the starting parent.
		if (rand.nextInt(2) > 0) {
			chromosomeA = newChromosomes[0];
			chromosomeB = newChromosomes[1];			
		} else {
			chromosomeA = newChromosomes[1];
			chromosomeB = newChromosomes[0];
		}
		
		try {
			switch (strategy) {
				case SINGLE_POINT:
					children = singlePoint(chromosomeA, chromosomeB);
					break;
				case DOUBLE_POINT:
					children = doublePoint(chromosomeA, chromosomeB);
					break;
				case CUT_AND_SPLICE:
					children = cutAndSplice(chromosomeA, chromosomeB);
					break;
				case RANDOM:
					children = randomCross(chromosomeA, chromosomeB);
					break;
				default:
					// Fall through.
			}
		} catch (IllegalArgumentException | GeneticsException ex) {
			throw ex;
		}
		
		Genotype genome1 = null;
		Genotype genome2 = null;
		
		try {
			genome1 = new Genotype(children[0]);
		} catch (IllegalArgumentException | GeneticsException ex) {
			ex.printStackTrace();
		}
		try {
			genome2 = new Genotype(children[1]);
		} catch (IllegalArgumentException | GeneticsException ex) {
			ex.printStackTrace();
		}
		
		Genotype[] genomes = {genome1, genome2};
		
		return genomes;
	}
	
	/**
	 * Perform single-point crossover on two parents to create twin children.
	 * 
	 * @param chromosomeA ArrayList<Gene> from parent A.
	 * @param chromosomeB ArrayList<Gene> from parent B.
	 * @return Two-element ArrayList<Gene> array for children. If there were
	 *         problems creating any of the genes (e.g. if the alleles didn't
	 *         trait match properly), returns null.
	 * @throws IllegalArgumentException from Genotype instantiation.
	 * @throws GeneticsException from Genotype instantiation.
	 */
	@SuppressWarnings("unchecked")
	private static ArrayList<Gene>[] singlePoint(ArrayList<Gene> chromosomeA,
			ArrayList<Gene> chromosomeB) throws IllegalArgumentException,
			GeneticsException {
		
		int size = chromosomeA.size();
		// Create the chromosomes for the twin children.
		ArrayList<Gene> childA = new ArrayList<Gene>();
		ArrayList<Gene> childB = new ArrayList<Gene>();
		
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

		@SuppressWarnings("rawtypes")
		ArrayList[] children = {childA, childB};
		return children;
	}
	
	/**
	 * Perform double-point crossover on two parents to create twin children.
	 * 
	 * @param chromosomeA ArrayList<Gene> from parent A.
	 * @param chromosomeB ArrayList<Gene> from parent B.
	 * @return Two-element ArrayList<Gene> array for children. If there were
	 *         problems creating any of the genes (e.g. if the alleles didn't
	 *         trait match properly), returns null.
	 * @throws IllegalArgumentException from Genotype instantiation.
	 * @throws GeneticsException from Genotype instantiation.
	 */
	@SuppressWarnings("unchecked")
	private static ArrayList<Gene>[] doublePoint(ArrayList<Gene> chromosomeA,
			ArrayList<Gene> chromosomeB) throws IllegalArgumentException,
			GeneticsException {
		int size = chromosomeA.size();
		// Create the chromosomes for the twin children.
		ArrayList<Gene> childA = new ArrayList<Gene>();
		ArrayList<Gene> childB = new ArrayList<Gene>();
		
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

		@SuppressWarnings("rawtypes")
		ArrayList[] children = {childA, childB};
		return children;
	}
	
	/**
	 * Perform cut-and-splice crossover on two parents to create twin children.
	 * 
	 * @param chromosomeA ArrayList<Gene> from parent A.
	 * @param chromosomeB ArrayList<Gene> from parent B.
	 * @return Two-element ArrayList<Gene> array for children. If there were
	 *         problems creating any of the genes (e.g. if the alleles didn't
	 *         trait match properly), returns null.
	 * @throws IllegalArgumentException from Genotype instantiation.
	 * @throws GeneticsException from Genotype instantiation.
	 */
	@SuppressWarnings("unchecked")
	private static ArrayList<Gene>[] cutAndSplice(ArrayList<Gene> chromosomeA,
			ArrayList<Gene> chromosomeB) throws IllegalArgumentException,
			GeneticsException {
		int size = chromosomeA.size();
		// Create the chromosomes for the twin children.
		ArrayList<Gene> childA = new ArrayList<Gene>();
		ArrayList<Gene> childB = new ArrayList<Gene>();
		
		// Align the key genes.
		ArrayList<Gene>[] newChromosomes = align(chromosomeA, chromosomeB);
		
		// Randomly choose the starting parent.
		if (rand.nextInt(2) > 0) {
			chromosomeA = newChromosomes[0];
			chromosomeB = newChromosomes[1];			
		} else {
			chromosomeA = newChromosomes[1];
			chromosomeB = newChromosomes[0];
		}
		
		// Iterate over the lists and pick a random allele from each parent.
		for (int i = 0; i <= size; i++) {
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
		@SuppressWarnings("rawtypes")
		ArrayList[] children = {childA, childB};
		return children;
	}
	
	/**
	 * Perform 50/50 random crossover on two parents to create twin children.
	 * Unlike the other crossover methods, which grab whole Genes from one
	 * parent or the other, 50/50 random crossover grabs individual Alleles
	 * and combines them into new Genes.
	 * 
	 * @param chromosomeA ArrayList<Gene> from parent A.
	 * @param chromosomeB ArrayList<Gene> from parent B.
	 * @return Two-element ArrayList<Gene> array for children. If there were
	 *         problems creating any of the genes (e.g. if the alleles didn't
	 *         trait match properly), returns null.
	 * @throws IllegalArgumentException from Genotype instantiation.
	 * @throws GeneticsException from Genotype instantiation.
	 */
	@SuppressWarnings("unchecked")
	private static ArrayList<Gene>[] randomCross(ArrayList<Gene> chromosomeA,
			ArrayList<Gene> chromosomeB) throws IllegalArgumentException,
			GeneticsException {
		int size = chromosomeA.size();
		// Create the chromosomes for the twin children.
		ArrayList<Gene> childA = new ArrayList<Gene>();
		ArrayList<Gene> childB = new ArrayList<Gene>();

		// Align the key genes.
		ArrayList<Gene>[] newChromosomes = align(chromosomeA, chromosomeB);
		chromosomeA = newChromosomes[0];
		chromosomeB = newChromosomes[1];
		
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
		@SuppressWarnings("rawtypes")
		ArrayList[] children = {childA, childB};
		
		return children;
	}
	
	/**
	 * Helper method for Crossover that matches the locations of the key genes
	 * on two strands.
	 * 
	 * @param strandA ArrayList<Gene> of first strand.
	 * @param strandB ArrayList<Gene> of second strand.
	 * @return ArrayList<Gene>[] containing key-gene-matched copies of the
	 *         strands.
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Gene>[] align(ArrayList<Gene> strandA,
								 ArrayList<Gene> strandB) {
		ArrayList<Gene>[] strands =	new ArrayList[2];
		strands[0] = new ArrayList<Gene>();
		strands[1] = new ArrayList<Gene>();
		
		ArrayList<Gene> bigger;
		ArrayList<Gene> smaller;
		
		int sizeA = strandA.size();
		int sizeB = strandB.size();
		int bigSize, smallSize;
		
		if (sizeA >= sizeB) {
			bigger = strandA;
			smaller = strandB;
			bigSize = sizeA;
			smallSize = sizeB;
		} else {
			bigger = strandB;
			smaller = strandA;
			bigSize = sizeB;
			smallSize = sizeA;
		}
		
		// For smaller, we need to clone each section individually and pad with
		// empty genes where the strands don't line up.
		int sI = 0;
		for (int bI = 0; bI < bigSize; bI++) {
			Gene gB = bigger.get(bI);
			Gene gS;
			if (sI < smallSize) {
				gS = smaller.get(sI);
			} else {
				gS = new Gene();
			}
			Trait tB = gB.getTrait();
			Trait tS = gS.getTrait();
			
			// Clone the Gene from bigger.
			strands[0].add(new Gene(gB));
			
			// If the traits are the same at the two indices, add the gene from
			// smaller.
			if (tB.equals(tS)) {
				strands[1].add(new Gene(gS));
				sI++;
			} else {
				strands[1].add(new Gene());
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
