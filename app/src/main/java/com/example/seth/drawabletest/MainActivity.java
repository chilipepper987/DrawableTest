package com.example.seth.drawabletest;

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
    public static String statusText="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d("a", "a");

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

        //assign handlers to the control buttons
        Button W = (Button) findViewById(R.id.buttonW);
        Button A = (Button) findViewById(R.id.buttonA);
        //Button S = (Button) findviewbyid(r.id.buttonS);
        Button D = (Button) findViewById(R.id.buttonD);
        Button Rock = (Button) findViewById(R.id.buttonRock);

        Button retry = (Button) findViewById(R.id.buttonRetry);

        //A is the left button so
        A.setOnClickListener((v) -> {
            //we always "turn" the sprite, regardless of whether or not it moved
            theDude.turnLeft();
            theDude.moveLeft();
            _reDraw();
        });

        //D is the right button
        D.setOnClickListener((v) -> {
            //we always "turn" the sprite, regardless of whether or not it moved
            theDude.turnRight();
            theDude.moveRight();
            _reDraw();
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
            _reDraw();
        });

        Rock.setOnClickListener((v) -> {
            if (theDude.holdingRock) {
                //then we're trying to drop it
                theDude.dropRock();
            } else {
                //then we're trying to pick one up
                theDude.pickUpRock();
            }
            _reDraw();
        });

        retry.setOnClickListener((v) -> {
            _reDraw();
            _init();
            _reDraw();
        });

    }

    private void _reDraw() {
        MyView gameArea = (MyView) findViewById(R.id.canvas);

        gameArea.invalidate();

        //and check the game state

        //status
        TextView status = (TextView) findViewById(R.id.textStatus);
        status.setText(statusText);

        if (MainActivity.theDude.map.getMap()[theDude.y][theDude.x].isGoal()) {

            status.setText("You Win!");
                    /*
            SystemClock.sleep(1000);
            MainActivity.theDude.map.getMap()[theDude.y][theDude.x - 1] = new Tile(1);
            MainActivity.theDude.map.getMap()[theDude.y][theDude.x - 2] = new Tile(1);
            MainActivity.theDude.map.getMap()[theDude.y][theDude.x - 3] = new Tile(1);
            gameArea.invalidate();
            SystemClock.sleep(5000);
            _init();*/
            return;
        }
        return;/*
        gameArea.drawingDude();
        gameArea.lastX = (float) Math.round(gameArea.lastX);
        gameArea.lastY = (float) Math.round(gameArea.lastY);
        float xStep = (theDude.x - gameArea.lastX) / 3;
        float yStep = (theDude.y - gameArea.lastY) / 3;
        gameArea.xStep=xStep;
        gameArea.yStep=yStep;

        for (int i=0;i<1;i++) {
            gameArea.lastX+=xStep;
            gameArea.lastY+=yStep;
            SystemClock.sleep(1000);
            gameArea.invalidate();

        }
        */
    }
}
