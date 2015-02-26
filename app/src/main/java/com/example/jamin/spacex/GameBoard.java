package com.example.jamin.spacex;

import android.opengl.Matrix;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

/**
 * Created by Jamin on 1/27/2015.
 */
public class GameBoard {
    protected static final float VELOCITY_MAX = 0.01333333f; //141 x 141 movement //0.015f; // 120 x 120 movements for pacman
    private static final float COLLISION_MARGIN_ERROR = 0.00001f;
    protected static final float PACMAN_RADIUS = 0.066667f; // this radius gives us 15 pacman lengths across screen
    private static final float WALL_LENGTH = 3 * VELOCITY_MAX;
    private int currentMove;

    private float PACMAN_OFFSET_X;
    private float PACMAN_OFFSET_Y;

    private static final int LEFT_MOVE = 0;
    private static final int UP_MOVE = 1;
    private static final int RIGHT_MOVE = 2;
    private static final int DOWN_MOVE = 3;
    private static final int LEFT_WALL = 0;
    private static final int TOP_WALL = 1;
    private static final int RIGHT_WALL = 2;
    private static final int BOTTOM_WALL = 3;
    protected static final int INDEX_X = 0;
    protected static final int INDEX_Y = 1;
    private float goalX;
    private float goalY;

    private float mAngle;
    private int mPacmanSide; // {0: Pacman is showing right side of race, 1: Pacman is showing left side of face. Orthogonal to the screen and is facing the user}
    private static final float ANGLE_OFFSET = 0;
    private OnTouchFrame mOnTouchFrame;
    private OnTouchFrameInside mOnTouchFrameInside;
    private boolean mDrawMarker;

    // Parent and children nodes
    GameBoard mParent;
    int mMoveFromParent;
    ArrayList<GameBoard> mChildren;

    private Pacman mPacman;
    private ArrayList<Wall> mWalls;
    private Wall[][] mWallDictionary;
    private ArrayList<Consumable> mConsumables;
    private ArrayList<Food> mConsumedFoods;



