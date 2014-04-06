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
import creature.geeksquad.genetics.Allele.Trait;
import creature.phenotype.Creature;
import creature.phenotype.EnumJointSite;
import creature.phenotype.EnumJointType;
import creature.phenotype.EnumNeuronInputType;
import creature.phenotype.EnumOperatorBinary;
import creature.phenotype.EnumOperatorUnary;
import creature.phenotype.NeuronInput;
import creature.geeksquad.library.*;

/**
 * @author Daniel
 *
 */
public abstract class Strategy {

	Genotype currentGenotype;
	Genotype newGenotype;

	//allele location - allele weight
	HashMap<Integer,Integer> alleleWeights = new HashMap<Integer,Integer>();

	//rule neuron input maps
	HashMap<EnumNeuronInputType, Integer> aRuleWeights1 = new HashMap<EnumNeuronInputType, Integer>();
	HashMap<EnumNeuronInputType, Integer> bRuleWeights1 = new HashMap<EnumNeuronInputType, Integer>();
	HashMap<EnumNeuronInputType, Integer> cRuleWeights1 = new HashMap<EnumNeuronInputType, Integer>();
	HashMap<EnumNeuronInputType, Integer> dRuleWeights1 = new HashMap<EnumNeuronInputType, Integer>();
	HashMap<EnumNeuronInputType, Integer> eRuleWeights1 = new HashMap<EnumNeuronInputType, Integer>();

	HashMap<EnumNeuronInputType, Integer> aRuleWeights2 = new HashMap<EnumNeuronInputType, Integer>();
	HashMap<EnumNeuronInputType, Integer> bRuleWeights2 = new HashMap<EnumNeuronInputType, Integer>();
	HashMap<EnumNeuronInputType, Integer> cRuleWeights2 = new HashMap<EnumNeuronInputType, Integer>();
	HashMap<EnumNeuronInputType, Integer> dRuleWeights2 = new HashMap<EnumNeuronInputType, Integer>();
	HashMap<EnumNeuronInputType, Integer> eRuleWeights2 = new HashMap<EnumNeuronInputType, Integer>();

	//binary enum maps
	HashMap<EnumOperatorBinary, Integer> binaryOneWeights = new HashMap<EnumOperatorBinary, Integer>();
	HashMap<EnumOperatorBinary, Integer> binaryThreeWeights = new HashMap<EnumOperatorBinary, Integer>();

	//unary enum maps
	HashMap<EnumOperatorUnary, Integer> unaryTwoWeights = new HashMap<EnumOperatorUnary, Integer>();
	HashMap<EnumOperatorUnary, Integer> unaryFourWeights = new HashMap<EnumOperatorUnary, Integer>();


	public Strategy(){
		//initialize all the probability maps
		initializeMaps();
	}

	/**
	 * This methods takes in an allele, gets its trait value
	 * and returns it as a string.
	 * 
	 * Used to determine which hill climbing to perform.
	 * 
	 * @param allele - Allele to extract a trait from.
	 * @return String - type of trait extracted from the allele.
	 */
	public String climbTypeChooser(Allele allele){
		//get the trait from the allele
		Allele.Trait trait = allele.getTrait();

		// Switch to determine what string to return based on the trait.
		switch (trait) {
		case HEIGHT: case WIDTH: case LENGTH:
			return "FLOAT"; //EXCEPTION
			//return "INDEX";

		case INDEX_TO_PARENT:
			return "INDEX";

		case JOINT_SITE_ON_CHILD:
			return "JOINT_CHILD"; //EXCEPTION
			//return "INDEX";

		case JOINT_SITE_ON_PARENT:
			return "JOINT_PARENT"; //EXCEPTION
			//return "INDEX";

		case JOINT_TYPE:
			EnumJointType j = (EnumJointType) allele.getValue();
			return "JOINT";
			//return "INDEX";

		case JOINT_ORIENTATION:
			//return "ORIENTATION"; //EXCEPTION
			return "INDEX";

		case RULE_INPUT_A:
			return "RULE_A";
			//return "INDEX";

		case RULE_INPUT_B:
			return "RULE_B";
			//return "INDEX";

		case RULE_INPUT_C:
			return "RULE_C";
			//return "INDEX";

		case RULE_INPUT_D:
			return "RULE_D";
			//return "INDEX";

		case RULE_INPUT_E:
			return "RULE_E";
			//return "INDEX";

		case BINARY_OPERATOR_1: 
			return "BINARY_1";
			//return "INDEX";

		case BINARY_OPERATOR_3:
			return "BINARY_3";
			//return "INDEX";

		case UNARY_OPERATOR_2:
			return "UNARY_2";
			//return "INDEX";

		case UNARY_OPERATOR_4:
			return "UNARY_4";
			//return "INDEX";

		default:
			return null;
		}
	}

