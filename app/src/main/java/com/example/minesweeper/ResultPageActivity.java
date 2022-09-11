package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import androidx.gridlayout.widget.GridLayout;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class ResultPageActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_page_activity);
        Intent intent = getIntent();
        TextView resultMessage = findViewById(R.id.textViewResultsPage);
        resultMessage.setTextSize(32);
        resultMessage.setGravity(Gravity.CENTER);
        if(intent.getIntExtra("winner",0) == 1){
            String message = "Used "+intent.getIntExtra("time",0) + " seconds. \n";
            message+= "You won.\nGood job!";
            resultMessage.setText(message);
        }
        else{
            resultMessage.setText("You lost!");
        }
        Button button = findViewById(R.id.button01);
        button.setOnClickListener(this::playAgain);
    }

    private void playAgain(View view) {
        Intent intent = new Intent(this, LandingPageActivity.class);
        startActivity(intent);
    }
}
