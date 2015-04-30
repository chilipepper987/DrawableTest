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
    private Bitmap[] right, left, rockRight, rockLeft, walkLeft, walkRight, jumpLeft, jumpRight, blockJumpLeft, blockJumpRight;
    ;
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
        this.block = BitmapFactory.decodeResource(getResources(), R.mipmap.blox);
        this.goal = BitmapFactory.decodeResource(getResources(), R.mipmap.flag);
        this.dudeLeft = BitmapFactory.decodeResource(getResources(), R.mipmap.dude_left);
        this.dudeRight = BitmapFactory.decodeResource(getResources(), R.mipmap.dude_right);
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

        Bitmap walkLeft[] = {
                BitmapFactory.decodeResource(getResources(), R.mipmap.walk_left_0),
                BitmapFactory.decodeResource(getResources(), R.mipmap.walk_left_1),
                BitmapFactory.decodeResource(getResources(), R.mipmap.walk_left_2),
                BitmapFactory.decodeResource(getResources(), R.mipmap.walk_left_3),
                BitmapFactory.decodeResource(getResources(), R.mipmap.walk_left_4),
                BitmapFactory.decodeResource(getResources(), R.mipmap.walk_left_5),
                BitmapFactory.decodeResource(getResources(), R.mipmap.walk_left_6),
                BitmapFactory.decodeResource(getResources(), R.mipmap.walk_left_7),
                BitmapFactory.decodeResource(getResources(), R.mipmap.walk_left_8)
        };

        Bitmap walkRight[] = {
                BitmapFactory.decodeResource(getResources(), R.mipmap.walk_right_0),
                BitmapFactory.decodeResource(getResources(), R.mipmap.walk_right_1),
                BitmapFactory.decodeResource(getResources(), R.mipmap.walk_right_2),
                BitmapFactory.decodeResource(getResources(), R.mipmap.walk_right_3),
                BitmapFactory.decodeResource(getResources(), R.mipmap.walk_right_4),
                BitmapFactory.decodeResource(getResources(), R.mipmap.walk_right_5),
                BitmapFactory.decodeResource(getResources(), R.mipmap.walk_right_6),
                BitmapFactory.decodeResource(getResources(), R.mipmap.walk_right_7),
                BitmapFactory.decodeResource(getResources(), R.mipmap.walk_right_8)
        };

        Bitmap jumpRight[] = {
                BitmapFactory.decodeResource(getResources(), R.mipmap.jump_right_0),
                BitmapFactory.decodeResource(getResources(), R.mipmap.jump_right_1),
                BitmapFactory.decodeResource(getResources(), R.mipmap.jump_right_2),
                BitmapFactory.decodeResource(getResources(), R.mipmap.jump_right_3),
                BitmapFactory.decodeResource(getResources(), R.mipmap.jump_right_4),
                BitmapFactory.decodeResource(getResources(), R.mipmap.jump_right_5),
                BitmapFactory.decodeResource(getResources(), R.mipmap.jump_right_6)
        };

        Bitmap jumpLeft[] = {
                BitmapFactory.decodeResource(getResources(), R.mipmap.jump_left_0),
                BitmapFactory.decodeResource(getResources(), R.mipmap.jump_left_1),
                BitmapFactory.decodeResource(getResources(), R.mipmap.jump_left_2),
                BitmapFactory.decodeResource(getResources(), R.mipmap.jump_left_3),
                BitmapFactory.decodeResource(getResources(), R.mipmap.jump_left_4),
                BitmapFactory.decodeResource(getResources(), R.mipmap.jump_left_5),
                BitmapFactory.decodeResource(getResources(), R.mipmap.jump_left_6)
        };

        Bitmap blockJumpRight[] = {
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_jump_right_0),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_jump_right_1),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_jump_right_2),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_jump_right_3),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_jump_right_4),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_jump_right_5),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_jump_right_6)

        };

        Bitmap blockJumpLeft[] = {
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_jump_left_0),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_jump_left_1),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_jump_left_2),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_jump_left_3),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_jump_left_4),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_jump_left_5),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_jump_left_6)
        };


        this.walkLeft = walkLeft;
        this.walkRight = walkRight;
        this.jumpLeft = jumpLeft;
        this.jumpRight = jumpRight;
        this.blockJumpLeft = blockJumpLeft;
        this.blockJumpRight = blockJumpRight;

        this.left = left;
        this.right = right;

        Bitmap rockLeft[] = {
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_left_0),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_left_1),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_left_2),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_left_3),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_left_4),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_left_5),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_left_6),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_left_7),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_left_8)
        };

        /*
        //left has the animation specified from left to right.
        //since the block looks the same on either side, for now we can just reverse the left and call it right.
        //if we want to change the animation later, we can, and the code referencing rockRight will not have to be adjusted.
        //ArrayUtils lets us reverse easily.
        Bitmap rockRight[];
        rockRight = rockLeft.clone();
        ArrayUtils.reverse(rockRight);
        */

        Bitmap rockRight[] = {
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_right_0),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_right_1),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_right_2),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_right_3),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_right_4),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_right_5),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_right_6),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_right_7),
                BitmapFactory.decodeResource(getResources(), R.mipmap.block_right_8)
        };

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
                if (MainActivity.theDude.verticalOrientation == 0) {
                    _animateDudeLeftOrRight(canvas);
                } else {
                    _animateStepUp(canvas);
                }
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

            for (int i = 0; i < walkLeft.length; i++) {
                //is 2x wide
                walkLeft[i] = HelperLib.getResizedBitmap(walkLeft[i], Dude.xScale * 2, Dude.yScale);
            }

            for (int i = 0; i < walkRight.length; i++) {
                //is 2x wide
                walkRight[i] = HelperLib.getResizedBitmap(walkRight[i], Dude.xScale * 2, Dude.yScale);
            }

            for (int i = 0; i < jumpLeft.length; i++) {
                //is 2x wide, 2x tall
                jumpLeft[i] = HelperLib.getResizedBitmap(jumpLeft[i], Dude.xScale * 2, Dude.yScale*2);
            }

            for (int i = 0; i < jumpRight.length; i++) {
                //is 2x wide, 2x tall
                jumpRight[i] = HelperLib.getResizedBitmap(jumpRight[i], Dude.xScale * 2, Dude.yScale*2);
            }

            for (int i = 0; i < blockJumpLeft.length; i++) {
                //is 2x wide, 2x tall
                blockJumpLeft[i] = HelperLib.getResizedBitmap(blockJumpLeft[i], Dude.xScale * 2, Dude.yScale*2);
            }

            for (int i = 0; i < blockJumpRight.length; i++) {
                //is 2x wide, 2x tall
                blockJumpRight[i] = HelperLib.getResizedBitmap(blockJumpRight[i], Dude.xScale * 2, Dude.yScale*2);
            }


        } else {
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
        xMax = xMin + 23 >= mapWidth ? mapWidth-1 : xMin + 23;
        yMax = yMin + 7 >= mapHeight ? mapHeight-1 : yMin + 7;
        //Log.d("start drawing", "now");
        for (int i = xMin; i < xMax; i++) {

            for (int j = yMin; j < yMax; j++) {
                //Log.d("loop", "");
                int tileNumber=0;
                try {
                    tileNumber = map.getMap()[j][i].getValue();
                }
                catch(Exception ex) {
                    Log.e("what happened?",j+","+i);
                    //Log.e("strMap",Arrays.deepToString(map.strMap));
                    Log.e("yMax",""+yMax);
                }

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
        //Log.d("stop", "drawing");
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
            dudeAnimation = this.walkLeft;
            rockAnimation = this.rockLeft;
            xOffset = 1;
        } else if (orientation == 1) {
            dudeAnimation = this.walkRight;
            rockAnimation = this.rockRight;
            xOffset = 0;
        } else {
            dudeAnimation = this.walkLeft;
            rockAnimation = this.rockLeft;
            xOffset = 1;
        }

        int frame = MainActivity.theDude.getFrame();
        frame = (frame > dudeAnimation.length - 1) ? dudeAnimation.length - 1 : frame;
        canvas.drawBitmap(dudeAnimation[frame], (x - xMin - xOffset) * Dude.xScale, (y - yMin) * Dude.yScale, null);
        //also draw the block if we're holding it
        if (MainActivity.theDude.holdingRock) {
            canvas.drawBitmap(rockAnimation[frame], (x - xMin - xOffset) * Dude.xScale, (y - yMin - 1 /*-1 above the head */) * Dude.yScale, null);
        }
    }

    private void _animateDudeUpOrDown(Canvas canvas) {

    }

    private void _animateStepUp(Canvas canvas) {
        int x = MainActivity.theDude.x;
        int y = MainActivity.theDude.y;

        Bitmap[] dudeAnimation, rockAnimation;
        int orientation = MainActivity.theDude.orientation;

        int xOffset;

        if (orientation == -1) {
            dudeAnimation = this.jumpLeft;
            rockAnimation = this.blockJumpLeft;
            xOffset = 1;
        } else if (orientation == 1) {
            dudeAnimation = this.jumpRight;
            rockAnimation = this.blockJumpRight;
            xOffset = 0;
        } else {
            dudeAnimation = this.jumpLeft;
            rockAnimation = this.blockJumpLeft;
            xOffset = 1;
        }

        int frame = MainActivity.theDude.getFrame();
        frame = (frame > dudeAnimation.length - 1) ? dudeAnimation.length - 1 : frame;
        //                                                                           so.. the -1 i needed here because the coordinate is the top left, not bottom left I guess?
        canvas.drawBitmap(dudeAnimation[frame], (x - xMin - xOffset) * Dude.xScale, (y - yMin - 1) * Dude.yScale, null);
        //also draw the block if we're holding it
        if (MainActivity.theDude.holdingRock) {
            canvas.drawBitmap(rockAnimation[frame], (x - xMin - xOffset) * Dude.xScale, (y - yMin - 2 /*-1 above the head */) * Dude.yScale, null);
        }
    }

    private void _animateStepDown(Canvas canvas) {

    }
}
