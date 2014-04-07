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
public class Genotype {
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
	private Creature phenotype;
	private Random random = Helper.RANDOM;
	
	/**
	 * Instantiate a new Genotype with random Genes.
	 * 
	 * @throws IllegalArgumentException if buildBody tried to pass an invalid
	 * 		       argument to one of the Builders' setters.
	 * @throws GeneticsException if buildBody detected an error or if too
	 *             many errors accumulated during Block creation.
	 */
	public Genotype() throws IllegalArgumentException, GeneticsException {
		chromosome = new ArrayList<Gene>();
		int numBlocks = random.nextInt(Helper.SEED_MAX_BLOCKS - 4) + 4;
		
		// Root block.
		float length = random.nextInt(Helper.SEED_MAX_SIZE - 1)
				+ random.nextFloat() + 1;
		chromosome.add(
				new Gene(
						new Allele(Trait.LENGTH, length, random.nextFloat()),
						new Allele(Trait.LENGTH, length, random.nextFloat())
						)
				);
		float height = random.nextInt(Helper.SEED_MAX_SIZE - 1)
				+ random.nextFloat() + 1;
		while (height < length / 10 || height > length * 10) {
			height = random.nextInt(Helper.SEED_MAX_SIZE - 1)
					+ random.nextFloat() + 1;
		}
		chromosome.add(
				new Gene(
						new Allele(Trait.HEIGHT, height, random.nextFloat()),
						new Allele(Trait.HEIGHT, height, random.nextFloat())
						)
				);
		float width = random.nextInt(Helper.SEED_MAX_SIZE - 1)
				+ random.nextFloat() + 1;
		while (width < length / 10 || width > length * 10
				|| width < height / 10 || width > height * 10) {
			width = random.nextInt(Helper.SEED_MAX_SIZE - 1)
					+ random.nextFloat() + 1;
		}
		chromosome.add(
				new Gene(
						new Allele(Trait.WIDTH, width, random.nextFloat()),
						new Allele(Trait.WIDTH, width, random.nextFloat())
						)
				);
		chromosome.add(
				new Gene(
						new Allele(Trait.INDEX_TO_PARENT,
								Block.PARENT_INDEX_NONE, random.nextFloat()),
						new Allele(Trait.INDEX_TO_PARENT,
								Block.PARENT_INDEX_NONE, random.nextFloat())
						)
				);
		
		// Add many blocks.
		int i = 0;
		while (i < numBlocks) {
			// Clone the current Genotype for testing.
			Genotype testGenotype = new Genotype(this);
			BlockBuilder block = makeRandomBlock(i);
			Block testBlock = block.toBlock();
			if (testBlock != null) {
				try {
					testGenotype.addBlock(testBlock);
					Creature testCreature = buildPhenotype();
					// If the test phenotype was valid, then we know it's safe
					// to add the block to *this* Genotype. Short-circuits if
					// testGenotype is null.
					if (testCreature != null && 
							testGenotype.buildPhenotype() != null) {
						addBlock(testBlock);
						i++;
					}
				} catch (IllegalArgumentException | GeneticsException ex) {
					// Prevent endless looping.
					throw new GeneticsException("Block[" + i + "]: "
							+ "random Genotype seeding failed.");
				}
			} else {
				throw new GeneticsException("Block[" + i + "]: "
							+ "random Genotype BlockBuilder was null.");
			}
		}
		phenotype = buildPhenotype();
	}

	/**
	 * Instantiate a new Genotype as a deep clone of a passed chromosome list.
	 * 
	 * @param source ArrayList<Gene> containing the chromosomes for this
	 * 			     Genotype.
	 * @throws IllegalArgumentException if buildBody tried to pass an invalid
	 * 		       argument to one of the Builders' setters.
	 * @throws GeneticsException if buildBody detected an error.
	 */
	public Genotype(ArrayList<Gene> source) throws IllegalArgumentException,
			GeneticsException {
		chromosome = new ArrayList<Gene>();
		int currentDoFs = 0;
		// Remove empty Genes and unnecessary degree of freedom markers.
		try {
			for (Gene g : source) {
				if (g != null && !g.isEmpty()) {
					Trait trait = g.getTrait();
					Object value = g.getValue();
					switch (trait) {
						case EMPTY:
							break;
						case DOF_MARKER:
							if (currentDoFs > 1) {
								chromosome.add(new Gene(g));
							}
							break;
						case JOINT_TYPE:
							currentDoFs = ((EnumJointType) value).getDoF();
							// Fall through.
						default:
							chromosome.add(new Gene(g));
					}
				}
			}
			phenotype = buildPhenotype();
		} catch (IllegalArgumentException | GeneticsException ex) {
			throw ex;
		}
	}
	
	/**
	 * Instantiate a new Genotype as a deep clone of a passed Genotype.
	 * 
	 * @param source Genotype to deep clone.
	 * @throws GeneticsException if buildBody detected an error.
	 * @throws IllegalArgumentException if buildBody tried to pass an invalid
	 * 		       argument to one of the Builders' setters.
	 */
	public Genotype(Genotype source) throws IllegalArgumentException,
			GeneticsException {
		this(source.getChromosome());
	}
	
