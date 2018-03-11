package com.example.chait.musoic;

import android.app.Activity;
import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by Utsav on 3/10/2018.
 */

public class Player extends Activity implements ExoPlayer.EventListener {

    private SeekBar seekPlayerProgress;
    private Handler handler;
    private ImageButton btnPlay;
    private TextView txtCurrentTime, txtEndTime, albumN, trackN;
    private boolean isPlaying = true;
    private Handler mainHandler;
    private RenderersFactory renderersFactory;
    private BandwidthMeter bandwidthMeter;
    private LoadControl loadControl;
    private DataSource.Factory dataSourceFactory;
    private ExtractorsFactory extractorsFactory;
    private MediaSource mediaSource;
    private TrackSelection.Factory trackSelectionFactory;
    private SimpleExoPlayer player;
    private Uri trackUri;
    private ImageButton next;
    private ImageButton previous;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        //player.getCurrentTrackSelections();
        Bundle message = getIntent().getExtras();
        long currSong = (Long) message.get("songID");
        String track = (String)message.get("Track");
        String album = (String) message.get("Album");

        trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
       //
       // Toast.makeText(this, "" + trackUri, Toast.LENGTH_LONG).show();

        albumN =findViewById(R.id.albumName);
        albumN.setText(album);
        trackN = findViewById(R.id.trackName);
        trackN.setText(track);

        renderersFactory = new DefaultRenderersFactory(getApplicationContext());
        bandwidthMeter = new DefaultBandwidthMeter();
        trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);


        DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        loadControl = new DefaultLoadControl();

        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
        player.addListener(this);

        dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), "ExoplayerDemo");
        extractorsFactory = new DefaultExtractorsFactory();
        mainHandler = new Handler();
        mediaSource = new ExtractorMediaSource(trackUri,dataSourceFactory,
                        extractorsFactory,
                        mainHandler,
                        null);

        player.prepare(mediaSource);
        if(player.getVolume()==0)
        {
            Toast.makeText(this, "TURN ON VOLUME", Toast.LENGTH_SHORT).show();
        }
        controls();

    }

    private void controls() {
        time();
        seekBar();
        play();
        next();
        setProgress();
        previous();
    }

    private void previous() {
        previous = findViewById(R.id.btnPrev);
        previous.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Toast.makeText(Player.this, "PREVIOUS SONG", Toast.LENGTH_SHORT).show();
                                        }
                                    }
        );
    }

    private void next() {
        next = findViewById(R.id.btnNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Player.this, "Next Song", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void play() {
        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.requestFocus();
        btnPlay.setImageResource(R.drawable.ic_play_arrow);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPlayPause(!isPlaying);
            }
        });
    }


    private void setPlayPause(boolean play){
        isPlaying = play;
        player.setPlayWhenReady(play);
        if(!isPlaying){
            btnPlay.setImageResource(R.drawable.ic_pause);
        }else{
            btnPlay.setImageResource(R.drawable.ic_play_arrow);
        }
    }

    private void time() {
        txtCurrentTime = findViewById(R.id.time_current);
        txtEndTime = findViewById(R.id.player_end_time);
    }

    private String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds =  timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;

        mFormatBuilder.setLength(0);

        return mFormatter.format("%02d:%02d", minutes, seconds).toString();

    }

    private void setProgress() {
       // seekPlayerProgress.setProgress(0);
        seekPlayerProgress.setMax((int) player.getDuration()/1000);
        txtCurrentTime.setText(stringForTime((int)player.getCurrentPosition()));
        txtEndTime.setText(stringForTime((int)player.getDuration()));

        if(handler == null)handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (player!= null && isPlaying) {
                    seekPlayerProgress.setMax((int) player.getDuration()/1000);
                    int mCurrentPosition = (int) player.getCurrentPosition() / 1000;
                    seekPlayerProgress.setProgress(mCurrentPosition);
                    txtCurrentTime.setText(stringForTime((int)player.getCurrentPosition()));
                    txtEndTime.setText(stringForTime((int)player.getDuration()));

                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void seekBar() {
        seekPlayerProgress = findViewById(R.id.media_progress);
        seekPlayerProgress.requestFocus();

        seekPlayerProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }

                player.seekTo(progress*1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekPlayerProgress.setMax(0);
        seekPlayerProgress.setMax((int) player.getDuration()/1000);

    }


    @Override
    protected void onResume() {
        super.onResume();
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.release();
        player.setPlayWhenReady(true);
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }


    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }


    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

}
