package com.example.seth.drawabletest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaActionSound;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.HashMap;

public class MyView extends View {
    private Bitmap b, block, goal, dudeLeft, dudeRight, wall, brick;
    private Bitmap[] right, left, rockRight, rockLeft;
    private boolean drawingGuy = false;
    public float lastX = -1;
    public float lastY = -1;
    public float xStep = 0;
    public float yStep = 0;

    private int mapWidth, mapHeight, xMin, yMin, xMax, yMax;

    public MyView(Context context) {
        super(context);
        _init();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    private void _init() {
        this.block = BitmapFactory.decodeResource(getResources(), R.mipmap.block);
        this.goal = BitmapFactory.decodeResource(getResources(), R.mipmap.flag);
        this.dudeLeft = BitmapFactory.decodeResource(getResources(), R.mipmap.barnheadleft);
        this.dudeRight = BitmapFactory.decodeResource(getResources(), R.mipmap.barnheadright);
        this.wall = BitmapFactory.decodeResource(getResources(), R.mipmap.wall);
        this.brick = BitmapFactory.decodeResource(getResources(), R.mipmap.brix);


        Bitmap left[] = {
                BitmapFactory.decodeResource(getResources(), R.mipmap.barnheadleft_left_0),
                BitmapFactory.decodeResource(getResources(), R.mipmap.barnheadleft_left_1),
                BitmapFactory.decodeResource(getResources(), R.mipmap.barnheadleft_left_2),
                BitmapFactory.decodeResource(getResources(), R.mipmap.barnheadleft_left_3),
                BitmapFactory.decodeResource(getResources(), R.mipmap.barnheadleft_left_4),
                BitmapFactory.decodeResource(getResources(), R.mipmap.barnheadleft_left_5),
                BitmapFactory.decodeResource(getResources(), R.mipmap.barnheadleft_left_6)
        };

        Bitmap right[] = {
                BitmapFactory.decodeResource(getResources(), R.mipmap.barnheadright_right_0),
                BitmapFactory.decodeResource(getResources(), R.mipmap.barnheadright_right_1),
                BitmapFactory.decodeResource(getResources(), R.mipmap.barnheadright_right_2),
                BitmapFactory.decodeResource(getResources(), R.mipmap.barnheadright_right_3),
                BitmapFactory.decodeResource(getResources(), R.mipmap.barnheadright_right_4),
                BitmapFactory.decodeResource(getResources(), R.mipmap.barnheadright_right_5),
                BitmapFactory.decodeResource(getResources(), R.mipmap.barnheadright_right_6)
        };

        this.left = left;
        this.right = right;

        Bitmap rockLeft[] = {
                BitmapFactory.decodeResource(getResources(), R.mipmap.blockleft_left_0),
                BitmapFactory.decodeResource(getResources(), R.mipmap.blockleft_left_1),
                BitmapFactory.decodeResource(getResources(), R.mipmap.blockleft_left_2),
                BitmapFactory.decodeResource(getResources(), R.mipmap.blockleft_left_3),
                BitmapFactory.decodeResource(getResources(), R.mipmap.blockleft_left_4),
                BitmapFactory.decodeResource(getResources(), R.mipmap.blockleft_left_5),
                BitmapFactory.decodeResource(getResources(), R.mipmap.blockleft_left_6)
        };

        //left has the animation specified from left to right.
        //since the block looks the same on either side, for now we can just reverse the left and call it right.
        //if we want to change the animation later, we can, and the code referencing rockRight will not have to be adjusted.
        //ArrayUtils lets us reverse easily.
        Bitmap rockRight[];
        rockRight = rockLeft.clone();
        ArrayUtils.reverse(rockRight);

        this.rockLeft = rockLeft;
        this.rockRight = rockRight;


    }

    public void drawingDude() {
        drawingGuy = true;
    }

    public void drawingLevel() {
        drawingGuy = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        {


            super.onDraw(canvas);
            //setup
            _initDraw(canvas);

            //draw the level
            _drawMap(canvas);

            if (!MainActivity.theDude.animating) {
                //draw the dude
                _drawDude(canvas);
            } else {
                _animateDudeLeftOrRight(canvas);
            }
        }
    }

    private void _initDraw(Canvas canvas) {
        //=======================================
        //init stuff. do this every time
        //set up scale, grab tiles, call super, draw background
        canvas.drawColor(Color.argb(255, 135, 205, 250));

        if (Dude.xScale == 0) {
            Dude.xScale = block.getScaledWidth(canvas) / 2;
            Dude.yScale = block.getScaledHeight(canvas) / 2;
            block = HelperLib.getResizedBitmap(block, Dude.xScale, Dude.yScale);
            goal = HelperLib.getResizedBitmap(goal, Dude.xScale, Dude.yScale);
            wall = HelperLib.getResizedBitmap(wall, Dude.xScale, Dude.yScale);
            dudeLeft = HelperLib.getResizedBitmap(dudeLeft, Dude.xScale, Dude.yScale);
            dudeRight = HelperLib.getResizedBitmap(dudeRight, Dude.xScale, Dude.yScale);
            brick = HelperLib.getResizedBitmap(brick, Dude.xScale, Dude.yScale);

            //resize the bitmap for each each frame in the animation arrays
            //these are all the same size, and so could probably all be done in the same loop,
            //but we might not know that ahead of time??..
            for (int i = 0; i < left.length; i++) {
                //is 2x wide
                left[i] = HelperLib.getResizedBitmap(left[i], Dude.xScale * 2, Dude.yScale);
            }

            for (int i = 0; i < right.length; i++) {
                //is 2x wide
                right[i] = HelperLib.getResizedBitmap(right[i], Dude.xScale * 2, Dude.yScale);
            }

            for (int i = 0; i < rockLeft.length; i++) {
                //is 2x wide
                rockLeft[i] = HelperLib.getResizedBitmap(rockLeft[i], Dude.xScale * 2, Dude.yScale);
            }

            for (int i = 0; i < rockRight.length; i++) {
                //is 2x wide
                rockRight[i] = HelperLib.getResizedBitmap(rockRight[i], Dude.xScale * 2, Dude.yScale);
            }


        } else {
            Log.d("xScale", "" + Dude.xScale);
            Log.d("yScale", "" + Dude.yScale);
            Log.d("blockHeight", "" + block.getScaledHeight(canvas));
        }

        //end init stuff
        //===========================================
    }

    private void _drawMap(Canvas canvas) {

        TileMap map = MainActivity.theDude.map;
        mapWidth = map.getMap()[0].length;
        mapHeight = map.getMap().length;
        xMin = map.getOffsetX();
        yMin = map.getOffsetY();
        xMax = xMin + 23 > mapWidth ? mapWidth - xMin : xMin + 23;
        yMax = yMin + 7 > mapHeight ? mapHeight - yMin : yMin + 7;
        Log.d("start drawing", "now");
        for (int i = xMin; i < xMax; i++) {

            for (int j = yMin; j < yMax; j++) {
                //Log.d("loop", "");
                int tileNumber = map.getMap()[j][i].getValue();
                if (tileNumber < 1) {
                    continue;
                }
                Bitmap tile = wall;
                Boolean draw = true;
                switch (tileNumber) {
                    case 1: {
                        tile = wall;
                        break;
                    }
                    case 2: {
                        tile = block;
                        break;
                    }
                    case 3: {
                        tile = goal;
                        break;
                    }
                    case 5: {
                        tile = brick;
                        break;
                    }
                    default: {
                        draw = false;
                        break;
                    }
                }
                if (draw) {
                    canvas.drawBitmap(tile, (i - xMin) * Dude.xScale, (j - yMin) * Dude.yScale, null);
                }
            }
        }
        Log.d("stop", "drawing");
    }

    private void _drawDude(Canvas canvas) {
        //just draw once without animating
        //offset x and y by the map offset
        int x = MainActivity.theDude.x;
        int y = MainActivity.theDude.y;
        //get the right sprite based on orientation
        Bitmap dude;
        String orientation = MainActivity.theDude.getOrientation();
        if (orientation == "left") {
            dude = dudeLeft;
        } else if (orientation == "right") {
            dude = dudeRight;
        } else {
            dude = dudeLeft;
        }
        canvas.drawBitmap(dude, (x - xMin) * Dude.xScale, (y - yMin) * Dude.yScale, null);
        //and finally, if the dude is holding the rock, draw the rock above the dude's head
        if (MainActivity.theDude.holdingRock) {
            canvas.drawBitmap(block, (x - xMin) * Dude.xScale, ((y - yMin) - 1) * Dude.yScale, null);
        }

        //initialize lastX,lastY
        lastX = x;
        lastY = y;
    }

    private void _animateDudeLeftOrRight(Canvas canvas) {
        int x = MainActivity.theDude.x;
        int y = MainActivity.theDude.y;

        Bitmap[] dudeAnimation, rockAnimation;
        int orientation = MainActivity.theDude.orientation;
        //the offset:
        //when moving right, no offset is needed, since we can orient the 2-tile-wide animation
        //block at our current position, and the coordinate specifies the left side, and we move left to right like this:
        //DUDE
        //[X--->-->]
        //however, for the left, we are moving from right to left, but the coordinate still specifies the left side, so we have this:
        //DUDE
        //[<---<--X]
        //which will shoot the guy a tile forward and then pull him back, but what we need is this:
        //      DUDE
        //[<---<--X]
        //so the animation needs to get offset 1 to the left
        int xOffset;

        if (orientation == -1) {
            dudeAnimation = this.left;
            rockAnimation = this.rockLeft;
            xOffset=1;
        } else if (orientation == 1) {
            dudeAnimation = this.right;
            rockAnimation = this.rockRight;
            xOffset=0;
        } else {
            dudeAnimation = this.left;
            rockAnimation = this.rockLeft;
            xOffset=1;
        }

        int frame = MainActivity.theDude.getFrame();
        canvas.drawBitmap(dudeAnimation[frame], (x - xMin - xOffset) * Dude.xScale, (y - yMin) * Dude.yScale, null);
        //also draw the block if we're holding it
        if (MainActivity.theDude.holdingRock) {
            canvas.drawBitmap(rockAnimation[frame], (x - xMin - xOffset) * Dude.xScale, (y - yMin - 1 /*-1 above the head */) * Dude.yScale, null);
        }
    }

    private void _animateDudeUpOrDown(Canvas canvas) {

    }

    private void _animateStepUp(Canvas canvas) {

    }

    private void _animateStepDown(Canvas canvas) {

    }
}