	/**
	 * Performs a climb on a float-based allele.
	 * 
	 * @param allele - allele to change
	 * @param direction - add or subtract from value
	 * @param stepSize - how much to add or subtract from value
	 */
	public void climbFloat(Genotype genotype, Allele allele, int direction, float stepSize, float max)
			throws GeneticsException, IllegalArgumentException{

		Genotype clonedGenotype = null;
		try {
			clonedGenotype = new Genotype(genotype);
		} catch (IllegalArgumentException | GeneticsException e) {
			//invalid creature came into climbFloat
			return;
		}

		//get allele value
		float f = (Float) allele.getValue();

		//what to change the allele to
		float newValue = f + (stepSize*direction);

		//if max is 0 or if newValue less than max
		if(max == 0 || newValue < max){
			//add step to allele value
			allele.setValue(newValue);
		}

		Genotype validGenotype = null;
		try {
			validGenotype = new Genotype(genotype);
		} catch (IllegalArgumentException | GeneticsException e) {
			//the float climb invalidated the creature, undo
			genotype = clonedGenotype;
		}
	}

	/**
	 * Performs a climb on a rule-based allele.
	 * Takes in a neuron input, returns a different neuron input based on
	 * probability/weight maps.
	 * 
	 * @param neuron - starting neuron input
	 * @param ruleType - type of neuron input, A,B,C,D,E
	 * @param boxIndex - box index of the neuron rule allele
	 * @return neuron - the changed neuron
	 */
	public NeuronInput climbRule(NeuronInput neuron, char ruleType, int boxIndex, int ruleDoF){
		//get a new rule from the maps based on the rule type
		EnumNeuronInputType newRuleValue = pickRuleValue(ruleType, ruleDoF);

		if(neuron.getType().equals(newRuleValue)){
			newRuleValue = pickRuleValue(ruleType, ruleDoF);
		}

		//check what neuron input was obtained from the maps and return it
		if(newRuleValue.equals(EnumNeuronInputType.TIME)){
			return new NeuronInput(newRuleValue); //return new neuron
		}
		else if(newRuleValue.equals(EnumNeuronInputType.HEIGHT) || newRuleValue.equals(EnumNeuronInputType.TOUCH)){
			return new NeuronInput(newRuleValue, boxIndex);
		}
		else if(newRuleValue.equals(EnumNeuronInputType.CONSTANT)){
			//System.out.println("Can't climb rule CONSTANT yet!");
		}
		else if(newRuleValue.equals(EnumNeuronInputType.JOINT)){
			return new NeuronInput(newRuleValue, boxIndex, neuron.getDOF());
		}

		//return starting neuron if no change occurs
		return neuron;
	}

	public EnumJointSite climbJointSite(EnumJointSite clonedJointSite){
		EnumJointSite jointSite = clonedJointSite;

		while(jointSite == clonedJointSite){
			jointSite = EnumJointSite.values()
					[Helper.RANDOM.nextInt(EnumJointSite.values().length)];
		}
		return jointSite;
	}

