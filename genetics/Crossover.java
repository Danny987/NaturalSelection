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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Random;

import creature.geeksquad.genetics.Allele.Key;
import creature.geeksquad.genetics.Allele.Value;
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
	private Map<Key, Value> weightMap;
	
	/**
	 * Instantiate a new Crossover and initialize an empty weight table.
	 */
	public Crossover() {
		weightMap = new HashMap<Key, Value>();
	}
	
	/**
	 * Instantiate a new Crossover as a deep clone of a passed Crossover.
	 * 
	 * @param cross Crossover object to deep clone.
	 */
	public Crossover(Crossover cross) {
		this(cross.weightMap);
	}
	
	/**
	 * A special constructor that instantiates a new Crossover with the data
	 * from two passed Crossover objects. Used for inter-Population Crossover.
	 * 
	 * @param crossA Crossover object from Population 1.
	 * @param crossB Crossover object from Population 2.
	 */
	public Crossover(Crossover crossA, Crossover crossB) {
		this();
		// Average the weights from the two input Crossovers. If a Key only
		// appears in one map, only its weight gets used. Since this Crossover
		// will only be used once, its ages don't matter, so they just get
		// set to 0.
		for (Map.Entry<Key, Value> e : crossA.weightMap.entrySet()) {
			weightMap.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<Key, Value> e : crossB.weightMap.entrySet()) {
			Key k = e.getKey();
			Value v = e.getValue();
			if (weightMap.containsKey(k)) {
				float oldWeight = v.getWeight();
				float newWeight = (oldWeight + v.getWeight()) / 2;
				weightMap.put(k, new Value(newWeight));
			} else {
				weightMap.put(k, v);
			}
		}
	}
	
	/**
	 * Instantiates a new Crossover with the data from the provided map. If the
	 * map doesn't contain data for a particular key, it assigns the value for
	 * that key to Helper.MEDIAN_WEIGHT with age 0.
	 * 
	 * @param map Map<Key, Value> containing the data for this Crossover.
	 */
	public Crossover(Map<Key, Value> map) {
		this();
		for (Entry<Key, Value> entry : map.entrySet()) {
			Key k = entry.getKey();
			Value v = entry.getValue();
			if (weightMap.containsKey(k)) {
				weightMap.put(k, new Value(v));
			} else {
				weightMap.put(k, new Value(Helper.MEDIAN_WEIGHT, 0));
			}
		}
	}
	
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
	public Hopper[] crossover(Hopper hopperA, Hopper hopperB)
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
	@SuppressWarnings("finally")
	public Hopper[] crossover(Hopper hopperA, Hopper hopperB,
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
//				case SINGLE_POINT:
//					children = singlePoint(chromosomeA, chromosomeB);
//					break;
//				case DOUBLE_POINT:
//					children = doublePoint(chromosomeA, chromosomeB);
//					break;
//				case CUT_AND_SPLICE:
//					children = cutAndSplice(chromosomeA, chromosomeB);
//					break;
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
		
		try {
			genome1 = new Genotype(children[0]);
			childA = new Hopper(genome1);
			genome2 = new Genotype(children[1]);
			childB = new Hopper(genome2);
		} catch (IllegalArgumentException | GeneticsException ex) {
			throw ex;
		} finally {
			validateCrossover(hopperA, hopperB, childA, childB);
			Hopper[] offspring = {childA, childB};
			return offspring;
		}		
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
	private ArrayList<Gene>[] singlePoint(ArrayList<Gene> chromosomeA,
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
	private ArrayList<Gene>[] doublePoint(ArrayList<Gene> chromosomeA,
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
	private ArrayList<Gene>[] cutAndSplice(ArrayList<Gene> chromosomeA,
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
	private ArrayList<Gene>[] randomCross(ArrayList<Gene> chromosomeA,
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
				Gene childGeneA = adjustWeight(
								  new Gene(parentGeneA.getAlleles()[a1],
						                   parentGeneB.getAlleles()[b1]));
				Gene childGeneB = adjustWeight(
								  new Gene(parentGeneA.getAlleles()[a2],
						                   parentGeneB.getAlleles()[b2]));
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
	private ArrayList<Gene>[] randomSinglePoint(ArrayList<Gene> chromosomeA,
			ArrayList<Gene> chromosomeB) throws IllegalArgumentException,
			GeneticsException {
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
	private ArrayList<Gene>[] randomDoublePoint(ArrayList<Gene> chromosomeA,
			ArrayList<Gene> chromosomeB) throws IllegalArgumentException,
			GeneticsException {
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
	private ArrayList<Gene>[] randomCutAndSplice(ArrayList<Gene> chromosomeA,
			ArrayList<Gene> chromosomeB) throws IllegalArgumentException,
			GeneticsException {
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
	 * Evaluates the weight associated with a particular Allele's
	 * Trait-to-value pairing. If the pairing isn't already in the table,
	 * adds it and returns its current weight. If it is in the table, the
	 * method determines if the weight in the table should be changed and/or
	 * if the weight for the child should be changed. After changing/not
	 * changing the weight, it returns it returns a new Allele with the
	 * adjusted weight.
	 * 
	 * @param allele The Allele whose Trait->value->weight mapping should be
	 *            evaluated.
	 * @return A new Allele with the adjusted weight.
	 */
	private Allele adjustWeight(Allele allele) {
		if (allele == null) {
			return allele;
		}
		Allele newAllele;
		float weight = allele.getWeight();
		if (!weightMap.containsKey(allele.key)) {
			weightMap.put(allele.key, new Value(allele));
			return allele;
		}
		
		Value mapValue = weightMap.get(allele.key);
		mapValue.floor();
		float mapWeight = mapValue.getWeight();
	
		// Adjust the child Allele's weight toward the map weight.
		if (weight > mapWeight) {
			newAllele = new Allele(allele, weight - Helper.WEIGHT_STEP);
		} else if (weight < mapWeight) {
			newAllele = new Allele(allele, weight + Helper.WEIGHT_STEP);
		} else {
			newAllele = new Allele(allele, weight);
		}
		
		return newAllele;
	}
	
	/**
	 * Performs adjustWeight(Allele) on both Alleles in a Gene.
	 * 
	 * @param gene Gene whose Alleles should go through adjustWeight(Allele).
	 * @return A new Gene with the adjusted Alleles.
	 */
	private Gene adjustWeight(Gene gene) {
		Allele[] alleles = gene.getAlleles();
		alleles[0] = adjustWeight(alleles[0]);
		alleles[1] = adjustWeight(alleles[1]);
		return new Gene(alleles);
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
	private void validateCrossover(Hopper parentA, Hopper parentB,
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
	private void validateHelper(Hopper parentHopper, Hopper childHopper) {
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
		for (int i = 0; i < childSize && i < parentSize; i++) {
			Allele parentAllele = parent.get(i).getDominant();
			Allele childAllele = child.get(i).getDominant();
			if (!childAllele.equals(parentAllele)) {
				if (childFitness > parentFitness) {
					increaseWeight(childAllele);
					decreaseWeight(parentAllele);
				} else if (parentFitness > childFitness) {
					increaseWeight(parentAllele);
					decreaseWeight(childAllele);
				}
			}
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
	 * Scans the weight table for elements that can be removed. If an element
	 * has gone untouched for Helper.WEIGHT_MAX_AGE generations, it is
	 * removed. If the weight table exceeds Heler.WEIGHT_TABLE_CAPACITY,
	 * the oldest elements are removed. An element's weight is reset to 0 every
	 * time it is accessed during crossover.
	 */
	public void cleanUp() {
		// A new set of references to the weightMap entries, sorted by age.
		if (weightMap == null) {
			return;
		}
		
		PriorityQueue<Map.Entry<Key, Value>> weightQueue =
				new PriorityQueue<Map.Entry<Key, Value>>(weightMap.size(),
				new Comparator<Map.Entry<Key, Value>>() {
			@Override
			public int compare(Map.Entry<Key, Value> e1,
							   Map.Entry<Key, Value> e2) {
				int a1 = e1.getValue().getAge();
				int a2 = e2.getValue().getAge();
				if (a1 < a2) {
					return 1;
				} else if (a1 > a2) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		weightQueue.addAll(weightMap.entrySet());
		// If entry hasn't been accessed in Helper.MAX_WEIGHT_AGE Crossover
		// generations, remove it from the map.
		for (Map.Entry<Key, Value> e : weightQueue) {
			Key k = e.getKey();
			Value v = e.getValue();
			int age = v.getAge();
			if (age > Helper.MAX_WEIGHT_AGE) {
				weightMap.remove(k);
			}
		}
		// Reduce the size of the list if it's over capacity.
		while (weightQueue.size() > Helper.WEIGHT_TABLE_CAPACITY) {
			weightQueue.poll();
		}
	}
	
	/**
	 * Getter for the weight of a specified Allele in the weight table.
	 * 
	 * @param allele Allele key to look up in the weights table.
	 * @return The weight attached to key Trait and value in the weights table.
	 */
	public float getWeight(Allele allele) {
		return weightMap.get(allele.key).getWeight();
	}
	
	/**
	 * Setter for the weights of the Traits in the weight table. If not in the
	 * range Helper.MIN_WEIGHT (0.0f) to Helper.MAX_WEIGHT (1.0f), sets it to
	 * the appropriate extreme instead.
	 * 
	 * @param allele Allele key to change in the weights table.
	 * @param value Value of the Allele to change in the weights table.
	 * @param weight New weight float to assign to key in the weight table.
	 */
	public void setWeight(Allele allele, float weight) {
		if (weight > Helper.MAX_WEIGHT) {
			weight = Helper.MAX_WEIGHT;
		} else if (weight < Helper.MIN_WEIGHT) {
			weight = Helper.MIN_WEIGHT;
		}
		// Accessing a Key in the map resets its age to 0.
		if (weightMap.containsKey(allele.key)) {
			weightMap.put(allele.key, new Value(weight));
		} else {
			weightMap.put(allele.key, new Value(weight));
		}
	}
	
	/**
	 * Change a weight of a Trait in the weight table by a percentage of the
	 * current value.
	 * 
	 * @param allele Allele key to change in the weights table.
	 * @param percent Percentage float by which to change the weight. If
	 * 		      positive, increases the value; if negative, decreases.
	 */
	public void setWeightPercent(Allele allele, float percent) {
		if (weightMap.containsKey(allele.key)) {
			float old = weightMap.get(allele.key).getWeight();
			if (old + (old * percent) > Helper.MAX_WEIGHT) {
				percent = Helper.MAX_WEIGHT;
			} else if (old + (old * percent) < Helper.MIN_WEIGHT) {
				percent = Helper.MIN_WEIGHT;
			}
			weightMap.get(allele.key).setWeight(old + (old * percent));
			weightMap.get(allele.key).floor();
		} else {
			weightMap.put(allele.key, new Value(allele.getWeight()));
		}
	}
	
	/**
	 * Increase weight in the weight table by a fixed percentage of the
	 * current value.
	 * 
	 * @param allele Allele key to change in the weights table.
	 * @param value Value of the Allele to change in the weights table.
	 */
	public void increaseWeight(Allele allele) {
		setWeightPercent(allele, Helper.WEIGHT_STEP);
	}
	
	/**
	 * Decrease weight in the weight table by a fixed percentage of the
	 * current value.
	 * 
	 * @param trait Trait key to increase in the rule table.
	 * @param value Value of the Allele to change in the weights table.
	 */
	public void decreaseWeight(Allele allele) {
		setWeightPercent(allele, -Helper.WEIGHT_STEP);
	}
	
	/**
	 * Getter for weightMap.
	 * 
	 * @return Map<Key, Value> containing the weight table.
	 */
	public Map<Key, Value> getMap() {
		return weightMap;
	}
	
	/**
	 * Override of toString. Used for saving the state of the Crossover.
	 * 
	 * @return Formatted String representing the state of this Crossover.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("");
		builder.append("{");
		for (Map.Entry<Key, Value> entry : weightMap.entrySet()) {
			builder.append("(");
			builder.append(entry.getKey());
			builder.append(":");
			builder.append(entry.getValue());
			builder.append(")");
			builder.append(Helper.NEWLINE);
		}
		if (builder.length() > 1) {
			builder.deleteCharAt(builder.length() - 1);
		}
		builder.append('}');
		
		return builder.toString();
	}
	
	/**
	 * A nested enum representing the strategy of Crossover to use.
	 */
	public enum Strategy {
		SINGLE_POINT, DOUBLE_POINT, CUT_AND_SPLICE, RANDOM,
		RANDOM_SINGLE_POINT, RANDOM_DOUBLE_POINT, RANDOM_CUT_AND_SPLICE;
	}
	
	/**
	 * Main method for testing purposes.
	 * 
	 * @param String[] args Command-line arguments.
	 */
	public static void main(String[] args) {
		try {
			Crossover cross = new Crossover();
			Hopper parent1 = new Hopper(TestCreatures.getGenotype());
			Hopper parent2 = new Hopper();
			System.out.println("---Parent 1---");
			System.out.println(parent1);
			System.out.println("---Parent 2---");
			System.out.println(parent2);
			Hopper[] children = cross.crossover(parent1, parent2,
					Strategy.RANDOM);
			System.out.println(children);
		} catch (IllegalArgumentException | GeneticsException e) {
			e.printStackTrace();
		}
	}

}
