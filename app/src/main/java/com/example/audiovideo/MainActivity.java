package com.example.audiovideo;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener{
//UI Components
    private VideoView myvideoview;
    private Button btnPlayvideo,btnPlayMusic,btnPauseMusic;
    private SeekBar seekBarVolume;
    private SeekBar seekBarMove;

    private MediaPlayer mediaPlayer;//to play video
    private MediaController mediaController; //to play audio
    private AudioManager audioManager; //to manage the audio sound
    private Timer timer; //UI freind

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myvideoview =findViewById(R.id.myVideoView);
        btnPlayvideo =findViewById(R.id.btnPlayVideo);
        btnPauseMusic=findViewById(R.id.btnPauseMusic);
        btnPlayMusic=findViewById(R.id.btnPlayMusic);
        seekBarVolume =findViewById(R.id.seekBarVolume);
        seekBarMove =findViewById(R.id.seekBarMove);

        btnPlayvideo.setOnClickListener(MainActivity.this);
        btnPlayMusic.setOnClickListener(MainActivity.this);
        btnPauseMusic.setOnClickListener(MainActivity.this);

        mediaController =new MediaController(MainActivity.this);
        mediaPlayer = MediaPlayer.create(MainActivity.this,R.raw.music);
        audioManager= (AudioManager) getSystemService(AUDIO_SERVICE);

        int maxVolumeOfUserDevice = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolumeOfUserDevice =audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        seekBarVolume.setMax(maxVolumeOfUserDevice);
        seekBarVolume.setProgress(currentVolumeOfUserDevice);

        seekBarMove.setOnSeekBarChangeListener(MainActivity.this);
         seekBarMove.setMax(mediaPlayer.getDuration());

        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(fromUser){
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer.setOnCompletionListener(MainActivity.this);
    }

    @Override
    public void onClick(View buttonView) {

        switch (buttonView.getId()){

            case R.id.btnPlayVideo:
                Uri videoUri = Uri.parse("android.resource://"+ getPackageName()+ "/"+ R.raw.video);
                myvideoview.setVideoURI(videoUri);
                myvideoview.setMediaController(mediaController);
                mediaController.setAnchorView(myvideoview);
                myvideoview.start();
                break;

            case R.id.btnPlayMusic:
                mediaPlayer.start();
                timer = new Timer();
                //creating new UI friend
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                      seekBarMove.setProgress(mediaPlayer.getCurrentPosition());
                    }
                },0,1000);

                break;

            case R.id.btnPauseMusic:
                  mediaPlayer.pause();
                  timer.cancel();//to stop new friend
                break;
        }


    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
       if(fromUser){
           //Toast.makeText(this, progress+" ", Toast.LENGTH_SHORT).show();
           mediaPlayer.seekTo(progress);
       }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mediaPlayer.pause();

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        timer.cancel(); //new friend will not be in use if we are not playing song
        Toast.makeText(this, "Music is ended", Toast.LENGTH_SHORT).show();
    }
}