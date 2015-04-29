package com.example.seth.drawabletest;

/**
 * Created by MagillaGorilla on 4/27/2015.
 */
public class Level {
    private Tile[][] map;
    private int startX;
    private int startY;
    public String[][] strMap={{"?"}};

    public Level(Tile[][] map,int startX, int startY) {
        this.map=map;
        this.startX=startX;
        this.startY=startY;
    }

    public int getStartY() {
        return startY;
    }

    public Tile[][] getMap() {
        return map;
    }

    public int getStartX() {
        return startX;
    }
}
