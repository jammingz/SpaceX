package com.example.jamin.spacex;

/**
 * Created by jamin on 3/16/15.
 */
import android.graphics.Point;
import android.opengl.GLES20;
import android.util.Log;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by jamin on 1/5/15.
 */
public class Ghost extends Monster {

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

    protected static final int FACE_LEFT = 0;
    protected static final int FACE_UP = 1;
    protected static final int FACE_RIGHT = 2;
    protected static final int FACE_DOWN = 3;
    protected static final int SIDE_LEFT = 0;
    protected static final int SIDE_RIGHT = 1;


    private FloatBuffer vertexBufferA;
    private ShortBuffer drawListBufferA;
    private FloatBuffer vertexBufferB;
    private ShortBuffer drawListBufferB;
    private FloatBuffer vertexBufferC;
    private ShortBuffer drawListBufferC;
    private FloatBuffer vertexBufferD;
    private ShortBuffer drawListBufferD;
    private FloatBuffer vertexBufferE;
    private ShortBuffer drawListBufferE;
    private ShortBuffer drawListBuffer;


    // private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int maxVertex;
    private Frame mFrame;
    private float radius = 1.0f;//= GameBoard.PACMAN_RADIUS;
    private boolean isAnimating = false;
    private int mLastIteration;
    private int mDirection; // Direction which Pacman is facing. {0:left, 1:up, 2:right, 3:down}




