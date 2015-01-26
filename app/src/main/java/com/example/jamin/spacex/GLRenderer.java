package com.example.jamin.spacex;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jamin on 1/5/15.
 */
public class GLRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "GLRenderer";
    private static final float VELOCITY_MAX = 0.015f; // 120 x 120 movements for pacman
    private Pacman mPacman;
    private ArrayList<Wall> mWalls;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    private float mAngle;
    private int mPacmanSide; // {0: Pacman is showing right side of race, 1: Pacman is showing left side of face. Orthogonal to the screen and is facing the user}
    private static final float ANGLE_OFFSET = 0;
    private boolean mFirstDraw;



    public GLRenderer() {
        mFirstDraw = true;
    }



    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mPacman = new Pacman(150,0.1f,1.0f,1.0f,0.2f,1.0f);
        mAngle = 0; // Initialize angle to 0 degrees.
        mPacmanSide = 0; // Initialize at seeing right side

        Wall wall1 = new Wall(1.0f,1.0f,1.0f,0.2f);
        Wall wall2 = new Wall(0.4f,-0.5f,0.4f,0.2f);
        Wall wall3 = new Wall(-0.5f,0.7f,0.2f,1.0f);
        Wall wall4 = new Wall(1.0f,0.8f,0.2f,1.0f);
        mWalls = new ArrayList<Wall>();
        mWalls.add(wall1);
        mWalls.add(wall2);
        mWalls.add(wall3);
        mWalls.add(wall4);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];
        float[] results = new float[16];
        float[] mTranslateM = new float[16];

        if (mFirstDraw) {
            mFirstDraw = false;
        } else {
            setTranslation(mPacman);
            mPacman.nextFrame();
        }


        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3.1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);


        //Matrix.translateM(mTranslateM, 0, -1.0f, -0.0f, 0);

        // Create a rotation for the triangle

        // Use the following code to generate constant rotation.
        // Leave this code out when using TouchEvents.
        // long time = SystemClock.uptimeMillis() % 4000L;
        // float angle = 0.090f * ((int) time);

        if (mPacmanSide == 1) {
            flip(mAngle);
        } else {
            Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);
        }

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        //Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        Matrix.multiplyMM(results, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        float pacmanX = mPacman.getOriginX() - mPacman.getRadius();
        float pacmanY = mPacman.getOriginY() - mPacman.getRadius();
        float projectedTransX = convertTranslationX(pacmanX,pacmanY,mAngle,mPacmanSide);
        float projectedTransY = convertTranslationY(pacmanX,pacmanY,mAngle,mPacmanSide);

        Matrix.translateM(results,0,projectedTransX,projectedTransY,0);
        //Matrix.multiplyMM(results,0,results,0,mTranslateM,0);

        //Matrix.translateM(results, 0, scratch, 0, 1.33f, mTranslationY, 0);


        // Set the camera position (View matrix)
        //Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -4.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        //Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Draw Pacman

        // iterate mouth shape if this is not the first drawing of pacman


        mPacman.draw(results);
        mAngle = mAngle % 360;

        // Draw all the walls
        Iterator<Wall> wallIter = mWalls.iterator();
        while (wallIter.hasNext()) {
            Wall curWall = wallIter.next();
            curWall.draw(mMVPMatrix);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    public float getAngle() {
        return mAngle;
    }

    public void setTranslation(Pacman creature) {
        float creatureLeft = creature.getOriginX();
        float creatureTop = creature.getOriginY();
        float creatureRight = creatureLeft - 2 * creature.getRadius();
        float createBottom = creatureTop - 2 * creature.getRadius();

        switch ((int) mAngle) {
            case 0: // Facing left
                if (creatureLeft + VELOCITY_MAX <= 1.0f && !collisionDetection(mPacman,Frame.SHIFT_LEFT)) {
                    creature.setOrigin(creatureLeft + VELOCITY_MAX,creature.getOriginY()); // move left only if we haven't hit boundary
                }
                break;

            case 90: // facing up
                if (creatureTop + VELOCITY_MAX <= 1.0f && !collisionDetection(mPacman,Frame.SHIFT_UP)) {
                    creature.setOrigin(creature.getOriginX(),creature.getOriginY() + VELOCITY_MAX); // move up only if we haven't hit boundary
                }
                break;

            case 180: // facing right
                if (creatureRight - VELOCITY_MAX >= -1.0f && !collisionDetection(mPacman,Frame.SHIFT_RIGHT)) {
                    creature.setOrigin(creatureLeft - VELOCITY_MAX,creature.getOriginY()); // move right only if we haven't hit boundary
                }
                break;

            case 270: // facing down
                if (createBottom - VELOCITY_MAX >= -1.0f && !collisionDetection(mPacman,Frame.SHIFT_DOWN)) {
                creature.setOrigin(creature.getOriginX(),creature.getOriginY() - VELOCITY_MAX); // move down only if we haven't hit boundary
                }
                break;
        }
    }

    /**
     * Sets the rotation angle of the triangle shape (mPacman).
     */
    public void setAngle(float angle) {
        mAngle = angle;
    }

    public void rotateCW() {
        mAngle = PacMath.modulus((int) mAngle + 90,360); // Add 90 mod(360) to current angle
    }

    public void rotateCCW() {
        mAngle = PacMath.modulus((int) mAngle - 90,360); // Add 90 mod(360) to current angle
    }

    public void flip(float angle) {
        float[] z_rotate = new float[16];
        float[] y_rotate = new float[16];
        float zAngle = (angle + 180 - 2 * ANGLE_OFFSET) % 360; // after flip, rotate 180 degrees to get correct direction
        Matrix.setRotateM(z_rotate, 0, zAngle, 0,0,1.0f);
        Matrix.setRotateM(y_rotate,0,180,0,1.0f,0);

        Matrix.multiplyMM(mRotationMatrix,0,z_rotate,0,y_rotate,0);
    }

    public void rotatePacman(int direction) {
        switch (direction) {
            case Pacman.FACE_LEFT:
                mAngle = 0;
                if (mPacmanSide == 1) {
                    mPacmanSide = 0;
                }
                break;
            case Pacman.FACE_DOWN:
                mAngle = 270;
                break;
            case Pacman.FACE_RIGHT:
                if (mPacmanSide == 0) {
                    mPacmanSide = 1;
                }
                mAngle = 180;
                break;
            case Pacman.FACE_UP:
                mAngle = 90;
                break;
        }
        mAngle += ANGLE_OFFSET;
    }


    public float convertTranslationX(float dx, float dy, float angle, int side) {
        if (side == 0) { // Original face
            switch ((int) angle) {
                case 90:
                    return dy;
                case 0:
                    return dx;
                case 270:
                    return -1 * dy;
            }
        } else {
            switch ((int) angle) {
                case 90:
                    return dy;
                case 180:
                    return -1 * dx;
                case 270:
                    return -1 * dy;
            }

        }
        return 0;
    }

    public float convertTranslationY(float dx, float dy, float angle, int side) {
        if (side == 0) {
            switch ((int) angle) {
                case 90:
                    return -1 * dx;
                case 0:
                    return dy;
                case 270:
                    return dx;
            }
        } else {
            switch ((int) angle) {
                case 90:
                    return dx;
                case 0:
                    return dy;
                case 270:
                    return -1 * dx;
            }
        }

        return dy;
    }


    public void startAnimation() {
        mPacman.startAnimation();
    }

    public void stopAnimation() { mPacman.stopAnimation();}

    public boolean collisionDetection(Pacman creature, int direction) {
        Frame newFrame = creature.getFrame().getShiftedFrame(direction,VELOCITY_MAX);

        float creatureLeft = newFrame.getOriginX();
        float creatureTop = newFrame.getOriginY();
        float creatureRadius = creature.getRadius();
        float creatureBottom = creatureTop - 2 * creatureRadius;
        float creatureRight = creatureLeft - 2 * creatureRadius;

        Iterator<Wall> wallIter = mWalls.iterator();
        while (wallIter.hasNext()) {
            Wall curWall = wallIter.next();
            float wallLeft = curWall.getOriginX();
            float wallTop = curWall.getOriginY();
            float wallHeight = curWall.getHeight();
            float wallWidth = curWall.getWidth();
            float wallRight = wallLeft - wallWidth;
            float wallBottom = wallTop - wallHeight;

            if ((creatureLeft <= wallLeft && creatureLeft >= wallRight) || (creatureRight <= wallLeft && creatureRight >= wallRight)) {
                // the wall and creature collide in the x axis. Lets confirm they also intersect in the y direction
                if ((creatureTop <= wallTop) && (creatureTop >= wallBottom) || ((creatureBottom <= wallTop) && (creatureBottom >= wallBottom))) {
                    return true; // Only section where the creature and the wall intersects, both in x and y axis.
                }
            }
        }

        return false; // return false if wall and creature does not come in contact

    }
}
