
package creature.geeksquad.gui;

import creature.geeksquad.genetics.Allele;
import creature.geeksquad.genetics.Gene;
import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Genotype;
import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.genetics.Population;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;

/**
 *
 * @author Marcos
 */
public class Log {

    private static final JFileChooser fileChooser = new JFileChooser();

    /**
     * Initialize error files for all tribes and a global file for anything extra
     * All files made in a user specified folder.
     *
     * @param numberOfTribes
     */
    public static void initialize(int numberOfTribes) {

    }

    /**
     * Write an error to the global error file.
     *
     * @param s the error string
     */
    public synchronized static void error(String s) {
        System.out.println(s);
    }

    /**
     * Write to specific error file.
     *
     * @param s
     * @param tribeNumber
     */
    public synchronized static void error(String s, int tribeNumber) {

    }

    /**
     * Saves an entire population to a file.
     *
     * @param p
     * @param tribeName
     */
    public synchronized static void population(Population p, String tribeName) {

    }

    /**
     * Save given hopper to file
     * file chosen by user
     *
     * @param parent
     * @param hopper
     * @param tribeName
     */
    public synchronized static void hopper(Component parent, Hopper hopper, String tribeName) {
        BufferedWriter writer;
        int option = fileChooser.showSaveDialog(parent);

        if (option != JFileChooser.CANCEL_OPTION) {
            File f = fileChooser.getSelectedFile();

            try {
                writer = new BufferedWriter(new FileWriter(f));
                writer.write(hopper.toString());
                writer.close();
            } catch (IOException ex) {
                Log.error(ex.toString());
            }
        }
    }

    /**
     * Load a user selected hopper
     *
     * @param parent
     * @param hopper
     */
    public static Hopper loadHopper(Component parent, Hopper hopper) {
        BufferedReader reader;
        int option = fileChooser.showOpenDialog(parent);

        if (option != JFileChooser.CANCEL_OPTION) {
            try {
                File f = fileChooser.getSelectedFile();
                String[] lineArray;
                String name;
                List<Allele> alleles = new ArrayList<>();
                ArrayList<Gene> genes;

                reader = new BufferedReader(new FileReader(f));

                String line;
                reader.readLine();
                reader.readLine();
                name = reader.readLine();
                reader.readLine();
                reader.readLine();
                line = reader.readLine();

                while (line.length() > 1) {

                    if (line.startsWith("{")) {
                        line = line.replace("{", "");
                    }
                    if (line.endsWith("}")) {
                        line = line.replace("}", "");
                    }

                    line = line.substring(1, line.length() - 1);

                    lineArray = line.split("\\)\\(");

                    if (lineArray.length < 2) {
                        break;
                    }

                    alleles.add(Allele.stringToAllele(lineArray[0] + ")"));
                    alleles.add(Allele.stringToAllele("(" + lineArray[1]));
                    line = reader.readLine();
                }

                genes = Gene.allelesToGenes(alleles);
                Genotype genotype = new Genotype(genes);

                hopper = new Hopper(genotype, name);
                
            } catch (GeneticsException | IOException | IllegalArgumentException ex) {
                Log.error(ex.toString());
            }
        }

        return hopper;
    }

    /**
     * Load user selected population
     *
     * @param parent
     * @param population
     */
    public static void loadPopulation(Component parent, Population population) {

    }
}
