# Evolving Virtual Creatures - CS 351, Spring 2014

## Team Members (Geek Squad):
Danny Gomez - hill-climbing
Marcos Lemus - main program, GUI
Ramón A. Lovato - genetics

## Introduction

Evolving Virtual Creatures is a genetic algorithm project to evolve virtual creatures to be good at jumping, based on Karl Sims’ 1994 research project of the same name. The project is written using the Java programming language and Java Open Graphics Library (JOGL). We created EVC as an academic project for the University of New Mexico’s CS 351 Design of Large Programs class, spring 2014, with Joel Castellanos.

In EVC, a number of independent populations of virtual creatures are created and run through several stages of a simulation: hill-climbing, crossover, physics simulation, inter-population crossover, and elimination (“culling”). Over time, the creatures (called “Hoppers”) move toward a solution to optimize their fitness as determined by dividing the height of the lowest point on the creature by its highest point at the peak of its jump.

## Member Roles

### Danny Gomez - Hill-Climbing
Package: creature.geeksquad.hillclimbing

The hill climbing aspect of the project involves using a Brain and Strategy relationship to perform different informed random changes to a creature. All the hill climbing classes can be found within the package creature.geeksquad.hillclimbing. It was designed to be simple to use by the other packages, in the sense that a creature is sent into hill climbing, and an improved or equal creature is return.

Each population of creatures has a TribeBrain object that is responsible for selecting which strategy of hill climbing to perform based on a set of weighted maps. The maps are stored within a MapHandler object belonging to that particular tribe. The MapHandler is responsible for retrieving and updating information from the probability maps. 

After the TribeBrain has selected the strategy to perform, the original creature passed into hill climbing is cloned and sent to the strategy. Hill climbing will always clone the creature to be modified and never tamper with the original. This is to ensure that if any strategy corrupts or invalidates the cloned creature, we can safely return the original back to the population.

Within the strategies, a change is done to the genotype of the cloned creature. This change will vary depending on which strategy has been chosen by the TribeBrain. For example, the ChangeSingleAllele strategy only changes one particular allele of the genotype. Depending on the trait of the allele, a different method is called to climb that particular allele. Dimension alleles enter the climbFloat method, rule alleles enter the climbRule method, joint alleles enter the climbJoint method, etc. These hill climbing methods extract the value of the allele and modify it based on the probability maps stored in the MapHandler. Certain methods such as climbFloat perform quick evaluations of the modified creatures fitness and repeat the climb until improvement is no longer obtained. 

Other strategies that can be chosen by the TribeBrain include AddBlock, RemoveBlock, AddRule, and RandomHopper. The names of the strategies give a good indication of what they entail, but detailed description can be found in their class files.

After a strategy has been performed on the cloned creature, its final fitness is evaluated. This fitness is then compared to the fitness of the original creature passed into hill climbing. The creature with the highest fitness is then returned. This guarantees that either a creature of improved or equal fitness will always leave hill climbing. Before the creature is return, all the probability maps are updated based on whether the hill climbing had produced a success or failure.

### Marcos Lemus - Main Program, GUI
Package: creature.geeksquad.gui

I was responsible for creating the GUI, drawing the creature, and the main loop. I also controlled the threads and was supposed to trigger breeding across populations, which gave some dead lock issues so is not implemented. I mainly made calls to different components in order to update keep the GUI updated. Because the GUI has so many different pieces most of my time was spent putting everything together and making sure everything was updating and working as it need to. I was also responsible for saving and loading creatures and I began to implement the saving and loading of entire population but simply ran out of time. 

Making the creature tree was an interesting task and after much experimentation I ended up just parsing the hoppers to string to construct the map.

### Ramón A. Lovato - Genetics
Packages: creature.geeksquad.genetics, creature.geeksquad.library

As the genetics programmer for EVC, my job was to come up with a system that would enable the creatures to store and share their virtual genetic information. To do this, I created the following hierarchy of data structures in the creature.geeksquad.genetics package:

