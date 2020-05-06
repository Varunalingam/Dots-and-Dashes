package com.vssv.dotsanddashes;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Debug;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ThreadLocalRandom;


public class DotsService extends View{

    public static List<List<Path>> DashesPaths;
    Paint DashesPaint, DashesPaintStatic;
    public static Path DashesPath;

    List<List<Paint>> DotsPaints;

    Paint TextColor;

    float height;
    float width;

    public static int gridx = 5;
    public static int gridy = 6;

    public static float ix = -1;
    public static float iy = -1;

    int xi = -1;
    int yi = -1;

    float dotr = 10;
    float dotgap = 100;
    float textsize = 75;

    public static int memoryix = -1,memoryiy = -1;

    public static int DotColor;
    public static int DotShadowColor;

    public static List<CompletedBoxes> CompletedBoxs;

    VectorDrawableCompat[] V = new VectorDrawableCompat[9];

    boolean AIPlaying = false;
    AIService AI;

    boolean TimerState = false;
    boolean Touched = false;

    boolean firstrun = true;

    public DotsService(Context context, AttributeSet attr) {

        super(context,attr);
        TypedArray A = context.obtainStyledAttributes(attr,R.styleable.DotsService);
        dotr = A.getFloat(R.styleable.DotsService_DotRadius,dotr);
        DotColor = A.getColor(R.styleable.DotsService_DotColor,Color.BLACK);
        DotShadowColor = A.getColor(R.styleable.DotsService_DotColor,0xFF000000);
        dotgap = A.getFloat(R.styleable.DotsService_Dotgap,dotgap);
        A.recycle();

        gridx = MainActivity.gridx;
        gridy = MainActivity.gridy;

        dotgap = MainActivity.dotgap;
        dotr = dotgap / 10;
        textsize = 3 * dotgap / 8;


        DashesPaths = new ArrayList<List<Path>>();

        for (int i = 0; i < MainActivity.Players.size(); i++)
        {
            DashesPaths.add(new ArrayList<Path>());
        }

        DashesPaint = new Paint();
        DashesPaint.setAntiAlias(true);
        DashesPaint.setStrokeJoin(Paint.Join.ROUND);
        DashesPaint.setStyle(Paint.Style.STROKE);
        DashesPaint.setStrokeWidth(dotr/2);

        DashesPaintStatic = new Paint();
        DashesPaintStatic.setAntiAlias(true);
        DashesPaintStatic.setStrokeJoin(Paint.Join.ROUND);
        DashesPaintStatic.setStyle(Paint.Style.STROKE);
        DashesPaintStatic.setStrokeWidth(3 * dotr / 4);
        DashesPaintStatic.setColor(DotColor);

        DotsPaints = new ArrayList<List<Paint>>();

        TextColor = new Paint();
        TextColor.setTextSize(textsize);
        TextColor.setTextAlign(Paint.Align.CENTER);

        CompletedBoxs = new ArrayList<CompletedBoxes>();

        for (int i = 0; i < gridx; i++) {
            DotsPaints.add(new ArrayList<Paint>());
            for (int j = 0; j < gridy; j++) {
                Paint DotsNormal = new Paint();
                DotsNormal.setColor(DotColor);
                DotsNormal.setStyle(Paint.Style.FILL);
                DotsPaints.get(i).add(DotsNormal);
            }
        }

        V[0] = VectorDrawableCompat.create(getResources(),R.drawable.ic_ai,getContext().getTheme());
        V[1] = VectorDrawableCompat.create(getResources(),R.drawable.ic_ave,getContext().getTheme());
        V[2] = VectorDrawableCompat.create(getResources(),R.drawable.ic_dolphin,getContext().getTheme());
        V[3] = VectorDrawableCompat.create(getResources(),R.drawable.ic_gl,getContext().getTheme());
        V[4] = VectorDrawableCompat.create(getResources(),R.drawable.ic_hp,getContext().getTheme());
        V[5] = VectorDrawableCompat.create(getResources(),R.drawable.ic_hq,getContext().getTheme());
        V[6] = VectorDrawableCompat.create(getResources(),R.drawable.ic_mario,getContext().getTheme());
        V[7] = VectorDrawableCompat.create(getResources(),R.drawable.ic_st,getContext().getTheme());
        V[8] = VectorDrawableCompat.create(getResources(),R.drawable.ic_thanos,getContext().getTheme());

         AI = new AIService(this,MainActivity.AIMode);


    }

    public void initialize()
    {
        if (MainActivity.History != null)
        {
            String[] Data = MainActivity.History.split(";");
            String[] DataBoxes = MainActivity.HistoryBoxes.split(";");

            int cp = 0;
            for (int i = 0; i < Data.length;i++)
            {
                if (Data[i].contains("|"))
                {
                    MainActivity.CurrentPlayer = Integer.parseInt(Data[i].substring(1));
                }
                else if (Data[i].matches("@"))
                {

                }
                else
                {
                    int x = Integer.parseInt(Data[i].split(",")[0]);
                    int y = Integer.parseInt(Data[i].split(",")[1]);
                    int xx = x/2;
                    int yy = y/2;
                    x -= xx;
                    y -= yy;
                    DashesPath = new Path();
                    DashesPath.moveTo(x * dotgap + ((width - (gridx * dotgap)) / 2) + (dotgap / 2),y * dotgap + ((height - (gridy * dotgap)) / 2) + (dotgap / 2));
                    DashesPath.lineTo(xx * dotgap + ((width - (gridx * dotgap)) / 2) + (dotgap / 2),yy * dotgap + ((height - (gridy * dotgap)) / 2) + (dotgap / 2));
                    DashesPaths.get(MainActivity.CurrentPlayer).add(DashesPath);
                }
            }

            for (int i = 0; i < DataBoxes.length; i++)
            {
                if (DataBoxes[i].contains("|"))
                {
                    cp = Integer.parseInt(DataBoxes[i].substring(1));
                }
                else
                {
                    float posx = Float.parseFloat(DataBoxes[i].split(",")[0]);
                    float posy = Float.parseFloat(DataBoxes[i].split(",")[1]);
                    CompletedBoxs.add(new CompletedBoxes(MainActivity.PlayerShapes.get(MainActivity.CurrentPlayer),MainActivity.Players.get(MainActivity.CurrentPlayer),posx,posy));
                }
            }

        }
        else
        {
            MainActivity.History = "|" + MainActivity.CurrentPlayer + ";";
            MainActivity.HistoryBoxes = "|" + MainActivity.CurrentPlayer + ";";
        }
        firstrun = false;
    }

