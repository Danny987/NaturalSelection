package creature.geeksquad.hillclimbing;

import java.util.ArrayList;

import creature.geeksquad.genetics.Allele;
import creature.geeksquad.genetics.Gene;
import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Genotype;
import creature.geeksquad.genetics.Allele.Trait;
import creature.geeksquad.genetics.Hopper;
import creature.phenotype.Block;
import creature.phenotype.EnumJointSite;
import creature.phenotype.EnumJointType;
import creature.phenotype.EnumNeuronInputType;
import creature.phenotype.EnumOperatorBinary;
import creature.phenotype.EnumOperatorUnary;
import creature.phenotype.NeuronInput;

public class BrainTester {

	public BrainTester(){

	}

	/**
	 * @param args
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
		alleles.add(new Allele(Trait.JOINT_TYPE, EnumJointType.RIGID,
				0.2f));
		alleles.add(new Allele(Trait.JOINT_TYPE, EnumJointType.RIGID, 0.0f));
		// Second block.
		alleles.add(new Allele(Trait.LENGTH, 21.4f, 0.2f));
		alleles.add(new Allele(Trait.LENGTH, 20.0f, 0.199f));
		alleles.add(new Allele(Trait.HEIGHT, 40.5f, 0.1f));
		alleles.add(new Allele(Trait.HEIGHT, 45.5f, 0.4f));
		alleles.add(new Allele(Trait.WIDTH, 19.5f, 0.5f));
		alleles.add(new Allele(Trait.WIDTH, 25.5f, 0.6f));
		alleles.add(new Allele(Trait.INDEX_TO_PARENT, 0, 0.63f));
		alleles.add(new Allele(Trait.INDEX_TO_PARENT, 1, 0.4f));
		alleles.add(new Allele(Trait.JOINT_TYPE, EnumJointType.RIGID, 0.0f));
		alleles.add(new Allele(Trait.JOINT_TYPE, EnumJointType.RIGID,
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
		/*alleles.add(new Allele(Trait.RULE_INPUT_A,
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
				0.2f));*/

		genes = Gene.allelesToGenes(alleles);

		
		//initialize hopper to hill climb
		Hopper hopper = null;
		//initialize genotype used to create the hopper
		Genotype genotype = null;
		
		
		//create genotype and hoper
		try {
			genotype = new Genotype(genes);
			hopper = new Hopper(genotype);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneticsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//create a new brain for this tribe
		TribeBrain brain = new TribeBrain();
		
		//send hopper to hill climbing
		//returns a hill climbed hopper
		hopper = brain.performHillClimbing(hopper);
		
	}
}
