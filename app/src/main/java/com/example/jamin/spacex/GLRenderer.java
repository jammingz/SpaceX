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
    private static final float VELOCITY_MAX = 0.01333333f; //140 x 140 movement //0.015f; // 120 x 120 movements for pacman
    private static final float COLLISION_MARGIN_ERROR = 0.00001f;
    private static final float PACMAN_RADIUS = 0.066667f; // this radius gives us 15 pacman lengths across screen
    private static final float WALL_LENGTH = 3 * VELOCITY_MAX;

    private static final int LEFT_MOVE = 0;
    private static final int UP_MOVE = 1;
    private static final int RIGHT_MOVE = 2;
    private static final int DOWN_MOVE = 3;


    private Pacman mPacman;
    private ArrayList<Wall> mWalls;
    private ArrayList<Consumable> mConsumables;
    private ArrayList<Food> mConsumedFoods;

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

        mPacman = new Pacman(150,PACMAN_RADIUS,1.0f,1.0f,0.2f,1.0f);
        mAngle = 0; // Initialize angle to 0 degrees.
        mPacmanSide = 0; // Initialize at seeing right side

        // Creating borders
        Wall wall1 = new Wall(0.8567f,0.8567f,WALL_LENGTH,0.7333f);
        Wall wall2 = new Wall(0.8267f,0.8667f,0.1733f,WALL_LENGTH);
        Wall wall3 = new Wall(0.6933f,0.8267f,WALL_LENGTH,0.5199f); //0.8267f,WALL_LENGTH,0.6933f);
        Wall wall4 = new Wall(0.8267f,0.1733f,0.1733f,WALL_LENGTH);
        Wall wall5 = new Wall(0.6933f,0.1733f,WALL_LENGTH,0.4799f);
        Wall wall6 = new Wall(0.52f,0.8667f,0.3869f,WALL_LENGTH);
        Wall wall7 = new Wall(0.4801f,0.6933f,0.1733f,WALL_LENGTH);
        Wall wall8 = new Wall(0.1733f,0.6933f,0.133f,WALL_LENGTH);
        Wall wall9 = new Wall(0.5201f,0.6933f,WALL_LENGTH,0.3866f);
        Wall wall10 = new Wall(0.3067f, 0.5199f, 0.2133f, WALL_LENGTH);
        Wall wall11 = new Wall(0.52f, 0.3467f, 0.3067f, WALL_LENGTH);
        Wall wall12 = new Wall(0.2133f,0.3467f,WALL_LENGTH,0.1733f);
        Wall wall13 = new Wall(0.5201f,0.1733f,WALL_LENGTH,0.1733f);
        mWalls = new ArrayList<Wall>();
        mWalls.add(wall1);
        mWalls.add(wall2);
        mWalls.add(wall3);
        mWalls.add(wall4);
        //mWalls.add(wall5);
        mWalls.add(wall6);
        mWalls.add(wall7);
        mWalls.add(wall8);
        mWalls.add(wall9);
        mWalls.add(wall10);
        mWalls.add(wall11);
        mWalls.add(wall12);
        mWalls.add(wall13);

        mConsumedFoods = new ArrayList<Food>();
        mConsumables = new ArrayList<Consumable>();


        for (int i = 1; i < 8; i ++ ) {
            Consumable c = new Consumable(2 * PACMAN_RADIUS * i,0f);
            mConsumables.add(c);
        }

        for (int i = 1; i < 8; i ++ ) {
            Consumable c = new Consumable(2 * PACMAN_RADIUS * 7, -2 * PACMAN_RADIUS * i);
            mConsumables.add(c);
        }


        //Consumable c1 = new Consumable(0.4f,0f);
        //mConsumables.add(c1);

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

        // Draw all the consumables
        Iterator<Consumable> consumableIter = mConsumables.iterator();
        while (consumableIter.hasNext()) {
            Consumable curConsumable = consumableIter.next();
            curConsumable.draw(mMVPMatrix);
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

    public ArrayList<Integer> getAvailableMoves(Pacman monster) {
        ArrayList<Integer> availableMoves = new ArrayList<Integer>();
        float monsterLeft = monster.getOriginX();
        float monsterTop = monster.getOriginY();
        float monsterRight = monsterLeft - 2 * monster.getWidth();
        float monsterBottom = monsterTop - 2 * monster.getHeight();

        if (monsterLeft + VELOCITY_MAX <= 1.0f && !collisionDetection(mPacman,Frame.SHIFT_LEFT)) {
            // left move is available
            availableMoves.add(LEFT_MOVE);
        }

        if (monsterTop + VELOCITY_MAX <= 1.0f && !collisionDetection(mPacman,Frame.SHIFT_UP)) {
            // up move is available
            availableMoves.add(UP_MOVE);

        }

        if (monsterRight - VELOCITY_MAX >= -1.0f && !collisionDetection(mPacman,Frame.SHIFT_RIGHT)) {
            // right move is available
            availableMoves.add(RIGHT_MOVE);

        }

        if (monsterBottom - VELOCITY_MAX >= -1.0f && !collisionDetection(mPacman,Frame.SHIFT_DOWN)) {
            // down move is available
            availableMoves.add(DOWN_MOVE);
        }

        return availableMoves;
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

    // ** REMINDER** This takes in the predict path frame and NOT THE ACTUAL monster frame.
    public boolean collisionDetection(Pacman creature, int direction) {
        Frame newFrame = creature.getFrame().getShiftedFrame(direction,VELOCITY_MAX);
        float creatureLeft = newFrame.getOriginX();
        float creatureTop = newFrame.getOriginY();
        float creatureRadius = creature.getRadius();
        float creatureBottom = creatureTop - 2 * creatureRadius;
        float creatureRight = creatureLeft - 2 * creatureRadius;

        // Check if pacman collides with any wall
        Iterator<Wall> wallIter = mWalls.iterator();
        while (wallIter.hasNext()) {
            Wall curWall = wallIter.next();
            float wallLeft = curWall.getOriginX();
            float wallTop = curWall.getOriginY();
            float wallHeight = curWall.getHeight();
            float wallWidth = curWall.getWidth();
            float wallRight = wallLeft - wallWidth;
            float wallBottom = wallTop - wallHeight;

            //if ((creatureLeft < wallLeft && creatureLeft > wallRight) || (creatureRight < wallLeft && creatureRight > wallRight)) {
            if ((creatureLeft < wallLeft && creatureLeft > wallRight && Math.abs(creatureLeft - wallRight) > COLLISION_MARGIN_ERROR) || (creatureRight < wallLeft && creatureRight > wallRight && Math.abs(creatureRight - wallLeft) > COLLISION_MARGIN_ERROR) || (creatureLeft > wallLeft && creatureRight < wallRight)) {
                // the wall and creature collide in the x axis. Lets confirm they also intersect in the y direction
                //if ((creatureTop < wallTop) && (creatureTop > wallBottom) || ((creatureBottom < wallTop) && (creatureBottom > wallBottom))) {
                if ((creatureTop < wallTop && creatureTop > wallBottom && Math.abs(creatureTop - wallBottom) > COLLISION_MARGIN_ERROR) || (creatureBottom < wallTop && creatureBottom > wallBottom && Math.abs(creatureBottom - wallTop) > COLLISION_MARGIN_ERROR) || (creatureTop > wallTop && creatureBottom < wallBottom)) {
                    return true; // Only section where the creature and the wall intersects, both in x and y axis.
                }
            }
        }

        // Checks if pacman collides with any food object
        Iterator<Consumable> foodIter = mConsumables.iterator();
        while (foodIter.hasNext()) {
            Consumable curFood = foodIter.next();
            float foodLeft = curFood.getOriginX();
            float foodTop = curFood.getOriginY();
            float foodLength = curFood.getHeight();
            float foodRight = foodLeft - foodLength;
            float foodBottom = foodTop - foodLength;

            //if ((creatureLeft < wallLeft && creatureLeft > wallRight) || (creatureRight < wallLeft && creatureRight > wallRight)) {
            if ((creatureLeft < foodLeft && creatureLeft > foodRight && Math.abs(creatureLeft - foodRight) > COLLISION_MARGIN_ERROR) || (creatureRight < foodLeft && creatureRight > foodRight && Math.abs(creatureRight - foodLeft) > COLLISION_MARGIN_ERROR) || (creatureLeft > foodLeft && creatureRight < foodRight)) {
                // the wall and creature collide in the x axis. Lets confirm they also intersect in the y direction
                //if ((creatureTop < wallTop) && (creatureTop > wallBottom) || ((creatureBottom < wallTop) && (creatureBottom > wallBottom))) {
                if ((creatureTop < foodTop && creatureTop > foodBottom && Math.abs(creatureTop - foodBottom) > COLLISION_MARGIN_ERROR) || (creatureBottom < foodTop && creatureBottom > foodBottom && Math.abs(creatureBottom - foodTop) > COLLISION_MARGIN_ERROR) || (creatureTop > foodTop && creatureBottom < foodBottom)) {
                    // pacman has obtained food item. We move food item into list of consumed foods
                    mConsumedFoods.add(curFood);
                    mConsumables.remove(curFood);
                    break;
                }
            }
        }

        return false; // return false if wall and creature does not come in contact

    }
}
