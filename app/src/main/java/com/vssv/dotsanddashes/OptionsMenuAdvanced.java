package com.vssv.dotsanddashes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class OptionsMenuAdvanced extends AppCompatActivity {

    SharedPreferences S;
    SharedPreferences.Editor E;
    int BoardSize,rowmax,columnmax;
    float width,height;
    Spinner Rows,Columns;


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

        Intent I = getIntent();
        int state = I.getIntExtra("StartGame",0);

        S = getSharedPreferences("ScreenData",MODE_PRIVATE);
        width = S.getFloat("width" ,0f);
        height =S.getFloat("height",0f);

        S = getSharedPreferences("SettingsData",MODE_PRIVATE);
        E = S.edit();

        setContentView(R.layout.activity_options_menu_advanced);
        hideSystemUI();

        CheckBox SetupPlayer = (CheckBox)findViewById(R.id.checkBox2);
        CheckBox Dontshowagain = (CheckBox)findViewById(R.id.checkBox3);

        SetupPlayer.setChecked(S.getBoolean("PlayerSetup",false));
        Dontshowagain.setChecked(S.getBoolean("DFS",false));

        final Button NextGame = (Button)findViewById(R.id.button2);

        NextGame.setVisibility(View.GONE);
        Dontshowagain.setVisibility(View.GONE);

        SetupPlayer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                V.vibrate(10);
                E.putBoolean("PlayerSetup",isChecked);
                E.commit();
                E = S.edit();

                if (isChecked)
                {
                    NextGame.setText("Start Game");
                }
                else
                {
                    NextGame.setText("Setup Player");
                }
            }
        });

        ((TextView)findViewById(R.id.textView12)).setText("Advanced Options");
        if (state == 1)
        {
            ((TextView)findViewById(R.id.textView12)).setText("Start New Game");
            NextGame.setVisibility(View.VISIBLE);
            SetupPlayer.setVisibility(View.VISIBLE);
            Dontshowagain.setVisibility(View.VISIBLE);

            if (SetupPlayer.isChecked())
            {
                NextGame.setText("Start Game");
            }

            Dontshowagain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    V.vibrate(10);
                    E.putBoolean("DFS",isChecked);
                    E.commit();
                    E = S.edit();
                }
            });

            NextGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    V.vibrate(10);
                    if (!S.getBoolean("PlayerSetup",false))
                    {
                        startActivity(new Intent(OptionsMenuAdvanced.this,PlayerSetup.class));
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
                        MainActivity.Timer = null;
                        MainActivity.HistoryBoxes = null;
                        MainActivity.History = null;
                        MainActivity.Players = Players;
                        MainActivity.PlayerColors = PlayersColors;
                        MainActivity.PlayerShapes = PlayersShapes;
                        MainActivity.Scores = Scores;
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

                        MainActivity.gridx = S.getInt("Rows",4);
                        MainActivity.gridy = S.getInt("Columns", 4);

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            });

        }


        final MultiStateToggleButton NOP = (MultiStateToggleButton)findViewById(R.id.Players);
        final Spinner NOPC = (Spinner)findViewById(R.id.custom2);
        String[] CustomPlayers = {"5","6","7","8"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item,CustomPlayers);
        NOPC.setAdapter(adapter);

        int players = S.getInt("Players",2);
        boolean[] nopbs = {false,false,false,false};

        if (players < 5)
        {
            nopbs[players - 2] = true;
            NOPC.setVisibility(View.GONE);
        }
        else
        {
            nopbs[3] = true;
            NOPC.setVisibility(View.VISIBLE);
            NOPC.setSelection(players - 5);
        }

        NOP.setStates(nopbs);

        NOPC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                V.vibrate(10);
                hideSystemUI();
                E.putInt("Players",position + 5);
                E.commit();
                E = S.edit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                hideSystemUI();

            }
        });

        NOP.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                V.vibrate(10);
                E.putInt("Players",value +2);
                E.commit();
                E = S.edit();
                if (value != 3)
                {
                    NOPC.setVisibility(View.GONE);
                }
                else
                {
                    NOPC.setVisibility(View.VISIBLE);
                    NOPC.setSelection(0);
                }
            }
        });

        MultiStateToggleButton AI = (MultiStateToggleButton)findViewById(R.id.AI);
        boolean[] aibs = {false,false,false,false};

        aibs[S.getInt("AImode",0)] = true;

        AI.setStates(aibs);
        AI.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                V.vibrate(10);
                E.putInt("AImode",value);
                E.commit();
                E = S.edit();
            }
        });

        Rows = (Spinner)findViewById(R.id.Row);
        Columns = (Spinner)findViewById(R.id.Column);


        MultiStateToggleButton Board = (MultiStateToggleButton)findViewById(R.id.Board);

        boolean[] Boardbs ={false,false};

        BoardSize = S.getInt("BoardSize",0);
        Boardbs[BoardSize] = true;
        Board.setStates(Boardbs);

        GridSetup();


        Board.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                V.vibrate(10);
                E.putInt("BoardSize",value);
                E.commit();
                E = S.edit();
                BoardSize = value;
                GridSetup();

            }
        });

        Rows.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                V.vibrate(10);
                int value = position;
                hideSystemUI();
                if (rowmax > 3)
                {
                    E.putInt("Rows",value + 4);
                }
                else
                {
                    E.putInt("Rows",value + 1);
                }
                E.commit();
                E = S.edit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                hideSystemUI();

            }
        });

        Columns.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                V.vibrate(10);
                int value = position;
                hideSystemUI();
                if (columnmax > 3)
                {
                    E.putInt("Columns",value + 4);
                }
                else
                {
                    E.putInt("Columns",value + 1);
                }
                E.commit();
                E = S.edit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                hideSystemUI();

            }
        });
        Rows.setVerticalScrollBarEnabled(true);
        Columns.setVerticalScrollBarEnabled(true);


        CheckBox ProMode = (CheckBox)findViewById(R.id.checkBox5);

        ProMode.setChecked(S.getBoolean("Timer",false));

        ProMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                V.vibrate(10);
                E.putBoolean("Timer",isChecked);
                E.commit();
                E = S.edit();
            }
        });


    }

    void GridSetup ()
    {

        if (BoardSize == 1) {
            rowmax = (int) width / 100;
            columnmax = (int) height / 100;
        }
        else
        {
            rowmax = (int) width / 200;
            columnmax = (int) height / 200;
        }

        String[] Rowss;
        int Rowbs = 0;

        if (rowmax > 3) {
            Rowss = new String[rowmax - 3];

            for (int i = 0; i < Rowss.length;i++)
            {
                Rowss[i] = "" + (i + 4);
            }

            if (S.getInt("Rows",4) - 4 < Rowss.length )
                Rowbs = S.getInt("Rows",4) - 4;
            else
            {
                E.putInt("Rows",4);
                E.commit();
                E = S.edit();
                Rowbs = 0;
            }

        }
        else
        {
            Rowss = new String[rowmax];
            for (int i = 1; i < Rowss.length;i++)
            {
                Rowss[i] = "" + (i+1);
            }
            if (S.getInt("Rows",rowmax) - 1 < Rowss.length )
                Rowbs = S.getInt("Rows",rowmax) - 1;
            else
            {
                E.putInt("Rows",rowmax);
                E.commit();
                E = S.edit();
                Rowbs = rowmax - 1;
            }
        }

        String[] Columnss;
        int columnbs = 0;

        if (columnmax > 3) {
            Columnss = new String[columnmax - 3];

            for (int i = 0; i < Columnss.length;i++)
            {
                Columnss[i] = "" + (i + 4);
            }

            if (S.getInt("Columns",4) - 4 < Columnss.length )
                columnbs = S.getInt("Columns",4) - 4;
            else
            {
                E.putInt("Columns",4);
                E.commit();
                E = S.edit();
                columnbs= 0;
            }
        }
        else {
            Columnss = new String[columnmax];

            for (int i = 1; i < Columnss.length; i++) {
                Columnss[i] = "" + (i + 1);
            }
            if (S.getInt("Columns", columnmax) - 1 < Columnss.length)
                columnbs = S.getInt("Columns", columnmax) - 1;
            else {
                E.putInt("Columns", columnmax);
                E.commit();
                E = S.edit();
                columnbs = columnmax - 1;
            }
        }

        ArrayAdapter<String> radapter = new ArrayAdapter<>(this, R.layout.spinner_item,Rowss);
        Rows.setAdapter(radapter);
        ArrayAdapter<String> cadapter = new ArrayAdapter<>(this, R.layout.spinner_item,Columnss);
        Columns.setAdapter(cadapter);

        Rows.setSelection(Rowbs);
        Columns.setSelection(columnbs);
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
