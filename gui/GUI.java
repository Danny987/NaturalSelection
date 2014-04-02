package creature.geeksquad.gui;

import creature.geeksquad.genetics.Allele;
import creature.geeksquad.genetics.Gene;
import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Genotype;
import creature.geeksquad.genetics.Hopper;
import creature.phenotype.Block;
import creature.phenotype.Creature;
import creature.phenotype.EnumJointSite;
import creature.phenotype.EnumJointType;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Marcos
 */
public class GUI extends JFrame implements ActionListener, MouseListener, Runnable {

    //Size
    private final int WIDTH = 640;
    private final int HEIGHT = 600;

    //Colors
    private final Color FONTCOLOR = new Color(205, 205, 205);
    private final Color BACKGROUND_COLOR = new Color(55, 55, 55);

    //Read and save files
    private JFileChooser fileChooser = new JFileChooser();

    //Tribe Names
    private final List<String> nameList = new ArrayList<>();
    private final List<Tribe> tribeList = new ArrayList<>();
    private List<Hopper> currentTribe = new ArrayList<>();

    // Contains opengl graphics
    private GraphicsPanel graphicsPanel;
    private Renderer renderer;

    private Hopper hopper;

    //Maintab
    private JTabbedPane mainTab;

    //Mainpanel contains upper panel and lower panel
    private Panel mainPanel;

    // contains all buttons, sliders, and JComboBox
    private Panel buttonsPanel;

    // Display the statistics
    private JScrollPane scroll;
    private Panel stats;
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
    private JLabel currentCreatureName;

    // slider used to choose creatures
    private Slider slider;

    // used to choose tribe
    private JComboBox tribes;

    // Disable or enable all searching threads
    private boolean paused = false;

