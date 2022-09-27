package com.mrapps.youtubechannel.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mrapps.youtubechannel.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class VideoPlay extends AppCompatActivity {


    String name, url;

    YouTubePlayerView youTubePlayerView;
    CardView volumeButton, playPauseButton, fullscreenButton;
    ImageView playPauseImage, volumeImage;
    TextView titleText;
    private AudioManager audioManager = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        setupIntentData();
        setupUI();
        setupPlayer();
        setupClickListener();


    }

    private void setupClickListener() {
        fullscreenButton.setOnClickListener(view -> {

            if (!youTubePlayerView.isFullScreen()) {
                youTubePlayerView.toggleFullScreen();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }

        });

        volumeButton.setOnClickListener(view -> volumeDialog());

    }

    private void setupPlayer() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        youTubePlayerView.setEnableAutomaticInitialization(true);
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError error) {
                super.onError(youTubePlayer, error);
            }

            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                youTubePlayer.loadVideo(url, 0);
                youTubePlayer.unMute();
            }

            @Override
            public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                super.onStateChange(youTubePlayer, state);

                playPauseButton.setOnClickListener(view -> {
                    if (state == PlayerConstants.PlayerState.PLAYING) {
                        youTubePlayer.pause();
                        playPauseImage.setBackgroundResource(R.drawable.ic_play);
                    } else {
                        youTubePlayer.play();
                        playPauseImage.setBackgroundResource(R.drawable.ic_pause);
                    }

                });

                if (state == PlayerConstants.PlayerState.PLAYING) {
                    playPauseImage.setBackgroundResource(R.drawable.ic_pause);
                } else {
                    playPauseImage.setBackgroundResource(R.drawable.ic_play);
                }


            }
        });


    }

    private void setupUI() {
        volumeButton = findViewById(R.id.volumeButton);
        playPauseButton = findViewById(R.id.playPauseButton);
        fullscreenButton = findViewById(R.id.fullscreenButton);
        playPauseImage = findViewById(R.id.playPauseImage);
        volumeImage = findViewById(R.id.volumeImage);
        titleText = findViewById(R.id.videoTitle);

        titleText.setText(name);
    }


    private void setupIntentData() {

        name = getIntent().getStringExtra("name");
        url = getIntent().getStringExtra("url");

        Log.e("TAG", "setupIntentData: " + name + url);
    }


    private void volumeDialog() {
        Dialog dialog = new Dialog(VideoPlay.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.volume_layout);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        ImageView volumeDown = dialog.findViewById(R.id.volumeDown);
        ImageView volumeUp = dialog.findViewById(R.id.volumeHigh);
        SeekBar seekBar = dialog.findViewById(R.id.seekBar);

        seekBar.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBar.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));

        volumeDown.setOnClickListener(view -> {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            volumeDown.setBackgroundResource(R.drawable.ic_mute);
            seekBar.setProgress(0);
            volumeImage.setBackgroundResource(R.drawable.ic_mute);
        });

        volumeUp.setOnClickListener(view -> {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            volumeUp.setBackgroundResource(R.drawable.ic_volume);
            seekBar.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeImage.setBackgroundResource(R.drawable.ic_volume);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                if (progress > seekBar.getMax() / 2) {
                    volumeUp.setBackgroundResource(R.drawable.ic_volume);
                    volumeImage.setBackgroundResource(R.drawable.ic_volume);

                }
                if (progress <= seekBar.getMax() / 2) {
                    volumeUp.setBackgroundResource(R.drawable.ic_volume_medium);
                    volumeImage.setBackgroundResource(R.drawable.ic_volume_medium);
                }

                if (progress == 0) {
                    volumeDown.setBackgroundResource(R.drawable.ic_mute);
                    volumeImage.setBackgroundResource(R.drawable.ic_mute);
                }
                if (progress > 0) {
                    volumeDown.setBackgroundResource(R.drawable.ic_volume_down);
                }

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        dialog.show();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            youTubePlayerView.enterFullScreen();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            youTubePlayerView.exitFullScreen();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        youTubePlayerView.release();
    }

    @Override
    public void onBackPressed() {
        if (youTubePlayerView.isFullScreen()) {
            youTubePlayerView.exitFullScreen();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else {
            super.onBackPressed();
        }

    }
}