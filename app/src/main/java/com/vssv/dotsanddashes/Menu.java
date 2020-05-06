package com.vssv.dotsanddashes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Menu extends AppCompatActivity {

    SharedPreferences S;
    SharedPreferences.Editor E;
    Vibrator V;

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


        Intent I = getIntent();
        if (I.getBooleanExtra("ThemeChanged",false))
        {
            startActivity(new Intent(Menu.this,OptionsMenu.class));
        }

        S = getSharedPreferences("SettingsData",MODE_PRIVATE);
        E = S.edit();

        setContentView(R.layout.activity_menu);
        hideSystemUI();
        V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                V.vibrate(10);
                if (!S.getBoolean("DFS",false))
                {
                    Intent I = new Intent(Menu.this,OptionsMenuAdvanced.class);
                    I.putExtra("StartGame",1);
                    startActivity(I);
                }
                else if (!S.getBoolean("PlayerSetup", false))
                {
                    startActivity(new Intent(Menu.this,PlayerSetup.class));
                }
                else
                {
                    List<String> Players = new ArrayList<String>();
                    List<Integer> PlayersColors = new ArrayList<Integer>();
                    List<Integer> PlayersShapes = new ArrayList<Integer>();
                    List<Integer> Scores = new ArrayList<Integer>();

                    int MaxPlayers = S.getInt("Players",2);

                    if (S.getInt("AImode",0) > 0)
                    {
                        Players.add("AI");
                        PlayersColors.add(getResources().getColor(R.color.colorPrimary));
                        PlayersShapes.add(0);
                        Scores.add(0);
                        MaxPlayers -= 1;
                    }

                    List<Integer> Colors = new ArrayList<Integer>();

                    Colors.add(getResources().getColor(R.color.BarbiePink));
                    Colors.add(getResources().getColor(R.color.Blue));
                    Colors.add(getResources().getColor(R.color.Green));
                    Colors.add(getResources().getColor(R.color.Orange));
                    Colors.add(getResources().getColor(R.color.Purple));
                    Colors.add(getResources().getColor(R.color.Pink));
                    Colors.add(getResources().getColor(R.color.Violet));
                    Colors.add(getResources().getColor(R.color.Cyan));

                    for (int i =0; i < MaxPlayers;i++)
                    {
                        Players.add("P" + (i + 1));
                        PlayersColors.add(Colors.get(i));
                        PlayersShapes.add(i + 1);
                        Scores.add(0);
                    }

                    MainActivity.Players = Players;
                    MainActivity.PlayerColors = PlayersColors;
                    MainActivity.PlayerShapes = PlayersShapes;
                    MainActivity.Scores = Scores;

                    if (S.getInt("AImode",0) > 0)
                        MainActivity.CurrentPlayer = 0;
                    else
                        MainActivity.CurrentPlayer = 0;

                    MainActivity.dotgap = S.getInt("BoardSize",0);
                    if (MainActivity.dotgap == 0)
                    {
                        MainActivity.dotgap = 200;
                    }
                    else
                    {
                        MainActivity.dotgap = 100;
                    }
                    MainActivity.Timer = null;
                    MainActivity.HistoryBoxes = null;
                    MainActivity.History = null;
                    MainActivity.gridx = S.getInt("Rows",4);
                    MainActivity.gridy = S.getInt("Columns", 4);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
            }
        });

        ((Button)findViewById(R.id.button4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                V.vibrate(10);
                startActivity(new Intent(Menu.this,OptionsMenu.class));
            }
        });

        ((Button)findViewById(R.id.button5)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                V.vibrate(10);
                startActivity(new Intent(Menu.this,quitactivity.class));
                Menu.this.finish();
            }
        });

    }

    boolean bp = false;

    @Override
    public void onBackPressed()
    {

        if (!bp) {
            Toast.makeText(getApplicationContext(), "Press Once more to Quit Game!", Toast.LENGTH_LONG);
            bp = true;
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bp = false;
                }
            },1000);
        }
        else
        {
            startActivity(new Intent(Menu.this,quitactivity.class));
            Menu.this.finish();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        MediaService.stop();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        MediaService.start(getApplicationContext());
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
