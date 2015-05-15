package com.example.seth.drawabletest;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    public static Dude theDude;
    public static String statusText = "";
    private static MyView gameArea;
    private static TextView status;
    public static Context context;
    public static boolean showTitleScreen = true;
    public static TileMap titleScreen;

    private static boolean up, left, right;

    private int levelNumber;

    //http://stackoverflow.com/questions/12071090/triggering-event-continuously-when-button-is-pressed-down-in-android

    private Handler repeatUpdateHandler = new Handler();
    //a left/right animation is 500 milis so
    private final long REPEAT_DELAY = 40;

    public static final int GRID_HEIGHT = 8;
    public static final int GRID_WIDTH = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();


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
        _init(1);
    }

    private void _init(int levelNumber) {
        //set up the game

        //title screen
        //titleScreen = new TileMap(levelNumber, "title");
        this.levelNumber = levelNumber;

        //our hero, the block dude!
        theDude = new Dude(new TileMap(levelNumber));

        //grab the view from the xml
        gameArea = (MyView) findViewById(R.id.canvas);

        //and the status box
        status = (TextView) findViewById(R.id.textStatus);
        //clear any messages that might be in the status box
        status.setText("Howdy!");

        //assign handlers to the control buttons
        ImageButton W = (ImageButton) findViewById(R.id.buttonW);
        ImageButton A = (ImageButton) findViewById(R.id.buttonA);
        //Button S = (Button) findViewById(R.id.buttonS);
        ImageButton D = (ImageButton) findViewById(R.id.buttonD);
        ImageButton Rock = (ImageButton) findViewById(R.id.buttonRock);
        ImageButton RockAlt = (ImageButton) findViewById(R.id.buttonRockAlt);

        ImageButton retry = (ImageButton) findViewById(R.id.buttonRetry);


        Spinner spinner = (Spinner) findViewById(R.id.spinner);


        //sample code, this will be a level-select drop down
        String[] items = {"Level Select", "1", "2", "3", "4","5","6","7","8","9"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //status.setText((String) spinner.getSelectedItem());
                int level = 0;
                try {
                    level = Integer.parseInt((String) spinner.getSelectedItem());
                } catch (Exception ex) {
                    //who cares, couldn't parse
                    level = 0;
                }
                if (level > 0) {
                    _init(level);
                    reDraw();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });
        //runnable for touch events so we can "hold down" a button, works like a setInterval where the interval is killed upon release

        class RepetitiveUpdater implements Runnable {

            @Override
            public void run() {
                if (MainActivity.left) {
                    _goLeft();
                    repeatUpdateHandler.postDelayed(new RepetitiveUpdater(), REPEAT_DELAY);
                } else if (MainActivity.right) {
                    _goRight();
                    repeatUpdateHandler.postDelayed(new RepetitiveUpdater(), REPEAT_DELAY);
                }
            }

        }


        //movement event:
        //we need three events to handle movement:
        //#1 - click (or down?) event: when the button is clicked, trigger the action a single time
        //#2 - "long click" event: when the button is held down, start a loop to continuously do the action until...
        //#3 - release "touch up" event: at this time, cancel any action


        //A is the left button son so
        A.setOnTouchListener((view, event) -> {
                    //concurrency...
                    //do nothing if we are currently in the middle of an animation...otherwise we might be able to
                    //walk thru a wall


                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        //cancel event
                        MainActivity.left = false;
                        MainActivity.right = false;
                        MainActivity.up = false;
                    }

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        //trigger once
                        _goLeft();
                    }
                    return false;
                }
        );

        A.setOnLongClickListener((view) -> {
            //start continuous event
            MainActivity.left = true;
            MainActivity.right = false;
            MainActivity.up = false;
            repeatUpdateHandler.post(new RepetitiveUpdater());
            return false;
        });


        //D is the right button
        D.setOnTouchListener((view, event) -> {

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        //cancel event
                        MainActivity.left = false;
                        MainActivity.right = false;
                        MainActivity.up = false;

                    }

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        //trigger once
                        _goRight();
                    }

                    return false;
                }
        );

        D.setOnLongClickListener((view) -> {
            MainActivity.left = false;
            MainActivity.right = true;
            MainActivity.up = false;
            repeatUpdateHandler.post(new RepetitiveUpdater());
            return false;
        });

        //                                                      **
        //                                                    ||[]
        //W is the up button. press up to go up a stair, like:[][]
        W.setOnClickListener((v) ->

                {
                    //concurrency...
                    //do nothing if we are currently in the middle of an animation...otherwise we might be able to
                    //walk thru a wall
                    if (theDude.animating == false) {
                        theDude.moveUp();
                        reDraw();
                    }
                }

        );


        View.OnClickListener rockListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (theDude.holdingRock) {
                    //then we're trying to drop it
                    theDude.dropRock();
                } else {
                    //then we're trying to pick one up
                    theDude.pickUpRock();
                }
                reDraw();
            }
        };

        Rock.setOnClickListener(rockListener);
        RockAlt.setOnClickListener(rockListener);

        retry.setOnClickListener((v) ->

                {
                    reDraw();
                    _init(levelNumber);
                    reDraw();
                }

        );

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

    private void _goLeft() {
        if (theDude.animating == false) {
            MainActivity.left = true;
            MainActivity.right = false;
            MainActivity.up = false;
            // vertical orientation should start at 0
            theDude.verticalOrientation = 0;
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
        }
    }

    private void _goRight() {
        //concurrency...
        //do nothing if we are currently in the middle of an animation...otherwise we might be able to
        //walk thru a wall
        if (theDude.animating == false) {

            // vertical orientation should start at 0
            theDude.verticalOrientation = 0;
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
        }
    }
}
