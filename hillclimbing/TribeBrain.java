package creature.geeksquad.hillclimbing;

import java.util.HashMap;

import creature.geeksquad.genetics.Crossover;
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
	int numberOfStrategies = 4;
	//starting strategy weight
	int startingWeight = 1;

	//the index of the current strategy being performed
	int currentStrat;

	//object for the strategy that will be performed
	Strategy strategy;

	//create the map handler
	MapHandler mapHandler;;

	public TribeBrain(){
		//initialize strategy map weights
		mapHandler = new MapHandler();
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

		//fitness of the hopper when it comes in
		float startingFitness = hopper.getFitness();

		/**
		 * Pick the strategy that will be performed on the hopper.
		 * The strategy chosen will be based on the maps of
		 * strategy weights. Maps are updated as strategies are
		 * performed.
		 */
		strategy = newStrategy();


		/**
		 * Clone the original hopper. We don't want to accidently
		 * break anything in the original hopper. Also, if the hill
		 * climbing somehow doesn't produce a better creature, we
		 * need to return the untampered hopper.
		 */
		Hopper clone = null;
		try {
			clone = new Hopper(hopper);
		} catch (IllegalArgumentException | GeneticsException e) {
			throw e;
		}


		/**
		 * Perform the hill climbing on the cloned hopper. The hill
		 * climbing method returns the climbing hopper. We overwrite
		 * the cloned hopper with the hill climbing one.
		 */

		clone = strategy.climb(clone);

		/**
		 * Make sure that the hill climbed hopper is valid. An easy 
		 * way to do this is by cloning the hopper and checking
		 * if an exception occurs. If an exception occurs, return
		 * the hopper that was originally passed into the brain.
		 */
		if(!strategy.validHopper(clone)){
			return hopper;
		}

		/**
		 * Get the fitness of the hill climbed hopper and compare it
		 * to the fitness of the original hopper. Return the hopper
		 * with the highest fitness.
		 */
		if(strategy.improved(clone)){
			return clone;
		}
		else if(startingFitness > clone.getFitness()){
			return clone;
		}
		else{
			mapHandler.undo();
			return hopper;
		}
	}

	/**
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
	 * @return i - Strategy number.
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
			return new ChangeSingleAllele(this.mapHandler);
		}
		else if(strat == 1){ //strategy 1
			currentStrat = 1;
			return new AddBlock(this.mapHandler);
		} 
		else if(strat == 2){ //strategy 2
			currentStrat = 2;
			return new RemoveBlock(this.mapHandler);
		}
		else if(strat == 3){ //strategy 3
			currentStrat = 3;
			return new AddRule(this.mapHandler);
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

	public void updateStrategyMap(int value){
		value += strategyWeights.get(currentStrat);
		if(value >= 1 && value <= 100)strategyWeights.put(currentStrat, value);
	}
}
