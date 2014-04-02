/**
 * 
 */
package creature.geeksquad.hillclimbing;

import java.util.ArrayList;
import java.util.HashMap;

import creature.geeksquad.genetics.Allele;
import creature.geeksquad.genetics.Gene;
import creature.geeksquad.genetics.Genotype;
import creature.geeksquad.genetics.Hopper;
import creature.phenotype.Creature;
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
	HashMap<EnumNeuronInputType, Integer> aRuleWeights = new HashMap<EnumNeuronInputType, Integer>();
	HashMap<EnumNeuronInputType, Integer> bRuleWeights = new HashMap<EnumNeuronInputType, Integer>();
	HashMap<EnumNeuronInputType, Integer> cRuleWeights = new HashMap<EnumNeuronInputType, Integer>();
	HashMap<EnumNeuronInputType, Integer> dRuleWeights = new HashMap<EnumNeuronInputType, Integer>();
	HashMap<EnumNeuronInputType, Integer> eRuleWeights = new HashMap<EnumNeuronInputType, Integer>();

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
			return "FLOAT";

		case INDEX_TO_PARENT:
			return "INDEX";

		case JOINT_TYPE:
			EnumJointType j = (EnumJointType) allele.getValue();
			return "JOINT";

		case RULE_INPUT_A:
			return "RULE_A";

		case RULE_INPUT_B:
			return "RULE_B";

		case RULE_INPUT_C:
			return "RULE_C";

		case RULE_INPUT_D:
			return "RULE_D";

		case RULE_INPUT_E:
			return "RULE_E";

		case BINARY_OPERATOR_1: 
			return "BINARY_1";

		case BINARY_OPERATOR_3:
			return "BINARY_3";

		case UNARY_OPERATOR_2:
			return "UNARY_2";

		case UNARY_OPERATOR_4:
			return "UNARY_4";

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
	public void climbFloat(Allele allele, int direction, float stepSize){
		//get allele value
		float f = (Float) allele.getValue();

		//add step to allele value
		allele.setValue(f + (stepSize*direction));	
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
	public NeuronInput climbRule(NeuronInput neuron, char ruleType, int boxIndex){
		//get a new rule from the maps based on the rule type
		EnumNeuronInputType newRuleValue = pickRuleValue(ruleType);

		//check what neuron input was obtained from the maps and return it
		if(newRuleValue.equals(EnumNeuronInputType.TIME))
			return new NeuronInput(newRuleValue); //return new neuron
		else if(newRuleValue.equals(EnumNeuronInputType.HEIGHT) || newRuleValue.equals(EnumNeuronInputType.TOUCH))
			return new NeuronInput(newRuleValue, boxIndex);
		else if(newRuleValue.equals(EnumNeuronInputType.CONSTANT))
			System.out.println("Can't climb rule CONSTANT yet!");
		else if(newRuleValue.equals(EnumNeuronInputType.JOINT))
			return new NeuronInput(newRuleValue, boxIndex, neuron.getDOF());

		//return starting neuron if no change occures
		return neuron;
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
	 */
	public abstract Hopper climb(Hopper startingHopper);

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
		float fitness = 0;

		//run the simulation
		for (int i = 0; i < 20; i++) {
			fitness = phenotype.advanceSimulation();
			if(fitness > highestFitness){
				highestFitness = fitness;
			}
		}
		//return highestFitness;
		return 0.5f;
	}

	//initialize the the weight maps
	public void initializeMaps(){

		//neuron input maps
		aRuleWeights.put(EnumNeuronInputType.CONSTANT, 1);
		aRuleWeights.put(EnumNeuronInputType.HEIGHT, 1);
		aRuleWeights.put(EnumNeuronInputType.JOINT, 1);
		aRuleWeights.put(EnumNeuronInputType.TIME, 1);
		aRuleWeights.put(EnumNeuronInputType.TOUCH, 1);

		bRuleWeights.put(EnumNeuronInputType.CONSTANT, 1);
		bRuleWeights.put(EnumNeuronInputType.HEIGHT, 1);
		bRuleWeights.put(EnumNeuronInputType.JOINT, 1);
		bRuleWeights.put(EnumNeuronInputType.TIME, 1);
		bRuleWeights.put(EnumNeuronInputType.TOUCH, 1);

		cRuleWeights.put(EnumNeuronInputType.CONSTANT, 1);
		cRuleWeights.put(EnumNeuronInputType.HEIGHT, 1);
		cRuleWeights.put(EnumNeuronInputType.JOINT, 1);
		cRuleWeights.put(EnumNeuronInputType.TIME, 1);
		cRuleWeights.put(EnumNeuronInputType.TOUCH, 1);

		dRuleWeights.put(EnumNeuronInputType.CONSTANT, 1);
		dRuleWeights.put(EnumNeuronInputType.HEIGHT, 1);
		dRuleWeights.put(EnumNeuronInputType.JOINT, 1);
		dRuleWeights.put(EnumNeuronInputType.TIME, 1);
		dRuleWeights.put(EnumNeuronInputType.TOUCH, 1);

		eRuleWeights.put(EnumNeuronInputType.CONSTANT, 1);
		eRuleWeights.put(EnumNeuronInputType.HEIGHT, 1);
		eRuleWeights.put(EnumNeuronInputType.JOINT, 1);
		eRuleWeights.put(EnumNeuronInputType.TIME, 1);
		eRuleWeights.put(EnumNeuronInputType.TOUCH, 1);

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
	public int getBoxIndex(Genotype genotype, int geneIndex){
		ArrayList<Gene> geneList = genotype.getChromosome();
		int blockCount = 0;

		for(int i = 0; i <= geneIndex; i++){
			if(geneList.get(i).getTrait() == Allele.Trait.LENGTH)
				blockCount++;
		}
		return blockCount;
	}

	/**
	 * Given a rule type (A,B,C,D,E), return a neuron input that might
	 * work for that rule type (using probability maps).
	 * 
	 * @param ruleType - type of rule, A,B,C,D,E
	 * @return EnumNeuronInputType - neuron input gotten from the maps
	 */
	public EnumNeuronInputType pickRuleValue(char ruleType){
		//initialize the map that will be used to get the neuron input
		HashMap<EnumNeuronInputType, Integer> weightMap = null;

		//set the weight map to the appropriate rule type map based on the
		//rule type based in.
		if(ruleType == 'A') weightMap = aRuleWeights;
		else if(ruleType == 'B') weightMap = bRuleWeights;
		else if(ruleType == 'C') weightMap = cRuleWeights;
		else if(ruleType == 'D') weightMap = dRuleWeights;
		else if(ruleType == 'E') weightMap = eRuleWeights;

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

}
