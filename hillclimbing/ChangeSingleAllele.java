/**
 * 
 */
package creature.geeksquad.hillclimbing;

import java.util.ArrayList;
import java.util.HashMap;

import creature.geeksquad.genetics.Allele;
import creature.geeksquad.genetics.Gene;
import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Genotype;
import creature.geeksquad.genetics.Hopper;
import creature.phenotype.EnumJointSite;
import creature.phenotype.EnumOperatorBinary;
import creature.phenotype.EnumOperatorUnary;
import creature.phenotype.NeuronInput;
import creature.phenotype.Rule;

/**
 * @author Danny Gomez
 * 
 * This strategy performs a hill climb on a single allele at a time.
 * 
 * A dominant allele is chosen, and a climb is performed on it depending
 * on the type of allele.
 *
 */
public class ChangeSingleAllele extends Strategy{

	final boolean DEBUG = false;

	//a map to store the gene indices and their success probability
	HashMap<Integer, Integer> geneWeights = new HashMap<Integer, Integer>();

	public ChangeSingleAllele() {
		//TODO average number of rules in genotype
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	public Hopper climb(Hopper originalHopper) throws IllegalArgumentException,
	GeneticsException {

		float startingFitness = originalHopper.getFitness();

		//clone original hopper
		Hopper hopperToClimb = null;
		try {
			hopperToClimb = new Hopper(originalHopper);
		} catch (IllegalArgumentException | GeneticsException e) {
			System.err.println("change single allele");
			throw e;
		}

		//get the genotype from the hopper
		Genotype genotypeToClimb = hopperToClimb.getGenotype();

		if(DEBUG)System.out.println("Starting Fitness: " + hopperToClimb.getFitness());

		//pick allele based on weighted probability --------
		ArrayList<Gene> geneList = genotypeToClimb.getChromosome();

		int geneIndex = pickAllele(genotypeToClimb); //index of the allele

		Allele allele = geneList.get(geneIndex).getDominant(); //the actual allele
		//----------------------------------------------------

		if(DEBUG)System.out.println("Allele chosen to climb: " + allele);

		//check what type of climbing needs to be done. int, float, enum, etc.
		String climbType = climbTypeChooser(allele);

		if(DEBUG)System.out.println("Hill Climb Type: " + climbType);

		if(climbType == null){
			System.out.println("Sorry! Can't hill climb this type of allele yet!");
		}
		else if(climbType.equals("FLOAT") || climbType.equals("ORIENTATION")){
			climbFloatAllele(hopperToClimb, genotypeToClimb, allele, geneIndex);
		}
		else if(climbType.equals("INDEX")){
			climbIndexAllele(allele);
		}
		else if(climbType.equals("JOINT_CHILD") || climbType.equals("JOINT_PARENT")){
			climbJointSiteAllele(hopperToClimb, genotypeToClimb, allele);
		}
		else if(climbType.equals("JOINT")){
			climbJointTypeAllele(hopperToClimb, genotypeToClimb, allele, geneIndex);
		}
		else if(climbType.equals("RULE_A") || climbType.equals("RULE_B") || 
				climbType.equals("RULE_C") || climbType.equals("RULE_D") ||
				climbType.equals("RULE_E")){

			//get box index
			int boxIndex = getBoxIndex(geneList, geneIndex);
			//get rule DoF location
			int ruleDoF = getRuleDoF(geneList, geneIndex);

			climbRuleAllele(hopperToClimb, allele, climbType, boxIndex, ruleDoF);

		}
		else if(climbType.equals("BINARY_1") || climbType.equals("BINARY_3")){
			climbBinaryAllele(hopperToClimb, allele, climbType);
		}
		else if(climbType.equals("UNARY_2") || climbType.equals("UNARY_4")){
			climbUnaryAllele(hopperToClimb, allele, climbType);
		}

		float newFitness = hopperToClimb.getFitness();

		if(DEBUG)System.out.println("Allele after climb: " + allele);

		if(DEBUG)System.out.println("New Fitness: " + hopperToClimb.getFitness());

		//TODO change strategy weight map
		Hopper testHopper = null;
		try {
			testHopper = new Hopper(hopperToClimb);
		} catch (IllegalArgumentException | GeneticsException e) {
			return originalHopper;
		}

		return hopperToClimb;
	}//end climb method

	/**
	 * Climbs a float-type allele.
	 * 
	 * @param hopper - Hopper object to climb
	 * @param allele - specific allele to climb in hopper
	 */
	private void climbFloatAllele(Hopper hopper, Genotype genotype, Allele allele, int geneIndex)throws GeneticsException,
	IllegalArgumentException{

		//variable initialization
		int initialDirection = 1; //default direction is "add"
		int direction = initialDirection; //set direction

		float initialStepSize = 0.2f; //default step size, aka how much to add to value
		float stepSize = initialStepSize; //set step size

		boolean hillClimb = true; //flag to turn on and off hill climbing

		//get the largest possible value for the float
		float max;

		while(hillClimb){ //while hillclimbing flag is set to true
			//do 1 step of float hillclimbing
			max = getFloatMax(genotype, geneIndex);
			climbFloat(genotype, allele, direction, stepSize, max);

			//if improvement
			if(improved(hopper)){
				//double step size
				stepSize *= 2;
			}
			//if worse
			else{
				max = getFloatMax(genotype, geneIndex);
				climbFloat(genotype, allele, direction, -stepSize, max);
				//if last step improved
				if(stepSize > initialStepSize){
					//set step size to midpoint
					stepSize = (3*stepSize) / 4;
					//try mid point
					max = getFloatMax(genotype, geneIndex);
					climbFloat(genotype, allele, direction, stepSize, max); //add step to allele value
					//if mid point improves fitness
					if(improved(hopper)){
						//stop hillclimbing
						hillClimb = false;
					}
					//if midpoint is worse
					else{
						//go back a step
						max = getFloatMax(genotype, geneIndex);
						climbFloat(genotype, allele, direction, -stepSize, max);
						stepSize = (2*stepSize) / 3;
						hillClimb = false;
					}
				}
				//if first step and changed direction already
				else if((stepSize == initialStepSize) && (direction != 1)){
					//undo last step
					max = getFloatMax(genotype, geneIndex);
					climbFloat(genotype, allele, direction, -stepSize, max);
					//stop hillclimbing
					hillClimb = false;
				}
				//if first step and have not changed direction
				else if((stepSize == initialStepSize) && (direction == 1)){
					//undo last step
					max = getFloatMax(genotype, geneIndex);
					climbFloat(genotype, allele, direction, -stepSize, max);
					//change direction
					direction = -1;
				}
			}
		}//end while loop
	}

	//change the type of joint at this allele
	public void climbJointTypeAllele(Hopper hopper, Genotype genotype, Allele allele, int geneIndex) throws GeneticsException,
	IllegalArgumentException{
		//clone genotype
		Genotype originalValue = null;
		Genotype clonedValue = null;
		try {
			originalValue = new Genotype(genotype);
			clonedValue = new Genotype(genotype);
		}	catch (IllegalArgumentException | GeneticsException e) {
			// TODO Auto-generated catch block
			System.err.println("climbJointTypeAllele");
			throw e;
		}

		genotype.getChromosome();

		//perform the joint change to the genotype
		clonedValue = climbJointType(clonedValue, allele, geneIndex);

		genotype = clonedValue;

		if(!improved(hopper)){
			genotype = originalValue;
		}
	}

	public void climbIndexAllele(Allele allele){
	}

	public void climbJointSiteAllele(Hopper hopper, Genotype genotype, Allele allele) throws GeneticsException, 
	IllegalArgumentException{
		//original genotype clone
		Genotype originalGenotype = null;
		Genotype clonedGenotype = null;
		try {
			originalGenotype = new Genotype(genotype);
			clonedGenotype = new Genotype(genotype);
		}	catch (IllegalArgumentException | GeneticsException e) {
			System.err.println("climbJointSiteAllele");
			throw e;
		}
		//original value
		EnumJointSite originalValue = (EnumJointSite)allele.getValue();
		//cloned value
		EnumJointSite clonedValue = (EnumJointSite)allele.getValue();

		int attempts = 25;

		for(int i = 0; i < attempts; i++){
			//get the new joint site
			clonedValue = climbJointSite(clonedValue);

			//change joint site of allele to new one
			allele.setValue(clonedValue);

			try{
				genotype = new Genotype(genotype);
				break;
			}catch (IllegalArgumentException | GeneticsException e) {
				genotype = new Genotype(originalGenotype);
			}
		}

		if(!improved(hopper)){
			genotype = originalGenotype;
		}


	}

	public void climbRuleAllele(Hopper hopper, Allele allele, String climbType, int boxIndex, int ruleDoF){
		//starting neuron input
		NeuronInput originalValue = (NeuronInput)allele.getValue();
		//clone of starting neuron
		NeuronInput clonedValue = Allele.copyNeuron(originalValue);

		char ruleType = 'A';

		//figure out what type of rule the allele contains
		if(climbType.equals("RULE_A")){
			ruleType = 'A';
			//change the rule value based on rule type map
			clonedValue = climbRule(clonedValue, 'A', boxIndex, ruleDoF);
		} else if(climbType.equals("RULE_B")){
			ruleType = 'B';
			//change the rule value based on rule type map
			clonedValue = climbRule(clonedValue, 'B', boxIndex, ruleDoF);
		} else if(climbType.equals("RULE_C")){
			ruleType = 'C';
			//change the rule value based on rule type map
			clonedValue = climbRule(clonedValue, 'C', boxIndex, ruleDoF);
		} else if(climbType.equals("RULE_D")){
			ruleType = 'D';
			//change the rule value based on rule type map
			clonedValue = climbRule(clonedValue, 'D', boxIndex, ruleDoF);
		} else if(climbType.equals("RULE_E")){
			ruleType = 'E';
			//change the rule value based on rule type map
			clonedValue = climbRule(clonedValue, 'E', boxIndex, ruleDoF);
		}

		allele.setValue(clonedValue);

		//if improved, replace neuron
		if(improved(hopper)){
			allele.setValue(clonedValue);
			updateRuleMap(clonedValue, ruleType, ruleDoF, 1);
		}
		//if it doesn't improve
		else{
			//undo change
			allele.setValue(originalValue);
			updateRuleMap(clonedValue, ruleType, ruleDoF, -1);
		}

	}

	public void climbBinaryAllele(Hopper hopper, Allele allele, String climbType){
		//starting operator
		EnumOperatorBinary originalValue = (EnumOperatorBinary) allele.getValue();
		//cloned operator
		EnumOperatorBinary clonedValue = (EnumOperatorBinary) allele.getValue();

		char opType = '1';

		if(climbType.equals("BINARY_1")){
			opType = '1';
		}
		else if(climbType.equals("BINARY_3")){
			opType = '3';
		}

		clonedValue = pickBinaryValue(opType);

		allele.setValue(clonedValue);

		//if improved, replace neuron
		if(improved(hopper)){
			allele.setValue(clonedValue);
			updateBinaryMap(clonedValue, opType, 1);
		}
		//if it doesn't improve
		else{
			//undo change
			allele.setValue(originalValue);
			updateBinaryMap(clonedValue, opType, -1);
		}
	}

	public void climbUnaryAllele(Hopper hopper, Allele allele, String climbType){
		//starting operator
		EnumOperatorUnary originalValue = (EnumOperatorUnary) allele.getValue();
		//cloned operator
		EnumOperatorUnary clonedValue = (EnumOperatorUnary) allele.getValue();

		char opType = '2';

		if(climbType.equals("UNARY_2")){
			opType = '2';
		}
		else if(climbType.equals("UNARY_4")){
			opType = '4';
		}

		clonedValue = pickUnaryValue(opType);

		allele.setValue(clonedValue);

		//if improved, replace neuron
		if(improved(hopper)){
			allele.setValue(clonedValue);
			updateUnaryMap(clonedValue, opType, 1);
		}
		//if it doesn't improve
		else{
			//undo change
			allele.setValue(originalValue);
			updateUnaryMap(clonedValue, opType, -1);
		}
	}








	private int pickAllele(Genotype currentGenotype) {
		ArrayList<Gene> geneList = currentGenotype.getChromosome();

		//if gene map is empty
		if(geneWeights.isEmpty()){
			//pick a gene at random
			int geneIndex = (int)(Math.random()*geneList.size());

			return geneIndex;
		}
		//check map of probabilities to pick a gene
		//keep track of gene index for updating weights
		//get dominant allele from gene
		//return allele
		return 0;
	}

	/*
	 *  EMPTY, // E (empty Allele) 
		done LENGTH, // L (length) - float
		done WIDTH, // W (width) - float
		done HEIGHT, // H (height) - float
		INDEX_TO_PARENT, // I (index to parent) - int
		JOINT_TYPE, // T (joint Type)
		done JOINT_ORIENTATION, // O (joint orientation)
		done JOINT_SITE_ON_PARENT, // P (joint site on Parent)
		done JOINT_SITE_ON_CHILD, // C (joint site on Child)
		done RULE_INPUT_A, // a (the five inputs to a rule)
		done RULE_INPUT_B, // b (the five inputs to a rule)
		done RULE_INPUT_C, // c (the five inputs to a rule)
		done RULE_INPUT_D, // d (the five inputs to a rule)
		done RULE_INPUT_E, // e (the five inputs to a rule)
		done BINARY_OPERATOR_1, // 1 (binary operator in the 1st neuron of a rule)
		done UNARY_OPERATOR_2, // 2 (unary operator in the 1st neuron of a rule)
		done BINARY_OPERATOR_3, // 3 (binary operator in the 2nd neuron of a rule)
		done UNARY_OPERATOR_4; // 4 (unary operator in the 2nd neuron of a rule)
	 */
}