    /**
     * Calls JPanel super and initializes the list of name.
     */
    public GUI() {
        super("Creature Creator");

        int numberofcores = Runtime.getRuntime().availableProcessors();
        for (int i = 0; i < numberofcores; i++) {
            String name = "Tribe " + i;
            tribeList.add(new Tribe(name));
            nameList.add(name);
        }

        currentTribe = tribeList.get(0).getList();

        try {
            hopper = new Hopper(currentTribe.get(0));
        } catch (GeneticsException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Listens for buttons, slider, and JComboBox
     *
     * @param e ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        mainTab.getSelectedIndex();
        switch (e.getActionCommand()) {

            // Animate Crature
            case "Animate":
                if (graphicsPanel.animating()) {
                    graphicsPanel.stopAnimator();
                }
                else {
                    graphicsPanel.startAnimator();
                }
                break;

            //Pause all threads
            case "Pause":
                if (!paused) {
                    pause.setText("Start");
                    paused = true;
                }
                else {
                    pause.setText("Pause");
                    paused = false;
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
                break;

            case "Change Tribe":
                currentTribe = tribeList.get(tribes.getSelectedIndex()).getList();
                try {
                    hopper = new Hopper(currentTribe.get(0));
                    slider.setValue(0);
                    renderer.setHopper(hopper);
                    currentCreatureName.setText(hopper.getName());
                } catch (GeneticsException ex) {
                    ex.printStackTrace();
                }
                break;
        }

        // if pause unpause the next generation button
        nextGeneration.setEnabled(paused);
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (mainTab.getSelectedIndex() == 1) {
            populateTree();
        }
        else {
            graphicsPanel.startAnimator();
        }

        try {
            hopper = new Hopper(currentTribe.get(slider.getValue()));
            renderer.setHopper(hopper);
            graphicsPanel.startAnimator();
            graphicsPanel.stopAnimator();
            currentCreatureName.setText(hopper.getName());
        } catch (GeneticsException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Runs on it's own thread
     */
    @Override
    public void run() {
        init();
        setVisible(true);
    }

    /**
     * Populate the tree with the current creatures phenotype
     */
    private void populateTree() {
        String title = hopper.getName();

        
        Block[] body = hopper.getGenotype().getBody();

        root.removeAllChildren();
        for (Block b : body) {
            DefaultMutableTreeNode blockTree;

            String[] blockArray = b.toString().split("\n");

            blockTree = new DefaultMutableTreeNode(blockArray[0]);
            blockTree.add(new DefaultMutableTreeNode(blockArray[1]));

            if (blockArray.length > 2) {
                DefaultMutableTreeNode ruleTree = null;
                for (int j = 2; j < blockArray.length; j++) {
                    if (blockArray[j].contains("Rule Table")) {
                        ruleTree = new DefaultMutableTreeNode(blockArray[j]);
                        blockTree.add(ruleTree);
                    }
                    else {
                        ruleTree.add(new DefaultMutableTreeNode(blockArray[j]));
                    }
                }
                blockTree.add(ruleTree);
            }

            root.add(blockTree);
        }

        table.setBorder(BorderFactory.createTitledBorder(null,
                                                         title,
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
                reader = new BufferedReader(new FileReader(f));
                reader.readLine();
            } catch (FileNotFoundException ex) {
                System.out.println("Error reading file.");
            } catch (IOException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
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
                System.exit(0);
            }
        });
        ///////////////////////////////////////////////////

        // Used to initialize the opengl graphics
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        graphicsPanel = new GraphicsPanel(500, HEIGHT, caps);
        renderer = graphicsPanel.getRenderer();

        try {
            renderer.setHopper(new Hopper(hopper));
        } catch (GeneticsException ex) {
            ex.printStackTrace();
        }
        //////////////////////////////////////////////////

        // main tab///////////////////////////////////////
        mainTab = new JTabbedPane();
        mainTab.setSize(WIDTH, HEIGHT);
        mainTab.setMinimumSize(new Dimension(WIDTH, HEIGHT + 20));
        mainTab.setMaximumSize(new Dimension(WIDTH, HEIGHT + 20));
        //////////////////////////////////////////////////

        // Initialize JPanels
        mainPanel = new Panel(WIDTH, HEIGHT);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        buttonsPanel = new Panel(140, HEIGHT);
        buttonsPanel.setBackground(BACKGROUND_COLOR);

        stats = new Panel(140, 140);
        stats.setBackground(Color.red);
        //////////////////////////////////////////////////////////////////

        //Initialize buttons
        pause = new Button(130, 20, "Pause");
        animate = new Button(130, 20, "Animate");
        nextGeneration = new Button(130, 20, "Next Generation");
        writeFile = new Button(130, 20, "Write Genome");
        loadFile = new Button(130, 20, "Load Genome");
        //////////////////////////////////////////////////////////

        // Initialize tribes JComboBox/////////////////////////////
        tribes = new JComboBox(nameList.toArray());
        tribes.setSize(new Dimension(130, 20));
        tribes.setPreferredSize(new Dimension(130, 20));
        tribes.setMaximumSize(new Dimension(130, 20));
        tribes.setMinimumSize(new Dimension(130, 20));
        tribes.setActionCommand("Change Tribe");
        ///////////////////////////////////////////////////////////

        // Make slider////////////////////////////////////////////
        slider = new Slider("Creature", 0, Tribe.POPULATION_SIZE, 0);
        slider.addMouseListener(this);
        ////////////////////////////////////////////////////////

        // Current Creature Label/////////////////////////////////
        currentCreature = new JLabel("   Current Creature   ");
        currentCreature.setForeground(FONTCOLOR);
        
        currentCreatureName = new JLabel(hopper.getName());
        currentCreatureName.setForeground(FONTCOLOR);
        //////////////////////////////////////////////////////////

        // Setup default table/////////////////////////////////////
        table = new Panel(WIDTH + 300, HEIGHT);
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
        buttonsPanel.add(pause);
        buttonsPanel.add(nextGeneration);
        buttonsPanel.add(tribes);
        buttonsPanel.add(animate);
        buttonsPanel.add(currentCreature);
        buttonsPanel.add(currentCreatureName);
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
        mainTab.addTab("Phenotype", scroll);

        add(mainTab, BorderLayout.CENTER);

        // Initialize the next generations button to off.
        nextGeneration.setEnabled(paused);

        // Pack because why not?
        pack();
    }

    /**
     * Setup a test creature
     */
    private void testCreature() {
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

        hopper = new Hopper(genotype, "Joel");
    }
}
