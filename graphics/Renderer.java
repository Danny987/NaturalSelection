package creature.geeksquad.graphics;

import creature.geeksquad.genetics.Genotype;
import creature.geeksquad.genetics.Hopper;
import creature.phenotype.Block;
import creature.phenotype.Creature;
import creature.phenotype.Vector3;
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
public class Renderer implements GLEventListener {

    private Hopper hopper;
    private Genotype genotype;
    private Creature phenotype;
    private Block[] body;
    private final GLU glu = new GLU();
    
    private float testTheta = 0;

    /**
     * Set the current creature to be drawn.
     *
     * @param hopper
     */
    public void setHopper(Hopper hopper) {
        this.hopper = hopper;
        this.genotype = hopper.getGenotype();
        this.phenotype = hopper.getPhenotype();
        this.body = genotype.getBody();
    }

    /**
     * draw the current creature.
     */
    private void render(GLAutoDrawable drawable) {
        float length;
        float height;
        float width;
        Vector3 center;
        Vector3 up;
        Vector3 forward;

        GL2 gl = drawable.getGL().getGL2();

        // clear the screen
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // sets everything to default
        glu.gluLookAt(0, 0, 0, 0, -testTheta, 0, 0, 0, 0);
        gl.glLoadIdentity();

        // Move the hopper away from the camera
       
        
        gl.glTranslatef(0, 0, -50);
        
        // Drawing stuff ////////////////////////////////////////////////////
        gl.glPushMatrix();
        
        //used to see the creature from different angles
        gl.glRotatef(testTheta, 0, 1, 0);
        testTheta += .5;
        
        
        // It's ALIVE!!!
        hopper.getPhenotype().advanceSimulation();
  
        // Draw the body block by block.
        for (int i = 0; i < body.length; i++) {
            length = body[i].getLength();
            height = body[i].getHeight();
            width = body[i].getWidth();
            center = phenotype.getBlockCenter(i);
            up = phenotype.getBlockUpVector(i);
            forward = phenotype.getBlockForwardVector(i);

            // Draw the current block
            drawBlock(gl, length, height, width, center, up, forward);
        }
        
        setColor(gl, 0, 1, 0);
        drawBlock(gl, 100, .0001f, 100, Vector3.ZERO, Vector3.UP, Vector3.FORWARD);
  
        gl.glPopMatrix();
        /////////////////////////////////////////////////////////////////////////
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        render(drawable);
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2(); //who does this...
        gl.glEnable(GL2.GL_DEPTH_TEST);

        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glShadeModel(GL2.GL_SMOOTH);

        // initialize lighting
        doLighting(gl);
    }

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

    /**
     * Initialize everything for lighting
     * @param gl
     */
    private void doLighting(GL2 gl) {
        float[] lightPos = new float[4]; // The lights position
        lightPos[0] = 0;                          // x position
        lightPos[1] = 0;                          // y position
        lightPos[2] = -5000;                      // z position
        lightPos[3] = 1;
        
        // enable lighting
        gl.glEnable(GLLightingFunc.GL_LIGHTING);
        gl.glEnable(GLLightingFunc.GL_LIGHT0);

        // Different lights colors
        float[] noAmbient = {0.25f, 0.25f, 0.25f, 1f};
        float[] spec = {0.5f, 0.5f, 0.5f, 1f};
        float[] diffuse = {0.1f, 0.1f, 0.1f, 1f};

        // Setting up the lights
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, noAmbient, 0);
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR, spec, 0);
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, lightPos, 0);
    }
    
    /**
     * draws whatever block you give it
     * @unfinished still needs need to into consideration the vectors
     * 
     * @param gl
     * @param length
     * @param height
     * @param width
     * @param center
     * @param up
     * @param forward 
     */
    public void drawBlock(GL2 gl, float length, float height, float width, Vector3 center, Vector3 up, Vector3 forward) {
        gl.glPushMatrix();
        
        // move to the center of the block
        gl.glTranslatef(center.x , center.y, center.z);
        
        float[] rotationMatrix = new float[16];
        Vector3.vectorsToRotationMatrix(rotationMatrix, forward, up);
        gl.glMultMatrixf(rotationMatrix, 0);
        
        // scale to the size of the given block
        gl.glScalef(length, height, width);
        
//        System.out.println("Up Vector = " + up + "Forward Vector = " + forward);
        setColor(gl, center.x, center.y, center.z);

        // Draw the vertecies 
        gl.glBegin(GL.GL_TRIANGLES);

        //Front
        gl.glNormal3f(0, 0, 1);
        gl.glVertex3f(1, 1, -1);
        gl.glVertex3f(-1, 1, -1);
        gl.glVertex3f(-1, -1, -1);
        gl.glVertex3f(-1, -1, -1);
        gl.glVertex3f(1, -1, -1);
        gl.glVertex3f(1, 1, -1);

        //Back
        gl.glNormal3f(0, 0, -1);
        gl.glVertex3f(1, 1, 1);
        gl.glVertex3f(-1, 1, 1);
        gl.glVertex3f(-1, -1, 1);
        gl.glVertex3f(-1, -1, 1);
        gl.glVertex3f(1, -1, 1);
        gl.glVertex3f(1, 1, 1);

        //Left
        gl.glNormal3f(1, 0, 0);
        gl.glVertex3f(-1, -1, -1);
        gl.glVertex3f(-1, 1, -1);
        gl.glVertex3f(-1, -1, 1);
        gl.glVertex3f(-1, 1, -1);
        gl.glVertex3f(-1, -1, 1);
        gl.glVertex3f(-1, 1, 1);

        //Right
        gl.glNormal3f(-1, 0, 0);
        gl.glVertex3f(1, -1, -1);
        gl.glVertex3f(1, 1, -1);
        gl.glVertex3f(1, -1, 1);
        gl.glVertex3f(1, 1, -1);
        gl.glVertex3f(1, -1, 1);
        gl.glVertex3f(1, 1, 1);

        //Top
        gl.glNormal3f(0, - 1, 0);
        gl.glVertex3f(-1, 1, 1);
        gl.glVertex3f(1, 1, 1);
        gl.glVertex3f(1, 1, -1);
        gl.glVertex3f(-1, 1, 1);
        gl.glVertex3f(1, 1, -1);
        gl.glVertex3f(-1, 1, -1);

        //Bottom
        gl.glNormal3f(0, 1, 0);
        gl.glVertex3f(-1, -1, 1);
        gl.glVertex3f(1, -1, 1);
        gl.glVertex3f(1, -1, -1);
        gl.glVertex3f(-1, -1, 1);
        gl.glVertex3f(1, -1, -1);
        gl.glVertex3f(-1, -1, -1);
        gl.glEnd();
        gl.glPopMatrix();
    }
    
    private void setColor(GL2 gl, float r, float g, float b){
        //Set material and shininess!
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, new float[]{r, g, b, 1}, 0);
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, new float[]{r, g, b, 1}, 0);
        gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, .5f);

    }
    
    private void drawFloor(GL2 gl){
        gl.glNormal3f(0, 1, 0);
        gl.glVertex3f(-1, -1, 1);
        gl.glVertex3f(1, -1, 1);
        gl.glVertex3f(1, -1, -1);
        gl.glVertex3f(-1, -1, 1);
        gl.glVertex3f(1, -1, -1);
        gl.glVertex3f(-1, -1, -1);  
    }

}