    @Override
    protected void onDraw(Canvas C) {
        super.onDraw(C);



        if (firstrun) {
            height = this.getHeight() - 20;
            width = this.getWidth() - 20;
            initialize();
        }


        for (int i = 0; i < DashesPaths.size();i++)
        {
            for (int j = 0; j < DashesPaths.get(i).size();j++)
            {
                C.drawPath(DashesPaths.get(i).get(j),DashesPaintStatic);
                DashesPaint.setColor(MainActivity.PlayerColors.get(i));
                C.drawPath(DashesPaths.get(i).get(j),DashesPaint);
            }
        }

        if (DashesPath != null) {
            C.drawPath(DashesPath, DashesPaintStatic);
            DashesPaint.setColor(MainActivity.PlayerColors.get(MainActivity.CurrentPlayer));
            C.drawPath(DashesPath, DashesPaint);
        }


       for (int i = 0; i < gridx; i++)
       {
           for (int j = 0; j < gridy; j++)
           {
               C.drawCircle(i * dotgap + ((width - (gridx * dotgap))/2) + (dotgap/2),j * dotgap + ((height - (gridy * dotgap))/2) + (dotgap/2),dotr,(DotsPaints.get(i)).get(j));
           }
       }

       for (int i = 0; i < CompletedBoxs.size(); i++)
       {
           if (CompletedBoxs.get(i).PlayerSymbol == 9)
           {
               TextColor.setColor(MainActivity.PlayerColors.get(MainActivity.Players.indexOf(CompletedBoxs.get(i).PlayerInitial)));
               if (CompletedBoxs.get(i).PlayerInitial.length() > 2) {
                   C.drawText(CompletedBoxs.get(i).PlayerInitial.substring(0,2), CompletedBoxs.get(i).posx, CompletedBoxs.get(i).posy + TextColor.descent(), TextColor);
               }
               else
               {
                   C.drawText(CompletedBoxs.get(i).PlayerInitial, CompletedBoxs.get(i).posx, CompletedBoxs.get(i).posy + TextColor.descent(), TextColor);
               }
           }
           else {
               V[CompletedBoxs.get(i).PlayerSymbol].setBounds((int) (CompletedBoxs.get(i).posx - (3 * dotgap / 8)), (int) (CompletedBoxs.get(i).posy - (3 * dotgap / 8)), (int) (CompletedBoxs.get(i).posx + (3 * dotgap / 8)), (int) (CompletedBoxs.get(i).posy + (3 * dotgap / 8)));
               V[CompletedBoxs.get(i).PlayerSymbol].draw(C);
           }

       }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!AIPlaying) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                        setstartpos(x, y);
                        Touched = true;
                        return true;

                case MotionEvent.ACTION_MOVE:
                    if (ix != -1 && iy != -1) {
                        DashesPath = new Path();
                        DashesPath.moveTo(ix, iy);
                        DashesPath.lineTo(x, y);
                        showposi(x, y);
                        break;
                    }
                case MotionEvent.ACTION_UP:
                    Touched = false;
                    if (ix != -1 && iy != -1 && !TimerState) {
                        setEndPos();
                        break;
                    }
                    if (TimerState)
                    {
                        TimerState =false;
                        CloseTimer();
                    }

