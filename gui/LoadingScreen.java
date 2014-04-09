
package creature.geeksquad.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 *
 * @author Marcos
 */
public class LoadingScreen extends JFrame{
    private final Button configButton;
    private final JLabel loadingLabel;
    private final JProgressBar progressBar;
    
    private int progress = 0;
    
    private Dimension size;
    
    public LoadingScreen(int WIDTH, int HEIGHT){
        super("Loading...");
        size = new Dimension(WIDTH, HEIGHT);
        setSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setPreferredSize(size);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        setBackground(new Color(55, 55, 55));
        
        configButton = new Button(new Dimension(200, 20), "Configure");
        loadingLabel = new JLabel("Loading Please Wait");
        loadingLabel.setForeground(new Color(205, 205, 205));
        progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 8);
        
        add(loadingLabel, BorderLayout.NORTH);
        add(progressBar, BorderLayout.CENTER);
        add(configButton, BorderLayout.SOUTH);
        
        pack();
    }
    
    public void update(){
        progressBar.setValue(progress);
        progress++;
        if(progress >= Log.NUMB_CORES){
            dispose();
        }
    }
}
