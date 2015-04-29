package com.example.seth.drawabletest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.Log;
import android.widget.TextView;

import java.util.MissingFormatArgumentException;

/**
 * Created by Seth on 4/12/2015.
 */
public class Dude {
    public int x = 0;
    public int y = 0;
    public static int xScale = 0;
    public static int yScale = 0;
    public int orientation = 1;
    public int verticalOrientation;
    public boolean wentUp = false;
    public TileMap map;
    public boolean holdingRock = false;

    public boolean animating = false;
    public boolean animatingBlock = false;
    public String animatingSprite = "";

    private static AnimatorListenerAdapter animationListener;

    private int frame = 0;

    public Dude(TileMap t) {
        //the dude needs to be constructed with a map
        this.map = t;
        //and the map also gets the dude
        this.map.setDude(this);
        //get the starting x,y

        //get the width and height of the map
        int width = map.getMap()[0].length;
        int height = map.getMap().length;
        //say our screen is a 7x7 grid. so this means, if we are closer than 5 from the left edge, then
        //the left side of the screen will be the left end of the map. if we are further than 4, then
        //the map needs to scroll 1 to the right for each step that we are in excess of 4, so that the player
        //can start at the center of the screen. for the vertical direction, we want to start the level as close
        //to the ground as possible. if the player is less than 7 from the bottom, start the level at the bottom.
        //otherwise, move the level down for each 1 unit over 7 from the bottom.
        //additionally, a map might specify how it wants to be scrolled if this initial calculation makes
        //it look shitty.

        //actually our screen is 6x21 ceil(21/2)=12;
        for (int i = 0; i < map.getStartX() - 12; i++) {
            //so this does nothing if getStartX<=12
            map.advanceRight();
        }
        for (int i = 0; i < map.getStartY() - 6; i++) {
            map.advanceUp();
        }
        //now place the dude in the starting position
        this.x = map.getStartX();
        this.y = map.getStartY();

        _initAnimatorListenerAdaptor();
    }

