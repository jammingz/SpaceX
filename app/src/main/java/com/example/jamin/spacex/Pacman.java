package com.example.jamin.spacex;

import android.graphics.Point;
import android.opengl.GLES20;
import android.util.Log;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by jamin on 1/5/15.
 */
public class Pacman {

    // This matrix member variable provides a hook to manipulate
    // the coordinates of the objects that use this vertex shader
    // The matrix must be included as a modifier of gl_Position.
    // Note that the uMVPMatrix factor *must be first* in order
    // for the matrix multiplication product to be correct.
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private FloatBuffer vertexBuffer;
    // private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int minVertex;
    private int maxVertex;
    private float radius; // Full screen radius = 1.0f
    private boolean isAnimating = false;
    private int mLastIteration;
    private int mDirection; // Direction which Pacman is facing. {0:left, 1:up, 2:right, 3:down}

    // **FIX LATER!**
    private float details[] = new float[6];
                                /*	{ X: { velocity_x, position_x},		details[0], details[1]
     							*	  Y: { velocity_y, position_y},		details[2], details[3]
     							*	  Z: { velocity_z, position_z},		details[4], details[5]
     							*	}
     							*
     							*/




    // number of coordinates per vertex in this array
    private final int COORDS_PER_VERTEX = 3;
    private float circleCoords[];
    private float backupCoords[];
    //private final short drawOrder[] = { 0,2,1,0,3,2,0,4,3,0,5,4,0,6,5,0,7,6,0,8,7,0,1,8 }; // order to draw vertices

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private float color[] = new float[4];//{ 0.2f, 0.709803922f, 0.898039216f, 1.0f };

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Pacman() {
        // Constructing the coordinates for the circle
        this(40,1.0f,1.0f,1.0f,1.0f,1.0f); // Default vertex to 40 if none is provided
    }

    public Pacman(int vertexCount, float radius) {
        this(vertexCount,radius,1.0f,1.0f,1.0f,1.0f);
    }

    public Pacman(int vertexCount, float radius, float colorR, float colorB, float colorG, float colorA) {
        // Constructing the coordinates for the circle
        if(vertexCount > 0) {
            maxVertex = vertexCount;
        } else {
            maxVertex = 40;
        }

        minVertex = (int) Math.round(0.85 * maxVertex);

        if (radius <= 0 || radius > 1) {
            this.radius = 1;
        } else {
            this.radius = radius;
        }

        color[0] = colorR;
        color[1] = colorB;
        color[2] = colorG;
        color[3] = colorA;

        details[0] = 0.0f;
        details[1] = 0.0f;
        details[2] = 0.0f;
        details[3] = 0.0f;
        details[4] = 0.0f;
        details[5] = 0.0f;
        details[0] = 0.0f;

        mDirection = 0; // Initially face left
        mLastIteration = 0;

        circleCoords = new float[(maxVertex+1)*3];
        circleCoords[0] = 0;
        circleCoords[1] = 0;
        circleCoords[2] = 0;
        backupCoords = new float[(maxVertex+1)*3];
        backupCoords[0] = 0;
        backupCoords[1] = 0;
        backupCoords[2] = 0;
        for(int i=3;i<(maxVertex+1)*3;i+=3){
            double rad = (i*360/(maxVertex*3))*(3.14159/180);
            circleCoords[i] = (float)Math.cos(rad) * radius;
            circleCoords[i+1] = (float) Math.sin(rad) * radius;
            circleCoords[i+2] = 0;
            backupCoords[i] = circleCoords[i];
            backupCoords[i+1] = circleCoords[i+1];
            backupCoords[i+2] = 0;
        }



        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                circleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(circleCoords);
        vertexBuffer.position(0);
/*
        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
*/
        // prepare shaders and OpenGL program
        int vertexShader = GLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = GLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the circle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, maxVertex);

        /*
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
                */

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }


    // Getter Functions
    public float getPositionX() {
        return details[1];
    }

    public float getPositionY() {
        return details[3];
    }

    public float getPositionZ() {
        return details[5];
    }

    public float getVelocityX() {
        return details[0];
    }

    public float getVelocityY() {
        return details[2];
    }

    public float getVelocityZ() {
        return details[4];
    }

    // Setter Functions
    public void setPositionX(float value) {
        details[1] = value;
    }

    public void setPositionY(float value) {
        details[3] = value;
    }

    public void setPositionZ(float value) {
        details[5] = value;
    }

    public void setVelictyX(float value) {
        details[0] = value;
    }

    public void setVelictyY(float value) {
        details[2] = value;
    }

    public void setVelictyZ(float value) {
        details[4] = value;
    }

    // IMPLEMENT LATER!
    public void setDirection(int value) {
       // DO SOMETHING LATER
    }

    public int getDirection() {
        return 0;
    }

    public void resetPosition() {
    }

    public void nextFrame() {
        mLastIteration += 2;
        int startIndex = calcStartVertex(minVertex,maxVertex,mLastIteration);


        for (int i = minVertex * 3; i < (startIndex) * 3; i += 3) {
            circleCoords[i] = backupCoords[i];
            circleCoords[i+1] = backupCoords[i+1];
        }

        for (int i = (startIndex +1 ) * 3; i < (maxVertex + 1) * 3; i += 3) {
            circleCoords[i] = 0;
            circleCoords[i + 1] = 0;
        }
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                circleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(circleCoords);
        vertexBuffer.position(0);
    }

    public boolean startAnimation() {
        return true;
    }

    public void stopAnimation() {
        isAnimating = false;
    }

    public int calcStartVertex(int minVertex, int maxVertex, int iteration) {
        int mod = maxVertex - minVertex;
        // Handles the special case where iteration is at the maximum but because of modulus, will return 0 instead of real maximum.

        // If modulus of iteration is 0 and if this is on the even cycle.
        if ((iteration % mod == 0) && (((iteration / mod) % 2) == 0)) {
            // once we find the maximum iteration, we will just calculate the previous iteration and add 1 to the result
            return calcStartVertex(minVertex,maxVertex,iteration-1) + 1;
        }


        return minVertex + PacMath.modulus((-iteration *  (int) Math.pow((-1),((int) Math.floor(iteration/mod)))),mod);
        //Log.i("Pacman index","iteration=" + String.valueOf(iteration) + " index=" + String.valueOf(temp) + " mod=" + String.valueOf(mod));

    }
    private int modulus(int x, int y)
    {
        int result = x % y;
        if (result < 0)
            result += y;
        return result;
    }
}
