package creature.geeksquad.hillclimbing;


import creature.geeksquad.genetics.BlockBuilder;
import creature.geeksquad.genetics.Crossover;
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
 * @author Daniel
 * 
 *
 */
public class AddBlock extends Strategy{

	public AddBlock(MapHandler mapHandler){
		super(mapHandler);
	}

	@Override
	public Hopper climb(Hopper hopper) throws IllegalArgumentException,
	GeneticsException {

		boolean validBlock = false;
		int attempts = 0;

		//clone original hopper
		Hopper hopperToClimb = null;
		try {
			hopperToClimb = new Hopper(hopper);
		} catch (IllegalArgumentException | GeneticsException e) {
			System.err.println("add block");
			throw e;
		}

		while(!validBlock && attempts < 10){
			//create the block builder
			BlockBuilder blockBuilder = new BlockBuilder();

			//set block dimension
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

		if(validBlock){
			return hopperToClimb;
		}

		return hopper;
	}

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
