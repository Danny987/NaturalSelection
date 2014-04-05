package creature.geeksquad.gui;

import creature.geeksquad.genetics.Allele;
import creature.geeksquad.genetics.Gene;
import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Genotype;
import creature.geeksquad.genetics.Hopper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author Marcos
 */
public class GUI extends JFrame implements ActionListener, MouseListener {

    //Size
    private final int WIDTH = 700;
    private final int HEIGHT = 600;

    //Colors
    private final Color FONTCOLOR = new Color(205, 205, 205);
    private final Color BACKGROUND_COLOR = new Color(55, 55, 55);

    //Read and save files
    private final JFileChooser fileChooser = new JFileChooser();

    //Tribe Names
    private List<String> nameList = new ArrayList<>();
    private List<Tribe> tribeList = new ArrayList<>();
    private Tribe currentTribe;

    // Contains opengl graphics
    private GraphicsPanel graphicsPanel;
    private Renderer renderer;

    private Timer timer;

    private Hopper hopper;

    //Maintab
    private JTabbedPane mainTab;

    //Mainpanel contains upper panel and lower panel
    private Panel mainPanel;

    // contains all buttons, sliders, and JComboBox
    private Panel buttonsPanel;

    // panel for the slider and or jcombobox
    private Panel bottomPanel;

    // Display the statistics
    private JScrollPane scroll;
    private Panel stats;
    private JLabel time;
    private JLabel generations;
    private JLabel generationsPerSecond;

    private int totalGenerations = 0;
    private int minutesSinceStart = 0;
    private int secondsSinceStart = 0;

    private Panel table;
    private JTree tree;
    private DefaultMutableTreeNode root;

    private JTextArea rules;

    // Buttons!
    private Button pause;
    private Button animate;
    private Button nextGeneration;
    private Button writeFile;
    private Button loadFile;

    // Label...
    private JLabel currentCreature;

    // slider used to choose creatures
    private Slider slider;

    // used to choose tribe
    private JComboBox tribes;

    // Disable or enable all searching threads
    private boolean paused = true;
    private int numberofcores;

    /**
     * Calls JPanel super and initializes the list of name.
     */
    public GUI(List<Tribe> tribeList, List<String> nameList) {
        super("Creature Creator");

        this.tribeList = tribeList;
        this.nameList = nameList;

        currentTribe = tribeList.get(0);
        while (currentTribe.getSize() == -1) {
            currentTribe = tribeList.get(0);
        }

        try {
            hopper = new Hopper(currentTribe.getHopper(0));
        } catch (GeneticsException | NullPointerException ex) {
            System.out.println(ex);
        }

        init();

        setVisible(true);
    }

    /**
     * Listens for buttons, slider, and JComboBox
     *
     * @param e ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(timer)) {
            if (!paused) {
                time.setText(" Time: " + time());
                totalGenerations = currentTribe.getGenerations();
                generations.setText("Total Generations: " + totalGenerations);

                if (secondsSinceStart != 0) {
                    generationsPerSecond.setText("Generations/second: " + totalGenerations / secondsSinceStart);
                }
            }

            return;
        }

        mainTab.getSelectedIndex();
        switch (e.getActionCommand()) {

            // Animate Crature
            case "Animate":
                if (!graphicsPanel.animating()) {
                    animate.setText("Animator On");
                    graphicsPanel.startAnimator();
                }
                else {
                    animate.setText("Animator Off");
                    graphicsPanel.stopAnimator();
                }
                break;

            //Pause all threads
            case "Pause":
                paused = !paused;

                for (Tribe t : tribeList) {
                    t.interrupt();
                }

                if (paused) {
                    pause.setText("Start");
                }
                else {
                    pause.setText("Pause");
                }
                
                break;

            // Write the currently selected genome to use selected file.
            case "Write Genome":
                writeGenome();
                break;

            // Read user selected Genome
            case "Load Genome":
                loadGenotype();
                break;

            // Step Next Generation
            case "Next Generation":
                for (Tribe t : tribeList) {
                    t.nextGeneration();
                }

                currentTribe = tribeList.get(tribes.getSelectedIndex());
                slider.setMaximum(currentTribe.getSize());

                try {
                    hopper = new Hopper(currentTribe.getHopper(0));
                    renderer.setHopper(hopper);
                } catch (GeneticsException ex) {
                    System.err.println(ex);
                }
                break;

            case "Change Tribe":
                currentTribe = tribeList.get(tribes.getSelectedIndex());
                try {
                    hopper = new Hopper(currentTribe.getHopper(0));
                    slider.setMaximum(currentTribe.getSize());
                    slider.setValue(0);
                    renderer.setHopper(hopper);
                    mainTab.setTitleAt(1, hopper.getName());
                } catch (GeneticsException ex) {
                    ex.printStackTrace();
                }
                break;
        }

        // if pause unpause the next generation button
        nextGeneration.setEnabled(paused);
    }

    public String time() {
        int seconds;
        secondsSinceStart++;
        if (secondsSinceStart == 60) {
            minutesSinceStart++;
        }

        seconds = secondsSinceStart % 60;

        return minutesSinceStart + ":"
                + (seconds > 9 ? seconds : "0" + seconds);
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (mainTab.getSelectedIndex() == 1) {
            populateTree();
        }
        else {
            graphicsPanel.startAnimator();
            animate.setText("Animator On");
        }

        try {
            hopper = new Hopper(currentTribe.getHopper(slider.getValue()));
            renderer.setHopper(hopper);

            mainTab.setTitleAt(1, hopper.getName());
        } catch (GeneticsException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Populate the tree with the current creatures phenotype
     */
    private void populateTree() {
        DefaultMutableTreeNode block = null;
        String[] str;
        str = hopper.toString().split("\n");

        if (str == null) {
            return;
        }

        String title = str[2];

        root.removeAllChildren();
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.reload();

        for (int i = 5; i < str.length; i++) {
            if (str[i].equals("</genotype>") || str[i].equals("}")) {
                break;
            }
            if (str[i].contains("LENGTH")) {
                block = new DefaultMutableTreeNode(str[i++] + str[i++] + str[i++]);
                root.add(block);
            }
            else {
                block.add(new DefaultMutableTreeNode(str[i]));
            }
        }
        table.setPreferredSize(new Dimension(WIDTH + 450, root.getChildCount() * root.getLeafCount()));
        table.setBorder(BorderFactory.createTitledBorder(null,
                                                         null,
                                                         TitledBorder.LEFT,
                                                         TitledBorder.TOP,
                                                         null,
                                                         FONTCOLOR));
    }

