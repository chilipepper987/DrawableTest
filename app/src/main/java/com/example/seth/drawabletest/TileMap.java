package com.example.seth.drawabletest;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by Seth on 4/15/2015.
 */
public class TileMap {
    private Tile[][] map;
    private int startX;
    private int startY;
    private int offsetX = 0;
    private int offsetY = 1;


    private Dude dude = null;

    private boolean rockRemoved = false;
    private int[] rock;


    public TileMap() {

        //...

        Level level=HelperLib.readLevelFromFile(2);
        this.map=level.getMap();
        this.startX=level.getStartX();
        this.startY=level.getStartY();

        int[] rock = {0, 0};
        this.rock = rock;
    }

    public boolean setDude(Dude dude) {
        if (this.dude == null) {
            this.dude = dude;
            return true;
        } else {
            return false;
        }
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    /**
     * To advance right in the level, we must move the level left.
     *
     * @return
     */
    public boolean advanceRight() {
        if (offsetX > 0) {
            --offsetX;
            return true;
        } else {
            return false;
        }
    }

    /**
     * To advance left in the level, we must move the level right.
     *
     * @return Boolean success
     */
    public boolean advanceLeft() {
        if (offsetX < this.map[0].length - 7) {
            ++offsetX;
            return true;
        } else {
            return false;
        }
    }

    /**
     * To advance up in the level, we must move the level down.
     *
     * @return Boolean success
     */
    public boolean advanceUp() {
        if (offsetY > 0) {
            --offsetY;
            return true;
        } else {
            return false;
        }
    }

    /**
     * To advance down in the level, we must move the level up.
     *
     * @return Boolean success
     */
    public boolean advanceDown() {
        if (offsetX < this.map.length - 7) {
            ++offsetY;
            return true;
        } else {
            return false;
        }
    }

    /**
     * move the map left or right, depending on the direction we just moved,
     * and if there is room on the screen to scroll or not
     *
     * @return
     */
    public int advanceX() {
        int moved = 0;
        int direction = this.dude.orientation;
        if (direction == -1) {
            //we just moved left. can we see the left edge?

            if (getOffsetX() > 0) {
                //the edge is off screen. shift.
                advanceRight();
                moved = 1;
            }
        } else {
            //we just moved right. can we see the edge?

            if (getOffsetX() + 21 < map[0].length) {
                //the map is 21 wide, so the right edge is 21 (or 20?) steps to the right from the left edge
                advanceLeft();
                moved = -1;
            }
        }
        return moved;
    }

    public boolean removeRock(int y, int x) {
        //we can only remove a rock IF there is a rock there AND we haven't already removed one
        //because we can only grab one rock at a time
        if (map[y][x].isRock() && this.rockRemoved == false) {
            //then there is a rock there, so we can remove it
            map[y][x] = new Tile(Tile.SPACE);
            //store the coordinates of where it was in case we need it later
            this.rockRemoved = true;
            this.rock[0] = y;
            this.rock[1] = x;

            return true;
        } else {
            //no rock, nothing to do
            return false;
        }
    }


    //when we are carrying the rock


    //when the rock is dropped
    public boolean placeRock(int y, int x) {
        //we can only place a rock IF there has been a rock removed AND there is space to place it
        if (map[y][x].isSpace() && this.rockRemoved == true) {
            //then there is a space, and we have a rock to place there

            //however, we can't just drop it right there, it needs to "fall" if
            //there is space below it IF we are not carrying it

            while (y < map.length && map[y + 1][x].isSpace()) {
                //then keep dropping
                y++;

            }
            map[y][x] = new Tile(Tile.ROCK);
            this.rockRemoved = false;//we just place it
            this.rock[0] = 0;
            this.rock[1] = 0;
            return true;
        } else {
            return false;
        }
    }

    //when the rock is being carried


//    //for when the dude is carrying the rock
//    //we don't have to validate because contractually we should have already checked
//    //if it is a legal place to carry the rock
//    public boolean carryRock(int y,int x) {
//        map[y][x]=ROCK;
//    }


    public Tile[][] getMap() {
        return this.map;
    }

}



