
package creature.geeksquad.gui;

import creature.geeksquad.genetics.Allele;
import creature.geeksquad.genetics.Gene;
import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Genotype;
import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.genetics.Population;
import creature.geeksquad.library.Helper;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Marcos
 */
public class Log {

    public static final int NUMB_CORES = Runtime.getRuntime().availableProcessors() > 8 ? 8
                                         : Runtime.getRuntime().availableProcessors();

    private static PrintWriter writer;
    private static BufferedReader reader;
    private static String directory = "logfile";

    private static final JFileChooser fileChooser = new JFileChooser();
    private static Map<String, File> files = new HashMap<>();

    /**
     * Initialize error files for all tribes and a global file for anything extra
     * All files made in a user specified folder.
     *
     */
    public static void initialize() {
        String selectedDirectory = (String) JOptionPane.showInputDialog(null,
                                                                        "Where would you like to save files?",
                                                                        "Evolving Virtual Creatures",
                                                                        JOptionPane.QUESTION_MESSAGE,
                                                                        null, null, directory);

        if(selectedDirectory != null) directory = selectedDirectory;
        
        File dir = new File(directory);
        dir.mkdir();
        
        File file = new File(directory + Helper.SEPARATOR + "ERROR.txt");
        try {
            file.createNewFile();
            files.put("ERROR", file);
        } catch (IOException ex) {
            Log.popup(null, "Error file creation failed");
        }
    }

    /**
     * Write an error to the global error file.
     *
     * @param s the error string
     */
    public synchronized static void error(String s) {
        try {
            writer = new PrintWriter(new FileWriter(files.get("ERROR"), true));
            writer.println(s);
            writer.close();
        } catch (IOException ex) {
            Log.popup(null, "Error saving error file");
        }
    }

    /**
     * Write to specific error file.
     *
     * @param s
     * @param tribeName
     */
    public synchronized static void error(String s, String tribeName) {
        File file;
        if (files.containsKey(tribeName)) {
            file = files.get(tribeName);
        }
        else {
            file = new File(directory + Helper.SEPARATOR + tribeName + ".txt");
            try {
                file.createNewFile();
                files.put(tribeName, file);
            } catch (IOException ex) {
                Log.error(ex.toString());
            }
        }

        try {
            writer = new PrintWriter(new FileWriter(file, true));
            writer.println(s);
            writer.close();
        } catch (IOException ex) {
            Log.error(ex.toString());
        }
    }

    /**
     * Saves an entire population to a file.
     *
     * @param parent
     * @param population
     * @param tribeName
     */
    public synchronized static void population(Component parent, Population population, String tribeName) {
        int option = fileChooser.showSaveDialog(parent);

        if (option != JFileChooser.CANCEL_OPTION) {
            File f = fileChooser.getSelectedFile();

            try {
                writer = new PrintWriter(new FileWriter(f));
                writer.print(tribeName + Helper.NEWLINE + population.toString());
                writer.close();
            } catch (IOException ex) {
                Log.popup(parent, "An error occured while saving " + tribeName + ".");
                Log.error(ex.toString(), tribeName);
            }
        }
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
        int option = fileChooser.showSaveDialog(parent);

        if (option != JFileChooser.CANCEL_OPTION) {
            File f = fileChooser.getSelectedFile();

            try {
                writer = new PrintWriter(new FileWriter(f + "_" + hopper.getAge()));
                writer.print(hopper.toString());
                writer.close();
            } catch (IOException ex) {
                Log.popup(parent, "An error occured while saving " + hopper.getName() + " in " + tribeName + ".");
                Log.error(ex.toString());
            }
        }
    }

    /**
     * Get the best hopper in the population
     * @param parent 
     * @param hopper 
     */
    public synchronized static void bestHopper(Component parent, Hopper hopper){
        String name = directory + Helper.SEPARATOR + hopper.getName() + "_" + hopper.getAge();
        try {
                writer = new PrintWriter(new FileWriter(name + "_" + hopper.getAge()));
                writer.print(hopper.toString());
                writer.close();
            } catch (IOException ex) {
                Log.popup(parent, "An error occured while saving " + hopper.getName());
                Log.error(ex.toString());
            }
    }
    
    /**
     * Load a user selected hopper
     *
     * @param parent
     * @param hopper
     *
     * @return new hopper
     */
    public static Hopper loadHopper(Component parent, Hopper hopper) {
        int option = fileChooser.showOpenDialog(parent);

        if (option != JFileChooser.CANCEL_OPTION) {
            try {
                File f = fileChooser.getSelectedFile();
                String[] lineArray;
                String name;
                List<Allele> alleles = new ArrayList<>();
                ArrayList<Gene> genes;

                reader = new BufferedReader(new FileReader(f));
                return parseHopper(reader, hopper);
            } catch (GeneticsException |
                     FileNotFoundException |
                     IllegalArgumentException ex) {
                popup(parent, "Loading Hopper Failed");
                Log.error(ex.toString());
            } catch (IOException ex) {
                popup(parent, "Loading Hopper Failed");
                Log.error(ex.toString());
            }
        }

        return hopper;
    }

    /**
     * parse a hoppers string
     * @param reader
     * @param hopper
     * @return Hopper 
     * @throws GeneticsException
     * @throws IllegalArgumentException
     * @throws IOException 
     */
    private static Hopper parseHopper(BufferedReader reader, Hopper hopper) throws GeneticsException, IllegalArgumentException, IOException {
        String line;
        String name;
        List<Allele> alleles = new ArrayList<>();
        ArrayList<Gene> genes;
        String[] lineArray;

        try {

            reader.readLine();
            reader.readLine();
            name = reader.readLine();
            reader.readLine();
            reader.readLine();

            line = reader.readLine();

            while (!line.contains("/genotype") && line.length() > 2) {

                if (line.startsWith("{")) {
                    line = line.replace("{", "");
                }
                if (line.endsWith("}")) {
                    line = line.replace("}", "");
                }

                line = line.substring(1, line.length() - 1);

                lineArray = line.split("\\)\\(");

                alleles.add(Allele.stringToAllele(lineArray[0] + ")"));
                alleles.add(Allele.stringToAllele("(" + lineArray[1]));

                line = reader.readLine();
            }

            genes = Gene.allelesToGenes(alleles);
            Genotype genotype = new Genotype(genes);

            hopper = new Hopper(genotype, name);

        } catch (GeneticsException | IllegalArgumentException | IOException ex) {
            throw (ex);
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
        int option = fileChooser.showOpenDialog(parent);
        Population p = new Population();
        Log.popup(null, "Loading Creature Is Currently Broken.");
        
        if (option != JFileChooser.CANCEL_OPTION && false) {
            try {
                File f = fileChooser.getSelectedFile();
                String[] lineArray;
                String tribeName = "";
                String hopperName = "";
                Hopper hopper;
                List<Allele> alleles = new ArrayList<>();
                ArrayList<Gene> genes;

                reader = new BufferedReader(new FileReader(f));

                String line;
                tribeName = reader.readLine();
                reader.readLine();
                reader.readLine();
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
                hopper = new Hopper(genotype, hopperName);

            } catch (GeneticsException | FileNotFoundException ex) {
                Log.popup(parent, "Error loading population.");
                Log.error(ex.toString());
            } catch (IOException ex) {
                Log.popup(parent, "Error loading population.");
                Log.error(ex.toString());
            }
        }
    }

    /**
     * Makes a JOptionPane to show a message
     * @param parent
     * @param message 
     */
    public static void popup(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message);
    }

    public static void main(String arg[]) {
        Log.initialize();
        Log.error("test");
    }
}
