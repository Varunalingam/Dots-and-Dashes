package com.vssv.dotsanddashes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.display.DisplayManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SplashScreen extends AppCompatActivity {

    public static float width;
    public static float height;

    public Handler H;
    public Runnable Rss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            System.exit(0);
        }

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

        setContentView(R.layout.activity_splash_screen);

        MediaService.start(getApplicationContext());

        hideSystemUI();

        DisplayMetrics displayMetrics = new DisplayMetrics();

        WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);

        Random a = new Random();
        ((TextView)findViewById(R.id.textView18)).setText(getResources().getStringArray(R.array.Tips)[Math.abs(a.nextInt() % getResources().getStringArray(R.array.Tips).length)]);

        SharedPreferences S = getSharedPreferences("ScreenData",MODE_PRIVATE);
        SharedPreferences.Editor E = S.edit();
        E.putFloat("width",displayMetrics.widthPixels - (3 * 10 * displayMetrics.density));
        E.putFloat("height",displayMetrics.heightPixels - (6*10*displayMetrics.density) - (50*displayMetrics.density) - (32 * displayMetrics.scaledDensity));
        E.commit();

        Rss = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, Menu.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();

            }
        };

        H = new Handler();
        H.postDelayed(Rss,2000);



    }

    @Override
    public void onPause()
    {
        super.onPause();
        MediaService.stop();
        H.removeCallbacks(Rss);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        MediaService.start(getApplicationContext());
        H = new Handler();
        H.postDelayed(Rss,2000);
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
