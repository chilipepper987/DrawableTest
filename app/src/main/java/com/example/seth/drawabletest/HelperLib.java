package com.example.seth.drawabletest;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by Seth on 4/17/2015.
 */
public class HelperLib {
    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    // reads resources regardless of their size
    public static byte[] getResource(int id, Context context) throws IOException {
        Resources resources = context.getResources();
        InputStream is = resources.openRawResource(id);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        byte[] readBuffer = new byte[4 * 1024];

        try {
            int read;
            do {
                read = is.read(readBuffer, 0, readBuffer.length);
                if (read == -1) {
                    break;
                }
                bout.write(readBuffer, 0, read);
            } while (true);

            return bout.toByteArray();
        } finally {
            is.close();
        }
    }

    // reads an UTF-8 string resource
    public static String getStringResource(int id) {
        try {
            return new String(getResource(id, MainActivity.context), Charset.forName("UTF-8"));
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public static Level readLevelFromFile(int levelNumber) {
        return HelperLib.readLevelFromFile(levelNumber,"levels");
    }

    public static Level readLevelFromFile(int levelNumber, String filename) {
        String contents="";
        if (filename=="levels") {
            contents = HelperLib.getStringResource(R.raw.levels);
        }
        if (filename=="title") {
            contents= HelperLib.getStringResource(R.raw.titlescreen);
        }

        //split on LEVELMARKER
        String[] levels = contents.split("LEVELMARKER");

        //lets get level 1
        //the file starts with the LEVELMARKER delimiter, meaning the first element is going to be empty, so this will
        //work out to be a 1-indexed array
        //read in a csv type file, (comma separated values w newlines)
        String[] rows = levels[levelNumber].split(System.getProperty("line.separator", "\r\n"));
        String[][] map = new String[rows.length][];
        int rowNumber = 0;
        for (String row : rows) {
            String[] rowArray = row.split(",");
            if (rowArray.length < 8) {
                //its a junk row, like [], skip it
                continue;
            } else {
                //trim it and add it
                for (int i = 0; i < rowArray.length; i++) {
                    rowArray[i] = rowArray[i].trim();
                }
                map[rowNumber++] = rowArray;
            }
        }


//        int[][] map =
//
//                {
//                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 5, 5},
//                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 5, 5},
//                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 5, 5, 5, 5, 5},
//                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 5, 5, 5, 5, 5},
//                        {1, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 5, 5, 5, 5, 5},
//                        {1, 0, 0, 0, 1, 0, 2, 2, 2, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 5, 5, 5, 5, 5},
//                        {1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5},
//                        {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5}};

        //convert each thing to a tile
        Tile[][] levelMap = new Tile[map.length][map[0].length];

        int startX=0,startY=0;
        for (int i = 0; i < map.length; i++) {
            //there might be null garbage in here
            //this also ensures the array is properly rectangular:
            if (map[i] == null || map[i].length != map[0].length) {
                continue;
            }
            for (int j = 0; j < map[0].length; j++) {
                levelMap[i][j] = new Tile(map[i][j]);
                Log.i("gen. i,j",i+","+j);
                Log.i("val",map[i][j]);
                if (levelMap[i][j].getValue() < 0) {
                    //the dude sits at the -1 tile
                    startX = j;
                    startY = i;
                    //and set it to 0
                    levelMap[i][j] = new Tile(0);
                }
            }
        }
        //pack it up
        Level retVal=new Level(levelMap,startX,startY);
        retVal.strMap=map;
        return retVal;
    }
}
