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

import java.util.ArrayList;

import creature.phenotype.*;

/**
 * A class to facilitate piecemeal Joint construction.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
public class JointBuilder implements Builder {
	public static final int JOINT_TYPE_NULL = -1;
	
	private EnumJointType type;
	private EnumJointSite siteOnParent;
	private EnumJointSite siteOnChild;
	private float orientation;
	private ArrayList<Rule>[] ruleTable;

	/**
	 * Instantiate an empty JointBuilder.
	 */
	public JointBuilder() {
		type = null;
		siteOnParent = null;
		siteOnChild = null;
		orientation = 0.0f;
		ruleTable = null;
	}

	/**
	 * Setter for type.
	 * 
	 * @param type EnumJointType to set as the Joint type.
	 * @throws IllegalArgumentException if Joint type is null.
	 */
	@SuppressWarnings("unchecked")
	public void setType(EnumJointType type) throws IllegalArgumentException {
		if (type == null) {
			throw new IllegalArgumentException("Joint type cannot be null.");
		} else {
			this.type = type;
			int dof = type.getDoF();
			ruleTable = new ArrayList[dof];
			for (int i = 0; i < dof; i++) {
				ruleTable[i] = new ArrayList<Rule>();
			}
		}
	}

	/**
	 * Setter for siteOnParent.
	 * 
	 * @param siteOnParent EnumJointSite to set as the Joint site on parent.
	 * @throws IllegalArgumentException if Joint site is null.
	 */
	public void setSiteOnParent(EnumJointSite siteOnParent)
				throws IllegalArgumentException {
		if (siteOnParent == null) {
			throw new IllegalArgumentException(
					"Joint site on parent cannot be null.");
		} else {
			this.siteOnParent = siteOnParent;
		}
	}
	
	/**
	 * Setter for siteOnChild.
	 * 
	 * @param siteOnParent EnumJointSite to set as the Joint site on parent.
	 * @throws IllegalArgumentException if Joint site is null.
	 */
	public void setSiteOnChild(EnumJointSite siteOnChild)
					throws IllegalArgumentException{
		if (siteOnChild == null) {
			throw new IllegalArgumentException(
					"Joint site on child cannot be null.");
		} else {
			this.siteOnChild = siteOnChild;
		}
	}
	
	/**
	 * Setter for orientation.
	 * 
	 * @param orientation Orientation to set for this Joint as a float.
	 */
	public void setOrientation(float orientation) {
		this.orientation = orientation;
	}
	
	/**
	 * Adds a rule to the rule table for the specified degree of freedom.
	 * 
	 * @param rule Rule to add to the table.
	 * @param dof Degree of freedom to which to add the Rule.
	 * @param position Optional position at which to insert the rule; if not
	 *                 provided, defaults to end of list. Arguments past the
	 *                 first are ignored.
	 * @throws IllegalArgumentException if Rule is null, or if dof or index is
	 * 		   out of bounds.
	 */
	public void setRule(Rule rule, int dof, int...position)
				throws IllegalArgumentException {
		int jointDoF;
		
		if (type == null) {
			throw new IllegalArgumentException(
					"Must set Joint type before adding Rules.");
		} else {
			jointDoF = type.getDoF();
		}
			
		if (dof < 0) {
			throw new IllegalArgumentException(
					"Degree of freedom cannot be negative.");
		} else if (dof > 1) {
			throw new IllegalArgumentException(
					"Degree of freedom must be 0 or 1.");
		// Short-circuits if type is null.
		} else if (dof >= jointDoF) {
			throw new IllegalArgumentException(
					"Degree of freedom out of bounds; for joint type " + type +
					", DoF must be in integer range [0, " + (jointDoF - 1) +
					"].");
		}
		
		int index;
		if (position.length > 0) {
			if (position[0] > ruleTable[dof].size()) {
				throw new IllegalArgumentException(
						"Position " + position + " exceeds size of rule table."
						+ "For degree of freedom " + dof + ", "
						+ "table size is " + ruleTable[dof].size() + ".");
			} else {
				index = position[0];
			}
		} else {
			index = ruleTable[dof].size();
		}
		
		ruleTable[dof].add(index, rule);
	}
	
	/**
	 * Getter for the degrees of freedom for this Joint's type.
	 * 
	 * @return Degrees of freedom of this Joint's type or JOINT_TYPE_NULL (-1)
	 * 		   if type hasn't been assigned yet or is somehow otherwise null.
	 */
	public int getNumDoFs() {
		if (type == null) {
			return JOINT_TYPE_NULL;
		} else {
			return type.getDoF();
		}
	}
	
	/**
	 * Getter for this RuleBuilder's currently set Joint type.
	 * 
	 * @return This RuleBuilder's currently set EnumJointType type or null if 
	 * 		       not set.
	 */
	public EnumJointType getJointType() {
		return type;
	}
	
	/**
	 * Converts the JointBuilder into a Joint. If any of the fields except the
	 * Rule table aren't set, returns null.
	 * 
	 * @return Joint instantiated from this JointBuilder or null if any of the
	 * 			     fields except the Rule table aren't set.
	 */
	public Joint toJoint() {
		if (type == null || siteOnParent == null || siteOnChild == null ||
				ruleTable == null) {
			return null;
		} else {
			Joint joint = new Joint(type, siteOnParent, siteOnChild,
								    orientation);
			int dof = type.getDoF();
			if (dof > 0) {
				for (Rule r : ruleTable[0]) {
					joint.addRule(r, EnumJointType.DOF_1);
				}
			}
			if (dof > 1) {
				for (Rule r : ruleTable[1]) {
					joint.addRule(r, EnumJointType.DOF_2);
				}
			}
			return joint;
		}
	}
	
}