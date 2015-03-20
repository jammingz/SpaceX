package com.example.jamin.spacex;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.logging.LoggingMXBean;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jamin on 1/5/15.
 */
public class GLRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "GLRenderer";
    private static final int VISIT_INDEX_X = 0;
    private static final int VISIT_INDEX_Y = 1;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    private boolean mFirstDraw;
    private boolean mDrawMarker;
    private GameBoard mGameBoard;

    private Stack<GameBoard> tempGameBoardStack;
    private GameBoard tempNode;
    private boolean[][] tempVisited;
    private boolean isRenderComplete;

    public GLRenderer() {

        mFirstDraw = true;
        mDrawMarker = false;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mGameBoard = new GameBoard();
        mGameBoard.fillChildren();
        isRenderComplete = true;
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        isRenderComplete = false;
        float[] scratch = new float[16];
        float[] results = new float[16];
        float[] mTranslateM = new float[16];

        Pacman mPacman = mGameBoard.getPacman();

        if (mFirstDraw) {
            mFirstDraw = false;
        } else {
            mGameBoard.setTranslation(mGameBoard.getPacman(),mGameBoard.getPacmanAngle());
            mGameBoard.getPacman().nextFrame();
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

        if (mGameBoard.getPacmanSide() == 1) {
            mGameBoard.flip(mRotationMatrix);
        } else {
            Matrix.setRotateM(mRotationMatrix, 0, mGameBoard.getPacmanAngle(), 0, 0, 1.0f);
        }

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        //Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        Matrix.multiplyMM(results, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        float pacmanX = mPacman.getOriginX() - mPacman.getRadius();
        float pacmanY = mPacman.getOriginY() - mPacman.getRadius();
        float projectedTransX = mGameBoard.convertTranslationX(pacmanX, pacmanY, mGameBoard.getPacmanAngle(), mGameBoard.getPacmanSide());
        float projectedTransY = mGameBoard.convertTranslationY(pacmanX, pacmanY, mGameBoard.getPacmanAngle(), mGameBoard.getPacmanSide());

        Matrix.translateM(results, 0, projectedTransX, projectedTransY, 0);
        //Matrix.multiplyMM(results,0,results,0,mTranslateM,0);

        //Matrix.translateM(results, 0, scratch, 0, 1.33f, mTranslationY, 0);


        // Set the camera position (View matrix)
        //Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -4.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        //Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Draw Pacman

        // iterate mouth shape if this is not the first drawing of pacman


        //mPacman.draw(results);
        mGameBoard.drawPacman(results);
        mGameBoard.drawGhost(mMVPMatrix);
        mGameBoard.setPacmanAngle( mGameBoard.getPacmanAngle() % 360);

        // Draw all the walls
        mGameBoard.drawWall(mMVPMatrix);

        // Draw all the consumables
        mGameBoard.drawConsumables(mMVPMatrix);
        isRenderComplete = true;

        // Draw onTouch selector if pressed
        if (mDrawMarker) {
            mGameBoard.drawOnTouch(mMVPMatrix);
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
     * <p/>
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type       - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode) {

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
     * <p/>
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

    public GameBoard getGameBoard() {
        return mGameBoard;
    }


    private int getDictionaryIndex(GameBoard board, int returnIndex) {  // return index is whether you want the x index or y index for the dictionary
        // first we find x and y coordinates of pacman
        float pacX = board.getPacman().getFrame().getOriginX();
        float pacY = board.getPacman().getFrame().getOriginY();

        if (returnIndex == 0) {
            int result = Math.round((1 - pacX) / GameBoard.VELOCITY_MAX);
            return result;
        } else {
            int result = Math.round((1 - pacY) / GameBoard.VELOCITY_MAX);
            return result;
        }
    }

    public int getNeighborDictIndex(int curX, int curY, int move, int returnIndex) { // return index is whether you want the x index or y index for the dictionary
        int resultX = curX;
        int resultY = curY;
        switch (move) {
            case 0:
                resultX -= 1;
                break;
            case 1:
                resultY -= 1;
                break;
            case 2:
                resultX += 1;
                break;
            case 3:
                resultY += 1;
                break;
        }

        if (returnIndex == 0) {
            return resultX;
        }

        return resultY;
    }


    public void initDFS() {
        tempGameBoardStack = new Stack<GameBoard>();
        tempNode = mGameBoard;
        tempVisited = new boolean[141][141]; // dictionary is used to keep track of unique paths. Repeating paths will not show up as valid move
        for (int i = 0; i < 141; i++) {
            for (int j = 0; j < 141; j++) {
                tempVisited[i][j] = false; // initialize whole dictioanry to false
            }
        }

        tempVisited[0][0] = true;

        tempNode.fillChildren();
        ArrayList<GameBoard> children = tempNode.getChildren();


        for(int i = 0; i < children.size(); i++) {
            GameBoard child = children.get(i);
            tempGameBoardStack.push(child);
        }

        Log.i("Checkpoint","init complete. Stack: " + tempGameBoardStack.size());
    }

    public void DFSIter() {
        if (!tempNode.isGoal() && tempGameBoardStack.size() > 0) { // Loop until we find the goal
            tempNode = tempGameBoardStack.pop();

            // We flag visited dictionary to show we have visited current node
            int nodeVisitX = getDictionaryIndex(tempNode, VISIT_INDEX_X);
            int nodeVisitY = getDictionaryIndex(tempNode, VISIT_INDEX_Y);
            Log.i("Checkpoint","Visiting (" + String.valueOf(nodeVisitX) + "," + String.valueOf(nodeVisitY) + ") Stack: " + tempGameBoardStack.size());
            tempVisited[nodeVisitX][nodeVisitY] = true;

            tempNode.fillChildren();
            ArrayList<GameBoard> children = tempNode.getChildren();

            for(int i = 0; i < children.size(); i++) {
                GameBoard child = children.get(i);
                // we only push child into stack if we havent visited it yet. we check dictionary to see if child node has been visited
                int childVisitX = getDictionaryIndex(child, VISIT_INDEX_X);
                int childVisitY = getDictionaryIndex(child, VISIT_INDEX_Y);
                if (!tempVisited[childVisitX][childVisitY]) {
                    // if we havent already visited the current node, we push child into stack
                    tempGameBoardStack.push(child);
                }
            }

            // if node has no child and we're not at goal, we backtrack
            if (!tempNode.isGoal() && children.size() == 0) {
                // we need to "unvisit" the current node
                tempVisited[nodeVisitX][nodeVisitY] = false;
                Log.i("Checkpoint","Un-Visiting (" + String.valueOf(nodeVisitX) + "," + String.valueOf(nodeVisitY) + ") Stack: " + tempGameBoardStack.size());


            }

        }

        if (tempNode.isGoal()) {
            Log.i("Checkpoint","GOAL!");
        }
    }


    public boolean isRenderComplete() {
        return isRenderComplete;
    }

    public Frame onTouch(float x, float y) {
        Frame results = mGameBoard.onTouch(x,y);
        if (results != null) {
            mDrawMarker = true;
        } else {
            mDrawMarker = false;
        }
        return results;
    }

    public void onRelease() {
        mDrawMarker = false;
        mGameBoard.onRelease();
    }

    public void skipNextFrame() {
        mFirstDraw = true;
    }


    /* Search Algorithms */


    public ArrayList<Integer> DFS() {
        ArrayList<Integer> solution = new ArrayList<Integer>();
        Stack<GameBoard> gameBoardStack = new Stack<GameBoard>();
        GameBoard curNode = mGameBoard;
        boolean[][] visited = new boolean[141][141]; // dictionary is used to keep track of unique paths. Repeating paths will not show up as valid move
        for (int i = 0; i < 141; i++) {
            for (int j = 0; j < 141; j++) {
                visited[i][j] = false; // initialize whole dictioanry to false
            }
        }

// RED ALERT. FIX VISIteD
        visited[0][0] = true; // top left corner is currently occupied
        // get initial set of neighboring possible moves
        // ArrayList<Integer> initMoveset = board.getAvailableMoves(board.getPacman());

        curNode.fillChildren();
        ArrayList<GameBoard> children = curNode.getChildren();

        Log.i("Checkpoint","1");

        for(int i = 0; i < children.size(); i++) {
            GameBoard child = children.get(i);
            gameBoardStack.push(child);
        }


        Log.i("Checkpoint","2");

        while (!curNode.isGoal() && gameBoardStack.size() > 0 && SurfaceView.isPacmanAnimating) { // Loop until we find the goal
            curNode = gameBoardStack.pop();

            // We flag visited dictionary to show we have visited current node
            int nodeVisitX = getDictionaryIndex(curNode, VISIT_INDEX_X);
            int nodeVisitY = getDictionaryIndex(curNode, VISIT_INDEX_Y);
            visited[nodeVisitX][nodeVisitY] = true;

            curNode.fillChildren();
            children = curNode.getChildren();

            for(int i = 0; i < children.size(); i++) {
                GameBoard child = children.get(i);
                // we only push child into stack if we havent visited it yet. we check dictionary to see if child node has been visited
                int childVisitX = getDictionaryIndex(child, VISIT_INDEX_X);
                int childVisitY = getDictionaryIndex(child, VISIT_INDEX_Y);
                //Log.i("Checkpoint","Visiting (" + String.valueOf(nodeVisitX) + "," + String.valueOf(nodeVisitY) + ") Stack: " + gameBoardStack.size());
                if (!visited[childVisitX][childVisitY]) {
                    // if we havent already visited the current node, we push child into stack
                    gameBoardStack.push(child);
                }

            }

            // if node has no child and we're not at goal, we backtrack
            if (!curNode.isGoal() && children.size() == 0) {
                // we need to "unvisit" the current node
                visited[nodeVisitX][nodeVisitY] = false;
                Log.i("Checkpoint","Un-Visiting (" + String.valueOf(nodeVisitX) + "," + String.valueOf(nodeVisitY) + ") Stack: " + gameBoardStack.size());

            }

        }

        Log.i("DFS", "Done");

        // Time to traverse up the goal node up to parent.
        while (curNode.getParent() != null) {
            solution.add(curNode.getMoveFromParent());
            curNode = curNode.getParent();
        }
        // Since the order of the solution is backwards from the goal to beginning, we need to reverse the solution list.
        Collections.reverse(solution);
        return solution;
    }


    public ArrayList<Integer> BFS() {
        ArrayList<Integer> solution = new ArrayList<Integer>();
        Queue<GameBoard> gameBoardQueue = new LinkedList<GameBoard>();
        GameBoard curNode = mGameBoard;
        boolean[][] visited = new boolean[141][141]; // dictionary is used to keep track of unique paths. Repeating paths will not show up as valid move
        for (int i = 0; i < 141; i++) {
            for (int j = 0; j < 141; j++) {
                visited[i][j] = false; // initialize whole dictioanry to false
            }
        }

        float originX = mGameBoard.getPacman().getOriginX();
        float originY = mGameBoard.getPacman().getOriginY();
        float origin[] = {originX,originY};

        int originIndexes[] = mGameBoard.convertLocationIntoIndex(origin);
        int originIndexX = originIndexes[0];
        int originIndexY = originIndexes[1];
        visited[originIndexX][originIndexY] = true;


        // get initial set of neighboring possible moves
        // ArrayList<Integer> initMoveset = board.getAvailableMoves(board.getPacman());

        curNode.fillChildren();
        ArrayList<GameBoard> children = curNode.getChildren();

        Log.i("Checkpoint","1");

        for(int i = 0; i < children.size(); i++) {
            GameBoard child = children.get(i);
            gameBoardQueue.add(child);
        }


        Log.i("Checkpoint","2");

        while (!curNode.isGoal() && gameBoardQueue.size() > 0 && SurfaceView.isPacmanAnimating) { // Loop until we find the goal
            curNode = gameBoardQueue.remove();

            // We flag visited dictionary to show we have visited current node
            int nodeVisitX = getDictionaryIndex(curNode, VISIT_INDEX_X);
            int nodeVisitY = getDictionaryIndex(curNode, VISIT_INDEX_Y);
            visited[nodeVisitX][nodeVisitY] = true;

            curNode.fillChildren();
            children = curNode.getChildren();

            for(int i = 0; i < children.size(); i++) {
                GameBoard child = children.get(i);
                // we only push child into stack if we havent visited it yet. we check dictionary to see if child node has been visited
                int childVisitX = getDictionaryIndex(child, VISIT_INDEX_X);
                int childVisitY = getDictionaryIndex(child, VISIT_INDEX_Y);
                //Log.i("Checkpoint","Visiting (" + String.valueOf(nodeVisitX) + "," + String.valueOf(nodeVisitY) + ") Stack: " + gameBoardQueue.size());
                if (!visited[childVisitX][childVisitY]) {
                    // if we havent already visited the current node, we push child into stack
                    gameBoardQueue.add(child);
                    visited[childVisitX][childVisitY] = true;
                   // Log.i("Checkpoint","Adding Child (" + String.valueOf(nodeVisitX) + "," + String.valueOf(nodeVisitY) + ") Stack: " + gameBoardQueue.size());
                }

            }

            // if node has no child and we're not at goal, we backtrack
            if (!curNode.isGoal() && children.size() == 0) {
                // we need to "unvisit" the current node
                //visited[nodeVisitX][nodeVisitY] = false;
                //Log.i("Checkpoint","Un-Visiting (" + String.valueOf(nodeVisitX) + "," + String.valueOf(nodeVisitY) + ") Stack: " + gameBoardQueue.size());

            }

        }

        Log.i("DFS", "Done");

        // Time to traverse up the goal node up to parent.
        while (curNode.getParent() != null) {
            solution.add(curNode.getMoveFromParent());
            curNode = curNode.getParent();
        }
        // Since the order of the solution is backwards from the goal to beginning, we need to reverse the solution list.
        Collections.reverse(solution);
        return solution;
    }

    public ArrayList<Integer> Greedy() {
        ArrayList<Integer> solution = new ArrayList<Integer>();
        Queue<GameBoard> gameBoardQueue = new LinkedList<GameBoard>();
        GameBoard curNode = mGameBoard;
        boolean[][] visited = new boolean[141][141]; // dictionary is used to keep track of unique paths. Repeating paths will not show up as valid move
        for (int i = 0; i < 141; i++) {
            for (int j = 0; j < 141; j++) {
                visited[i][j] = false; // initialize whole dictioanry to false
            }
        }

        float originX = mGameBoard.getPacman().getOriginX();
        float originY = mGameBoard.getPacman().getOriginY();
        float origin[] = {originX,originY};

        int originIndexes[] = mGameBoard.convertLocationIntoIndex(origin);
        int originIndexX = originIndexes[0];
        int originIndexY = originIndexes[1];
        visited[originIndexX][originIndexY] = true;


        // get initial set of neighboring possible moves
        // ArrayList<Integer> initMoveset = board.getAvailableMoves(board.getPacman());

        curNode.fillChildren();
        ArrayList<GameBoard> children = curNode.getChildren();

        Log.i("Checkpoint","1");

        for(int i = 0; i < children.size(); i++) {
            GameBoard child = children.get(i);
            gameBoardQueue.add(child);
        }


        Log.i("Checkpoint","2");

        while (!curNode.isGoal() && gameBoardQueue.size() > 0 && SurfaceView.isPacmanAnimating) { // Loop until we find the goal
            curNode = gameBoardQueue.remove();

            // We flag visited dictionary to show we have visited current node
            int nodeVisitX = getDictionaryIndex(curNode, VISIT_INDEX_X);
            int nodeVisitY = getDictionaryIndex(curNode, VISIT_INDEX_Y);
            visited[nodeVisitX][nodeVisitY] = true;

            curNode.fillChildren();
            children = curNode.getChildren();

            for(int i = 0; i < children.size(); i++) {
                GameBoard child = children.get(i);
                // we only push child into stack if we havent visited it yet. we check dictionary to see if child node has been visited
                int childVisitX = getDictionaryIndex(child, VISIT_INDEX_X);
                int childVisitY = getDictionaryIndex(child, VISIT_INDEX_Y);
                //Log.i("Checkpoint","Visiting (" + String.valueOf(nodeVisitX) + "," + String.valueOf(nodeVisitY) + ") Stack: " + gameBoardQueue.size());
                if (!visited[childVisitX][childVisitY]) {
                    // if we havent already visited the current node, we push child into stack
                    gameBoardQueue.add(child);
                    visited[childVisitX][childVisitY] = true;
                    // Log.i("Checkpoint","Adding Child (" + String.valueOf(nodeVisitX) + "," + String.valueOf(nodeVisitY) + ") Stack: " + gameBoardQueue.size());
                }

            }

            // if node has no child and we're not at goal, we backtrack
            if (!curNode.isGoal() && children.size() == 0) {
                // we need to "unvisit" the current node
                //visited[nodeVisitX][nodeVisitY] = false;
                //Log.i("Checkpoint","Un-Visiting (" + String.valueOf(nodeVisitX) + "," + String.valueOf(nodeVisitY) + ") Stack: " + gameBoardQueue.size());

            }

        }

        Log.i("DFS", "Done");

        // Time to traverse up the goal node up to parent.
        while (curNode.getParent() != null) {
            solution.add(curNode.getMoveFromParent());
            curNode = curNode.getParent();
        }
        // Since the order of the solution is backwards from the goal to beginning, we need to reverse the solution list.
        Collections.reverse(solution);
        return solution;
    }


}
