package com.example.jamin.spacex;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private SurfaceView mGLView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGLView = new SurfaceView(this);
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
        RelativeLayout controllerLayout = new RelativeLayout(this);
        controllerLayout.setId(R.id.controllerview);

        RelativeLayout.LayoutParams controllerParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        controllerParams.addRule(RelativeLayout.BELOW, mGLView.getId());
        controllerLayout.setLayoutParams(controllerParams);
        controllerLayout.setBackgroundColor(Color.BLACK);

        controllerLayout.getLayoutParams().height = height - width;
        controllerLayout.getLayoutParams().width = width/2; // Take up half the width of the screen

        RelativeLayout consoleLayout = new RelativeLayout(this);
        consoleLayout.setId(R.id.consoleview);

        RelativeLayout.LayoutParams consoleParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        consoleParams.addRule(RelativeLayout.BELOW, mGLView.getId());
        consoleParams.addRule(RelativeLayout.RIGHT_OF, R.id.controllerview);
        consoleLayout.setLayoutParams(consoleParams);
        consoleLayout.setBackgroundColor(Color.BLACK);

        consoleLayout.getLayoutParams().height = height - width;
        consoleLayout.getLayoutParams().width = width/2; // Take up half the width of the screen

        mLayout.addView(mGLView);
        mLayout.addView(controllerLayout);
        mLayout.addView(consoleLayout);

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
