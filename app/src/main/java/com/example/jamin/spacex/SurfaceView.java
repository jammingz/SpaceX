package com.example.jamin.spacex;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by jamin on 1/5/15.
 */
public class SurfaceView extends GLSurfaceView {
    private final GLRenderer mRenderer;
    private static Context aContext=null;

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
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;


    public void reset() {
        mRenderer.reset();
        requestRender();
    }
}
