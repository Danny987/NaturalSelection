/**
 * 
 */
package creature.geeksquad.hillclimbing;

import java.util.ArrayList;
import java.util.HashMap;

import creature.geeksquad.genetics.Allele;
import creature.geeksquad.genetics.Gene;
import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Hopper;
import creature.phenotype.EnumJointSite;


/**
 * Hill Climbing strategy that targets a single allele and
 * changes its value or trait.
 * 
 * @author Danny Gomez
 * @group Ramon A. Lovato
 * @group Marcos Lemus
 */
public class ChangeSingleAllele extends Strategy{

	final boolean DEBUG = false;

	//a map to store the gene indices and their success probability
	HashMap<Integer, Integer> geneWeights = new HashMap<Integer, Integer>();

	public ChangeSingleAllele(MapHandler mapHandler){
		super(mapHandler);
	}

	/**
	 * Take in a hopper and returns a hill climbed hopper.
	 * If hill climbing did not improve the hopper, returns the
	 * original hopper.
	 */
	public Hopper climb(Hopper hopper) throws IllegalArgumentException,
	GeneticsException {
		
		/**
		 * Clone the original hopper and perform the hill climbing
		 * on the clone. If anything goes wrong, we can just return the
		 * starting hopper.
		 */
		Hopper hopperToClimb = null;
		try {
			hopperToClimb = new Hopper(hopper);
		} catch (IllegalArgumentException | GeneticsException e) {
			throw e;
		}

		/**
		 * Since this hill climbing method changes a single allele,
		 * we need to pick one from the genotype.
		 */


		/**
		 * Get the index in the gene list of the allele that is going to be changed.
		 * The index will make it easy to grab the allele from the genotype when it's
		 * needed.
		 */
		int geneIndex = pickAllele(hopperToClimb);
		
		/**
		 * Depending on what type of allele was chosen, a different type of
		 * hill climbing occurs. climbTypeChooser returns a string that
		 * represents the type of allele at an index.
		 */

		/**
		 * Send a hopper and allele to climbTypeChooser.
		 * Get a string that represents the type of allele
		 * at the index provided.
		 */
		String climbType = climbTypeChooser(hopperToClimb, geneIndex);

		if(climbType == null){
			return hopperToClimb;
		}
		else if(climbType.equals("FLOAT") || climbType.equals("ORIENTATION")){
			hopperToClimb = climbFloatAllele(hopperToClimb, geneIndex);
		}
		else if(climbType.equals("INDEX")){
			hopperToClimb = climbIndexAllele(hopperToClimb, geneIndex);
		}
		else if(climbType.equals("JOINT_CHILD") || climbType.equals("JOINT_PARENT")){
			hopperToClimb = climbJointSiteAllele(hopperToClimb, geneIndex);
		}
		else if(climbType.equals("JOINT")){
			hopperToClimb = climbJointTypeAllele(hopperToClimb, geneIndex);
		}
		else if(climbType.equals("RULE_A") || climbType.equals("RULE_B") || 
				climbType.equals("RULE_C") || climbType.equals("RULE_D") ||
				climbType.equals("RULE_E")){

			hopperToClimb = climbRuleAllele(hopperToClimb, geneIndex);
		}
		else if(climbType.equals("BINARY_1") || climbType.equals("BINARY_3")
				|| climbType.equals("UNARY_2") || climbType.equals("UNARY_4")){
			
			hopperToClimb = climbBinaryUnaryAllele(hopperToClimb, geneIndex);
		}

		return hopperToClimb;
	}//end climb method

	/**
	 * Climbs a float-type allele.
	 * 
	 * @param hopper - Hopper object to climb
	 * @param allele - specific allele to climb in hopper
	 */
	private Hopper climbFloatAllele(Hopper hopper, int geneIndex)throws GeneticsException,
	IllegalArgumentException{
		/**
		 * Clone the original hopper and perform the hill climbing
		 * on the clone. If anything goes wrong, we can just return the
		 * starting hopper.
		 */
		Hopper hopperToClimb = null;
		try {
			hopperToClimb = new Hopper(hopper);
		} catch (IllegalArgumentException | GeneticsException e) {
			throw e;
		}

		//variable initialization
		int initialDirection = 1; //default direction is "add"
		int d = initialDirection; //set direction

		float initialStepSize = 0.2f; //default step size, aka how much to add to value
		float stepSize = initialStepSize; //set step size

		boolean done = false; //flag to turn on and off hill climbing

		//while not done hill climbing
		while(!done){
			//take a step
			hopperToClimb = climbFloat(hopperToClimb, geneIndex, d*stepSize);

			//evaluate step
			if(improved(hopperToClimb)){
				//if step was an improvement, increase the step size
				stepSize *= 2;
			}
			//step was not an improvement
			else{
				//undo the last step
				hopperToClimb = climbFloat(hopperToClimb, geneIndex, -d*stepSize);
				//if it wasn't the first step
				if(stepSize > initialStepSize){
					//set the stepSize to the middle of the current and last step
					stepSize = (3*stepSize) / 4;
					//take a step to the middle
					hopperToClimb = climbFloat(hopperToClimb, geneIndex, d*stepSize);
					//evaluate
					if(improved(hopperToClimb)){
						//if the middle step was an improvement, stop hill climbing
						done = true;
					}
					//if the middle step was not an improvement
					else{
						//undo the middle step and go back to the last improved step
						hopperToClimb = climbFloat(hopperToClimb, geneIndex, -d*stepSize);
						done = true; //stop hill climbing
					}
				}
				//if the step was the first step in the opposite direction
				else if((stepSize == initialStepSize) && (d != 1)){
					//undo the step and stop hillclimbing
					hopperToClimb = climbFloat(hopperToClimb, geneIndex, -d*stepSize);
					done = true;
				}
				//if the step was the first step
				else if((stepSize == initialStepSize) && (d == 1)){
					//undo step and change direction
					hopperToClimb = climbFloat(hopperToClimb, geneIndex, -d*stepSize);
					d = -1;
				}
			}
		}
		return hopperToClimb;
	}

