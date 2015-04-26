package com.example.seth.drawabletest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.HashMap;

public class MyView extends View {
    private Bitmap b, block, goal, dudeLeft, dudeRight, wall;
    private Bitmap[] right;
    private Bitmap[] left;
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
        this.goal = BitmapFactory.decodeResource(getResources(), R.mipmap.goal);
        this.dudeLeft = BitmapFactory.decodeResource(getResources(), R.mipmap.barnheadleft);
        this.dudeRight = BitmapFactory.decodeResource(getResources(), R.mipmap.barnheadright);
        this.wall = BitmapFactory.decodeResource(getResources(), R.mipmap.wall);

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
    }

    public void drawingDude() {
        drawingGuy = true;
    }

    public void drawingLevel() {
        drawingGuy = false;
    }

    //THIS is why all 3 constructors need to be provided: so this can be used
    //DIRECTLY in the xml! via <view class="com.example.seth.drawabletest.MyView" //...

    @Override
    protected void onDraw(Canvas canvas) {
        {


            // TODO Auto-generated method stub
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

            //resize the bitmap for each each frame in the animation arrays
            for (int i=0;i<left.length;i++) {
                //is 2x wide
                left[i]=HelperLib.getResizedBitmap(left[i],Dude.xScale*2,Dude.yScale);
            }

            for (int i=0;i<right.length;i++) {
                //is 2x wide
                right[i]=HelperLib.getResizedBitmap(right[i],Dude.xScale*2,Dude.yScale);
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
        xMax = xMin + 21 > mapWidth ? mapWidth - xMin : xMin + 21;
        yMax = yMin + 6 > mapHeight ? mapHeight - yMin : yMin + 6;
        Log.d("start drawing", "now");
        for (int i = xMin; i < xMax; i++) {

            for (int j = yMin; j < yMax; j++) {
                //Log.d("loop", "");
                int tileNumber = map.getMap()[j][i].getValue();
                if (tileNumber < 1 || tileNumber > 3) {
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
                    default: {
                        draw = false;
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

        Bitmap[] dudeAnimation;
        String orientation = MainActivity.theDude.getOrientation();
        if (orientation == "left") {
            dudeAnimation = this.left;
            x++; //set x to its previous value
        } else if (orientation == "right") {
            dudeAnimation = this.right;
            x--; //set x to its previous value
        } else {
            dudeAnimation = this.left;
            x++; //set x to its previous value
        }

        int frame = MainActivity.theDude.getFrame();
        canvas.drawBitmap(dudeAnimation[frame], (x - xMin) * Dude.xScale, (y - yMin) * Dude.yScale, null);
    }
}
