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

import java.util.*;

import creature.geeksquad.library.Helper;
import creature.geeksquad.genetics.Crossover.Strategy;
import creature.geeksquad.hillclimbing.TribeBrain;

/**
 * A class for a sortable Population of Hoppers in an extended ArrayList.
 * 
 * @author Ramon A. Lovato
 * @group Danny Gomez
 * @group Marcos Lemus
 */
@SuppressWarnings("serial")
public class Population extends ArrayList<Hopper> {
	private static Random random = Helper.RANDOM;
	private int generations;
	// Sub-collections allow the Population to separate the Hoppers that need
	// special handling from those in the general population.
	private final ArrayList<Hopper> breeders;
	// The Crossover module this Population will use.
	private Crossover crossover;
	// The hill-climbing Tribe brain for this Population.
	private TribeBrain brain = new TribeBrain();
	// A pointer to the Hopper with the highest fitness.
	private Hopper overachiever = null;
	
	/**
	 * The default constructor creates an empty Population.
	 */
	public Population() {
		super();
		generations = 0;
		breeders = new ArrayList<Hopper>();
		crossover = new Crossover();
		sort();
	}
	
	/**
	 * Instantiate a new Population containing randomly generated seed Hoppers.
	 * 
	 * @param num Number of random Hoppers to create.
	 */
	public Population(int num) {
		this();
		int i = 0;
		while (i < num) {
			try {
				super.add(new Hopper());
				i++;
			} catch (IllegalArgumentException | GeneticsException ex) {
				System.out.println("Creature[" + i + "] " + ex
								   + " Rebuilding.");
			}
		}
	}
	
	/**
	 * A static method that performs interpopulation crossover selection for
	 * two Populations. Returns nothing since it modifies the Populations
	 * directly.
	 * 
	 * @param pop1 First Population to interbreed.
	 * @param pop2 Second Population to interbreed.
	 */
	public static void interbreed(Population pop1, Population pop2) {
		// Make breeder lists of the highest-fitness creatures from each
		// Population. Also get a copy of each Population's Crossover module.
		ArrayList<Hopper> breeders1;
		ArrayList<Hopper> breeders2;
		ArrayList<Hopper> children1 = new ArrayList<Hopper>();
		ArrayList<Hopper> children2 = new ArrayList<Hopper>();
		Crossover crossover1;
		Crossover crossover2;
		synchronized (pop1) {
			pop1.moveBreeders();
			breeders1 = new ArrayList<Hopper>(pop1.breeders);
			for (Hopper h : breeders1) {
				h.increaseBreedCount();
			}
			crossover1 = new Crossover(pop1.crossover);
			pop1.flushBreeders();
		}
		
		int size = breeders1.size();
				
		synchronized (pop2) {
			// Passing the size of breeders1 as an argument guarantees that
			// both collections have the same number of Hoppers.
			pop2.moveBreeders(size);
			breeders2 = new ArrayList<Hopper>(pop2.breeders);
			for (Hopper h : breeders2) {
				h.increaseBreedCount();
			}
			crossover2 = new Crossover(pop2.crossover);
			pop2.flushBreeders();
		}
		Collections.shuffle(breeders1);
		Collections.shuffle(breeders2);
		
		// Create a special Crossover object for this occasion.
		Crossover cross = new Crossover(crossover1, crossover2);
		
		for (int i = 0; i < size; i++) {
			Hopper parentA = breeders1.get(i);
			Hopper parentB = breeders2.get(i);
			// Pick a random Crossover strategy.
			Strategy strategy = Strategy.values()
					[random.nextInt(Strategy.values().length)];
			// Determine which Population the offspring
			try {
				Hopper[] offspring = cross.crossover(
						parentA, parentB, strategy);
				if (offspring != null) {
					for (int j = 0; j < offspring.length; j++) {
						Hopper child = offspring[j];
						if (child != null) {
							if (j % 2 == 0) {
								children1.add(child);
							} else {
								children2.add(child);
							}
						}
					}
				}
			} catch (IllegalArgumentException | GeneticsException ex) {
				System.out.println(
						"Interbreed produced offspring invalid. Continuing.");
			}
		}
		
		// Add the children to their respective populations.
		synchronized (pop1) {
			pop1.cull(children1.size());
			pop1.addAll(children1);
		}
		synchronized(pop2) {
			pop2.cull(children2.size());
			pop2.addAll(children2);
		}
		
	}
	
