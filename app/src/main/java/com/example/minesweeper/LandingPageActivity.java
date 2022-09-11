package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import androidx.gridlayout.widget.GridLayout;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class LandingPageActivity extends AppCompatActivity {

    private static final int COLUMN_COUNT = 8;
    private static final int ROW_COUNT = 10;
    private static final int NUM_BOMBS = 40;
    private TextView[][] cell_tvs = new TextView[ROW_COUNT][COLUMN_COUNT];
    private boolean[][] bombLocs = new boolean[ROW_COUNT][COLUMN_COUNT];
    private boolean[][] visited = new boolean[ROW_COUNT][COLUMN_COUNT];
    private boolean[][] flagLocs = new boolean[ROW_COUNT][COLUMN_COUNT];
    private boolean firstClick = true;
    private boolean flagClicked = false;
    private int flagsPlaced = 0;
    private int totalSquaresRevealed = 0;
    private int seconds = 0;
    private boolean running = false;
    private boolean gameOver = false;
    private boolean winner = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page_activity);
        displayGrid();
        runTimer();
    }

    private void runTimer() {
        final TextView timeDisplay = (TextView) findViewById(R.id.timePassed);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run(){
                timeDisplay.setText(String.valueOf(seconds));
                if(running){
                    seconds++;
                }
                handler.postDelayed(this,1000);
            }
                     });

    }

    private void populateGrid(int n) {
        for(int i = 0;i<NUM_BOMBS;i++){
            Random rand = new Random();
            int potentialRow = rand.nextInt(ROW_COUNT);
            int potentialCol = rand.nextInt(COLUMN_COUNT);
            while(bombLocs[potentialRow][potentialCol] || ((potentialRow*COLUMN_COUNT)+potentialCol == n)){
                potentialRow = rand.nextInt(ROW_COUNT);
                potentialCol = rand.nextInt(COLUMN_COUNT);
            }
            bombLocs[potentialRow][potentialCol] = true;
        }
    }

    private void displayGrid() {
        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);

        for(int r=0;r<ROW_COUNT;r++) {
            for (int c = 0; c < COLUMN_COUNT; c++) {
                TextView tv = new TextView(this);
                tv.setGravity(Gravity.CENTER);
                tv.setBackgroundColor(Color.parseColor("lime"));
                tv.setHeight(84);
                tv.setWidth(84);
                tv.setOnClickListener(this::onClickTV);
                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.rowSpec = GridLayout.spec(r);
                lp.columnSpec = GridLayout.spec(c);
                cell_tvs[r][c] = tv;
                grid.addView(tv, lp);
            }
        }
        TextView theSwitch = findViewById(R.id.FlagAxeSwitch);
        theSwitch.setText(getResources().getString(R.string.pick));
        theSwitch.setTextSize(64);
        theSwitch.setOnClickListener(this::toggleFlagAxe);
        TextView tv = (TextView) findViewById(R.id.flagCount);
        tv.setText(String.valueOf(NUM_BOMBS));
    }

    private void toggleFlagAxe(View view) {
        TextView tv = (TextView) view;
        if(flagClicked){
            tv.setText(getResources().getString(R.string.pick));
        }
        else
            tv.setText(getResources().getString(R.string.flag));
        flagClicked = !flagClicked;

    }


    public void onClickTV(View view){
        if(gameOver){
            Intent intent = new Intent(this, ResultPageActivity.class);
            if(winner){
                intent.putExtra("winner", 1);
                intent.putExtra("time", seconds);
            }
            startActivity(intent);
        }
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        if(firstClick){
            populateGrid(n);
            running = true;
            firstClick = false;
        }
        //this is where the magic happens
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;

        clickedOn(i,j, tv);
        if(totalSquaresRevealed == (ROW_COUNT*COLUMN_COUNT)-NUM_BOMBS)
            gameOver(true);
    }

    private void clickedOn(int i, int j, TextView tv) {
        if(flagClicked){
            //also need to increase the flagged count at the top
            if(flagLocs[i][j]){
                tv.setText("");
                flagsPlaced--;
                flagLocs[i][j] = false;
                updateFlagCounter();
            }
            else{
                tv.setText(getResources().getString(R.string.flag));
                TextView theSwitch = findViewById(R.id.FlagAxeSwitch);
                flagLocs[i][j] = true;
                flagsPlaced++;
                updateFlagCounter();
                if(flagClicked)
                    theSwitch.setText(getResources().getString(R.string.flag));
                else
                    theSwitch.setText(getResources().getString(R.string.pick));
            }
        }
        else {
            totalSquaresRevealed++;
            if(bombLocs[i][j]){
                gameOver(false);
                return;
            }
            visited[i][j] = true;
            tv.setBackgroundColor(Color.GRAY);
            if(surroundingBombs(i,j) == 0){
                tv.setText("");
                if(i+1<ROW_COUNT){
                    if(!visited[i+1][j]) clickedOn(i+1,j,cell_tvs[i+1][j]);
                    if(j+1<COLUMN_COUNT){
                        if(!visited[i+1][j+1]) clickedOn(i+1,j+1,cell_tvs[i+1][j+1]);
                    }
                    if(j-1>=0){
                        if(!visited[i+1][j-1]) clickedOn(i+1,j-1,cell_tvs[i+1][j-1]);
                    }
                }
                if(i-1>=0){
                    if(!visited[i-1][j]){
                        clickedOn(i-1,j,cell_tvs[i-1][j]);
                    }
                    if(j+1<COLUMN_COUNT){
                        if(!visited[i-1][j+1]) clickedOn(i-1,j+1,cell_tvs[i-1][j+1]);
                    }
                    if(j-1>=0){
                        if(!visited[i-1][j-1]) clickedOn(i-1,j-1,cell_tvs[i-1][j-1]);
                    }
                }
                if(j+1<COLUMN_COUNT && !visited[i][j+1]) clickedOn(i,j+1,cell_tvs[i][j+1]);
                if(j-1>=0 && !visited[i][j-1]) clickedOn(i,j-1,cell_tvs[i][j-1]);
            }
            else{
                int toDisplay = surroundingBombs(i,j);
                tv.setText(String.valueOf(toDisplay));
            }
        }
    }

    private void updateFlagCounter() {
        TextView tv = (TextView) findViewById(R.id.flagCount);
        tv.setText(String.valueOf(NUM_BOMBS - flagsPlaced));
    }

    private int surroundingBombs(int i, int j) {
        int toReturn = 0;
        if(i+1<ROW_COUNT){
            if(bombLocs[i+1][j]) toReturn++;
            if(j+1<COLUMN_COUNT){
                if(bombLocs[i+1][j+1]) toReturn++;
            }
            if(j-1>=0){
                if(bombLocs[i+1][j-1]) toReturn++;
            }
        }
        if(i-1>=0){
            if(bombLocs[i-1][j]) toReturn++;
            if(j+1<COLUMN_COUNT){
                if(bombLocs[i-1][j+1]) toReturn++;
            }
            if(j-1>=0){
                if(bombLocs[i-1][j-1]) toReturn++;
            }
        }
        if(j+1<COLUMN_COUNT && bombLocs[i][j+1]) toReturn++;
        if(j-1>=0 && bombLocs[i][j-1]) toReturn++;

        return toReturn;
    }

    private void gameOver(boolean b) {
        gameOver = true;
        winner = b;
        running = false;
        for(int i = 0;i<ROW_COUNT;i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                if (bombLocs[i][j]) {
                    cell_tvs[i][j].setText(getResources().getString(R.string.mine));
                    if(!winner)
                        cell_tvs[i][j].setBackgroundColor(Color.RED);
                }
            }
        }

    }


    private int findIndexOfCellTextView(TextView tv){
        for(int r = 0;r<ROW_COUNT;r++){
            for(int c = 0;c<COLUMN_COUNT;c++){
                if(cell_tvs[r][c] == tv)
                    return (r*COLUMN_COUNT)+c;
            }
        }
        return -1;
    }
}