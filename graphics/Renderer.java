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
        gl.glLoadIdentity();

        gl.glTranslatef(0, 0, -50);
        
        gl.glPushMatrix();
        gl.glRotatef(testTheta, 0, 1, 0);
        testTheta += .5;
        for (int i = 0; i < body.length; i++) {
            length = body[i].getLength();
            height = body[i].getHeight();
            width = body[i].getWidth();
            center = phenotype.getBlockCenter(i);
            up = phenotype.getBlockUpVector(i);
            forward = phenotype.getBlockForwardVector(i);

            drawBlock(gl, length, height, width, center, up, forward);
        }
        gl.glPopMatrix();
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
     *
     * @param gl
     */
    private void doLighting(GL2 gl) {
        float[] lightPos = new float[4]; // The lights position
        lightPos[0] = 0;
        lightPos[1] = 0;
        lightPos[2] = -5000;
        lightPos[3] = 1;
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
        float angle = 0;
        
        gl.glPushMatrix();
        gl.glTranslatef(center.x , center.y, center.z);
        gl.glScalef(length, height, width);
        
//        angle = (float)Math.toDegrees(Vector3.getAngleBetweenVectors(up, Vector3.UP));
//        gl.glRotatef(angle, up.x, up.y, up.z);
        
//        angle = (float)Math.toDegrees(Vector3.getAngleBetweenVectors(forward, Vector3.FORWARD));
//        gl.glRotatef(angle, forward.x, 0, forward.z);
        
        System.out.println(center);
        
        //Set material and shininess!
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, new float[]{center.x, center.y, center.z, 1}, 0);
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, new float[]{center.x, center.y, center.z, 1}, 0);
        gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, .5f);

        // Draw the vertecies 
        gl.glBegin(GL.GL_TRIANGLES);

//        gl.glColor3f(center.x, center.y, center.z);
        
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

}
