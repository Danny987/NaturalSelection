package creature.geeksquad.gui;

import creature.geeksquad.genetics.GeneticsException;
import creature.geeksquad.genetics.Hopper;
import creature.geeksquad.library.Helper;
import creature.geeksquad.library.KeyBinds;
import creature.geeksquad.library.PlayerControls;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author Marcos
 */
public class GUI extends JFrame implements ActionListener, ChangeListener {

    //Size
    private final int WIDTH = 700;
    private final int HEIGHT = 600;
    private Dimension size; //temp variable 

    //Controls
    private final PlayerControls controls = new PlayerControls();
    private final Map<String, Boolean> controlMap = controls.getInputs();

    //Colors
    private final Color FONTCOLOR = new Color(205, 205, 205);
    private final Color BACKGROUND_COLOR = new Color(55, 55, 55);

    // Data structures
    private List<String> nameList = new ArrayList<>(); //names for all the generations
    private List<Tribe> tribeList = new ArrayList<>(); //tribe threafs
    private List<Tribe> crossed = new ArrayList<>();
    private List<Tribe> notcrossed = new ArrayList<>();

    private Tribe currentTribe;                        //currently selected tribe
    private Hopper hopper = null;                      //currently selected hopper
    private Hopper bestHopper = null;

    //Maintabs
    private JTabbedPane mainTab; // contains other tabs
    private Panel mainPanel;     // contains tribe info, buttons, slider, jcombobox
    private JScrollPane scroll;  // contains the tree
    private Panel statsPanel;    // contains the accumulated statistics 

    // Inner panels
    private Panel buttonsPanel;          // contains all buttons 
    private Panel bottomPanel;           // contains time and generation statistic
    private Panel table;                 // contains the current creatures table
    private GraphicsPanel graphicsPanel; // contains the opengl graphics
    private Renderer renderer;           // used to specify the drawn creature

    // All JLabels
    private JLabel tribeFitness;
    private JLabel bestFitness;          // Best fitness found from simulation
    private JLabel currentFitness;       // Current fitness from simulation
    private JLabel totalHillclimbs;      // total number of hill climbs
    private JLabel totalBreed;           // total number of crossover
    private JLabel time;                 // time since the start button was pressed
    private JLabel generations;          // number of hillclimb + crossover
    private JLabel generationsPerSecond; // generations / time
    private JLabel currentCreature;      // current creature label
    private JLabel allhills;
    private JLabel allcross;
    private JLabel allFails;
    private JLabel allhillFails;
    private JLabel overallfitness;
    private JLabel bestbestfitness;

    // Buttons!
    private Button pause;           // Pause/Start threads
    private Button animate;         // Pause/Start opengl graphics
    private Button nextGeneration;  // hillclimb and crossover once
    private Button writeFile;       // write current selected genome to file
    private Button loadFile;        // load saved genome 
    private Button writePopulation; // write the entire population to a file
    private Button loadPopulation;  // load saved population
    private Button getBest;         // get the current populations overachiever
    private Button reset;           // reset the current creatures simulation
    private Button worldChampion;

    //Misc Components
    private Slider slider;               //JSlider used to select hopper in the current population
    private JComboBox tribes;            //JComboBox, select different threads
    private JTree tree;                  //JTree, populated with current creatures phenotype
    private DefaultMutableTreeNode root; //The first brand of the tree

    // Variabes
    private float populationMergeTime = 0;
    private final float CROSS_OVER_WAIT = 60 * 1000;
    private float bestFitnessValue = 0f; //Best fitness value from simulation
    private int totalGenerations = 0;    //total hillclimb + total crossover
    private long startmilis;
    private long milistime = 0;
    private long waittime = 0;
    private boolean paused = true;       //Are the thread paused

    //Timers
    private Timer timer;    //Runs 1 frame/second
    private Timer keyTimer; //Run 60 frame/second

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
        try {
            changeHopper(new Hopper(currentTribe.getHopper(0)));
        } catch (IllegalArgumentException | GeneticsException ex) {
            Log.error(ex.toString(), currentTribe.getName());
        }