                default:
                    return false;
            }
            invalidate();
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void Undo()
    {
        int aix = -1;
        int aiy = -1;
        if (!AIPlaying) {
            String[] Data = MainActivity.History.split(";");

            String[] DataBoxes = MainActivity.HistoryBoxes.split(";");
            DashesPath = null;
            memoryix = -1;
            memoryiy = -1;
            ix = -1;
            iy = -1;
            if (Data[Data.length - 1].matches("@")) {
                MainActivity.V.vibrate(10);
                CompletedBoxs.remove(CompletedBoxs.size() - 1);
                MainActivity.Scores.set(MainActivity.CurrentPlayer, MainActivity.Scores.get(MainActivity.CurrentPlayer) - 1);
                DashesPaths.get(MainActivity.CurrentPlayer).remove(DashesPaths.get(MainActivity.CurrentPlayer).size() - 1);
                if (Data[Data.length - 2].matches("@")) {
                    CompletedBoxs.remove(CompletedBoxs.size() - 1);
                    MainActivity.Scores.set(MainActivity.CurrentPlayer, MainActivity.Scores.get(MainActivity.CurrentPlayer) - 1);
                    MainActivity.History = "";
                    for (int i = 0; i < Data.length - 3; i++) {
                        MainActivity.History += Data[i] + ";";
                    }

                    aix = Integer.parseInt(Data[Data.length - 3].split(",")[0]);
                    aiy = Integer.parseInt(Data[Data.length - 3].split(",")[1]);

                    MainActivity.HistoryBoxes = "";
                    for (int i = 0; i < DataBoxes.length - 2; i++) {
                        MainActivity.HistoryBoxes += DataBoxes[i] + ";";
                    }
                } else {
                    MainActivity.History = "";
                    for (int i = 0; i < Data.length - 2; i++) {
                        MainActivity.History += Data[i] + ";";
                    }
                    aix = Integer.parseInt(Data[Data.length - 2].split(",")[0]);
                    aiy = Integer.parseInt(Data[Data.length - 2].split(",")[1]);
                    MainActivity.HistoryBoxes = "";
                    for (int i = 0; i < DataBoxes.length - 1; i++) {
                        MainActivity.HistoryBoxes += DataBoxes[i] + ";";
                    }
                }
            } else if ((Data[Data.length - 1].contains("|")) && Data.length != 1) {
                    MainActivity.V.vibrate(10);
                    MainActivity.CurrentPlayer -= 1;
                    if (MainActivity.CurrentPlayer == -1) {
                        MainActivity.CurrentPlayer = MainActivity.Players.size() - 1;
                    }
                    MainActivity.History = "";
                    for (int i = 0; i < Data.length - 2; i++) {
                        MainActivity.History += Data[i] + ";";
                    }
                    DashesPaths.get(MainActivity.CurrentPlayer).remove(DashesPaths.get(MainActivity.CurrentPlayer).size() - 1);
                    aix = Integer.parseInt(Data[Data.length - 2].split(",")[0]);
                    aiy = Integer.parseInt(Data[Data.length - 2].split(",")[1]);

                    MainActivity.HistoryBoxes = "";
                    for (int i = 0; i < DataBoxes.length - 1; i++) {
                        MainActivity.HistoryBoxes += DataBoxes[i] + ";";
                    }
            } else if (Data.length == 1) {
                MainActivity.V.vibrate(100);
                MainActivity.Error.setText(getResources().getStringArray(R.array.Undoecodes)[ThreadLocalRandom.current().nextInt(getResources().getStringArray(R.array.Undoecodes).length)]);
                MainActivity.Error.setBackgroundColor(getResources().getColor(R.color.Wrong));
                MainActivity.Error.setVisibility(VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.Error.setVisibility(INVISIBLE);
                    }
                }, 2000);

            } else {
                MainActivity.V.vibrate(10);
                DashesPaths.get(MainActivity.CurrentPlayer).remove(DashesPaths.get(MainActivity.CurrentPlayer).size() - 1);
                MainActivity.History = "";
                for (int i = 0; i < Data.length - 1; i++) {
                    MainActivity.History += Data[i] + ";";
                }
                aix = Integer.parseInt(Data[Data.length - 1].split(",")[0]);
                aiy = Integer.parseInt(Data[Data.length - 1].split(",")[1]);
            }
            if (MainActivity.PlayerShapes.get(MainActivity.CurrentPlayer) == 0) {
                AIPlaying = true;
                if (aix != -1)
                    AIDraw(aix,aiy);
                else
                {
                    AI = new AIService(this, MainActivity.AIMode);
                    AI.execute();
                }
            }

            MainActivity.Updates();
            invalidate();
        }
    }

    void setEndPos()
    {
        if (memoryix != -1 && memoryiy != -1)
        {
            MainActivity.History = MainActivity.History + (int)(xi + memoryix) + "," + (int)(yi + memoryiy) +";";
            DashesPath = new Path();
            DashesPath.moveTo(ix, iy);
            float cx = (memoryix * dotgap + ((width - (gridx * dotgap))/2) + (dotgap/2));
            float cy = (memoryiy * dotgap + ((height - (gridy * dotgap))/2) + (dotgap/2));
            DashesPath.lineTo(cx, cy);
            DashesPaths.get(MainActivity.CurrentPlayer).add(DashesPath);
            DashesPath = null;
            CheckClosure();
            cx = (int) ((ix - (dotgap/2) - ((width - (gridx * dotgap))/2))/dotgap);
            cy = (int) ((iy - (dotgap/2) - ((height - (gridy * dotgap))/2))/dotgap);
            DotsPaints.get((int)cx).get((int)cy).clearShadowLayer();
            DotsPaints.get(memoryix).get(memoryiy).clearShadowLayer();
            ix = -1;
            iy = -1;
            memoryiy = -1;
            memoryix = -1;
            if (MainActivity.PlayerShapes.get(MainActivity.CurrentPlayer) == 0)
            {
                AIPlaying = true;
                AI = new AIService(this,MainActivity.AIMode);
                AI.execute();
            }
            MainActivity.Timer.cancel();
            if (checkfinish())
            {
                MainActivity.rb.setEnabled(false);
                MainActivity.pb.setEnabled(false);
                Intent I = new Intent(getContext(),GameFinish.class);

                int MaxScoreIndex = 0;
                for (int i = 1; i < MainActivity.Scores.size();i++)
                {
                    if(MainActivity.Scores.get(MaxScoreIndex) < MainActivity.Scores.get(i))
                        MaxScoreIndex = i;
                }
                List<Integer> MSC = new ArrayList<>();
                for (int i = 0;i < MainActivity.Scores.size();i++)
                {
                    if (MainActivity.Scores.get(i) == MainActivity.Scores.get(MaxScoreIndex))
                        MSC.add(i);
                }

                if (MSC.size() > 1)
                {
                    String pnames = "";
                    for (int i = 0; i < MSC.size();i++)
                    {
                        pnames += MainActivity.Players.get(MSC.get(i)) + " ";
                    }

                    I.putExtra("Winner",pnames);
                    I.putExtra("Points",MainActivity.Scores.get(MaxScoreIndex));
                    I.putExtra("Match Tied",true);
                }
                else
                {
                    I.putExtra("Winner",MainActivity.Players.get(MaxScoreIndex));
                    I.putExtra("Points",MainActivity.Scores.get(MaxScoreIndex));
                    I.putExtra("Match Tied",false);
                }

                getContext().startActivity(I);

            }
            else {
                MainActivity.Timer.start();
            }

        }
        else
        {
            DashesPath = null;
            int cx = (int) ((ix - (dotgap/2) - ((width - (gridx * dotgap))/2))/dotgap);
            int cy = (int) ((iy - (dotgap/2) - ((height - (gridy * dotgap))/2))/dotgap);
            DotsPaints.get(cx).get(cy).clearShadowLayer();
            ix = -1;
            iy = -1;
        }

    }

    boolean checkfinish()
    {
        for (int i = 0; i < gridx;i++)
        {
            for (int j = 0; j < gridx;j++)
            {
                if (!MainActivity.History.contains(";" + (2*i) + "," + (2*j + 1) + ";") && j != gridy - 1)
                    return false;
                if (!MainActivity.History.contains(";" + (2*i + 1) + "," + (2*j) + ";") && i != gridx - 1)
                    return false;
            }
        }
        return true;
    }

    public void CloseTimer()
    {
        if (Touched)
        {
            DashesPath = null;
            TimerState = true;
            int cx = (int) ((ix - (dotgap/2) - ((width - (gridx * dotgap))/2))/dotgap);
            int cy = (int) ((iy - (dotgap/2) - ((height - (gridy * dotgap))/2))/dotgap);
            DotsPaints.get((int)cx).get((int)cy).clearShadowLayer();
            DotsPaints.get(memoryix).get(memoryiy).clearShadowLayer();

            MainActivity.Error.setText(getResources().getStringArray(R.array.TimeUpHandsOff)[ThreadLocalRandom.current().nextInt(getResources().getStringArray(R.array.TimeUpHandsOff).length)]);
            MainActivity.Error.setBackgroundColor(getResources().getColor(R.color.Wrong));
            MainActivity.Error.setVisibility(VISIBLE);
        }
        else
        {
            MainActivity.Error.setVisibility(INVISIBLE);
            MainActivity.Timer = ValueAnimator.ofInt(0,10000);
            MainActivity.Timer.setDuration(10000);
            MainActivity.previous = 10;
            MainActivity.Timer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = 10000 - (int)animation.getAnimatedValue();
                    Log.d("Bones"," " + value + " " + MainActivity.previous + " " + (value/1000 == MainActivity.previous));
                    MainActivity.timerbar.setProgress(value);
                    MainActivity.timerbar.setVisibility(View.VISIBLE);
                    if (!MainActivity.Timers){
                        MainActivity.timerbar.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        if (value/1000 != MainActivity.previous && value>3000)
                        {
                            Log.d("Time"," " + value);
                            MainActivity.Tick.start();
                            MainActivity.previous = value/1000;
                        }
                        else if (value/1000 != MainActivity.previous) {
                            Log.d("Time"," " + value);
                            MainActivity.Tock.start();
                            MainActivity.previous = value/1000;
                        }
                    }
                    if (value == 0 && MainActivity.Timers)
                    {
                        MainActivity.D.CloseTimer();
                    }
                }
            });
            MainActivity.Timer.start();

            MainActivity.Scores.set(MainActivity.CurrentPlayer, MainActivity.Scores.get(MainActivity.CurrentPlayer) - 1);

            if (ix != -1 && iy != -1) {
                int cx = (int) ((ix - (dotgap / 2) - ((width - (gridx * dotgap)) / 2)) / dotgap);
                int cy = (int) ((iy - (dotgap / 2) - ((height - (gridy * dotgap)) / 2)) / dotgap);
                DotsPaints.get((int) cx).get((int) cy).clearShadowLayer();
            }
            if (memoryiy != -1 && memoryix != -1)
                DotsPaints.get(memoryix).get(memoryiy).clearShadowLayer();

            ix = -1;
            iy = -1;
            memoryiy = -1;
            memoryix = -1;

            MainActivity.CurrentPlayer += 1;
            if (MainActivity.CurrentPlayer == MainActivity.Players.size())
            {
                MainActivity.CurrentPlayer = 0;
            }
            MainActivity.History += "|" + MainActivity.CurrentPlayer + ";";
            MainActivity.HistoryBoxes += "|" + MainActivity.CurrentPlayer + ";";
            MainActivity.Updates();
            if (MainActivity.PlayerShapes.get(MainActivity.CurrentPlayer) == 0)
            {
                AIPlaying = true;
                AI = new AIService(this,MainActivity.AIMode);
                AI.execute();
            }
        }
    }

    void CheckClosure()
    {
        boolean changesmade = false;
        if (yi == memoryiy && MainActivity.History.contains(";" + (int)(2 * xi) + "," + (int) (2 * yi + 1) + ";") && MainActivity.History.contains(";" + (int)(2 * memoryix) + "," + (int) (2 * yi + 1) + ";") && MainActivity.History.contains(";" + (int)(xi + memoryix) + "," + (int) (2 * yi + 2) + ";"))
        {
            float posx = memoryix * dotgap + ((width - (gridx * dotgap))/2) + (dotgap/2) + ix;
            posx /= 2;
            float posy = (yi + 1) * dotgap + ((height - (gridy * dotgap))/2) + (dotgap/2) + iy;
            posy /=2;
            if(!MainActivity.HistoryBoxes.contains(";" + posx + "," + posy +";"))
            {
                changesmade = true;
                CompletedBoxs.add(new CompletedBoxes(MainActivity.PlayerShapes.get(MainActivity.CurrentPlayer),MainActivity.Players.get(MainActivity.CurrentPlayer),posx,posy));
                MainActivity.Scores.set(MainActivity.CurrentPlayer,MainActivity.Scores.get(MainActivity.CurrentPlayer) + 1);
                MainActivity.History += "@;";
                MainActivity.HistoryBoxes += "" + posx + "," + posy + ";";
            }
        }
        if (yi == memoryiy && MainActivity.History.contains(";" + (int)(2 * xi) + "," + (int) (2 * yi - 1) + ";") && MainActivity.History.contains(";" + (int)(2 * memoryix) + "," + (int) (2 * yi - 1) + ";") && MainActivity.History.contains(";" + (int)(xi + memoryix) + "," + (int) (2 * yi - 2) + ";"))
        {
            float posx = memoryix * dotgap + ((width - (gridx * dotgap))/2) + (dotgap/2) + ix;
            posx /= 2;
            float posy = (yi - 1) * dotgap + ((height - (gridy * dotgap))/2) + (dotgap/2) + iy;
            posy /=2;
            if(!MainActivity.HistoryBoxes.contains(";" + posx + "," + posy +";"))
            {
                changesmade = true;
                CompletedBoxs.add(new CompletedBoxes(MainActivity.PlayerShapes.get(MainActivity.CurrentPlayer),MainActivity.Players.get(MainActivity.CurrentPlayer),posx,posy));
                MainActivity.Scores.set(MainActivity.CurrentPlayer,MainActivity.Scores.get(MainActivity.CurrentPlayer) + 1);
                MainActivity.History += "@;";
                MainActivity.HistoryBoxes += "" + posx + "," + posy + ";";
            }
        }
        if (xi == memoryix && MainActivity.History.contains(";" + (int)(2 * xi + 1) + "," + (int) (2 * yi) + ";") && MainActivity.History.contains(";" + (int)(2 * xi + 1) + "," + (int) (2 * memoryiy) + ";") && MainActivity.History.contains(";" + (int)(2 * xi + 2) + "," + (int) (memoryiy + yi) + ";"))
        {
            float posx = (xi + 1) * dotgap + ((width - (gridx * dotgap))/2) + (dotgap/2) + ix;
            posx /= 2;
            float posy = memoryiy * dotgap + ((height - (gridy * dotgap))/2) + (dotgap/2) + iy;
            posy /=2;
            if(!MainActivity.HistoryBoxes.contains(";" + posx + "," + posy +";"))
            {
                changesmade = true;
                CompletedBoxs.add(new CompletedBoxes(MainActivity.PlayerShapes.get(MainActivity.CurrentPlayer),MainActivity.Players.get(MainActivity.CurrentPlayer),posx,posy));
                MainActivity.Scores.set(MainActivity.CurrentPlayer,MainActivity.Scores.get(MainActivity.CurrentPlayer) + 1);
                MainActivity.History += "@;";
                MainActivity.HistoryBoxes += "" + posx + "," + posy + ";";
            }
        }
        if (xi == memoryix && MainActivity.History.contains(";" + (int)(2 * xi - 1) + "," + (int) (2 * yi) + ";") && MainActivity.History.contains(";" + (int)(2 * xi - 1) + "," + (int) (2 * memoryiy) + ";") && MainActivity.History.contains(";" + (int)(2 * xi -2) + "," + (int) (memoryiy + yi) + ";"))
        {
            float posx = (xi - 1) * dotgap + ((width - (gridx * dotgap))/2) + (dotgap/2) + ix;
            posx /= 2;
            float posy = memoryiy * dotgap + ((height - (gridy * dotgap))/2) + (dotgap/2) + iy;
            posy /=2;
            if(!MainActivity.HistoryBoxes.contains(";" + posx + "," + posy +";"))
            {
                changesmade = true;
                CompletedBoxs.add(new CompletedBoxes(MainActivity.PlayerShapes.get(MainActivity.CurrentPlayer),MainActivity.Players.get(MainActivity.CurrentPlayer),posx,posy));
                MainActivity.Scores.set(MainActivity.CurrentPlayer,MainActivity.Scores.get(MainActivity.CurrentPlayer) + 1);
                MainActivity.History += "@;";
                MainActivity.HistoryBoxes += "" + posx + "," + posy + ";";
            }
        }

        if (!changesmade)
        {
            MainActivity.CurrentPlayer += 1;
            if (MainActivity.CurrentPlayer == MainActivity.Players.size())
            {
                MainActivity.CurrentPlayer = 0;
            }
            MainActivity.History += "|" + MainActivity.CurrentPlayer + ";";
            MainActivity.HistoryBoxes += "|" + MainActivity.CurrentPlayer + ";";
        }
        MainActivity.Updates();
    }

    void showposi(float x, float y)
    {
        if(memoryix!=-1&&memoryiy!=-1) {
            (DotsPaints.get(memoryix)).get(memoryiy).clearShadowLayer();
            memoryiy = -1;
            memoryix = -1;
        }

        if (x > ix - dotgap/4 && x < ix + dotgap / 4 && y > iy - dotgap/4 && y < iy + dotgap / 4 )
        {

        }
        else if (x > ix - dotgap/4 && x < ix + dotgap / 4)
        {
            if (y > iy)
            {
                if (yi != gridy - 1 && !MainActivity.History.contains(";" + (int)(2 * xi) +","+(int)(yi + yi + 1) + ";")) {
                    (DotsPaints.get(xi)).get(yi + 1).setShadowLayer(dotr, 0.0f, 2.0f, DotShadowColor);
                    memoryiy = yi + 1;
                    memoryix = xi;
                }
            }
            else
            {
                if (yi != 0 && !MainActivity.History.contains(";" + (int)(2 * xi) +","+(int)(yi + yi - 1) + ";")) {
                    (DotsPaints.get(xi)).get(yi - 1).setShadowLayer(dotr, 0.0f, 2.0f, DotShadowColor);
                    memoryiy = yi - 1;
                    memoryix = xi;
                }
            }
        }
        else if (y > iy - dotgap/4 && y < iy + dotgap / 4 )
        {
            if (x > ix)
            {
                if (xi != gridx - 1 && !MainActivity.History.contains(";" + (int)(xi + xi + 1) +","+(int)(2 * yi) + ";")) {
                    (DotsPaints.get(xi + 1)).get(yi).setShadowLayer(dotr, 0.0f, 2.0f, DotShadowColor);
                    memoryiy = yi;
                    memoryix = xi + 1;
                }
            }
            else
            {
                if (xi != 0 && !MainActivity.History.contains(";" + (int)(xi + xi - 1) +","+(int)(2 * yi) + ";")) {
                    (DotsPaints.get(xi - 1)).get(yi).setShadowLayer(dotr, 0.0f, 2.0f, DotShadowColor);
                    memoryiy = yi;
                    memoryix = xi - 1;
                }
            }
        }
    }

    void setstartpos(float x,float y) {
        for (int i = 0; i < gridx && ix == -1 && iy == -1; i++) {
            for (int j = 0; j < gridy && ix == -1 && iy == -1; j++) {
                if (i * dotgap + ((width - (gridx * dotgap)) / 2) + ((dotgap) / 4) < x && i * dotgap + ((width - (gridx * dotgap)) / 2) + ((3 * dotgap) / 4) > x) {
                    if (j * dotgap + ((height - (gridy * dotgap)) / 2) + ((dotgap) / 4) < y && j * dotgap + ((height - (gridy * dotgap)) / 2) + ((3 * dotgap) / 4) > y) {

                        if (CheckStartHistory(i,j)) {
                            ix = i * dotgap + ((width - (gridx * dotgap)) / 2) + (dotgap / 2);
                            iy = j * dotgap + ((height - (gridy * dotgap)) / 2) + (dotgap / 2);
                            xi = i;
                            yi = j;
                            (DotsPaints.get(i)).get(j).setShadowLayer(dotr, 0.0f, 2.0f, DotShadowColor);
                            MainActivity.V.vibrate(10);
                        }
                        else
                        {
                            i = gridx;
                            j = gridy;
                            ix = -1;
                            iy = -1;
                        }
                    }
                }
            }
        }

        if (ix == -1 && iy == -1 && x > ((width - (gridx * dotgap)) / 2) - dotgap/2 && x < ((width + (gridx * dotgap)) / 2) + dotgap / 2 && y > ((height - (gridy * dotgap)) / 2) - dotgap/2 && y < ((height + (gridy * dotgap)) / 2) + dotgap/2) {
            MainActivity.V.vibrate(100);
        }

    }

    boolean CheckStartHistory(int xi, int yi)
    {
        if (MainActivity.History.contains(";" + (xi + xi) + "," + (yi + yi + 1) + ";") && MainActivity.History.contains(";" + (xi + xi) + "," + (yi + yi - 1) + ";") && MainActivity.History.contains(";" + (xi + xi + 1) + "," + (yi + yi) + ";") && MainActivity.History.contains(";" + (xi + xi - 1) + "," + (yi + yi) + ";"))
        {
            return false;
        }
        else if (MainActivity.History.contains(";" + (xi + xi) + "," + (yi + yi + 1) + ";") && MainActivity.History.contains(";" + (xi + xi) + "," + (yi + yi - 1) + ";") && (xi == 0 || xi == gridx - 1) && yi != 0 && yi != gridy - 1)
        {
            if (xi == 0 && MainActivity.History.contains(";" + 1 + "," + (yi + yi) + ";"))
            {
                return false;
            }
            else if (xi == gridx - 1 && MainActivity.History.contains(";" + (gridx - 1 + gridx - 2) + "," + (yi + yi) + ";"))
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else if (MainActivity.History.contains(";" + (xi + xi + 1) + "," + (yi + yi) + ";") && MainActivity.History.contains(";" + (xi + xi - 1) + "," + (yi + yi) + ";") && xi != 0 && xi != gridx - 1 && (yi == 0 || yi == gridy - 1))
        {
            if (yi == 0 && MainActivity.History.contains(";" + (xi + xi) + "," + 1 + ";"))
            {
                return false;
            }
            else if (yi == gridy - 1 && MainActivity.History.contains(";" + (xi + xi) + "," + (gridy - 1 + gridy - 2) + ";"))
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else if (xi == 0)
        {
            if (yi == 0 && MainActivity.History.contains(";0,1;") && MainActivity.History.contains(";1,0;"))
            {
                return false;
            }
            else if (yi == gridy - 1 && MainActivity.History.contains(";0," + (gridy - 1 + gridy - 2) + ";") && MainActivity.History.contains(";1," + (gridy - 1 + gridy - 1) +  ";"))
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else if (xi == gridx -1)
        {
            if (yi == 0 && MainActivity.History.contains(";"+(gridx - 1 + gridx - 1)+",1;") && MainActivity.History.contains(";"+(gridx - 1 + gridx - 2) +",0;"))
            {
                return false;
            }
            else if (yi == gridy - 1 && MainActivity.History.contains(";" +(gridx -1 + gridx - 1) + "," + (gridy - 1 + gridy - 2) + ";")&& MainActivity.History.contains(";" +(gridx -1 + gridx - 2) + "," + (gridy - 1 + gridy - 1) + ";"))
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return true;
        }
    }

    public void AIDraw(int x, int y)
    {
        int xx = x/2;
        int yy = y/2;

        x -= xx;
        y -= yy;

        (DotsPaints.get(x)).get(y).setShadowLayer(dotr, 0.0f, 2.0f, DotShadowColor);
        (DotsPaints.get(xx)).get(yy).setShadowLayer(dotr, 0.0f, 2.0f, DotShadowColor);

        if (ThreadLocalRandom.current().nextBoolean()) {
            xi = x;
            yi = y;
            memoryiy = yy;
            memoryix = xx;
        }
        else
        {
            xi = xx;
            yi = yy;
            memoryiy = y;
            memoryix = x;
        }


        ix = xi * dotgap + ((width - (gridx * dotgap)) / 2) + (dotgap / 2);
        iy = yi * dotgap + ((height - (gridy * dotgap)) / 2) + (dotgap / 2);

        DashesPath = new Path();
        DashesPath.moveTo(ix,iy);


        ValueAnimator valueAnimatorLoading = ValueAnimator.ofFloat(0, dotgap);
        valueAnimatorLoading.setDuration((4 - MainActivity.AIMode) * 500);
        valueAnimatorLoading.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float AIAnimatefactor = (float)valueAnimator.getAnimatedValue(); //factor for use in onDraw()
                if (xi == memoryix)
                {
                    if (yi > memoryiy)
                    {
                        DashesPath.lineTo(ix,iy  - AIAnimatefactor);
                    }
                    else
                    {
                        DashesPath.lineTo(ix,iy  + AIAnimatefactor);
                    }
                }
                else
                {
                    if (xi > memoryix)
                    {
                        DashesPath.lineTo(ix - AIAnimatefactor,iy);
                    }
                    else
                    {
                        DashesPath.lineTo(ix + AIAnimatefactor,iy);
                    }
                }
                if (AIAnimatefactor == dotgap)
                {
                    AIPlaying = false;
                    setEndPos();
                }
                invalidate();
            }
        });
        valueAnimatorLoading.start();
    }

}

