/**
 * 
 */
package danny;

import java.util.Random;

/**
 * @author Danny Gomez
 *
 */
public class Brain {

	public final boolean DEBUG = true;
	
	//determines if creature should be hillclimbing
	//will probably be grabbed from the creature object
	public boolean hillclimb = true;
	
	//number of possible strategies
	public int numberOfStrats = 5;

	//holds the weights of the strategies
	//stratWeights[stratNumber][weight]
	public int[][] stratWeights = new int[numberOfStrats][1];
	public int maxWeight = 100; //max weight for a strategy

	//the strategy that is currently being ran
	int currentStrat = -1;
	
	//initialize strategies--------------
	ChangeSingleAllele strategy; //default
	
	//-----------------------------------

	public Brain(){
		//initialize weights
		initializeWeights();
		//start a strategy
		newStrategy();

		if(DEBUG) brainDebug();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public void hillclimb(){
		//perform a single run of hillclimbing
		strategy.run();
		
		//if hillclimbing improve fitness,
		//increase strategy weight
		stratWeights[currentStrat][0]++;
		
		//if fitness doesn't change or decreases,
		//decrease strategy weight, pick new strategy
		stratWeights[currentStrat][0]--;
		newStrategy();
	}

	//Sets all strategy weights equal. All strategies should
	//have an equal chance of occurring at the start.
	public void initializeWeights(){
		for(int i = 0; i < stratWeights.length; i++){
			stratWeights[i][0] = maxWeight / numberOfStrats;
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
		for(int i = 0; i < stratWeights.length; i++){
			weightSum += stratWeights[i][0];
		}

		int choice = new Random().nextInt(weightSum);
		int subTotal = 0;

		//pick strategy based on weight
		for(int i = 0; i < stratWeights.length; i++){
			subTotal += stratWeights[i][0];
			if(choice < subTotal) return i;
		}

		return -1; //we shouldn't get here
	}

	public void newStrategy(){
		int strat = pickStrategy();

		if(strat == 0){ //create new strategy 0
			currentStrat = 0;
			strategy = new ChangeSingleAllele();
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
		}
	}

	//debug methods -------------------------------------------------------------------------
	public void brainDebug(){
		//print strategy weights
		for(int i = 0; i < stratWeights.length; i++){
			System.out.println("Strategy: " + i + " Weight: " + stratWeights[i][0]);
		}

		//print current strat
		System.out.println("Current Strategy: " + currentStrat);
	}

}
