package creature.geeksquad.hillclimbing;


import creature.geeksquad.genetics.Allele;
import creature.geeksquad.genetics.Crossover;
import creature.geeksquad.genetics.Gene;
import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.library.Helper;
import creature.phenotype.EnumJointType;
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

		//System.out.println("numOfValidJoints = " + numOfValidJoints);

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
				//System.out.println(getDomAllele(hopperToClimb, geneIndex));
				break;
			}
		}
		
		//geneIndex is now at the joint that we're going to add rules to
		//but first, we need to decide which DoF we're going to add to.
		
		EnumJointType jointType = (EnumJointType) getDomAllele(hopperToClimb, geneIndex).getValue();
		int totalDoF = jointType.getDoF();
		
		//pick a random DoF to add the rules to
		int dofToAddRules = Helper.RANDOM.nextInt(totalDoF)+1;
		
		//now we'll need the actual geneList from the hopper
		//ArrayList<Genes> geneList
		
		//move to the correct spot
		if(dofToAddRules == 1){
			//move to rule type A
			
		}


		return hopper;
	}

}
