package com.example.jamin.spacex;

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
        controllerFrameLayout.getLayoutParams().width = width/2; // Take up half the width of the screen

        RelativeLayout consoleLayout = new RelativeLayout(this);
        consoleLayout.setId(R.id.consoleview);

        RelativeLayout.LayoutParams consoleParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        consoleParams.addRule(RelativeLayout.BELOW, mGLView.getId());
        consoleLayout.setLayoutParams(consoleParams);
        consoleLayout.setBackgroundColor(Color.BLACK);

        consoleLayout.getLayoutParams().height = height - width;
        consoleLayout.getLayoutParams().width = width/2; // Take up half the width of the screen

        mLayout.addView(mGLView);
        mLayout.addView(controllerFrameLayout);
        mLayout.addView(consoleLayout);


        TextView debugWindow = new TextView(this);
        debugWindow.setId(R.id.debugWindow);
        debugWindow.setText("FPS: --");
        debugWindow.setTextColor(Color.WHITE);
        debugWindow.setTextSize(20.0f);

        RelativeLayout.LayoutParams debugLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        debugWindow.setLayoutParams(debugLayout);
        consoleLayout.addView(debugWindow);

        RelativeLayout.LayoutParams controllerViewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        View controllerView = getLayoutInflater().inflate(R.layout.controller , null);
        controllerView.setLayoutParams(controllerViewParams);
        controllerFrameLayout.addView(controllerView);


        mGLView.init(debugWindow, this);

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
