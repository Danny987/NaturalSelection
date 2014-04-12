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
 * A class to facilitate piecemeal Block construction.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
public class BlockBuilder implements Builder {
	private float length = 0;
	private float height = 0;
	private float width = 0;
	private int indexToParent = -2;
	private Joint jointToParent = null;
	
	/**
	 * Instantiate an empty BlockBuilder.
	 */
	public BlockBuilder() {
		length = height = width = 0;
		indexToParent = -2;
		jointToParent = null;
	}
	
	/**
	 * Setter for length.
	 * 
	 * @param length Float to set as the length.
	 * @throws IllegalArgumentException if < 1.0.
	 */
	public void setLength(float length) throws IllegalArgumentException {
		if (length < 1.0f) {
			throw new IllegalArgumentException("Length must be >= 1.0f.");
		} else if (!isDimensionValid(length, "length")) {
			throw new IllegalArgumentException(
					"Largest dimension must be no more than 10x the smallest: "+
							"[" + length + ", " + height + ", " + width + "].");
		} else {
			this.length = length;
		}
	}
	
	/**
	 * Setter for height.
	 * 
	 * @param height Float to set as the height.
	 * @throws IllegalArgumentException if < 1.0.
	 */
	public void setHeight(float height) throws IllegalArgumentException {
		if (height < 1.0f) {
			throw new IllegalArgumentException("Height must be >= 1.0f.");
		} else if (!isDimensionValid(length, "height")) {
			throw new IllegalArgumentException(
					"Largest dimension must be no more than 10x the smallest: "+
					"[" + length + ", " + height + ", " + width + "].");
		} else {
			
			this.height = height;
		}
	}
	
	/**
	 * Setter for width.
	 * 
	 * @param width Float to set as the width.
	 * @throws IllegalArgumentException if < 1.0.
	 */
	public void setWidth(float width) throws IllegalArgumentException {
		if (width < 1.0f) {
			throw new IllegalArgumentException("Width must be >= 1.0f.");
		} else if (!isDimensionValid(width, "width")){
			throw new IllegalArgumentException(
					"Largest dimension must be no more than 10x the smallest: "+
					"[" + length + ", " + height + ", " + width + "].");
		} else {
			this.width = width;
		}
	}
	
	/**
	 * Setter for indexToParent.
	 * 
	 * @param indexToParent Array index of parent Block.
	 * @throws IllegalArgumentException if < 1.0.
	 */
	public void setIndexToParent(int indexToParent)
				throws IllegalArgumentException {
		if (indexToParent < Block.PARENT_INDEX_NONE) {
			throw new IllegalArgumentException(
					"Index of parent must be >= -1.");
		} else {
			this.indexToParent = indexToParent;
		}
	}
	
	/**
	 * Setter for jointToParent.
	 * 
	 * @param jointToParent Joint to set as jointToParent (can be null for
	 * 		                root).
	 */
	public void setJointToParent(Joint jointToParent) {
		this.jointToParent = jointToParent;
	}
	
	/**
	 * Checks if this is a root block, if jointToParent is null and
	 * indexToParent is -1.
	 * 
	 * @return True if this is a valid root block, false otherwise.
	 */
	public boolean isRootBlock() {
		return jointToParent == null &&
				indexToParent == Block.PARENT_INDEX_NONE;
	}
	
	/**
	 * A helper method for the setters that checks if the largest dimension is
	 * more than 10x the smallest.
	 * 
	 * @param value The new float value being set that should be checked against
	 *            the current dimension values.
	 * @param dimension String specifying which dimension to test.
	 * @return True if passed value is valid, false otherwise.
	 */
	public boolean isDimensionValid(float value, String dimension) {
		if (dimension.equals("length")) {
			if ((height > 0 && (value > height * 10 || value < width / 10))
			    || (width > 0 && (value > width * 10 || value < width / 10))) {
				return false;
			}
		}
		
		if (dimension.equals("height")) {
			if ((length > 0 && (value > length * 10 || value < length / 10))
			    || (width > 0 && (value > width * 10 || value < width / 10))) {
				return false;
			}
		}
		
		if (dimension.equals("width")) {
			if ((length > 0 && (value > length * 10 || value < length / 10))
				|| (height > 0 && (value > height * 10
						|| value < height / 10))) {
					return false;
				}
		}
		
		return true;
	}

	/**
	 * Converts the BlockBuilder into a Block. If any of the fields aren't set,
	 * returns null.
	 * 
	 * @return Block instantiated from this BlockBuilder or null if the
	 * 	           Builder's fields aren't set.
	 */
	public Block toBlock() {
		if (length < 1 || height < 1 || width < 1 ||
				indexToParent < Block.PARENT_INDEX_NONE) {
			return null;
		} else {
			return new Block(indexToParent, jointToParent, length,
					height, width);
		}
	}
	
}