    /**
     * Opens a JFileChoose to select a file to save a genome to
     */
    private void writeGenome() {
        BufferedWriter writer;
        int option = fileChooser.showSaveDialog(this);

        if (option != JFileChooser.CANCEL_OPTION) {
            File f = fileChooser.getSelectedFile();

            try {
                writer = new BufferedWriter(new FileWriter(f));
                writer.write(hopper.toString());
                writer.close();
            } catch (IOException ex) {
                System.out.println("Error saving file.");
            }
        }
    }

    /**
     * Read Genotype from user selected file
     */
    private void loadGenotype() {
        BufferedReader reader;
        int option = fileChooser.showOpenDialog(this);

        if (option != JFileChooser.CANCEL_OPTION) {
            File f = fileChooser.getSelectedFile();
            try {
                String[] lineArray;
                String name;
                List<Allele> alleles = new ArrayList<>();
                ArrayList<Gene> genes;

                reader = new BufferedReader(new FileReader(f));

                String line = reader.readLine();
                reader.readLine();
                name = reader.readLine();
                reader.readLine();
                reader.readLine();

                while (line != null) {
                    line = reader.readLine();

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
                }

                genes = Gene.allelesToGenes(alleles);
                Genotype genotype = new Genotype(genes);
                hopper = new Hopper(genotype, name);

                currentTribe.addHopper(hopper);

                renderer.setHopper(hopper);
                mainTab.setTitleAt(1, hopper.getName());
                graphicsPanel.startAnimator();
                animate.setText("Animator On");

                secondsSinceStart = 0;
                minutesSinceStart = 0;
                totalGenerations = 0;
            } catch (FileNotFoundException ex) {
                System.out.println(ex);
            } catch (IllegalArgumentException | GeneticsException | IOException ex) {
                System.out.println(ex);
            }

        }

    }