        startmilis = System.currentTimeMillis();
        milistime = 0;
    }

    /**
     * Listens for buttons, slider, timers, and JComboBox
     * Events:
     * keyTimer: advances the simulation, bestFitness and currentFitness values,
     * if a key was pressed the corresponding action will take place.
     *
     * timer: updated timer running crossover + hill climbing.
     *
     * actionCommand: Each button has an actionCommand associated with it.
     * Animation On : toggles opengl animation
     * Pause: toggles the tribes to life.
     * Write Genome: Saves genome to user selected file
     * Load Genome: Loads user selected file
     * Write Population: Writes population to user selected file
     * Load Population: Loads user selected population
     * Overachiever: polls the population for the best creature
     * Next Generation: Runs one instance of crossover and hill climbing
     * Change Tribe: Listens for changes to JComboBox
     * Reset: Resets the current hopper simulation
     *
     * @param e ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(keyTimer)) {
            if (paused) {
                if (hopper != null && graphicsPanel.animating()) {

                    float hf = hopper.getPhenotype().advanceSimulation();
                    bestFitnessValue = hf > bestFitnessValue ? hf : bestFitnessValue;

                    String b = String.format("%.5f", bestFitnessValue);
                    String c = String.format("%.5f", hf);
                    bestFitness.setText("Best Fitness: " + b);
                    currentFitness.setText("Current Fitness: " + c);
                }

                checkKeys();
            }
            return;
        }

        if (e.getSource().equals(timer)) {
            if (!paused) {
                time.setText(" Time: " + time());
                totalGenerations = currentTribe.getGenerations();
                generations.setText("Total Generations: " + totalGenerations);

                float div = milistime / 1000f;

                if (div != 0) {
                    String f = String.format("%.5f", (float) (totalGenerations / div));
                    generationsPerSecond.setText("Generations/Second: " + f);
                }

            }
            else {
                waittime = milistime;
                startmilis = System.currentTimeMillis();
            }

            if (paused) {
                tribeFitness.setText("Tribe Fitness: " + String.format("%.5f", currentTribe.getFitness()));

                float best = 0;
                float f = 0;
                int i = 0;
                long c = 0;
                long h = 0;
                long cf = 0;
                long hf = 0;

                for (Tribe t : tribeList) {
                    Hopper overachiever = t.getOverachiever();
                    if (overachiever != null) {
                        float foo = overachiever.getFitness();

                        if (bestHopper == null) {
                            bestHopper = overachiever;
                        }

                        if (best < foo) {
                            bestHopper = overachiever;
                            best = foo;
                        }
                    }
                    f += t.getFitness();

                    c += t.getcross();
                    h += t.gethills();

                    cf += t.getFails();
                    hf += t.gethillFails();
                    i++;
                }

                allhills.setText("Total Hillclimbs: " + h);
                allcross.setText("Total Crossover: " + c);
                allhillFails.setText("Total Hillclimb Fails: " + hf);
                allFails.setText("Total Crossover Fails: " + cf);
                overallfitness.setText("Overall Fitness: " + f / i);
                bestbestfitness.setText("Best Fitness: " + best);
            }
            return;
        }

        switch (e.getActionCommand()) {

            // Animate Crature
            case "Animate Off":
                if (!graphicsPanel.animating()) {
                    animate.setText("Animator Off");
                    graphicsPanel.startAnimator();
                }
                else {
                    animate.setText("Animator On");
                    graphicsPanel.stopAnimator();
                }
                break;

            //Pause all threads
            case "Pause":
                paused = !paused;
                
                if (paused) {
                    pause.setText("Start");
                    for(Tribe t: tribeList){
                        t.interrupt();
                    }
                }
                else {
                    pause.setText("Pause");
                    for(Tribe t: tribeList){
                        t.interrupt();
                    }
                }

                if (graphicsPanel.animating()) {
                    animate.setText("Animator On");
                    graphicsPanel.stopAnimator();
                }

                worldChampion.setEnabled(paused);
                getBest.setEnabled(paused);
                reset.setEnabled(paused);
                animate.setEnabled(paused);
                scroll.setEnabled(paused);
                statsPanel.setEnabled(paused);
                loadPopulation.setEnabled(paused);
                loadFile.setEnabled(paused);
                writePopulation.setEnabled(paused);
                writeFile.setEnabled(paused);
                tribes.setEnabled(paused);
                slider.setEnabled(paused);
                nextGeneration.setEnabled(paused);
                break;

            // Write the currently selected genome to use selected file.
            case "Write Genome":
                Log.hopper(this, hopper, currentTribe.getName());
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
                changeHopper(currentTribe.getOverachiever());
                slider.setValue(currentTribe.getSize() - 1);
                break;

            // Step Next Generation
            case "Next Generation":
                for (Tribe t : tribeList) {
                    t.nextGeneration();
                }

                slider.setMaximum(currentTribe.getSize() - 1);
                currentTribe = tribeList.get(tribes.getSelectedIndex());
                totalGenerations = currentTribe.getGenerations();
                generations.setText("Total Generations: " + totalGenerations);

                try {
                    changeHopper(new Hopper(currentTribe.getHopper(0)));
                } catch (GeneticsException ex) {
                    Log.error(ex.toString(), currentTribe.getName());
                }
                break;

            case "Change Tribe":
                currentTribe = tribeList.get(tribes.getSelectedIndex());
                totalGenerations = currentTribe.getGenerations();
                generations.setText("Total Generations: " + totalGenerations);
                if (hopper != null) {
                    try {
                        changeHopper(new Hopper(currentTribe.getHopper(0)));
                        slider.setMaximum(currentTribe.getSize() - 1);
                        slider.setMajorTickSpacing(slider.getMaximum() / 5);
                        slider.setValue(0);
                    } catch (GeneticsException ex) {
                        Log.error(ex.toString(), currentTribe.getName());
                    }
                }
                break;
            case "Reset":
                hopper.getPhenotype().resetSimulation();
                break;
            case "Champion":
                if (bestHopper != null) {
                    changeHopper(bestHopper);
                }
                break;
            default:
                Log.popup(this, "Something went wrong with buttons.");
                break;
        }

    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource().equals(mainTab)) {
            if (mainTab.getSelectedIndex() == 1) {
                populateTree();
            }

        }

        if (e.getSource().equals(slider)) {
            try {
                slider.setMaximum(currentTribe.getSize() - 1);
                changeHopper(new Hopper(currentTribe.getHopper(slider.getValue())));
            } catch (GeneticsException | IndexOutOfBoundsException | NullPointerException ex) {
                Log.error(ex.toString(), currentTribe.getName());
            }
        }
    }

    /**
     * @return string containing formated time h:m:s
     */
    private String time() {
        milistime = System.currentTimeMillis() - startmilis + waittime;
        populationMergeTime = milistime;

        long elapsedSecs = milistime / 1000;
        long elapsedMins = elapsedSecs / 60;
        long hours = elapsedMins / 60;
        long mins = elapsedMins % 60;
        long secs = elapsedSecs % 60;

        Tribe t = tribeList.get(Helper.RANDOM.nextInt(tribeList.size() - 1));
        Hopper hooper = t.randomHopper();
        if (hooper != null) {
            tribeList.get(Helper.RANDOM.nextInt(tribeList.size() - 1)).addHopper(hooper);
        }

        String h = hours > 9 ? hours + "" : ("0" + hours);
        String m = mins > 9 ? mins + "" : ("0" + mins);
        String s = secs > 9 ? secs + "" : ("0" + secs);

        return h + ":" + m + ":" + s;
    }

    /**
     * Populate the tree with the current creatures phenotype
     * currently a little ugly.
     */
    private void populateTree() {
        DefaultMutableTreeNode block = null;
        String[] str;
        str = hopper.toString().split("\n");

        if (str == null) {
            return;
        }

        root.removeAllChildren();
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

        model.reload();

        for (int i = 5; i < str.length; i++) {
            if (str[i].contains("</genotype>") || str[i].equals("}")) {
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
        table.setPreferredSize(new Dimension(WIDTH + 450, root.getChildCount() * root.getLeafCount() * 5));
        table.setBorder(BorderFactory.createTitledBorder(null,
                                                         "ID Number: " + Long.toString(hopper.getSerial()),
                                                         TitledBorder.LEFT,
                                                         TitledBorder.TOP,
                                                         null,
                                                         FONTCOLOR));
    }

    /**
     * Read Genotype from user selected file
     */
    private void loadGenotype() {
        changeHopper(Log.loadHopper(this, hopper));
//        hopper.getPhenotype().

        if (hopper != null) {
            currentTribe.addHopper(hopper);

            slider.setMaximum(currentTribe.getSize() - 1);
            slider.setValue(currentTribe.getSize() - 1);

        }
    }

    /**
     * load a population from user selected file.
     */
    private void loadPopulation() {

    }

    /**
     * Initializes and setup the GUI
     */
    private void init() {

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Log.error(ex.toString());
        }

        // Setup JFrame
        size = new Dimension(WIDTH + 8, HEIGHT + 50);
        setSize(size);
        getContentPane().setBackground(new Color(15, 15, 15));
        setMinimumSize(size);
        setMaximumSize(size);
        setPreferredSize(size);
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
        size = new Dimension(WIDTH, HEIGHT);
        mainTab.setSize(size);
        mainTab.setMinimumSize(size);
        mainTab.setMaximumSize(size);
        //////////////////////////////////////////////////

        // Initialize JPanels
        mainPanel = new Panel(size);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        // Stats contains number of sucessful breads/hill climbs, over achiever for population, heighest fitness
        statsPanel = new Panel(size);
        statsPanel.setBackground(BACKGROUND_COLOR);
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));

        bestbestfitness = new JLabel("Best Fitness: 0");
        bestbestfitness.setForeground(FONTCOLOR);

        statsPanel.add(bestbestfitness);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        size = new Dimension(WIDTH - graphicsPanel.getWidth(), HEIGHT);
        buttonsPanel = new Panel(size);
        buttonsPanel.setBackground(BACKGROUND_COLOR);

        size = new Dimension(WIDTH, 20);
        bottomPanel = new Panel(size);
        bottomPanel.setBackground(BACKGROUND_COLOR);

        time = new JLabel(" Time: 00:00:00");
        time.setForeground(FONTCOLOR);

        // labels /////////////////////////////////////////////////////////
        generationsPerSecond = new JLabel("Generations/Second: 0.0000");
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
        size = new Dimension(WIDTH - graphicsPanel.getWidth() - 20, 20);
        pause = new Button(size, "Pause");
        pause.setText("Start");

        animate = new Button(size, "Animate Off");

        nextGeneration = new Button(size, "Next Generation");

        writeFile = new Button(size, "Write Genome");

        loadFile = new Button(size, "Load Genome");

        writePopulation = new Button(size, "Write Population");

        loadPopulation = new Button(size, "Load Population");

        getBest = new Button(size, "Overachiever");

        reset = new Button(size, "Reset");

        worldChampion = new Button(size, "Champion");
        //////////////////////////////////////////////////////////

        statsPanel.add(getBest);

        // Initialize tribes JComboBox/////////////////////////////
        tribes = new JComboBox(nameList.toArray());
        tribes.setSize(size);
        tribes.setPreferredSize(size);
        tribes.setMaximumSize(size);
        tribes.setMinimumSize(size);
        tribes.setActionCommand("Change Tribe");
        ///////////////////////////////////////////////////////////

        // Make slider////////////////////////////////////////////
        slider = new Slider("Creature", 0, currentTribe.getSize(), 1);
        slider.addChangeListener(this);
        ////////////////////////////////////////////////////////

        // Current Creature Label/////////////////////////////////
        currentCreature = new JLabel("   Current Creature   ");
        currentCreature.setForeground(FONTCOLOR);

        //////////////////////////////////////////////////////////
        // Setup default table/////////////////////////////////////
        table = new Panel(new Dimension(WIDTH, HEIGHT));
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
        reset.addActionListener(this);
        tribes.addActionListener(this);
        worldChampion.addActionListener(this);
        ///////////////////////////////////////////////
        tribeFitness = new JLabel("Tribe Fitness: 0.00000");
        tribeFitness.setForeground(FONTCOLOR);

        currentFitness = new JLabel("Creature Fitness: 0.00000");
        currentFitness.setForeground(FONTCOLOR);

        bestFitness = new JLabel("Best Fitness: 0.00000");
        bestFitness.setForeground(FONTCOLOR);

        allFails = new JLabel("Dead Children: 0");
        allFails.setForeground(FONTCOLOR);

        allhillFails = new JLabel("Failed Climbs: 0");
        allhillFails.setForeground(FONTCOLOR);

        allcross = new JLabel("Children Born: 0");
        allcross.setForeground(FONTCOLOR);

        allhills = new JLabel("All Climbs: 0");
        allhills.setForeground(FONTCOLOR);

        overallfitness = new JLabel("Overall Fitness: 0");
        overallfitness.setForeground(FONTCOLOR);

        
        statsPanel.add(allcross);
        statsPanel.add(allhills);
        statsPanel.add(overallfitness);
        statsPanel.add(allFails);
        statsPanel.add(allhillFails);

        // Add things to the buttons panel
        buttonsPanel.add(tribeFitness);
        buttonsPanel.add(tribes);
        buttonsPanel.add(pause);
        buttonsPanel.add(nextGeneration);
        buttonsPanel.add(animate);
