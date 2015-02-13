package com.example.jamin.spacex;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
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
    private boolean isPacmanAnimating = false;

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

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if (!isPacmanAnimating) {
                    Toast.makeText(aContext, "Start Animation", Toast.LENGTH_SHORT).show();
                    this.start();
                } else {
                    stop();
                    Toast.makeText(aContext, "Stop Animation", Toast.LENGTH_SHORT).show();
                }
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
            final int numOfNodes = 0;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateDebug(statusWindow, "STATUS: Searching...");
                }
            });

            // Search algorithm applied here


            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateDebug(statusWindow, "STATUS: Search Complete.");
                }
            });

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateDebug(nodeWindow, "NODES: " + String.valueOf(numOfNodes));
                }
            });


            // Begin Game Loop
            long cumulativeDt = 0;
            fps = 0;
            while (isPacmanAnimating) {
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
                if (true) {
                //if (cumulativeDt >= 1000) {
                    /*
                    fps = 1000 * mFPS / cumulativeDt;
                    cumulativeDt = 0;
                    mFPS = 0;
                    */
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {



                            //Long curFPS = mFPS / dt;
                            //updateDebug("FPSs: " + Long.toString(( mFPS/(1+currentTime - mStartTime))));
                            //updateDebug("FPS: " + Long.toString(curFPS));
                            //updateDebug("FPS: " + Long.toString(curFPS));
                            updateDebug(debugWindow, "FPS: " + Long.toString(mFPS));
                            //stuff that updates ui
                        }
                    });
                }

                //int currentFPS = round(mFPS/(currentTime - mStartTime));



                if (dt < 66) {
                    try {
                        Thread.sleep(66 - dt);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                requestRender();


                ArrayList<String> pacmanMoves = mRenderer.getAvailableMovesString(mRenderer.getPacman());
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

                if (mRenderer.isGoal()) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateDebug(movesWindow, "GOAL");
                        }
                    });
                }


            }


            return null;
        }

    }

    // Methods for rotating Pacman
    public void rotatePacman(int direction) {
        mRenderer.rotatePacman(direction);
    }

}
