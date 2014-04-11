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
	// Sub-collections allow the Population to separate the Hoppers that need
	// special handling from those in the general population.
	private final ArrayList<Hopper> breeders;
	// The hill-climbing Tribe brain for this Population.
	private TribeBrain brain;
	// Statistics.
	private volatile int generations;
	private volatile float averageFitness;
	private volatile float highestFitness;
	private volatile long lifetimeOffspring;
	private volatile long lifetimeHillClimbs;
	private volatile long currentRejectedCreatures;
	private volatile long currentFailedHillClimbs;
	private volatile long lifetimeRejectedCreatures;
	private volatile long lifetimeFailedHillClimbs;
	private volatile long failedRandomHoppers;
	
	/**
	 * The default constructor creates an empty Population.
	 */
	public Population() {
		super();
		generations = 0;
		averageFitness = 0.0f;
		highestFitness = 0.0f;
		lifetimeOffspring = 0l;
		lifetimeHillClimbs = 0l;
		currentRejectedCreatures = 0l;
		currentFailedHillClimbs = 0l;
		lifetimeRejectedCreatures = 0l;
		lifetimeFailedHillClimbs = 0l;
		failedRandomHoppers = 0l;
		breeders = new ArrayList<Hopper>();
		brain = new TribeBrain();
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
				// ++ isn't atomic, but it doesn't matter since this field
				// doesn't need to be exact.
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
		synchronized (pop1) {
			pop1.moveBreeders();
			breeders1 = new ArrayList<Hopper>(pop1.breeders);
			int size1 = breeders1.size();
			for (int i = 0; i < size1; i++) {
				Hopper h = breeders1.get(i);
				if (h != null) {
					h.increaseBreedCount();
				}
			}
		}
		pop1.flushBreeders();
				
		synchronized (pop2) {
			// Passing the size of breeders1 as an argument guarantees that
			// both collections have the same number of Hoppers.
			pop2.moveBreeders();
			breeders2 = new ArrayList<Hopper>(pop2.breeders);
			int size2 = breeders2.size();
			for (int i = 0; i < size2; i++) {
				Hopper h = breeders2.get(i);
				if (h != null) {
					h.increaseBreedCount();
				}
			}
		}
		pop2.flushBreeders();
		
		Collections.shuffle(breeders1);
		Collections.shuffle(breeders2);
		int size1 = breeders1.size();
		int size2 = breeders2.size();
		
		for (int i = 0; i < size1 && i < size2; i++) {
			Hopper parentA = breeders1.get(i);
			Hopper parentB = breeders2.get(i);

			// Determine to which Population to send the offspring.
			try {
				Hopper[] offspring = Crossover.crossover(
						parentA, parentB);
				if (offspring != null) {
					for (int j = 0; j < offspring.length; j++) {
						Hopper child = offspring[j];
						// Short-circuits if child is null.
						if (j % 2 == 0) {
							children1.add(child);
						} else {
							children2.add(child);
						}
					}
				}
			} catch (IllegalArgumentException | GeneticsException ex) {
				pop1.currentRejectedCreatures++;
				pop2.currentRejectedCreatures++;
				pop1.lifetimeRejectedCreatures++;
				pop2.lifetimeRejectedCreatures++;
//				System.out.println(
//						"Interbreed produced offspring invalid. Continuing.");
			}
		}
		
		// Add the children to their respective populations.
		synchronized (pop1) {
			for (Hopper h : children1) {
				// Short-circuit if h is null.
				if (h == null || h.getGenotype() == null
						|| h.getPhenotype() == null || h.getBody() == null) {
					pop1.currentRejectedCreatures++;
					pop1.lifetimeRejectedCreatures++;
				} else {
					pop1.add(h);
				}
			}
		}
		synchronized (pop2) {
			for (Hopper h : children2) {
				// Short-circuit if h is null.
				if (h == null || h.getGenotype() == null
						|| h.getPhenotype() == null || h.getBody() == null) {
					pop2.currentRejectedCreatures++;
					pop2.lifetimeRejectedCreatures++;
				} else {
					pop2.add(h);
				}
			}
		}
	}
	
	/**
	 * Update the population.
	 */
	public void update() {
		generations++;
		// Randomly select hill climbing or breeding this generation.
		// Hill climbing the population will change the creatures, which
		// means their sorting will no longer be valid. However,
		// moveBreeders will sort them again, so it's fine.
		if (Helper.choose() > 0) {
			hillClimb();			
		} else {
			moveBreeders();
			breed();
		}
		cull();
		// Like above, breeding will change the creatures in the collection,
		// but cull will sort them again.
		// Every 100 generations, reseed the Population with 20% new, random
		// Hoppers to provide new Alleles.
//		if (generations % Helper.SEED_NEW_RANDOMS_GAP == 0) {
//			seedNewRandoms();
//		}
		if (size() > 0) {
			highestFitness = get(size() - 1).getFitness();
		}
		calculateAverageFitness();
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
		synchronized (breeders) {
			Collections.shuffle(breeders);
			
			while (breeders.size() > 1) {
				// Get the parents and return them to the general population.
				Hopper parentA = breeders.get(0);
				breeders.remove(0);
				add(parentA);
				Hopper parentB = breeders.get(0);
				breeders.remove(0);
				add(parentB);
				
				try {
					Hopper[] offspring = Crossover.crossover(
							parentA, parentB);
					if (offspring != null) {
						children.add(offspring[0]);
						children.add(offspring[1]);
					}
				} catch (IllegalArgumentException | GeneticsException ex) {
					currentRejectedCreatures++;
					lifetimeRejectedCreatures++;
//				System.out.println(
//						"Breed offspring invalid. Continuing.");
				} finally {
					parentA.increaseBreedCount();
					parentB.increaseBreedCount();
				}
			}
		}
		// Clear out any remaining breeders.
		flushBreeders();
		int successes = 0;
		for (Hopper h : children) {
			if (add(h)) {
				successes++;
			} else {
				currentRejectedCreatures++;
				lifetimeRejectedCreatures++;
			}
		}
		
		return successes;
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
				// The != unary operator works here because we want to know if
				// the two objects are, in fact, the same object.
				if (newHotness != original) {
					synchronized (this) {
						remove(original);
						unsynchronizedAdd(newHotness);
					}
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
		addAll(newBlood);
		cull();
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
		synchronized (this) {
			sort();
			// Overperformers.
			int size = size();
			int stop = (int) (size - (over * size));
			if (stop >= size) {
				stop = 0;
			}
			
			for (int i = size - 1; i >= stop && i > 0; i--) {
				synchronized (breeders) {
					breeders.add(remove(i));
				}
			}
			// Underperformers.
			int count = 0;
			if (under.length > 0) {
				count = (int) (size * under[0]);
			} else {
				count = (int) (size * (over / 2));
			}
			for (int i = 0; i < count && i < size(); i++) {
				int index = Helper.RANDOM.nextInt(size());
				synchronized (breeders) {
					breeders.add(remove(index));
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
		synchronized (breeders) {
			breeders.clear();
		}
	}
	
	/**
	 * Kill off the lowest-fitness n individuals in the general Population.
	 * 
	 * @param n Number of individuals to kill off.
	 */
	private void cull() {
		sort();
		synchronized (this) {
			int size = size();
			if (size > Helper.POPULATION_SIZE) {
				removeRange(0, size - Helper.POPULATION_SIZE);
			}
		}
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
	 * Calculate the average fitness of this Population.
	 */
	private void calculateAverageFitness() {
		float sum = 0.0f;
		int size = super.size();
		for (Hopper h : this) {
			sum += h.getFitness();
		}
		averageFitness = sum / size;
	}
	
	/**
	 * Getter for averageFitness.
	 */
	public float getAverageFitness() {
		return averageFitness;
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
				int size = size();
				if (size > 0) {
					newGuy = new Hopper(get(size - 1));
				}
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
	 * Getter/setter for currentRejectedCreatures.
	 * 
	 * @param writeNotRead Optional long to assign to this field.
	 * @return Lifetime failed breeds.
	 */
	public synchronized long getCurrentRejectedCreatures(long...writeNotRead) {
		if (writeNotRead.length > 0) {
			currentRejectedCreatures = writeNotRead[0];
		} else {
			long oldValue = currentRejectedCreatures;
			currentRejectedCreatures = 0;
			return oldValue;
		}
		return currentRejectedCreatures;
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
	 * Getter/setter for lifetimeRejectedCreatures.
	 * 
	 * @param writeNotRead Optional long to assign to this field.
	 * @return Lifetime failed breeds.
	 */
	public synchronized long getLifetimeDeadChildren(long...writeNotRead) {
		if (writeNotRead.length > 0) {
			lifetimeOffspring = writeNotRead[0];
		}
		return lifetimeRejectedCreatures;
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
			return;
		}
		
		try {
			super.add(new Hopper(hopper));
		} catch (IllegalArgumentException | GeneticsException e) {
			return;
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
//			Log.error("Adding Hopper to Population failed.");
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
//			Log.error("Adding Hopper to Population failed.");
//			System.out.println("Adding Hopper to Population failed.");
			return;
		}
	}
	
	/**
	 * Override of addAll - individually adds all requested Hoppers to the
	 * Population only if their Genotypes, phenotypes and bodies are valid.
	 * 
	 * @param collection Collection of Hoppers to add.
	 * @return True if at all creatures were added, false if not.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean addAll(Collection collection) {
		List<Hopper> hoppers = new ArrayList<Hopper>();
		// If an object in the collection is a Hopper, add it to the list.
		for (Object o : collection) {
			if (o != null && o.getClass() == this.getClass()) {
				Hopper h = (Hopper) o;
				if (h != null && h.getGenotype() != null
						&& h.getPhenotype() != null && h.getBody() != null) {
					hoppers.add(h);
				}
			}
		}
		
		int count = 0;
		synchronized (this) {
			for (Hopper h : hoppers) {
				if (add(h)) {
					count++;
				}
			}
		}
		return count == hoppers.size();
	}
	
	/**
	 * Override of get by index - returns a copy of the requested Hopper.
	 * 
	 * @param index Index of Hopper of which to return a copy.
	 * @return Deep clone of the Hopper at index.
	 */
	@Override
	public Hopper get(int index) {
		if (index < 0 || index >= size()) {
			return null;
		}
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
		for (Hopper h : hoppers) {
			output.append(h.toString() + Helper.NEWLINE);
		}
		output.append("</population>");

		return output.toString();
	}
	
	/**
	 * Main method for testing purposes.
	 * 
	 * @param args Command-line arguments.
	 */
	public static void main(String[] args) {
		Population pop1 = new Population(100);
		Population pop2 = new Population(100);
		interbreed(pop1, pop2);
	}
	
}
