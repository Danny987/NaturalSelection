/**
 * @author Marcos Lemus
 * CS351
 */
package creature.geeksquad.gui;

import com.jogamp.opengl.util.FPSAnimator;
import java.awt.Color;
import java.awt.Dimension;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.awt.GLCanvas;

/**
 *
 * @author Marcos
 */
public class GraphicsPanel extends GLCanvas {

    private final FPSAnimator animator;
    private Renderer renderer;

    /**
     * GraphixsPanel creates a GLCanvas used for opengl
     * 
     * @param width
     * @param height
     * @param caps 
     */
    public GraphicsPanel(int width, int height, GLCapabilities caps) {
        super(caps);
        Dimension size = new Dimension(width, height);
        setSize(size);
        setPreferredSize(new Dimension(size));
        setMinimumSize(size);
        setMaximumSize(size);
        setBackground(Color.BLACK);
        
        renderer = new Renderer();

        addGLEventListener(renderer);

        animator = new FPSAnimator(this, 60);
        animator.start();
        animator.pause();

    }

    /**
     *
     * @return
     */
    public boolean animating(){return !animator.isPaused();}

    /**
     *
     */
    public void startAnimator(){animator.resume();}

    /**
     *
     */
    public void stopAnimator(){animator.pause();}

    /**
     *
     */
    public void kill(){animator.stop();}
    
    public Renderer getRenderer(){ return renderer;}
}