	public Genotype climbJointType(Genotype genotype, Allele allele, int geneIndex){
		EnumJointType startingJointType = (EnumJointType)allele.getValue();
		EnumJointType jointType = startingJointType;

		while(jointType == startingJointType){
			jointType = EnumJointType.values()
					[Helper.RANDOM.nextInt(EnumJointType.values().length)];
		}

		allele.setValue(jointType);

		ArrayList<Gene> geneList = genotype.getChromosome();

		int boxIndex = getBoxIndex(geneList, geneIndex);

		if(jointType.getDoF() == 0){
			//remove rules
			int i = geneIndex + 4; //go to first rule
			while(i < geneList.size() && 
					!geneList.get(i).getDominant().getTrait().equals(Allele.Trait.LENGTH)){
				geneList.remove(i); //remove gene at index
			}

		}
		else if(startingJointType.getDoF() == 2 && jointType.getDoF() == 1){
			//remove 1 set of rules

			//index to the closest dof marker or length allele
			int i = geneIndex;
			while(i < geneList.size() &&
					(!geneList.get(i).getDominant().getTrait().equals(Allele.Trait.DOF_MARKER) ||
							!geneList.get(i).getDominant().getTrait().equals(Allele.Trait.LENGTH))){
				if (geneList.get(i).getDominant().getTrait().equals(Allele.Trait.DOF_MARKER)) {
					break;
				}
				i++; //go to next index
			}
			//at the dof marker or length
			//remove up to the next length allele
			while(i < geneList.size() && 
					!geneList.get(i).getDominant().getTrait().equals(Allele.Trait.LENGTH)){
				geneList.remove(i); //remove gene at index
			}
		}
		else{
			//add rules
			int rulesLength = 0; //number of rule sets to add per DoF
			int n = jointType.getDoF() - startingJointType.getDoF(); //how many DoFs need rules
			int i = geneIndex;

			//move the index to where we need to add the rules
			while(i < geneList.size() && 
					!geneList.get(i).getDominant().getTrait().equals(Allele.Trait.LENGTH)){
				i++;
			}

			if(startingJointType.getDoF() == 1 && jointType.getDoF() == 2){
				//add a DoF marker
				Allele allele1 = new Allele(Trait.DOF_MARKER, EnumJointType.DOF_2, 0.1f);
				geneList.add(i, new Gene(allele1));
				i++;
			}

			for(int j = 0; j < n; j++){

				if(j != 0){ //if adding a second DoF
					//add a dof marker
					Allele allele1 = new Allele(Trait.DOF_MARKER, EnumJointType.DOF_2, 0.1f);
					geneList.add(i, new Gene(allele1));
					i++;
				}

				rulesLength = Helper.RANDOM.nextInt(Helper.SEED_MAX_CONSTANT+1);
				for(int k = 0; k < rulesLength; k++){
					Allele allele1;
					Allele allele2;
					//a
					allele1 = new Allele(Trait.RULE_INPUT_A, new NeuronInput(EnumNeuronInputType.TIME), 0.5f);
					allele1.setValue(climbRule((NeuronInput)allele1.getValue(), 'A', boxIndex, j+1));
					allele2 = new Allele(Trait.RULE_INPUT_A, new NeuronInput(EnumNeuronInputType.TIME), 0.5f);
					allele2.setValue(climbRule((NeuronInput)allele2.getValue(), 'A', boxIndex, j+1));
					geneList.add(i, new Gene(allele1, allele2));
					i++;
					//b
					allele1 = new Allele(Trait.RULE_INPUT_B, new NeuronInput(EnumNeuronInputType.TIME), 0.5f);
					allele1.setValue(climbRule((NeuronInput)allele1.getValue(), 'B', boxIndex, j+1));
					allele2 = new Allele(Trait.RULE_INPUT_B, new NeuronInput(EnumNeuronInputType.TIME), 0.5f);
					allele2.setValue(climbRule((NeuronInput)allele2.getValue(), 'B', boxIndex, j+1));
					geneList.add(i, new Gene(allele1, allele2));
					i++;
					//c
					allele1 = new Allele(Trait.RULE_INPUT_C, new NeuronInput(EnumNeuronInputType.TIME), 0.5f);
					allele1.setValue(climbRule((NeuronInput)allele1.getValue(), 'C', boxIndex, j+1));
					allele2 = new Allele(Trait.RULE_INPUT_C, new NeuronInput(EnumNeuronInputType.TIME), 0.5f);
					allele2.setValue(climbRule((NeuronInput)allele2.getValue(), 'C', boxIndex, j+1));
					geneList.add(i, new Gene(allele1, allele2));
					i++;
					//d
					allele1 = new Allele(Trait.RULE_INPUT_D, new NeuronInput(EnumNeuronInputType.TIME), 0.5f);
					allele1.setValue(climbRule((NeuronInput)allele1.getValue(), 'D', boxIndex, j+1));
					allele2 = new Allele(Trait.RULE_INPUT_D, new NeuronInput(EnumNeuronInputType.TIME), 0.5f);
					allele2.setValue(climbRule((NeuronInput)allele2.getValue(), 'D', boxIndex, j+1));
					geneList.add(i, new Gene(allele1, allele2));
					i++;
					//e
					allele1 = new Allele(Trait.RULE_INPUT_E, new NeuronInput(EnumNeuronInputType.TIME), 0.5f);
					allele1.setValue(climbRule((NeuronInput)allele1.getValue(), 'E', boxIndex, j+1));
					allele2 = new Allele(Trait.RULE_INPUT_E, new NeuronInput(EnumNeuronInputType.TIME), 0.5f);
					allele2.setValue(climbRule((NeuronInput)allele2.getValue(), 'E', boxIndex, j+1));
					geneList.add(i, new Gene(allele1, allele2));
					i++;
					//bin1
					allele1 = new Allele(Trait.BINARY_OPERATOR_1, EnumOperatorBinary.ADD, 0.5f);
					allele1.setValue(pickBinaryValue('1'));
					allele2 = new Allele(Trait.BINARY_OPERATOR_1, EnumOperatorBinary.ADD, 0.5f);
					allele2.setValue(pickBinaryValue('1'));
					geneList.add(i, new Gene(allele1, allele2));
					i++;
					//un2
					allele1 = new Allele(Trait.UNARY_OPERATOR_2, EnumOperatorUnary.ABS, 0.3f);
					allele1.setValue(pickUnaryValue('2'));
					allele2 = new Allele(Trait.UNARY_OPERATOR_2, EnumOperatorUnary.ABS, 0.3f);
					allele2.setValue(pickUnaryValue('2'));
					geneList.add(i, new Gene(allele1, allele2));
					i++;
					//bin3
					allele1 = new Allele(Trait.BINARY_OPERATOR_3, EnumOperatorBinary.ADD, 0.5f);
					allele1.setValue(pickBinaryValue('3'));
					allele2 = new Allele(Trait.BINARY_OPERATOR_3, EnumOperatorBinary.ADD, 0.5f);
					allele2.setValue(pickBinaryValue('3'));
					geneList.add(i, new Gene(allele1, allele2));
					i++;
					//un4
					allele1 = new Allele(Trait.UNARY_OPERATOR_4, EnumOperatorUnary.ABS, 0.3f);
					allele1.setValue(pickUnaryValue('4'));
					allele2 = new Allele(Trait.UNARY_OPERATOR_4, EnumOperatorUnary.ABS, 0.3f);
					allele2.setValue(pickUnaryValue('4'));
					geneList.add(i, new Gene(allele1, allele2));
					i++;
				}
			}
		}
		return genotype;
	}

