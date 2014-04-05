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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
	private Map<Allele, Float> weightMap;
	
	/**
	 * Instantiate a new Crossover and initialize the weight table.
	 */
	public Crossover() {
		weightMap = new HashMap<Allele, Float>();
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
		// Average the weights from the two input Crossovers.
		for (Map.Entry<Allele, Float> entry : weightMap.entrySet()) {
			Allele allele = entry.getKey();
			float weight = entry.getValue();
			weightMap.put(new Allele(allele), weight);
		}
	}
	
	/**
	 * Instantiates a new Crossover with the data from the provided map. If the
	 * map doesn't contain data for a particular key, it assigns the value for
	 * that key to Helper.MEDIAN_WEIGHT.
	 * 
	 * @param map Map<Allele, Float> containing the data for this Crossover.
	 */
	public Crossover(Map<Allele, Float> map) {
		this();
		for (Entry<Allele, Float> entry : map.entrySet()) {
			Allele allele = entry.getKey();
			float weight = entry.getValue();
			weightMap.put(new Allele(allele), weight);
		}
	}
	
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
	 *         trait match properly), or either parent was null, returns null.
	 * @throws IllegalArgumentException from Genotype instantiation.
	 * @throws GeneticsException from Genotype instantiation.
	 */
	public Genotype[] crossover(Genotype parentA, Genotype parentB,
			Strategy strategy) throws IllegalArgumentException,
			GeneticsException {
		// Verify that both parents exist.
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
			throw ex;
		}
		try {
			genome2 = new Genotype(children[1]);
		} catch (IllegalArgumentException | GeneticsException ex) {
			throw ex;
		}
		
		Genotype[] genomes = {genome1, genome2};
		
		return genomes;
	}
	
	/**
	 * An overloaded crossover that just provides an alternate input method for
	 * calling the primary crossover method.
	 * 
	 * Perform crossover on two parents based on a provided strategy. Since the
	 * positions of blocks don't matter and we need the key genes to line up,
	 * first align the strands and shift the root block to the beginning.
	 * 
	 * @param Genotype[] parents Genotype array containing parents A and B.
	 * @param Strategy strategy Strategy to use for crossover.
	 * @return Two-element array of Genotypes for children. If there were
	 *         problems creating any of the genes (e.g. if the alleles didn't
	 *         trait match properly), returns null.
	 * @throws IllegalArgumentException from Genotype instantiation.
	 * @throws GeneticsException from Genotype instantiation.
	 */
	public Genotype[] crossover(Genotype[] parents, Strategy strategy)
			throws IllegalArgumentException, GeneticsException {
		return crossover(parents[0], parents[1], strategy);
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
		Allele newAllele;
		float weight = allele.getWeight();
		if (!weightMap.containsKey(allele)) {
			weightMap.put(allele, weight);
			return allele;
		}
		float mapWeight = weightMap.get(allele);
	
		// Adjust the child Allele's weight toward the 
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
	 * Getter for the weights in the weight table.
	 * 
	 * @param allele Allele key to look up in the weights table.
	 * @return The weight attached to key Trait and value in the weights table.
	 */
	public float getWeight(Allele allele) {
		return weightMap.get(allele);
	}
	
	/**
	 * Setter for the weights of the Traits in the weight table. If not in the
	 * range Helper.MIN_WEIGHT (0.0f) to Helper.MAX_WEIGHT (1.0f), sets it to
	 * the appropriate extrema instead.
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
		weightMap.put(new Allele(allele), weight);
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
		float old = weightMap.get(allele);
		if (old * percent > Helper.MAX_WEIGHT) {
			percent = Helper.MAX_WEIGHT;
		} else if (old * percent < Helper.MIN_WEIGHT) {
			percent = Helper.MIN_WEIGHT;
		}
		weightMap.put(new Allele(allele), percent);
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
	 * A nested enum representing the strategy of Crossover to use.
	 */
	public enum Strategy {
		SINGLE_POINT, DOUBLE_POINT, CUT_AND_SPLICE, RANDOM;
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
		for (Map.Entry<Allele, Float> entry : weightMap.entrySet()) {
			builder.append(entry.getKey());
			builder.append(":");
			builder.append(entry.getValue());
			builder.append(Helper.NEWLINE);
		}
		if (builder.length() > 1) {
			builder.deleteCharAt(builder.length() - 1);
		}
		builder.append('}');
		
		return builder.toString();
	}
	
	/**
	 * Main method for testing purposes.
	 * 
	 * @param String[] args Command-line arguments.
	 */
	public static void main(String[] args) {
		Crossover cross = new Crossover();
		System.out.println(cross);
	}

}
