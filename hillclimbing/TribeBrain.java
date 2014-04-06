package creature.geeksquad.hillclimbing;

import java.util.HashMap;

import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Genotype;
import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.library.*;

/**
 * 
 * @author Daniel
 * 
 * The tribe brain object contains a map of hill climbing strategies that
 * can be performed to a hopper.
 * 
 * Strategies are chosen based on weights. Weights are manipulated based on
 * success rates of each strategy.
 * 
 * performHillClimbing method performs hill climbing on a hopper, returns 
 * an improved hopper.
 *
 */
public class TribeBrain {
	Genotype newGenotype; //genotype that leaves the brain

	//map that stores the probability of each strategy occuring.
	//strategy number - strategy weight
	HashMap<Integer,Integer> strategyWeights = new HashMap<Integer,Integer>();

	//number of strategies
	int numberOfStrategies = 1;
	//starting strategy weight
	int startingWeight = 1;

	//the index of the current strategy being performed
	int currentStrat;

	//object for the strategy that will be performed
	Strategy strategy;

	public TribeBrain(){
		//initialize strategy map weights
		initializeWeights();
	}

	
	/**
	 * This method takes in a hopper, performs hill climbing on it and
	 * returns the updated hopper.
	 * 
	 *  
	 * @param hopper - Hopper object that will get hill climbed
	 * @return Hill climbed hopper
	 * @throws IllegalArgumentException, GeneticsException  
	 */
	public Hopper performHillClimbing(Hopper hopper)
			throws IllegalArgumentException, GeneticsException {
		//set the strategy to perform
		strategy = newStrategy();
		//clone hopper
		Hopper clone = null;
		try {
			clone = new Hopper(hopper);
		} catch (IllegalArgumentException | GeneticsException e) {
			// TODO Auto-generated catch block
			System.err.println("Clone Hopper Exception1");
			throw e;
		}
		//perform the hill climbing on the clone
		int failedAttemps = 0;
		try {
			return strategy.climb(clone);
		} catch (IllegalArgumentException | GeneticsException e) {
			System.err.println("Climb Hopper Exception2");
			return hopper;
		}
	}

	/*
	 * Initialize the strategy weights in the map.
	 */
	private void initializeWeights() {
		for(int i = 0; i < numberOfStrategies; i++){
			strategyWeights.put(i, startingWeight);
		}
	}

	/**
	 * Pick a strategy based on probabilities/weights
	 * 
	 * @return i Strategy number.
	 */
	public int pickStrategy(){
		int weightSum = 0;

		//get sum of weights
		for(int i = 0; i < strategyWeights.size(); i++){
			weightSum += strategyWeights.get(i);
		}

		int choice = Helper.RANDOM.nextInt(weightSum);
		int subTotal = 0;

		//pick strategy based on weight
		for(int i = 0; i < strategyWeights.size(); i++){
			subTotal += strategyWeights.get(i);
			if(choice < subTotal) return i;
		}

		return -1; //we shouldn't get here
	}

	public Strategy newStrategy(){
		//pick the strategy number based on weights
		int strat = pickStrategy();

		//return a strategy object depending on the strategy number
		//chosen above.
		if(strat == 0){ //create new strategy 0
			currentStrat = 0;
			return new ChangeSingleAllele();
		}
		else if(strat == 1){ //strategy 1
			currentStrat = 1;
		} 
		else if(strat == 2){ //strategy 2
			currentStrat = 2;
		}
		else if(strat == 3){ //strategy 3
			currentStrat = 3;
		}
		else if(strat == 4){ //strategy 4
			currentStrat = 4;
		}
		else{ //something went wrong
			currentStrat = -1;
			return null;
		}
		return null;
	}
}