	/*public void climbInt(Allele allele){
		int i = (Integer) allele.getValue();
	}*/

	/**
	 * This method compares the new and current fitness and returns
	 * whether the new fitness is an improvement over the old fitness.
	 * 
	 * If the fitness is an improvement, the hopper current fitness is updated.
	 * 
	 * @param hopper - Hopper creature to check improvement on.
	 * @return True if fitness has improved. False if it has not improved.
	 */
	public boolean improved(Hopper hopper){
		//run simulation to get new fitness
		float newFitness = getNewFitness(hopper);
		//if the fitness from the sim is greater than the current fitness
		if(newFitness > hopper.getFitness()){
			//update hopper current fitness
			System.err.println("SUCCESS: " + hopper.getFitness() + " --> " + newFitness);
			hopper.setFitness(newFitness);
			return true;
		}
		return false;
	}

	/**
	 * Main climbing method. Starts the hill climbing process.
	 * 
	 * @param startingHopper - Starting/Non-hill climbed hopper.
	 * @return Improved, hill climbed hopper.
	 * @throws GeneticsException 
	 * @throws IllegalArgumentException 
	 */
	public abstract Hopper climb(Hopper startingHopper) throws 
	IllegalArgumentException, GeneticsException;

	/**
	 * Runs the simulation on a hopper.
	 * Returns the highest fitness from the simulation.
	 * 
	 * @param hopper - hopper to run fitness test on
	 * @return float - highest fitness from simulation
	 */
	public float getNewFitness(Hopper hopper){
		//generate a phenotype from the creature
		Creature phenotype = hopper.getPhenotype();

		//initialize fitness
		float highestFitness = 0;
		//float fitness = 0;

		//run the simulation
		/*for (int i = 0; i < 20; i++) {
			fitness = phenotype.advanceSimulation();
			if(fitness > highestFitness){
				highestFitness = fitness;
			}
		}*/
		//return highestFitness;
		return hopper.evalFitness();
	}

