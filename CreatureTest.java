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
 * A testing class for the creatures.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
public class CreatureTest {
	private ArrayList<Creature> creatures;
	
	/**
	 * Instantiates CreatureTest.
	 */
	public CreatureTest() {
		creatures = new ArrayList<Creature>();
	}
	
	/**
	 * Spawn a new creature.
	 */
	public void spawnNew() {
		Block[] body = new Block[50];
		
		body[0] = new Block(null, 1.0f, 1.0f, 1.0f);
		
		Vector3 rootForwardStart = new Vector3(0, 0, 1);
		Vector3 rootUpStart = new Vector3(0, 1, 0);
		
		Creature creature = new Creature(body, rootForwardStart, rootUpStart);
	}
	
	/**
	 * @param args String array of command-line arguments.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