class AIService
{
    List <List<String>> PosibleMoves;

    String History;
    int gridx,gridy;
    public int mode;

    String move;
    DotsService d;

    public AIService (DotsService d,int mode)
    {
        this.d = d;
        this.mode = mode;
    }

    void execute()
    {
        History = MainActivity.History;
        gridx = d.gridx;
        gridy = d.gridy;

        PosibleMoves = new ArrayList<List<String>>();
        PosibleMoves.add(new ArrayList<String>());
        PosibleMoves.add(new ArrayList<String>());
        PosibleMoves.add(new ArrayList<String>());

        for (int i=0; i < gridx; i++)
        {
            for (int j = 0; j < gridy; j++)
            {
                if (!History.contains(";" + (2*i) + "," + (2*j + 1) + ";") && j != gridy - 1) {

                    if (HC(2*i,2*j + 1) > 4)
                    {
                        PosibleMoves.get(0).add((2*i) + "," + (2*j + 1));
                    }
                    else  if (HC(2*i,2*j + 1) > 2)
                    {
                        if (History.contains(";" + (2*i - 1) + "," + (2*j) +";") && History.contains(";" + (2*i - 1) + "," + (2*j + 2) +";") && History.contains(";" + (2*i - 2) + "," + (2*j + 1)))
                        {
                            PosibleMoves.get(0).add((2*i) + "," + (2*j + 1));
                        }
                        else  if (History.contains(";" + (2*i + 1) + "," + (2*j) +";") && History.contains(";" + (2*i + 1) + "," + (2*j + 2) +";") && History.contains(";" + (2*i + 2) + "," + (2*j + 1)))
                        {
                            PosibleMoves.get(0).add((2*i) + "," + (2*j + 1));
                        }
                        else
                        {
                            PosibleMoves.get(2).add((2*i) + "," + (2*j + 1));
                        }

                    }
                    else if (HC(2*i,2*j + 1) < 2)
                    {
                        PosibleMoves.get(1).add((2*i) + "," + (2*j + 1));
                    }
                    else
                    {
                        if (History.contains(";" + (2*i - 2) + "," + (2*j + 1)) && History.contains(";" + (2*i + 2) + "," + (2*j + 1)))
                        {
                            PosibleMoves.get(1).add((2*i) + "," + (2*j + 1));
                        }
                        else
                        {
                            PosibleMoves.get(2).add((2*i) + "," + (2*j + 1));
                        }
                    }
                }

                if (!History.contains(";" + (2*i + 1) + "," + (2*j) + ";") && i != gridx - 1)
                {
                    if (HCy(2*i + 1,2*j) > 4)
                    {
                        PosibleMoves.get(0).add((2*i + 1) + "," + (2*j));
                    }
                    else if (HCy(2*i + 1,2*j) > 2)
                    {
                        if (History.contains(";" + (2*i) + "," + (2*j - 1) +";") && History.contains(";" + (2*i + 2) + "," + (2*j - 1) +";") && History.contains(";" + (2*i + 1) + "," + (2*j - 2)))
                        {
                            PosibleMoves.get(0).add((2*i + 1) + "," + (2*j));
                        }
                        else  if (History.contains(";" + (2*i) + "," + (2*j + 1) +";") && History.contains(";" + (2*i + 2) + "," + (2*j + 1) +";") && History.contains(";" + (2*i + 1) + "," + (2*j + 2)))
                        {
                            PosibleMoves.get(0).add((2*i + 1) + "," + (2*j));
                        }
                        else
                        {
                            PosibleMoves.get(2).add((2*i + 1) + "," + (2*j));
                        }
                    }
                    else if (HCy(2*i + 1,2*j) < 2)
                    {
                        PosibleMoves.get(1).add((2*i + 1) + "," + (2*j));
                    }
                    else
                    {
                        if (History.contains(";" + (2*i+1) + "," + (2*j - 2)) && History.contains(";" + (2*i + 1) + "," + (2*j + 2)))
                        {
                            PosibleMoves.get(1).add((2*i + 1) + "," + (2*j));
                        }
                        else
                        {
                            PosibleMoves.get(2).add((2*i + 1) + "," + (2*j));
                        }
                    }

                }
            }
        }

        if (mode == 1)
        {
            Random A = new Random();
            int a = A.nextInt(3);
            while (PosibleMoves.get(a).size() == 0)
            {
                a = A.nextInt(3);
            }
            move = PosibleMoves.get(a).get(A.nextInt(PosibleMoves.get(a).size()));
        }
        else if (mode == 2)
        {
            Random A = new Random();
            if (PosibleMoves.get(0).size() > 0)
            {
                move = PosibleMoves.get(0).get(A.nextInt(PosibleMoves.get(0).size()));
            }
            else
            {
                int a = A.nextInt(3);
                while (PosibleMoves.get(a).size() == 0)
                {
                    a = A.nextInt(3);
                }
                move = PosibleMoves.get(a).get(A.nextInt(PosibleMoves.get(a).size()));
            }

        }
        else
        {
            Random A = new Random();
            if (PosibleMoves.get(0).size() > 0)
            {
                move = PosibleMoves.get(0).get(A.nextInt(PosibleMoves.get(0).size()));
            }
            else if (PosibleMoves.get(1).size() > 0)
            {
                List<String> Successmoves = new ArrayList<>();

                for (int i = 0; i < PosibleMoves.get(1).size(); i++)
                {
                    move = PosibleMoves.get(1).get(i);
                    int x = Integer.parseInt(move.split(",")[0]);
                    int y = Integer.parseInt(move.split(",")[1]);

                    int c = 2;

                    if (x%2 == 0)
                    {
                        c = HC(x,y);
                    }
                    else
                    {
                        c = HCy(x,y);
                    }

                    if (c == 0 && MainActivity.Players.size() == 3)
                    {
                        Successmoves.add(PosibleMoves.get(1).get(i));
                    }
                    else if (c == 1 && MainActivity.Players.size() == 2)
                    {
                        Successmoves.add(PosibleMoves.get(1).get(i));
                    }
                }

                if (Successmoves.size() > 0)
                {
                    move = Successmoves.get(A.nextInt(Successmoves.size()));
                }
                else
                {
                    move = PosibleMoves.get(1).get(A.nextInt(PosibleMoves.get(1).size()));
                }

            }
            else
            {
                List<String> Successmoves = new ArrayList<>();

                for (int i = 0; i < PosibleMoves.get(2).size(); i++)
                {
                    move = PosibleMoves.get(2).get(i);
                    int x = Integer.parseInt(move.split(",")[0]);
                    int y = Integer.parseInt(move.split(",")[1]);

                    int c = 0;

                    if (x%2 == 0)
                    {
                        c = HC(x,y);
                    }
                    else
                    {
                        c = HCy(x,y);
                    }

                    if (c == 2)
                    {
                        Successmoves.add(PosibleMoves.get(2).get(i));
                    }
                    else if (c == 3)
                    {
                        Successmoves.add(PosibleMoves.get(2).get(i));
                    }
                }

                if (Successmoves.size() > 0)
                {
                    move = Successmoves.get(A.nextInt(Successmoves.size()));
                }
                else
                {
                    move = PosibleMoves.get(2).get(A.nextInt(PosibleMoves.get(1).size()));
                }
            }

        }
        int x = Integer.parseInt(move.split(",")[0]);
        int y = Integer.parseInt(move.split(",")[1]);

        d.AIDraw(x,y);
    }

