/**
 * Evolving Virtual Jumping Creatures
 * CS 351, Project 2
 * 
 * Team members:
 * Ramon A. Lovato
 * Danny Gomez
 * Marcos Lemus
 */
package creature.geeksquad.genetics;

import creature.phenotype.*;

/**
 * A class to facilitate piecemeal Rule construction.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
public class RuleBuilder implements Builder {
	  private NeuronInput[] input = new NeuronInput[NeuronInput.TOTAL_INPUTS]; 
	  private EnumOperatorBinary op1;
	  private EnumOperatorUnary op2; 
	  private EnumOperatorBinary op3;
	  private EnumOperatorUnary op4;
	  
	  /**
	   * Instantiate an empty RuleBuilder.
	   */
	  public RuleBuilder() {
		  for (int i = 0; i < NeuronInput.TOTAL_INPUTS; i++) {
			  input[i] = null;
		  }
		  op1 = null;
		  op2 = null;
		  op3 = null;
		  op4 = null;
	  }
	  
	  /**
	   * Setter for NeuronInput.
	   * 
	   * @param input NeuronInput to set.
	   * @param index Which input to set.
	   */
	  public void setNeuronInput(NeuronInput input, int index)
			  	throws IllegalArgumentException {
		  if (input == null) {
			  throw new IllegalArgumentException(
					  "Neuron input cannot be null.");
		  } else if (index < 0 || index >= NeuronInput.TOTAL_INPUTS) {
			  throw new IllegalArgumentException(
					  "Neuron input index " + index + "out of bounds. " +
					  "Must be in range [0, " + NeuronInput.TOTAL_INPUTS +
					  "].");
		  }
		  
		  this.input[index] = input;
	  }
	  
	  /**
	   * Setter for NeuronInput A.
	   * 
	   * @param input NeuronInput to set for input A.
	   */
	  public void setNeuronInputA(NeuronInput input)
			  	throws IllegalArgumentException {
		  if (input == null) {
			  throw new IllegalArgumentException(
					  "Neuron input A cannot be null.");
		  }
		  
		  this.input[NeuronInput.A] = input;
	  }
	  
	  /**
	   * Setter for NeuronInput B.
	   * 
	   * @param input NeuronInput to set for input B.
	   */
	  public void setNeuronInputB(NeuronInput input)
			  	throws IllegalArgumentException {
		  if (input == null) {
			  throw new IllegalArgumentException(
					  "Neuron input B cannot be null.");
		  }
		  
		  this.input[NeuronInput.B] = input;
	  }
	  
	  /**
	   * Setter for NeuronInput C.
	   * 
	   * @param input NeuronInput to set for input C.
	   */
	  public void setNeuronInputC(NeuronInput input)
			  	throws IllegalArgumentException {
		  if (input == null) {
			  throw new IllegalArgumentException(
					  "Neuron input C cannot be null.");
		  }
		  
		  this.input[NeuronInput.C] = input;
	  }
	  
	  /**
	   * Setter for NeuronInput D.
	   * 
	   * @param input NeuronInput to set for input D.
	   */
	  public void setNeuronInputD(NeuronInput input)
			  	throws IllegalArgumentException {
		  if (input == null) {
			  throw new IllegalArgumentException(
					  "Neuron input D cannot be null.");
		  }
		  
		  this.input[NeuronInput.D] = input;
	  }
	  
	  /**
	   * Setter for NeuronInput E.
	   * 
	   * @param input NeuronInput to set for input E.
	   */
	  public void setNeuronInputE(NeuronInput input)
			  	throws IllegalArgumentException {
		  if (input == null) {
			  throw new IllegalArgumentException(
					  "Neuron input E cannot be null.");
		  }
		  
		  this.input[NeuronInput.E] = input;
	  }
	  
	  /**
	   * Setter for op1.
	   * 
	   * @param op EnumOperatorBinary to set as op1.
	   */
	  public void setOp1(EnumOperatorBinary op)
			  	throws IllegalArgumentException {
		  if (op == null) {
			  throw new IllegalArgumentException(
					  "Binary operator 1 cannot be null.");
		  }
		  
		  op1 = op;
	  }
	  
	  /**
	   * Setter for op2.
	   * 
	   * @param op EnumOperatorUnary to set as op2.
	   */
	  public void setOp2(EnumOperatorUnary op)
			  	throws IllegalArgumentException {
		  if (op == null) {
			  throw new IllegalArgumentException(
					  "Unary operator 2 cannot be null.");
		  }
		  
		  op2 = op;
	  }
	  
	  /**
	   * Setter for op3.
	   * 
	   * @param op EnumOperatorBinary to set as op3.
	   */
	  public void setOp3(EnumOperatorBinary op)
			  	throws IllegalArgumentException {
		  if (op == null) {
			  throw new IllegalArgumentException(
					  "Binary operator 3 cannot be null.");
		  }
		  
		  op3 = op;
	  }
	  
	  /**
	   * Setter for op4.
	   * 
	   * @param op EnumOperatorUnary to set as op4.
	   */
	  public void setOp4(EnumOperatorUnary op)
			  	throws IllegalArgumentException {
		  if (op == null) {
			  throw new IllegalArgumentException(
					  "Unary operator 4 cannot be null.");
		  }
		  
		  op4 = op;
	  }
	  
	  /**
	   * Converts the RuleBuilder into a Rule. If any of the fields aren't set,
	   * returns null.
	   * 
	   * @return Rule instantiated from this RuleBuilder. If any of the fields
	   * 			  aren't set, returns null.
	   */
	  public Rule toRule() {
		  for (int i = 0; i < NeuronInput.TOTAL_INPUTS; i++) {
			  if (input[i] == null) {
				  return null;
			  }
		  }
		  
		  if (op1 == null || op2 == null || op3 == null || op4 == null) {
			  return null;
		  } else {
			  Rule rule = new Rule();
			  for (int i = 0; i < NeuronInput.TOTAL_INPUTS; i++) {
				  rule.setInput(input[i], i);
			  }
			  rule.setOp1(op1);
			  rule.setOp2(op2);
			  rule.setOp3(op3);
			  rule.setOp4(op4);
			  return rule;
		  }
	  }
	  
}
