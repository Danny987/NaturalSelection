package creature.geeksquad.hillclimbing;

import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.library.Helper;

public class RemoveBlock extends Strategy {
	
	public RemoveBlock(MapHandler mapHandler) {
		super(mapHandler);
		// TODO Auto-generated constructor stub
	}

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
		
		if(mapsOn) mapHandler.updateRemoveBlockMap(boxIndex, 1);
		
		//clone hopper to make sure its valid
		Hopper testHopper = null;
		try {
			testHopper = new Hopper(hopperToClimb);
			return testHopper;
		} catch (IllegalArgumentException | GeneticsException e) {
			//return original
			return originalHopper;
		}
	}
}
