package com.project.amplifire;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by Utsav on 3/10/2018.
 */

public class Player extends Activity implements ExoPlayer.EventListener {

    private SeekBar seekPlayerProgress;
    private Handler handler;
    private TextView txtCurrentTime, txtEndTime, albumN, trackN, artistN;
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
    private ImageButton btnNext,btnPrev,btnPlay,btnShuffle,btnRepeat;
    private ImageView albumImage;
    int repeat_clickCount =0;
    int shuffle_clickCounter =0;
    int position;
    ArrayList<Song> songArray;

    private static long currentsongID;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_interface);
        Bundle message = getIntent().getExtras();
        position = (Integer) message.get("position");
        Log.v("player",position+"");
        songArray = VerticalAdapter.getSongsList();
        Song currentSong = songArray.get(position);

        updateUI(currentSong);
    }

    private void controls() {
        time();
        seekBar();
        play();
        next();
        setProgress();
        repeat();
        shuffle();
        previous();
    }

    private void repeat() {

        btnRepeat = findViewById(R.id.repeat);
        btnRepeat.requestFocus();
        btnRepeat.setImageResource(R.drawable.ic_repeat_dark);
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repeat_clickCount++;
                repeat_clickCount %=3;
                switch (repeat_clickCount)
                {
                    case 0:
                        btnRepeat.setImageResource(R.drawable.ic_repeat_one_dark);
                        onRepeatModeChanged(1);
                        break;
                    case 1:
                        btnRepeat.setImageResource(R.drawable.exo_controls_repeat_all);
                        onRepeatModeChanged(2);
                        break;
                    case 2:
                        btnRepeat.setImageResource(R.drawable.exo_controls_repeat_off);
                        onRepeatModeChanged(3);
                        break;
                }
            }
        });

    }

    private void shuffle() {
        btnShuffle = findViewById(R.id.shuffle);
        btnShuffle.requestFocus();
        btnShuffle.setImageResource(R.drawable.exo_controls_shuffle);
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shuffle_clickCounter++;
                shuffle_clickCounter%=2;

                switch (shuffle_clickCounter)
                {
                    case 0:
                        btnShuffle.setImageResource(R.drawable.exo_controls_shuffle);
                        onShuffleModeEnabledChanged(false);
                        break;
                    case 1:
                        btnShuffle.setImageResource(R.drawable.ic_shuffle_dark);
                        onShuffleModeEnabledChanged(true);
                        break;
                }
            }
        });
    }

    private void previous() {
        btnPrev = findViewById(R.id.btnPrev);
        btnPrev.requestFocus();
        btnPrev.setImageResource(R.drawable.ic_skip_previous_dark);
        btnPrev.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
//                                            onPositionDiscontinuity(position);
                                            player.stop();
                                            position--;
                                            Song newSong = songArray.get(position);
                                            updateUI(newSong);
                                            player.setPlayWhenReady(true);
                                        }
                                    }
        );
    }

    private void next() {
        btnNext = findViewById(R.id.btnNext);
        btnNext.requestFocus();
        btnNext.setImageResource(R.drawable.ic_skip_next_dark);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(Player.this, "Next Song", Toast.LENGTH_SHORT).show();
                player.stop();
                position++;
                Song newSong = songArray.get(position);
                updateUI(newSong);
                player.setPlayWhenReady(true);

            }
        });
    }

    private void play() {
        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.requestFocus();
        btnPlay.setImageResource(R.drawable.ic_pause_circle_filled);
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
        if(isPlaying){
            setProgress();
            btnPlay.setImageResource(R.drawable.ic_pause_circle_filled);
        }else{
            setProgress();
            btnPlay.setImageResource(R.drawable.ic_play_circle_filled);
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
       // player.release();
        player.setPlayWhenReady(true);
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

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
    public void onRepeatModeChanged(int repeatMode) {

        switch (repeatMode) {
            case 1:
                player.setRepeatMode(com.google.android.exoplayer2.Player.REPEAT_MODE_ONE);
                break;
            case 2:
                player.setRepeatMode(com.google.android.exoplayer2.Player.REPEAT_MODE_ALL);
                break;
            case 3:
                player.setRepeatMode(com.google.android.exoplayer2.Player.REPEAT_MODE_OFF);
                break;
        }
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        if(shuffleModeEnabled)
        {
            player.setShuffleModeEnabled(true);
            int shuffle =player.getCurrentPeriodIndex();
            Song shuffledSong = songArray.get(shuffle);
            updateUI(shuffledSong);
        }
        else
        {
            player.setShuffleModeEnabled(false);
        }

    }


    @Override
    public void onPlayerError(ExoPlaybackException error) {

        Snackbar snackbar = Snackbar.make(findViewById(R.id.player_Layout),"ERROR: " +error,Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

//    @Override
//    public void onPositionDiscontinuity(int reason) {
//
//        if(reason != player.getCurrentWindowIndex())
//        {
//           position--;
//           Song song =
//
//        }
//        else {
//            Toast.makeText(this, "NOT", Toast.LENGTH_SHORT).show();
//        }




    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    public static long  getCurrentSongID(){
        return currentsongID;
    }

    @Override
    public void onSeekProcessed() {

    }
    public void updateUI(Song currentSong){
        currentsongID = currentSong.getMId();

        trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong.getMId());
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, currentSong.getMAlbumId());
        ContentResolver contentResolver = this.getApplicationContext().getContentResolver();
        InputStream in = null;
        try {
            in = contentResolver.openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap artwork = BitmapFactory.decodeStream(in);

        if(currentSong.getMAlbum() != null) {
            albumN = findViewById(R.id.albumName);
            albumN.setText(currentSong.getMAlbum());
            albumN.setSelected(true);
        }
        trackN = findViewById(R.id.trackName);
        artistN = findViewById(R.id.artistName);
        albumImage = findViewById(R.id.image_album_art);

        trackN.setText(currentSong.getMTitle());
        trackN.setSelected(true);
        artistN.setText(currentSong.getMArtist());
        artistN.setSelected(true);
        albumImage.requestFocus();
        albumImage.setImageResource(R.drawable.album_art_template);
        if(artwork != null) {
            albumImage.setImageBitmap(artwork);
        }

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
        controls();
    }
}