    public GameBoard() {
        PACMAN_OFFSET_X = 1.0f;
        PACMAN_OFFSET_Y = 1.0f;
        mDrawMarker = false;

        mPacman = new Pacman(150,PACMAN_RADIUS,1.0f,1.0f,0.2f,1.0f);
        mPacman.setOrigin(PACMAN_OFFSET_X, PACMAN_OFFSET_Y);
        mAngle = 0; // Initialize angle to 0 degrees.
        mPacmanSide = 0; // Initialize at seeing right side

        // initializing children arrayList
        mChildren = new ArrayList<GameBoard>();


        // Creating borders
        Wall wall1 = new Wall(1 - gridLength(1, 0), 1 - gridLength(1, 0), WALL_LENGTH, gridLength(4, 5));
        Wall wall2 = new Wall(1 - gridLength(1, 1), 1 - gridLength(1, 0), gridLength(1, 1), WALL_LENGTH);
        Wall wall3 = new Wall(1 - gridLength(2, 1), 1 - gridLength(1, 1), WALL_LENGTH, 0.5199f);
        Wall wall4 = new Wall(1 - gridLength(1, 1), 1 - gridLength(5, 4), gridLength(1, 1), WALL_LENGTH);
        Wall wall5 = new Wall(1 - gridLength(2, 1), 1 - gridLength(5, 5), WALL_LENGTH, gridLength(3, 2));
        Wall wall6 = new Wall(1 - gridLength(1, 0), 1 - gridLength(6, 5), WALL_LENGTH, gridLength(2, 2));
        Wall wall7 = new Wall(1 - gridLength(3, 2), 1 - gridLength(1, 0), gridLength(2, 3), WALL_LENGTH);
        Wall wall8 = new Wall(1 - gridLength(3, 3), 1 - gridLength(2, 1), gridLength(1, 1), WALL_LENGTH);
        Wall wall9 = new Wall(1 - gridLength(5, 4), 1 - gridLength(2, 1), gridLength(1, 1), WALL_LENGTH);
        Wall wall10 = new Wall(1 - gridLength(3, 2), 1 - gridLength(2, 1), WALL_LENGTH, gridLength(2, 3));
        Wall wall11 = new Wall(1 - gridLength(4, 3), 1 - gridLength(3, 2), gridLength(1, 2), WALL_LENGTH);
        Wall wall12 = new Wall(1 - gridLength(3, 3), 1 - gridLength(4, 3), gridLength(2, 1), WALL_LENGTH);
        Wall wall13 = new Wall(1 - gridLength(5, 4), 1 - gridLength(4, 3), WALL_LENGTH, gridLength(1, 1));
        Wall wall14 = new Wall(1 - gridLength(3, 2), 1 - gridLength(5, 4), WALL_LENGTH, gridLength(1, 1));
        Wall wall15 = new Wall(1 - gridLength(3, 2), 1 - gridLength(6, 5), gridLength(4, 5), WALL_LENGTH);
        Wall wall16 = new Wall(1 - gridLength(3, 2), 1 - gridLength(7, 6), gridLength(5, 5), WALL_LENGTH);
        Wall wall17 = new Wall(1 - gridLength(1, 0), 1 - gridLength(8, 7), gridLength(6, 7), WALL_LENGTH);
        Wall wall18 = new Wall(1 - gridLength(6, 5), 1.0f, WALL_LENGTH, gridLength(6, 5));
        Wall wall19 = new Wall(1 - gridLength(7, 6), 1 - gridLength(5, 4), WALL_LENGTH, gridLength(1, 1));
        Wall wall20 = new Wall(1 - gridLength(7, 6), 1 - gridLength(1, 0), WALL_LENGTH, gridLength(3, 3));
        Wall wall21 = new Wall(1 - gridLength(7, 6), 1 - gridLength(4, 3), gridLength(1, 1), WALL_LENGTH);
        Wall wall22 = new Wall(1 - gridLength(8, 7), 1.0f, WALL_LENGTH, gridLength(2, 2));
        Wall wall23 = new Wall(1 - gridLength(8, 7), 1 - gridLength(3, 2), WALL_LENGTH, gridLength(6, 7));

        Wall border1 = new Wall(1.0f, 1 - gridLength(9, 8), gridLength(9, 9), WALL_LENGTH);
        Wall border2 = new Wall(1 - gridLength(9, 8), 1.0f, WALL_LENGTH, gridLength(9, 8));
        mWalls = new ArrayList<Wall>();
        mWalls.add(wall1);
        mWalls.add(wall2);
        mWalls.add(wall3);
        mWalls.add(wall4);
        mWalls.add(wall5);
        mWalls.add(wall6);
        mWalls.add(wall7);
        mWalls.add(wall8);
        mWalls.add(wall9);
        mWalls.add(wall10);
        mWalls.add(wall11);
        mWalls.add(wall12);
        mWalls.add(wall13);
        mWalls.add(wall14);
        mWalls.add(wall15);
        mWalls.add(wall16);
        mWalls.add(wall17);
        mWalls.add(wall18);
        mWalls.add(wall19);
        mWalls.add(wall20);
        mWalls.add(wall21);
        mWalls.add(wall22);
        mWalls.add(wall23);
        mWalls.add(border1);
        mWalls.add(border2);


        mWallDictionary = new Wall[141][141];

        for (int i = 0; i < 141; i++) {
            for (int j = 0; j < 141; j++) {
                //mWallDictionary = null; // initializing dictionary to contain nulls
            }
        }

        wallIntoDictionary(wall1);
        wallIntoDictionary(wall2);
        wallIntoDictionary(wall3);
        wallIntoDictionary(wall4);
        wallIntoDictionary(wall5);
        wallIntoDictionary(wall6);
        wallIntoDictionary(wall7);
        wallIntoDictionary(wall8);
        wallIntoDictionary(wall9);
        wallIntoDictionary(wall10);
        wallIntoDictionary(wall11);
        wallIntoDictionary(wall12);
        wallIntoDictionary(wall13);
        wallIntoDictionary(wall14);
        wallIntoDictionary(wall15);
        wallIntoDictionary(wall16);
        wallIntoDictionary(wall17);
        wallIntoDictionary(wall18);
        wallIntoDictionary(wall19);
        wallIntoDictionary(wall20);
        wallIntoDictionary(wall21);
        wallIntoDictionary(wall22);
        wallIntoDictionary(wall23);
        wallIntoDictionary(border1);
        wallIntoDictionary(border2);

        mConsumedFoods = new ArrayList<Food>();
        mConsumables = new ArrayList<Consumable>();

        for (int i = 1; i < 8; i++) {
            Consumable c = new Consumable(2 * PACMAN_RADIUS * i, 0f);
            mConsumables.add(c);
        }

        for (int i = 1; i < 8; i++) {
            Consumable c = new Consumable(2 * PACMAN_RADIUS * 7, -2 * PACMAN_RADIUS * i);
            mConsumables.add(c);
        }


        //Consumable c1 = new Consumable(0.4f,0f);
        //mConsumables.add(c1);

        currentMove = 0;
        mOnTouchFrame = new OnTouchFrame(getPacman().getFrame());
        mOnTouchFrameInside = new OnTouchFrameInside(getPacman().getFrame(),0f,0f,0f);

        // default goal
        goalX = 1 - gridLength(8,8);
        goalY = 1 - gridLength(8,8);

    }

