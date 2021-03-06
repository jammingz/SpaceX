package com.example.jamin.spacex;

/**
 * Created by jamin on 2/21/15.
 */
import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;

import com.example.jamin.spacex.Frame;
import com.example.jamin.spacex.GLRenderer;
import com.example.jamin.spacex.PacMath;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by jamin on 2/21/15.
 */
public class OnTouchFrameInside {
    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // The matrix must be included as a modifier of gl_Position.
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private Frame mFrame;
    private boolean mDrawLock;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    private float squareCoords[];

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float color[];

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */

    public OnTouchFrameInside(Frame frame) {
        this(frame,0f,0.336f,0.8f);
    }

    public OnTouchFrameInside(Frame frame, float colorR, float colorG, float colorB) {
        mDrawLock = true;
        color = new float[4];
        color[0] = colorR;
        color[1] = colorG;
        color[2] = colorB;
        color[3] = 1.0f;

        mFrame = frame;

        float topLeftX = frame.getOriginX();
        float topLeftY = frame.getOriginY();
        float botLeftX = topLeftX;
        float botLeftY = frame.getOriginY() - frame.getHeight();
        float botRightX = frame.getOriginX() - frame.getWidth();
        float botRightY = botLeftY;
        float topRightX = botRightX;
        float topRightY = topLeftY;

        squareCoords = new float[] {
                topLeftX,  topLeftY, 0.0f,   // top left
                botLeftX, botLeftY, 0.0f,   // bottom left
                botRightX, botRightY, 0.0f,   // bottom right
                topRightX,  topRightY, 0.0f }; // top right

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

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
        mDrawLock = false;


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

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public void setFrame(Frame frame) {
        if (mDrawLock) {
            Log.i("DrawLock", "Concurrancy Issues");
            return; // Do nothing because lock is used
        } else {
            mDrawLock = true;
        }

        mFrame = frame;

        float topLeftX = frame.getOriginX();
        float topLeftY = frame.getOriginY();
        float botLeftX = topLeftX;
        float botLeftY = frame.getOriginY() - frame.getHeight();
        float botRightX = frame.getOriginX() - frame.getWidth();
        float botRightY = botLeftY;
        float topRightX = botRightX;
        float topRightY = topLeftY;

        squareCoords = new float[] {
                topLeftX,  topLeftY, 0.0f,   // top left
                botLeftX, botLeftY, 0.0f,   // bottom left
                botRightX, botRightY, 0.0f,   // bottom right
                topRightX,  topRightY, 0.0f }; // top right

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
        mDrawLock = false;

    }

    public float getOriginX() {
        return mFrame.getOriginX();
    }

    public float getOriginY() {
        return mFrame.getOriginY();
    }

    public float getHeight() {
        return mFrame.getHeight();
    }

    public float getWidth() {
        return mFrame.getWidth();
    }


}
