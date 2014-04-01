//
// @author Marcos Lemus
// CS351
// Creature Creation Project
//
package creature.geeksquad.graphics;

import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;

/**
 *
 * @author Marcos
 */
public class EventListener implements GLEventListener{
    private final List<Rectangle3D> cubes = new ArrayList<>();
    private final GLU glu = new GLU();
    
    /**
     *
     * @param drawable
     */
    @Override
    public void display(GLAutoDrawable drawable) {
        update();
        render(drawable);
    }
    
    /**
     *
     * @param drawable
     */
    @Override
    public void dispose(GLAutoDrawable drawable) {
    }
    
    /**
     *
     * @param drawable
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2(); //who does this...
        gl.glEnable(GL2.GL_DEPTH_TEST);
        
        gl.glDepthFunc(GL2.GL_LEQUAL);
       gl.glShadeModel(GL2.GL_SMOOTH);
        // Fill a list with cubes
        for (int i = 0; i < 100; i++) {
//            cubes.add(new Rectangle3D());
        }

        // initialize lighting
        doLighting(gl);
    }
    
    /**
     *
     * @param drawable
     * @param x
     * @param y
     * @param width
     * @param height
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height); //update viewport
        gl.glMatrixMode(GL2.GL_PROJECTION);        
        gl.glLoadIdentity();
        
        glu.gluPerspective(45.0, (float) width / height, .1, 100); // set perspective

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    private void update() {
        
    }
    
    private void render(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        
// clear the screen
        gl.glClear(GL.GL_COLOR_BUFFER_BIT
                | GL.GL_DEPTH_BUFFER_BIT);

        // sets everything to default
        gl.glLoadIdentity();
        
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        for (Rectangle3D c : cubes) {
            c.update();
            c.draw(gl);
        }
    }
    
    private void doLighting(GL2 gl) {
        float[] lightPos = new float[4]; // The lights position
        lightPos[0] = 0;
        lightPos[1] = 0;
        lightPos[2] = -5000;
        lightPos[3] = 1;
        gl.glEnable(GLLightingFunc.GL_LIGHTING);
        gl.glEnable(GLLightingFunc.GL_LIGHT0);

        // Different lights colors
        float[] noAmbient = {0.4f, 0.4f, 0.4f, 1f};
        float[] spec = {0f, 0f, 0f, 1f};
        float[] diffuse = {0.5f, 0.5f, 0.5f, 1f};

        // Setting up the lights
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, noAmbient, 0);
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR, spec, 0);
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, lightPos, 0);
    }
}
