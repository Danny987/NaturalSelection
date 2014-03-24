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
		// If body is null, the Genotype is invalid.
		if (body == null) {
			return null;
		} else {
			Vector3 rootForwardStart = Vector3.FORWARD;
			Vector3 rootUpStart = Vector3.UP;
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
			
			switch(trait) {
				// Trait LENGTH is a special case: it marks the beginning of
				// the encoding for a new Block, so close construction of the
				// previous block and add it to the list.
				case LENGTH:
					if (blockCount > 0) {
						Block block = new Block(indexToParent, jointToParent,
								 				length, width, height);						
						body.add(block);
						jointsThisBlock = 0;
						//
						// TODO
						//
					}
					blockCount++;
					length = (Float) value;
					break;
				// Width.
				case WIDTH:
					width = (Float) value;
					break;
				// Height.
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
					//
					// TODO
					//
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
				// Joint orientation.
				case JOINT_ORIENTATION:
					jointOrientation = (Float) value;
					break;
				// Joint site on parent.
				case JOINT_SITE_ON_PARENT:
					jointSiteOnParent = (EnumJointSite) value;
					break;
				// Joint site on child.
				case JOINT_SITE_ON_CHILD:
					jointSiteOnChild = (EnumJointSite) value;
					break;
				// Rule input A marks the beginning of a new Rule definition.
				// Like Block and Joint, the old one needs to be closed first.
				case RULE_INPUT_A:
					//
					// TODO
					//
					if (rulesThisJoint > 0) {
						rules.add(rule);
					}
					rule = new Rule();
					rulesThisJoint++;
					ruleInputA = (NeuronInput) value;
					rule.setInput(ruleInputA, 0);
					break;
				// Rule input B.
				case RULE_INPUT_B:
					ruleInputB = (NeuronInput) value;
					rule.setInput(ruleInputB, 1);
					break;
				// Rule input C.
				case RULE_INPUT_C:
					ruleInputC = (NeuronInput) value;
					rule.setInput(ruleInputC, 2);
					break;
				// Rule input D.
				case RULE_INPUT_D:
					ruleInputD = (NeuronInput) value;
					rule.setInput(ruleInputD, 3);
					break;
				// Rule input E.
				case RULE_INPUT_E:
					ruleInputE = (NeuronInput) value;
					rule.setInput(ruleInputE, 4);
					break;
				// Binary operator 1.
				case BINARY_OPERATOR_1:
					binaryOperator1 = (EnumOperatorBinary) value;
					rule.setOp1(binaryOperator1);						
					break;
				// Unary operator 2.
				case UNARY_OPERATOR_2:
					unaryOperator2 = (EnumOperatorUnary) value;
					rule.setOp2(unaryOperator2);
					break;
				// Binary operator 3.
				case BINARY_OPERATOR_3:
					binaryOperator3 = (EnumOperatorBinary) value;
					rule.setOp3(binaryOperator3);
					break;
				// Unary operator 4.
				case UNARY_OPERATOR_4:
					unaryOperator4 = (EnumOperatorUnary) value;
					rule.setOp4(unaryOperator4);
					break;
				// Default case catches EMPTY. EMPTY genes aren't expressed.
				default:
					break;
			}
		}
		//
		// TODO close everything else
		//
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
		java.util.ArrayList<Allele> alleles = new java.util.ArrayList<Allele>();
		java.util.ArrayList<Gene> genes = new java.util.ArrayList<Gene>();
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
		
		// Build some Genes from the Alleles.
		for (int i = 0; i < alleles.size(); i++) {
			genes.add(new Gene(alleles.get(i), alleles.get(++i)));
		}
		
		// Create a Genotype from the Genes.
		Genotype genotype = new Genotype(genes);
		System.out.println("Genotype " + genotype);
		Creature phenotype = genotype.getPhenotype();
		System.out.println("Phenotype " + phenotype);
	}

}
