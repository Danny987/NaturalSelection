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

import creature.geeksquad.genetics.Allele.Trait;
import creature.geeksquad.genetics.Crossover.Strategy;
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
	private int size;

	/**
	 * Instantiate a new Genotype as a deep clone of a passed chromosome list.
	 * 
	 * @param source ArrayList<Gene> containing the chromosomes for this
	 * 			     Genotype.
	 */
	public Genotype(ArrayList<Gene> source) {
		chromosome = new ArrayList<Gene>();
		try {
			for (Gene g : source) {
				if (g != null && !g.isEmpty()) {
					chromosome.add(new Gene(g));
				}
			}
			body = buildBody();
		} catch (IllegalArgumentException ex) {
			System.err.println(
					"Genotype construction error. Creation cannot continue.");
		}
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
	 * @param Strategy strategy Crossover Strategy to use.
	 * @return Two-element array of Genotypes for children. If there were
	 *         problems creating any of the genes (e.g. if the alleles didn't
	 *         trait match properly), returns null.
	 */
	public static Genotype[] crossover(Genotype parentA, Genotype parentB,
									   Strategy strategy) {
		return Crossover.crossover(parentA, parentB, strategy);
	}
	
	/**
	 * Perform crossover on two parents to create twin children.
	 * 
	 * @param Genotype[] Two-element array of Genotypes from parents.
	 * @param Strategy strategy Crossover Strategy to use.
	 * @return Two-element array of Genotypes for children. If there were
	 *         problems creating any of the genes (e.g. if the alleles didn't
	 *         trait match properly), returns null.
	 */
	public static Genotype[] crossover(Genotype[] parents, Strategy strategy) {
		return Crossover.crossover(parents[0], parents[0], strategy);
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
	 * Adds a new Block with associated Joint to the end of the Genotype. It
	 * adds all necessary Genes for a Block, including the Block structure,
	 * index to parent, Joint specifications, and Joint rule table. When
	 * inserting a Block, the newly created Genes start with matched pairs of
	 * identical Alleles with their weights initialized to the mean of 0.5f.
	 * 
	 * Since the Blocks can be listed in any order, addBlock doesn't need a
	 * position; it just adds the Block to the end of the Chromasome.
	 * 
	 * @param block Block to add.
	 */
	public void addBlock(Block block) {
		Allele length = new Allele(Trait.LENGTH, new Float(block.getLength()),
					0.5f);
		Allele height = new Allele(Trait.HEIGHT, new Float(block.getHeight()),
					0.5f);
		Allele width = new Allele(Trait.WIDTH, block.getWidth(), 0.5f);
		Allele indexToParent = new Allele(Trait.INDEX_TO_PARENT,
										  block.getIndexOfParent(), 0.5f);
		Joint joint = block.getJointToParent();
		Allele jointType = new Allele(Trait.JOINT_TYPE, joint.getType(), 0.5f);
		Allele jointOrientation = new Allele(Trait.JOINT_ORIENTATION,
											 joint.getOrientation(), 0.5f);
		Allele jointSiteOnParent = new Allele(Trait.JOINT_SITE_ON_PARENT,
											  joint.getSiteOnParent(), 0.5f);
		Allele jointSiteOnChild = new Allele(Trait.JOINT_SITE_ON_CHILD,
										     joint.getSiteOnChild(), 0.5f);
		ArrayList<Rule> ruleList1 = new ArrayList<Rule>();
		ArrayList<Rule> ruleList2 = new ArrayList<Rule>();
		int maxDoF = joint.getType().getDoF();
		
		for (int dof = 0; dof < maxDoF; dof++) {
			int i = 0;
			// Since Joint has no way to get the size of the array, we have to
			// use this annoying hack to index until we go out of bounds.
			try {
				Rule rule = joint.getRule(dof, i);
				if (dof == 0) {
					ruleList1.add(rule);
				} else {
					ruleList2.add(rule);
				}
			} catch (IllegalArgumentException ex){
				continue;
			}
		}
		
		chromosome.add(new Gene(length));
		chromosome.add(new Gene(height));
		chromosome.add(new Gene(width));
		chromosome.add(new Gene(indexToParent));
		chromosome.add(new Gene(jointType));
		chromosome.add(new Gene(jointOrientation));
		chromosome.add(new Gene(jointSiteOnParent));
		chromosome.add(new Gene(jointSiteOnChild));
		
		for (Rule r : ruleList1) {
			Allele ruleInputA = new Allele(Trait.RULE_INPUT_A, r.getInput(0),
										   0.5f);
			Allele ruleInputB = new Allele(Trait.RULE_INPUT_B, r.getInput(1),
										   0.5f);
			Allele ruleInputC = new Allele(Trait.RULE_INPUT_C, r.getInput(2),
										   0.5f);
			Allele ruleInputD = new Allele(Trait.RULE_INPUT_D, r.getInput(3),
										   0.5f);
			Allele ruleInputE = new Allele(Trait.RULE_INPUT_E, r.getInput(4),
										   0.5f);
			Allele binaryOperator1 = new Allele(Trait.BINARY_OPERATOR_1,
										   r.getOp1(), 0.5f);
			Allele unaryOperator2 = new Allele(Trait.UNARY_OPERATOR_2,
					   					   r.getOp2(), 0.5f);
			Allele binaryOperator3 = new Allele(Trait.BINARY_OPERATOR_3,
					   					   r.getOp3(), 0.5f);
			Allele unaryOperator4 = new Allele(Trait.UNARY_OPERATOR_2,
					   					   r.getOp2(), 0.5f);
			
			chromosome.add(new Gene(ruleInputA));
			chromosome.add(new Gene(ruleInputB));
			chromosome.add(new Gene(ruleInputC));
			chromosome.add(new Gene(ruleInputD));
			chromosome.add(new Gene(ruleInputE));
			chromosome.add(new Gene(binaryOperator1));
			chromosome.add(new Gene(unaryOperator2));
			chromosome.add(new Gene(binaryOperator3));
			chromosome.add(new Gene(unaryOperator4));
		}
		
		if (maxDoF > 1) {
		for (Rule r : ruleList2) {
				Allele ruleInputA = new Allele(Trait.RULE_INPUT_A,
						r.getInput(0), 0.5f);
				Allele ruleInputB = new Allele(Trait.RULE_INPUT_B,
						r.getInput(1), 0.5f);
				Allele ruleInputC = new Allele(Trait.RULE_INPUT_C,
						r.getInput(2), 0.5f);
				Allele ruleInputD = new Allele(Trait.RULE_INPUT_D,
						r.getInput(3), 0.5f);
				Allele ruleInputE = new Allele(Trait.RULE_INPUT_E,
						r.getInput(4), 0.5f);
				Allele binaryOperator1 = new Allele(Trait.BINARY_OPERATOR_1,
											   r.getOp1(), 0.5f);
				Allele unaryOperator2 = new Allele(Trait.UNARY_OPERATOR_2,
						   					   r.getOp2(), 0.5f);
				Allele binaryOperator3 = new Allele(Trait.BINARY_OPERATOR_3,
						   					   r.getOp3(), 0.5f);
				Allele unaryOperator4 = new Allele(Trait.UNARY_OPERATOR_2,
						   					   r.getOp2(), 0.5f);
				
				chromosome.add(new Gene(ruleInputA));
				chromosome.add(new Gene(ruleInputB));
				chromosome.add(new Gene(ruleInputC));
				chromosome.add(new Gene(ruleInputD));
				chromosome.add(new Gene(ruleInputE));
				chromosome.add(new Gene(binaryOperator1));
				chromosome.add(new Gene(unaryOperator2));
				chromosome.add(new Gene(binaryOperator3));
				chromosome.add(new Gene(unaryOperator4));
			}
		}
		// Update the body array.
		body = buildBody();
	}
	
	/**
	 * Remove a Block and all associated Alleles from the chromosome.
	 * 
	 * @param block Index of Block to remove.
	 */
	public void removeBlock(int block) {
		int i = findBlock(block);
		// Removes all Genes at i, letting higher-indexed ones fall into
		// position as Genes are deleted, until it reaches the start of the
		// next Block.
		while (i < chromosome.size() && 
				chromosome.get(i).getTrait() != Trait.LENGTH) {
			chromosome.remove(i);
		}
	}
	
	/**
	 * Adds a new Rule at the specified position to the rule list for Block.
	 * 
	 * @param rule Rule to add to the rule list.
	 * @param block Index of Block whose rule list should be modified.
	 * @param dof Degree of freedom to which to add the rule (assuming more
	 *            than one.
	 * @param index Position in the DoF rule list at which to add the rule.
	 * @param boolean True if add succeeded; false if unsuccessful (such as
	 *                if adding to joint type that doesn't support rules).
	 */
	public boolean addRule(Rule rule, int block, int dof, int index) {
		if (rule == null || block >= size) {
			return false;
		}
		
		// Get the insertion index.
		int i = 0;
		int maxDoF = -1;
		// Increase i over Blocks.
		for (int current = 0; current < block; ) {
			if (chromosome.get(i).getTrait() == Trait.LENGTH) {
				current++;
			}
			i++;
		}
		// Increase i over Joint.
		while (maxDoF < 0) {
			Gene gene = chromosome.get(i);
			Trait trait = gene.getTrait();
			if (trait == Trait.JOINT_TYPE) {
				maxDoF = ((EnumJointType) gene.getValue()).getDoF();
			}
			i++;
		}
		
		if (dof >= maxDoF) {
			return false;
		}
		
		// Increase i over DoF.
		if (maxDoF > 1) {
			while (chromosome.get(i).getTrait() != Trait.DOF_MARKER &&
					chromosome.get(i).getTrait() != Trait.LENGTH) {
				i++;
			}
		}
		
		// Add a DoF marker if needed.
		if (chromosome.get(i).getTrait() == Trait.LENGTH) {
			chromosome.add(--i, new Gene(
					new Allele(Trait.DOF_MARKER, 2, 0.5f)));
		}
		i++;
		
		// Increase i over rule list until index.
		for (int current = 0; current < index; ) {
			if (i > chromosome.size()) {
				return false;
			} else {
				Trait trait = chromosome.get(i).getTrait();
				if (trait == Trait.LENGTH) {
					return false;
				} else if (trait == Trait.UNARY_OPERATOR_4) {
					current++;
				}
			}
			i++;
		}
		
		Allele ruleInputA = new Allele(Trait.RULE_INPUT_A, rule.getInput(0),
						   0.5f);
		Allele ruleInputB = new Allele(Trait.RULE_INPUT_B, rule.getInput(1),
						   0.5f);
		Allele ruleInputC = new Allele(Trait.RULE_INPUT_C, rule.getInput(2),
						   0.5f);
		Allele ruleInputD = new Allele(Trait.RULE_INPUT_D, rule.getInput(3),
						   0.5f);
		Allele ruleInputE = new Allele(Trait.RULE_INPUT_E, rule.getInput(4),
						   0.5f);
		Allele binaryOperator1 = new Allele(Trait.BINARY_OPERATOR_1,
						   rule.getOp1(), 0.5f);
		Allele unaryOperator2 = new Allele(Trait.UNARY_OPERATOR_2,
						   rule.getOp2(), 0.5f);
		Allele binaryOperator3 = new Allele(Trait.BINARY_OPERATOR_3,
						   rule.getOp3(), 0.5f);
		Allele unaryOperator4 = new Allele(Trait.UNARY_OPERATOR_4,
						   rule.getOp2(), 0.5f);
		
		chromosome.add(i, new Gene(ruleInputA));
		chromosome.add(++i, new Gene(ruleInputB));
		chromosome.add(++i, new Gene(ruleInputC));
		chromosome.add(++i, new Gene(ruleInputD));
		chromosome.add(++i, new Gene(ruleInputE));
		chromosome.add(++i, new Gene(binaryOperator1));
		chromosome.add(++i, new Gene(unaryOperator2));
		chromosome.add(++i, new Gene(binaryOperator3));
		chromosome.add(++i, new Gene(unaryOperator4));
		
		// Update the body.
		body = buildBody();
		
		return true;
	}
	
	/**
	 * Remove a new Rule at the specified position from the rule list for Block.
	 * 
	 * @param block Index of Block whose rule list should be modified.
	 * @param dof Degree of freedom from which to remove the rule (if more
	 *            than one.
	 * @param index Position in the DoF rule list from which to remove the rule.
	 * @param boolean True if remove succeeded; false if unsuccessful.
	 */
	public boolean removeRule(int block, int dof, int index) {
		int i = findRule(block, dof, index);
		if (i < 0 || i + 9 >= chromosome.size()) {
			return false;
		}
		// A full Rule is nine Alleles.
		for (int j = 0; j < 9; j++) {
			Trait testTrait = chromosome.get(i).getTrait();
			if (testTrait != Trait.RULE_INPUT_A &&
					testTrait != Trait.RULE_INPUT_B &&
					testTrait != Trait.RULE_INPUT_C &&
					testTrait != Trait.RULE_INPUT_D &&
					testTrait != Trait.RULE_INPUT_E &&
					testTrait != Trait.BINARY_OPERATOR_1 &&
					testTrait != Trait.UNARY_OPERATOR_2 &&
					testTrait != Trait.BINARY_OPERATOR_3 &&
					testTrait != Trait.UNARY_OPERATOR_4) {
				return false;
			}
			// Since remove will shorten the chromosome every time, we can keep
			// removing the Gene at i and having the next Gene fall into place.
			chromosome.remove(i);
		}
		
		// Check for orphaned DoF marker.
		if (i > 0 && i < chromosome.size()) {
			if (chromosome.get(i - 1).getTrait() == Trait.DOF_MARKER
					&& chromosome.get(i).getTrait() == Trait.LENGTH) {
				chromosome.remove(i - 1);
			}
		}
		
		return true;
	}
	
	/**
	 * Shift a DoF marker left or right by a specified amount.
	 * 
	 * @param block Index of Block whose DoF marker should be shifted.
	 * @param amount Number of rules to shift the marker (left is negative).
	 * @return True if shift successful, false otherwise (such as if there is
	 *              no DoF marker for the indicated Block or if shift would
	 *              place DoF marker at an invalid position in the table).
	 */
	public boolean shiftDoF(int block, int amount) {
		int dir = (amount < 0 ? -1 : 1);
		// The shift has to happen in nine-Allele jumps since it takes nine to
		// express a full Rule.
		int interval = dir * 9;
		int i = findBlock(block);
		
		while (chromosome.get(i).getTrait() != Trait.DOF_MARKER) {
			// Short-circuits if i exceeds chromosome size, preventing out-of-
			// bounds from chromosome.get.
			if (++i >= chromosome.size() || chromosome.get(i).getTrait()
					== Trait.LENGTH) {
				return false;
			}
		}
		
		int j = 0;
		while (j <= Math.abs(amount)) {
			// Short-circuits if j exceeds chromosome size.
			if (j >= chromosome.size() || chromosome.get(j).getTrait()
					!= Trait.RULE_INPUT_A) {
				return false;
			} else if (j == Math.abs(amount) && chromosome.get(j).getTrait()
					   == Trait.RULE_INPUT_A) {
				Gene marker = chromosome.get(i);
				chromosome.remove(i);
				chromosome.add(j, marker);
				return true;
			} else {
				j += interval;
			}
		}
		
		// If we get this far, the shift failed.
		return false;
	}
	
	/**
	 * Get the chromosome index of the starting Allele of a Block.
	 * 
	 * @param block Index of Block to locate.
	 * @return Chromosome index of the Block's LENGTH-Trait Allele or -1 if
	 *                    index is invalid.
	 */
	public int findBlock(int block) {
		if (block > size) {
			return -1;
		} else {
			int index = 0;
			int counter = 0;
			
			while (counter < block) {
				if (chromosome.get(index).getTrait() == Trait.LENGTH) {
					counter++;
				}
				index++;
			}
			
			return index;
		}
	}
	
	/**
	 * Get the index of the starting Allele of a Rule of a Block.
	 * 
	 * @param block Index of Block to locate.
	 * @param dof Which degree-of-freedom to check.
	 * @param rule Index of Rule to locate.
	 * @return Chromosome index of Rule's RULE_INPUT_A-Trait Allele or -1 if
	 *                    index is invalid.
	 */
	public int findRule(int block, int dof, int rule) {
		// Increase i over Blocks.
		int i = findBlock(block);
		
		if (i < 0) {
			return -1;
		}
		
		// Increase i to Joint type.
		while (chromosome.get(i).getTrait() != Trait.JOINT_TYPE) {
			i++;
		}
		
		// Increase i over DoF if necessary and possible.
		if (dof >= ((EnumJointType) chromosome.get(i).getValue()).getDoF()) {
			return -1;
		} else {
			if (dof > 1) {
				while (chromosome.get(i).getTrait() != Trait.DOF_MARKER
						&& chromosome.get(i).getTrait() != Trait.LENGTH) {
					i++;
				}
			}
		}
		
		// Add a DoF marker if needed.
		if (chromosome.get(i).getTrait() == Trait.LENGTH) {
			chromosome.add(--i, new Gene(
					new Allele(Trait.DOF_MARKER, 2, 0.5f)));
		}
		
		// Increase i over Rule list until rule index is reached..
		for (int current = 0; current < rule; ) {
			if (++i > chromosome.size()) {
				return -1;
			} else {
				Trait trait = chromosome.get(i).getTrait();
				if (trait == Trait.LENGTH) {
					return -1;
				} else if (trait == Trait.BINARY_OPERATOR_1) {
					current++;
				}
			}
		}
		
		return i;
	}

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
		int numBlocks = 0;
		ArrayList<Block> blocks = new ArrayList<Block>();
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
						// Close the open rule.
						if (ruleOpen) {
							rules.add(rule);
						}
						// Close the open joint.
						if (jointOpen) {
							Joint joint = new Joint(jointType, 
													jointSiteOnParent,
						             				jointSiteOnChild,
						             				jointOrientation);
							// Add the currently open Rule list to the Joint.
						if (jointType.getDoF() > 0) {
							for (Rule r : dof1) {
								if (r != null) {
									joint.addRule(r, EnumJointType.DOF_1 - 1);
								}
							}
							if (jointType.getDoF() > 1) {
								for (Rule r : dof2) {
									if (r != null) {
										joint.addRule(r,
												EnumJointType.DOF_2 - 1);
									}
								}
							}
						}
							jointToParent = joint;
						}
						
						Block block = new Block(indexToParent, jointToParent,
								 				length, height, width);
						blocks.add(block);
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
				        dof1.clear();
				        dof2.clear();
				        rules = dof1;
					}
					jointOpen = false;
					ruleOpen = false;
					blockOpen = true;
					length = (Float) value;
					numBlocks++;
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
					if (!rootFound && indexToParent ==
							Block.PARENT_INDEX_NONE) {
						rootFound = true;
					} else if (rootFound && indexToParent ==
							Block.PARENT_INDEX_NONE) {
						// If multiple roots, Genotype is invalid. Return null.
						return null;
					}
					break;
				// JOINT_TYPE is the first Gene of a Joint set, so we need to
				// close the previous Joint and add it before we continue.
				case JOINT_TYPE:
					if (jointOpen) {
						if (ruleOpen) {
							rules.add(rule);
						}
						Joint joint = new Joint(jointType, jointSiteOnParent,
					             				jointSiteOnChild,
					             				jointOrientation);
						// Add the Rule tables to the Joint
						if (jointType.getDoF() > 0) {
							for (Rule r : dof1) {
								if (r != null) {
									joint.addRule(r, EnumJointType.DOF_1 - 1);
								}
							}
							if (jointType.getDoF() > 1) {
								for (Rule r : dof2) {
									if (r != null) {
										joint.addRule(r,
												EnumJointType.DOF_2 - 1);
									}
								}
							}
						}
						dof1.clear();
						dof2.clear();
						rules = dof1;
						jointToParent = joint;
						jointOpen = false;
					}
					ruleOpen = false;
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
		
		// The final Block and Joint should always be open at the end.
		if (jointOpen) {
			Joint joint = new Joint(jointType, jointSiteOnParent,
 				jointSiteOnChild, jointOrientation);
			jointOpen = false;
			// Add the Rule lists to the Joint. For some reason, DOF_1 and DOF_2
			// constants are one greater than the index to which they actually
			// refer, so we need to subtract 1 every time.
			jointToParent = joint;
			jointOpen = false;
			
			if (jointType.getDoF() > 0) {
				// Add the rule list(s) to the Joint.
				for (Rule r : dof1) {
					if (r != null) {
						joint.addRule(r, EnumJointType.DOF_1 - 1);
					}
				}
				if (jointType.getDoF() > 1) {
					for (Rule r : dof2) {
						if (r != null) {
							joint.addRule(r, EnumJointType.DOF_2 - 1);
						}
					}
				}
			}
		}	

		// Since there's no LENGTH trait at the end, the final block should
		// still be open, so it needs to be added to the list.
		if (blockOpen) {
			blocks.add(new Block(indexToParent, jointToParent, length, height,
					 width));
			blockOpen = false;
		}

		// A final check to confirm that the root block was found.
		if (!rootFound) {
			return null;
		} else {
			size = numBlocks;
			return Arrays.copyOf(blocks.toArray(), blocks.size(), Block[].class);
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
	 * Getter for the size of the body (in blocks).
	 * 
	 * @return Size of the body (in blocks).
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * A static debugging method for printing out a formatted chromosome list.
	 * 
	 * @param chromosome ArrayList<Gene> to print out.
	 */
	public static void printChromosome(ArrayList<Gene> chromosome) {
		for (Gene g : chromosome) {
			System.out.println(g);
		}
	}

	/**
	 * Override of toString. Formats the returned String as the genes list
	 * enclosed in square brackets.
	 * 
	 * @return String containing Genes list separated by spaces, enclosed in
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
	 * 
	 * @param args Command-line arguments.
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
		
		// addBlock test.
		Joint newJoint = new Joint(EnumJointType.RIGID,
				EnumJointSite.FACE_SOUTH, EnumJointSite.EDGE_BACK_NORTH,
				30.0f);
		Block newBlock = new Block(1, newJoint, 1.0f, 2.0f, 3.0f);
		genotype.addBlock(newBlock);
		
		// addRule test.
		Rule newRule = new Rule();
		newRule.setInput(new NeuronInput(EnumNeuronInputType.TIME), 0);
		newRule.setInput(new NeuronInput(EnumNeuronInputType.TIME), 1);
		newRule.setInput(new NeuronInput(EnumNeuronInputType.TIME), 2);
		newRule.setInput(new NeuronInput(EnumNeuronInputType.TIME), 3);
		newRule.setInput(new NeuronInput(EnumNeuronInputType.TIME), 4);
		newRule.setOp1(EnumOperatorBinary.ADD);
		newRule.setOp2(EnumOperatorUnary.ABS);
		newRule.setOp3(EnumOperatorBinary.ADD);
		newRule.setOp4(EnumOperatorUnary.ABS);
		genotype.addRule(newRule, 1, 0, 0);
		
		System.out.println("---Genotype1---");
		System.out.println(genotype);
		Creature phenotype = genotype.getPhenotype();
		System.out.println("---Phenotype1---");
		System.out.println(phenotype);
		
		// Second test creature.
		ArrayList<Allele> alleles2 = new ArrayList<Allele>();
		ArrayList<Gene> genes2;
		// Box 1
		alleles2.add(new Allele(Trait.LENGTH, 45.4f, 0.37f));
		alleles2.add(new Allele(Trait.LENGTH, 29.3f, 0.54f));
		alleles2.add(new Allele(Trait.HEIGHT, 40.5f, 0.35f));
		alleles2.add(new Allele(Trait.HEIGHT, 41.5f, 0.36f));
		alleles2.add(new Allele(Trait.WIDTH, 56.5f, 0.5f));
		alleles2.add(new Allele(Trait.WIDTH, 56.5f, 0.5f));
		alleles2.add(new Allele(Trait.INDEX_TO_PARENT, 1, 0.1f));
		alleles2.add(new Allele(Trait.INDEX_TO_PARENT, 1, 0.433f));
		alleles2.add(new Allele(Trait.JOINT_TYPE,
				EnumJointType.HINGE,0.0f));
		alleles2.add(new Allele(Trait.JOINT_TYPE, EnumJointType.HINGE,
				0.22f));
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
		// Box 2 (root)
		alleles2.add(new Allele(Trait.LENGTH, 20.4f, 0.232f));
		alleles2.add(new Allele(Trait.LENGTH, 21.3f, 0.855f));
		alleles2.add(new Allele(Trait.HEIGHT, 60.0f, 0.125f));
		alleles2.add(new Allele(Trait.HEIGHT, 60.0f, 0.115f));
		alleles2.add(new Allele(Trait.WIDTH, 19.5f, 0.5f));
		alleles2.add(new Allele(Trait.WIDTH, 19.4f, 0.5f));
		alleles2.add(new Allele(Trait.INDEX_TO_PARENT, Block.PARENT_INDEX_NONE,
				0.59f));
		alleles2.add(new Allele(Trait.INDEX_TO_PARENT, Block.PARENT_INDEX_NONE,
				0.49f));
		// Box 3
		alleles2.add(new Allele(Trait.LENGTH, 45.4f, 0.37f));
		alleles2.add(new Allele(Trait.LENGTH, 29.3f, 0.54f));
		alleles2.add(new Allele(Trait.HEIGHT, 40.5f, 0.35f));
		alleles2.add(new Allele(Trait.HEIGHT, 41.5f, 0.36f));
		alleles2.add(new Allele(Trait.WIDTH, 56.5f, 0.5f));
		alleles2.add(new Allele(Trait.WIDTH, 56.5f, 0.5f));
		alleles2.add(new Allele(Trait.INDEX_TO_PARENT, 1, 0.59f));
		alleles2.add(new Allele(Trait.INDEX_TO_PARENT, 1, 0.49f));
		alleles2.add(new Allele(Trait.JOINT_TYPE, EnumJointType.TWIST, 0.0f));
		alleles2.add(new Allele(Trait.JOINT_TYPE,
							    EnumJointType.HINGE, 0.22f));
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
		System.out.println("---Genotype2---");
		System.out.println(genotype2);
		Creature phenotype2 = genotype2.getPhenotype();
		System.out.println("---Phenotype2---");
		System.out.println(phenotype2);
		
		// Crossover test.
		System.out.println("Starting crossover test.");
		Genotype[] children = crossover(genotype, genotype2, Strategy.RANDOM);
		System.out.println("---Child1---");
		System.out.println(children[0]);
		System.out.println("---Child1 Phenotype---");
		System.out.println(children[0].getPhenotype());
		System.out.println("---Child2---");
		System.out.println(children[1]);
		System.out.println("---Child2 Phenotype---");
		System.out.println(children[1].getPhenotype());
	}

}
