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
import creature.geeksquad.library.Helper;
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
	private Block[] body;
	private static Random random = new Random();

	/**
	 * Instantiate a new Genotype as a deep clone of a passed chromosome list.
	 * 
	 * @param source ArrayList<Gene> containing the chromosomes for this
	 * 			     Genotype.
	 */
	public Genotype(ArrayList<Gene> source) {
		chromosome = new ArrayList<Gene>();
		for (Gene g : source) {
			chromosome.add(new Gene(g));
		}
		body = buildBody();
	}
	
	/**
	 * Instantiate a new Genotype as a deep clone of a passed Genotype.
	 * 
	 * @param source Genotype to deep clone.
	 */
	public Genotype(Genotype source) {
		this(source.getChromosome());
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
		
		// Align the key genes.
		ArrayList<ArrayList<Gene>> newChromosomes = align(chromosomeA,
				chromosomeB);
		chromosomeA = newChromosomes.get(0);
		chromosomeB = newChromosomes.get(1);
		
		// Iterate over the lists and pick a random allele from each parent.
		for (int i = 0; i < size; i++) {
			Gene parentGeneA = new Gene(chromosomeA.get(i));
			Gene parentGeneB = chromosomeB.get(i);
			int a1 = random.nextInt(2);
			int b1 = random.nextInt(2);
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
	 * Helper method for crossover that matches the locations of the key genes
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
		
		// Clone bigger.
		for (Gene g : bigger) {
			strands.get(0).add(new Gene(g));
			// Initialize smaller.
			strands.get(1).add(new Gene());
		}
		
		// For smaller, we need to clone each section individually and pad with
		// empty genes where the strands don't line up.
		int sI = 0;
		for (int bI = 0; bI < bigSize; bI++) {
			ArrayList<Gene> strand = strands.get(1);
			Gene gB = bigger.get(bI);
			Gene gS = smaller.get(sI);
			Trait tB = gB.getTrait();
			Trait tS = gS.getTrait();
			
			// If the traits are the same at the two indices, add the gene.
			if (tB.equals(tS)) {
				strand.add(new Gene(gS));
				sI++;
			} else {
				strand.add(new Gene());
			}
		}
		
		return strands;
	}
	
	/**
	 * Adds a new Block with associated Joint to the end of the Genotype.
	 * 
	 * @param
	 * @return
	 */
	//
	// TODO
	//
	
	/**
	 * Adds a new Rule at the specified position to the rule list for block.
	 */
	//
	// TODO
	//

	/**
	 * Evaluates the Genotype to create the Creature (phenotype) with default
	 * vectors.
	 * 
	 * @return Creature (phenotype) of the Genotype.
	 */
	public Creature getPhenotype() {
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
	public Block[] buildBody() {
		ArrayList<Block> body = new ArrayList<Block>();
		ArrayList<Rule> dof1 = new ArrayList<Rule>();
		ArrayList<Rule> dof2 = new ArrayList<Rule>();
		ArrayList<Rule> rules = dof1;
		// Flags for open elements.
		boolean blockOpen = false;
		boolean jointOpen = false;
		boolean ruleOpen = false;
		// Set once we find the root Block (where indexToParent is null).
		boolean rootFound = false;
		
		// Variables used for building the Blocks, Joints, and Rules.
		Rule rule = null;
		float length = 0.0f;
		float height = 0.0f;
		float width = 0.0f;
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
					if (blockOpen) {
						// Close the open joint.
						if (jointOpen) {
							Joint joint = new Joint(jointType, 
													jointSiteOnParent,
						             				jointSiteOnChild,
						             				jointOrientation);
							jointOpen = false;
							// Add the currently open Rule list to the Joint.
							for (Rule r : dof1) {
								joint.addRule(r, EnumJointType.DOF_1 - 1);
							}
							for (Rule r : dof2) {
								joint.addRule(r, EnumJointType.DOF_2 - 1);
							}
							dof1.clear();
							dof2.clear();
						}
						
						Block block = new Block(indexToParent, jointToParent,
								 				length, height, width);
						body.add(block);
						// Clear all variables.
						rule = null;
						length = 0.0f;
						height = 0.0f;
						width = 0.0f;
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
				        rules = dof1;
					}
					blockOpen = true;
					length = (Float) value;
					break;
				// Width and height.
				case HEIGHT:
					height = (Float) value;
					break;
				case WIDTH:
					width = (Float) value;
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
					if (jointOpen) {
						Joint joint = new Joint(jointType, jointSiteOnParent,
					             				jointSiteOnChild,
					             				jointOrientation);
						// Add the rule tables to the Joint
						for (Rule r : dof1) {
							joint.addRule(r, EnumJointType.DOF_1 - 1);
						}
						for (Rule r : dof2) {
							joint.addRule(r, EnumJointType.DOF_2 - 1);
						}
						dof1.clear();
						dof2.clear();
						rules = dof1;
						jointToParent = joint;
						jointOpen = false;
					}
					jointOpen = true;
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
					if (ruleOpen) {
						rules.add(rule);
					}
					ruleOpen = true;
					rule = new Rule();
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
				// If we find a degree of freedom Allele, switch rules to point
				// to the second DoF list.
				case DOF_MARKER:
					if (ruleOpen) {
						rules.add(rule);
						ruleOpen = false;
					}
					rules = (value.equals(EnumJointType.DOF_1) ? dof1 : dof2);
					break;
				// Default case catches EMPTY. Empty genes aren't expressed.
				default:
					// Fall through.
			}
		}
		
		if (ruleOpen) {
			rules.add(rule);
			ruleOpen = false;
		}
		
		// The final Block and Joint will always be open at the end.
		Joint joint = new Joint(jointType, jointSiteOnParent,
 				jointSiteOnChild,
 				jointOrientation);
		jointOpen = false;
		// Add the Rule lists to the Joint. For some reason, DOF_1 and DOF_2
		// constants are one greater than the index to which they actually
		// refer, so we need to subtract 1 every time.
		jointToParent = joint;
		jointOpen = false;
		
		// Add the rule list(s) to the Joint.
		for (Rule r : dof1) {
			joint.addRule(r, EnumJointType.DOF_1 - 1);
		}
		for (Rule r : dof2) {
			joint.addRule(r, EnumJointType.DOF_2 - 1);
		}
		
		// Since there's no LENGTH trait at the end, the final block is still
		// open, so it needs to be added to the list.
		body.add(new Block(indexToParent, jointToParent, length, height,
				 width));
		blockOpen = false;

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
	 * Getter for body.
	 * 
	 * @return Array of Blocks containing the body.
	 */
	public Block[] getBody() {
		return body;
	}

	/**
	 * Override of toString. Formats the returned String as the genes list
	 * enclosed in square brackets.
	 * 
	 * @return String containing genes list separated by spaces, enclosed in
	 *         curly braces:
	 *             {[(alleleA1)(alleleA2])]%n[(alleleB1)(alleleB2)] ...}.
	 */
	@Override
	public String toString() {
		StringBuilder gString = new StringBuilder("");
		gString.append('{');
		for (Gene g : chromosome) {
			gString.append(g.toString());
			gString.append(Helper.NEWLINE);
		}
		gString.deleteCharAt(gString.length() - 1);
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
		return new Genotype(this);
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
		alleles.add(new Allele(Trait.JOINT_TYPE, EnumJointType.SPHERICAL,
								0.2f));
		alleles.add(new Allele(Trait.JOINT_TYPE, EnumJointType.HINGE, 0.0f));
		// Second block.
		alleles.add(new Allele(Trait.LENGTH, 21.4f, 0.2f));
		alleles.add(new Allele(Trait.LENGTH, 20.0f, 0.199f));
		alleles.add(new Allele(Trait.HEIGHT, 40.5f, 0.1f));
		alleles.add(new Allele(Trait.HEIGHT, 45.5f, 0.4f));
		alleles.add(new Allele(Trait.WIDTH, 19.5f, 0.5f));
		alleles.add(new Allele(Trait.WIDTH, 25.5f, 0.6f));
		alleles.add(new Allele(Trait.INDEX_TO_PARENT, 0, 0.63f));
		alleles.add(new Allele(Trait.INDEX_TO_PARENT, 1, 0.4f));
		alleles.add(new Allele(Trait.JOINT_TYPE, EnumJointType.TWIST, 0.0f));
		alleles.add(new Allele(Trait.JOINT_TYPE, EnumJointType.SPHERICAL,
							   0.2f));
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
		alleles.add(new Allele(Trait.DOF_MARKER, EnumJointType.DOF_2, 0.1f));
		alleles.add(new Allele(Trait.DOF_MARKER, EnumJointType.DOF_2, 0.1f));
		alleles.add(new Allele(Trait.RULE_INPUT_A,
				new NeuronInput(EnumNeuronInputType.TIME), 0.3f));
		alleles.add(new Allele(Trait.RULE_INPUT_A, new NeuronInput(
				EnumNeuronInputType.TIME), 0.25f));
		alleles.add(new Allele(Trait.RULE_INPUT_B, new NeuronInput(
				EnumNeuronInputType.TIME), 0.3f));
		alleles.add(new Allele(Trait.RULE_INPUT_B, new NeuronInput(
				EnumNeuronInputType.TIME), 0.25f));
		alleles.add(new Allele(Trait.RULE_INPUT_C, new NeuronInput(
				EnumNeuronInputType.TIME), 0.3f));
		alleles.add(new Allele(Trait.RULE_INPUT_C, new NeuronInput(
				EnumNeuronInputType.TIME), 0.25f));
		alleles.add(new Allele(Trait.RULE_INPUT_D, new NeuronInput(
				EnumNeuronInputType.TIME), 0.3f));
		alleles.add(new Allele(Trait.RULE_INPUT_D, new NeuronInput(
				EnumNeuronInputType.TIME), 0.25f));
		alleles.add(new Allele(Trait.RULE_INPUT_E, new NeuronInput(
				EnumNeuronInputType.TIME), 0.3f));
		alleles.add(new Allele(Trait.RULE_INPUT_E, new NeuronInput(
				EnumNeuronInputType.TIME), 0.25f));
		alleles.add(new Allele(Trait.BINARY_OPERATOR_1, EnumOperatorBinary.ADD,
				0.2f));
		alleles.add(new Allele(Trait.BINARY_OPERATOR_1,
				EnumOperatorBinary.SUBTRACT, 0.1f));
		alleles.add(new Allele(Trait.UNARY_OPERATOR_2, EnumOperatorUnary.ABS,
				0.3f));
		alleles.add(new Allele(Trait.UNARY_OPERATOR_2, EnumOperatorUnary.EXP,
				0.2f));
		alleles.add(new Allele(Trait.BINARY_OPERATOR_3, EnumOperatorBinary.ADD,
				0.2f));
		alleles.add(new Allele(Trait.BINARY_OPERATOR_3,
				EnumOperatorBinary.SUBTRACT, 0.1f));
		alleles.add(new Allele(Trait.UNARY_OPERATOR_4, EnumOperatorUnary.ABS,
				0.3f));
		alleles.add(new Allele(Trait.UNARY_OPERATOR_4, EnumOperatorUnary.EXP,
				0.2f));
		
		genes = Gene.allelesToGenes(alleles);
		
		// Build some Genes from the Alleles.
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
		alleles2.add(new Allele(Trait.JOINT_TYPE,
								EnumJointType.SPHERICAL,0.0f));
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
		alleles2.add(new Allele(Trait.JOINT_TYPE, EnumJointType.TWIST, 0.0f));
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
		System.out.println("Starting crossover test.");
		Genotype[] children = crossover(genotype, genotype2);
		System.out.println("Child1 " + children[0]);
		System.out.println("Child1 Phenotype " + children[0].getPhenotype());
		System.out.println("Child2 " + children[1]);
		System.out.println("Child2 Phenotype " + children[1].getPhenotype());
	}

}
