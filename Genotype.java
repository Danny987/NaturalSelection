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
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import creature.geeksquad.genetics.Allele.Trait;
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
	 * @return Two-element array of Genotypes for children. If there were
	 *         problems creating any of the genes (e.g. if the alleles didn't
	 *         trait match properly), returns null.
	 */
	public static Genotype[] crossover(Genotype parentA, Genotype parentB) {
		ArrayList<Gene> chromosomeA = parentA.getChromosome();
		ArrayList<Gene> chromosomeB = parentB.getChromosome();
		// Get the size of the larger chromosome.
		int sizeA = chromosomeA.size();
		int sizeB = chromosomeB.size();
		int size = (sizeA >= sizeB ? sizeA : sizeB);
		// Create the chromosomes for the twin children.
		ArrayList<Gene> childA = new ArrayList<Gene>();
		ArrayList<Gene> childB = new ArrayList<Gene>();
		
		// If the sizes differ, we need to call our helper method to pad out
		// the smaller strand.
		if (sizeA != sizeB) {
			ArrayList<ArrayList<Gene>> newChromosomes =
						matchSize(chromosomeA, chromosomeB);
			chromosomeA = newChromosomes.get(0);
			chromosomeB = newChromosomes.get(1);
		}
		
		// Iterate over the lists and pick a random allele from each parent.
		for (int i = 0; i < size; i++) {
			Gene parentGeneA = chromosomeA.get(i);
			Gene parentGeneB = chromosomeB.get(i);
			int a1 = random.nextInt(2);
			int b1 = random.nextInt(2);
			int a2 = (a1 == 1 ? 0 : 1);
			int b2 = (b1 == 1 ? 0 : 1);
			// Create the genes for the children.
			try {
				Gene childGeneA = new Gene(parentGeneA.getAlleles()[a1],
						                   parentGeneB.getAlleles()[b1]);
				Gene childGeneB = new Gene(parentGeneA.getAlleles()[a2],
						                   parentGeneB.getAlleles()[b2]);
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

		Genotype[] children = {new Genotype(childA), new Genotype(childB)};

		return children;
	}
	
	/**
	 * Helper method for crossover that matches size of two strands.
	 * 
	 * @param strandA ArrayList<Gene> of first strand.
	 * @param strandB ArrayList<Gene> of second strand.
	 * @return ArrayList<ArrayList<Gene>> containing length-matched copies of
	 *         the strands.
	 */
	public static ArrayList<ArrayList<Gene>> matchSize(ArrayList<Gene> strandA,
								 ArrayList<Gene> strandB) {
		ArrayList<ArrayList<Gene>> newStrands =
					new ArrayList<ArrayList<Gene>>();
		int sizeA = strandA.size();
		int sizeB = strandB.size();
		ArrayList<Gene> bigger;
		ArrayList<Gene> smaller;
		// If the strands are already the same length, don't change anything;
		// just return.
		if (sizeA == sizeB) {
			newStrands.add(strandA);
			newStrands.add(strandB);
			return newStrands;
		} else if (sizeA > sizeB) {
			bigger = new ArrayList<Gene>(strandA);
			smaller = new ArrayList<Gene>(strandB);
		} else {
			bigger = new ArrayList<Gene>(strandB);
			smaller = new ArrayList<Gene>(strandA);
		}
		// Iterate over the strands.
		Iterator<Gene> bigIt = bigger.iterator();
		for (Iterator<Gene> it = smaller.iterator(); it.hasNext() &&
					bigIt.hasNext(); ) {
			Gene bigGene = bigIt.next();
			Gene smallGene = it.next();
			// If the two Traits don't match, pad the smaller strand with
			// blank Genes.
			if (!bigGene.getTrait().equals(smallGene.getTrait())) {
				smaller.add(new Gene());
			}
		}
		// If smaller is still shorter than bigger, the difference was at the
		// end, so pad the end with empty Genes.
		while (smaller.size() < bigger.size()) {
			smaller.add(new Gene());
		}
		// Add the strand copies to the collection.
		newStrands.add(bigger);
		newStrands.add(smaller);
		
		return newStrands;
	}

	/**
	 * Evaluates the Genotype to create the Creature (phenotype) with default
	 * vectors.
	 * 
	 * @return Creature (phenotype) of the Genotype.
	 */
	public Creature getPhenotype() {
		Block[] body = getBody();
		Creature phenotype = null;
		// If body is null, the Genotype is invalid.
		if (body == null) {
			return null;
		} else {
			Vector3 rootForwardStart = Vector3.FORWARD;
			Vector3 rootUpStart = Vector3.UP;
			// Return a new Creature (phenotype) with the calculated values.
			try {
				phenotype = new Creature(body, rootForwardStart, rootUpStart);
			} catch (IllegalArgumentException ex) {
				ex.printStackTrace();
			}
			return phenotype;
		}
	}
	
	/**
	 * Creates the Creature (phenotype) for this Genotype with custom vectors.
	 * 
	 * @param rootForwardStart Vector3 a forward vector.
	 * @param rootUpStart Vector3 an up vector.
	 * @return Creature (phenotype) of the Genotype.
	 */
	public Creature getPhenotype(Vector3 rootForwardStart,
								 Vector3 rootUpStart) {
		Block[] body = getBody();
		// If body is null, the Genotype is invalid.
		if (body == null) {
			return null;
		} else {
			// Return a new Creature (phenotype) with the calculated values.
			return new Creature(body, rootForwardStart, rootUpStart);
		}
	}

	/**
	 * Gets the Block array representing this Genotype's body.
	 * 
	 * @return Block array representing this Genotype's body.
	 */
	public Block[] getBody() {
		ArrayList<Block> body = new ArrayList<Block>();
		ArrayList<Rule> rules = new ArrayList<Rule>();
		// Number of Blocks added so far.
		int blockCount = 0;
		// Number of Joints added to this Block so far.
		int jointsThisBlock = 0;
		// Number of Rules added to this Joint's rule list so far.
		int rulesThisJoint = 0;
		// Set once we find the root Block (where indexToParent is null).
		boolean rootFound = false;
		
		// Variables used for building the Blocks, Joints, and Rules.
		Rule rule = null;
		float length = 0.0f;
		float width = 0.0f;
		float height = 0.0f;
		int indexToParent = Block.PARENT_INDEX_NONE;
		Joint jointToParent = null;
		EnumJointType jointType = null;
		EnumJointSite jointSiteOnParent = null;
        EnumJointSite jointSiteOnChild = null;
        float jointOrientation = 0.0f;
        NeuronInput ruleInputA = null;
        NeuronInput ruleInputB = null;
        NeuronInput ruleInputC = null;
        NeuronInput ruleInputD = null;
        NeuronInput ruleInputE = null;
        EnumOperatorBinary binaryOperator1 = null;
        EnumOperatorUnary unaryOperator2 = null;
        EnumOperatorBinary binaryOperator3 = null;
        EnumOperatorUnary unaryOperator4 = null;

		// Iterate over the list and grab the metadata.
		for (Gene gene : chromosome) {
			Trait trait = gene.getTrait();
			Object value = gene.getValue();
			
			switch (trait) {
				// Trait LENGTH is a special case: it marks the beginning of
				// the encoding for a new Block, so close construction of the
				// previous block and add it to the list.
				case LENGTH:
					if (blockCount > 0) {
						// Close the open joint.
						if (jointsThisBlock > 0) {
							Joint joint = new Joint(jointType, 
													jointSiteOnParent,
						             				jointSiteOnChild,
						             				jointOrientation);
							// Add the currently open Rule list to the Joint.
							for (Rule r : rules) {
								joint.addRule(r, jointType.getDoF());
							}
							rulesThisJoint = 0;
							rules.clear();
						}
						
						Block block = new Block(indexToParent, jointToParent,
								 				length, width, height);
						body.add(block);
						jointsThisBlock = 0;
						// Clear all variables.
						rule = null;
						length = 0.0f;
						width = 0.0f;
						height = 0.0f;
						indexToParent = Block.PARENT_INDEX_NONE;
						jointToParent = null;
						jointType = null;
						jointSiteOnParent = null;
				        jointSiteOnChild = null;
				        jointOrientation = 0.0f;
				        ruleInputA = null;
				        ruleInputB = null;
				        ruleInputC = null;
				        ruleInputD = null;
				        ruleInputE = null;
				        binaryOperator1 = null;
				        unaryOperator2 = null;
				        binaryOperator3 = null;
				        unaryOperator4 = null;
					}
					blockCount++;
					length = (Float) value;
					break;
				// Width and height.
				case WIDTH:
					width = (Float) value;
					break;
				case HEIGHT:
					height = (Float) value;
					break;
				// If the trait is INDEX_TO_PARENT, since the body must have
				// exactly one root, whose indexToParent is null, we need
				// to check if rootFound has already been set.
				case INDEX_TO_PARENT:
					indexToParent = (Integer) value;
					if (rootFound && indexToParent == Block.PARENT_INDEX_NONE) {
						// If multiple roots, Genotype is invalid. Return null.
						return null;
					} else {
						rootFound = true;
					}
					break;
				// JOINT_TYPE is the first Gene of a Joint set, so we need to
				// close the previous Joint and add it before we continue.
				case JOINT_TYPE:
					if (jointsThisBlock > 0) {
						Joint joint = new Joint(jointType, jointSiteOnParent,
					             				jointSiteOnChild,
					             				jointOrientation);
						// Add the currently open Rule list to the Joint.
						for (Rule r : rules) {
							joint.addRule(r, jointType.getDoF());
						}
						jointToParent = joint;
						rulesThisJoint = 0;
						rules.clear();
					}
					jointsThisBlock++;
					jointType = (EnumJointType) value;
					break;
				// Joint orientation and sites.
				case JOINT_ORIENTATION:
					jointOrientation = (Float) value;
					break;
				case JOINT_SITE_ON_PARENT:
					jointSiteOnParent = (EnumJointSite) value;
					break;
				case JOINT_SITE_ON_CHILD:
					jointSiteOnChild = (EnumJointSite) value;
					break;
				// Rule input A marks the beginning of a new Rule definition.
				// Like Block and Joint, the old one needs to be closed first.
				case RULE_INPUT_A:
					if (rulesThisJoint > 0) {
						rules.add(rule);
					}
					rule = new Rule();
					rulesThisJoint++;
					ruleInputA = (NeuronInput) value;
					rule.setInput(ruleInputA, 0);
					break;
				// Rule inputs B-E.
				case RULE_INPUT_B:
					ruleInputB = (NeuronInput) value;
					rule.setInput(ruleInputB, 1);
					break;
				case RULE_INPUT_C:
					ruleInputC = (NeuronInput) value;
					rule.setInput(ruleInputC, 2);
					break;
				case RULE_INPUT_D:
					ruleInputD = (NeuronInput) value;
					rule.setInput(ruleInputD, 3);
					break;
				case RULE_INPUT_E:
					ruleInputE = (NeuronInput) value;
					rule.setInput(ruleInputE, 4);
					break;
				// Binary and unary operators.
				case BINARY_OPERATOR_1:
					binaryOperator1 = (EnumOperatorBinary) value;
					rule.setOp1(binaryOperator1);						
					break;
				case UNARY_OPERATOR_2:
					unaryOperator2 = (EnumOperatorUnary) value;
					rule.setOp2(unaryOperator2);
					break;
				case BINARY_OPERATOR_3:
					binaryOperator3 = (EnumOperatorBinary) value;
					rule.setOp3(binaryOperator3);
					break;
				case UNARY_OPERATOR_4:
					unaryOperator4 = (EnumOperatorUnary) value;
					rule.setOp4(unaryOperator4);
					break;
				// Default case catches EMPTY. EMPTY genes aren't expressed.
				default:
					// Fall through.
			}
		}
		// Close everything.
		Joint joint = new Joint(jointType, jointSiteOnParent,
 				jointSiteOnChild,
 				jointOrientation);
		// Add the currently open Rule list to the Joint.
		for (Rule r : rules) {
			joint.addRule(r, jointType.getDoF());
		}
		jointToParent = joint;
		
		// Since there's no LENGTH trait at the end, the final block is still
		// open, so it needs to be added to the list.
		body.add(new Block(indexToParent, jointToParent, length, width,
				 height));

		// A final check to confirm that the root block was found.
		if (!rootFound) {
			return null;
		} else {
			return Arrays.copyOf(body.toArray(), body.size(), Block[].class);
		}
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
	 * @return String containing genes list separated by spaces, enclosed in
	 *         curly braces:
	 *             {[(alleleA1)(alleleA2])] [(alleleB1)(alleleB2)] ...}.
	 */
	@Override
	public String toString() {
		StringBuilder gString = new StringBuilder("");
		gString.append('{');
		for (Gene g : chromosome) {
			gString.append(g.toString() + " ");
		}
		gString.deleteCharAt(gString.length() - 1);
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
	
	/**
	 * Main method for testing purposes.
	 */
	public static void main(String[] args) {
		ArrayList<Allele> alleles = new ArrayList<Allele>();
		ArrayList<Gene> genes;
		// Adding some dummy Alleles to the list.
		alleles.add(new Allele(Trait.LENGTH, 35.4f, 0.3f));
		alleles.add(new Allele(Trait.LENGTH, 13.3f, 0.64f));
		alleles.add(new Allele(Trait.HEIGHT, 42.5f, 0.5f));
		alleles.add(new Allele(Trait.HEIGHT, 20.5f, 0.35f));
		alleles.add(new Allele(Trait.WIDTH, 42.5f, 0.5f));
		alleles.add(new Allele(Trait.WIDTH, 20.5f, 0.35f));
		alleles.add(new Allele(Trait.INDEX_TO_PARENT, Block.PARENT_INDEX_NONE,
				               0.63f));
		alleles.add(new Allele(Trait.INDEX_TO_PARENT, 1, 0.4f));
		alleles.add(new Allele(Trait.JOINT_TYPE, EnumJointType.RIGID, 0.3f));
		alleles.add(new Allele(Trait.JOINT_TYPE, EnumJointType.HINGE, 0.2f));
		// Second block.
		alleles.add(new Allele(Trait.LENGTH, 21.4f, 0.2f));
		alleles.add(new Allele(Trait.LENGTH, 20.0f, 0.199f));
		alleles.add(new Allele(Trait.HEIGHT, 40.5f, 0.1f));
		alleles.add(new Allele(Trait.HEIGHT, 45.5f, 0.4f));
		alleles.add(new Allele(Trait.WIDTH, 19.5f, 0.5f));
		alleles.add(new Allele(Trait.WIDTH, 25.5f, 0.6f));
		alleles.add(new Allele(Trait.INDEX_TO_PARENT, 0, 0.63f));
		alleles.add(new Allele(Trait.INDEX_TO_PARENT, 1, 0.4f));
		alleles.add(new Allele(Trait.JOINT_TYPE, EnumJointType.RIGID, 0.3f));
		alleles.add(new Allele(Trait.JOINT_TYPE, EnumJointType.HINGE, 0.2f));
		alleles.add(new Allele(Trait.JOINT_ORIENTATION, 0.5f, 0.5f));
		alleles.add(new Allele(Trait.JOINT_ORIENTATION, 0.5f, 0.5f));
		alleles.add(new Allele(Trait.JOINT_SITE_ON_PARENT,
				               EnumJointSite.EDGE_FRONT_NORTH, 0.6f));
		alleles.add(new Allele(Trait.JOINT_SITE_ON_PARENT,
	               			   EnumJointSite.EDGE_BACK_EAST, 0.3f));
		alleles.add(new Allele(Trait.JOINT_SITE_ON_CHILD,
	               			   EnumJointSite.EDGE_BACK_SOUTH, 0.6f));
		alleles.add(new Allele(Trait.JOINT_SITE_ON_CHILD,
    			   			   EnumJointSite.EDGE_BACK_WEST, 0.7f));
		alleles.add(new Allele(Trait.RULE_INPUT_A,
					new NeuronInput(EnumNeuronInputType.TIME), 0.3f));
		alleles.add(new Allele(Trait.RULE_INPUT_A,
					new NeuronInput(EnumNeuronInputType.TIME), 0.25f));
		alleles.add(new Allele(Trait.RULE_INPUT_B,
					new NeuronInput(EnumNeuronInputType.TIME), 0.3f));
		alleles.add(new Allele(Trait.RULE_INPUT_B,
					new NeuronInput(EnumNeuronInputType.TIME), 0.25f));
		alleles.add(new Allele(Trait.RULE_INPUT_C,
					new NeuronInput(EnumNeuronInputType.TIME), 0.3f));
		alleles.add(new Allele(Trait.RULE_INPUT_C,
					new NeuronInput(EnumNeuronInputType.TIME), 0.25f));
		alleles.add(new Allele(Trait.RULE_INPUT_D,
					new NeuronInput(EnumNeuronInputType.TIME), 0.3f));
		alleles.add(new Allele(Trait.RULE_INPUT_D,
					new NeuronInput(EnumNeuronInputType.TIME), 0.25f));
		alleles.add(new Allele(Trait.RULE_INPUT_E,
					new NeuronInput(EnumNeuronInputType.TIME), 0.3f));
		alleles.add(new Allele(Trait.RULE_INPUT_E,
					new NeuronInput(EnumNeuronInputType.TIME), 0.25f));
		alleles.add(new Allele(Trait.BINARY_OPERATOR_1,
					EnumOperatorBinary.ADD, 0.2f));
		alleles.add(new Allele(Trait.BINARY_OPERATOR_1,
					EnumOperatorBinary.SUBTRACT, 0.1f));
		alleles.add(new Allele(Trait.UNARY_OPERATOR_2,
					EnumOperatorUnary.ABS,0.3f));
		alleles.add(new Allele(Trait.UNARY_OPERATOR_2,
					EnumOperatorUnary.EXP,0.2f));
		alleles.add(new Allele(Trait.BINARY_OPERATOR_3,
					EnumOperatorBinary.ADD, 0.2f));
		alleles.add(new Allele(Trait.BINARY_OPERATOR_3,
					EnumOperatorBinary.SUBTRACT, 0.1f));
		alleles.add(new Allele(Trait.UNARY_OPERATOR_4,
					EnumOperatorUnary.ABS,0.3f));
		alleles.add(new Allele(Trait.UNARY_OPERATOR_4,
					EnumOperatorUnary.EXP,0.2f));
		
		// Build some Genes from the Alleles.
		genes = Gene.allelesToGenes(alleles);
		
		// Create a Genotype from the Genes.
		Genotype genotype = new Genotype(genes);
		System.out.println("Genotype " + genotype);
		Creature phenotype = genotype.getPhenotype();
		System.out.println("Phenotype " + phenotype);
		
		// Second test creature.
		ArrayList<Allele> alleles2 = new ArrayList<Allele>();
		ArrayList<Gene> genes2;
		alleles2.add(new Allele(Trait.LENGTH, 45.4f, 0.37f));
		alleles2.add(new Allele(Trait.LENGTH, 29.3f, 0.54f));
		alleles2.add(new Allele(Trait.HEIGHT, 40.5f, 0.35f));
		alleles2.add(new Allele(Trait.HEIGHT, 41.5f, 0.36f));
		alleles2.add(new Allele(Trait.WIDTH, 56.5f, 0.5f));
		alleles2.add(new Allele(Trait.WIDTH, 56.5f, 0.5f));
		alleles2.add(new Allele(Trait.INDEX_TO_PARENT, Block.PARENT_INDEX_NONE,
				0.54f));
		alleles2.add(new Allele(Trait.INDEX_TO_PARENT, 1, 0.433f));
		alleles2.add(new Allele(Trait.JOINT_TYPE,
							    EnumJointType.SPHERICAL,0.233f));
		alleles2.add(new Allele(Trait.JOINT_TYPE,
				                EnumJointType.SPHERICAL, 0.86f));
		alleles2.add(new Allele(Trait.LENGTH, 20.4f, 0.232f));
		alleles2.add(new Allele(Trait.LENGTH, 21.3f, 0.855f));
		alleles2.add(new Allele(Trait.HEIGHT, 60.0f, 0.125f));
		alleles2.add(new Allele(Trait.HEIGHT, 60.0f, 0.115f));
		alleles2.add(new Allele(Trait.WIDTH, 19.5f, 0.5f));
		alleles2.add(new Allele(Trait.WIDTH, 19.4f, 0.5f));
		alleles2.add(new Allele(Trait.INDEX_TO_PARENT, 0, 0.59f));
		alleles2.add(new Allele(Trait.INDEX_TO_PARENT, 1, 0.49f));
		alleles2.add(new Allele(Trait.JOINT_TYPE, EnumJointType.RIGID, 0.32f));
		alleles2.add(new Allele(Trait.JOINT_TYPE,
							    EnumJointType.SPHERICAL, 0.22f));
		alleles2.add(new Allele(Trait.JOINT_ORIENTATION, 0.5f, 0.5f));
		alleles2.add(new Allele(Trait.JOINT_ORIENTATION, 0.5f, 0.5f));
		alleles2.add(new Allele(Trait.JOINT_SITE_ON_PARENT,
				EnumJointSite.EDGE_FRONT_WEST, 0.6f));
		alleles2.add(new Allele(Trait.JOINT_SITE_ON_PARENT,
				EnumJointSite.EDGE_MID_SOUTHWEST, 0.3f));
		alleles2.add(new Allele(Trait.JOINT_SITE_ON_CHILD,
				EnumJointSite.VERTEX_BACK_SOUTHEAST, 0.6f));
		alleles2.add(new Allele(Trait.JOINT_SITE_ON_CHILD,
				EnumJointSite.VERTEX_BACK_NORTHEAST, 0.7f));
		alleles2.add(new Allele(Trait.RULE_INPUT_A, new NeuronInput(
				EnumNeuronInputType.TIME), 0.3f));
		alleles2.add(new Allele(Trait.RULE_INPUT_A, new NeuronInput(
				EnumNeuronInputType.TIME), 0.25f));
		alleles2.add(new Allele(Trait.RULE_INPUT_B, new NeuronInput(
				EnumNeuronInputType.TIME), 0.3f));
		alleles2.add(new Allele(Trait.RULE_INPUT_B, new NeuronInput(
				EnumNeuronInputType.TIME), 0.25f));
		alleles2.add(new Allele(Trait.RULE_INPUT_C, new NeuronInput(
				EnumNeuronInputType.TIME), 0.3f));
		alleles2.add(new Allele(Trait.RULE_INPUT_C, new NeuronInput(
				EnumNeuronInputType.TIME), 0.25f));
		alleles2.add(new Allele(Trait.RULE_INPUT_D, new NeuronInput(
				EnumNeuronInputType.TIME), 0.3f));
		alleles2.add(new Allele(Trait.RULE_INPUT_D, new NeuronInput(
				EnumNeuronInputType.TIME), 0.25f));
		alleles2.add(new Allele(Trait.RULE_INPUT_E, new NeuronInput(
				EnumNeuronInputType.TIME), 0.3f));
		alleles2.add(new Allele(Trait.RULE_INPUT_E, new NeuronInput(
				EnumNeuronInputType.TIME), 0.25f));
		alleles2.add(new Allele(Trait.BINARY_OPERATOR_1,
				EnumOperatorBinary.ADD, 0.2f));
		alleles2.add(new Allele(Trait.BINARY_OPERATOR_1,
				EnumOperatorBinary.SUBTRACT, 0.1f));
		alleles2.add(new Allele(Trait.UNARY_OPERATOR_2, EnumOperatorUnary.ABS,
				0.3f));
		alleles2.add(new Allele(Trait.UNARY_OPERATOR_2, EnumOperatorUnary.EXP,
				0.2f));
		alleles2.add(new Allele(Trait.BINARY_OPERATOR_3,
				EnumOperatorBinary.MULTIPLY, 0.2f));
		alleles2.add(new Allele(Trait.BINARY_OPERATOR_3,
				EnumOperatorBinary.ARCTAN2, 0.1f));
		alleles2.add(new Allele(Trait.UNARY_OPERATOR_4, EnumOperatorUnary.LOG,
				0.3f));
		alleles2.add(new Allele(Trait.UNARY_OPERATOR_4, EnumOperatorUnary.SIN,
				0.2f));
		alleles2.add(new Allele(Trait.INDEX_TO_PARENT, 1, 0.59f));
		alleles2.add(new Allele(Trait.INDEX_TO_PARENT, 2, 0.49f));
		alleles2.add(new Allele(Trait.JOINT_TYPE, EnumJointType.RIGID, 0.32f));
		alleles2.add(new Allele(Trait.JOINT_TYPE,
							    EnumJointType.SPHERICAL, 0.22f));
		alleles2.add(new Allele(Trait.JOINT_ORIENTATION, 0.5f, 0.5f));
		alleles2.add(new Allele(Trait.JOINT_ORIENTATION, 0.5f, 0.5f));
		alleles2.add(new Allele(Trait.JOINT_SITE_ON_PARENT,
				EnumJointSite.EDGE_MID_NORTHWEST, 0.6f));
		alleles2.add(new Allele(Trait.JOINT_SITE_ON_PARENT,
				EnumJointSite.FACE_EAST, 0.3f));
		alleles2.add(new Allele(Trait.JOINT_SITE_ON_CHILD,
				EnumJointSite.VERTEX_BACK_SOUTHEAST, 0.6f));
		alleles2.add(new Allele(Trait.JOINT_SITE_ON_CHILD,
				EnumJointSite.VERTEX_FRONT_SOUTHWEST, 0.7f));
		alleles2.add(new Allele(Trait.RULE_INPUT_A, new NeuronInput(
				EnumNeuronInputType.TIME), 0.3f));
		alleles2.add(new Allele(Trait.RULE_INPUT_A, new NeuronInput(
				EnumNeuronInputType.TIME), 0.25f));
		alleles2.add(new Allele(Trait.RULE_INPUT_B, new NeuronInput(
				EnumNeuronInputType.TIME), 0.3f));
		alleles2.add(new Allele(Trait.RULE_INPUT_B, new NeuronInput(
				EnumNeuronInputType.TIME), 0.25f));
		alleles2.add(new Allele(Trait.RULE_INPUT_C, new NeuronInput(
				EnumNeuronInputType.TIME), 0.3f));
		alleles2.add(new Allele(Trait.RULE_INPUT_C, new NeuronInput(
				EnumNeuronInputType.TIME), 0.25f));
		alleles2.add(new Allele(Trait.RULE_INPUT_D, new NeuronInput(
				EnumNeuronInputType.TIME), 0.3f));
		alleles2.add(new Allele(Trait.RULE_INPUT_D, new NeuronInput(
				EnumNeuronInputType.TIME), 0.25f));
		alleles2.add(new Allele(Trait.RULE_INPUT_E, new NeuronInput(
				EnumNeuronInputType.TIME), 0.3f));
		alleles2.add(new Allele(Trait.RULE_INPUT_E, new NeuronInput(
				EnumNeuronInputType.TIME), 0.25f));
		alleles2.add(new Allele(Trait.BINARY_OPERATOR_1,
				EnumOperatorBinary.ADD, 0.2f));
		alleles2.add(new Allele(Trait.BINARY_OPERATOR_1,
				EnumOperatorBinary.SUBTRACT, 0.1f));
		alleles2.add(new Allele(Trait.UNARY_OPERATOR_2, EnumOperatorUnary.ABS,
				0.3f));
		alleles2.add(new Allele(Trait.UNARY_OPERATOR_2, EnumOperatorUnary.EXP,
				0.2f));
		alleles2.add(new Allele(Trait.BINARY_OPERATOR_3,
				EnumOperatorBinary.MULTIPLY, 0.2f));
		alleles2.add(new Allele(Trait.BINARY_OPERATOR_3,
				EnumOperatorBinary.ARCTAN2, 0.1f));
		alleles2.add(new Allele(Trait.UNARY_OPERATOR_4, EnumOperatorUnary.LOG,
				0.3f));
		alleles2.add(new Allele(Trait.UNARY_OPERATOR_4, EnumOperatorUnary.SIN,
				0.2f));
		
		genes2 = Gene.allelesToGenes(alleles2);
		
		Genotype genotype2 = new Genotype(genes2);
		System.out.println("Genotype2 " + genotype2);
		Creature phenotype2 = genotype2.getPhenotype();
		System.out.println("Phenotype2 " + phenotype2);
		
		// Crossover test.
		Genotype[] children = crossover(genotype, genotype2);
		System.out.println("Child1 " + children[0]);
		System.out.println("Child1 Phenotype " + children[0].getPhenotype());
		System.out.println("Child2 " + children[1]);
		System.out.println("Child2 Phenotype " + children[1].getPhenotype());
	}

}
