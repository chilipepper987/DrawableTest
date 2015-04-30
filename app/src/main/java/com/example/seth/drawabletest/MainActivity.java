package com.example.seth.drawabletest;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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


        //our hero, the block dude!
        theDude = new Dude(new TileMap(levelNumber));

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

        Spinner spinner = (Spinner) findViewById(R.id.spinner);


        //sample code, this will be a level-select drop down
        String[] items = {"Level Select","1", "2", "3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                status.setText((String) spinner.getSelectedItem());
                int level=0;
                try {
                    level = Integer.parseInt((String) spinner.getSelectedItem());
                } catch(Exception ex) {
                    //who cares, couldn't parse
                    level=0;
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

        Button retry = (Button) findViewById(R.id.buttonRetry);

        //A is the left button so
        A.setOnClickListener((v) -> {
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
                theDude.turnLeft();

                if (theDude.getOrientation() == previousOrientation) {
                    //then we were already facing this way, so get moving!
                    theDude.moveLeft();
                }
                reDraw();
            }
        });

        //D is the right button
        D.setOnClickListener((v) -> {

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
        });
        //                                                      **
        //                                                    ||[]
        //W is the up button. press up to go up a stair, like:[][]
        W.setOnClickListener((v) -> {
            //concurrency...
            //do nothing if we are currently in the middle of an animation...otherwise we might be able to
            //walk thru a wall
            if (theDude.animating == false) {
                theDude.moveUp();
                reDraw();
            }
        });

        Rock.setOnClickListener((v) -> {
            if (theDude.animating == false) {
                if (theDude.holdingRock) {
                    //then we're trying to drop it
                    theDude.dropRock();
                } else {
                    //then we're trying to pick one up
                    theDude.pickUpRock();
                }
                reDraw();
            }
        });

        retry.setOnClickListener((v) -> {
            reDraw();
            _init(3);
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