	/**
	 * Update the population.
	 */
	public void update() {
		generations++;
		hillClimb();
		// Hill climbing the population will change the creatures, which
		// means their sorting will no longer be valid. However,
		// moveBreeders will sort them again, so it's fine.
		moveBreeders();
		int count = breed();
		// Like above, breeding will change the creatures in the collection,
		// but cull will sort them again first.
		cull(count);
		if (size() > 0) {
			overachiever = get(size() - 1);
		}
	}
	
	/**
	 * Breed, perform selection and crossover, within the population.
	 * 
	 * @return The number of new, valid Hoppers that were added to the
	 * 		   population. Needed to tell how many Hoppers should be culled.
	 */
	private int breed() {
		ArrayList<Hopper> children = new ArrayList<Hopper>();
		// In the interest of preserving diversity, shuffle the breeders
		// list so the Hoppers within it match up semi-randomly.
		Collections.shuffle(breeders);
		
		for (int i = 0; i < breeders.size(); i++) {
			Hopper parentA = breeders.get(i);
			// Check to make sure there's another parent to match it with.
			if (i + 1 >= breeders.size()) {
				// If not, remove the highest-fitness creature from the
				// general Population and add it instead.
				synchronized (this) {
					breeders.add(remove(size() - 1));
				}
			}
			Hopper parentB = breeders.get(++i);
			// Pick a random Crossover strategy.
			Strategy strategy = Strategy.values()
					[random.nextInt(Strategy.values().length)];
			try {
				Hopper[] offspring = crossover.crossover(
						parentA, parentB, strategy);
				if (offspring != null) {
					for (Hopper h : offspring) {
						if (h != null) {
							children.add(h);
						}
					}
				}
			} catch (IllegalArgumentException | GeneticsException ex) {
				System.out.println(
						"Breed offspring invalid. Continuing.");
			} finally {
				parentA.increaseBreedCount();
				parentB.increaseBreedCount();
			}
		}
		// flushBreeders is already synchronized.
		flushBreeders();
		synchronized (this) {
			addAll(children);
		}
		
		return children.size();
	}
	
	/**
	 * Perform hill-climbing on all members of the Population.
	 */
	private void hillClimb() {
		int size = size();
		for (int i = 0; i < size; i++) {
			Hopper original;
			synchronized (this) {
				original = super.get(i);
			}
			try {
				Hopper newHotness = brain.performHillClimbing(original);
				newHotness.hillClimbed();
				synchronized (this) {
					remove(original);
					super.add(newHotness);
				}
			} catch (IllegalArgumentException ex) {
				System.out.println(
					"HillClimbing produced an illegal creature. Skipping.");
			}
		}
	}
	
	/**
	 * Move the top 20% most fit Hoppers into the breeders list.
	 */
	private void moveBreeders() {
		moveBreeders(Helper.BREED_PERCENTAGE);
	}
	
	/**
	 * Move a percentage of the highest-fitness Hoppers into the breeders
	 * list.
	 * 
	 * @param f Percentage of the highest-fitness Hoppers to move, as a float.
	 */
	private void moveBreeders(float f) {
		sort();
		synchronized (this) {
			int size = size();
			int stop = (int) (size - (f * size));
			if (stop >= size) {
				breeders.addAll(this);
				clear();
			} else {
				for (int i = size - 1; i >= stop; i--) {
					breeders.add(remove(i));
				}
			}
		}
	}
	
	/**
	 * Move all Hoppers in the breeders list back into the general population.
	 */
	private void flushBreeders() {
		synchronized (this) {
			addAll(breeders);
		}
		breeders.clear();
	}
	
	/**
	 * Kill off the lowest-fitness n individuals in the general Population.
	 * 
	 * @param n Number of individuals to kill off.
	 */
	private void cull(int n) {
		sort();
		if (n < super.size()) {
			synchronized (this) {
				removeRange(0, n);
			}
		} else {
			synchronized (this) {
				clear();
			}
		}
	}
	