• Population
A collection for holding Hoppers and triggering population-level functions such as breeding (crossover), hill-climbing initialization, and elimination (culling). Each time a population’s update method is called, the population’s generation counter increments, and it randomly decides whether to perform hill-climbing or breeding that generation. If hill-climbing is selected, the population passes all of its currently active creatures into the hill-climbing module for improvement. If breeding is selected, the population selects 20 percent of the population’s highest-performing creatures and 10 percent randomly distributed other creatures and places them into a breeding pool, from which the creatures are passed in pairs to the crossover module.

• Hopper
The basic unit creature, hoppers have a name and unique serial identifier, genotype, phenotype (and instance of the phenotype.Creature class), body structure, age, etc. To be considered valid, a hopper must have a valid genotype and phenotype.

• Genotype
Contains the hoppers’ genetic structure: an array list of genes, from which the creature’s body and phenotype can be derived. Genotype is one of the most complex classes in the genetics package. In addition to storing the genetic data, the genotype contains all of the methods for parsing the genetic data and generating new genetic data, whether procedurally or randomly.

• Gene
A pair of alleles packaged together as a single unit. The allele with the highest weight is deemed the dominant allele and is the allele expressed in the creature’s phenotype.

• Allele
The basic building block of the genetic structure. Alleles come in a number of different types (called “traits”), and contain a value and weight. The trait of an allele determines which part of the creature’s phenotype the allele represents; the value determines what state that part assumes; and the weight is compared with the pair allele in the gene to determine which allele is dominant. Although alleles have three distinct data fields, only two are used in general comparisons. Two alleles are considered equal if their traits and values are the same, but not their weights. Weights are only used at the gene level to determine which allele is expressed in the final phenotype.

• Crossover
The crossover module was originally conceived to be a static procedure into which two parent genotypes could be passed to produce offspring. This later morphed into the idea to use the crossover module to also store a history of information about the allele weights. This ended up being too problematic to be useful: the number of entries that could be effectively saved was limited by access speed and available memory, whereas the number of individual alleles in use in a population at any time is many orders of magnitude larger. In the end, crossover went back to being a static library, but it retains the spirit of its previous incarnation. There are seven different types of crossover defined within the module: four primary singleton strategies and three additional strategies that combine the 50-50 random strategy with one of the other methods.

Notably, the 50-50 random strategy is designed to mimic real-life genetic exchange in DNA. It aligns each parent’s chromosome and selects one allele from each parent’s gene at each index along the strand, recombining the two alleles into a new gene. The remaining alleles from each position are then combined into a second gene, which is used to build a second “twin” offspring. After the children have been created, crossover compares the fitness of each child and parent. If a child has higher fitness than its parent, the weight of the child’s dominant allele is increased, and the weight of its recessive allele decreased, whereas the parent’s dominant allele is decreased and its recessive allele increased. If the child’s fitness is lower than its parent, the process is reversed. This technique has shown, through experimentation, to be extremely powerful, but also somewhat overbearing. In order to prevent the populations from very rapidly growing toward a single solution and over-homogenizing, the weight adjustments have been set extremely small so that hill-climbing has more room to influence population-level change.

Additionally, creature.geeksquad.genetics contains several “builder” classes that are used to facilitate the use of the various structures in the creatures.phenotype package. The companion package, creature.geeksquad.library, contains a number of resource files, such as the hopper and population/tribe name lists, and a single Java file, Helper, which contains the random number generator and a list of global constants.

In closing, I found maintaining genetic diversity to be a significant challenge. Several techniques were implemented and then abandoned to counteract this problem, but I never did find a satisfactory solution. The combination of rapid homogenization; the slow pace at which random, valid genotypes can be created; and problems with the fitness evaluation led to mixed results in this area.

## External Sources

Java Open Graphics Library (JOGL), face.png, fur.png, grass.png, sky.png, hoppernames.txt, tribenames.txt.

## Known Issues

• Switching the simulator on and off occasionally causes one of the tribes to refuse to pause, causing the GUI to become unresponsive.
• Weighted crossover can be overbearing, causing very rapid growth toward a single solution very early on at a loss of genetic diversity. This has been countered somewhat by reducing the step size and strength of weight shifts, but the solution is incomplete.
• Generated Javadocs for the class files are incomplete due to the Javadocs for creature.phenotype and creature.physcis (which were provided as part of the project and which we didn’t write) being formatted incorrectly.
