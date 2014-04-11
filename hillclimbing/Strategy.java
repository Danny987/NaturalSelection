/**
 * 
 */
package creature.geeksquad.hillclimbing;

import java.util.ArrayList;
import java.util.HashMap;

import creature.geeksquad.genetics.Allele;
import creature.geeksquad.genetics.Crossover;
import creature.geeksquad.genetics.Gene;
import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Genotype;
import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.genetics.Allele.Trait;
import creature.geeksquad.genetics.WeightHelper;
import creature.geeksquad.genetics.Genotype.*;
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

	boolean mapsOn = false;

	Genotype currentGenotype;
	Genotype newGenotype;

	WeightHelper weightHelper;

	MapHandler mapHandler;

	public Strategy(MapHandler mapHandler){
		//set the crossover object
		this.weightHelper = new WeightHelper(false);
		this.mapHandler = mapHandler;
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
	public String climbTypeChooser(Hopper clonedHopper, int geneIndex){

		//get the trait of the dominant allele at the geneIndex
		Allele allele = getDomAllele(clonedHopper, geneIndex);
		Allele.Trait trait = allele.getTrait();

		// Switch to determine what string to return based on the trait.
		switch (trait) {
		case HEIGHT: case WIDTH: case LENGTH:
			return "FLOAT";

		case INDEX_TO_PARENT:
			return "INDEX";

		case JOINT_SITE_ON_CHILD:
			return "JOINT_CHILD";

		case JOINT_SITE_ON_PARENT:
			return "JOINT_PARENT";

		case JOINT_TYPE:
			return "JOINT";
			//return "INDEX";

		case JOINT_ORIENTATION:
			return "ORIENTATION";
			//return "INDEX";

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
	public Hopper climbFloat(Hopper hopper, int geneIndex, float step)
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

		//get the allele
		Allele alleleToClimb = getDomAllele(hopperToClimb, geneIndex);


		//get the allele value
		float currentValue = (float)alleleToClimb.getValue();

		//add the step to the current value
		float newValue = currentValue + step;


		/**
		 * Before changing the value of the allele, make sure that it meets
		 * all the criteria. If the allele is a JOINT_ORIENTATION, then we
		 * don't care about imposing a min and max size. If the allele is
		 * a dimension (L, W, H), then make sure the new value is within
		 * the max and min bounds.
		 */
		if((alleleToClimb.getTrait().equals(Allele.Trait.JOINT_ORIENTATION))
				|| (newValue >= getFloatMin(hopperToClimb, geneIndex) 
				&& newValue <= getFloatMax(hopperToClimb, geneIndex))){
			//update the allele with the new value
			alleleToClimb = new Allele(alleleToClimb.getTrait(),
					newValue,
					alleleToClimb.getWeight());
		}

		//insert the allele into the genotype, if it's valid, return the hopper
		Hopper temp = insertAllele(hopperToClimb, geneIndex, alleleToClimb);
		if(temp != null){
			return temp;
		}
		else{
			return hopper;
		}
	}

	/**
	 * Given a neuron, returns a new one one.
	 * 
	 * @param neuron - starting neuron input
	 * @param ruleType - type of neuron input, A,B,C,D,E
	 * @param boxIndex - box index of the neuron rule allele
	 * @return neuron - the changed neuron
	 */
	public NeuronInput replaceNeuron(NeuronInput neuron, char ruleType, int boxIndex, int ruleDoF){
		//get a new rule from the maps based on the rule type
		EnumNeuronInputType newRuleValue = mapHandler.pickRuleValue(ruleType, ruleDoF);

		if(neuron.getType().equals(newRuleValue)){
			newRuleValue = mapHandler.pickRuleValue(ruleType, ruleDoF);
		}

		//check what neuron input was obtained from the maps and return it
		if(newRuleValue.equals(EnumNeuronInputType.TIME)){
			return new NeuronInput(newRuleValue); //return new neuron
		}
		else if(newRuleValue.equals(EnumNeuronInputType.HEIGHT) || newRuleValue.equals(EnumNeuronInputType.TOUCH)){
			return new NeuronInput(newRuleValue, boxIndex);
		}
		else if(newRuleValue.equals(EnumNeuronInputType.CONSTANT)){
			return new NeuronInput(newRuleValue, Helper.RANDOM.nextFloat());
		}
		else if(newRuleValue.equals(EnumNeuronInputType.JOINT)){
			return new NeuronInput(newRuleValue, boxIndex, neuron.getDOF());
		}

		//return starting neuron if no change occurs
		return neuron;
	}

	/**
	 * When given a joint site, returns a different one
	 * @param clonedJointSite
	 * @return
	 */
	public EnumJointSite getNewJointSite(EnumJointSite clonedJointSite){
		//TODO
		EnumJointSite jointSite = clonedJointSite;

		while(jointSite == clonedJointSite){
			jointSite = EnumJointSite.values()
					[Helper.RANDOM.nextInt(EnumJointSite.values().length)];
		}
		return jointSite;
	}

	/**
	 * When given a joint type, returns a different one
	 * @param clonedJointType
	 * @return
	 */
	public EnumJointType getNewJointType(EnumJointType clonedJointType){
		//TODO
		EnumJointType jointType = clonedJointType;

		while(jointType == clonedJointType){
			jointType = EnumJointType.values()
					[Helper.RANDOM.nextInt(EnumJointType.values().length)];
		}
		return jointType;
	}

	public Hopper climbJointType(Hopper hopper, int geneIndex)
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

		//get the allele at the geneIndex. This isn't necessary, but it will shorten
		//some lines of code later on.
		Allele alleleToClimb = getDomAllele(hopperToClimb, geneIndex);

		//get the current joint type
		EnumJointType startingJointType = (EnumJointType)alleleToClimb.getValue();
		//send the current joint type to getNewJointType to get a new joint type
		EnumJointType jointType = getNewJointType(startingJointType);

		//change the allele to have the new joint type
		alleleToClimb = new Allele(alleleToClimb.getTrait(), 
				jointType, 
				alleleToClimb.getWeight());

		Allele recAllele = hopperToClimb.getChromosome().get(geneIndex).getRecessive();

		Gene gene = new Gene(alleleToClimb, recAllele);

		hopperToClimb.getChromosome().remove(geneIndex);
		hopperToClimb.getChromosome().add(geneIndex, gene);

		int boxIndex = getBoxIndex(hopperToClimb, geneIndex);

		if(jointType.getDoF() == 0){
			//remove rules
			int i = geneIndex + 4; //go to first rule
			while(i < hopperToClimb.getChromosome().size() && 
					!getDomAllele(hopperToClimb, i).getTrait().equals(Allele.Trait.LENGTH)){
				hopperToClimb.getChromosome().remove(i); //remove gene at index
			}

		}
		else if(startingJointType.getDoF() == 2 && jointType.getDoF() == 1){
			//remove 1 set of rules

			//index to the closest dof marker or length allele
			int i = geneIndex;
			while(i < hopperToClimb.getChromosome().size() &&
					(!getDomAllele(hopperToClimb, i).getTrait().equals(Allele.Trait.DOF_MARKER)
							|| !getDomAllele(hopperToClimb, i).getTrait().equals(Allele.Trait.LENGTH))){
				if (getDomAllele(hopperToClimb, i).getTrait().equals(Allele.Trait.DOF_MARKER)) {
					break;
				}
				i++; //go to next index
			}
			//at the dof marker or length
			//remove up to the next length allele
			while(i < hopperToClimb.getChromosome().size() && 
					!getDomAllele(hopperToClimb, i).getTrait().equals(Allele.Trait.LENGTH)){
				hopperToClimb.getChromosome().remove(i); //remove gene at index
			}
		}
		else{
			//add rules
			int rulesLength = 0; //number of rule sets to add per DoF
			int n = jointType.getDoF() - startingJointType.getDoF(); //how many DoFs need rules
			int i = geneIndex;

			//move the index to where we need to add the rules
			while(i < hopperToClimb.getChromosome().size() && 
					!getDomAllele(hopperToClimb, i).getTrait().equals(Allele.Trait.LENGTH)){
				i++;
			}

			if(startingJointType.getDoF() == 1 && jointType.getDoF() == 2){
				//add a DoF marker
				Allele allele1 = new Allele(Trait.DOF_MARKER, EnumJointType.DOF_2, 0.1f);
				hopperToClimb.getChromosome().add(i, new Gene(allele1));
				i++;
			}

			for(int j = 0; j < n; j++){

				if(j != 0){ //if adding a second DoF
					//add a dof marker
					Allele allele1 = new Allele(Trait.DOF_MARKER, EnumJointType.DOF_2, 0.1f);
					hopperToClimb.getChromosome().add(i, new Gene(allele1));
					i++;
				}

				rulesLength = Helper.RANDOM.nextInt(Helper.SEED_MAX_CONSTANT+1);
				for(int k = 0; k < rulesLength; k++){
					Allele allele1;
					Allele allele2;
					//a
					allele1 = new Allele(Trait.RULE_INPUT_A, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
					allele1 = new Allele(allele1.getTrait(),
							replaceNeuron((NeuronInput)allele1.getValue(), 'A', boxIndex, j+1), 
							weightHelper.weight());

					allele2 = new Allele(Trait.RULE_INPUT_A, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
					allele2 = new Allele(allele2.getTrait(), 
							replaceNeuron((NeuronInput)allele2.getValue(), 'A', boxIndex, j+1), weightHelper.weight());

					hopperToClimb.getChromosome().add(i, new Gene(allele1, allele2));
					i++;
					//b
					allele1 = new Allele(Trait.RULE_INPUT_B, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
					allele1 = new Allele(allele1.getTrait(),
							replaceNeuron((NeuronInput)allele1.getValue(), 'B', boxIndex, j+1), weightHelper.weight());

					allele2 = new Allele(Trait.RULE_INPUT_B, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
					allele2 = new Allele(allele2.getTrait(), 
							replaceNeuron((NeuronInput)allele2.getValue(), 'B', boxIndex, j+1), weightHelper.weight());

					hopperToClimb.getChromosome().add(i, new Gene(allele1, allele2));
					i++;
					//c
					allele1 = new Allele(Trait.RULE_INPUT_C, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
					allele1 = new Allele(allele1.getTrait(),
							replaceNeuron((NeuronInput)allele1.getValue(), 'C', boxIndex, j+1), weightHelper.weight());

					allele2 = new Allele(Trait.RULE_INPUT_C, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
					allele2 = new Allele(allele2.getTrait(), 
							replaceNeuron((NeuronInput)allele2.getValue(), 'C', boxIndex, j+1), weightHelper.weight());

					hopperToClimb.getChromosome().add(i, new Gene(allele1, allele2));
					i++;
					//d
					allele1 = new Allele(Trait.RULE_INPUT_D, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
					allele1 = new Allele(allele1.getTrait(),
							replaceNeuron((NeuronInput)allele1.getValue(), 'D', boxIndex, j+1), weightHelper.weight());

					allele2 = new Allele(Trait.RULE_INPUT_D, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
					allele2 = new Allele(allele2.getTrait(), 
							replaceNeuron((NeuronInput)allele2.getValue(), 'D', boxIndex, j+1), weightHelper.weight());

					hopperToClimb.getChromosome().add(i, new Gene(allele1, allele2));
					i++;
					//e
					allele1 = new Allele(Trait.RULE_INPUT_E, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
					allele1 = new Allele(allele1.getTrait(),
							replaceNeuron((NeuronInput)allele1.getValue(), 'E', boxIndex, j+1), weightHelper.weight());

					allele2 = new Allele(Trait.RULE_INPUT_E, new NeuronInput(EnumNeuronInputType.TIME), weightHelper.weight());
					allele2 = new Allele(allele2.getTrait(), 
							replaceNeuron((NeuronInput)allele2.getValue(), 'E', boxIndex, j+1), weightHelper.weight());

					hopperToClimb.getChromosome().add(i, new Gene(allele1, allele2));
					i++;
					//bin1
					allele1 = new Allele(Trait.BINARY_OPERATOR_1, EnumOperatorBinary.ADD, weightHelper.weight());
					allele1 = new Allele(allele1.getTrait(), 
							mapHandler.getNewBinary('1'), weightHelper.weight());

					allele2 = new Allele(Trait.BINARY_OPERATOR_1, EnumOperatorBinary.ADD, weightHelper.weight());
					allele2 = new Allele(allele2.getTrait(), 
							mapHandler.getNewBinary('1'), weightHelper.weight());

					hopperToClimb.getChromosome().add(i, new Gene(allele1, allele2));
					i++;
					//un2
					allele1 = new Allele(Trait.UNARY_OPERATOR_2, EnumOperatorUnary.ABS, weightHelper.weight());
					allele1 = new Allele(allele1.getTrait(), 
							mapHandler.getNewUnary('2'), weightHelper.weight());

					allele2 = new Allele(Trait.UNARY_OPERATOR_2, EnumOperatorUnary.ABS, weightHelper.weight());
					allele2 = new Allele(allele2.getTrait(), 
							mapHandler.getNewUnary('2'), weightHelper.weight());

					hopperToClimb.getChromosome().add(i, new Gene(allele1, allele2));
					i++;
					//bin3
					allele1 = new Allele(Trait.BINARY_OPERATOR_3, EnumOperatorBinary.ADD, weightHelper.weight());
					allele1 = new Allele(allele1.getTrait(),
							mapHandler.getNewBinary('3'), weightHelper.weight());

					allele2 = new Allele(Trait.BINARY_OPERATOR_3, EnumOperatorBinary.ADD, weightHelper.weight());
					allele2 = new Allele(allele2.getTrait(),
							mapHandler.getNewBinary('3'), weightHelper.weight());

					hopperToClimb.getChromosome().add(i, new Gene(allele1, allele2));
					i++;
					//un4
					allele1 = new Allele(Trait.UNARY_OPERATOR_4, EnumOperatorUnary.ABS, weightHelper.weight());
					allele1 = new Allele(allele1.getTrait(),
							mapHandler.getNewUnary('4'), weightHelper.weight());

					allele2 = new Allele(Trait.UNARY_OPERATOR_4, EnumOperatorUnary.ABS, weightHelper.weight());
					allele2 = new Allele(allele2.getTrait(),
							mapHandler.getNewUnary('4'), weightHelper.weight());

					hopperToClimb.getChromosome().add(i, new Gene(allele1, allele2));
					i++;
				}
			}
		}
		if(validHopper(hopperToClimb))
		{
			return hopperToClimb;
		}
		else{
			return hopper;
		}
	}

	/**
	 * Given a hopper and an index to an allele of type RULE, changes
	 * the allele to a different rule type. Returns the modified
	 * hopper if it's valid.
	 * 
	 * @param hopper
	 * @param geneIndex
	 * @return
	 * @throws GeneticsException
	 * @throws IllegalArgumentException
	 */
	public Hopper climbRule(Hopper hopper, int geneIndex)
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

		//get the rule allele from the cloned hopper
		Allele alleleToClimb = getDomAllele(hopperToClimb, geneIndex);

		//get box index
		int boxIndex = getBoxIndex(hopperToClimb, geneIndex);

		//get rule DoF location
		int ruleDoF = getRuleDoF(hopperToClimb, geneIndex);

		//create a new allele based on the current rule type
		switch (alleleToClimb.getTrait()) {
		case RULE_INPUT_A:
			alleleToClimb = new Allele(alleleToClimb.getTrait(),
					replaceNeuron((NeuronInput)alleleToClimb.getValue(), 'A', boxIndex, ruleDoF),
					alleleToClimb.getWeight());

			break;

		case RULE_INPUT_B:
			alleleToClimb = new Allele(alleleToClimb.getTrait(),
					replaceNeuron((NeuronInput)alleleToClimb.getValue(), 'B', boxIndex, ruleDoF),
					alleleToClimb.getWeight());

			break;

		case RULE_INPUT_C:
			alleleToClimb = new Allele(alleleToClimb.getTrait(),
					replaceNeuron((NeuronInput)alleleToClimb.getValue(), 'C', boxIndex, ruleDoF),
					alleleToClimb.getWeight());

			break;

		case RULE_INPUT_D:
			alleleToClimb = new Allele(alleleToClimb.getTrait(),
					replaceNeuron((NeuronInput)alleleToClimb.getValue(), 'D', boxIndex, ruleDoF),
					alleleToClimb.getWeight());

			break;

		case RULE_INPUT_E:
			alleleToClimb = new Allele(alleleToClimb.getTrait(),
					replaceNeuron((NeuronInput)alleleToClimb.getValue(), 'E', boxIndex, ruleDoF),
					alleleToClimb.getWeight());

			break;

		default:
			//a non-rule allele was passed in so just return the original hopper.
			break;
		}

		//update map
		char ruleType = 'A';

		switch (alleleToClimb.getTrait()) {
		case RULE_INPUT_A:
			ruleType = 'A';
			break;
		case RULE_INPUT_B:
			ruleType = 'B';
			break;
		case RULE_INPUT_C:
			ruleType = 'C';
			break;
		case RULE_INPUT_D:
			ruleType = 'D';
			break;
		case RULE_INPUT_E:
			ruleType = 'E';
			break;
		default:
			break;
		}

		if(mapsOn) mapHandler.updateRuleMap((NeuronInput) alleleToClimb.getValue(), 
				ruleType, 
				ruleDoF, 
				1);

		//insert the allele into the genotype, if it's valid, return the hopper
		Hopper temp = insertAllele(hopperToClimb, geneIndex, alleleToClimb);
		if(temp != null){
			return temp;
		}
		else{
			return hopper;
		}
	}



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
		float startingFitness = hopper.getFitness();

		try {
			hopper.getGenotype().buildPhenotype();
		} catch (IllegalArgumentException | GeneticsException e) {
			return false;
		}

		//run simulation to get new fitness
		float newFitness = hopper.evalFitness();

		//if the fitness from the sim is greater than the current fitness
		if(newFitness > startingFitness){
			//update hopper current fitness
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

	public Hopper climbBinaryUnary(Hopper hopper, int geneIndex)
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

		//get the allele that is going to be changed
		Allele alleleToClimb = getDomAllele(hopperToClimb, geneIndex);

		//change the allele depending on the its current trait
		switch (alleleToClimb.getTrait()) {
		case BINARY_OPERATOR_1: 
			alleleToClimb = new Allele(alleleToClimb.getTrait(),
					mapHandler.getNewBinary('1'),
					alleleToClimb.getWeight());

			if(mapsOn) mapHandler.updateBinaryMap((EnumOperatorBinary) alleleToClimb.getValue(), 
					'1', 
					1);

			break;

		case BINARY_OPERATOR_3:
			alleleToClimb = new Allele(alleleToClimb.getTrait(),
					mapHandler.getNewBinary('3'),
					alleleToClimb.getWeight());

			if(mapsOn) mapHandler.updateBinaryMap((EnumOperatorBinary) alleleToClimb.getValue(), 
					'3', 
					1);

			break;

		case UNARY_OPERATOR_2:
			alleleToClimb = new Allele(alleleToClimb.getTrait(),
					mapHandler.getNewUnary('2'),
					alleleToClimb.getWeight());

			if(mapsOn) mapHandler.updateUnaryMap((EnumOperatorUnary) alleleToClimb.getValue(), 
					'2', 
					1);

			break;

		case UNARY_OPERATOR_4:
			alleleToClimb = new Allele(alleleToClimb.getTrait(),
					mapHandler.getNewUnary('4'),
					alleleToClimb.getWeight());

			mapHandler.updateUnaryMap((EnumOperatorUnary) alleleToClimb.getValue(), 
					'4', 
					1);

			break;

		default:
			break;
		}

		//insert the allele into the genotype, if it's valid, return the hopper
		Hopper temp = insertAllele(hopperToClimb, geneIndex, alleleToClimb);
		if(temp != null){
			return temp;
		}
		else{
			return hopper;
		}
	}

	/**
	 * Runs the simulation on a hopper.
	 * Returns the highest fitness from the simulation.
	 * 
	 * @param hopper - hopper to run fitness test on
	 * @return float - highest fitness from simulation
	 */
	public float getNewFitness(Hopper hopper){
		//TODO
		//generate a phenotype from the creature
		Creature phenotype = hopper.getPhenotype();

		//initialize fitness
		float highestFitness = 0;
		//float fitness = 0;

		//return highestFitness, this should also set the fitness of the hopper
		return hopper.evalFitness();
	}

	//initialize the the weight maps

	/**
	 * Gets the box index of the geneIndex.
	 * 
	 * @param genotype - Genotype that contains the gene.
	 * @param geneIndex - location of the gene to check
	 * @return int - box index of the gene
	 */
	public int getBoxIndex(Hopper hopper, int geneIndex){
		int blockCount = 0;
		for(int i = 0; i <= geneIndex; i++){
			if(getDomAllele(hopper, i).getTrait().equals(Allele.Trait.LENGTH))
				blockCount++;
		}
		return blockCount;
	}

	/** Calculates the maximum float value for a dimension of the box that
	 * the given allele is in. Maximum is x10 the smallest dimension.
	 * 
	 * @param genotype
	 * @param allele
	 * @param geneIndex
	 * @return
	 */
	public float getFloatMax(Hopper hopper, int geneIndex){
		float currentVal = 0;
		float val1 = 0;
		float val2 = 0;

		currentVal = (float)getDomAllele(hopper, geneIndex).getValue();

		//if allele is not a dimension, return 0
		if(getDomAllele(hopper, geneIndex).getTrait().equals(Allele.Trait.JOINT_ORIENTATION)){
			return 0;
		}

		/**
		 * Depending on what allele is given, get the values of the other dimensions.
		 * Order is LENGTH -> HEIGHT -> WIDTH
		 */
		//at a length allele
		if(getDomAllele(hopper, geneIndex).getTrait().equals(Allele.Trait.LENGTH)){
			//go forward 1 allele to get width
			if(getDomAllele(hopper, geneIndex+1).getTrait().equals(Allele.Trait.WIDTH)){
				val1 = (float)getDomAllele(hopper, geneIndex+1).getValue();
			}
			//go forward 2 alleles to get height
			if(getDomAllele(hopper, geneIndex+2).getTrait().equals(Allele.Trait.HEIGHT)){
				val2 = (float)getDomAllele(hopper, geneIndex+2).getValue();
			}
		}
		//at a height allele
		else if(getDomAllele(hopper, geneIndex).getTrait().equals(Allele.Trait.HEIGHT)){
			//go back 1 allele to get length
			if(getDomAllele(hopper, geneIndex-1).getTrait().equals(Allele.Trait.LENGTH)){
				val1 = (float)getDomAllele(hopper, geneIndex-1).getValue();
			}
			//go forward 1 allele to get width
			if(getDomAllele(hopper, geneIndex+1).getTrait().equals(Allele.Trait.WIDTH)){
				val2 = (float)getDomAllele(hopper, geneIndex+1).getValue();
			}
		}
		//at a width allele
		else if(getDomAllele(hopper, geneIndex).getTrait().equals(Allele.Trait.WIDTH)){
			//go back 1 allele to get length
			if(getDomAllele(hopper, geneIndex-1).getTrait().equals(Allele.Trait.HEIGHT)){
				val1 = (float)getDomAllele(hopper, geneIndex-1).getValue();
			}
			//go back 2 alleles to get height
			if(getDomAllele(hopper, geneIndex-2).getTrait().equals(Allele.Trait.LENGTH)){
				val2 = (float)getDomAllele(hopper, geneIndex-2).getValue();
			}
		}

		//return the smallest value
		if(currentVal < val1 && currentVal < val2){
			return currentVal*10;
		}
		else if(val1 < currentVal && val1 < val2){
			return val1*10;
		}
		else if(val2 < currentVal && val2 < val1){
			return val2*10;
		}

		return 0;
	}

	/** Calculates the minimum float value for a dimension of the box that
	 * the given allele is in. Minimum is 1/10th the largest dimension.
	 * 
	 * @param genotype
	 * @param allele
	 * @param geneIndex
	 * @return
	 */
	public float getFloatMin(Hopper hopper, int geneIndex){
		float currentVal = 0;
		float val1 = 0;
		float val2 = 0;

		currentVal = (float)getDomAllele(hopper, geneIndex).getValue();

		//if allele is not a dimension, return 0
		if(getDomAllele(hopper, geneIndex).getTrait().equals(Allele.Trait.JOINT_ORIENTATION)){
			return 0;
		}

		/**
		 * Depending on what allele is given, get the values of the other dimensions.
		 * Order is LENGTH -> HEIGHT -> WIDTH
		 */
		//at a length allele
		if(getDomAllele(hopper, geneIndex).getTrait().equals(Allele.Trait.LENGTH)){
			//go forward 1 allele to get width
			if(getDomAllele(hopper, geneIndex+1).getTrait().equals(Allele.Trait.WIDTH)){
				val1 = (float)getDomAllele(hopper, geneIndex+1).getValue();
			}
			//go forward 2 alleles to get height
			if(getDomAllele(hopper, geneIndex+2).getTrait().equals(Allele.Trait.HEIGHT)){
				val2 = (float)getDomAllele(hopper, geneIndex+2).getValue();
			}
		}
		//at a height allele
		else if(getDomAllele(hopper, geneIndex).getTrait().equals(Allele.Trait.HEIGHT)){
			//go back 1 allele to get length
			if(getDomAllele(hopper, geneIndex-1).getTrait().equals(Allele.Trait.LENGTH)){
				val1 = (float)getDomAllele(hopper, geneIndex-1).getValue();
			}
			//go forward 1 allele to get width
			if(getDomAllele(hopper, geneIndex+1).getTrait().equals(Allele.Trait.WIDTH)){
				val2 = (float)getDomAllele(hopper, geneIndex+1).getValue();
			}
		}
		//at a width allele
		else if(getDomAllele(hopper, geneIndex).getTrait().equals(Allele.Trait.WIDTH)){
			//go back 1 allele to get length
			if(getDomAllele(hopper, geneIndex-1).getTrait().equals(Allele.Trait.HEIGHT)){
				val1 = (float)getDomAllele(hopper, geneIndex-1).getValue();
			}
			//go back 2 alleles to get height
			if(getDomAllele(hopper, geneIndex-2).getTrait().equals(Allele.Trait.LENGTH)){
				val2 = (float)getDomAllele(hopper, geneIndex-2).getValue();
			}
		}

		//return the largest value times 1/10
		if(currentVal > val1 && currentVal > val2){
			if(currentVal*0.1f >= 1.0f)
				return currentVal*0.1f;
			else
				return 1.0f;
		}
		else if(val1 > currentVal && val1 > val2){
			if(val1*0.1f >= 1.0f)
				return val1*0.1f;
			else
				return 1.0f;
		}
		else if(val2 > currentVal && val2 > val1){
			if(val2*0.1f >= 1.0f)
				return val2*0.1f;
			else
				return 1.0f;
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
	public int getRuleDoF(Hopper hopper, int geneIndex){
		//TODO
		//go backwards through the gene list
		for(int i = geneIndex; i >= 0; i--){
			//if we get to a joint type allele
			if(getDomAllele(hopper, i).getTrait().equals(Allele.Trait.JOINT_TYPE)){
				return 1; //index was in DoF 1
			}
			//if we get to a DoF marker
			if(getDomAllele(hopper, i).getTrait().equals(Allele.Trait.DOF_MARKER)){
				return 2; //index was in DoF 2
			}
		}
		return 0;
	}

	/**
	 * Given a hopper and a gene index, returns the allele at that index in the
	 * hoppers genotype.
	 * 
	 * @param hopper - Hopper to extract allele from.
	 * @param geneIndex - Location of the allele in the genotype.
	 * @return Allele - The allele at the gene index in the genotype.
	 */
	public Allele getDomAllele(Hopper hopper, int geneIndex){
		return hopper.getChromosome().get(geneIndex).getDominant();
	}


	/**
	 * Takes in a hopper and determines if has a valid phenotype.
	 * This is done by cloning the hopper and checking for any
	 * exceptions generated. Any exception will indicate that
	 * the hopper is invalid.
	 * 
	 * @param hopper - Hopper to validate.
	 * @return true if valid hopper, false if invalid
	 */
	public boolean validHopper(Hopper hopper){
		Hopper validHopper = null;
		try {
			validHopper = new Hopper(hopper);
			return true;
		} catch (IllegalArgumentException | GeneticsException e) {
			return false;
		}
	}

	public Hopper insertAllele(Hopper hopper, int geneIndex, Allele allele)
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

		ArrayList<Gene> geneList = hopperToClimb.getChromosome();
		Allele recAllele = geneList.get(geneIndex).getRecessive();

		Gene gene = new Gene(allele, recAllele);

		//		System.out.println(geneList.get(geneIndex));
		//		System.out.println(gene);
		geneList.remove(geneIndex);
		geneList.add(geneIndex, gene);

		Genotype genotype = null;
		try{
			genotype = new Genotype(geneList);
			hopperToClimb = new Hopper(genotype);
			return hopperToClimb;
		} catch (IllegalArgumentException | GeneticsException e) {
			return null;
		}
	}
}
