package com.example.jamin.spacex;

/**
 * Created by Jamin on 1/27/2015.
 */
public class GameBoard {
    private int width;
    private int height;
    private GameObject[][] gamePieces;

    public GameBoard(int x, int y) {
        width = x;
        height = y;
        gamePieces = new GameObject[x][y];

        // Set null to every index in 2D array
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                gamePieces[i][j] = null;
            }
        }

    }


    public GameObject getGameObject(int x, int y) throws NullPointerException{
        return gamePieces[x][y]; // Returns object or returns null;
    }

    public boolean containsObject(int x, int y) {
        return false;
    }

}
