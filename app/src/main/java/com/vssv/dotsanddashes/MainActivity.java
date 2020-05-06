package com.vssv.dotsanddashes;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static Vibrator V;
    public static List<String> Players;
    public static List<Integer> Scores;
    public static List<Integer> PlayerShapes;
    public static int CurrentPlayer;

    public static List<Integer> PlayerColors;

    public static int gridx =5,gridy = 7;
    public static float dotgap = 200;

    static TextView TP1,TP2,TP1Score,TP2Score,VS,CP,Genral;
    public static TextView Error;

    public static ImageView I;

    public static String History = null,HistoryBoxes = null;

    static int NeutralBg;

    public static List<Drawable> Icons;

    public static int AIMode = 3;

    public static DotsService D;

    public static ValueAnimator Timer;

    public static boolean Timers =false;

    public static ProgressBar timerbar;

    public static int AIid = -1;

    public static int previous = 10;

    public static MediaPlayer Tick,Tock;

    public static ImageButton pb,rb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences Se = getSharedPreferences("SettingsData",MODE_PRIVATE);
        int theme = Se.getInt("theme",0);
        if (theme == 0)
        {
            setTheme(R.style.AppTheme);
        }
        else if (theme == 1)
        {
            setTheme(R.style.DarkTheme);
        }
        else
        {
            setTheme(R.style.LightTheme);
        }

        V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        SharedPreferences S = getSharedPreferences("SettingsData",MODE_PRIVATE);
        AIMode = S.getInt("AImode",0);

        Timers = S.getBoolean("Timer",false);

        if (AIMode > 0)
            AIid = PlayerShapes.indexOf(0);

        setContentView(R.layout.activity_main);
        hideSystemUI();

        Tick = MediaPlayer.create(getApplicationContext(),R.raw.tick);
        Tick.setVolume(getApplication().getSharedPreferences("SettingsData", Context.MODE_PRIVATE).getInt("Volume", 100) / 100f, getApplicationContext().getSharedPreferences("SettingsData", Context.MODE_PRIVATE).getInt("Volume", 100) / 100f);

        Tock = MediaPlayer.create(getApplicationContext(),R.raw.alarm);
        Tock.setVolume(getApplication().getSharedPreferences("SettingsData", Context.MODE_PRIVATE).getInt("Volume", 100) / 100f, getApplicationContext().getSharedPreferences("SettingsData", Context.MODE_PRIVATE).getInt("Volume", 100) / 100f);

        timerbar = findViewById(R.id.timer);


        if (Timer == null) {

            Timer = ValueAnimator.ofInt(0, 10000);
            Timer.setDuration(10000);
            Timer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = 10000 - (int) animation.getAnimatedValue();
                    timerbar.setProgress(value);
                    timerbar.setVisibility(View.VISIBLE);
                    if (!Timers) {
                        timerbar.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        if (value/1000 != previous && value>3000)
                        {
                            Log.d("Time"," " + value);
                            Tick.start();
                            previous = value/1000;
                        }
                        else if (value/1000 != previous) {
                            Log.d("Time"," " + value);
                            Tock.start();
                            previous = value/1000;
                        }
                    }
                    if (value == 0 && Timers) {
                        D.CloseTimer();
                    }
                }
            });
            Timer.start();


        }

        D = (DotsService)findViewById(R.id.Dots);

        if (Timers)
            ((ImageButton)findViewById(R.id.Undo)).setVisibility(View.GONE);
        else
            ((ImageButton)findViewById(R.id.Undo)).setVisibility(View.VISIBLE);

        ((ImageButton)findViewById(R.id.Undo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Timers)
                    ((DotsService)findViewById(R.id.Dots)).Undo();
            }
        });

        TP1 = (TextView)findViewById(R.id.TP1);
        TP2 = (TextView)findViewById(R.id.TP2);
        TP1Score = (TextView)findViewById(R.id.TP1Score);
        TP2Score = (TextView)findViewById(R.id.TP2Score);
        VS = (TextView)findViewById(R.id.vs);
        Genral = (TextView)findViewById(R.id.Genral);
        CP = (TextView)findViewById(R.id.CP);
        Error = (TextView)findViewById(R.id.Error);

        I = (ImageView)findViewById(R.id.CPI);

        NeutralBg = getResources().getColor(R.color.colorPrimary);

        Icons = new ArrayList<Drawable>();

        Icons.add(getResources().getDrawable(R.drawable.ic_ai));
        Icons.add(getResources().getDrawable(R.drawable.ic_ave));
        Icons.add(getResources().getDrawable(R.drawable.ic_dolphin));
        Icons.add(getResources().getDrawable(R.drawable.ic_gl));
        Icons.add(getResources().getDrawable(R.drawable.ic_hp));
        Icons.add(getResources().getDrawable(R.drawable.ic_hq));
        Icons.add(getResources().getDrawable(R.drawable.ic_mario));
        Icons.add(getResources().getDrawable(R.drawable.ic_st));
        Icons.add(getResources().getDrawable(R.drawable.ic_thanos));

        Updates();

        pb = ((ImageButton)findViewById(R.id.pauseb));
        rb = ((ImageButton)findViewById(R.id.Undo));


        ((ImageButton)findViewById(R.id.pauseb)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer.pause();
                Intent I = new Intent(MainActivity.this,GameFinish.class);
                I.putExtra("Pause",1);
                startActivity(I);
                ((ImageButton)findViewById(R.id.pauseb)).setEnabled(false);
                rb.setEnabled(false);
            }
        });


    }

    @Override
    public void onPause()
    {
        super.onPause();
        MediaService.stop();
        Timer.pause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        MediaService.start(getApplicationContext());
        Timer.resume();
    }

    public static void Updates()
    {
        CP.setBackgroundColor(PlayerColors.get(CurrentPlayer));
        CP.setText(Players.get(CurrentPlayer));

        I.setVisibility(View.VISIBLE);
        if (PlayerShapes.get(CurrentPlayer) != 9)
            I.setImageDrawable(Icons.get(PlayerShapes.get(CurrentPlayer)));
        else
            I.setVisibility(View.INVISIBLE);

        if (Players.size() == 2)
        {
            TP1.setText(Players.get(0));
            TP1.setBackgroundColor(PlayerColors.get(0));
            TP1Score.setText(""+Scores.get(0));
            TP1Score.setBackgroundColor(PlayerColors.get(0));

            VS.setText("VS");
            VS.setBackgroundColor(NeutralBg);

            TP2.setText(Players.get(1));
            TP2.setBackgroundColor(PlayerColors.get(1));
            TP2Score.setText(""+Scores.get(1));
            TP2Score.setBackgroundColor(PlayerColors.get(1));


            TP1.setVisibility(View.VISIBLE);
            TP2.setVisibility(View.VISIBLE);
            TP2Score.setVisibility(View.VISIBLE);
            TP1Score.setVisibility(View.VISIBLE);
            VS.setVisibility(View.VISIBLE);
            Genral.setVisibility(View.INVISIBLE);
        }
        else if (Players.size() == 3)
        {
            TP1.setText(""+Scores.get(0));
            TP1.setBackgroundColor(PlayerColors.get(0));
            TP1Score.setText(""+Scores.get(0));
            TP1Score.setBackgroundColor(PlayerColors.get(0));

            VS.setText("" + Scores.get(1));
            VS.setBackgroundColor(PlayerColors.get(1));

            TP2.setText(""+ Scores.get(2));
            TP2.setBackgroundColor(PlayerColors.get(2));
            TP2Score.setText(""+Scores.get(1));
            TP2Score.setBackgroundColor(PlayerColors.get(1));


            TP1.setVisibility(View.VISIBLE);
            TP2.setVisibility(View.VISIBLE);
            TP2Score.setVisibility(View.INVISIBLE);
            TP1Score.setVisibility(View.INVISIBLE);
            VS.setVisibility(View.VISIBLE);
            Genral.setVisibility(View.INVISIBLE);
        }
        else if (Players.size() == 4)
        {
            TP1.setText(""+Scores.get(0));
            TP1.setBackgroundColor(PlayerColors.get(0));
            TP1Score.setText(""+Scores.get(1));
            TP1Score.setBackgroundColor(PlayerColors.get(1));

            VS.setText("");
            VS.setBackgroundColor(NeutralBg);

            TP2.setText("" + Scores.get(2));
            TP2.setBackgroundColor(PlayerColors.get(2));
            TP2Score.setText(""+Scores.get(3));
            TP2Score.setBackgroundColor(PlayerColors.get(3));


            TP1.setVisibility(View.VISIBLE);
            TP2.setVisibility(View.VISIBLE);
            TP2Score.setVisibility(View.VISIBLE);
            TP1Score.setVisibility(View.VISIBLE);
            VS.setVisibility(View.GONE);
            Genral.setVisibility(View.INVISIBLE);
        }
        else if (Players.size() == 5)
        {
            TP1.setText(""+Scores.get(0));
            TP1.setBackgroundColor(PlayerColors.get(0));
            TP1Score.setText(""+Scores.get(1));
            TP1Score.setBackgroundColor(PlayerColors.get(1));

            VS.setText("" + Scores.get(2));
            VS.setBackgroundColor(PlayerColors.get(2));

            TP2.setText("" + Scores.get(3));
            TP2.setBackgroundColor(PlayerColors.get(3));
            TP2Score.setText(""+Scores.get(4));
            TP2Score.setBackgroundColor(PlayerColors.get(4));


            TP1.setVisibility(View.VISIBLE);
            TP2.setVisibility(View.VISIBLE);
            TP2Score.setVisibility(View.VISIBLE);
            TP1Score.setVisibility(View.VISIBLE);
            VS.setVisibility(View.VISIBLE);
            Genral.setVisibility(View.INVISIBLE);
        }
        else
        {
            String ts = "";
            for (int i = 0; i < Players.size();i++)
            {
                ts += Players.get(i) + " : " + Scores.get(i) + " ";
            }
            Genral.setVisibility(View.VISIBLE);
            Genral.setText(ts);
            Genral.setBackgroundColor(NeutralBg);

            TP1.setVisibility(View.INVISIBLE);
            TP2.setVisibility(View.INVISIBLE);
            TP2Score.setVisibility(View.INVISIBLE);
            TP1Score.setVisibility(View.INVISIBLE);
            VS.setVisibility(View.INVISIBLE);
        }

    }

    public static void Unpause()
    {
        pb.setEnabled(true);
        rb.setEnabled(true);
    }

    @Override
    public void  onBackPressed()
    {
        DotsService D = (DotsService)findViewById(R.id.Dots);
        if (!Timers)
            D.Undo();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
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
