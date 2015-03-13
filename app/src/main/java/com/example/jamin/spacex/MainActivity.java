package com.example.jamin.spacex;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Math.round;


public class MainActivity extends Activity {

    private SurfaceView mGLView;
    private boolean isPacmanAnimating;
    private long mStartTime;
    private long mLastTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGLView = new SurfaceView(getApplicationContext());
        // Getting width and height of the device screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        // Setting up the main view
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.main_layout);
        mGLView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        // Setting the size of the game to square matching the size of the width of the screen
        mGLView.getLayoutParams().height = width;
        mGLView.getLayoutParams().width = width;
        mGLView.setId(R.id.mglview);

        // Now we create the controller
        RelativeLayout controllerFrameLayout = new RelativeLayout(this);
        controllerFrameLayout.setId(R.id.controller_frame_layout);

        RelativeLayout.LayoutParams controllerFrameParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        controllerFrameParams.addRule(RelativeLayout.BELOW, mGLView.getId());
        controllerFrameParams.addRule(RelativeLayout.RIGHT_OF, R.id.consoleview);
        controllerFrameLayout.setLayoutParams(controllerFrameParams);
        controllerFrameLayout.setBackgroundColor(Color.BLACK);

        controllerFrameLayout.getLayoutParams().height = height - width;
        controllerFrameLayout.getLayoutParams().width = width*2/3; // Take up half the width of the screen


        RelativeLayout consoleLayout = new RelativeLayout(this);
        consoleLayout.setId(R.id.consoleview);

        RelativeLayout.LayoutParams consoleParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        consoleParams.addRule(RelativeLayout.BELOW, mGLView.getId());
        consoleLayout.setLayoutParams(consoleParams);
        consoleLayout.setBackgroundColor(Color.BLACK);

        consoleLayout.getLayoutParams().height = height - width;
        consoleLayout.getLayoutParams().width = width * 1/3; // Take up half the width of the screen

        mLayout.addView(mGLView);
        mLayout.addView(controllerFrameLayout);
        mLayout.addView(consoleLayout);

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);



        TextView debugWindow = new TextView(this);
        debugWindow.setId(R.id.debugWindow);
        debugWindow.setText("FPS: --");
        debugWindow.setTextColor(Color.WHITE);
        debugWindow.setTextSize(14.0f);

        TextView debugWindow2 = new TextView(this);
        debugWindow2.setId(R.id.debugWindow2);
        debugWindow2.setText("STATUS: --");
        debugWindow2.setTextColor(Color.WHITE);
        debugWindow2.setTextSize(14.0f);
        params2.addRule(RelativeLayout.BELOW, R.id.debugWindow);

        TextView debugWindow3 = new TextView(this);
        debugWindow3.setId(R.id.debugWindow3);
        debugWindow3.setText("NODES: --");
        debugWindow3.setTextColor(Color.WHITE);
        debugWindow3.setTextSize(14.0f);
        params3.addRule(RelativeLayout.BELOW, R.id.debugWindow2);

        TextView debugWindow4 = new TextView(this);
        debugWindow4.setId(R.id.debugWindow4);
        debugWindow4.setText("SCORE: 300");
        debugWindow4.setTextColor(Color.WHITE);
        debugWindow4.setTextSize(14.0f);
        params4.addRule(RelativeLayout.BELOW, R.id.debugWindow3);

        TextView debugWindow5 = new TextView(this);
        debugWindow5.setId(R.id.debugWindow5);
        debugWindow5.setText("MOVES: {}");
        debugWindow5.setTextColor(Color.WHITE);
        debugWindow5.setTextSize(14.0f);
        params5.addRule(RelativeLayout.BELOW, R.id.debugWindow4);

        consoleLayout.addView(debugWindow,params1);
        consoleLayout.addView(debugWindow2,params2);
        consoleLayout.addView(debugWindow3,params3);
        consoleLayout.addView(debugWindow4,params4);
        consoleLayout.addView(debugWindow5,params5);


        /*
        RelativeLayout.LayoutParams debugLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        debugLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
        debugWindow.setLayoutParams(debugLayout);

        RelativeLayout.LayoutParams statusLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        statusLayout.addRule(RelativeLayout.BELOW, R.id.debugWindow);
        debugWindow2.setLayoutParams(statusLayout);


        RelativeLayout.LayoutParams nodeLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        nodeLayout.addRule(RelativeLayout.RIGHT_OF, R.id.debugWindow2);
        nodeLayout.addRule(RelativeLayout.BELOW, R.id.debugWindow2);
        debugWindow3.setLayoutParams(nodeLayout);
        consoleLayout.addView(debugWindow);
        consoleLayout.addView(debugWindow2);
        consoleLayout.addView(debugWindow3);



        RelativeLayout.LayoutParams scoreLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        nodeLayout.addRule(RelativeLayout.BELOW, R.id.debugWindow3);
        debugWindow4.setLayoutParams(scoreLayout);
        consoleLayout.addView(debugWindow4);

        RelativeLayout.LayoutParams movesLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        movesLayout.addRule(RelativeLayout.BELOW, R.id.debugWindow4);
        debugWindow5.setLayoutParams(movesLayout);
        consoleLayout.addView(debugWindow5);

        */

        RelativeLayout.LayoutParams controllerViewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        View controllerView = getLayoutInflater().inflate(R.layout.controller , null);
        controllerView.setLayoutParams(controllerViewParams);
        controllerFrameLayout.addView(controllerView);

        mGLView.init(debugWindow, debugWindow2, debugWindow3, debugWindow4, debugWindow5, this);

        // Setting up button logic
        final Button upButton = (Button) findViewById(R.id.cntr_up_btn);
        final Button leftButton = (Button) findViewById(R.id.cntr_left_btn);
        final Button downButton = (Button) findViewById(R.id.cntr_down_btn);
        final Button rightButton = (Button) findViewById(R.id.cntr_right_btn);


        upButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Up button is pressed
                mGLView.rotatePacman(Pacman.FACE_UP);
            }
        });

        leftButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Left button is pressed
                mGLView.rotatePacman(Pacman.FACE_LEFT);
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Right button is pressed
                mGLView.rotatePacman(Pacman.FACE_RIGHT);
            }
        });

        downButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Down button is pressed
                mGLView.rotatePacman(Pacman.FACE_DOWN);
            }
        });

    }



    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGLView.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGLView.onPause();
    }



}
