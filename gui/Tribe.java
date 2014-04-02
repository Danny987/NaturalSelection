package creature.geeksquad.gui;

import creature.geeksquad.genetics.Allele;
import creature.geeksquad.genetics.Gene;
import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Genotype;
import creature.geeksquad.genetics.Hopper;
import creature.phenotype.Block;
import creature.phenotype.EnumJointSite;
import creature.phenotype.EnumJointType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Marcos
 */
public class Tribe extends Thread {

    public String name;
    public static final int POPULATION_SIZE = 100;
    private List<Hopper> hoppers = new ArrayList<>();
    private boolean paused = false;
    private boolean running = true;

    public Tribe(String name) {
        this.name = name;
        
        String hopperName;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            hopperName = "Joel " + i;
            hoppers.add(generateHopper(hopperName));
        }
    }

    /**
     * Call things to run hill climbing and cross over.
     */
    public void nextGeneration() {

    }

    @Override
    public void run() {
        while (running) {

            // if the thread is interupted pause or unpause
            if (Thread.interrupted()) {
                paused = !paused;
            }

            // if not paused let them mutate
            if (!paused) {
                nextGeneration();
            }
        }
    }

    /**
     * Returns the Tribes population.
     *
     * @return List of Hopper
     */
    public synchronized List<Hopper> getList() {
        return hoppers;
    }

    /**
     * generates a functional hopper.
     *
     * @return new Hopper
     */
    private Hopper generateHopper(String hopperName) {
        ArrayList<Allele> alleles = new ArrayList<>();
        ArrayList<Gene> genes;

        //Body[0]
        alleles.add(new Allele(Allele.Trait.LENGTH, 2f, 0.3f));
        alleles.add(new Allele(Allele.Trait.LENGTH, 2f, 0.64f));

        alleles.add(new Allele(Allele.Trait.HEIGHT, 1f, 0.5f));
        alleles.add(new Allele(Allele.Trait.HEIGHT, 1f, 0.35f));

        alleles.add(new Allele(Allele.Trait.WIDTH, 3f, 0.5f));
        alleles.add(new Allele(Allele.Trait.WIDTH, 3f, 0.35f));

        alleles.add(new Allele(Allele.Trait.INDEX_TO_PARENT, Block.PARENT_INDEX_NONE, 0.63f));
        alleles.add(new Allele(Allele.Trait.INDEX_TO_PARENT, Block.PARENT_INDEX_NONE, 0.4f));

        //Body[1]
        alleles.add(new Allele(Allele.Trait.LENGTH, 1f, 0.2f));
        alleles.add(new Allele(Allele.Trait.LENGTH, 1f, 0.199f));

        alleles.add(new Allele(Allele.Trait.HEIGHT, 3f, 0.1f));
        alleles.add(new Allele(Allele.Trait.HEIGHT, 3f, 0.4f));

        alleles.add(new Allele(Allele.Trait.WIDTH, 1f, 0.5f));
        alleles.add(new Allele(Allele.Trait.WIDTH, 1f, 0.6f));

        alleles.add(new Allele(Allele.Trait.INDEX_TO_PARENT, 0, 0.63f));
        alleles.add(new Allele(Allele.Trait.INDEX_TO_PARENT, 0, 0.4f));

        alleles.add(new Allele(Allele.Trait.JOINT_TYPE, EnumJointType.HINGE, 0.0f));
        alleles.add(new Allele(Allele.Trait.JOINT_TYPE, EnumJointType.HINGE, 0.2f));
        alleles.add(new Allele(Allele.Trait.JOINT_ORIENTATION, 0f, 0.5f));
        alleles.add(new Allele(Allele.Trait.JOINT_ORIENTATION, 0f, 0.5f));

        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_PARENT, EnumJointSite.VERTEX_FRONT_SOUTHEAST, 1f));
        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_PARENT, EnumJointSite.VERTEX_FRONT_SOUTHEAST, 0.3f));

        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_CHILD, EnumJointSite.VERTEX_BACK_SOUTHWEST, 1f));
        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_CHILD, EnumJointSite.VERTEX_BACK_SOUTHWEST, 0.7f));

        //Body[2]
        alleles.add(new Allele(Allele.Trait.LENGTH, 1f, 0.2f));
        alleles.add(new Allele(Allele.Trait.LENGTH, 1f, 0.199f));

        alleles.add(new Allele(Allele.Trait.HEIGHT, 3f, 0.1f));
        alleles.add(new Allele(Allele.Trait.HEIGHT, 3f, 0.4f));

        alleles.add(new Allele(Allele.Trait.WIDTH, 1f, 0.5f));
        alleles.add(new Allele(Allele.Trait.WIDTH, 1f, 0.6f));

        alleles.add(new Allele(Allele.Trait.INDEX_TO_PARENT, 0, 0.63f));
        alleles.add(new Allele(Allele.Trait.INDEX_TO_PARENT, 0, 0.4f));

        alleles.add(new Allele(Allele.Trait.JOINT_TYPE, EnumJointType.HINGE, 0.1f));
        alleles.add(new Allele(Allele.Trait.JOINT_TYPE, EnumJointType.HINGE, 0.2f));
        alleles.add(new Allele(Allele.Trait.JOINT_ORIENTATION, 0f, 0.5f));
        alleles.add(new Allele(Allele.Trait.JOINT_ORIENTATION, 0f, 0.5f));

        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_PARENT, EnumJointSite.VERTEX_FRONT_SOUTHWEST, 1f));
        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_PARENT, EnumJointSite.VERTEX_FRONT_SOUTHWEST, 0.3f));

        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_CHILD, EnumJointSite.VERTEX_BACK_SOUTHEAST, 1f));
        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_CHILD, EnumJointSite.VERTEX_BACK_SOUTHEAST, 0.7f));

        //Body[3]
        alleles.add(new Allele(Allele.Trait.LENGTH, 1f, 0.2f));
        alleles.add(new Allele(Allele.Trait.LENGTH, 1f, 0.199f));

        alleles.add(new Allele(Allele.Trait.HEIGHT, 3f, 0.1f));
        alleles.add(new Allele(Allele.Trait.HEIGHT, 3f, 0.4f));

        alleles.add(new Allele(Allele.Trait.WIDTH, 1f, 0.5f));
        alleles.add(new Allele(Allele.Trait.WIDTH, 1f, 0.6f));

        alleles.add(new Allele(Allele.Trait.INDEX_TO_PARENT, 0, 0.63f));
        alleles.add(new Allele(Allele.Trait.INDEX_TO_PARENT, 0, 0.4f));

        alleles.add(new Allele(Allele.Trait.JOINT_TYPE, EnumJointType.HINGE, 0.1f));
        alleles.add(new Allele(Allele.Trait.JOINT_TYPE, EnumJointType.HINGE, 0.2f));
        alleles.add(new Allele(Allele.Trait.JOINT_ORIENTATION, 0f, 0.5f));
        alleles.add(new Allele(Allele.Trait.JOINT_ORIENTATION, 0f, 0.5f));

        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_PARENT, EnumJointSite.VERTEX_BACK_SOUTHEAST, 1f));
        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_PARENT, EnumJointSite.VERTEX_BACK_SOUTHEAST, 0.3f));

        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_CHILD, EnumJointSite.VERTEX_BACK_SOUTHWEST, 1f));
        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_CHILD, EnumJointSite.VERTEX_BACK_SOUTHWEST, 0.7f));

        //Body[4]
        alleles.add(new Allele(Allele.Trait.LENGTH, 1f, 0.2f));
        alleles.add(new Allele(Allele.Trait.LENGTH, 1f, 0.199f));

        alleles.add(new Allele(Allele.Trait.HEIGHT, 3f, 0.1f));
        alleles.add(new Allele(Allele.Trait.HEIGHT, 3f, 0.4f));

        alleles.add(new Allele(Allele.Trait.WIDTH, 1f, 0.5f));
        alleles.add(new Allele(Allele.Trait.WIDTH, 1f, 0.6f));

        alleles.add(new Allele(Allele.Trait.JOINT_TYPE, EnumJointType.HINGE, 0.1f));
        alleles.add(new Allele(Allele.Trait.JOINT_TYPE, EnumJointType.HINGE, 0.2f));

        alleles.add(new Allele(Allele.Trait.INDEX_TO_PARENT, 0, 0.63f));
        alleles.add(new Allele(Allele.Trait.INDEX_TO_PARENT, 0, 0.4f));
        alleles.add(new Allele(Allele.Trait.JOINT_ORIENTATION, 0f, 0.5f));
        alleles.add(new Allele(Allele.Trait.JOINT_ORIENTATION, 0f, 0.5f));

        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_PARENT, EnumJointSite.VERTEX_BACK_SOUTHWEST, 1f));
        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_PARENT, EnumJointSite.VERTEX_BACK_SOUTHWEST, 0.3f));

        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_CHILD, EnumJointSite.VERTEX_BACK_SOUTHEAST, 1f));
        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_CHILD, EnumJointSite.VERTEX_BACK_SOUTHEAST, 0.7f));

        //Body[5]
        alleles.add(new Allele(Allele.Trait.LENGTH, 1f, 0.2f));
        alleles.add(new Allele(Allele.Trait.LENGTH, 1f, 0.199f));

        alleles.add(new Allele(Allele.Trait.HEIGHT, 1f, 0.1f));
        alleles.add(new Allele(Allele.Trait.HEIGHT, 1f, 0.4f));

        alleles.add(new Allele(Allele.Trait.WIDTH, 1f, 0.5f));
        alleles.add(new Allele(Allele.Trait.WIDTH, 1f, 0.6f));

        alleles.add(new Allele(Allele.Trait.JOINT_TYPE, EnumJointType.TWIST, 0.1f));
        alleles.add(new Allele(Allele.Trait.JOINT_TYPE, EnumJointType.TWIST, 0.2f));

        alleles.add(new Allele(Allele.Trait.INDEX_TO_PARENT, 0, 0.63f));
        alleles.add(new Allele(Allele.Trait.INDEX_TO_PARENT, 0, 0.4f));
        alleles.add(new Allele(Allele.Trait.JOINT_ORIENTATION, 0f, 0.5f));
        alleles.add(new Allele(Allele.Trait.JOINT_ORIENTATION, 0f, 0.5f));

        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_PARENT, EnumJointSite.FACE_NORTH, 1f));
        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_PARENT, EnumJointSite.FACE_NORTH, 0.3f));

        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_CHILD, EnumJointSite.FACE_BACK, 1f));
        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_CHILD, EnumJointSite.FACE_BACK, 0.7f));

        //Body[6]
        alleles.add(new Allele(Allele.Trait.LENGTH, 1f, 0.2f));
        alleles.add(new Allele(Allele.Trait.LENGTH, 1f, 0.199f));

        alleles.add(new Allele(Allele.Trait.HEIGHT, 1f, 0.1f));
        alleles.add(new Allele(Allele.Trait.HEIGHT, 1f, 0.4f));

        alleles.add(new Allele(Allele.Trait.WIDTH, 1f, 0.5f));
        alleles.add(new Allele(Allele.Trait.WIDTH, 1f, 0.6f));

        alleles.add(new Allele(Allele.Trait.JOINT_TYPE, EnumJointType.TWIST, 0.1f));
        alleles.add(new Allele(Allele.Trait.JOINT_TYPE, EnumJointType.TWIST, 0.2f));

        alleles.add(new Allele(Allele.Trait.INDEX_TO_PARENT, 5, 0.63f));
        alleles.add(new Allele(Allele.Trait.INDEX_TO_PARENT, 5, 0.4f));
        alleles.add(new Allele(Allele.Trait.JOINT_ORIENTATION, 1f, 0.5f));
        alleles.add(new Allele(Allele.Trait.JOINT_ORIENTATION, 1f, 0.5f));

        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_PARENT, EnumJointSite.FACE_FRONT, 1f));
        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_PARENT, EnumJointSite.FACE_FRONT, 0.3f));

        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_CHILD, EnumJointSite.FACE_EAST, 1f));
        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_CHILD, EnumJointSite.FACE_EAST, 0.7f));

        //Body[7]
        alleles.add(new Allele(Allele.Trait.LENGTH, 1f, 0.2f));
        alleles.add(new Allele(Allele.Trait.LENGTH, 1f, 0.199f));

        alleles.add(new Allele(Allele.Trait.HEIGHT, 1f, 0.1f));
        alleles.add(new Allele(Allele.Trait.HEIGHT, 1f, 0.4f));

        alleles.add(new Allele(Allele.Trait.WIDTH, 1f, 0.5f));
        alleles.add(new Allele(Allele.Trait.WIDTH, 1f, 0.6f));

        alleles.add(new Allele(Allele.Trait.JOINT_TYPE, EnumJointType.TWIST, 0.1f));
        alleles.add(new Allele(Allele.Trait.JOINT_TYPE, EnumJointType.TWIST, 0.2f));

        alleles.add(new Allele(Allele.Trait.INDEX_TO_PARENT, 6, 0.63f));
        alleles.add(new Allele(Allele.Trait.INDEX_TO_PARENT, 6, 0.4f));
        alleles.add(new Allele(Allele.Trait.JOINT_ORIENTATION, 0f, 0.5f));
        alleles.add(new Allele(Allele.Trait.JOINT_ORIENTATION, 0f, 0.5f));

        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_PARENT, EnumJointSite.FACE_WEST, 1f));
        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_PARENT, EnumJointSite.FACE_WEST, 0.3f));

        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_CHILD, EnumJointSite.FACE_WEST, 1f));
        alleles.add(new Allele(Allele.Trait.JOINT_SITE_ON_CHILD, EnumJointSite.FACE_WEST, 0.7f));

        genes = Gene.allelesToGenes(alleles);

		// Build some Genes from the Alleles.
        // Create a Genotype from the Genes.
        Genotype genotype = null;
        try {
            genotype = new Genotype(genes);
        } catch (GeneticsException ex) {
            ex.printStackTrace();
        }

        return new Hopper(genotype, hopperName);
    }

    /**
     * Kills the thread.
     */
    public void kill() {
        running = false;
    }
}
