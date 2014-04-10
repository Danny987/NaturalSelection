package creature.geeksquad.hillclimbing;

import java.util.HashMap;

import creature.geeksquad.library.Helper;
import creature.phenotype.EnumNeuronInputType;
import creature.phenotype.EnumOperatorBinary;
import creature.phenotype.EnumOperatorUnary;
import creature.phenotype.NeuronInput;

public class MapHandler {

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

	//remove block maps
	HashMap<Integer, Integer> removeBlockWeights = new HashMap<Integer, Integer>();

	public MapHandler(){
		initializeMaps();
	}


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

		//remove block maps
		for(int i = 0; i < 50; i++){
			removeBlockWeights.put(i, 1);
		}
	}

	/**
	 * Given a rule type (A,B,C,D,E), return a neuron input that might
	 * work for that rule type (using probability maps).
	 * 
	 * @param ruleType - type of rule, A,B,C,D,E
	 * @return EnumNeuronInputType - neuron input gotten from the maps
	 */
	public EnumNeuronInputType pickRuleValue(char ruleType, int ruleDoF){
		//TODO
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

	public EnumOperatorBinary getNewBinary(char opType){
		//TODO
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

	public EnumOperatorUnary getNewUnary(char opType){
		//TODO
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

	public void updateRuleMap(NeuronInput neuron, char ruleType, int ruleDoF, int value){
		//TODO
		if(ruleType == 'A'){
			if(ruleDoF == 1){
				value += aRuleWeights1.get(neuron.getType());
				if(value >= 1 && value <= 100)aRuleWeights1.put(neuron.getType(), value);
			}
			else if(ruleDoF == 2){
				value += aRuleWeights1.get(neuron.getType());
				if(value >= 1 && value <= 100)aRuleWeights1.put(neuron.getType(), value);
			}
		}
		else if(ruleType == 'B'){
			if(ruleDoF == 1){
				value += bRuleWeights1.get(neuron.getType());
				if(value >= 1 && value <= 100)bRuleWeights1.put(neuron.getType(), value);
			}
			else if(ruleDoF == 2){
				value += bRuleWeights1.get(neuron.getType());
				if(value >= 1 && value <= 100)bRuleWeights1.put(neuron.getType(), value);
			}
		}
		else if(ruleType == 'C'){
			if(ruleDoF == 1){
				value += cRuleWeights1.get(neuron.getType());
				if(value >= 1 && value <= 100)cRuleWeights1.put(neuron.getType(), value);
			}
			else if(ruleDoF == 2){
				value += cRuleWeights1.get(neuron.getType());
				if(value >= 1 && value <= 100)cRuleWeights1.put(neuron.getType(), value);
			}
		}
		else if(ruleType == 'D'){
			if(ruleDoF == 1){
				value += dRuleWeights1.get(neuron.getType());
				if(value >= 1 && value <= 100)dRuleWeights1.put(neuron.getType(), value);
			}
			else if(ruleDoF == 2){
				value += dRuleWeights1.get(neuron.getType());
				if(value >= 1 && value <= 100)dRuleWeights1.put(neuron.getType(), value);
			}
		}
		else if(ruleType == 'E'){
			if(ruleDoF == 1){
				value += eRuleWeights1.get(neuron.getType());
				if(value >= 1 && value <= 100)eRuleWeights1.put(neuron.getType(), value);
			}
			else if(ruleDoF == 2){
				value += eRuleWeights1.get(neuron.getType());
				if(value >= 1 && value <= 100)eRuleWeights1.put(neuron.getType(), value);
			}
		}
	}

	public void updateBinaryMap(EnumOperatorBinary operator, char opType, int value){
		//TODO
		if(opType == '1'){
			value += binaryOneWeights.get(operator);
			if(value >= 1 && value <= 100)binaryOneWeights.put(operator, value);
		}
		else if(opType == '3'){
			value += binaryThreeWeights.get(operator);
			if(value >= 1 && value <= 100)binaryThreeWeights.put(operator, value);
		}

	}

	public void updateUnaryMap(EnumOperatorUnary operator, char opType, int value){
		//TODO
		if(opType == '2'){
			value += unaryTwoWeights.get(operator);
			if(value >= 1 && value <= 100)unaryTwoWeights.put(operator, value);
		}
		else if(opType == '4'){
			value += unaryFourWeights.get(operator);
			if(value >= 1 && value <= 100)unaryFourWeights.put(operator, value);
		}
	}

	public void updateRemoveBlockMap(int blockIndex, int value){
		//TODO
		value += removeBlockWeights.get(blockIndex);
		if(value >= 1 && value <= 100)removeBlockWeights.put(blockIndex, value);
	}

}