    int HC(int x,int y)
    {
        int c = 0;
        if (History.contains(";" + (x - 1) + "," + (y - 1) + ";"))
        {
            c += 1;
        }
        if (History.contains(";" + (x - 2) + "," + (y) + ";"))
        {
            c += 1;
        }
        if (History.contains(";" + (x - 1) + "," + (y + 1) + ";"))
        {
            c += 1;
        }
        if (History.contains(";" + (x + 1) + "," + (y + 1) + ";"))
        {
            c += 1;
        }
        if (History.contains(";" + (x + 2) + "," + (y) + ";"))
        {
            c += 1;
        }
        if (History.contains(";" + (x + 1) + "," + (y - 1) + ";"))
        {
            c += 1;
        }

        return c;
    }

    int HCy(int x,int y)
    {
        int c = 0;
        if (History.contains(";" + (x - 1) + "," + (y - 1) + ";"))
        {
            c += 1;
        }
        if (History.contains(";" + (x) + "," + (y - 2) + ";"))
        {
            c += 1;
        }
        if (History.contains(";" + (x + 1) + "," + (y - 1) + ";"))
        {
            c += 1;
        }
        if (History.contains(";" + (x + 1) + "," + (y + 1) + ";"))
        {
            c += 1;
        }
        if (History.contains(";" + (x) + "," + (y + 2) + ";"))
        {
            c += 1;
        }
        if (History.contains(";" + (x - 1) + "," + (y + 1) + ";"))
        {
            c += 1;
        }

        return c;
    }

}