	//change the type of joint at this allele
	public Hopper climbJointTypeAllele(Hopper hopper, int geneIndex) throws GeneticsException,
	IllegalArgumentException{

		/**
		 * Clone the original hopper and perform the hill climbing
		 * on the clone. If anything goes wrong, we can just return the
		 * starting hopper.
		 */
		Hopper hopperToClimb = null;
		try {
			hopperToClimb = new Hopper(hopper);
		} catch (IllegalArgumentException | GeneticsException e) {
			throw e;
		}

		//perform the joint change to the genotype
		hopperToClimb = climbJointType(hopperToClimb, geneIndex);

		if(validHopper(hopperToClimb)){
			return hopperToClimb;
		}
		else{
			return hopper;
		}
	}

	public Hopper climbIndexAllele(Hopper hopper, int geneIndex){
		return hopper;
	}

	public Hopper climbJointSiteAllele(Hopper hopper, int geneIndex) throws GeneticsException, 
	IllegalArgumentException{
		/**
		 * Clone the original hopper and perform the hill climbing
		 * on the clone. If anything goes wrong, we can just return the
		 * starting hopper.
		 */
		Hopper hopperToClimb = null;
		try {
			hopperToClimb = new Hopper(hopper);
		} catch (IllegalArgumentException | GeneticsException e) {
			throw e;
		}

		ArrayList<Gene> geneList = hopperToClimb.getChromosome();
		
		Allele alleleToClimb = geneList.get(geneIndex).getDominant();
		
		//get the joint site from the cloned hopper
		EnumJointSite clonedValue = (EnumJointSite)alleleToClimb.getValue();

		/**
		 * Picking a new joint site can be tricky. We need to make sure that the site
		 * isn't taken or doesn't cause an invalid creature. This loop will run for
		 * a set number of times and attempt to change the joint site. If a new valid
		 * joint site is found, the loop ends.
		 */
		int attempts = 25;

		for(int i = 0; i < attempts; i++){
			//get the new joint site
			clonedValue = getNewJointSite(clonedValue);

			//change joint site of allele to new one
			alleleToClimb = new Allele(alleleToClimb.getTrait(), 
					clonedValue, 
					alleleToClimb.getWeight());
			
			Hopper temp = insertAllele(hopperToClimb, geneIndex, alleleToClimb);
			if(temp != null){
				return temp;
			}
		}

		//no valid joint site was found, return original hopper
		return hopper;
	}

	/**
	 * This method is for alleles of the RULE type.
	 * Takes in a hopper and a gene index, clones the hopper,
	 * and changes the allele of the hopper to a different rule.
	 * Returns a valid hill climbed hopper, or the original hopper.
	 * 
	 * @param hopper
	 * @param geneIndex
	 * @return
	 * @throws GeneticsException
	 * @throws IllegalArgumentException
	 */
	public Hopper climbRuleAllele(Hopper hopper, int geneIndex) 
			throws GeneticsException, IllegalArgumentException{
		/**
		 * Clone the original hopper and perform the hill climbing
		 * on the clone. If anything goes wrong, we can just return the
		 * starting hopper.
		 */
		Hopper hopperToClimb = null;
		try {
			hopperToClimb = new Hopper(hopper);
		} catch (IllegalArgumentException | GeneticsException e) {
			throw e;
		}

		//change the allele of the hopper.
		hopperToClimb = climbRule(hopperToClimb, geneIndex);

		//make sure the hill climbed hopper is valid
		if(validHopper(hopperToClimb)){
			return hopperToClimb;
		}
		else{
			return hopper;
		}
	}

	public Hopper climbBinaryUnaryAllele(Hopper hopper, int geneIndex)
			throws GeneticsException, IllegalArgumentException{
		/**
		 * Clone the original hopper and perform the hill climbing
		 * on the clone. If anything goes wrong, we can just return the
		 * starting hopper.
		 */
		Hopper hopperToClimb = null;
		try {
			hopperToClimb = new Hopper(hopper);
		} catch (IllegalArgumentException | GeneticsException e) {
			throw e;
		}
		
		//change the binary allele
		hopperToClimb = climbBinaryUnary(hopperToClimb, geneIndex);

		//check if the new hopper is valid
		if(validHopper(hopperToClimb)){
			return hopperToClimb;
		}
		else{
			return hopper;
		}
	}


	/**
	 * Takes in a clone of a hopper, and picks an allele within the
	 * cloned hoppers genotype. Returns the index of the allele within
	 * the gene list so it can be modified by the hill climbing.
	 * 
	 * @param clonedHopper - clone of a hopper
	 * @return The index of an allele within the genotype of the hopper
	 */
	private int pickAllele(Hopper clonedHopper) {
		//get the gene list from the hopper
		ArrayList<Gene> geneList = clonedHopper.getChromosome();

		//pick an allele index based on the probability maps
		if(geneWeights.isEmpty()){ //if the map is empty
			//pick a gene at random
			int geneIndex = (int)(Math.random()*geneList.size());

			return geneIndex;
		}
		//pick an allele index from the map
		return 0;
	}
}
