package creature.geeksquad.hillclimbing;

import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Hopper;

/**
 * This Hill Climbing strategy was made in hopes of creating more
 * diversity between the populations. The strategy attempts to
 * create a randomly generated hopper and returns it. If the new
 * hopper is an improvement, the original hopper is replaced.
 * 
 * @author Danny Gomez
 * @group Ramon A. Lovato
 * @group Marcos Lemus
 */
public class RandomHopper extends Strategy{

	public RandomHopper(MapHandler mapHandler) {
		super(mapHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Hopper climb(Hopper hopper) 
			throws IllegalArgumentException, GeneticsException {

		int attempts = 20;

		for(int i = 0; i < attempts; i++){
			try{
				Hopper randomHopper = null;
				randomHopper = new Hopper();
				return randomHopper;
			}catch (IllegalArgumentException | GeneticsException e) {
			}
		}

		return hopper;
	}

}
