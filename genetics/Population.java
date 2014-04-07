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
import creature.geeksquad.gui.Log;
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
	private int generations;
	private int failedAdds;
	// Sub-collections allow the Population to separate the Hoppers that need
	// special handling from those in the general population.
	private final ArrayList<Hopper> breeders;
	// The Crossover module this Population will use.
	private Crossover crossover;
	// The hill-climbing Tribe brain for this Population.
	private TribeBrain brain = new TribeBrain();
	// Statistics.
	private long lifetimeOffspring;
	private long lifetimeHillClimbs;
	private long currentFailedBreeds;
	private long currentFailedHillClimbs;
	private long lifetimeFailedBreeds;
	private long lifetimeFailedHillClimbs;
	private long failedRandomHoppers;
	private float highestFitness;
	
	/**
	 * The default constructor creates an empty Population.
	 */
	public Population() {
		super();
		generations = 0;
		failedAdds = 0;
		lifetimeOffspring = 0l;
		lifetimeHillClimbs = 0l;
		currentFailedBreeds = 0l;
		currentFailedHillClimbs = 0l;
		lifetimeFailedBreeds = 0l;
		lifetimeFailedHillClimbs = 0l;
		failedRandomHoppers = 0l;
		highestFitness = 0.0f;
		breeders = new ArrayList<Hopper>();
		crossover = new Crossover();
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
				unsynchronizedAdd(new Hopper());
				i++;
			} catch (IllegalArgumentException | GeneticsException ex) {
				failedRandomHoppers++;
//				System.out.println("Creature[" + i + "] " + ex
//								   + " Rebuilding.");
			}
		}
		sort();
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

			// Determine which Population the offspring
			try {
				Hopper[] offspring = cross.crossover(
						parentA, parentB);
				if (offspring != null) {
					for (int j = 0; j < offspring.length; j++) {
						Hopper child = offspring[j];
						// Short-circuits if child is null.
						if (child == null || child.getGenotype() == null
								|| child.getPhenotype() == null
								|| child.getBody() == null) {
							throw new GeneticsException("Child returned from "
									+ "interpopulation breeding was invalid.");
						} else {
							if (j % 2 == 0) {
								children1.add(child);
							} else {
								children2.add(child);
							}
						}
					}
				}
			} catch (IllegalArgumentException | GeneticsException ex) {
				pop1.currentFailedBreeds++;
				pop2.currentFailedBreeds++;
				pop1.lifetimeFailedBreeds++;
				pop2.lifetimeFailedBreeds++;
//				System.out.println(
//						"Interbreed produced offspring invalid. Continuing.");
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
		// but cull will sort them again.
		cull(count);
		// Every 100 generations, reseed the Population with 20% new, random
		// Hoppers to provide new Alleles.
		if (generations % Helper.SEED_NEW_RANDOMS_GAP == 0) {
			seedNewRandoms();
		}
		
		if (size() > 0) {
			highestFitness = get(size() - 1).getFitness();
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
					if (size() > 0) {
						breeders.add(remove(size() - 1));
					} else {
						flushBreeders();
						return 0;
					}
				}
			}
			Hopper parentB = breeders.get(++i);
			try {
				Hopper[] offspring = crossover.crossover(
						parentA, parentB);
				if (offspring != null) {
					for (Hopper h : offspring) {
						// Short-circuits if h is null.
						if (h == null || h.getGenotype() == null
								|| h.getPhenotype() == null
								|| h.getBody() == null) {
							throw new GeneticsException("Child returned "
									+ "from breeding was invalid.");
						} else {
							children.add(h);
						}
					}
				}
			} catch (IllegalArgumentException | GeneticsException ex) {
				currentFailedBreeds++;
				lifetimeFailedBreeds++;
//				System.out.println(
//						"Breed offspring invalid. Continuing.");
			} finally {
				parentA.increaseBreedCount();
				parentB.increaseBreedCount();
			}
		}
		// flushBreeders and addAll are already synchronized.
		flushBreeders();
		addAll(children);
		
		return children.size();
	}
	
	/**
	 * Perform hill-climbing on all members of the Population.
	 */
	private void hillClimb() {
		List<Hopper> climbers = new ArrayList<Hopper>();
		synchronized (this) {
			climbers.addAll(this);
		}
		int size = size();
		for (int i = 0; i < size; i++) {
			Hopper original = climbers.get(i);
			try {
				Hopper newHotness = brain.performHillClimbing(original);
				newHotness.hillClimbed();
				synchronized (this) {
					remove(original);
					unsynchronizedAdd(newHotness);
				}
			} catch (IllegalArgumentException | GeneticsException ex) {
				currentFailedHillClimbs++;
				lifetimeFailedHillClimbs++;
//				System.out.println(
//					"HillClimbing produced an illegal creature. Skipping.");
			}
		}
	}
	
	/**
	 * Seed the Population with new, random Hoppers to provide fresh Alleles.
	 * Removes Hoppers from the bottom of the Population and reseeds with newly
	 * created Hoppers.
	 */
	public void seedNewRandoms() {
		int newHopperCount = (int) (size() * Helper.BREED_PERCENTAGE);
		Population newBlood = new Population(newHopperCount);
		cull(newHopperCount);
		addAll(newBlood);
		sort();
	}
	
	/**
	 * Move the top 20% most fit Hoppers into the breeders list.
	 */
	private void moveBreeders() {
		moveBreeders(Helper.BREED_PERCENTAGE);
	}
	
	/**
	 * Move a percentage of the highest-fitness Hoppers into the breeders
	 * list. A second optional argument sets a number of random, suboptimal
	 * Hoppers to be moved as well.
	 * 
	 * @param over Percentage float of the highest-fitness Hoppers to move.
	 * @param under Optional float percent of how many random other Hoppers
	 *            should be moved. If not provided, moveBreeders moves half
	 *            of over.
	 */
	private void moveBreeders(float over, float...under) {
		sort();
		synchronized (this) {
			int size = size();
			int stop = (int) (size - (over * size));
			if (stop >= size) {
				breeders.addAll(this);
				clear();
			} else {
				for (int i = size - 1; i >= stop; i--) {
					breeders.add(remove(i));
				}
			}
			int count = 0;
			if (under.length > 0) {
				count = (int) (size * under[0]);
			} else {
				count = (int) (size * (over / 2));
			}
			for (int i = 0; i < count; i++) {
				int index = Helper.RANDOM.nextInt(size());
				breeders.add(remove(index));
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
		if (n - failedAdds <= 0) {
			return;
		}
		
		sort();
		if (n - failedAdds < super.size()) {
			synchronized (this) {
				removeRange(0, n - failedAdds);
			}
		} else {
			synchronized (this) {
				clear();
			}
		}
		failedAdds = 0;
	}
	
	/**
	 * Get a clone of the Hopper with the highest fitness.
	 * 
	 * @return Deep clone of Hopper with the highest fitness.
	 */
	public Hopper getOverachiever() {
		Hopper newGuy = null;
		sort();
		try {
			synchronized (this) {
				newGuy = new Hopper(get(size() - 1));
			}
		// Should never fail since it's cloning a Hopper that's already
		// valid.
		} catch (IllegalArgumentException | GeneticsException e) {
			Log.error("Cloning Hopper for getOverachiever failed.");
//			System.out.println("Cloning Hopper for getOverachiever failed.");
		}
		return newGuy;
	}
	
	/**
	 * Getter for highestFitness.
	 * 
	 * @return The highest fitness of the Population as a float.
	 */
	public float getHighestFitness() {
		return highestFitness;
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
	 * Getter/setter for lifetimeOffspring.
	 * 
	 * @param writeNotRead Optional long to assign to this field.
	 * @return Lifetime offspring.
	 */
	public synchronized long getLifetimeOffspring(long...writeNotRead) {
		if (writeNotRead.length > 0) {
			lifetimeOffspring = writeNotRead[0];
		}
		return lifetimeOffspring;
	}
	
	/**
	 * Getter/setter for lifetimeHillClimbs.
	 * 
	 * @param writeNotRead Optional long to assign to this field.
	 * @return Lifetime hill climbs.
	 */
	public synchronized long getLifetimeHillClimbs(long...writeNotRead) {
		if (writeNotRead.length > 0) {
			lifetimeHillClimbs = writeNotRead[0];
		}
		return lifetimeHillClimbs;
	}
	
	/**
	 * Getter/setter for currentFailedBreeds.
	 * 
	 * @param writeNotRead Optional long to assign to this field.
	 * @return Lifetime failed breeds.
	 */
	public synchronized long getCurrentFailedBreeds(long...writeNotRead) {
		if (writeNotRead.length > 0) {
			currentFailedBreeds = writeNotRead[0];
		} else {
			long oldValue = currentFailedBreeds;
			currentFailedBreeds = 0;
			return oldValue;
		}
		return currentFailedBreeds;
	}
	
	/**
	 * Getter/setter for currentFailedHillClimbs.
	 * 
	 * @param writeNotRead Optional long to assign to this field.
	 * @return Failed hill climbs this generation.
	 */
	public synchronized long getCurrentFailedHillClimbs(
			long...writeNotRead) {
		if (writeNotRead.length > 0) {
			currentFailedHillClimbs = writeNotRead[0];
		} else {
			long oldValue = currentFailedHillClimbs;
			currentFailedHillClimbs = 0;
			return oldValue;
		}
		return currentFailedHillClimbs;
	}
	
	/**
	 * Getter/setter for lifetimeFailedBreeds.
	 * 
	 * @param writeNotRead Optional long to assign to this field.
	 * @return Lifetime failed breeds.
	 */
	public synchronized long getLifetimeDeadChildren(long...writeNotRead) {
		if (writeNotRead.length > 0) {
			lifetimeOffspring = writeNotRead[0];
		}
		return lifetimeFailedBreeds;
	}
	
	/**
	 * Getter/setter for lifetimeFailedHillClimbs.
	 * 
	 * @param writeNotRead Optional long to assign to this field.
	 * @return Lifetime failed hill climbs.
	 */
	public synchronized long getLifetimeFailedHillClimbs(
			long...writeNotRead) {
		if (writeNotRead.length > 0) {
			lifetimeOffspring = writeNotRead[0];
		}
		return lifetimeFailedHillClimbs;
	}
	
	/**
	 * Getter/setter for failedRandomHoppers.
	 * 
	 * @param writeNotRead Optional long to assign to this field.
	 * @return Number of failed random hopper creations during initialization.
	 */
	public synchronized long getFailedRandomHoppers(long...writeNotRead) {
		if (writeNotRead.length > 0) {
			lifetimeOffspring = writeNotRead[0];
		}
		return failedRandomHoppers;
	}
	
	/**
	 * Adds a copy of the requested Hopper only if that Hopper is valid (has a
	 * valid Genotype, phenotype, and body). This is a special, private version
	 * of add that is unsynchronized.
	 * 
	 * @param hopper Hopper to add to the Population.
	 */
	private void unsynchronizedAdd(Hopper hopper) {
		// Short-circuits if hopper is null.
		if (hopper == null || hopper.getGenotype() == null
				|| hopper.getPhenotype() == null || hopper.getBody() == null) {
			failedAdds++;
			return;
		}
		
		try {
			super.add(new Hopper(hopper));
		} catch (IllegalArgumentException | GeneticsException e) {
			failedAdds++;
		}
	}
	
	/**
	 * Override of add - adds a copy of the requested Hopper only if that
	 * Hopper is valid (has a valid Genotype, phenotype, and body). Since this
	 * always adds the Hopper to the end of the list, it doesn't need to be
	 * synchronized.
	 * 
	 * @param hopper Hopper to add to the Population.
	 * @return True if add succeeded, else false.
	 */
	@Override
	public boolean add(Hopper hopper) {
		// Short-circuits if hopper is null.
		if (hopper == null || hopper.getGenotype() == null
				|| hopper.getPhenotype() == null || hopper.getBody() == null) {
			return false;
		}
		
		try {
			super.add(new Hopper(hopper));
		} catch (IllegalArgumentException | GeneticsException e) {
			Log.error("Adding Hopper to Population failed.");
//			System.out.println("Adding Hopper to Population failed.");
			return false;
		}
		return true;
	}
	
	/**
	 * Override of add by index - adds a copy of the requested Hopper only if
	 * that Hopper is valid (has a valid Genotype, phenotype, and body). Since
	 * this always inserts the Hopper into the middle of the collection, it
	 * does need to be synchronized.
	 * 
	 * @param index Index at which to insert the Hopper.
	 * @param hopper Hopper to add to the Population.
	 */
	@Override
	public void add(int index, Hopper hopper) {
		// Short-circuits if hopper is null.
		if (hopper == null || hopper.getGenotype() == null
				|| hopper.getPhenotype() == null || hopper.getBody() == null) {
			return;
		}
		
		try {
			synchronized (this) {
				super.add(index, new Hopper(hopper));
			}
		} catch (IllegalArgumentException | GeneticsException e) {
			Log.error("Adding Hopper to Population failed.");
//			System.out.println("Adding Hopper to Population failed.");
		}
	}
	
	/**
	 * Override of addAll - individually adds all requested Hoppers to the
	 * Population only if their Genotypes, phenotypes and bodies are valid.
	 * 
	 * @param collection Collection of Hoppers to add.
	 * @return True if at least one creature was added, false if not.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean addAll(Collection collection) {
		List<Hopper> hoppers = new ArrayList<Hopper>();
		// If an object in the collection is a Hopper, add it to the list.
		for (Object o : collection) {
			if (o != null && o instanceof Hopper) {
				Hopper h = (Hopper) o;
				if (h.getGenotype() != null && h.getPhenotype() != null
						&& h.getBody() != null) {
					hoppers.add(h);
				} else {
					failedAdds++;
				}
			} else {
				failedAdds++;
			}
		}
		
		synchronized (this) {
			return super.addAll(hoppers);
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
		synchronized (this) {
			try {
				return new Hopper(super.get(index));
			} catch (IllegalArgumentException | GeneticsException ex) {
				return null;
			} catch (IndexOutOfBoundsException ex) {
				throw ex;
			}
		}
	}
	
	/**
	 * Override of size returns the current size of the Population.
	 *
	 * @return Size of the population.
	 */
	@Override
	public synchronized int size() {
		return super.size();
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
