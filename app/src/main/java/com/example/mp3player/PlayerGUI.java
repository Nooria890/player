package com.example.mp3player;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import static android.graphics.PorterDuff.Mode.SRC_IN;

public class PlayerGUI extends AppCompatActivity {
     Button BtnPuse, BtnNext, BtnBack;
     TextView  Song;
     SeekBar seekBar;
     String sName;

     static MediaPlayer myMediaPlayer;
     int Position;
     ArrayList<File> mySongs;
     Thread updateSeekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_gui);

        BtnPuse = (Button) findViewById(R.id.BtnPuse);
        BtnBack = (Button) findViewById(R.id.BtnBack);
        BtnNext = (Button) findViewById(R.id.BtnNext);
        Song = (TextView) findViewById(R.id.Song);
        seekBar = (SeekBar) findViewById(R.id.seekBar);


        updateSeekBar = new Thread() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                int totalDuration = myMediaPlayer.getDuration();
                int currentPosition = 0;

                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = myMediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (myMediaPlayer != null) {
                    myMediaPlayer.stop();
                    myMediaPlayer.release();
                }
                Intent i = getIntent();
                Bundle bundle = i.getExtras();

                mySongs = (ArrayList) bundle.getParcelableArrayList("song");

                sName = mySongs.get(Position).getName().toString();
                String songName = i.getStringExtra("songName");

                Song.setText(songName);
                Song.setSelected(true);

                Position= bundle.getInt("pos", 0);
                Uri u= Uri.parse(mySongs.get(Position).toString());

                myMediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                myMediaPlayer.start();
                seekBar.setMax(myMediaPlayer.getDuration());

                updateSeekBar.start();

                seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
                seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), SRC_IN);

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        myMediaPlayer.seekTo(seekBar.getProgress());

                    }
                });


                BtnPuse.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(View v) {
                        seekBar.setMax(myMediaPlayer.getDuration());

                        if (myMediaPlayer.isPlaying()){
                            BtnPuse.setBackgroundResource(R.raw.play);
                            myMediaPlayer.pause();
                        }
                        else
                        {
                            BtnPuse.setBackgroundResource(R.raw.stop);
                            myMediaPlayer.start();
                        }
                    }
                });

                BtnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myMediaPlayer.stop();
                        myMediaPlayer.release();
                        Position= ((Position+1)%mySongs.size());
                        Uri u= Uri.parse(mySongs.get(Position).toString());
                        myMediaPlayer= MediaPlayer.create(getApplicationContext(),u);
                        sName= mySongs.get(Position).getName().toString();
                        Song.setText(sName);
                        myMediaPlayer.start();

                    }
                });

                BtnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myMediaPlayer.stop();
                        myMediaPlayer.release();

                        Position=((Position-1)<0)?(mySongs.size()-1): (Position-1);
                        Uri u= Uri.parse(mySongs.get(Position).toString());
                        myMediaPlayer= myMediaPlayer.create(getApplicationContext(),u);
                        sName= mySongs.get(Position).getName().toString();
                        Song.setText(sName);
                        myMediaPlayer.start();

                    }
                });





            }
        };
    }}