package creature.geeksquad.hillclimbing;

import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.library.Helper;

public class RemoveBlock extends Strategy {
	
	public Hopper climb(Hopper originalHopper) throws IllegalArgumentException,
	GeneticsException{
		
		int boxIndex = 0;
		
		//clone original hopper
		Hopper hopperToClimb = null;
		try {
			hopperToClimb = new Hopper(originalHopper);
		} catch (IllegalArgumentException | GeneticsException e) {
			System.err.println("remove block");
			throw e;
		}
		
		
		//pick a random block in the genotype
		boxIndex = Helper.RANDOM.nextInt(hopperToClimb.getGenotype().size());
		
		//remove block at index
		hopperToClimb.getGenotype().removeBlock(boxIndex);
		
		//clone hopper to make sure its valid
		Hopper testHopper = null;
		try {
			testHopper = new Hopper(hopperToClimb);
		} catch (IllegalArgumentException | GeneticsException e) {
			//update map
			updateRemoveBlockMap(boxIndex, 1);
			//return original
			return originalHopper;
		}
		
		
		if(improved(hopperToClimb)){
			//update map
			updateRemoveBlockMap(boxIndex, 1);
			//return climbed hopper
			return hopperToClimb;
		}
		else
		{
			//update map
			updateRemoveBlockMap(boxIndex, -1);
			//return original
			return originalHopper;
		}
	}
}