    private void _initAnimatorListenerAdaptor() {
        animationListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //we are done animating
                Dude theDude = MainActivity.theDude;
                theDude.animating = false;
                //advance the dudes x/y in the direction we just walked
                theDude.x += theDude.orientation;
                if (theDude.verticalOrientation != 0) {
                    theDude.y -= verticalOrientation; //move up //-- is up, + is down, but orientation is positive if up, negative if down...
                    //reset vertical orientation
                    //set vertical orientation. we just moved up

                }


                //do we need to fall?
                int dropped = theDude.checkGround();
                //now we have fallen, so put the rock back on top of the dude
                //this.map.placeRock(this.x,this.y-1);
                //finally, advance the map
                if (dropped > 0) {
                    MainActivity.statusText = "Watch your step!";
                }
                int width = theDude.map.getMap()[0].length;
                int height = theDude.map.getMap().length;
                //first, do we need to advance left or right
                //if we have just advanced, and the edge of the map is not showing, then we need to shift
                //middle of level                              left side, wall not showing yet                        right side, wall not showing yet
                if (theDude.x > 12 && theDude.x < width - 10 || theDude.x <= 12 && theDude.map.getOffsetX() > 0 /*|| theDude.x >= width - 8 && theDude.map.getOffsetX() < width - 7*/) {
                    theDude.map.advanceX();
                }
                //if we went up or down, scroll accordingly
                Log.d("went up?", "" + theDude.wentUp + "|");
                if (dropped > 0 || theDude.verticalOrientation != 0) {
                    //if (theDude.y > 3 && theDude.y < height || theDude.y <= 4 && theDude.map.getOffsetY() > 4 || theDude.y > height - 6 && theDude.map.getOffsetY() < height - 3) {
                    //if dropped > 0)  then we need to do this multiple times, one for each level dropped
                    int iterations = dropped > 0 ? dropped : 1;

                    for (int i = 0; i < iterations; i++) {
                        //in the middle
                        //                       on the top, can't see ceiling                         on bottom, can't see floor
                        Log.i("y,height,offsetY",theDude.y+", "+height+", "+theDude.map.getOffsetY());
                        if (theDude.y > 3 && theDude.y < height - 7 || theDude.y <= 4 && theDude.map.getOffsetY() > 1 /*|| theDude.y > height - 7 && theDude.map.getOffsetY() < height - 6*/) {
                            Log.d("advance", "Y");
                            theDude.map.advanceY();
                        }
                        if (theDude.verticalOrientation == 1) {
                            Log.d("tried to go up.", "");
                        }
                    }
                    //reset vertical orientation
                    theDude.verticalOrientation = 0;
                }
                theDude.wentUp = false;

                Log.d("theDude.x / y", "" + theDude.x + "," + theDude.y);
                Log.d("map offset", "" + map.getOffsetX() + "," + map.getOffsetY());
                Log.d("width x height", "" + width + " x " + height);
                Log.d("", "");
                if (dropped > 0)

                {
                    //then we have dropped. advance the map
                    //..
                }


                //finally redraw
                MainActivity.reDraw();
            }
        };
    }

    public String getOrientation() {
        return orientation > 0 ? "right" : "left";
    }

    public void turnLeft() {
        this.orientation = -1;
    }

    public void turnRight() {
        this.orientation = 1;
    }

    public boolean moveLeft() {
        return _moveX();
    }

    public boolean moveRight() {
        return _moveX();
    }

    /**
     * move in the x direction.
     *
     * @return
     */

    public int setFrame(int frame) {
        this.frame = frame;
        return frame;
    }

    public int getFrame() {
        return this.frame;
    }


    private boolean _moveX() {
        int direction = this.orientation;
        boolean mayBeAbleToMove, moved, dudeWillHitWall, rockWillHitWall;
        moved = false;
        String statusText = "";

        if (direction == 1) {
            mayBeAbleToMove = this.x < this.map.getMap()[0].length;
        } else {
            mayBeAbleToMove = this.x > 0;
        }
        if (mayBeAbleToMove) {
            //then he might be able to move. is he about to hit a wall?
            dudeWillHitWall = this.map.getMap()[this.y][this.x + direction].isSolid();
            //if he is holding a block, is the block about to hit a wall?
            if (this.holdingRock) {
                rockWillHitWall = this.map.getMap()[this.y - 1][this.x + direction].isSolid();
            } else {
                rockWillHitWall = false;
            }

            if (dudeWillHitWall || rockWillHitWall) {
                //then there is a wall directly to the left or right of him
                // (and / or the block)
                // so don't move
                statusText = "That's a wall.";
            } else {
                //otherwise, we are safe
                if (this.holdingRock) {
                    //advance the rock
                    //remove it for now, then replace it once the dude has landed
                    this.map.removeRock(this.y - 1, this.x);
                }
                moved = true;
            }
        } else {
            statusText = "That's a wall.";
        }
        if (moved) {

            //hmmm, async animate
            _animateX();

            //execution continues in the animation callback


        }
        MainActivity.statusText = statusText;
        return moved;
    }

    public boolean moveUp() {
        // vertical orientation should start at 0
        this.verticalOrientation = 0;
        //don't turn the sprite to move up.
        //we always move in the direction we are already facing
        //get the orientation
        int direction = this.orientation;
        boolean moved, blockAdjacent, spaceAboveBlock, spaceAboveDude,
                extraSpaceAboveDude, extraSpaceAboveBlock;
        String statusText = "";
        moved = false;
        //we can only move up if there is a block directly next, and a space directly above it

        if (_mayBeAbleToMove(true)) {
            //then maybe we can move
            //since we are doing a "jump", we need there to be a block next to us,
            //a space above us,
            //and a space above the block
            blockAdjacent = this.map.getMap()[this.y][this.x + direction].isSolid();
            spaceAboveBlock = this.map.getMap()[this.y - 1][this.x + direction].isSpace();
            spaceAboveDude = this.map.getMap()[this.y - 1][this.x].isSpace();
            if (this.holdingRock) {
                //we need extra room if we are holding a block above our head
                extraSpaceAboveDude = this.map.getMap()[this.y - 2][this.x].isSpace();
                extraSpaceAboveBlock = this.map.getMap()[this.y - 2][this.x + direction].isSpace();
            } else {
                //we don't care if there is extra room
                extraSpaceAboveBlock = true;
                extraSpaceAboveDude = true;
            }

            //if all conditions are met
            if (blockAdjacent &&
                    spaceAboveBlock &&
                    spaceAboveDude &&
                    extraSpaceAboveBlock &&
                    extraSpaceAboveDude) {
                //then we can move so
                this.verticalOrientation = 1;
                if (this.holdingRock) {
                    this.map.removeRock(this.y - 1, this.x);
                }
                moved = true;
            }
        } else {
            statusText = "That's a wall.";
        }
        if (moved) {
            //async animate
            _animateY();
        }
        MainActivity.statusText = statusText;
        return moved;
    }

    private boolean _mayBeAbleToMove() {
        boolean mayBeAbleToMove;
        if (this.orientation == 1) {
            mayBeAbleToMove = this.x < this.map.getMap()[0].length;
        } else {
            mayBeAbleToMove = this.x > 0;
        }
        return mayBeAbleToMove;
    }

    private boolean _mayBeAbleToMove(Boolean up) {
        if (up == true) {
            int yMin;
            if (this.holdingRock) {
                yMin = 1;
            } else {
                yMin = 0;
            }
            return _mayBeAbleToMove() && this.y > yMin;
        } else {
            return _mayBeAbleToMove();
        }
    }

    /**
     * After moving left or right, if there is nothing under the dude, he needs to fall to the
     * ground, so bring the dudes y down until we find solid ground
     *
     * @return
     */
    public int checkGround() {
        int dropped = 0;
        while (this.y < this.map.getMap().length - 1 && this.map.getMap()[this.y + 1][this.x].isSpace()) {
            //drop down one
            this.y++;
            dropped++;
            this.verticalOrientation = -1;
        }
        return dropped;
    }

    public boolean pickUpRock() {
        //cant pick up the rock if were already holding one
        if (this.holdingRock) {
            return false;
        }

        //ok, maybe we can pick up a rock.
        //in order to do so, the following must be true:
        // #1 There must be a rock in front of us
        boolean rockAvailable = this.map.getMap()[this.y][this.x + this.orientation].isRock();
        // #2 The space above the rock must be clear
        boolean rockClear = this.map.getMap()[this.y - 1][this.x + this.orientation].isSpace();
        // # The space above the dude must be clear
        boolean dudeClear = this.map.getMap()[this.y - 1][this.x].isSpace();
        String statusText = "";
        if (rockAvailable && rockClear && dudeClear) {
            // then we can pick up a rock!
            if (map.removeRock(this.y, this.x + this.orientation)) {
                //map.placeRock(this.y - 1, this.x);
                //rock placement should happen on redraw, not here
                this.holdingRock = true;
                MainActivity.statusText = "";
                return true;
            } else {
                //???
            }
        } else {
            if (!rockAvailable) {
                statusText = "No rock to pick up.";
            } else {
                statusText = "Can't pick up that rock.";
            }
        }
        MainActivity.statusText = statusText;
        return false;
    }

    public boolean dropRock() {
        //cant drop a rock if were not holding one

        if (!this.holdingRock) {
            return false;
        }

        //ok, maybe we can drop up a rock.
        //in order to do so, the following must be true:
        // #1 There must be a space in front of us
        boolean spaceAvailable = this.map.getMap()[this.y][this.x + this.orientation].isSpace();
        // #2 It must be clear above the space in front of us
        boolean spaceClear = this.map.getMap()[this.y - 1][this.x + this.orientation].isSpace();


        if (spaceAvailable && spaceClear) {
            // then we can drop a rock!
            map.placeRock(this.y, this.x + this.orientation);
            this.holdingRock = false;
            MainActivity.statusText = "";
            return true;
        }
        MainActivity.statusText = "Can't drop rock there.";
        return false;
    }

    private void _animateX() {
        //we are animating the dude
        this.animating = true;
        if (this.holdingRock) {
            //then we are also animating the block
            this.animatingBlock = true;
        }
        //new value animator. we are animating frames 0-6
        ValueAnimator va = ValueAnimator.ofInt(0, 8);
        //set duration in millis.
        va.setDuration(500);
        //what to do for each frame? increment the frame counter,
        //and redraw
        va.addUpdateListener((animation) -> {
            int frame = (int) animation.getAnimatedValue();
            this.setFrame(frame);
            MainActivity.reDraw();
        });
        //what to do when finished? redraw a final time
        //cant use a lambda here since there are multiple
        //methods that could be passed here, so we have
        //to use an anonymous class to specify it directly
        va.addListener(
                animationListener
        );
        //start the animation!
        va.start();
    }

    private void _animateY() {
        //we are animating the dude
        this.animating = true;
        if (this.holdingRock) {
            //then we are also animating the block
            this.animatingBlock = true;
        }

        //new value animator, the jump is 15 frames
        ValueAnimator va = ValueAnimator.ofInt(0, 14);
        va.setDuration(1000);
        va.addUpdateListener((animation) -> {
            this.setFrame((int) animation.getAnimatedValue());
            MainActivity.reDraw();
        });

        va.addListener(animationListener);
        va.start();

    }

}
