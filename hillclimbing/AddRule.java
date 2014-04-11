package creature.geeksquad.hillclimbing;


import java.util.ArrayList;

import creature.geeksquad.genetics.Allele;
import creature.geeksquad.genetics.Crossover;
import creature.geeksquad.genetics.Gene;
import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Genotype;
import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.genetics.Allele.Trait;
import creature.geeksquad.library.Helper;
import creature.phenotype.EnumJointType;
import creature.phenotype.EnumNeuronInputType;
import creature.phenotype.EnumOperatorBinary;
import creature.phenotype.EnumOperatorUnary;
import creature.phenotype.NeuronInput;
import creature.phenotype.Rule;

public class AddRule extends Strategy{

	public AddRule(MapHandler mapHandler) {
		super(mapHandler);
	}

	@Override
	public Hopper climb(Hopper hopper) throws IllegalArgumentException,
	GeneticsException {

		//clone original hopper
		Hopper hopperToClimb = null;
		try {
			hopperToClimb = new Hopper(hopper);
		} catch (IllegalArgumentException | GeneticsException e) {
			System.err.println("add block");
			throw e;
		}

		//go through the genotype and make sure that there are joints
		//that can accept new rules. Only non-rigid joints can have rules
		//added to them.

		int numOfValidJoints = 0;

		for(int i = 0; i < hopperToClimb.getChromosome().size(); i++){
			if(getDomAllele(hopperToClimb, i).getTrait().equals(Allele.Trait.JOINT_TYPE)){
				if(!getDomAllele(hopperToClimb, i).getValue().equals(EnumJointType.RIGID)){
					numOfValidJoints++;
				}
			}
		}

<<<<<<< HEAD
		System.err.println("numOfValidJoints = " + numOfValidJoints);
=======
		//System.out.println("numOfValidJoints = " + numOfValidJoints);
>>>>>>> 3cfb4d3fad1674403c254897552b0cece7fa3420

		//if there are no valid joints to add rules to, return original hopper
		if(numOfValidJoints == 0){
			return hopper;
		}

		//pick a random valid joint to add rules to
		int jointToAddRules = Helper.RANDOM.nextInt(numOfValidJoints)+1;

		//get the geneIndex of the joint
		int counter = 0;
		int geneIndex = 0;
		for(int i = 0; i < hopperToClimb.getChromosome().size(); i++){
			if(getDomAllele(hopperToClimb, i).getTrait().equals(Allele.Trait.JOINT_TYPE)){
				if(!getDomAllele(hopperToClimb, i).getValue().equals(EnumJointType.RIGID)){
					counter++;
				}
			}
			if(counter == jointToAddRules){
				geneIndex = i;
<<<<<<< HEAD
=======
				//System.out.println(getDomAllele(hopperToClimb, geneIndex));
>>>>>>> 3cfb4d3fad1674403c254897552b0cece7fa3420
				break;
			}
		}

<<<<<<< HEAD
		System.err.println("Adding rules to this joint: " + getDomAllele(hopperToClimb, geneIndex));
		
=======
>>>>>>> 3cfb4d3fad1674403c254897552b0cece7fa3420
		//geneIndex is now at the joint that we're going to add rules to
		//but first, we need to decide which DoF we're going to add to.

		EnumJointType jointType = (EnumJointType)getDomAllele(hopperToClimb, geneIndex).getValue();
		int totalDoF = jointType.getDoF();

		//pick a random DoF to add the rules to
		int dofToAddRules = Helper.RANDOM.nextInt(totalDoF)+1;

		//now we'll need the actual geneList from the hopper
		ArrayList<Gene> geneList = hopperToClimb.getChromosome();

		//move to the correct spot
		if(dofToAddRules == 1){
			//move to rule type A
			while(!geneList.get(geneIndex).getTrait().equals(Allele.Trait.RULE_INPUT_A)
					&& !geneList.get(geneIndex).getTrait().equals(Allele.Trait.LENGTH)
					&& geneIndex < geneList.size()){
				geneIndex++;
			}
		}
		else if(dofToAddRules == 2){
			//move to dof marker
			while(!geneList.get(geneIndex).getTrait().equals(Allele.Trait.DOF_MARKER)){
				//System.out.println(geneList.get(geneIndex).getDominant());
				geneIndex++;
			}
			//System.out.println(geneList.get(geneIndex).getDominant());
			geneIndex++;
		}

		int boxIndex = getBoxIndex(hopperToClimb, geneIndex);


		//geneIndex is now at the location to add rules in
		int rulesLength = Helper.RANDOM.nextInt(Helper.SEED_MAX_CONSTANT+1);
		for(int k = 0; k < rulesLength; k++){
			Allele allele1;
			Allele allele2;
			//a
			allele1 = new Allele(Trait.RULE_INPUT_A, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
			allele1 = new Allele(allele1.getTrait(),
					replaceNeuron((NeuronInput)allele1.getValue(), 'A', boxIndex, dofToAddRules), 
					weightHelper.weight());

			allele2 = new Allele(Trait.RULE_INPUT_A, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
			allele2 = new Allele(allele2.getTrait(), 
					replaceNeuron((NeuronInput)allele2.getValue(), 'A', boxIndex, dofToAddRules), weightHelper.weight());

			geneList.add(geneIndex, new Gene(allele1, allele2));
			geneIndex++;
			//b
			allele1 = new Allele(Trait.RULE_INPUT_B, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
			allele1 = new Allele(allele1.getTrait(),
					replaceNeuron((NeuronInput)allele1.getValue(), 'B', boxIndex, dofToAddRules), weightHelper.weight());

			allele2 = new Allele(Trait.RULE_INPUT_B, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
			allele2 = new Allele(allele2.getTrait(), 
					replaceNeuron((NeuronInput)allele2.getValue(), 'B', boxIndex, dofToAddRules), weightHelper.weight());

			geneList.add(geneIndex, new Gene(allele1, allele2));
			geneIndex++;
			//c
			allele1 = new Allele(Trait.RULE_INPUT_C, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
			allele1 = new Allele(allele1.getTrait(),
					replaceNeuron((NeuronInput)allele1.getValue(), 'C', boxIndex, dofToAddRules), weightHelper.weight());

			allele2 = new Allele(Trait.RULE_INPUT_C, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
			allele2 = new Allele(allele2.getTrait(), 
					replaceNeuron((NeuronInput)allele2.getValue(), 'C', boxIndex, dofToAddRules), weightHelper.weight());

			geneList.add(geneIndex, new Gene(allele1, allele2));
			geneIndex++;
			//d
			allele1 = new Allele(Trait.RULE_INPUT_D, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
			allele1 = new Allele(allele1.getTrait(),
					replaceNeuron((NeuronInput)allele1.getValue(), 'D', boxIndex, dofToAddRules), weightHelper.weight());

			allele2 = new Allele(Trait.RULE_INPUT_D, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
			allele2 = new Allele(allele2.getTrait(), 
					replaceNeuron((NeuronInput)allele2.getValue(), 'D', boxIndex, dofToAddRules), weightHelper.weight());

			geneList.add(geneIndex, new Gene(allele1, allele2));
			geneIndex++;
			//e
			allele1 = new Allele(Trait.RULE_INPUT_E, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
			allele1 = new Allele(allele1.getTrait(),
					replaceNeuron((NeuronInput)allele1.getValue(), 'E', boxIndex, dofToAddRules), weightHelper.weight());

			allele2 = new Allele(Trait.RULE_INPUT_E, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
			allele2 = new Allele(allele2.getTrait(), 
					replaceNeuron((NeuronInput)allele2.getValue(), 'E', boxIndex, dofToAddRules), weightHelper.weight());

			geneList.add(geneIndex, new Gene(allele1, allele2));
			geneIndex++;
			//bin1
			allele1 = new Allele(Trait.BINARY_OPERATOR_1, EnumOperatorBinary.ADD, weightHelper.weight());
			allele1 = new Allele(allele1.getTrait(), 
					mapHandler.getNewBinary('1'), weightHelper.weight());

			allele2 = new Allele(Trait.BINARY_OPERATOR_1, EnumOperatorBinary.ADD, weightHelper.weight());
			allele2 = new Allele(allele2.getTrait(), 
					mapHandler.getNewBinary('1'), weightHelper.weight());

			geneList.add(geneIndex, new Gene(allele1, allele2));
			geneIndex++;
			//un2
			allele1 = new Allele(Trait.UNARY_OPERATOR_2, EnumOperatorUnary.ABS, weightHelper.weight());
			allele1 = new Allele(allele1.getTrait(), 
					mapHandler.getNewUnary('2'), weightHelper.weight());

			allele2 = new Allele(Trait.UNARY_OPERATOR_2, EnumOperatorUnary.ABS, weightHelper.weight());
			allele2 = new Allele(allele2.getTrait(), 
					mapHandler.getNewUnary('2'), weightHelper.weight());

			geneList.add(geneIndex, new Gene(allele1, allele2));
			geneIndex++;
			//bin3
			allele1 = new Allele(Trait.BINARY_OPERATOR_3, EnumOperatorBinary.ADD, weightHelper.weight());
			allele1 = new Allele(allele1.getTrait(),
					mapHandler.getNewBinary('3'), weightHelper.weight());

			allele2 = new Allele(Trait.BINARY_OPERATOR_3, EnumOperatorBinary.ADD, weightHelper.weight());
			allele2 = new Allele(allele2.getTrait(),
					mapHandler.getNewBinary('3'), weightHelper.weight());

			geneList.add(geneIndex, new Gene(allele1, allele2));
			geneIndex++;
			//un4
			allele1 = new Allele(Trait.UNARY_OPERATOR_4, EnumOperatorUnary.ABS, weightHelper.weight());
			allele1 = new Allele(allele1.getTrait(),
					mapHandler.getNewUnary('4'), weightHelper.weight());

			allele2 = new Allele(Trait.UNARY_OPERATOR_4, EnumOperatorUnary.ABS, weightHelper.weight());
			allele2 = new Allele(allele2.getTrait(),
					mapHandler.getNewUnary('4'), weightHelper.weight());

			geneList.add(geneIndex, new Gene(allele1, allele2));
		}

		Hopper temp = null;
		Genotype genotype = null;
		try{
			genotype = new Genotype(geneList);
			temp = new Hopper(genotype);
			return temp;
		}catch (IllegalArgumentException | GeneticsException e) {
			return hopper;
		}
	}
}