	//initialize the the weight maps
	public void initializeMaps(){

		//neuron input maps
		aRuleWeights1.put(EnumNeuronInputType.CONSTANT, 1);
		aRuleWeights1.put(EnumNeuronInputType.HEIGHT, 1);
		aRuleWeights1.put(EnumNeuronInputType.JOINT, 1);
		aRuleWeights1.put(EnumNeuronInputType.TIME, 1);
		aRuleWeights1.put(EnumNeuronInputType.TOUCH, 1);

		bRuleWeights1.put(EnumNeuronInputType.CONSTANT, 1);
		bRuleWeights1.put(EnumNeuronInputType.HEIGHT, 1);
		bRuleWeights1.put(EnumNeuronInputType.JOINT, 1);
		bRuleWeights1.put(EnumNeuronInputType.TIME, 1);
		bRuleWeights1.put(EnumNeuronInputType.TOUCH, 1);

		cRuleWeights1.put(EnumNeuronInputType.CONSTANT, 1);
		cRuleWeights1.put(EnumNeuronInputType.HEIGHT, 1);
		cRuleWeights1.put(EnumNeuronInputType.JOINT, 1);
		cRuleWeights1.put(EnumNeuronInputType.TIME, 1);
		cRuleWeights1.put(EnumNeuronInputType.TOUCH, 1);

		dRuleWeights1.put(EnumNeuronInputType.CONSTANT, 1);
		dRuleWeights1.put(EnumNeuronInputType.HEIGHT, 1);
		dRuleWeights1.put(EnumNeuronInputType.JOINT, 1);
		dRuleWeights1.put(EnumNeuronInputType.TIME, 1);
		dRuleWeights1.put(EnumNeuronInputType.TOUCH, 1);

		eRuleWeights1.put(EnumNeuronInputType.CONSTANT, 1);
		eRuleWeights1.put(EnumNeuronInputType.HEIGHT, 1);
		eRuleWeights1.put(EnumNeuronInputType.JOINT, 1);
		eRuleWeights1.put(EnumNeuronInputType.TIME, 1);
		eRuleWeights1.put(EnumNeuronInputType.TOUCH, 1);

		aRuleWeights2.put(EnumNeuronInputType.CONSTANT, 1);
		aRuleWeights2.put(EnumNeuronInputType.HEIGHT, 1);
		aRuleWeights2.put(EnumNeuronInputType.JOINT, 1);
		aRuleWeights2.put(EnumNeuronInputType.TIME, 1);
		aRuleWeights2.put(EnumNeuronInputType.TOUCH, 1);

		bRuleWeights2.put(EnumNeuronInputType.CONSTANT, 1);
		bRuleWeights2.put(EnumNeuronInputType.HEIGHT, 1);
		bRuleWeights2.put(EnumNeuronInputType.JOINT, 1);
		bRuleWeights2.put(EnumNeuronInputType.TIME, 1);
		bRuleWeights2.put(EnumNeuronInputType.TOUCH, 1);

		cRuleWeights2.put(EnumNeuronInputType.CONSTANT, 1);
		cRuleWeights2.put(EnumNeuronInputType.HEIGHT, 1);
		cRuleWeights2.put(EnumNeuronInputType.JOINT, 1);
		cRuleWeights2.put(EnumNeuronInputType.TIME, 1);
		cRuleWeights2.put(EnumNeuronInputType.TOUCH, 1);

		dRuleWeights2.put(EnumNeuronInputType.CONSTANT, 1);
		dRuleWeights2.put(EnumNeuronInputType.HEIGHT, 1);
		dRuleWeights2.put(EnumNeuronInputType.JOINT, 1);
		dRuleWeights2.put(EnumNeuronInputType.TIME, 1);
		dRuleWeights2.put(EnumNeuronInputType.TOUCH, 1);

		eRuleWeights2.put(EnumNeuronInputType.CONSTANT, 1);
		eRuleWeights2.put(EnumNeuronInputType.HEIGHT, 1);
		eRuleWeights2.put(EnumNeuronInputType.JOINT, 1);
		eRuleWeights2.put(EnumNeuronInputType.TIME, 1);
		eRuleWeights2.put(EnumNeuronInputType.TOUCH, 1);

		//binary operator maps
		binaryOneWeights.put(EnumOperatorBinary.ADD, 1);
		binaryOneWeights.put(EnumOperatorBinary.SUBTRACT, 1);
		binaryOneWeights.put(EnumOperatorBinary.MULTIPLY, 1);
		binaryOneWeights.put(EnumOperatorBinary.POWER, 1);
		binaryOneWeights.put(EnumOperatorBinary.MAX, 1);
		binaryOneWeights.put(EnumOperatorBinary.MIN, 1);
		binaryOneWeights.put(EnumOperatorBinary.ARCTAN2, 1);

		binaryThreeWeights.put(EnumOperatorBinary.ADD, 1);
		binaryThreeWeights.put(EnumOperatorBinary.SUBTRACT, 1);
		binaryThreeWeights.put(EnumOperatorBinary.MULTIPLY, 1);
		binaryThreeWeights.put(EnumOperatorBinary.POWER, 1);
		binaryThreeWeights.put(EnumOperatorBinary.MAX, 1);
		binaryThreeWeights.put(EnumOperatorBinary.MIN, 1);
		binaryThreeWeights.put(EnumOperatorBinary.ARCTAN2, 1);

		//unary operator maps
		unaryTwoWeights.put(EnumOperatorUnary.ABS, 1);
		unaryTwoWeights.put(EnumOperatorUnary.IDENTITY, 1);
		unaryTwoWeights.put(EnumOperatorUnary.SIN, 1);
		unaryTwoWeights.put(EnumOperatorUnary.SIGN, 1);
		unaryTwoWeights.put(EnumOperatorUnary.NEGATIVE, 1);
		unaryTwoWeights.put(EnumOperatorUnary.LOG, 1);
		unaryTwoWeights.put(EnumOperatorUnary.EXP, 1);

		unaryFourWeights.put(EnumOperatorUnary.ABS, 1);
		unaryFourWeights.put(EnumOperatorUnary.IDENTITY, 1);
		unaryFourWeights.put(EnumOperatorUnary.SIN, 1);
		unaryFourWeights.put(EnumOperatorUnary.SIGN, 1);
		unaryFourWeights.put(EnumOperatorUnary.NEGATIVE, 1);
		unaryFourWeights.put(EnumOperatorUnary.LOG, 1);
		unaryFourWeights.put(EnumOperatorUnary.EXP, 1);

	}

