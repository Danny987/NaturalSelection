package creature.geeksquad.hillclimbing;

import java.util.ArrayList;

import creature.geeksquad.genetics.Allele;
import creature.geeksquad.genetics.Crossover;
import creature.geeksquad.genetics.Gene;
import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.library.Helper;
import creature.phenotype.EnumJointType;
import creature.phenotype.Rule;

public class AddRule extends Strategy{

	public AddRule(Crossover crossover) {
		super(crossover);
	}

	@Override
	public Hopper climb(Hopper originalHopper) throws IllegalArgumentException,
	GeneticsException {
		
		System.out.println(1);

		//clone original hopper
		Hopper hopperToClimb = null;
		try {
			hopperToClimb = new Hopper(originalHopper);
		} catch (IllegalArgumentException | GeneticsException e) {
			System.err.println("add rule");
			throw e;
		}

		System.out.println(2);
		
		int attempts = 0;
		int DoF = 0;
		int geneIndex = 0;
		int boxIndex = 0;

		while(attempts < 10 && DoF <= 0){
			System.out.println(3);
			//current gene we're at
			geneIndex = 0;

			//get box
			boxIndex = Helper.RANDOM.nextInt(hopperToClimb.getGenotype().size()-1)+1;

			//move to the joint at the box index-------------------------------------

			//get gene list
			ArrayList<Gene> geneList = hopperToClimb.getGenotype().getChromosome();

			//move to the correct box
			geneIndex = hopperToClimb.getGenotype().findBlock(boxIndex);

			//geneIndex is now at the length allele of the correct block

			//-----------------------------------------------------------------------

			System.out.println(4);
			
			//get DoF
			//move geneIndex to the joint type allele
			while(!geneList.get(geneIndex).getDominant().getTrait().equals(Allele.Trait.JOINT_TYPE)){
				geneIndex++;
			}
			
			System.out.println(5);

			//geneIndex is now at the joint type of the block
			//get DoF
			EnumJointType joint = (EnumJointType)geneList.get(geneIndex).getDominant().getValue();
			DoF = joint.getDoF();
			
			System.out.println(6);
		}
		
		//if reached max attempts
		if(attempts > 10){
			return originalHopper;
		}
		
		System.out.println(7);

		//build rule
		

		//add n rules to start of DoF or joint
		

		//if improvement

		//keep change

		//else return original hopper





		return originalHopper;
	}

}
