//
// @author Marcos Lemus
// CS351
// Creature Creation Project
//
package creature.geeksquad.gui;

import creature.phenotype.Vector3;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 *
 * @author Marcos
 */
public class Rectangle3D {

    private float length, height, width;
    private Vector3 center;
    private Vector3 up;
    private Vector3 forward;
    
    //Constructor randomly generates all aspects of the cube
    /**
     *
     * @param length
     * @param height
     * @param width
     * @param center
     * @param up
     * @param forward
     */
    public Rectangle3D(float length,
                       float height,
                       float width,
                       Vector3 center,
                       Vector3 up,
                       Vector3 forward) {
        
        this.length = length;
        this.height = height; 
        this.width = width;
        
        this.center = center;
        this.up = up;
        this.forward = forward;
    }

    /**
     *
     */
    public void update() {
    }

    // Draw the cube using the given GL2 object
    /**
     *
     * @param gl
     */
    public void draw(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(center.x, center.y, center.z);            //move the cube
//        gl.glRotatef(theta, x, y, z);        //Rotate cube

        //Set material and shininess!
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, new float[]{0, 1, 0, 1}, 0);
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, new float[]{0, 1, 0, 1}, 0);
        gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, .5f);

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
        gl.glNormal3f(-1, 0, 0);
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