    /**
     * Initializes and setup the GUI
     */
    private void init() {
        // Setup JFrame
        setSize(WIDTH + 8, HEIGHT + 50);
        getContentPane().setBackground(new Color(15, 15, 15));
        setMinimumSize(new Dimension(WIDTH + 8, HEIGHT + 50));
        setMaximumSize(new Dimension(WIDTH + 8, HEIGHT + 50));
        setPreferredSize(new Dimension(WIDTH + 8, HEIGHT + 50));
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // Used to set specification for closing the window
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                graphicsPanel.kill();
                for (Tribe t : tribeList) {
                    t.kill();
                }
                System.exit(0);
            }
        });
        ///////////////////////////////////////////////////

        // Used to initialize the opengl graphics
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        graphicsPanel = new GraphicsPanel(500, HEIGHT, caps);
        renderer = graphicsPanel.getRenderer();
        renderer.setHopper(hopper);

        //////////////////////////////////////////////////
        // main tab///////////////////////////////////////
        mainTab = new JTabbedPane();
        mainTab.setSize(WIDTH, HEIGHT);
        mainTab.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        mainTab.setMaximumSize(new Dimension(WIDTH, HEIGHT));
        //////////////////////////////////////////////////

        // Initialize JPanels
        mainPanel = new Panel(WIDTH, HEIGHT);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        buttonsPanel = new Panel(WIDTH - graphicsPanel.getWidth(), HEIGHT);
        buttonsPanel.setBackground(BACKGROUND_COLOR);

        bottomPanel = new Panel(WIDTH, 20);
        bottomPanel.setBackground(BACKGROUND_COLOR);

        stats = new Panel(140, 140);
        time = new JLabel(" Time: 0:00");
        time.setForeground(FONTCOLOR);

        stats.setForeground(FONTCOLOR);
        stats.setBackground(BACKGROUND_COLOR);

        // labels /////////////////////////////////////////////////////////
        generationsPerSecond = new JLabel("Generations/second: 0");

        generations = new JLabel("Total Generations: " + totalGenerations);
        generations.setForeground(FONTCOLOR);

        bottomPanel.add(Box.createHorizontalStrut(5));
        bottomPanel.add(time);
        bottomPanel.add(Box.createHorizontalStrut(5));
        bottomPanel.add(generations);
        bottomPanel.add(Box.createHorizontalStrut(5));
        bottomPanel.add(generationsPerSecond);
        //////////////////////////////////////////////////////////////////

        //Initialize buttons
        pause = new Button(130, 20, "Pause");
        pause.setText("Start");

        animate = new Button(130, 20, "Animate");
        nextGeneration = new Button(130, 20, "Next Generation");
        writeFile = new Button(130, 20, "Write Genome");
        loadFile = new Button(130, 20, "Load Genome");
        //////////////////////////////////////////////////////////

        // Initialize tribes JComboBox/////////////////////////////
        tribes = new JComboBox(nameList.toArray());
        tribes.setSize(new Dimension(WIDTH - graphicsPanel.getWidth() - 20, 20));
        tribes.setPreferredSize(new Dimension(WIDTH - graphicsPanel.getWidth() - 20, 20));
        tribes.setMaximumSize(new Dimension(WIDTH - graphicsPanel.getWidth() - 20, 20));
        tribes.setMinimumSize(new Dimension(WIDTH - graphicsPanel.getWidth() - 20, 20));
        tribes.setActionCommand("Change Tribe");
        ///////////////////////////////////////////////////////////

        // Make slider////////////////////////////////////////////
        slider = new Slider("Creature", 0, currentTribe.getSize(), 0);
        slider.addMouseListener(this);
        ////////////////////////////////////////////////////////

        // Current Creature Label/////////////////////////////////
        currentCreature = new JLabel("   Current Creature   ");
        currentCreature.setForeground(FONTCOLOR);

        //////////////////////////////////////////////////////////
        // Setup default table/////////////////////////////////////
        table = new Panel(WIDTH, HEIGHT);
        table.setAlignmentX(CENTER_ALIGNMENT);
        table.setBorder(BorderFactory.createTitledBorder(null,
                                                         "No Selection",
                                                         TitledBorder.LEFT,
                                                         TitledBorder.TOP,
                                                         null,
                                                         FONTCOLOR));
        table.setBackground(BACKGROUND_COLOR);
        table.setForeground(FONTCOLOR);
        table.setLayout(new BorderLayout());

        scroll = new JScrollPane(table);
        rules = new JTextArea();
        rules.setBackground(BACKGROUND_COLOR);
        rules.setForeground(FONTCOLOR);

        //////////////////////////////////////////////////////////
        // Tree///////////////////////////////////////////////////
        root = new DefaultMutableTreeNode("Blocks");

        tree = new JTree(root);
        tree.setBackground(BACKGROUND_COLOR);
        tree.setForeground(FONTCOLOR);

        // Setup the colors of the tree
        final DefaultTreeCellRenderer treeRenderer = (DefaultTreeCellRenderer) (tree.getCellRenderer());
        treeRenderer.setBackgroundNonSelectionColor(BACKGROUND_COLOR);
        treeRenderer.setBackgroundSelectionColor(new Color(15, 15, 15));
        treeRenderer.setTextNonSelectionColor(FONTCOLOR);
        treeRenderer.setTextSelectionColor(FONTCOLOR);

        //////////////////////////////////////////////////////////
        // Add things to the second panel
        table.add(tree, BorderLayout.WEST);
        /////////////////////////////////

        // Add "this" ActionListener to all the buttons
        pause.addActionListener(this);
        animate.addActionListener(this);
        nextGeneration.addActionListener(this);
        writeFile.addActionListener(this);
        loadFile.addActionListener(this);
        tribes.addActionListener(this);
        ///////////////////////////////////////////////

        // Add things to the buttons panel
        buttonsPanel.add(tribes);
        buttonsPanel.add(pause);
        buttonsPanel.add(nextGeneration);
        buttonsPanel.add(animate);
        buttonsPanel.add(currentCreature);
        buttonsPanel.add(slider);
        buttonsPanel.add(writeFile);
        buttonsPanel.add(loadFile);
        buttonsPanel.add(stats);
        //////////////////////////////////////////////

        // Setup the upper panel
        mainPanel.add(graphicsPanel);
        mainPanel.add(buttonsPanel);
        //////////////////////////////////////////////

        // add the main panel to "this"
        mainTab.addMouseListener(this);
        mainTab.addTab("Main", mainPanel);
        mainTab.addTab(hopper.getName(), scroll);

        add(mainTab, BorderLayout.CENTER);
        add(bottomPanel);

        // Initialize the next generations button to off.
        nextGeneration.setEnabled(paused);

        // Pack because why not?
        pack();

        timer = new Timer(1000, this);
        timer.start();
    }
}
