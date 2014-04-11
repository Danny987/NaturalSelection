package creature.geeksquad.hillclimbing;

import java.util.ArrayList;

import creature.geeksquad.genetics.Allele;
import creature.geeksquad.genetics.Crossover;
import creature.geeksquad.genetics.Gene;
import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Genotype;
import creature.geeksquad.genetics.Allele.Trait;
import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.genetics.Population;
import creature.phenotype.Block;
import creature.phenotype.EnumJointSite;
import creature.phenotype.EnumJointType;
import creature.phenotype.EnumNeuronInputType;
import creature.phenotype.EnumOperatorBinary;
import creature.phenotype.EnumOperatorUnary;
import creature.phenotype.NeuronInput;

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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("done");
	}
}
