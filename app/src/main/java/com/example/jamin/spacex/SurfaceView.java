package com.example.jamin.spacex;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static java.lang.Math.round;

/**
 * Created by jamin on 1/5/15.
 */
public class SurfaceView extends GLSurfaceView {
    private final GLRenderer mRenderer;
    private static Context aContext=null;
    static protected boolean isPacmanAnimating = false;

    private long mLastTime;
    private long mStartTime;
    private int mFPS;
    private long fps;
    int stop;
    boolean needsUpdate;
    int currentFPS;

    private TextView debugWindow;
    private TextView statusWindow;
    private TextView nodeWindow;
    private TextView movesWindow;
    private String movesString;
    private Activity mActivity;
    private Long currentTime;
    private Long dt;
    private boolean isHold;


    private boolean temp;

    public SurfaceView(Context context) {
        super(context);
        aContext = context;
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new GLRenderer();
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mFPS = 0;
        needsUpdate = false;
        temp = false;
    }


    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;


    public void init(TextView fps, TextView status, TextView node, TextView score, TextView moves, Activity activity) {
        statusWindow = status;
        nodeWindow = node;
        debugWindow = fps;
        mActivity = activity;
        movesWindow = moves;
    }

    public void start() {
        isPacmanAnimating = true;
        mStartTime = System.currentTimeMillis();
        mLastTime = mStartTime;

        new UpdateAnimationTask().execute();

    }

    public void stop() {
        isPacmanAnimating = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:

                /*
                if (!isPacmanAnimating) {
                    Toast.makeText(aContext, "("+String.valueOf(x)+","+String.valueOf(y)+")", Toast.LENGTH_SHORT).show();
                    this.start();
                } else {
                    stop();
                    Toast.makeText(aContext, "("+String.valueOf(x)+","+String.valueOf(y)+")", Toast.LENGTH_SHORT).show();
                }

                */

                mRenderer.onTouch(x,y);
                requestRender();
                break;
            case MotionEvent.ACTION_UP:
                mRenderer.onRelease();
                requestRender();
                break;
        }


        return true;
    }

    public void updateDebug(TextView view,String text) {
        view.setText(text);
    }

    private class UpdateAnimationTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // initialize search
            ArrayList<Integer> solution = new ArrayList<Integer>();

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateDebug(statusWindow, "STATUS: Searching...");
                }
            });

            // Search algorithm applied here
            solution = mRenderer.simulateDFS();


            final int numOfNode = solution.size();

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateDebug(statusWindow, "STATUS: Search Complete.");
                }
            });

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateDebug(nodeWindow, "NODES: " + String.valueOf(numOfNode));
                }
            });


            // Begin Game Loop
            long cumulativeDt = 0;
            fps = 0;

            solution.add(0,solution.get(0));
            int solutionIndex = 0;
            int solutionEnd = solution.size();
            while (isPacmanAnimating && solutionIndex + 1 < solutionEnd) {
                solutionIndex = mRenderer.getGameBoard().getMoveCount();
                int curSolutionMove = solution.get(solutionIndex);
                mRenderer.getGameBoard().incrementMoveCount();
                mFPS++;
                currentTime = System.currentTimeMillis();
                dt = currentTime - mLastTime;
                mLastTime = currentTime;
                if (dt <= 0) {
                    dt = new Long(1); // Cannot divide by 0 for FPS
                }

                cumulativeDt += dt;
                /*
                if (currentTime - mLastTime >= 1000) {
                    mFPS = 0;
                    mLastTime = currentTime;
                }
                */

                fps = 1000 * mFPS / cumulativeDt;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Long curFPS = mFPS / dt;
                        updateDebug(debugWindow, "FPS: " + Long.toString(dt));
                        //stuff that updates ui
                    }
                });

                //int currentFPS = round(mFPS/(currentTime - mStartTime));



                if (dt < 66) {
                    try {
                        Thread.sleep(66 - dt);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                rotatePacman(curSolutionMove);
                requestRender();




                ArrayList<String> pacmanMoves = mRenderer.getGameBoard().getAvailableMovesString(mRenderer.getGameBoard().getPacman());
                // Now we format the moves into a string

                movesString = "{";
                if (pacmanMoves.size() > 0) {
                    movesString += pacmanMoves.get(0);
                }

                for (int i = 1; i < pacmanMoves.size(); i++) {
                    movesString += ", " + pacmanMoves.get(i);
                }

                movesString += "}";

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateDebug(movesWindow, "Moves: " + movesString);
                    }
                });

                if (mRenderer.getGameBoard().isGoal()) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateDebug(movesWindow, "GOAL");
                        }
                    });
                }

                while (!mRenderer.isRenderComplete()) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }


            return null;
        }

    }

    // Methods for rotating Pacman
    public void rotatePacman(int direction) {
        mRenderer.getGameBoard().rotatePacman(direction);
    }

}