	/**
	 * Gets the box index of a specific gene in a genotype.
	 * @param genotype - Genotype that contains the gene.
	 * @param geneIndex - location of the gene to check
	 * @return int - box index of the gene
	 */
	public int getBoxIndex(ArrayList<Gene> geneList, int geneIndex){
		int blockCount = 0;

		for(int i = 0; i <= geneIndex; i++){
			if(geneList.get(i).getTrait().equals(Allele.Trait.LENGTH))
				blockCount++;
		}
		return blockCount;
	}

	/** Calculates the maximum float value for a dimension of the box that
	 * the given allele is in.
	 * 
	 * @param genotype
	 * @param allele
	 * @param geneIndex
	 * @return
	 */
	public float getFloatMax(Genotype genotype, int geneIndex){
		float currentVal = 0;
		float val1 = 0;
		float val2 = 0;

		ArrayList<Gene> geneList = genotype.getChromosome();

		currentVal = (float)geneList.get(geneIndex).getValue();

		//if allele is not a dimension, return 0
		if(geneList.get(geneIndex).getTrait().equals(Allele.Trait.JOINT_ORIENTATION)){
			return 0;
		}


		//length, height, width
		if(geneList.get(geneIndex).getTrait().equals(Allele.Trait.LENGTH)){
			val1 = (float)geneList.get(geneIndex+1).getValue();
			val2 = (float)geneList.get(geneIndex+2).getValue();
		}
		else if(geneList.get(geneIndex).getTrait().equals(Allele.Trait.HEIGHT)){
			val1 = (float)geneList.get(geneIndex-1).getValue();
			val2 = (float)geneList.get(geneIndex+1).getValue();

		}
		else if(geneList.get(geneIndex).getTrait().equals(Allele.Trait.WIDTH)){
			val1 = (float)geneList.get(geneIndex-1).getValue();
			val2 = (float)geneList.get(geneIndex-2).getValue();
		}

		//return the smallest value
		if(currentVal < val1 && currentVal < val2){
			return 0;
		}
		else if(val1 < currentVal && val1 < val2){
			return val1*10;
		}
		else if(val2 < currentVal && val2 < val1){
			return val2*10;
		}

		return 0;
	}