    // number of coordinates per vertex in this array
    private final int COORDS_PER_VERTEX = 3;
    private float coordsA[]; // Two faces, face A
    private float coordsB[]; // face B
    private float coordsC[]; // face C
    private float coordsD[]; // eye A
    private float coordsE[]; // eye B
    private short drawOrder[];
    private short drawOrderB[];
    private short drawOrderC[];
    private short drawOrderD[]; //eye A
    private short drawOrderE[]; // eye B

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private float color[] = new float[4];//{ 0.2f, 0.709803922f, 0.898039216f, 1.0f };
    private float colorB[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
    private float colorC[] = { 0.0f, 0.0f, 0.0f, 1.0f };

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Ghost() {
        // Constructing the coordinates for the Ghost
        this(40,1.0f,1.0f,1.0f,1.0f); // Default vertex to 40 if none is provided
    }

    public Ghost(int vertexCount) {
        this(vertexCount,1.0f,1.0f,1.0f,1.0f);
    }

    public Ghost(int vertexCount, float colorR, float colorB, float colorG, float colorA) {
        // Constructing the coordinates for the circle
        if(vertexCount > 0) {
            maxVertex = vertexCount;
        } else {
            maxVertex = 150;
        }


        // Creating frame object for Ghost

        float x = radius;
        float y = radius;
        float width = 2 * radius;
        float height = 2 * radius;

        mFrame = new Frame(x,y,width,height);

        color[0] = colorR;
        color[1] = colorB;
        color[2] = colorG;
        color[3] = colorA;

        mDirection = 0; // Initially face left
        mLastIteration = 0;

        coordsA = new float[(maxVertex+11)*3];
        coordsA[0] = 0;
        coordsA[1] = 0;
        coordsA[2] = 0;
        coordsA[3] = -radius;
        coordsA[4] = 0;
        coordsA[5] = 0;
        coordsA[6] = radius;
        coordsA[7] = 0;
        coordsA[8] = 0;
        coordsA[9] = -radius;
        coordsA[10] = (float)(-0.6 * radius);
        coordsA[11] = 0;
        coordsA[12] = radius;
        coordsA[13] = (float)(-0.6 * radius);
        coordsA[14] = 0;
        coordsA[15] = -radius;
        coordsA[16] = -radius;
        coordsA[17] = 0;
        coordsA[18] = (float)(-0.575 * radius);
        coordsA[19] = (float)(-0.6 * radius);
        coordsA[20] = 0;
        coordsA[21] = (float)(-0.15 * radius);
        coordsA[22] = -radius;
        coordsA[23] = 0;
        coordsA[24] = (float)(-0.15 * radius);
        coordsA[25] = (float)(-0.6 * radius);
        coordsA[26] = 0;
        coordsA[27] = (float)(0.15 * radius);
        coordsA[28] = (float)(-0.6 * radius);
        coordsA[29] = 0;
        coordsA[30] = (float)(0.15 * radius);
        coordsA[31] = -radius;
        coordsA[32] = 0;
        coordsA[33] = (float)(0.575 * radius);
        coordsA[34] = (float)(-0.6 * radius);
        coordsA[35] = 0;
        coordsA[36] = radius;
        coordsA[37] = -radius;
        coordsA[38] = 0;

        for(int i=3;i<(maxVertex-1)*3;i+=3){
            double rad =  (i * 3.14159)/(maxVertex*3);//simplified from (i*180/(maxVertex*3))*(3.14159/180);
            coordsA[i+36] = (float)Math.cos(rad) * radius;
            coordsA[i+1+36] = (float) Math.sin(rad) * radius;
            coordsA[i+2+36] = 0;
        }

        drawOrder = new short[24 + 3 * (maxVertex - 3)];
        drawOrder[0] = 1;
        drawOrder[1] = 2;
        drawOrder[2] = 3;
        drawOrder[3] = 2;
        drawOrder[4] = 3;
        drawOrder[5] = 4;
        drawOrder[6] = 3;
        drawOrder[7] = 5;
        drawOrder[8] = 6;
        drawOrder[9] = 6;
        drawOrder[10] = 7;
        drawOrder[11] = 8;
        drawOrder[12] = 9;
        drawOrder[13] = 10;
        drawOrder[14] = 11;
        drawOrder[15] = 11;
        drawOrder[16] = 12;
        drawOrder[17] = 4;
        drawOrder[18] = 0; // Left end triangle
        drawOrder[19] = 1;
        drawOrder[20] = (short) (12 + (maxVertex - 2));
        drawOrder[21] = 0; // Right beginning triangle
        drawOrder[22] = 2;
        drawOrder[23] = 13;

        for (int i = 0; i < maxVertex - 3; i++) {
            drawOrder[24+3*i] = (short) (13 + i);
            drawOrder[24+3*i+1] = (short) (13 + i + 1);
            drawOrder[24+3*i+2] = 0;
        }

        /*
         * Formula for the eclipse for the eyes
         *
         */

        float centerLeftEyeX = 0.4f * radius;
        float centerLeftEyeY = 0.2f * radius;


        coordsB = new float[(maxVertex+1)*3];
        coordsB[0] = centerLeftEyeX;
        coordsB[1] = centerLeftEyeY;
        coordsB[2] = 0;
        coordsD = new float[(maxVertex+1)*3];
        coordsD[0] = centerLeftEyeX;
        coordsD[1] = centerLeftEyeY;
        coordsD[2] = 0;
        for(int i=3;i<maxVertex*3;i+=3){
            double rad =  (2 * i * 3.14159)/(maxVertex*3);//simplified from (i*360/(maxVertex*3))*(3.14159/180);
            coordsB[i] = 0.20f * (float)Math.cos(rad) * radius + centerLeftEyeX;
            coordsB[i+1] = 0.25f * (float) Math.sin(rad) * radius + centerLeftEyeY;
            coordsB[i+2] = 0;
            coordsD[i] = 0.1f * (float)Math.cos(rad) * radius + centerLeftEyeX;
            coordsD[i+1] = 0.1f * (float) Math.sin(rad) * radius + centerLeftEyeY;
            coordsD[i+2] = 0;
        }


        drawOrderB = new short[maxVertex*3];
        for (int i = 0; i < maxVertex - 2; i++) {
            drawOrderB[3*i] = (short) (i+1);
            drawOrderB[3*i+1] = (short) (i+2);
            drawOrderB[3*i+2] = 0;
        }

        drawOrderB[3*(maxVertex-1)] = (short) (maxVertex-1);
        drawOrderB[3*(maxVertex-1)+1] = 1;
        drawOrderB[3*(maxVertex-1)+2] = 0;

        float centerRightEyeX = -0.4f * radius;
        float centerRightEyeY = 0.2f * radius;


        coordsC = new float[(maxVertex+1)*3];
        coordsC[0] = centerRightEyeX;
        coordsC[1] = centerRightEyeY;
        coordsC[2] = 0;
        coordsE = new float[(maxVertex+1)*3];
        coordsE[0] = centerRightEyeX;
        coordsE[1] = centerRightEyeY;
        coordsE[2] = 0;
        for(int i=3;i<maxVertex*3;i+=3){
            double rad =  (2 * i * 3.14159)/(maxVertex*3);//simplified from (i*360/(maxVertex*3))*(3.14159/180);
            coordsC[i] = 0.20f * (float)Math.cos(rad) * radius + centerRightEyeX;
            coordsC[i+1] = 0.25f * (float) Math.sin(rad) * radius + centerRightEyeY;
            coordsC[i+2] = 0;
            coordsE[i] = 0.1f * (float)Math.cos(rad) * radius + centerRightEyeX;
            coordsE[i+1] = 0.1f * (float) Math.sin(rad) * radius + centerRightEyeY;
            coordsE[i+2] = 0;
        }

        drawOrderC = new short[maxVertex*3];
        for (int i = 0; i < maxVertex - 2; i++) {
            drawOrderC[3*i] = (short) (i+1);
            drawOrderC[3*i+1] = (short) (i+2);
            drawOrderC[3*i+2] = 0;
        }

        drawOrderC[3*(maxVertex-1)] = (short) (maxVertex-1);
        drawOrderC[3*(maxVertex-1)+1] = 1;
        drawOrderC[3*(maxVertex-1)+2] = 0;

        drawOrderD = new short[maxVertex*3];
        for (int i = 0; i < maxVertex - 2; i++) {
            drawOrderD[3 * i] = (short) (i + 1);
            drawOrderD[3 * i + 1] = (short) (i + 2);
            drawOrderD[3 * i + 2] = 0;
        }

        drawOrderD[3*(maxVertex-1)] = (short) (maxVertex-1);
        drawOrderD[3*(maxVertex-1)+1] = 1;
        drawOrderD[3*(maxVertex-1)+2] = 0;


        drawOrderE = new short[maxVertex*3];
        for (int i = 0; i < maxVertex - 2; i++) {
            drawOrderE[3*i] = (short) (i+1);
            drawOrderE[3*i+1] = (short) (i+2);
            drawOrderE[3*i+2] = 0;
        }

        drawOrderE[3*(maxVertex-1)] = (short) (maxVertex-1);
        drawOrderE[3*(maxVertex-1)+1] = 1;
        drawOrderE[3*(maxVertex-1)+2] = 0;



        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                coordsA.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBufferA = bb.asFloatBuffer();
        vertexBufferA.put(coordsA);
        vertexBufferA.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb2 = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                coordsB.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        vertexBufferB = bb2.asFloatBuffer();
        vertexBufferB.put(coordsB);
        vertexBufferB.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb2 = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrderB.length * 2);
        dlb2.order(ByteOrder.nativeOrder());
        drawListBufferB = dlb2.asShortBuffer();
        drawListBufferB.put(drawOrderB);
        drawListBufferB.position(0);

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb3 = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                coordsC.length * 4);
        bb3.order(ByteOrder.nativeOrder());
        vertexBufferC = bb3.asFloatBuffer();
        vertexBufferC.put(coordsC);
        vertexBufferC.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb3 = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrderB.length * 2);
        dlb3.order(ByteOrder.nativeOrder());
        drawListBufferC = dlb3.asShortBuffer();
        drawListBufferC.put(drawOrderB);
        drawListBufferC.position(0);

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb4 = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                coordsD.length * 4);
        bb4.order(ByteOrder.nativeOrder());
        vertexBufferD = bb4.asFloatBuffer();
        vertexBufferD.put(coordsD);
        vertexBufferD.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb4 = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrderB.length * 2);
        dlb4.order(ByteOrder.nativeOrder());
        drawListBufferD = dlb4.asShortBuffer();
        drawListBufferD.put(drawOrderB);
        drawListBufferD.position(0);

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb5 = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                coordsE.length * 4);
        bb5.order(ByteOrder.nativeOrder());
        vertexBufferE = bb5.asFloatBuffer();
        vertexBufferE.put(coordsE);
        vertexBufferE.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb5 = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrderB.length * 2);
        dlb5.order(ByteOrder.nativeOrder());
        drawListBufferE = dlb4.asShortBuffer();
        drawListBufferE.put(drawOrderB);
        drawListBufferE.position(0);

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
                vertexStride, vertexBufferA);

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

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Now to draw element B
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBufferB);

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, colorB, 0);

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrderB.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBufferB);

        // Now to draw element C
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBufferC);

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrderC.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBufferC);

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, colorC, 0);

        // Now to draw element D
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBufferD);

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrderD.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBufferD);

        // Now to draw element E
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBufferE);

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrderE.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBufferE);

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

    public void setFrame(Frame frame) {
        mFrame = frame;
    }


    public void nextFrame() {
        /*
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
        */
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


    public Ghost clone() {
        Ghost clone = new Ghost(maxVertex,color[0],color[1],color[2],color[3]);
        clone.setFrame(mFrame.clone()); // We clone a frame for the NEW pacman
        return clone;
    }
}
