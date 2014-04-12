package creature.geeksquad.hillclimbing;


import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.genetics.Population;

/**
 * Hill Climbing tester. Performs hill climbing for a number of time
 * on a randomly generated population.
 * 
 * @author Danny Gomez
 * @group Ramon A. Lovato
 * @group Marcos Lemus
 */
public class BrainTester {

	public BrainTester(){

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Population population = new Population(100);

		//create a new brrain for this tribe
		TribeBrain brain = new TribeBrain();
		
		//Hopper hopper = population.get(0);
		@SuppressWarnings("unused")
		Hopper hopper = null;
		
		//send hopper to hill climbing
		//returns a hill climbed hopper
		for(int i = 0; i < 999; i++){
			System.out.println(i);
			for(int j = 0; j < population.size(); j++){
				try {

					//Genotype.printChromosome(hopper.getChromosome());
					
					hopper = brain.performHillClimbing(population.get(j));
				} catch (IllegalArgumentException | GeneticsException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("done");
	}
}