//        buttonsPanel.add(currentCreature);
        buttonsPanel.add(slider);
        buttonsPanel.add(bestFitness);
        buttonsPanel.add(currentFitness);
        buttonsPanel.add(reset);
        buttonsPanel.add(getBest);
        buttonsPanel.add(worldChampion);

        buttonsPanel.add(writeFile);
        buttonsPanel.add(loadFile);
        buttonsPanel.add(writePopulation);
        buttonsPanel.add(loadPopulation);
        //////////////////////////////////////////////

        // Setup the upper panel
        mainPanel.add(graphicsPanel);
        mainPanel.add(buttonsPanel);
        //////////////////////////////////////////////

        // add the main panel to "this"
        mainTab.addChangeListener(this);
        mainTab.addTab("Main", mainPanel);
        mainTab.addTab("", scroll);
        mainTab.addTab("Stats", statsPanel);

        add(mainTab, BorderLayout.CENTER);
        add(bottomPanel);

        // Initialize the next generations button to off.
        nextGeneration.setEnabled(paused);

        // Pack because why not?
        pack();

        timer = new Timer(1000, this);
        keyTimer = new Timer(1000 / 30, this);

        for (Tribe t : tribeList) {
            notcrossed.add(t);
        }

        graphicsPanel.requestFocus();
        KeyBinds keyBinds = new KeyBinds((JComponent) getContentPane(), controls);
        keyBinds.addBinding(KeyEvent.VK_T, "Texture");
        controls.addBinding("Texture");

        timer.start();
        keyTimer.start();

    }

    /**
     * respond the zoom
     * up or w zooms in
     * down or s zooms out
     * space resets the timer for the simulation
     */
    private void checkKeys() {
        if (controlMap.get("up")) {
            renderer.zoomIn();
        }
        else if (controlMap.get("down")) {
            renderer.zoomOut();
        }
        else if (controlMap.get("left")) {
            renderer.rotateLeft();
        }
        else if (controlMap.get("right")) {
            renderer.rotateRight();
        }
        else if (controlMap.get("space")) {
            hopper.getPhenotype().resetSimulation();
        }
        else if (controlMap.get("texture")) {
            controlMap.put("texture", false);
            renderer.toggleTextures();
        }
        else if (controlMap.get("left") && graphicsPanel.animating()) {
            renderer.rotateLeft();
        }
        else if (controlMap.get("right") && graphicsPanel.animating()) {
            renderer.rotateRight();
        }
    }

    /**
     * Helper class to change the current hopper.
     *
     * @param hopper the hopper to change to.
     */
    private void changeHopper(Hopper hopper) {
        bestFitnessValue = 0f;
        if (hopper != null) {
            this.hopper = hopper;
            renderer.setHopper(hopper);
            mainTab.setTitleAt(1, hopper.getName() + " Age: " + hopper.getAge());
        }
    }
}
