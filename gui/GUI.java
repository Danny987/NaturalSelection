package creature.geeksquad.gui;

import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.library.KeyBinds;
import creature.geeksquad.library.PlayerControls;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
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

    //Controls
    private final PlayerControls controls = new PlayerControls();
    private final Map<String, Boolean> controlMap = controls.getInputs();

    //Colors
    private final Color FONTCOLOR = new Color(205, 205, 205);
    private final Color BACKGROUND_COLOR = new Color(55, 55, 55);

    //Read and save files
    //Tribe Names
    private List<String> nameList = new ArrayList<>();
    private List<Tribe> tribeList = new ArrayList<>();
    private Tribe currentTribe;

    // Contains opengl graphics
    private GraphicsPanel graphicsPanel;
    private Renderer renderer;

    private Timer timer;
    private Timer keyTimer;

    private Hopper hopper = null;

    //Maintab
    private JTabbedPane mainTab;

    //Mainpanel contains upper panel and lower panel
    private Panel mainPanel;
    private JScrollPane scroll;

    // Statistics stuff/////////////////////////////////
    private JLabel currentFitness;
    private Panel statsPanel;
    private JLabel totalHillclimbs;
    private JLabel totalBreed;
    private Hopper bestHopper;
    private JLabel bestFitness;

    /////////////////////////////////////////////////////
    // contains all buttons, sliders, and JComboBox
    private Panel buttonsPanel;

    // panel for the slider and or jcombobox
    private Panel bottomPanel;

    // Display the statistics
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
    private Button writePopulation;
    private Button loadPopulation;
    private Button getBest;

    // Label...
    private JLabel currentCreature;

    // slider used to choose creatures
    private Slider slider;

    // used to choose tribe
    private JComboBox tribes;

    // Disable or enable all searching threads
    private boolean paused = true;

    /**
     * Calls JPanel super and initializes the list of name.
     *
     * @param tribeList
     * @param nameList
     */
    public GUI(List<Tribe> tribeList, List<String> nameList) {
        super("Creature Creator");

        this.tribeList = tribeList;
        this.nameList = nameList;

        currentTribe = tribeList.get(0);
        init();

        Log.loadingScreen();
        int numbRunning = 0;
        while (numbRunning < 8) {
            numbRunning = 0;
            for (Tribe t : tribeList) {
                if (t.isRunning()) {
                    numbRunning++;
                }
            }
            Log.updateProgress(numbRunning);
        }

        setVisible(true);
    }

    /**
     * Listens for buttons, slider, and JComboBox
     *
     * @param e ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(keyTimer)) {
            if (hopper != null) {
                currentFitness.setText("Current Fitness: " + hopper.getPhenotype().advanceSimulation());
            }

            rotate();
            zoom();
            return;
        }

        if (e.getSource().equals(timer)) {
            if (!paused) {
                time.setText(" Time: " + time());
                if (currentTribe.isRunning()) {
                    totalGenerations = currentTribe.getGenerations();
                }
                generations.setText("Total Generations: " + totalGenerations);

                if (secondsSinceStart != 0) {
                    generationsPerSecond.setText("Generations/second: " + (float) totalGenerations / secondsSinceStart);
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
                if (currentTribe.isRunning()) {
                    Log.hopper(this, hopper, currentTribe.getName());
                }
                break;

            // Read user selected Genome
            case "Load Genome":
                loadGenotype();

                break;

            case "Write Population":
                Log.population(this, currentTribe.getPopulation(), currentTribe.getName());
                break;
            case "Load Population":
                break;
            case "Overachiever":
                Hopper overachiever = currentTribe.getOverachiever();
                break;

            // Step Next Generation
            case "Next Generation":
                for (Tribe t : tribeList) {
                    t.nextGeneration();
                }

                slider.setMaximum(currentTribe.getSize() - 1);
                currentTribe = tribeList.get(tribes.getSelectedIndex());

                try {
                    hopper = new Hopper(currentTribe.getHopper(0));
                    renderer.setHopper(hopper);
                } catch (GeneticsException ex) {
                    Log.error(ex.toString());
                }
                break;

            case "Change Tribe":
                currentTribe = tribeList.get(tribes.getSelectedIndex());
                if (hopper != null) {
                    try {
                        hopper = new Hopper(currentTribe.getHopper(0));
                        slider.setMaximum(currentTribe.getSize() - 1);
                        slider.setValue(0);
                        renderer.setHopper(hopper);
                        mainTab.setTitleAt(1, hopper.getName());
                    } catch (GeneticsException ex) {
                        Log.error(ex.toString());
                    }
                }
                break;
        }

        // if pause unpause the next generation button
        nextGeneration.setEnabled(paused);
    }

    private String time() {
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
            slider.setMaximum(currentTribe.getSize() - 1);
            hopper = new Hopper(currentTribe.getHopper(slider.getValue()));
            renderer.setHopper(hopper);
            currentFitness.setText("Current Fitness: " + hopper.getFitness());

            if (hopper != null) {
                mainTab.setTitleAt(1, hopper.getName());
            }
        } catch (GeneticsException | NullPointerException ex) {
            Log.error(ex.toString());
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
     * Read Genotype from user selected file
     */
    private void loadGenotype() {
        hopper = Log.loadHopper(this, hopper);

        if (hopper != null) {
            currentTribe.addHopper(hopper);
            slider.setMaximum(currentTribe.getSize() - 1);

            renderer.setHopper(hopper);
            mainTab.setTitleAt(1, hopper.getName());
            graphicsPanel.startAnimator();
            animate.setText("Animator On");

            secondsSinceStart = 0;
            minutesSinceStart = 0;
            totalGenerations = 0;
        }
    }

    private void loadPopulation() {

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
        setResizable(false);
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

        // Stats contains number of sucessful breads/hill climbs, over achiever for population, heighest fitness
        statsPanel = new Panel(WIDTH, HEIGHT);
        statsPanel.setBackground(BACKGROUND_COLOR);
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));

        totalHillclimbs = new JLabel("Total Hill Climb Generations: 0");
        totalHillclimbs.setForeground(FONTCOLOR);
        totalBreed = new JLabel("Total Breed Generations: 0");
        totalBreed.setForeground(FONTCOLOR);
        bestFitness = new JLabel("Best Fitness: 0");
        bestFitness.setForeground(FONTCOLOR);

        statsPanel.add(totalHillclimbs);
        statsPanel.add(totalBreed);
        statsPanel.add(bestFitness);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        buttonsPanel = new Panel(WIDTH - graphicsPanel.getWidth(), HEIGHT);
        buttonsPanel.setBackground(BACKGROUND_COLOR);

        bottomPanel = new Panel(WIDTH, 20);
        bottomPanel.setBackground(BACKGROUND_COLOR);

        time = new JLabel(" Time: 0:00");
        time.setForeground(FONTCOLOR);

        // labels /////////////////////////////////////////////////////////
        generationsPerSecond = new JLabel("Generations/second: 0");
        generationsPerSecond.setForeground(FONTCOLOR);

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
        animate.setText("Animator On");
        nextGeneration = new Button(130, 20, "Next Generation");
        writeFile = new Button(130, 20, "Write Genome");
        loadFile = new Button(130, 20, "Load Genome");
        writePopulation = new Button(130, 20, "Write Population");
        loadPopulation = new Button(130, 20, "Load Population");
        getBest = new Button(130, 20, "Overachiever");
        //////////////////////////////////////////////////////////

        statsPanel.add(getBest);

        // Initialize tribes JComboBox/////////////////////////////
        tribes = new JComboBox(nameList.toArray());
        tribes.setSize(new Dimension(WIDTH - graphicsPanel.getWidth() - 20, 20));
        tribes.setPreferredSize(new Dimension(WIDTH - graphicsPanel.getWidth() - 20, 20));
        tribes.setMaximumSize(new Dimension(WIDTH - graphicsPanel.getWidth() - 20, 20));
        tribes.setMinimumSize(new Dimension(WIDTH - graphicsPanel.getWidth() - 20, 20));
        tribes.setActionCommand("Change Tribe");
        ///////////////////////////////////////////////////////////

        // Make slider////////////////////////////////////////////
        slider = new Slider("Creature", 0, 0, 0);
        if (currentTribe.isRunning()) {
            slider.setMaximum(currentTribe.getSize() - 1);
        }
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
        writePopulation.addActionListener(this);
        loadPopulation.addActionListener(this);
        getBest.addActionListener(this);
        tribes.addActionListener(this);
        ///////////////////////////////////////////////

        currentFitness = new JLabel("Creature Fitness: 0");
        currentFitness.setForeground(FONTCOLOR);

        // Add things to the buttons panel
        buttonsPanel.add(tribes);
        buttonsPanel.add(pause);
        buttonsPanel.add(nextGeneration);
        buttonsPanel.add(animate);
        buttonsPanel.add(currentCreature);
        buttonsPanel.add(slider);
        buttonsPanel.add(writeFile);
        buttonsPanel.add(loadFile);
        buttonsPanel.add(writePopulation);
        buttonsPanel.add(loadPopulation);
        buttonsPanel.add(getBest);
        buttonsPanel.add(currentFitness);
        //////////////////////////////////////////////

        // Setup the upper panel
        mainPanel.add(graphicsPanel);
        mainPanel.add(buttonsPanel);
        //////////////////////////////////////////////

        // add the main panel to "this"
        mainTab.addMouseListener(this);
        mainTab.addTab("Main", mainPanel);
        mainTab.addTab("", scroll);
        mainTab.addTab("Stats", statsPanel);

        if (hopper != null) {
            mainTab.setTitleAt(1, hopper.getName());
        }

        add(mainTab, BorderLayout.CENTER);
        add(bottomPanel);

        // Initialize the next generations button to off.
        nextGeneration.setEnabled(paused);

        // Pack because why not?
        pack();

        timer = new Timer(1000, this);
        keyTimer = new Timer(1000 / 60, this);

        graphicsPanel.requestFocus();
        KeyBinds keyBinds = new KeyBinds((JComponent) getContentPane(), controls);

        timer.start();
        keyTimer.start();
    }

    private void rotate() {
        if (controlMap.get("left") && graphicsPanel.animating()) {
            renderer.rotateLeft();
        }
        else if (controlMap.get("right") && graphicsPanel.animating()) {
            renderer.rotateRight();
        }
    }

    private void zoom() {
        if (controlMap.get("up")) {
            renderer.zoomIn();
        }
        if (controlMap.get("down")) {
            renderer.zoomOut();
        }
    }
}
