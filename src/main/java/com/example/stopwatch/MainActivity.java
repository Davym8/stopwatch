package com.example.stopwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    Handler customhandler = new Handler();
    Runnable timer;
    ImageButton startbutton, stopbutton, resetbutton;
    TextView workouttext, chronometers;
    EditText editworkout;
    SharedPreferences sharedPreferences;
    boolean running;
    long secondstimer = 0;
    String TIME_TAKEN = "TIME_TAKEN", IS_RUNNING = "IS_RUNING", WORKOUT_TEXT = "WORKOUT_TEXT";
    String workout = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("com.example.stopwatch", MODE_PRIVATE);
        chronometers = findViewById(R.id.chronometers);
        editworkout = findViewById(R.id.editworkout);
        workouttext = findViewById(R.id.workouttext);

        settimer();
        loaddata();
        if(savedInstanceState != null)
        {
            secondstimer = savedInstanceState.getLong(TIME_TAKEN);
            updatetimer();
            running = savedInstanceState.getBoolean(IS_RUNNING);
            workout = savedInstanceState.getString(WORKOUT_TEXT);
            if(running)
            {
                customhandler.postDelayed(timer,0);
            }
        }
    }
    private void settimer()
    {
        timer = new Runnable()
        {
            @Override
            public void run()
            {
                updatetimer();
                secondstimer++;
                customhandler.postDelayed(timer, 1000);
            }
        };

    }
    public void updatetimer()
    {
        long sec = secondstimer ;
        long min = sec/60;
        long hour = sec/3600;
        chronometers.setText(String.format("%02d", hour) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec));
    }
    private void recordworkout()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(WORKOUT_TEXT, workout);
        editor.putLong(TIME_TAKEN, secondstimer);
        editor.commit();
    }
    private void loaddata()
    {
        String prevwork = sharedPreferences.getString(WORKOUT_TEXT,"");
        if(!prevwork.isEmpty())
        {
            long sec = sharedPreferences.getLong(TIME_TAKEN,0);
            long min = sec/60;
            long hour = sec/3600;
            String timefield = String.format("%02d", hour) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec);
            workouttext.setText("You spent " + timefield+ " on "+ prevwork+ " last time ");
        }
    }
    public void playbutton(View view)
    {
        if (!running) {

            if (!editworkout.getText().toString().trim().isEmpty()) {
                running = true;
                workout = editworkout.getText().toString();
                editworkout.setEnabled(false);
                customhandler.postDelayed(timer, 0);
            }
            else
            {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                {
                    Toast.makeText(this,"Enter a workout type!", Toast.LENGTH_LONG).show();
                }
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                {
                    Toast.makeText(this,"Enter a workout type!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void pausebutton(View view)
    {
        running = false;
        customhandler.removeCallbacks(timer);
    }
    public void resetbutton(View view)
    {
        running = false;
        customhandler.removeCallbacks(timer);
        recordworkout();
        loaddata();
        secondstimer = 0;
        chronometers.setText("00:00:00");
        editworkout.setText("");
        editworkout.setEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putLong(TIME_TAKEN,secondstimer);
        outState.putString(WORKOUT_TEXT,workout);
        outState.putBoolean(IS_RUNNING,running);
    }
}