	/**
	 * Getter a clone of the Hopper with the highest fitness.
	 * 
	 * @return Deep clone of Hopper with the highest fitness.
	 */
	public Hopper getOverachiever() {
		Hopper newGuy = null;
		try {
			synchronized (this) {
				newGuy = new Hopper(overachiever);
			}
		// Should never fail since it's cloning a Hopper that's already
		// valid.
		} catch (IllegalArgumentException | GeneticsException e) {
			System.out.println("Cloning Hopper for getOverachiever failed.");
		}
		return newGuy;
	}
	
	/**
	 * Getter for the number of generations this population has gone through.
	 * 
	 * @return Number of generations this population has had.
	 */
	public int getGenerations() {
		return generations;
	}
	
	/**
	 * Get the average fitness of this Population. Takes n time since it has
	 * to iterate over the whole array. This method makes no guarantees about
	 * the accuracy of its result.
	 * 
	 * @return Average fitness of Population as a float.
	 */
	public float getAverageFitness() {
		float sum = 0.0f;
		ArrayList<Hopper> hoppers;
		synchronized (this) {
			hoppers = new ArrayList<Hopper>(this);
		}
		int size = super.size();
		for (Hopper h : hoppers) {
			sum += h.getFitness();
		}

		return sum / size;
	}
	
	/**
	 * Sort this Population according to its natural ordering: ascending Hopper
	 * fitness.
	 */
	private void sort() {
		synchronized (this) {
			Collections.sort(this);
		}
	}
	
	/**
	 * Override of add - adds a copy of the requested Hopper. Since this always
	 * adds the Hopper to the end of the list, it doesn't need to be
	 * synchronized.
	 * 
	 * @param hopper Hopper to add to the Population.
	 * @return True if add succeeded, else false.
	 */
	@Override
	public boolean add(Hopper hopper) {
		try {
			super.add(new Hopper(hopper));
		} catch (IllegalArgumentException | GeneticsException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Override of add by index - adds a copy of the requested Hopper. Since
	 * this always inserts the Hopper into the middle of the collection, it
	 * does need to be synchronized.
	 * 
	 * @param index Index at which to insert the Hopper.
	 * @param hopper Hopper to add to the Population.
	 */
	@Override
	public void add(int index, Hopper hopper) {
		try {
			synchronized (this) {
				super.add(index, new Hopper(hopper));
			}
		} catch (IllegalArgumentException | GeneticsException e) {
			System.out.println("Adding Hopper to Population failed.");
		}
	}
	
	/**
	 * Override of get by index - returns a copy of the requested Hopper.
	 * 
	 * @param index Index of Hopper of which to return a copy.
	 * @return Deep clone of the Hopper at index.
	 */
	@Override
	public Hopper get(int index) {
		try {
			synchronized (this) {
				return new Hopper(super.get(index));
			}
		} catch (IllegalArgumentException | GeneticsException e) {
			return null;
		}
	}
	
	/**
	 * Override of size returns the current size of the Population.
	 *
	 * @return Size of the population.
	 */
	@Override
	public int size() {
		synchronized (this) {
			return super.size();
		}
	}
	
	/**
	 * Override of toString: returns an exportable representation of this
	 * population's current state.
	 * 
	 * @return String representation of this population's current state.
	 */
	@Override
	public String toString() {
		ArrayList<Hopper> hoppers;
		synchronized (this) {
			hoppers = new ArrayList<Hopper>(this);
		}
		StringBuilder output = new StringBuilder("<population>"
												 + Helper.NEWLINE);
		output.append("<hoppers>" + Helper.NEWLINE);
		for (Hopper h : hoppers) {
			output.append(h.toString() + Helper.NEWLINE);
		}
		output.append("</hoppers>" + Helper.NEWLINE);
		output.append("<crossover>" + Helper.NEWLINE);
		output.append(crossover.toString() + Helper.NEWLINE);
		output.append("</crossover>" + Helper.NEWLINE);
		output.append("</population>");

		return output.toString();
	}
	
	/**
	 * Main method for testing purposes.
	 * 
	 * @param args Command-line arguments.
	 */
	public static void main(String[] args) {
		Population pop = new Population(10);
		System.out.println(pop);
	}
	
}
