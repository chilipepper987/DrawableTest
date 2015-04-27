package com.example.seth.drawabletest;

/**
 * Created by Seth on 4/26/2015.
 */
public class Tile {
    public static final int ROCK = 2;
    public static final int GOAL = 3;
    public static final int SPACE = 0;
    public static final int WALL = 1;

    private int value;

    public Tile(int value) {
        this.value=value;
    }

    public Tile(String value) {
        this.value=Integer.parseInt(value);
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public int getValue() {
        return value;
    }

    public boolean isSpace() {
        return (value==SPACE || value==GOAL);
    }

    public boolean isSolid() {
        return !isSpace();
    }

    public boolean isRock() {
        return value==ROCK;
    }

    public boolean isGoal() {
        return value==GOAL;
    }

    public boolean isWall() {
        return value==WALL;
    }
}
