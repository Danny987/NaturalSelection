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

import creature.geeksquad.genetics.Allele.*;
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
	 * Perform crossover on two parents based on a random strategy.
	 * 
	 * @param Hopper hopperA First parent.
	 * @param Hopper hopperB Second parent.
	 * @return Two-element array of Hoppers containing the children. If there
	 *         were problems creating any of the genes (e.g. alleles didn't
	 *         trait match properly), or either parent was null, returns null.
	 * @throws IllegalArgumentException from Genotype instantiation.
	 * @throws GeneticsException from Genotype instantiation.
	 */
	public static Hopper[] crossover(Hopper hopperA, Hopper hopperB)
			throws IllegalArgumentException, GeneticsException {
		Strategy strategy = Strategy.values()
				[Helper.RANDOM.nextInt(Strategy.values().length)];
		return crossover(hopperA, hopperB, strategy);
	}
	
	/**
	 * Perform crossover on two parents based on a provided strategy. Since the
	 * positions of blocks don't matter and we need the key genes to line up,
	 * first align the strands and shift the root block to the beginning.
	 * 
	 * @param Hopper hopperA First parent.
	 * @param Hopper hopperB Second parent.
	 * @param Strategy strategy Strategy to use for crossover.
	 * @return Two-element array of Hoppers containing the children. If there
	 *         were problems creating any of the genes (e.g. alleles didn't
	 *         trait match properly), or either parent was null, returns null.
	 * @throws IllegalArgumentException from Genotype instantiation.
	 * @throws GeneticsException from Genotype instantiation.
	 */
	public static Hopper[] crossover(Hopper hopperA, Hopper hopperB,
			Strategy strategy) throws IllegalArgumentException,
			GeneticsException {
		Genotype parentA, parentB;
		// Verify that both parents exist.
		if (hopperA != null && hopperB != null) {
			parentA = hopperA.getGenotype();
			parentB = hopperB.getGenotype();
		} else {
			return null;
		}
		if (parentA == null || parentB == null) {
			return null;
		}
		@SuppressWarnings("unchecked")
		ArrayList<Gene>[] children = new ArrayList[2];
		
		ArrayList<Gene> chromosomeA = parentA.getChromosome();
		ArrayList<Gene> chromosomeB = parentB.getChromosome();
		
		// Align the key genes.
		ArrayList<Gene>[] newChromosomes = align(chromosomeA, chromosomeB);
		
		// Randomly choose the starting parent.
		if (Helper.choose() > 0) {
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
				case RANDOM_SINGLE_POINT:
					children = randomSinglePoint(chromosomeA, chromosomeB);
					break;
				case RANDOM_DOUBLE_POINT:
					children = randomDoublePoint(chromosomeA, chromosomeB);
					break;
				case RANDOM_CUT_AND_SPLICE:
					children = randomCutAndSplice(chromosomeA, chromosomeB);
					break;
				default:
					// Fall through.
			}
		} catch (IllegalArgumentException | GeneticsException ex) {
			throw ex;
		}
		
		Genotype genome1 = null;
		Genotype genome2 = null;
		Hopper childA = null;
		Hopper childB = null;
		
		genome1 = new Genotype(children[0]);
		genome2 = new Genotype(children[1]);
		validateCrossover(hopperA, hopperB, childA, childB);

		try {
			childA = new Hopper(genome1);
		} catch (IllegalArgumentException | GeneticsException ex) {
			childA = null;
		}
		try {
			childB = new Hopper(genome2);
		} catch (IllegalArgumentException | GeneticsException ex) {
			childB = null;
		}
		
		Hopper[] offspring = {childA, childB};
		return offspring;
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
		
		// Randomly choose the transition point. In order to prevent the
		// child from being identical to the parent, the transition point
		// cannot be within 20% length to either end.
		int interval = size / 5;
		int transition = rand.nextInt(size - (2 * interval)) + interval;
		
		// Iterate over the lists.
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
		
		// Randomly choose the transition points. To prevent the child from
		// being too similar to the parent, it sets a minimum distance between
		// transitions of 10% of the strand's length.
		int interval = size / 10;
		int transition1 = rand.nextInt(size - (2 * interval)) + interval;
		int transition2 = transition1 + interval + rand.nextInt(size
				- transition1 - (2 * interval));
		
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
				throw ex;
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
		
		// Number of splits. Min 5, max 20.
		int numSplits = rand.nextInt(16) + 5;
		int[] splitPoints = new int[numSplits];
		int tally = 0;
		for (int i = 0; i < numSplits; i++) {
			splitPoints[i] = rand.nextInt(size - tally) + tally;
			tally += splitPoints[i];
		}
		
		int j = 0;
		// Iterate over the lists.
		for (int i = 0; i < size; i++) {
			// Change segments. Short-circuits if j >= splitPoints.length.
			if (j < splitPoints.length && i == splitPoints[j]) {
				ArrayList<Gene> swap = chromosomeA;
				chromosomeB = chromosomeA;
				chromosomeA = swap;
				j++;
			}
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
				throw ex;
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
			throw new GeneticsException(
					"Crossover.align produced one or more null chromosomes.");
		}
		
		// Iterate over the lists and pick a random allele from each parent.
		for (int i = 0; i < size; i++) {
			Gene parentGeneA = new Gene(chromosomeA.get(i));
			Gene parentGeneB = new Gene(chromosomeB.get(i));
			int a1 = Helper.choose();
			int b1 = Helper.choose();
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
			} catch (IllegalArgumentException ex) {
				throw ex;
			}
		}
		// If the child Gene pulled a matched pair of empty Alleles, it will
		// be trimmed when instantiating the new Genotypes.
		@SuppressWarnings("rawtypes")
		ArrayList[] children = {childA, childB};
		
		return children;
	}
	
	/**
	 * Perform 50/50 random crossover with single-point on two parents to
	 * create twin children.
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
	private static ArrayList<Gene>[] randomSinglePoint(
			ArrayList<Gene> chromosomeA, ArrayList<Gene> chromosomeB)
			throws IllegalArgumentException, GeneticsException {
		ArrayList<Gene>[] children = new ArrayList[2];
		if (chromosomeA != null && chromosomeB != null) {
			children = singlePoint(chromosomeA, chromosomeB);
		}
		
		if (children != null && children[0] != null && children[1] != null) {
			return randomCross(children[0], children[1]);
		} else {
			return null;
		}
	}
	
	/**
	 * Perform 50/50 random crossover with double-point on two parents to
	 * create twin children.
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
	private static ArrayList<Gene>[] randomDoublePoint(
			ArrayList<Gene> chromosomeA, ArrayList<Gene> chromosomeB)
			throws IllegalArgumentException, GeneticsException {
		ArrayList<Gene>[] children = new ArrayList[2];
		if (chromosomeA != null && chromosomeB != null) {
			children = doublePoint(chromosomeA, chromosomeB);
		}
		
		if (children != null && children[0] != null && children[1] != null) {
			return randomCross(children[0], children[1]);
		} else {
			return null;
		}
	}
	
	/**
	 * Perform 50/50 random crossover with cut-and-splice on two parents to
	 * create twin children.
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
	private static ArrayList<Gene>[] randomCutAndSplice(
			ArrayList<Gene> chromosomeA, ArrayList<Gene> chromosomeB)
			throws IllegalArgumentException, GeneticsException {
		ArrayList<Gene>[] children = new ArrayList[2];
		if (chromosomeA != null && chromosomeB != null) {
			children = cutAndSplice(chromosomeA, chromosomeB);
		}
		
		if (children != null && children[0] != null && children[1] != null) {
			return randomCross(children[0], children[1]);
		} else {
			return null;
		}
	}
	
	/**
	 * Validates the Crossover: compares fitness and similarity of parents and
	 * offspring, and adjusts Allele weights.
	 * 
	 * @param parentA First parent Hopper.
	 * @param parentB Second parent Hopper.
	 * @param childA First child Hopper.
	 * @param childB Second child Hopper.
	 */
	private static void validateCrossover(Hopper parentA, Hopper parentB,
			Hopper childA, Hopper childB) {
		/* ****************************************************************** */
		/* Waiting on Joel's code.                                            */
		/* May do nothing if fitness simulation still doesn't work.           */
		/* ****************************************************************** */
		
		validateHelper(parentA, childA);
		validateHelper(parentA, childB);
		validateHelper(parentB, childA);
		validateHelper(parentB, childB);
	}
	
	/**
	 * Helper method for validateCrossover - takes one parent and one child
	 * and adjusts their weights.
	 * 
	 * @param parent Parent Hopper to compare.
	 * @param child Child Hopper to compare.
	 */
	private static void validateHelper(Hopper parentHopper,
									   Hopper childHopper) {
		// Return immediately if either input is null.
		if (parentHopper == null || childHopper == null) {
			return;
		}
		
		ArrayList<Gene> parent = parentHopper.getChromosome();
		ArrayList<Gene> child = childHopper.getChromosome();
		float parentFitness = parentHopper.getFitness();
		float childFitness = childHopper.getFitness();
		int parentSize = parent.size();
		int childSize = child.size();
		// If a child is too similar to its parent, reject it.
		int similar = 0;
		for (int i = 0; i < childSize && i < parentSize; i++) {
			Allele parentDominant = parent.get(i).getDominant();
			Allele parentRecessive = parent.get(i).getRecessive();
			Allele childDominant = child.get(i).getDominant();
			Allele childRecessive = parent.get(i).getRecessive();

			if (!childDominant.equals(parentDominant)) {
				// Weight decreases are substantially larger than increases.
				if (childFitness > parentFitness) {
					childDominant.increaseWeight();
					childRecessive.decreaseWeight();
					parentDominant.decreaseWeight();
					parentRecessive.increaseWeight();
				} else if (parentFitness > childFitness) {
					childDominant.decreaseWeight();
					childRecessive.increaseWeight();
					parentDominant.decreaseWeight();
					parentRecessive.increaseWeight();
				}
			} else {
				similar++;
			}
		}
		if (similar / child.size() >= Helper.MAX_SIMILAR_PERCENTAGE) {
			child = null;
		}
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
		SINGLE_POINT, DOUBLE_POINT, CUT_AND_SPLICE,
		RANDOM, RANDOM_SINGLE_POINT, RANDOM_DOUBLE_POINT, RANDOM_CUT_AND_SPLICE;
	}
	
	/**
	 * Main method for testing purposes.
	 * 
	 * @param String[] args Command-line arguments.
	 */
	public static void main(String[] args) {
		try {
			Hopper parent1 = new Hopper(TestCreatures.getGenotype());
			Hopper parent2 = new Hopper();
			System.out.println("---Parent 1---");
			System.out.println(parent1);
			System.out.println("---Parent 2---");
			System.out.println(parent2);
			Hopper[] children = crossover(parent1, parent2,
					Strategy.RANDOM);
			System.out.println(children);
		} catch (IllegalArgumentException | GeneticsException e) {
			e.printStackTrace();
		}
	}

}
