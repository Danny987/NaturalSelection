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
import creature.phenotype.Joint;
import creature.phenotype.NeuronInput;
import creature.phenotype.Rule;

/**
 * @author Daniel
 * 
 *
 */
public class AddBlock extends Strategy{

	Crossover crossover = null;
	
	public AddBlock(Crossover crossover){
		this.crossover = crossover;
	}
	
	@Override
	public Hopper climb(Hopper originalHopper) throws IllegalArgumentException,
	GeneticsException {
		// TODO Auto-generated method stub

		boolean validBlock = false;
		int attempts = 0;

		float startingFitness = originalHopper.getFitness();

		//clone original hopper
		Hopper hopperToClimb = null;
		try {
			hopperToClimb = new Hopper(originalHopper);
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

					//add rules
					ruleBuilder.setNeuronInputA(new NeuronInput(pickRuleValue('A',i+1)));
					ruleBuilder.setNeuronInputB(new NeuronInput(pickRuleValue('B',i+1)));
					ruleBuilder.setNeuronInputC(new NeuronInput(pickRuleValue('C',i+1)));
					ruleBuilder.setNeuronInputD(new NeuronInput(pickRuleValue('D',i+1)));
					ruleBuilder.setNeuronInputE(new NeuronInput(pickRuleValue('E',i+1)));
					ruleBuilder.setOp1(pickBinaryValue('1'));
					ruleBuilder.setOp2(pickUnaryValue('2'));
					ruleBuilder.setOp3(pickBinaryValue('3'));
					ruleBuilder.setOp4(pickUnaryValue('4'));

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
			hopperToClimb.getGenotype().addBlock(block, crossover);

			//clone hopper to see if block is valid
			Hopper testHopper = null;
			try {
				testHopper = new Hopper(hopperToClimb);
				validBlock = true;
			} catch (IllegalArgumentException | GeneticsException e) {
				validBlock = false;
				attempts++;
				hopperToClimb = new Hopper(originalHopper);
			}
		}

		if(validBlock){
			if(improved(hopperToClimb)){
				return hopperToClimb;
			}
		}
		
		return originalHopper;
	}
}
