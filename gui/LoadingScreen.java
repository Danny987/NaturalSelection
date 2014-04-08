
package creature.geeksquad.gui;

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
    private Button configButton;
    private JLabel loadingLabel;
    private JProgressBar progressBar;
    
    private int progress = 0;
    
    private Dimension size;
    
    public LoadingScreen(int WIDTH, int HEIGHT){
        size = new Dimension(WIDTH, HEIGHT);
        setSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(new Color(55, 0, 55));
        
        configButton = new Button(200, 50, "Configure");
        loadingLabel = new JLabel("Loading Please Wait");
        loadingLabel.setForeground(new Color(205, 205, 205));
        progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 8);
        
        add(loadingLabel);
        add(configButton);
        add(progressBar);
        
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