	/**
	 * Given a chromosome and index, locates which DoF the index is in.
	 * 
	 * @param geneList ArrayList of gene, aka the chromosome
	 * @param geneIndex location of given gene
	 * @return dof that the geneIndex is in
	 */
	public int getRuleDoF(ArrayList<Gene> geneList, int geneIndex){
		//go backwards through the gene list
		for(int i = geneIndex; i >= 0; i--){
			//if we get to a joint type allele
			if(geneList.get(i).getTrait().equals(Allele.Trait.JOINT_TYPE)){
				return 1; //index was in DoF 1
			}
			//if we get to a DoF marker
			if(geneList.get(i).getTrait().equals(Allele.Trait.DOF_MARKER)){
				return 2; //index was in DoF 2
			}
		}
		return 0;
	}

	/**
	 * Given a rule type (A,B,C,D,E), return a neuron input that might
	 * work for that rule type (using probability maps).
	 * 
	 * @param ruleType - type of rule, A,B,C,D,E
	 * @return EnumNeuronInputType - neuron input gotten from the maps
	 */
	public EnumNeuronInputType pickRuleValue(char ruleType, int ruleDoF){
		//initialize the map that will be used to get the neuron input
		HashMap<EnumNeuronInputType, Integer> weightMap = null;

		//set the weight map to the appropriate rule type map based on the
		//rule type based in.
		if(ruleType == 'A'){
			if(ruleDoF == 1) weightMap = aRuleWeights1;
			else if(ruleDoF == 2) weightMap = aRuleWeights2;
		}
		else if(ruleType == 'B'){
			if(ruleDoF == 1) weightMap = bRuleWeights1;
			else if(ruleDoF == 2) weightMap = bRuleWeights2;
		}
		else if(ruleType == 'C'){
			if(ruleDoF == 1 ) weightMap = cRuleWeights1;
			else if(ruleDoF == 2) weightMap = cRuleWeights2;
		}
		else if(ruleType == 'D'){
			if(ruleDoF == 1) weightMap = dRuleWeights1;
			else if(ruleDoF == 2) weightMap = dRuleWeights2;
		}
		else if(ruleType == 'E'){
			if(ruleDoF == 1) weightMap = eRuleWeights1;
			else if(ruleDoF == 2) weightMap = eRuleWeights2;
		}

		//pick a neuron type based on weights
		int weightSum = 0;

		//get sum of weights in map
		weightSum += weightMap.get(EnumNeuronInputType.CONSTANT);
		weightSum += weightMap.get(EnumNeuronInputType.HEIGHT);
		weightSum += weightMap.get(EnumNeuronInputType.JOINT);
		weightSum += weightMap.get(EnumNeuronInputType.TIME);
		weightSum += weightMap.get(EnumNeuronInputType.TOUCH);

		int choice = Helper.RANDOM.nextInt(weightSum);
		int subTotal = 0;

		//pick strategy based on weight
		subTotal += weightMap.get(EnumNeuronInputType.CONSTANT);
		if(choice < subTotal)
			return EnumNeuronInputType.CONSTANT;
		subTotal += weightMap.get(EnumNeuronInputType.HEIGHT);
		if(choice < subTotal)
			return EnumNeuronInputType.HEIGHT;
		subTotal += weightMap.get(EnumNeuronInputType.JOINT);
		if(choice < subTotal)
			return EnumNeuronInputType.JOINT;
		subTotal += weightMap.get(EnumNeuronInputType.TIME);
		if(choice < subTotal)
			return EnumNeuronInputType.TIME;
		subTotal += weightMap.get(EnumNeuronInputType.TOUCH);
		if(choice < subTotal)
			return EnumNeuronInputType.TOUCH;

		//if we got here, I broke it
		return null;
	}

