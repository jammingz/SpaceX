package com.example.jamin.spacex;

/**
 * Created by jamin on 2/1/15.
 */

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by jamin on 1/21/15.
 */
public class Food {

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

    protected static final int CONSUMABLE = 0;
    protected static final int CHERRY = 1;
    private static final float FRAME_LENGTH = 0.015f;
    private int mFoodType;


    private FloatBuffer vertexBuffer;
    // private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private Frame mFrame;

    // number of coordinates per vertex in this array
    private final int COORDS_PER_VERTEX = 3;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private float color[] = new float[4];//{ 0.2f, 0.709803922f, 0.898039216f, 1.0f };
    private int numOfVertex;

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */


    public Food(float originX, float originY, int foodType){
        numOfVertex = 8;
        mFoodType = foodType;

        // Creating frame for food
        mFrame = new Frame(originX,originY,FRAME_LENGTH,FRAME_LENGTH);
        float circleCoords[] = new float[(numOfVertex + 1) * 3];

        /**
         * Settings for consumables
         */
        if (foodType == CONSUMABLE) {
            color[0] = 1.0f;
            color[1] = 1.0f;
            color[2] = 0.796875f;
            color[3] = 1.0f;


            float radius = 0.02f;
            for (int i = 0; i < (numOfVertex + 1) * 3; i += 3) {
                double rad = (i * 360 / (numOfVertex * 3)) * (3.14159 / 180);
                circleCoords[i] = (float) Math.cos(rad) * radius + originX; // originX is the offset to move the circle from center to desired X coordinate
                circleCoords[i + 1] = (float) Math.sin(rad) * radius + originY; // originY is ... to desired Y coordinate
                circleCoords[i + 2] = 0;
            }
        } else if (foodType == CHERRY) {
            // IMPLEMENT LATER
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
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, numOfVertex);

        /*
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
                */

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }


    // Getter Functions
    public float getHeight() {return mFrame.getHeight();}
    public float getWidth() {return mFrame.getWidth();}
    public float getOriginX() {
        return mFrame.getOriginX();
    }
    public float getOriginY() {
        return mFrame.getOriginY();
    }

    // Setter Functions
    public void setOrigin(float newX, float newY) {
        mFrame.setOriginX(newX);
        mFrame.setOriginY(newY);
    }

    public Frame getFrame() {
        return mFrame;
    }


}
