package creature.geeksquad.genetics;

import creature.phenotype.*;

public class TestCreatures {
	public static Genotype getGenotype() {
		Block[] body = getJoel1();
		try {
			return new Genotype(body);
		} catch (IllegalArgumentException | GeneticsException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unused")
	public static Block[] getJoel1() {
		Vector3.setDisplayDecimalPlaces(3);
		Vector3.test();

		Vector3 rootForward = Vector3.FORWARD;
		Vector3 rootUp = Vector3.UP;

		Block[] body = new Block[12];

		body[0] = new Block(Block.PARENT_INDEX_NONE, null, 3, 1, 4);

		// Joint(type, siteOnParent, siteOnChild, orientation)
		// Joint(type, siteOnParent, siteOnChild, orientation)
		Joint joint1 = new Joint(EnumJointType.HINGE,
				EnumJointSite.VERTEX_FRONT_SOUTHEAST,
				EnumJointSite.VERTEX_BACK_SOUTHWEST, (float) (Math.PI / 2));
		Joint joint2 = new Joint(EnumJointType.HINGE,
				EnumJointSite.VERTEX_FRONT_SOUTHWEST,
				EnumJointSite.VERTEX_BACK_SOUTHEAST, -(float) (Math.PI / 2));
		Joint joint3 = new Joint(EnumJointType.HINGE,
				EnumJointSite.VERTEX_BACK_SOUTHEAST,
				EnumJointSite.VERTEX_FRONT_SOUTHWEST, (float) (5 * Math.PI / 6));
		Joint joint4 = new Joint(EnumJointType.HINGE,
				EnumJointSite.VERTEX_BACK_SOUTHWEST,
				EnumJointSite.VERTEX_FRONT_SOUTHEAST,
				-(float) (5 * Math.PI / 6));

		Joint joint5 = new Joint(EnumJointType.TWIST, EnumJointSite.FACE_NORTH,
				EnumJointSite.FACE_BACK, 0);
		Joint joint6 = new Joint(EnumJointType.TWIST, EnumJointSite.FACE_FRONT,
				EnumJointSite.FACE_EAST, 0);
		Joint joint7 = new Joint(EnumJointType.TWIST, EnumJointSite.FACE_WEST,
				EnumJointSite.FACE_WEST, 0);

		Joint joint8 = new Joint(EnumJointType.RIGID, EnumJointSite.FACE_NORTH,
				EnumJointSite.FACE_NORTH, 0);
		Joint joint9 = new Joint(EnumJointType.RIGID, EnumJointSite.FACE_NORTH,
				EnumJointSite.FACE_NORTH, 0);
		Joint joint10 = new Joint(EnumJointType.RIGID,
				EnumJointSite.FACE_NORTH, EnumJointSite.FACE_NORTH, 0);
		Joint joint11 = new Joint(EnumJointType.RIGID,
				EnumJointSite.FACE_NORTH, EnumJointSite.FACE_NORTH, 0);

		body[1] = new Block(0, joint1, 1, 2, 1);
		body[2] = new Block(0, joint2, 1, 2, 1);
		body[3] = new Block(0, joint3, 1, 2, 1);
		body[4] = new Block(0, joint4, 1, 2, 1);
		body[5] = new Block(0, joint5, 1, 1, 1);
		body[6] = new Block(5, joint6, 1, 1, 1);
		body[7] = new Block(6, joint7, 1, 1, 1);

		body[8] = new Block(1, joint8, 1, 1, 1);
		body[9] = new Block(2, joint9, 1, 1, 1);
		body[10] = new Block(3, joint10, 1, 1, 1);
		body[11] = new Block(4, joint11, 1, 1, 1);

		Rule rule1 = new Rule();
		NeuronInput neuron1A = new NeuronInput(EnumNeuronInputType.CONSTANT, 1f);
		NeuronInput neuron1B = new NeuronInput(EnumNeuronInputType.CONSTANT, 0f);
		NeuronInput neuron1C = new NeuronInput(EnumNeuronInputType.CONSTANT, 0f);
		NeuronInput neuron1D = new NeuronInput(EnumNeuronInputType.CONSTANT, 0f);
		NeuronInput neuron1E = new NeuronInput(EnumNeuronInputType.CONSTANT,
				Float.MAX_VALUE);

		rule1.setInput(neuron1A, NeuronInput.A);
		rule1.setInput(neuron1B, NeuronInput.B);
		rule1.setInput(neuron1C, NeuronInput.C);
		rule1.setInput(neuron1D, NeuronInput.D);
		rule1.setInput(neuron1E, NeuronInput.E);

		rule1.setOp1(EnumOperatorBinary.ADD);
		rule1.setOp2(EnumOperatorUnary.IDENTITY);
		rule1.setOp3(EnumOperatorBinary.ADD);
		rule1.setOp4(EnumOperatorUnary.IDENTITY);

		Rule rule5 = new Rule();
		Rule rule6 = new Rule();
		Rule rule7 = new Rule();

		NeuronInput neuron5A = new NeuronInput(EnumNeuronInputType.CONSTANT, 1f);
		NeuronInput neuron5B = new NeuronInput(EnumNeuronInputType.CONSTANT, 0f);
		NeuronInput neuron5C = new NeuronInput(EnumNeuronInputType.CONSTANT, 0f);
		NeuronInput neuron5D = new NeuronInput(EnumNeuronInputType.CONSTANT, 0f);
		NeuronInput neuron5E = new NeuronInput(EnumNeuronInputType.CONSTANT,
				100.0f);
		NeuronInput neuron6E = new NeuronInput(EnumNeuronInputType.CONSTANT,
				-150.0f);
		NeuronInput neuron7E = new NeuronInput(EnumNeuronInputType.CONSTANT,
				100.0f);

		rule5.setInput(neuron5A, NeuronInput.A);
		rule5.setInput(neuron5B, NeuronInput.B);
		rule5.setInput(neuron5C, NeuronInput.C);
		rule5.setInput(neuron5D, NeuronInput.D);
		rule5.setInput(neuron5E, NeuronInput.E);

		rule6.setInput(neuron5A, NeuronInput.A);
		rule6.setInput(neuron5B, NeuronInput.B);
		rule6.setInput(neuron5C, NeuronInput.C);
		rule6.setInput(neuron5D, NeuronInput.D);
		rule6.setInput(neuron6E, NeuronInput.E);

		rule7.setInput(neuron5A, NeuronInput.A);
		rule7.setInput(neuron5B, NeuronInput.B);
		rule7.setInput(neuron5C, NeuronInput.C);
		rule7.setInput(neuron5D, NeuronInput.D);
		rule7.setInput(neuron7E, NeuronInput.E);

		rule5.setOp1(EnumOperatorBinary.ADD);
		rule5.setOp2(EnumOperatorUnary.IDENTITY);
		rule5.setOp3(EnumOperatorBinary.ADD);
		rule5.setOp4(EnumOperatorUnary.IDENTITY);

		rule6.setOp1(EnumOperatorBinary.ADD);
		rule6.setOp2(EnumOperatorUnary.IDENTITY);
		rule6.setOp3(EnumOperatorBinary.ADD);
		rule6.setOp4(EnumOperatorUnary.IDENTITY);

		rule7.setOp1(EnumOperatorBinary.ADD);
		rule7.setOp2(EnumOperatorUnary.IDENTITY);
		rule7.setOp3(EnumOperatorBinary.ADD);
		rule7.setOp4(EnumOperatorUnary.IDENTITY);

		joint1.addRule(rule1, 0);
		joint2.addRule(rule1, 0);
		joint3.addRule(rule1, 0);
		joint4.addRule(rule1, 0);

		joint5.addRule(rule5, 0);
		joint6.addRule(rule6, 0);
		joint7.addRule(rule7, 0);
		
		return body;

//		Creature mycritter = new Creature(body, rootForward, rootUp);
	}
	
	@SuppressWarnings("unused")
	public static Block[] getJoel2() {
		Vector3.setDisplayDecimalPlaces(3);
	    Vector3.test();
	  
	    Vector3 rootForward = Vector3.FORWARD;
	    Vector3 rootUp = Vector3.UP;
	    
	    Block[] body = new Block[8];
	    

	    body[0] = new Block(Block.PARENT_INDEX_NONE, null, 3, 1, 4); 
	    EnumJointSite childsite  =EnumJointSite.FACE_WEST;  //EnumJointSite.FACE_NORTH;
	    
	    //                 Joint(type,                    siteOnParent,                         siteOnChild,                 orientation)    //                 Joint(type,                    siteOnParent,                         siteOnChild,                 orientation)
	    Joint joint1 = new Joint(EnumJointType.TWIST, EnumJointSite.FACE_BACK, childsite, 0);
	    Joint joint2 = new Joint(EnumJointType.TWIST, EnumJointSite.FACE_EAST, childsite, 0);
	    Joint joint3 = new Joint(EnumJointType.TWIST, EnumJointSite.FACE_FRONT, childsite,0);
	    Joint joint4 = new Joint(EnumJointType.TWIST, EnumJointSite.FACE_NORTH, childsite,0);
	    Joint joint5 = new Joint(EnumJointType.TWIST, EnumJointSite.FACE_SOUTH, childsite, 0);
	    Joint joint6 = new Joint(EnumJointType.TWIST, EnumJointSite.FACE_WEST, childsite, 0);
	    
	    Joint joint7 = new Joint(EnumJointType.SPHERICAL, EnumJointSite.FACE_WEST, childsite, 0);
	    
	    
	    body[1] = new Block(0, joint1, 1, 1, 1); 
	    body[2] = new Block(0, joint2, 1, 1, 1); 
	    body[3] = new Block(0, joint3, 1, 1, 1); 
	    body[4] = new Block(0, joint4, 1, 1, 1); 
	    body[5] = new Block(0, joint5, 1, 1, 1); 
	    body[6] = new Block(0, joint6, 1, 1, 1); 
	    
	    body[7] = new Block(5, joint7, 1, 1, 1); 
	   
	    
	    Rule rule1 = new Rule();
	    NeuronInput neuron1A = new NeuronInput(EnumNeuronInputType.CONSTANT, 100f);
	    NeuronInput neuron1B = new NeuronInput(EnumNeuronInputType.CONSTANT, 100f);
	    NeuronInput neuron1C = new NeuronInput(EnumNeuronInputType.CONSTANT, 100f);
	    NeuronInput neuron1D = new NeuronInput(EnumNeuronInputType.CONSTANT, 100f);
	    NeuronInput neuron1E = new NeuronInput(EnumNeuronInputType.CONSTANT, 100f);
	    
	    rule1.setInput(neuron1A, NeuronInput.A);
	    rule1.setInput(neuron1B, NeuronInput.B);
	    rule1.setInput(neuron1C, NeuronInput.C);
	    rule1.setInput(neuron1D, NeuronInput.D);
	    rule1.setInput(neuron1E, NeuronInput.E);
	    
	    rule1.setOp1(EnumOperatorBinary.ADD);
	    rule1.setOp2(EnumOperatorUnary.IDENTITY);
	    rule1.setOp3(EnumOperatorBinary.ADD);
	    rule1.setOp4(EnumOperatorUnary.IDENTITY);


	    Rule rule2 = new Rule();
	    NeuronInput neuron2A = new NeuronInput(EnumNeuronInputType.TIME);
	    NeuronInput neuron2B = new NeuronInput(EnumNeuronInputType.CONSTANT, 100f);
	    NeuronInput neuron2C = new NeuronInput(EnumNeuronInputType.CONSTANT, 0f);
	    NeuronInput neuron2D = new NeuronInput(EnumNeuronInputType.CONSTANT, 100f);
	    NeuronInput neuron2E = new NeuronInput(EnumNeuronInputType.CONSTANT, 0f);
	    
	    rule2.setInput(neuron2A, NeuronInput.A);
	    rule2.setInput(neuron2B, NeuronInput.B);
	    rule2.setInput(neuron2C, NeuronInput.C);
	    rule2.setInput(neuron2D, NeuronInput.D);
	    rule2.setInput(neuron2E, NeuronInput.E);
	    
	    rule2.setOp1(EnumOperatorBinary.MULTIPLY);
	    rule2.setOp2(EnumOperatorUnary.SIN);
	    rule2.setOp3(EnumOperatorBinary.ADD);
	    rule2.setOp4(EnumOperatorUnary.IDENTITY);


	    joint1.addRule(rule1, 0);
	    joint2.addRule(rule1, 0);
	    joint3.addRule(rule1, 0);
	    joint4.addRule(rule1, 0);
	    joint5.addRule(rule1, 0);
	    joint6.addRule(rule1, 0);
	    
	    joint7.addRule(rule1, 0);
	    joint7.addRule(rule2, 1);

	    return body;
	}
	
	public static void main(String[] args) {
		Genotype genotype = getGenotype();
		Creature phenotype = genotype.getPhenotype();
		System.out.println(genotype);
		System.out.println(phenotype);
		for (int i = 0; i < 20; i++) {
			System.out.println(phenotype.advanceSimulation());
		}
	}
	
}