class CompletedBoxes
{
    public int PlayerSymbol;
    public String PlayerInitial;
    public float posx;
    public float posy;

    public CompletedBoxes (int PlayerSymbol, String PlayerInitial,float posx,float posy)
    {
        this.PlayerSymbol = PlayerSymbol;
        this.PlayerInitial = PlayerInitial;
        this.posx = posx;
        this.posy = posy;
    }
}

class MediaService
{
    public static MediaPlayer Player = null;
    public static Context C;
    public static void start (MediaPlayer toplay, Context C)
    {
        MediaService.C = C;
        if (Player != toplay) {
            Player = toplay;
            Player.setVolume(C.getSharedPreferences("SettingsData", Context.MODE_PRIVATE).getInt("Volume", 100) / 100f, C.getSharedPreferences("SettingsData", Context.MODE_PRIVATE).getInt("Volume", 100) / 100f);
            Player.setLooping(true);
        }

        if (!Player.isPlaying())
        {
            Player.start();
        }
    }

    public static void setVolume()
    {
        if (Player != null)
        {
            Player.setVolume(C.getSharedPreferences("SettingsData", Context.MODE_PRIVATE).getInt("Volume", 100) / 100f, C.getSharedPreferences("SettingsData", Context.MODE_PRIVATE).getInt("Volume", 100) / 100f);
        }
    }

    public static void stop()
    {
        if (Player != null && Player.isPlaying())
        {
            Player.stop();
        }
    }
}