package com.vssv.dotsanddashes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class GameFinish extends AppCompatActivity {

    public int mode =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences Se = getSharedPreferences("SettingsData",MODE_PRIVATE);
        int themes = Se.getInt("theme",0);
        if (themes == 0)
        {
            setTheme(R.style.DialogAppTheme);
        }
        else if (themes == 1)
        {
            setTheme(R.style.DialogDarkTheme);
        }
        else
        {
            setTheme(R.style.DialogLightTheme);
        }

        super.onCreate(savedInstanceState);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        setContentView(R.layout.activity_game_finish);
        hideSystemUI();


        Intent I = getIntent();

        mode = I.getIntExtra("Pause",0);

        if(mode == 0) {


            if (I.getBooleanExtra("MatchTied", false)) {
                ((TextView) findViewById(R.id.W)).setText(I.getStringExtra("Match Tied"));
                ((TextView) findViewById(R.id.W)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                ((TextView) findViewById(R.id.s)).setText(I.getStringExtra("among"));
            }

            ((TextView) findViewById(R.id.textView22)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.winnername)).setText(I.getStringExtra("Winner"));

            ((TextView) findViewById(R.id.scores)).setText(I.getIntExtra("Points", 0) + " points");


            ((Button) findViewById(R.id.button3)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    V.vibrate(10);
                    startActivity(new Intent(getApplicationContext(), Menu.class));
                    finish();
                }
            });

            ((Button) findViewById(R.id.button6)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    V.vibrate(10);
                    startActivity(new Intent(getApplicationContext(), quitactivity.class));
                    finish();
                }
            });
        }
        else
        {
            ((TextView)findViewById(R.id.textView24)).setText("Pause Menu");
            ((TextView) findViewById(R.id.W)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.s)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.textView28)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.winnername)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.scores)).setVisibility(View.GONE);

            ((Button) findViewById(R.id.button3)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    V.vibrate(10);
                    startActivity(new Intent(getApplicationContext(), Menu.class));
                    finish();
                }
            });

            ((Button) findViewById(R.id.button6)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    V.vibrate(10);
                    startActivity(new Intent(getApplicationContext(), quitactivity.class));
                    finish();
                }
            });
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
        {
            if (mode == 0)
                return false;
            else
            {
                MainActivity.Timer.resume();
                MainActivity.Unpause();
                finish();
                return super.onTouchEvent(event);
            }
        }
        else
        {
            return super.onTouchEvent(event);
        }

    }


    @Override
    public void onBackPressed()
    {
        if (mode == 1)
        {
            MainActivity.Timer.resume();
            finish();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
