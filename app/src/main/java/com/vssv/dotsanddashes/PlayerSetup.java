package com.vssv.dotsanddashes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerSetup extends AppCompatActivity {

    int CurrentPlayerIcon = 9;
    int CurrentPlayer = 0;
    int CurrentPlayerColor = 0;
    int MaxPlayer = 0;

    public List<String> Players;
    public List<Integer> Scores;
    public List<Integer> PlayerShapes;
    public List<Integer> PlayerColors;

    List<Integer> Colors;
    List<Integer> AvbColors;

    TextView FavColor;

    SharedPreferences S;
    SharedPreferences.Editor E;

    boolean AI = false;

    TextView Error;

    EditText Es;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences Se = getSharedPreferences("SettingsData",MODE_PRIVATE);
        int theme = Se.getInt("theme",0);
        if (theme == 0)
        {
            setTheme(R.style.DialogAppTheme);
        }
        else if (theme == 1)
        {
            setTheme(R.style.DialogDarkTheme);
        }
        else
        {
            setTheme(R.style.DialogLightTheme);
        }
        super.onCreate(savedInstanceState);

        S = getSharedPreferences("SettingsData",MODE_PRIVATE);
        E = S.edit();
        setContentView(R.layout.activity_player_setup);
        hideSystemUI();

        FavColor = (TextView)findViewById(R.id.textView14);

        Error = (TextView)findViewById(R.id.eps);
        Error.setVisibility(View.GONE);

        Colors = new ArrayList<Integer>();

        Colors.add(getResources().getColor(R.color.BarbiePink));
        Colors.add(getResources().getColor(R.color.Blue));
        Colors.add(getResources().getColor(R.color.Green));
        Colors.add(getResources().getColor(R.color.Orange));
       Colors.add(getResources().getColor(R.color.Purple));
        Colors.add(getResources().getColor(R.color.Pink));
        Colors.add(getResources().getColor(R.color.Violet));
        Colors.add(getResources().getColor(R.color.Cyan));

        AvbColors = new ArrayList<Integer>();

        for (int i = 0; i < Colors.size(); i++)
        {
            AvbColors.add(Colors.get(i));
        }

        Players = new ArrayList<String>();
        Scores = new ArrayList<Integer>();
        PlayerShapes = new ArrayList<Integer>();
        PlayerColors = new ArrayList<Integer>();

        MaxPlayer = S.getInt("Players",2) - 1;

        if (S.getInt("AImode",0) > 0)
        {
            MaxPlayer -= 1;
            AI = true;
        }



        PlayerSetup();



    }

    void PlayerSetup()
    {
        Es = (EditText)findViewById(R.id.editText);
        Es.setText("P" + (int)(CurrentPlayer + 1));

        Es.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Error.setText(getResources().getStringArray(R.array.PlayerNameErrorCodes)[ThreadLocalRandom.current().nextInt(getResources().getStringArray(R.array.PlayerNameErrorCodes).length)]);
                if (!s.toString().matches(".*[a-zA-Z]+.*"))
                {
                    Error.setVisibility(View.VISIBLE);
                }
                else
                {
                    Error.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ((TextView)findViewById(R.id.textView11)).setText("Player " + (CurrentPlayer + 1) + " Setup");

        for (int i = 0; i < PlayerShapes.size(); i++)
        {
            if (PlayerShapes.get(i) != 9)
            ((ImageButton)findViewById(getResources().getIdentifier("I" + (int)(PlayerShapes.get(i)),"id",this.getPackageName()))).setEnabled(false);
        }

        for (int i = 0; i < PlayerColors.size(); i++)
        {
            ((Button)findViewById(getResources().getIdentifier("c" + (int)(Colors.indexOf(PlayerColors.get(i)) + 1),"id",this.getPackageName()))).setEnabled(false);
    }

        FavColor.setBackgroundColor(AvbColors.get(0));
        CurrentPlayerColor = Colors.indexOf(AvbColors.get(0));

        for (int i =0; i < 9; i++)
        {
            ((ImageButton)findViewById(getResources().getIdentifier("I" + (int)(i + 1),"id",this.getPackageName()))).setBackgroundColor(Color.TRANSPARENT);
            ((ImageButton)findViewById(getResources().getIdentifier("I" + (int)(i + 1),"id",this.getPackageName()))).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    V.vibrate(10);
                    for (int i = 0; i <9; i++)
                    {
                        ((ImageButton)findViewById(getResources().getIdentifier("I" + (int)(i + 1),"id",getPackageName()))).setBackgroundColor(Color.TRANSPARENT);
                    }
                    ((ImageButton)v).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    CurrentPlayerIcon = Integer.parseInt((v.getResources().getResourceEntryName(v.getId())).substring(1));
                }
            });
        }

        ((ImageButton)findViewById(R.id.I9)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        for (int i = 0; i < 8;i++)
        {
            ((Button)findViewById(getResources().getIdentifier("c" + (int)(i + 1),"id",this.getPackageName()))).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    V.vibrate(10);
                    CurrentPlayerColor = Integer.parseInt((v.getResources().getResourceEntryName(v.getId())).substring(1)) - 1;
                    FavColor.setBackgroundColor(Colors.get(CurrentPlayerColor));
                }
            });
        }

        Button Next = (Button)findViewById(R.id.next);

        if(CurrentPlayer == MaxPlayer)
        {
            Next.setText("Start Game");
        }

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                V.vibrate(10);
                if(!Es.getText().toString().matches(".*[a-zA-Z]+.*"))
                {
                    Error.setText(getResources().getStringArray(R.array.PlayerNameErrorCodes)[ThreadLocalRandom.current().nextInt(getResources().getStringArray(R.array.PlayerNameErrorCodes).length)]);
                }
                else
                {
                    Players.add(Es.getText().toString());
                    Scores.add(0);
                    PlayerColors.add(Colors.get(CurrentPlayerColor));
                    PlayerShapes.add(CurrentPlayerIcon);
                    AvbColors.remove(Colors.get(CurrentPlayerColor));

                    CurrentPlayerIcon = 9;

                    if (CurrentPlayer == MaxPlayer)
                    {
                        MainActivity.CurrentPlayer = 0;
                        if (AI)
                        {
                            Players.add("AI");
                            PlayerShapes.add(0);
                            PlayerColors.add(getResources().getColor(R.color.colorPrimary));
                            MainActivity.CurrentPlayer = 0;
                            Scores.add(0);
                        }

                        MainActivity.PlayerShapes = PlayerShapes;
                        MainActivity.PlayerColors = PlayerColors;
                        MainActivity.Players = Players;
                        MainActivity.Scores = Scores;
                        MainActivity.dotgap = S.getInt("BoardSize",0);
                        MainActivity.History = null;
                        MainActivity.Timer = null;
                        MainActivity.HistoryBoxes = null;
                        if (MainActivity.dotgap == 0)
                        {
                            MainActivity.dotgap = 200;
                        }
                        else
                        {
                            MainActivity.dotgap = 100;
                        }

                        MainActivity.gridx = S.getInt("Rows",4);
                        MainActivity.gridy = S.getInt("Columns", 4);

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        CurrentPlayer += 1;
                        PlayerSetup();
                    }
                }
            }
        });

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