	/**
	 * Adds a new Block with associated Joint to the end of the Genotype. It
	 * adds all necessary Genes for a Block, including the Block structure,
	 * index to parent, Joint specifications, and Joint rule table. When
	 * inserting a Block, the newly created Genes start with matched pairs of
	 * identical Alleles with their weights initialized to the current
	 * population weights for Alleles of that type.
	 * 
	 * Since the Blocks can be listed in any order, the position is optional.
	 * If not provided, it just adds the Block to the end of the Chromosome.
	 * 
	 * @param block Block to add.
	 * @param position Optional position at which to add the Block. Arguments
	 *             past the first are ignored.
	 * @return True if add successful; false if not (e.g. if position is
	 *             invalid).
	 * @throws IllegalArgumentException if one of the Builders' setters was
	 * 		       passed an invalid argument.
	 * @throws GeneticsException if buildBody detected an error.
	 */
	public boolean addBlock(Block block, int...position) throws 
			IllegalArgumentException, GeneticsException {
		int index;
		if (position.length > 0) {
			index = findBlock(position[0]);
			if (index < 0 || index >= size()) {
				return false;
			}
		} else {
			index = chromosome.size();
		}
		
		Allele length = new Allele(Trait.LENGTH, new Float(block.getLength()),
					random.nextFloat());
		Allele height = new Allele(Trait.HEIGHT, new Float(block.getHeight()),
				random.nextFloat());
		Allele width = new Allele(Trait.WIDTH, block.getWidth(),
				random.nextFloat());
		Allele indexToParent = new Allele(Trait.INDEX_TO_PARENT,
				block.getIndexOfParent(), random.nextFloat());
		Joint joint = block.getJointToParent();
		Allele jointType = new Allele(Trait.JOINT_TYPE, joint.getType(),
				random.nextFloat());
		Allele jointOrientation = new Allele(Trait.JOINT_ORIENTATION,
				joint.getOrientation(), random.nextFloat());
		Allele jointSiteOnParent = new Allele(Trait.JOINT_SITE_ON_PARENT,
				joint.getSiteOnParent(), random.nextFloat());
		Allele jointSiteOnChild = new Allele(Trait.JOINT_SITE_ON_CHILD,
				joint.getSiteOnChild(), random.nextFloat());
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
				i++;
			} catch (IllegalArgumentException ex) {
				continue;
			}
		}
		
		// We could insert these in reverse order to avoid having to change
		// index, but we'll need it to be updated for the later adds anyway.
		chromosome.add(index, new Gene(length));
		chromosome.add(++index, new Gene(height));
		chromosome.add(++index, new Gene(width));
		chromosome.add(++index, new Gene(indexToParent));
		chromosome.add(++index, new Gene(jointType));
		chromosome.add(++index, new Gene(jointOrientation));
		chromosome.add(++index, new Gene(jointSiteOnParent));
		chromosome.add(++index, new Gene(jointSiteOnChild));
		
		for (Rule r : ruleList1) {
			Allele ruleInputA = new Allele(Trait.RULE_INPUT_A, r.getInput(0),
										   random.nextFloat());
			Allele ruleInputB = new Allele(Trait.RULE_INPUT_B, r.getInput(1),
										   random.nextFloat());
			Allele ruleInputC = new Allele(Trait.RULE_INPUT_C, r.getInput(2),
										   random.nextFloat());
			Allele ruleInputD = new Allele(Trait.RULE_INPUT_D, r.getInput(3),
										   random.nextFloat());
			Allele ruleInputE = new Allele(Trait.RULE_INPUT_E, r.getInput(4),
										   random.nextFloat());
			Allele binaryOperator1 = new Allele(Trait.BINARY_OPERATOR_1,
										   r.getOp1(), random.nextFloat());
			Allele unaryOperator2 = new Allele(Trait.UNARY_OPERATOR_2,
					   					   r.getOp2(), random.nextFloat());
			Allele binaryOperator3 = new Allele(Trait.BINARY_OPERATOR_3,
					   					   r.getOp3(), random.nextFloat());
			Allele unaryOperator4 = new Allele(Trait.UNARY_OPERATOR_2,
					   					   r.getOp2(), random.nextFloat());
			
			chromosome.add(++index, new Gene(ruleInputA));
			chromosome.add(++index, new Gene(ruleInputB));
			chromosome.add(++index, new Gene(ruleInputC));
			chromosome.add(++index, new Gene(ruleInputD));
			chromosome.add(++index, new Gene(ruleInputE));
			chromosome.add(++index, new Gene(binaryOperator1));
			chromosome.add(++index, new Gene(unaryOperator2));
			chromosome.add(++index, new Gene(binaryOperator3));
			chromosome.add(++index, new Gene(unaryOperator4));
		}
		
		if (maxDoF > 1) {
			for (Rule r : ruleList2) {
				Allele ruleInputA = new Allele(Trait.RULE_INPUT_A,
						r.getInput(0), random.nextFloat());
				Allele ruleInputB = new Allele(Trait.RULE_INPUT_B,
						r.getInput(1), random.nextFloat());
				Allele ruleInputC = new Allele(Trait.RULE_INPUT_C,
						r.getInput(2), random.nextFloat());
				Allele ruleInputD = new Allele(Trait.RULE_INPUT_D,
						r.getInput(3), random.nextFloat());
				Allele ruleInputE = new Allele(Trait.RULE_INPUT_E,
						r.getInput(4), random.nextFloat());
				Allele binaryOperator1 = new Allele(Trait.BINARY_OPERATOR_1,
											   r.getOp1(), random.nextFloat());
				Allele unaryOperator2 = new Allele(Trait.UNARY_OPERATOR_2,
						   					   r.getOp2(), random.nextFloat());
				Allele binaryOperator3 = new Allele(Trait.BINARY_OPERATOR_3,
						   					   r.getOp3(), random.nextFloat());
				Allele unaryOperator4 = new Allele(Trait.UNARY_OPERATOR_2,
						   					   r.getOp2(), random.nextFloat());
				
				chromosome.add(++index, new Gene(ruleInputA));
				chromosome.add(++index, new Gene(ruleInputB));
				chromosome.add(++index, new Gene(ruleInputC));
				chromosome.add(++index, new Gene(ruleInputD));
				chromosome.add(++index, new Gene(ruleInputE));
				chromosome.add(++index, new Gene(binaryOperator1));
				chromosome.add(++index, new Gene(unaryOperator2));
				chromosome.add(++index, new Gene(binaryOperator3));
				chromosome.add(++index, new Gene(unaryOperator4));
			}
		}
		// Update the body array.
		try {
			body = buildBody();
		} catch (IllegalArgumentException | GeneticsException ex) {
			throw ex;
		}
		return true;
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
	 * @return boolean True if add succeeded; false if unsuccessful (such as
	 *                if adding to joint type that doesn't support rules).
	 * @throws IllegalArgumentException if one of the Builders' setters
	 * 		   was passed an invalid argument.
	 * @throws GeneticsException if buildBody detected an error.
	 */
	public boolean addRule(Rule rule, int block, int dof, int index) throws
			IllegalArgumentException, GeneticsException {
		if (rule == null || block >= size()) {
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
					new Allele(Trait.DOF_MARKER, 2, random.nextFloat())));
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
						   random.nextFloat());
		Allele ruleInputB = new Allele(Trait.RULE_INPUT_B, rule.getInput(1),
						   random.nextFloat());
		Allele ruleInputC = new Allele(Trait.RULE_INPUT_C, rule.getInput(2),
						   random.nextFloat());
		Allele ruleInputD = new Allele(Trait.RULE_INPUT_D, rule.getInput(3),
						   random.nextFloat());
		Allele ruleInputE = new Allele(Trait.RULE_INPUT_E, rule.getInput(4),
						   random.nextFloat());
		Allele binaryOperator1 = new Allele(Trait.BINARY_OPERATOR_1,
						   rule.getOp1(), random.nextFloat());
		Allele unaryOperator2 = new Allele(Trait.UNARY_OPERATOR_2,
						   rule.getOp2(), random.nextFloat());
		Allele binaryOperator3 = new Allele(Trait.BINARY_OPERATOR_3,
						   rule.getOp3(), random.nextFloat());
		Allele unaryOperator4 = new Allele(Trait.UNARY_OPERATOR_4,
						   rule.getOp2(), random.nextFloat());
		
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
		try {
			body = buildBody();
		} catch (IllegalArgumentException | GeneticsException ex) {
			throw ex;
		}
		
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
	 * Shift a DoF marker left or right by a specified number of Rules.
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
		if (block >= size()) {
			return -1;
		} else {
			int index = 0;
			int counter = 0;
			
			while (counter < block) {
				if (chromosome.get(++index).getTrait() == Trait.LENGTH) {
					counter++;
				}
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
	 * @throws IllegalArgumentException if phenotype is invalid; caught and
	 *             rethrown from phenotype.Creature.
	 * @throws GeneticsException if buildBody found an error.
	 */
	public Creature buildPhenotype() throws IllegalArgumentException,
			GeneticsException {
		Creature phenotype = null;
		try {
			body = buildBody();
		} catch (GeneticsException ex) {
			throw ex;
		}
		// If body is null, the Genotype is invalid.
		if (body == null) {
			throw new IllegalArgumentException(
					"Can't create phenotype; content of field body was null.");
		} else {
			Vector3 rootForwardStart = Vector3.FORWARD;
			Vector3 rootUpStart = Vector3.UP;
			// Return a new Creature (phenotype) with the calculated values.
			try {
				phenotype = new Creature(body, rootForwardStart, rootUpStart);
				return phenotype;
			} catch (IllegalArgumentException ex) {;
				throw ex;
			}
		}
	}
	
	/**
	 * Creates the Creature (phenotype) for this Genotype with custom vectors.
	 * No idea why this would ever be needed, but it's included in case.
	 * 
	 * @param rootForwardStart Vector3 a forward vector.
	 * @param rootUpStart Vector3 an up vector.
	 * @return Creature (phenotype) of the Genotype.
	 * @throws IllegalArgumentException if there was a problem creating the
	 *                                  Creature.
	 */
	public Creature buildPhenotype(Vector3 rootForwardStart,
								 Vector3 rootUpStart)
								 throws IllegalArgumentException{
		// If body is null, the Genotype is invalid.
		if (body == null) {
			return null;
		} else {
			// Return a new Creature (phenotype) with the calculated values.
			try {
				return new Creature(body, rootForwardStart, rootUpStart);
			} catch (IllegalArgumentException ex) {
				throw ex;
			}
		}
	}
	
	/**
	 * Gets the Creature phenotype for this Genotype.
	 * 
	 * @return Creature representing this Genotype's phenotype.
	 */
	public Creature getPhenotype() {
		return phenotype;
	}
	
	/**
	 * Gets the Block array representing this Genotype's body.
	 * 
	 * @return Block array representing this Genotype's body.
	 * @throws GeneticsException if parsing detected an error.
	 * @throws IllegalArgumentException if one of the Builder class setters
	 * 		       was passed an invalid argument.
	 */
	public Block[] buildBody() throws GeneticsException,
			IllegalArgumentException {
		ArrayList<Block> body = new ArrayList<Block>();
		ArrayList<Rule> dof1 = new ArrayList<Rule>();
		ArrayList<Rule> dof2 = new ArrayList<Rule>();
		int dof = EnumJointType.DOF_1;
		boolean rootFound = false;
		BlockBuilder block = new BlockBuilder();
		JointBuilder joint = new JointBuilder();
		RuleBuilder rule = new RuleBuilder();
		
		// Iterate over the chromosome and parse it into the body.
		for (int i = 0; i < chromosome.size(); i++) {
			Gene gene = chromosome.get(i);
			Trait trait = gene.getTrait();
			Object value = gene.getValue();
			
			Gene nextGene;
			Trait nextTrait;
			if (i + 1 < chromosome.size()) {
				nextGene = chromosome.get(i + 1);
				nextTrait = nextGene.getTrait();
			} else {
				nextGene = null;
				nextTrait = null;
			}
			
			switch (trait) {
				// Trait LENGTH is a special case: it marks the beginning of
				// the encoding for a new Block. Since the Block can't be
				// closed until any Joint and Rules are accounted for, this
				// Builder will remain open until the start of the next Block
				// or the end of the strand.
				case LENGTH:
					block = new BlockBuilder();
					block.setLength((Float) value);
					break;
				// Width and height.
				case HEIGHT:
					block.setHeight((Float) value);
					break;
				case WIDTH:
					block.setWidth((Float) value);
					break;
				// If the trait is INDEX_TO_PARENT, since the body must have
				// exactly one root, whose indexToParent is null, we need
				// to check if rootFound has already been set.
				case INDEX_TO_PARENT:
					int index = (Integer) value;
					// If multiple roots, Genotype is invalid. Return null.
					if (index == Block.PARENT_INDEX_NONE) {
						if (rootFound) {
							return null;
						} else {
							rootFound = true;
						}
					}
					block.setIndexToParent(index);
					break;
				// JOINT_TYPE is the first Gene of a Joint set. Like the
				// BlockBuilder, this JointBuilder will remain open until the
				// start of the next Block or the end of the strand.
				case JOINT_TYPE:
					joint = new JointBuilder();
					joint.setType((EnumJointType) value);
					break;
				// Joint orientation and sites.
				case JOINT_ORIENTATION:
					joint.setOrientation((Float) value);
					break;
				case JOINT_SITE_ON_PARENT:
					joint.setSiteOnParent((EnumJointSite) value);
					break;
				case JOINT_SITE_ON_CHILD:
					joint.setSiteOnChild((EnumJointSite) value);
					break;
				// RULE_INPUT_A marks the beginning of a new Rule definition.
				// Unlike the BlockBuilder and JointBuilder, this RuleBuilder
				// can be closed as soon as we encounter a UNARY_OPERATOR_4,
				// which marks the end of the Rule.
				case RULE_INPUT_A:
					rule = new RuleBuilder();
					rule.setNeuronInputA((NeuronInput) value);
					break;
				// Rule inputs B-E.
				case RULE_INPUT_B:
					rule.setNeuronInputB((NeuronInput) value);
					break;
				case RULE_INPUT_C:
					rule.setNeuronInputC((NeuronInput) value);
					break;
				case RULE_INPUT_D:
					rule.setNeuronInputD((NeuronInput) value);
					break;
				case RULE_INPUT_E:
					rule.setNeuronInputE((NeuronInput) value);
					break;
				// Binary and unary operators.
				case BINARY_OPERATOR_1:
					rule.setOp1((EnumOperatorBinary) value);
					break;
				case UNARY_OPERATOR_2:
					rule.setOp2((EnumOperatorUnary) value);
					break;
				case BINARY_OPERATOR_3:
					rule.setOp3((EnumOperatorBinary) value);
					break;
				// UNARY_OPERATOR_4 marks the end of a Rule, so it can close.
				case UNARY_OPERATOR_4:
					rule.setOp4((EnumOperatorUnary) value);
					Rule r = rule.toRule();
					
					if (r != null) {
						if (dof == EnumJointType.DOF_1) {
							dof1.add(r);
						} else {
							dof2.add(r);
						}
					} else {
						throw new GeneticsException(
								"Gene " + i + ": RuleBuilder was null.");
					}
					// Reset the Builder.
					rule = new RuleBuilder();
					break;
				// If we find a degree of freedom Allele, switch rules to point
				// to the second DoF list.
				case DOF_MARKER:
					int numDoFs = joint.getNumDoFs();
					// -1 is the error code for a null Joint type.
					if (numDoFs == JointBuilder.JOINT_TYPE_NULL) {
						throw new GeneticsException(
								"Gene " + i + ": found DoF marker before " +
								"assigning Joint type, or found DoF marker " +
								"for null or empty Joint");
					} else if (numDoFs < 2) {
						throw new GeneticsException(
								"Gene " + i + ": found DoF marker for " +
								"non-spherical Joint.");
					} else {
						dof = EnumJointType.DOF_2;
					}
					break;
				// Default case catches EMPTY. Empty genes aren't expressed.
				default:
					// Fall through.
			}
			
			// If this is the last Gene in the strand or the next Gene is the
			// start of a new Block, close the current Block and add it to
			// the list.
			if (nextGene == null || nextTrait == Trait.LENGTH) {
				// Get the Rules for each DoF, then clear the lists.
				for (Rule r : dof1) {
					joint.setRule(r, EnumJointType.DOF_1);
				}
				dof1.clear();
				
				if (joint.getNumDoFs() == 2) {
					for (Rule r : dof2) {
						joint.setRule(r, EnumJointType.DOF_2);
					}
				}
				dof2.clear();
				
				Joint j = joint.toJoint();
				// If j is null, check if this is the root block. If so, it's
				// fine. Otherwise, the Joint is invalid.
				if (j != null || block.isRootBlock()) {
					block.setJointToParent(j);
				} else {
					throw new GeneticsException(
							"Gene " + i + ": JointBuilder was null on a " +
							"non-root Block.");
				}
				
				Block b = block.toBlock();
				// If b is null, the Genes for the last Block were
				// invalid, so the strand is invalid.
				if (b != null) {
					body.add(b);
				} else {
					throw new GeneticsException(
							"Gene " + i + ": BlockBuilder was null.");
				}
				// Reset the Builders.
				joint = new JointBuilder();
				block = new BlockBuilder();
			}
		}

		if (!rootFound) {
			throw new GeneticsException("Didn't find root Block.");
		} else {
			return Arrays.copyOf(body.toArray(), body.size(), Block[].class);
		}
	}
	
	/**
	 * A helper method for the generic random Genotype constructor that creates
	 * a random BlockBuilder with dimensions less than 20 and index to parent
	 * of less than or equal to max.
	 * 
	 * @param index Index of this Block in the array and one more than the
	 * 		      maximum index value that's valid for the Block's parent.
	 * @return BlockBuilder for a random Block.
	 * @throws GeneticsException if random Rule generation produced too many
	 * 		       errors.
	 */
	public BlockBuilder makeRandomBlock(int index) throws GeneticsException {
		BlockBuilder blockBuilder = new BlockBuilder();
		
		// Set dimensions.
		float length = random.nextInt(Helper.SEED_MAX_SIZE - 1)
				+ random.nextFloat() + 1;
		float height = random.nextInt(Helper.SEED_MAX_SIZE)
				+ random.nextFloat() + 1;
		while (height < length / 10 || height > length * 10) {
			height = random.nextInt(Helper.SEED_MAX_SIZE - 1)
					+ random.nextFloat() + 1;
		}
		float width = random.nextInt(Helper.SEED_MAX_SIZE - 1)
				+ random.nextFloat() + 1;
		while (width < length / 10 || width > length * 10
				|| width < height / 10 || width > height * 10) {
			width = random.nextInt(Helper.SEED_MAX_SIZE)
					+ random.nextFloat() + 1;
		}
		blockBuilder.setLength(length);
		blockBuilder.setHeight(height);
		blockBuilder.setWidth(width);
		
		// By design, makeRandomJoint returns an incomplete JointBuilder without
		// its jointSiteOnParent or jointSiteOnChild fields set.
		JointBuilder jointBuilder = makeRandomJoint(index);

		/* ****************************************************************** */
		/* Make Joel give you a better way to check if a joint site is blocked.
		/* ****************************************************************** */
		int indexToParent = (index <= 1 ? 0 : random.nextInt(index - 1));
		EnumJointSite siteOnParent = EnumJointSite.values()
				[random.nextInt(EnumJointSite.values().length)];
		EnumJointSite siteOnChild = EnumJointSite.values()
				[random.nextInt(EnumJointSite.values().length)];
		/* ****************************************************************** */
		
		blockBuilder.setIndexToParent(indexToParent);
		jointBuilder.setSiteOnParent(siteOnParent);
		jointBuilder.setSiteOnChild(siteOnChild);

		// Generate random rules for any available degrees of freedom.
		int dof = jointBuilder.getNumDoFs();
		for (int i = 0; i < dof; i++) {
			int numRules = random.nextInt(10) + 1;
			int j = 0;
			int error = 0;
			while (j < numRules) {
				RuleBuilder ruleBuilder = makeRandomRule(index, dof);
				if (ruleBuilder.toRule() != null) {
					jointBuilder.setRule(ruleBuilder.toRule(), dof - 1);
					j++;
				} else {
					error++;
				}
				if (error >= Helper.FAULT_TOLERENCE) {
					throw new GeneticsException("Block[" + index + "]: "
						+ "random Rule seeding failed; errors exceed "
						+ Helper.FAULT_TOLERENCE + ".");
				}
			}
		}
		
		blockBuilder.setJointToParent(jointBuilder.toJoint());		
		
		return blockBuilder;
	}
	
	/**
	 * A helper method for the generic random Genotype constructor that creates
	 * a random JointBuilder with no rules and without its jointSiteOnParent
	 * and jointSiteOnChild fields set.
	 * 
	 * @param index Index of owner Block in the array and one more than the
	 * 		      maximum index value that's valid for the Block's parent.
	 * @return An incomplete JointBuilder for a random Joint without its
	 * 		       jointToParent or jointToChild fields set.
	 */
	public JointBuilder makeRandomJoint(int index) {
		JointBuilder jointBuilder = new JointBuilder();
		// By design, makeRandomJoint does not set its JointBuilder's
		// jointSiteOnParent or jointSiteOnChild fields.
		EnumJointType jointType = EnumJointType.values()
				[random.nextInt(EnumJointType.values().length)];
		float orientation = (float) ((random.nextFloat() * 100)
				% (2 * Math.PI));
		
		jointBuilder.setType(jointType);
		jointBuilder.setOrientation(orientation);
		
		return jointBuilder;
	}
	
	/**
	 * A helper method for the generic random Genotype constructor that creates
	 * a random RuleBuilder
	 * 
	 * @param index Index of owner Block in the array and one more than the
	 * 		      maximum index value that's valid for the Block's parent.
	 * @param dof Degree of freedom to which this Rule applies.
	 * @return A RuleBuilder for a random Rule.
	 */
	public RuleBuilder makeRandomRule(int index, int dof) {
		RuleBuilder ruleBuilder = new RuleBuilder();
		
		for (int i = 0; i < NeuronInput.TOTAL_INPUTS; i++) {
			ruleBuilder.setNeuronInput(makeRandomNeuronInput(i, dof), i);
		}
		
		EnumOperatorBinary op1 = EnumOperatorBinary.values()
				[random.nextInt(EnumOperatorBinary.values().length)];
		EnumOperatorUnary op2 = EnumOperatorUnary.values()
				[random.nextInt(EnumOperatorUnary.values().length)];
		EnumOperatorBinary op3 = EnumOperatorBinary.values()
				[random.nextInt(EnumOperatorBinary.values().length)];
		EnumOperatorUnary op4 = EnumOperatorUnary.values()
				[random.nextInt(EnumOperatorUnary.values().length)];
		
		ruleBuilder.setOp1(op1);
		ruleBuilder.setOp2(op2);
		ruleBuilder.setOp3(op3);
		ruleBuilder.setOp4(op4);
		
		return ruleBuilder;
	}
	
	/**
	 * A helper method for the generic random Genotype constructor that creates
	 * a random NeuronInput.
	 * 
	 * @param index Index of owner Block in the array and one more than the
	 * 		      maximum index value that's valid for the Block's parent.
	 * @return A random NeuronInput.
	 */
	public NeuronInput makeRandomNeuronInput(int index, int dof) {
		NeuronInput neuronInput;
		EnumNeuronInputType inputType = EnumNeuronInputType.values()
				[random.nextInt(EnumNeuronInputType.values().length)];
		
		switch (inputType) {
		case TIME:
			neuronInput = new NeuronInput(inputType);
			break;
		case CONSTANT:
			neuronInput = new NeuronInput(inputType, 
					random.nextFloat()
						+ random.nextInt(Helper.SEED_MAX_CONSTANT) - 10);
			break;
		case HEIGHT: case TOUCH:
			neuronInput = new NeuronInput(inputType, index);
			break;
		case JOINT:
			neuronInput = new NeuronInput(inputType, index, dof);
			break;
		default:
			neuronInput = null;
	}
		
		return neuronInput;
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
	public int size() {
		int size = 0;
		
		for (Gene g : chromosome) {
			if (g.getTrait() == Trait.LENGTH) {
				size++;
			}
		}
		
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
		if (gString.length() > 1) {
			gString.deleteCharAt(gString.length() - 1);			
		}
		gString.append('}');

		return gString.toString();
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
		// Block 1 (root)
		alleles.add(new Allele(Trait.LENGTH, 20.0f, 0.3f));
		alleles.add(new Allele(Trait.LENGTH, 18.0f, 0.64f));
		alleles.add(new Allele(Trait.HEIGHT, 25.0f, 0.5f));
		alleles.add(new Allele(Trait.HEIGHT, 24.0f, 0.35f));
		alleles.add(new Allele(Trait.WIDTH, 22.0f, 0.5f));
		alleles.add(new Allele(Trait.WIDTH, 23.0f, 0.35f));
		alleles.add(new Allele(Trait.INDEX_TO_PARENT, Block.PARENT_INDEX_NONE,
				               0.63f));
		alleles.add(new Allele(Trait.INDEX_TO_PARENT, 1, 0.4f));
		// Block 2
		alleles.add(new Allele(Trait.LENGTH, 10.0f, 0.2f));
		alleles.add(new Allele(Trait.LENGTH, 13.9f, 0.199f));
		alleles.add(new Allele(Trait.HEIGHT, 14.1f, 0.1f));
		alleles.add(new Allele(Trait.HEIGHT, 14.2f, 0.4f));
		alleles.add(new Allele(Trait.WIDTH, 13.9f, 0.5f));
		alleles.add(new Allele(Trait.WIDTH, 13.9f, 0.6f));
		alleles.add(new Allele(Trait.INDEX_TO_PARENT, 0, 0.63f));
		alleles.add(new Allele(Trait.INDEX_TO_PARENT, 1, 0.4f));
		alleles.add(new Allele(Trait.JOINT_TYPE, EnumJointType.TWIST, 0.0f));
		alleles.add(new Allele(Trait.JOINT_TYPE, EnumJointType.SPHERICAL,
							   0.2f));
		alleles.add(new Allele(Trait.JOINT_ORIENTATION, 0.0f, 0.5f));
		alleles.add(new Allele(Trait.JOINT_ORIENTATION, 0.0f, 0.5f));
		alleles.add(new Allele(Trait.JOINT_SITE_ON_PARENT,
				               EnumJointSite.FACE_NORTH, 0.6f));
		alleles.add(new Allele(Trait.JOINT_SITE_ON_PARENT,
	               			   EnumJointSite.VERTEX_BACK_NORTHWEST, 0.3f));
		alleles.add(new Allele(Trait.JOINT_SITE_ON_CHILD,
	               			   EnumJointSite.FACE_WEST, 0.6f));
		alleles.add(new Allele(Trait.JOINT_SITE_ON_CHILD,
    			   			   EnumJointSite.FACE_WEST, 0.7f));
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
					EnumOperatorUnary.ABS, 0.3f));
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
		Genotype genotype = null;
		try {
			genotype = new Genotype(genes);
			// addBlock test.
			Joint newJoint = new Joint(EnumJointType.RIGID,
					EnumJointSite.FACE_SOUTH, EnumJointSite.FACE_WEST,
					30.0f);
			Block newBlock = new Block(1, newJoint, 1.1f, 1.2f, 1.3f);
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
			Creature phenotype = genotype.buildPhenotype();
			System.out.println("---Genotype 1---");
			System.out.println(genotype);
			System.out.println("---Phenotype 1---");
			System.out.println(phenotype);
		} catch (IllegalArgumentException | GeneticsException ex) {
			ex.printStackTrace();
		}
		
		Genotype genotype2 = null;
		
		try {			
			// Second test creature.
			ArrayList<Allele> alleles2 = new ArrayList<Allele>();
			ArrayList<Gene> genes2;
			// Box 1 (root)
			alleles2.add(new Allele(Trait.LENGTH, 15.29f, 0.232f));
			alleles2.add(new Allele(Trait.LENGTH, 13.31f, 0.855f));
			alleles2.add(new Allele(Trait.HEIGHT, 13.35f, 0.125f));
			alleles2.add(new Allele(Trait.HEIGHT, 14.35f, 0.115f));
			alleles2.add(new Allele(Trait.WIDTH, 13.45f, 0.5f));
			alleles2.add(new Allele(Trait.WIDTH, 17.45f, 0.5f));
			alleles2.add(new Allele(Trait.INDEX_TO_PARENT,
					Block.PARENT_INDEX_NONE, 0.59f));
			alleles2.add(new Allele(Trait.INDEX_TO_PARENT,
					Block.PARENT_INDEX_NONE, 0.49f));
			// Box 2
			alleles2.add(new Allele(Trait.LENGTH, 14.45f, 0.37f));
			alleles2.add(new Allele(Trait.LENGTH, 11.43f, 0.54f));
			alleles2.add(new Allele(Trait.HEIGHT, 13.39f, 0.35f));
			alleles2.add(new Allele(Trait.HEIGHT, 16.31f, 0.36f));
			alleles2.add(new Allele(Trait.WIDTH, 17.56f, 0.5f));
			alleles2.add(new Allele(Trait.WIDTH, 15.56f, 0.5f));
			alleles2.add(new Allele(Trait.INDEX_TO_PARENT, 0, 0.1f));
			alleles2.add(new Allele(Trait.INDEX_TO_PARENT, 0, 0.433f));
			alleles2.add(new Allele(Trait.JOINT_TYPE,
					EnumJointType.HINGE,0.0f));
			alleles2.add(new Allele(Trait.JOINT_TYPE, EnumJointType.HINGE,
					0.22f));
			alleles2.add(new Allele(Trait.JOINT_ORIENTATION, 0.5f, 0.5f));
			alleles2.add(new Allele(Trait.JOINT_ORIENTATION, 0.5f, 0.5f));
			alleles2.add(new Allele(Trait.JOINT_SITE_ON_PARENT,
					EnumJointSite.EDGE_FRONT_SOUTH, 0.6f));
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
			alleles2.add(new Allele(Trait.UNARY_OPERATOR_2,
					EnumOperatorUnary.ABS, 0.3f));
			alleles2.add(new Allele(Trait.UNARY_OPERATOR_2,
					EnumOperatorUnary.EXP, 0.2f));
			alleles2.add(new Allele(Trait.BINARY_OPERATOR_3,
					EnumOperatorBinary.MULTIPLY, 0.2f));
			alleles2.add(new Allele(Trait.BINARY_OPERATOR_3,
					EnumOperatorBinary.ARCTAN2, 0.1f));
			alleles2.add(new Allele(Trait.UNARY_OPERATOR_4,
					EnumOperatorUnary.LOG, 0.3f));
			alleles2.add(new Allele(Trait.UNARY_OPERATOR_4,
					EnumOperatorUnary.SIN, 0.2f));
			// Box 3
			alleles2.add(new Allele(Trait.LENGTH, 16.45f, 0.37f));
			alleles2.add(new Allele(Trait.LENGTH, 15.29f, 0.54f));
			alleles2.add(new Allele(Trait.HEIGHT, 13.40f, 0.35f));
			alleles2.add(new Allele(Trait.HEIGHT, 21.41f, 0.36f));
			alleles2.add(new Allele(Trait.WIDTH, 25.56f, 0.5f));
			alleles2.add(new Allele(Trait.WIDTH, 21.56f, 0.5f));
			alleles2.add(new Allele(Trait.INDEX_TO_PARENT, 1, 0.59f));
			alleles2.add(new Allele(Trait.INDEX_TO_PARENT, 1, 0.49f));
			alleles2.add(new Allele(Trait.JOINT_TYPE, EnumJointType.TWIST,
					0.0f));
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
			alleles2.add(new Allele(Trait.UNARY_OPERATOR_2,
					EnumOperatorUnary.ABS, 0.3f));
			alleles2.add(new Allele(Trait.UNARY_OPERATOR_2,
					EnumOperatorUnary.EXP, 0.2f));
			alleles2.add(new Allele(Trait.BINARY_OPERATOR_3,
					EnumOperatorBinary.MULTIPLY, 0.2f));
			alleles2.add(new Allele(Trait.BINARY_OPERATOR_3,
					EnumOperatorBinary.ARCTAN2, 0.1f));
			alleles2.add(new Allele(Trait.UNARY_OPERATOR_4, 
					EnumOperatorUnary.LOG, 0.3f));
			alleles2.add(new Allele(Trait.UNARY_OPERATOR_4,
					EnumOperatorUnary.SIN, 0.2f));
			
			genes2 = Gene.allelesToGenes(alleles2);
			
			genotype2 = new Genotype(genes2);
			Creature phenotype2 = genotype2.buildPhenotype();
			System.out.println("---Genotype 2---");
			System.out.println(genotype2);
			System.out.println("---Phenotype 2---");
			System.out.println(phenotype2);
		} catch (IllegalArgumentException | GeneticsException ex) {
			ex.printStackTrace();
		}
		
		try {			
			// Crossover test.
			System.out.println("Starting crossover test.");
			
			Crossover crossover = new Crossover();
			Hopper[] children = crossover.crossover(new Hopper(genotype),
					new Hopper(genotype2),
					Strategy.RANDOM);
			System.out.println("---Child 1---");
			System.out.println(children[0]);
			System.out.println("---Child 1 Phenotype---");
			System.out.println(children[0].getGenotype().buildPhenotype());
			System.out.println("---Child 2---");
			System.out.println(children[1]);
			System.out.println("---Child 2 Phenotype---");
			System.out.println(children[1].getGenotype().buildPhenotype());
		} catch (IllegalArgumentException | GeneticsException ex) {
			ex.printStackTrace();
		}
		
		// Genotype3 is a single-block, (1, 1, 1) test Genotype.
		try {
			ArrayList<Allele> alleles3 = new ArrayList<Allele>();
			alleles3.add(new Allele(Trait.LENGTH, 1.0f, 0.37f));
			alleles3.add(new Allele(Trait.LENGTH, 1.0f, 0.54f));
			alleles3.add(new Allele(Trait.HEIGHT, 1.0f, 0.35f));
			alleles3.add(new Allele(Trait.HEIGHT, 1.0f, 0.36f));
			alleles3.add(new Allele(Trait.WIDTH, 1.0f, 0.5f));
			alleles3.add(new Allele(Trait.WIDTH, 1.0f, 0.5f));
			alleles3.add(new Allele(Trait.INDEX_TO_PARENT,
					Block.PARENT_INDEX_NONE, 0.1f));
			alleles3.add(new Allele(Trait.INDEX_TO_PARENT,
					Block.PARENT_INDEX_NONE, 0.433f));
			ArrayList<Gene> genes3 = Gene.allelesToGenes(alleles3);
			Genotype genotype3 = new Genotype(genes3);
			Creature phenotype3 = genotype3.buildPhenotype();
			System.out.println("---Genotype 3---");
			System.out.println(genotype3);
			System.out.println("---Phenotype 3---");
			System.out.println(phenotype3);
		} catch (IllegalArgumentException | GeneticsException ex) {
			ex.printStackTrace();
		}
		// NOTE: float test for NaN -> Float.isNaN(value);
	}

}