	public EnumOperatorBinary pickBinaryValue(char opType){
		HashMap<EnumOperatorBinary, Integer> weightMap = null;

		if(opType == '1') weightMap = binaryOneWeights;
		else if(opType == '3') weightMap = binaryThreeWeights;

		int weightSum = 0;

		weightSum += weightMap.get(EnumOperatorBinary.ADD);
		weightSum += weightMap.get(EnumOperatorBinary.SUBTRACT);
		weightSum += weightMap.get(EnumOperatorBinary.MULTIPLY);
		weightSum += weightMap.get(EnumOperatorBinary.POWER);
		weightSum += weightMap.get(EnumOperatorBinary.MAX);
		weightSum += weightMap.get(EnumOperatorBinary.MIN);
		weightSum += weightMap.get(EnumOperatorBinary.ARCTAN2);

		int choice = Helper.RANDOM.nextInt(weightSum);
		int subTotal = 0;

		subTotal += weightMap.get(EnumOperatorBinary.ADD);
		if(choice < subTotal)
			return EnumOperatorBinary.ADD;
		subTotal += weightMap.get(EnumOperatorBinary.SUBTRACT);
		if(choice < subTotal)
			return EnumOperatorBinary.SUBTRACT;
		subTotal += weightMap.get(EnumOperatorBinary.MULTIPLY);
		if(choice < subTotal)
			return EnumOperatorBinary.MULTIPLY;
		subTotal += weightMap.get(EnumOperatorBinary.POWER);
		if(choice < subTotal)
			return EnumOperatorBinary.POWER;
		subTotal += weightMap.get(EnumOperatorBinary.MAX);
		if(choice < subTotal)
			return EnumOperatorBinary.MAX;
		subTotal += weightMap.get(EnumOperatorBinary.MIN);
		if(choice < subTotal)
			return EnumOperatorBinary.MIN;
		subTotal += weightMap.get(EnumOperatorBinary.ARCTAN2);
		if(choice < subTotal)
			return EnumOperatorBinary.ARCTAN2;

		//if we got here, it's broken
		return null;
	}

	public EnumOperatorUnary pickUnaryValue(char opType){
		HashMap<EnumOperatorUnary, Integer> weightMap = null;

		if(opType == '2') weightMap = unaryTwoWeights;
		else if(opType == '4') weightMap = unaryFourWeights;

		int weightSum = 0;

		weightSum += weightMap.get(EnumOperatorUnary.ABS);
		weightSum += weightMap.get(EnumOperatorUnary.EXP);
		weightSum += weightMap.get(EnumOperatorUnary.IDENTITY);
		weightSum += weightMap.get(EnumOperatorUnary.LOG);
		weightSum += weightMap.get(EnumOperatorUnary.NEGATIVE);
		weightSum += weightMap.get(EnumOperatorUnary.SIGN);
		weightSum += weightMap.get(EnumOperatorUnary.SIN);

		int choice = Helper.RANDOM.nextInt(weightSum);
		int subTotal = 0;

		subTotal += weightMap.get(EnumOperatorUnary.ABS);
		if(choice < subTotal)
			return EnumOperatorUnary.ABS;
		subTotal += weightMap.get(EnumOperatorUnary.EXP);
		if(choice < subTotal)
			return EnumOperatorUnary.EXP;
		subTotal += weightMap.get(EnumOperatorUnary.IDENTITY);
		if(choice < subTotal)
			return EnumOperatorUnary.IDENTITY;
		subTotal += weightMap.get(EnumOperatorUnary.LOG);
		if(choice < subTotal)
			return EnumOperatorUnary.LOG;
		subTotal += weightMap.get(EnumOperatorUnary.NEGATIVE);
		if(choice < subTotal)
			return EnumOperatorUnary.NEGATIVE;
		subTotal += weightMap.get(EnumOperatorUnary.SIGN);
		if(choice < subTotal)
			return EnumOperatorUnary.SIGN;
		subTotal += weightMap.get(EnumOperatorUnary.SIN);
		if(choice < subTotal)
			return EnumOperatorUnary.SIN;

		//if we got here, it's broken
		return null;
	}

	public void updateRuleMap(NeuronInput neuron, int ruleDoF, int value){

	}

	public void updateBinaryMap(EnumOperatorBinary operator, char opType, int value){

	}

	public void updateUnaryMap(EnumOperatorUnary operator, char opType, int value){

	}

}