    private GameBoard(float angle, int pacmanSide, GameBoard parent, ArrayList<GameBoard> children, Pacman pacman, ArrayList<Wall> walls, ArrayList<Consumable> consumables, ArrayList<Food> consumedFoods, float goal_x, float goal_y)  {
        mAngle = angle;
        mPacmanSide = pacmanSide;
        mParent = parent;
        mChildren = children;
        mPacman = pacman.clone();
        mWalls = walls;

        // Creating a clone of consumables. This cloning is only possible because we do not modify the actual object; only modifying the arrayList references
        ArrayList<Consumable> consumeablesClone = new ArrayList<Consumable>(consumables.size());
        for (Consumable item: consumables) consumeablesClone.add(item);
        mConsumables = consumeablesClone;

        // Creating a clone of consumedFoods.
        ArrayList<Food> consumedClone = new ArrayList<Food>(consumedFoods.size());
        for (Food item: consumedFoods) consumedClone.add(item);
        mConsumedFoods = consumedClone;
        currentMove = 0;
        mDrawMarker = false;
        mOnTouchFrame = new OnTouchFrame(getPacman().getFrame());
        mOnTouchFrameInside = new OnTouchFrameInside(getPacman().getFrame(),0f,0f,0f);

        // default goal
        goalX = goal_x;
        goalY = goal_y;

    }

    public void fillChildren() {
        mChildren = new ArrayList<GameBoard>();
        ArrayList<Integer> availableMoves = getAvailableMoves(mPacman);
        Iterator<Integer> availableMovesIter = availableMoves.iterator();

        while (availableMovesIter.hasNext()) {
            // we create a new gameboard configuration representing each possible move
            int availableMove = availableMovesIter.next();
            GameBoard nextConfig = new GameBoard();
            switch (availableMove) {
                case LEFT_MOVE: // Move left
                    nextConfig = simulateMove(LEFT_MOVE);
                    nextConfig.setMoveFromParent(LEFT_MOVE);
                    break;
                case UP_MOVE: // Move Up
                    nextConfig = simulateMove(UP_MOVE);
                    nextConfig.setMoveFromParent(UP_MOVE);
                    break;
                case RIGHT_MOVE: // Move Right
                    nextConfig = simulateMove(RIGHT_MOVE);
                    nextConfig.setMoveFromParent(RIGHT_MOVE);
                    break;
                case DOWN_MOVE: // Move Down
                    nextConfig = simulateMove(DOWN_MOVE);
                    nextConfig.setMoveFromParent(DOWN_MOVE);
                    break;
            }

            nextConfig.setParent(this); // setting the children node to point its parent field to current node
            mChildren.add(nextConfig); // setting current node's children field to point to child node
        }

        //Log.i("logChildren", "NumberOfChildren: " + String.valueOf(mChildren.size()));
    }

    private GameBoard simulateMove(int move) {
        // create clone GameBoard
        GameBoard newBoard = new GameBoard(mAngle,mPacmanSide,mParent,mChildren,mPacman,mWalls,mConsumables,mConsumedFoods,goalX,goalY);
        switch(move) {
            case LEFT_MOVE: // Move left
                newBoard.setTranslation(newBoard.getPacman(),0);
                break;
            case UP_MOVE: // Move Up
                newBoard.setTranslation(newBoard.getPacman(),90.0f);
                break;
            case RIGHT_MOVE: // Move Right
                newBoard.setTranslation(newBoard.getPacman(),180.0f);
                break;
            case DOWN_MOVE: // Move Down
                newBoard.setTranslation(newBoard.getPacman(),270.0f);
                break;

        }
        return newBoard;
    }

    public void drawPacman(float[] matrix) {
        mPacman.draw(matrix);
    }

