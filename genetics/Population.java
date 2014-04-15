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
public class Population extends Vector<Hopper> {
	// Sub-collections allow the Population to separate the Hoppers that need
	// special handling from those in the general population.
	private final Vector<Hopper> breeders;
	// The hill-climbing Tribe brain for this Population.
	private final TribeBrain brain;
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
	// Special Hoppers.
	private volatile Hopper overachiever;
	private volatile Hopper underachiever;
	
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
		brain = new TribeBrain();
		breeders = new Vector<Hopper>();
		overachiever = null;
		underachiever = null;
	}
	
	/**
	 * Instantiate a new Population containing randomly generated seed Hoppers.
	 * 
	 * @param num Number of random Hoppers to create.
	 * @param boolean...random Optional parameter to set new Hoppers' Allele
	 *            weights to random (default) or 1.0f (false).
	 */
	public Population(int num, boolean...random) {
		super(num);
		
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
		brain = new TribeBrain();
		breeders = new Vector<Hopper>();
		
		int i = 0;
		while (i < num) {
			try {
				// Short-circuit.
				if (random.length > 0 && !random[0]) {
					add(new Hopper(false));
				} else {
					add(new Hopper());
				}
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
		overachiever = get(size() - 1);
		underachiever = get(0);
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
		if (pop1 == null || pop2 == null) {
			return;
		} else {
			pop1.generations++;
			pop2.generations++;
		}
		// Make breeder lists of the highest-fitness creatures from each
		// Population. Also get a copy of each Population's Crossover module.
		ArrayList<Hopper> breeders1;
		ArrayList<Hopper> breeders2;
		ArrayList<Hopper> children1 = new ArrayList<Hopper>();
		ArrayList<Hopper> children2 = new ArrayList<Hopper>();
		breeders1 = new ArrayList<Hopper>();
		synchronized(pop1) {
			for (int i = 0; i < pop1.size() * Helper.BREED_PERCENTAGE; i++) {
				breeders1.add(pop1.get(pop1.size() - 1));
			}
		}
		// Passing the size of breeders1 as an argument guarantees that
		// both collections have the same number of Hoppers.
		breeders2 = new ArrayList<Hopper>();
		synchronized (pop2) {
			for (int i = 0; i < pop2.size() * Helper.BREED_PERCENTAGE; i++) {
				breeders2.add(pop2.get(pop2.size() - 1));
			}
		}
		Collections.shuffle(breeders1);
		Collections.shuffle(breeders2);
		int size1 = breeders1.size();
		int size2 = breeders2.size();
		for (int i = 0; i < size1 && i < size2; i++) {
			Hopper parentA = breeders1.get(i);
			parentA.increaseBreedCount();
			Hopper parentB = breeders2.get(i);
			parentB.increaseBreedCount();

			// Determine to which Population to send the offspring.
			try {
				Hopper[] offspring = Crossover.crossover(
						parentA, parentB);
				if (offspring != null) {
					for (int j = 0; j < offspring.length; j++) {
						Hopper child = offspring[j];
						// Short-circuits if child is null.
						if (j == 0) {
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
//						"Interbreed produced invalid offspring. Continuing.");
			}
		}
		
		// Add the children to their respective populations.
		for (Hopper h : children1) {
			// Short-circuit if h is null.
			if (!pop1.add(h)) {
				pop1.currentRejectedCreatures++;
				pop1.lifetimeRejectedCreatures++;
			}
		}
		for (Hopper h : children2) {
			// Short-circuit if h is null.
			if (!pop2.add(h)) {
				pop2.currentRejectedCreatures++;
				pop2.lifetimeRejectedCreatures++;
			}
		}
	}
	
	/**
	 * Update the population.
	 */
	public void update() {
		generations++;
		// Reseed the Population with new, random Hoppers to diversify.
		if (generations % Helper.SEED_NEW_RANDOMS_GAP == 0) {
			seedNewRandoms();
		}
		
		// Select hill climbing or breeding this generation.
		if (Helper.choose() > 0) {
			hillClimb();
		} else {
			moveBreeders();
			breed();
		}
		
		cull();
		if (size() > 0) {
			highestFitness = get(size() - 1).getFitness();
			overachiever = get(size() - 1);
			underachiever = get(0);
		}
		calculateAverageFitness();
	}
	
	/**
	 * Breed, perform selection and crossover, within the population.
	 * 
	 * @return The number of new, valid Hoppers that were added to the
	 * 		   population.
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
				Hopper parentB = breeders.get(0);
				breeders.remove(0);
				
				try {
					Hopper[] offspring = Crossover.crossover(
							parentA, parentB);
					if (offspring != null) {
						if (children.add(offspring[0])) {
							lifetimeOffspring++;
						}
						if (children.add(offspring[1])) {
							lifetimeOffspring++;
						}
					}
				} catch (IllegalArgumentException | GeneticsException ex) {
					currentRejectedCreatures++;
					lifetimeRejectedCreatures++;
//					System.out.println(
//							"Breed offspring invalid. Continuing.");
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
		synchronized (this) {
			for (ListIterator<Hopper> i = listIterator(); i.hasNext(); ) {
				Hopper original = i.next();
				try {
					Hopper newHotness = brain.performHillClimbing(original);
					newHotness.hillClimbed();
					// The != unary operator works here because we want to know if
					// the two objects are, in fact, the same object.
					if (newHotness != original) {
						i.set(newHotness);
						lifetimeHillClimbs++;
					} else {
						currentFailedHillClimbs++;
						lifetimeFailedHillClimbs++;
					}
				} catch (IllegalArgumentException | GeneticsException ex) {
					currentFailedHillClimbs++;
					lifetimeFailedHillClimbs++;
	//				System.out.println(
	//					"HillClimbing produced an illegal creature. Skipping.");
				}
			}
		}
	}
	
	/**
	 * Seed the Population with new, random Hoppers to provide fresh Alleles.
	 */
	public void seedNewRandoms() {
		int newHopperCount = (int) (size() * Helper.RANDOM_RESEED_PERCENTAGE);
		Population newBlood = new Population(newHopperCount, false);
		addAll(newBlood);
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
		// If there's already Hoppers in the breeders collection, end early.
		if (!breeders.isEmpty()) {
			return;
		}
		
		sort();
		// Overperformers.
		int size = size();
		int stop = (int) (size - (over * size));
		if (stop >= size) {
			stop = 0;
		}
		
		for (int i = size - 1; i >= stop && i > 0; i--) {
			breeders.add(get(i));
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
			breeders.add(get(index));
		}
	}
	
	/**
	 * Move all Hoppers in the breeders list back into the general population.
	 */
	private void flushBreeders() {
		breeders.clear();
	}
	
	/**
	 * Kill off the lowest-fitness n individuals in the general Population.
	 * 
	 * @param n Number of individuals to kill off.
	 */
	public void cull() {
		sort();
		if (size() > Helper.POPULATION_SIZE) {
			removeRange(0, size() - Helper.POPULATION_SIZE);
		}
	}
	
	/**
	 * Rediversify the population by removing Hoppers that are too similar and
	 * replacing them with new, randomly generated Hoppers.
	 * 
	 * Note: this is extremely slow, so use it sparingly.
	 */
	public void rediversify() {
		int minSize = (int) (size() * Helper.BREED_PERCENTAGE);
		ArrayList<Hopper> trash = new ArrayList<Hopper>();
		for (ListIterator<Hopper> i = listIterator(); i.hasNext()
				&& size() > minSize; ) {
			Hopper h1 = i.next();
			for (int j = 0; j < size(); j++) {
				Hopper h2 = get(j);
				if (h1 != h2 && Genotype.tooSimilar(h1.getGenotype(),
						h2.getGenotype())) {
					trash.add(h2);
				}
			}
		}
		// Remove the trash hoppers from the general Population.
		for (Hopper h : trash) {
			remove(h);
		}
		// Nerf all remaining weights.
		for (Hopper h : this) {
			Genotype.nerfWeights(h.getGenotype());
		}
		while (size() < Helper.POPULATION_SIZE) {
			try {
				Hopper h = new Hopper();
				if (h != null) {
					add(h);
				}
			} catch (GeneticsException | IllegalArgumentException ex) {
				failedRandomHoppers++;
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
		synchronized (this) {
			int size = super.size();
			for (ListIterator<Hopper> i = listIterator(); i.hasNext(); ) {
				Hopper h = i.next();
				h.setAge(h.getAge() + 1);
				sum += h.getFitness();
			}
			averageFitness = sum / size;
		}
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
		try {
			newGuy = new Hopper(overachiever);
		// Should never fail since it's cloning a Hopper that's already
		// valid.
		} catch (IllegalArgumentException | GeneticsException e) {
			Log.error("Cloning Hopper for getOverachiever failed.");
//			System.out.println("Cloning Hopper for getOverachiever failed.");
		}
		return newGuy;
	}
	
	/**
	 * Get a clone of the Hopper with the lowest fitness.
	 * 
	 * @return Deep clone of Hopper with the lowest fitness.
	 */
	public Hopper getUnderachiever() {
		Hopper newGuy = null;
		try {
			newGuy = new Hopper(underachiever);
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
	 * @return Lifetime offspring.
	 */
	public long getLifetimeOffspring() {
		return lifetimeOffspring;
	}
	
	/**
	 * Getter/setter for lifetimeHillClimbs.
	 * 
	 * @return Lifetime hill climbs.
	 */
	public long getLifetimeHillClimbs() {
		return lifetimeHillClimbs;
	}
	
	/**
	 * Getter/setter for currentRejectedCreatures.
	 * 
	 * @return Lifetime failed breeds.
	 */
	public long getCurrentRejectedCreatures() {
		long oldValue = currentRejectedCreatures;
		currentRejectedCreatures = 0;
		return oldValue;
	}
	
	/**
	 * Getter/setter for currentFailedHillClimbs.
	 * 
	 * @return Failed hill climbs this generation.
	 */
	public long getCurrentFailedHillClimbs() {
		long oldValue = currentFailedHillClimbs;
		currentFailedHillClimbs = 0;
		return oldValue;
	}
	
	/**
	 * Getter/setter for lifetimeRejectedCreatures.
	 * 
	 * @return Lifetime failed breeds.
	 */
	public long getLifetimeDeadChildren(long...writeNotRead) {
		return lifetimeRejectedCreatures;
	}
	
	/**
	 * Getter/setter for lifetimeFailedHillClimbs.
	 * 
	 * @return Lifetime failed hill climbs.
	 */
	public long getLifetimeFailedHillClimbs() {
		return lifetimeFailedHillClimbs;
	}
	
	/**
	 * Getter/setter for failedRandomHoppers.
	 * 
	 * @return Number of failed random hopper creations during initialization.
	 */
	public long getFailedRandomHoppers() {
		return failedRandomHoppers;
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
				|| hopper.getPhenotype() == null) {
			return false;
		}
		
		try {
			return super.add(new Hopper(hopper));
		} catch (IllegalArgumentException | GeneticsException e) {
//			Log.error("Adding Hopper to Population failed.");
//			System.out.println("Adding Hopper to Population failed.");
			return false;
		}
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
				|| hopper.getPhenotype() == null) {
			return;
		}
		
		try {
			super.add(index, new Hopper(hopper));
		} catch (IllegalArgumentException | GeneticsException e) {
//			Log.error("Adding Hopper to Population failed.");
//			System.out.println("Adding Hopper to Population failed.");
			return;
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
		for (ListIterator<Hopper> i = hoppers.listIterator(); i.hasNext(); ) {
			output.append(i.next().toString() + Helper.NEWLINE);
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
		Population pop1 = new Population(1000);
		Population pop2 = new Population(1000);
		interbreed(pop1, pop2);
	}
	
}
