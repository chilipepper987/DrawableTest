package com.example.seth.drawabletest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends ActionBarActivity {

    public static Dude theDude;
    public static String statusText = "";
    private static MyView gameArea;
    private static TextView status;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d("a", "a");
        context=this.getApplicationContext();

        //Log.i("length", ""+f.length());

        //setContentView(new MyView(this));
        setContentView(R.layout.activity_main);/*
        //set up a view with a canvas on it
        MyView MyView = new MyView(this);
        //grab the layout
        TableRow tr = (TableRow) findViewById(R.id.row1);
        //add the view to the layout
        tr.addView(MyView);
        //MyView.setLayoutParams(new LinearLayout.LayoutParams(800,1300));
        */
        _init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void _init() {
        //set up the game



        //our hero, the block dude!
        theDude = new Dude(new TileMap());

        //grab the view from the xml
        gameArea = (MyView) findViewById(R.id.canvas);

        //and the status box
        status = (TextView) findViewById(R.id.textStatus);
        //clear any messages that might be in the status box
        status.setText("Howdy!");

        //assign handlers to the control buttons
        Button W = (Button) findViewById(R.id.buttonW);
        Button A = (Button) findViewById(R.id.buttonA);
        Button S = (Button) findViewById(R.id.buttonS);
        Button D = (Button) findViewById(R.id.buttonD);
        Button Rock = (Button) findViewById(R.id.buttonRock);

        Button retry = (Button) findViewById(R.id.buttonRetry);

        //A is the left button so
        A.setOnClickListener((v) -> {
            //we always "turn" the sprite, regardless of whether or not it moved
            //we can turn without moving, so if the turn results in an orientation change, don't advance the dude
            //so store the orientation before turning, so we can know if it changed
            String previousOrientation = theDude.getOrientation();
            theDude.turnLeft();

            if (theDude.getOrientation() == previousOrientation) {
                //then we were already facing this way, so get moving!
                theDude.moveLeft();
            }
            reDraw();
        });

        S.setOnClickListener((v) -> {
            //try and animate the dude right


            //we are animating the dude right
            theDude.animating = true;
            theDude.animatingSprite = "right";
            theDude.x++;
            //new value animator. we are animating frames 0-6
            ValueAnimator va = ValueAnimator.ofInt(0, 6);
            //set duration in millis.
            va.setDuration(200);
            //what to do for each frame? increment the frame counter,
            //and redraw
            va.addUpdateListener((animation) -> {
                int frame = (int) animation.getAnimatedValue();
                theDude.setFrame(frame);
                reDraw();
            });
            //what to do when finished? redraw a final time
            //cant use a lambda here since there are multiple
            //methods that could be passed here, so we have
            //to use an anonymous class to specify it directly
            va.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    MainActivity.theDude.animating = false;
                    reDraw();
                }
            });
            //start the animation!
            va.start();

        });

        //D is the right button
        D.setOnClickListener((v) -> {
            //we always "turn" the sprite, regardless of whether or not it moved
            //we can turn without moving, so if the turn results in an orientation change, don't advance the dude
            //so store the orientation before turning, so we can know if it changed
            String previousOrientation = theDude.getOrientation();
            theDude.turnRight();

            if (theDude.getOrientation() == previousOrientation) {
                //then we were already facing this way, so get moving!
                theDude.moveRight();
            }
            reDraw();
        });
        //                                                      **
        //                                                    ||[]
        //W is the up button. press up to go up a stair, like:[][]
        W.setOnClickListener((v) -> {
            //don't turn the sprite to move up.
            //we always move in the direction we are already facing
            //get the orientation
            int direction;
            if (theDude.getOrientation() == "left") {
                direction = -1;
            } else {
                direction = 1;
            }
            //we can only move up if there is a block directly next, and a space directly above it
            if (
                //next to the dude is a block
                    theDude.map.getMap()[theDude.y][theDude.x + direction].isSolid() &&
                            //and there is space above the block
                            theDude.map.getMap()[theDude.y - 1][theDude.x + direction].isSpace()) {
                //then we can move so
                theDude.x += direction; //advance x
                theDude.y--; //move up
            }
            reDraw();
        });

        Rock.setOnClickListener((v) -> {
            if (theDude.holdingRock) {
                //then we're trying to drop it
                theDude.dropRock();
            } else {
                //then we're trying to pick one up
                theDude.pickUpRock();
            }
            reDraw();
        });

        retry.setOnClickListener((v) -> {
            reDraw();
            _init();
            reDraw();
        });

    }

    public static void reDraw() {

        gameArea.invalidate();

        //and check the game state
        //status
        status.setText(statusText);

        if (MainActivity.theDude.map.getMap()[theDude.y][theDude.x].isGoal()) {

            status.setText("You Win!");

        }
        return;
    }
}