    public void drawWall(float[] matrix) {
        Iterator<Wall> wallIter = mWalls.iterator();
        while (wallIter.hasNext()) {
            Wall curWall = wallIter.next();
            curWall.draw(matrix);
        }
    }

    public void drawConsumables(float[] matrix) {
        Iterator<Consumable> consumableIter = mConsumables.iterator();
        while (consumableIter.hasNext()) {
            Consumable curConsumable = consumableIter.next();
            curConsumable.draw(matrix);
        }
    }

    public boolean drawOnTouch(float[] matrix) {
        if (mDrawMarker) {
            mOnTouchFrame.draw(matrix);
            mOnTouchFrameInside.draw(matrix);
            return true;
        }
        return false; // returns false if user did not press screen
    }

    public void flip(float[] rotationMatrix) {
        float[] z_rotate = new float[16];
        float[] y_rotate = new float[16];
        float zAngle = (mAngle + 180 - 2 * ANGLE_OFFSET) % 360; // after flip, rotate 180 degrees to get correct direction
        Matrix.setRotateM(z_rotate, 0, zAngle, 0, 0, 1.0f);
        Matrix.setRotateM(y_rotate,0,180,0,1.0f,0);

        Matrix.multiplyMM(rotationMatrix,0,z_rotate,0,y_rotate,0);
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

            if ((creatureLeft < wallLeft && creatureLeft > wallRight && Math.abs(creatureLeft - wallRight) > COLLISION_MARGIN_ERROR) || (creatureRight < wallLeft && creatureRight > wallRight && Math.abs(creatureRight - wallLeft) > COLLISION_MARGIN_ERROR) || (creatureLeft > wallLeft && creatureRight < wallRight)) {
                // the wall and creature collide in the x axis. Lets confirm they also intersect in the y direction
                if ((creatureTop < wallTop && creatureTop > wallBottom && Math.abs(creatureTop - wallBottom) > COLLISION_MARGIN_ERROR) || (creatureBottom < wallTop && creatureBottom > wallBottom && Math.abs(creatureBottom - wallTop) > COLLISION_MARGIN_ERROR) || (creatureTop > wallTop && creatureBottom < wallBottom)) {
                    //Log.i("COLLISION", "pacman:{"+creatureLeft+","+creatureTop+","+creatureRight+","+creatureBottom+"}, wall: {"+wallLeft+","+wallTop+","+wallRight+","+wallBottom+"}");
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

    // Checks for collision only. Does not remove food if it is in the way. Used for getAvailableMoves
    public boolean collisionDetectionNoFood(Pacman creature, int direction) {
        Frame newFrame = creature.getFrame().getShiftedFrame(direction, VELOCITY_MAX);
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

            if ((creatureLeft < wallLeft && creatureLeft > wallRight && Math.abs(creatureLeft - wallRight) > COLLISION_MARGIN_ERROR) || (creatureRight < wallLeft && creatureRight > wallRight && Math.abs(creatureRight - wallLeft) > COLLISION_MARGIN_ERROR) || (creatureLeft > wallLeft && creatureRight < wallRight)) {
                // the wall and creature collide in the x axis. Lets confirm they also intersect in the y direction
                if ((creatureTop < wallTop && creatureTop > wallBottom && Math.abs(creatureTop - wallBottom) > COLLISION_MARGIN_ERROR) || (creatureBottom < wallTop && creatureBottom > wallBottom && Math.abs(creatureBottom - wallTop) > COLLISION_MARGIN_ERROR) || (creatureTop > wallTop && creatureBottom < wallBottom)) {
                    //Log.i("COLLISION", "pacman:{"+creatureLeft+","+creatureTop+","+creatureRight+","+creatureBottom+"}, wall: {"+wallLeft+","+wallTop+","+wallRight+","+wallBottom+"}");
                    return true; // Only section where the creature and the wall intersects, both in x and y axis.
                }
            }
        }
        return false;
    }

    public ArrayList<Integer> collisionDetectionFrame(Frame frame) {
        ArrayList<Integer> results = new ArrayList<Integer>();
        float creatureLeft = frame.getOriginX();
        float creatureTop = frame.getOriginY();
        float creatureBottom = creatureTop - frame.getHeight();
        float creatureRight = creatureLeft - frame.getWidth();

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

            if ((creatureLeft < wallLeft && creatureLeft > wallRight && Math.abs(creatureLeft - wallRight) > COLLISION_MARGIN_ERROR) || (creatureRight < wallLeft && creatureRight > wallRight && Math.abs(creatureRight - wallLeft) > COLLISION_MARGIN_ERROR) || (creatureLeft > wallLeft && creatureRight < wallRight)) {
                // the wall and creature collide in the x axis. Lets confirm they also intersect in the y direction
                if ((creatureTop < wallTop && creatureTop > wallBottom && Math.abs(creatureTop - wallBottom) > COLLISION_MARGIN_ERROR) || (creatureBottom < wallTop && creatureBottom > wallBottom && Math.abs(creatureBottom - wallTop) > COLLISION_MARGIN_ERROR) || (creatureTop > wallTop && creatureBottom < wallBottom)) {
                    //Log.i("COLLISION", "pacman:{"+creatureLeft+","+creatureTop+","+creatureRight+","+creatureBottom+"}, wall: {"+wallLeft+","+wallTop+","+wallRight+","+wallBottom+"}");
                    if (creatureLeft < wallLeft && creatureLeft > wallRight && Math.abs(creatureLeft - wallRight) > COLLISION_MARGIN_ERROR) {
                        if (!results.contains(LEFT_WALL)) {
                            results.add(LEFT_WALL);
                        }
                    }

                    if (creatureRight < wallLeft && creatureRight > wallRight && Math.abs(creatureRight - wallLeft) > COLLISION_MARGIN_ERROR) {
                        if (!results.contains(RIGHT_WALL)) {
                            results.add(RIGHT_WALL);
                        }
                    }

                    if (creatureTop < wallTop && creatureTop > wallBottom && Math.abs(creatureTop - wallBottom) > COLLISION_MARGIN_ERROR) {
                        if (!results.contains(TOP_WALL)) {
                            results.add(TOP_WALL);
                        }
                    }

                    if (creatureBottom < wallTop && creatureBottom > wallBottom && Math.abs(creatureBottom - wallTop) > COLLISION_MARGIN_ERROR) {
                        if (!results.contains(BOTTOM_WALL)) {
                            results.add(BOTTOM_WALL);
                        }
                    }
                }
            }




        }
        return results;
    }

    // methods for calculuating the coordinates on the grid. Used for creating walls

    //  gridLength takes in the number of pacman lengths and wall lengths and gives you the length of result
    private float gridLength(int pac, int wall) {
        return 2 * pac * PACMAN_RADIUS + wall * WALL_LENGTH;
    }


    public ArrayList<String> getAvailableMovesString(Pacman monster) {
        ArrayList<Integer> arr = getAvailableMoves(monster);
        ArrayList<String> results = new ArrayList();
        for (int i = 0; i < arr.size(); i++) {
            Integer curMove = arr.get(i);
            switch(curMove) {
                case 0:
                    results.add("Left");
                    break;
                case 1:
                    results.add("Up");
                    break;
                case 2:
                    results.add("Right");
                    break;
                case 3:
                    results.add("Down");
                    break;
            }
        }

        return results;
    }

    public boolean isGoal() {
        float pacX = mPacman.getFrame().getOriginX();
        float pacY = mPacman.getFrame().getOriginY();

        if (Math.abs(goalX - pacX) < COLLISION_MARGIN_ERROR && Math.abs(goalY - pacY) < COLLISION_MARGIN_ERROR) {
            return true;
        }

        return false;
    }

    public void setGoal(float x, float y) {
        Log.i("setGoal","("+String.valueOf(x)+","+String.valueOf(y)+")");
        goalX = x;
        goalY = y;
    }




    public void setTranslation(Pacman creature, float angle) {
        float creatureLeft = creature.getOriginX();
        float creatureTop = creature.getOriginY();
        float creatureRight = creatureLeft - 2 * creature.getRadius();
        float createBottom = creatureTop - 2 * creature.getRadius();

        switch ((int) angle) {
            case 0: // Facing left
                if (creatureLeft + VELOCITY_MAX <= 1.0f && !collisionDetection(mPacman, Frame.SHIFT_LEFT)) {
                    creature.setOrigin(creatureLeft + VELOCITY_MAX, creature.getOriginY()); // move left only if we haven't hit boundary
                }
                break;

            case 90: // facing up
                if (creatureTop + VELOCITY_MAX <= 1.0f && !collisionDetection(mPacman, Frame.SHIFT_UP)) {
                    creature.setOrigin(creature.getOriginX(), creature.getOriginY() + VELOCITY_MAX); // move up only if we haven't hit boundary
                }
                break;

            case 180: // facing right
                if (creatureRight - VELOCITY_MAX >= -1.0f && !collisionDetection(mPacman, Frame.SHIFT_RIGHT)) {
                    creature.setOrigin(creatureLeft - VELOCITY_MAX, creature.getOriginY()); // move right only if we haven't hit boundary
                }
                break;

            case 270: // facing down
                if (createBottom - VELOCITY_MAX >= -1.0f && !collisionDetection(mPacman, Frame.SHIFT_DOWN)) {
                    creature.setOrigin(creature.getOriginX(), creature.getOriginY() - VELOCITY_MAX); // move down only if we haven't hit boundary
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

        if (monsterLeft + VELOCITY_MAX <= 1.0f && !collisionDetectionNoFood(mPacman, Frame.SHIFT_LEFT)) {
            // left move is available
            availableMoves.add(LEFT_MOVE);
        }

        if (monsterTop + VELOCITY_MAX <= 1.0f && !collisionDetectionNoFood(mPacman, Frame.SHIFT_UP)) {
            // up move is available
            availableMoves.add(UP_MOVE);

        }

        if (monsterRight - VELOCITY_MAX >= -1.0f && !collisionDetectionNoFood(mPacman, Frame.SHIFT_RIGHT)) {
            // right move is available
            availableMoves.add(RIGHT_MOVE);

        }

        if (monsterBottom - VELOCITY_MAX >= -1.0f && !collisionDetectionNoFood(mPacman, Frame.SHIFT_DOWN)) {
            // down move is available
            availableMoves.add(DOWN_MOVE);
        }

        return availableMoves;
    }


    private void wallIntoDictionary(Wall wall) {
        int x = Math.round((1 - wall.getOriginX())/VELOCITY_MAX);
        int y = Math.round((1 - wall.getOriginY())/VELOCITY_MAX);
        int r = Math.round(wall.getWidth()/VELOCITY_MAX);
        int s = Math.round(wall.getHeight()/VELOCITY_MAX);



        for (int i = 0; i < r; i++) {
            for (int j = 0; j < s; j++) {
                //Log.i("Dictionary","("+String.valueOf(i)+","+String.valueOf(j)+")");
                mWallDictionary[x+i][y+j] = wall;
            }
        }
    }


    public Frame nearestPacmanSelection(float pixelX, float pixelY) {
        int x = (int) ((pixelX * 141) / 720); // x coordinate on the surfaceview. there are 141 x 141 coordinates
        int y = (int) ((pixelY * 141) / 720); // y coordinate

        /*
         We count the amount of left x coordinates from the current tap and the top y coordinates. We reset counter if we hit a wall, only counting the coordinates next to the
         */

        if (mWallDictionary[x][y] == null) { // empty space
            int leftCounter = 0; // find how many index to the left are empty
            int topCounter = 0; // find how many index to the top are empty
            int rightCounter = 0;
            int bottomCounter = 0;




            // Count left coordinates from ontouch Point
            for (int i = x - 1; i > x - 10 && i >= 0; i--) {
                if (mWallDictionary[i][y] == null) {
                    // it is empty slot, we increment leftCounter
                    leftCounter++;
                } else {
                    break;
                }
            }

            // Count right coordinates
            for (int i = x + 1; i < x + 10 && i < 141; i++) {
                if (mWallDictionary[i][y] == null) {
                    // it is empty slot, we increment leftCounter
                    rightCounter++;
                } else {
                    break;
                }
            }

            // Count top coordinates
            for (int j = y - 1; j > y - 10 && j >= 0; j--) {
                if (mWallDictionary[x][j] == null) {
                    // it is empty slot, we increment leftCounter
                    topCounter++;
                } else {
                    break;
                }
            }

            // Count top coordinates
            for (int j = y + 1; j < y + 10 && j < 141; j++) {
                if (mWallDictionary[x][j] == null) {
                    // it is empty slot, we increment leftCounter
                    bottomCounter++;
                } else {
                    break;
                }
            }


            Log.i("WallCollision","("+String.valueOf(x)+","+String.valueOf(y)+","+String.valueOf(leftCounter)+","+String.valueOf(rightCounter)+","+String.valueOf(topCounter)+","+String.valueOf(bottomCounter)+")");

            // Check to see if frame is wide enough. Should be 10 x 10 coordinates, otherwise, we return null for an impossible marker. left side + right side should be at least 9, we include the current onTouch point as an empty(non-colliding point)
            if (leftCounter + rightCounter + 1 < 10 || topCounter + bottomCounter + 1 < 10) {
                return null;
            }

            // Center border as much as possible without being inside a wall
            int xOffset = leftCounter;
            int yOffset = topCounter;


            // Determining offset for x-coordinate
            if (xOffset < 4) {
                //xOffset = 4;
            } else {
                if (rightCounter < 4) {
                    xOffset = 5 + 4 - rightCounter;
                } else {
                    xOffset = 5;
                }
            }

            // Determining offset for y-coordinate
            if (yOffset < 4) {
                //yOffset = 4;
            } else {
                if (bottomCounter < 4) {
                    yOffset = 5 + 4 - bottomCounter;
                } else {
                    yOffset = 5;
                }
            }

            float frameX = 1 - VELOCITY_MAX * (x - xOffset);
            float frameY = 1 - VELOCITY_MAX * (y - yOffset);

            Frame trialFrame = new Frame(frameX,frameY,2 * PACMAN_RADIUS, 2 * PACMAN_RADIUS);
            ArrayList<Integer> collisionList = collisionDetectionFrame(trialFrame);
            while (collisionList.size() > 0) { // If the ArrayList is greater than 0, there is a collision on 1 of the 4 sides of the frame
                Iterator<Integer> collisionIter = collisionList.iterator();
                while(collisionIter.hasNext()) {
                    int wallCollision = collisionIter.next();
                    switch (wallCollision) {
                        case LEFT_WALL:
                            trialFrame = trialFrame.getShiftedFrame(RIGHT_MOVE,VELOCITY_MAX);
                            break;
                        case TOP_WALL:
                            trialFrame = trialFrame.getShiftedFrame(RIGHT_MOVE,VELOCITY_MAX);
                            break;
                        case RIGHT_WALL:
                            trialFrame = trialFrame.getShiftedFrame(LEFT_MOVE,VELOCITY_MAX);
                            break;
                        case BOTTOM_WALL:
                            trialFrame = trialFrame.getShiftedFrame(UP_MOVE,VELOCITY_MAX);
                            break;
                    }
                }

                collisionList = collisionDetectionFrame(trialFrame);
            }

            return trialFrame;
        }
        // if we are currently pressing on a wall, we return null since pacman cannot be in a wall
        return null;

    }

    // returns the frame of the nearest Frame to the touch point, if invalid, return null
    public Frame onTouch(float x, float y) {
        Frame touchFrame = nearestPacmanSelection(x,y);
        if (touchFrame != null) {
            // True if we touched an empty spot
            mOnTouchFrame.setFrame(touchFrame);

            // Calculating the dimensions of the inner frame
            float innerX = touchFrame.getOriginX() - touchFrame.getWidth() * 0.1f;
            float innerY = touchFrame.getOriginY() - touchFrame.getHeight() * 0.1f;
            float innerWidth = touchFrame.getWidth() * 0.8f;
            float innerHeight = touchFrame.getHeight() * 0.8f;
            mOnTouchFrameInside.setFrame(new Frame(innerX, innerY, innerWidth, innerHeight)); // black colored square
        } else {
            mDrawMarker = false;
            return null;
        }
        mDrawMarker = true;
        return touchFrame;
    }

    public void onRelease() {
        mDrawMarker = false;
        mOnTouchFrameInside.setFrame(getPacman().getFrame());
        mOnTouchFrame.setFrame(getPacman().getFrame());
    }

    /* Setter and Getter Methods */


    public void setPacmanAngle(float angle) {
        mAngle = angle;
    }

    public Pacman getPacman() {
        return mPacman;
    }

    public int getPacmanSide() {
        return mPacmanSide;
    }

    public float getPacmanAngle() {
        return mAngle;
    }

    public ArrayList<GameBoard> getChildren() {
        return mChildren;
    }

    private void setParent(GameBoard parent) {
        mParent = parent;
    }

    private void setMoveFromParent(int move) {mMoveFromParent = move;}

    public int getMoveFromParent() { return mMoveFromParent;}

    public GameBoard getParent() {
        return mParent;
    }

    public void incrementMoveCount() {
        currentMove++;
    }

    public void resetMoveCount() {
        currentMove = 0;
    }

    public int getMoveCount() {
        return currentMove;
    }


}
