package com.vssv.dotsanddashes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

public class OptionsMenu extends AppCompatActivity {

    SharedPreferences S;
    SharedPreferences.Editor E;

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

        setContentView(R.layout.activity_options_menu);
        hideSystemUI();


        S = getSharedPreferences("SettingsData",MODE_PRIVATE);
        E = S.edit();

        ((Button)findViewById(R.id.adv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                V.vibrate(10);
                E.commit();
                startActivity(new Intent(OptionsMenu.this,OptionsMenuAdvanced.class));
            }
        });

        final SeekBar Volume = (SeekBar)findViewById(R.id.seekBar);
        Volume.setProgress(S.getInt("Volume",100));
        Volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                E.putInt("Volume",progress);
                E.commit();
                MediaService.setVolume();
                E = S.edit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                V.vibrate(10);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        CheckBox DFS = (CheckBox)findViewById(R.id.checkBox);

        DFS.setChecked(S.getBoolean("DFS",false));

        if (S.getBoolean("DFS",false))
            ((Button)findViewById(R.id.adv)).setVisibility(View.VISIBLE);
        else
            ((Button)findViewById(R.id.adv)).setVisibility(View.GONE);

        DFS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                V.vibrate(10);
                E.putBoolean("DFS",isChecked);
                E.commit();
                E = S.edit();
                if (isChecked)
                    ((Button)findViewById(R.id.adv)).setVisibility(View.VISIBLE);
                else
                    ((Button)findViewById(R.id.adv)).setVisibility(View.GONE);
            }
        });

        MultiStateToggleButton themeb = (MultiStateToggleButton)findViewById(R.id.theme);

        int theme = S.getInt("theme",0);

        boolean[] bs = {false,false,false};
        bs[theme] = true;

        themeb.setStates(bs);

        themeb.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                Vibrator V = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                V.vibrate(10);
                E.putInt("theme",value);
                E.commit();
                Intent I = new Intent(OptionsMenu.this,Menu.class);
                I.putExtra("ThemeChanged",true);
                startActivity(I);
                finish();
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
