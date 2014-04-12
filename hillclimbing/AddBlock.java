package creature.geeksquad.hillclimbing;


import creature.geeksquad.genetics.BlockBuilder;
import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.genetics.JointBuilder;
import creature.geeksquad.genetics.RuleBuilder;
import creature.geeksquad.library.Helper;
import creature.phenotype.Block;
import creature.phenotype.EnumJointSite;
import creature.phenotype.EnumJointType;
import creature.phenotype.EnumNeuronInputType;
import creature.phenotype.Joint;
import creature.phenotype.NeuronInput;
import creature.phenotype.Rule;

/**
 * Hill Climbing strategy that adds a block to a genotype.
 * 
 * @author Danny Gomez
 * @group Ramon A. Lovato
 * @group Marcos Lemus
 */
public class AddBlock extends Strategy{

	public AddBlock(MapHandler mapHandler){
		super(mapHandler);
	}

	@Override
	public Hopper climb(Hopper hopper) throws IllegalArgumentException,
	GeneticsException {

		boolean validBlock = false; //checks if block generated is valid
		int attempts = 0; //num of times attempted to make a valid block

		//clone original hopper
		Hopper hopperToClimb = null;
		try {
			hopperToClimb = new Hopper(hopper);
		} catch (IllegalArgumentException | GeneticsException e) {
			System.err.println("add block");
			throw e;
		}

		/**
		 * 
		 * Attempt to generate a valid block. The number of attempts is
		 * defaulted to 10. If a random block is created, or the # of
		 * attempts is exceeded, the loop ends.
		 */
		while(!validBlock && attempts < 10){
			//create the block builder
			BlockBuilder blockBuilder = new BlockBuilder();

			//Set random dimensions for the block. Hopefully the use of
			//random will produce some diversity in the tribe. These dimensions
			//will be hill climbed in the next generation.
			blockBuilder.setLength(Helper.RANDOM.nextFloat()*9 + 1);
			blockBuilder.setHeight(Helper.RANDOM.nextFloat()*9 + 1);
			blockBuilder.setWidth(Helper.RANDOM.nextFloat()*9 + 1);

			//get number of blocks in hopper genotype
			int numOfBlocks = hopperToClimb.getGenotype().size();

			//set index to parent
			blockBuilder.setIndexToParent(Helper.RANDOM.nextInt(numOfBlocks));

			//start joint builder
			JointBuilder jointBuilder = new JointBuilder();

			//set joint type to a random joint
			jointBuilder.setType(EnumJointType.values()
					[Helper.RANDOM.nextInt(EnumJointType.values().length)]);

			//set joint sites
			jointBuilder.setSiteOnChild(EnumJointSite.values()
					[Helper.RANDOM.nextInt(EnumJointSite.values().length)]);
			jointBuilder.setSiteOnParent(EnumJointSite.values()
					[Helper.RANDOM.nextInt(EnumJointSite.values().length)]);

			//set joint orientation
			jointBuilder.setOrientation(Helper.RANDOM.nextFloat()*5);

			//get joint DoF
			for(int i = 0; i < jointBuilder.getNumDoFs(); i++){
				//number of rules per DoF
				int numOfRules = Helper.RANDOM.nextInt(10);
				for(int j = 0; j < numOfRules; j++){
					//start rule builder
					RuleBuilder ruleBuilder = new RuleBuilder();

					NeuronInput neuronInput = getNeuronInput('A', i+1, jointBuilder.getNumDoFs(), 
							hopperToClimb.getChromosome().size());

					//add rules
					ruleBuilder.setNeuronInputA(neuronInput);
					
					neuronInput = getNeuronInput('B', i+1, jointBuilder.getNumDoFs(), 
							hopperToClimb.getChromosome().size());
					
					ruleBuilder.setNeuronInputB(neuronInput);
					
					neuronInput = getNeuronInput('C', i+1, jointBuilder.getNumDoFs(), 
							hopperToClimb.getChromosome().size());
					
					ruleBuilder.setNeuronInputC(neuronInput);
					
					neuronInput = getNeuronInput('D', i+1, jointBuilder.getNumDoFs(), 
							hopperToClimb.getChromosome().size());
					
					ruleBuilder.setNeuronInputD(neuronInput);
					
					neuronInput = getNeuronInput('E', i+1, jointBuilder.getNumDoFs(), 
							hopperToClimb.getChromosome().size());
					
					ruleBuilder.setNeuronInputE(neuronInput);
					
					
					ruleBuilder.setOp1(mapHandler.getNewBinary('1'));
					
					
					ruleBuilder.setOp2(mapHandler.getNewUnary('2'));
					
					
					ruleBuilder.setOp3(mapHandler.getNewBinary('3'));
					
					
					ruleBuilder.setOp4(mapHandler.getNewUnary('4'));

					Rule rule = ruleBuilder.toRule();

					//add to joint
					if(rule != null){
						jointBuilder.setRule(rule, i);
					}
				}
			}

			//create joint
			Joint joint = jointBuilder.toJoint();

			//set joint in block
			blockBuilder.setJointToParent(joint);

			//create block
			Block block = blockBuilder.toBlock();

			//add block to genotype
			hopperToClimb.getGenotype().addBlock(block, false);

			//clone hopper to see if block is valid
			@SuppressWarnings("unused")
			Hopper testHopper = null;
			try {
				testHopper = new Hopper(hopperToClimb);
				validBlock = true;
			} catch (IllegalArgumentException | GeneticsException e) {
				validBlock = false;
				attempts++;
				hopperToClimb = new Hopper(hopper);
			}
		}

		//if a valid block was add to the hopper.
		if(validBlock){
			return hopperToClimb; //return new hopper
		}

		//no valid block added, return original hopper
		return hopper;
	}

	
	/**
	 * Given a rule type and some information about the hopper, returns
	 * a NeuronInput. Mainly used for generating new rules when adding 
	 * new blocks.
	 * 
	 * @param ruleType - 'A', 'B', 'C', 'D', 'E'
	 * @param ruleDoF - DoF of the rule location
	 * @param jointDoF - DoFs of the joint the rule is being added to
	 * @param boxIndex -  index of the box the rule will be added to
	 * @return A new NeuronInput
	 */
	public NeuronInput getNeuronInput(char ruleType, int ruleDoF, int jointDoF, int boxIndex){
		//get a new rule from the maps based on the rule type
		EnumNeuronInputType newRuleValue = mapHandler.pickRuleValue(ruleType, ruleDoF);
		
		while(newRuleValue.equals(EnumNeuronInputType.CONSTANT)){
			newRuleValue = mapHandler.pickRuleValue(ruleType, ruleDoF);
		}

		//check what neuron input was obtained from the maps and return it
		if(newRuleValue.equals(EnumNeuronInputType.TIME)){
			return new NeuronInput(newRuleValue); //return new neuron
		}
		else if(newRuleValue.equals(EnumNeuronInputType.HEIGHT) || newRuleValue.equals(EnumNeuronInputType.TOUCH)){
			return new NeuronInput(newRuleValue, boxIndex);
		}
		else if(newRuleValue.equals(EnumNeuronInputType.JOINT)){
			return new NeuronInput(newRuleValue, boxIndex, jointDoF);
		}

		//return starting neuron if no change occurs
		return null;
	}
}
