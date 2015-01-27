package com.example.jamin.spacex;

/**
 * Created by jamin on 1/25/15.
 */
public class Frame {

    public final static int SHIFT_LEFT = 0;
    public final static int SHIFT_UP = 1;
    public final static int SHIFT_RIGHT = 2;
    public final static int SHIFT_DOWN = 3;
    private float originX;
    private float originY;
    private float height;
    private float width;

    public Frame(float x, float y, float width, float height) {
        originX = x;
        originY = y;
        this.width = width;
        this.height = height;
    }

    public float getOriginX() {
        return originX;
    }

    public float getOriginY() {
        return originY;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public void setOriginX(float x) {
        originX = x;
    }

    public void setOriginY(float y) {
        originY = y;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    // Returns a new Frame object with frame shifted in desired direction. DOES NOT HANDLE ILLEGAL FRAMES. IMPLEMENT LATER!
    public Frame getShiftedFrame(int direction, float amount) {
        switch (direction) {
            case SHIFT_LEFT:
                return new Frame(originX + amount, originY, width, height);
            case SHIFT_DOWN:
                return new Frame(originX, originY - amount, width, height);
            case SHIFT_RIGHT:
                return new Frame(originX - amount, originY, width, height);
            case SHIFT_UP:
                return new Frame(originX, originY + amount, width, height);
        }

        return this; // should never get to this case
    }
}
