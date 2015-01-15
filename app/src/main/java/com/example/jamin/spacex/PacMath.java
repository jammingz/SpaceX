package com.example.jamin.spacex;

/**
 * Created by jamin on 1/14/15.
 */
public class PacMath {

    public static int modulus(int x, int y)
    {
        int result = x % y;
        if (result < 0)
            result += y;
        return result;
    }
}
