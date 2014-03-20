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
 * A Genotype class for the creatures.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
public class Genotype implements Cloneable {
	private ArrayList<Gene> chromosome;
	
	/**
	 * Instantiate a blank Genotype.
	 */
	public Genotype() {
		// TODO
	}
	
	/**
	 * Instantiate a new Genotype from two parents' Genotypes.
	 * 
	 * @param parent1 Genotype from parent 1.
	 * @param parent2 Genotype from parent 2.
	 */
	public Genotype(Genotype parent1, Genotype parent2) {
		// TODO
	}
	
	/**
	 * Evaluates the Genotype to create the Creature (phenotype).
	 * 
	 * @return Creature (phenotype) of the Genotype.
	 */
	public Creature getPhenotype() {
		// TODO
		Block[] body = null;
		Vector3 rootForwardStart = null;
		Vector3 rootUpStart = null;
		// Return a new Creature (phenotype) with the calculated values.
		return new Creature(body, rootForwardStart, rootUpStart);
	}
	
	/**
	 * Override of toString. Formats the returned String as the genes list
	 * enclosed in square brackets.
	 * 
	 * @return String containing genes list enclosed in square brackets.
	 */
	@Override
	public String toString() {
		StringBuilder gString = new StringBuilder("");
		gString.append('[');
		for (Gene g : chromosome) {
			gString.append(g.toString());
		}
		gString.append(']');
		
		return gString.toString();
	}
	
	/**
	 * Override of clone. Creates a deep clone of this Genotype.
	 * 
	 * @return Deep clone of this Genotype.
	 */
	@Override
	public Object clone() {
		// TODO
		return null;
	}
}
