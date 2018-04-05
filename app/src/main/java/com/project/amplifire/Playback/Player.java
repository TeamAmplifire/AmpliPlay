package com.project.amplifire.Playback;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.project.amplifire.Adapters.VerticalAdapter;
import com.project.amplifire.DataModels.Song;
import com.project.amplifire.R;
import com.project.amplifire.Utilities.AlbumArtView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by Utsav on 3/10/2018.
 */

public class Player extends Activity implements PlaybackInterface {

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
    private static SimpleExoPlayer player;
    private Uri trackUri;
    private ImageButton btnNext,btnPrev,btnPlay,btnShuffle,btnRepeat;
    int repeat_clickCount = 0;
    int shuffle_clickCounter =0;
    static int position = 0;
    ArrayList<Song> songArray;
    public static ArrayList<Song> enqueue;
    private static long currentSongID;
    ExoplayerEventListener mExoplayerEventListener = new ExoplayerEventListener();
    public static final float VOLUME_DUCK = 0.2f;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_interface);
        Bundle message = getIntent().getExtras();
        position = (Integer) message.get("position");
        songArray = VerticalAdapter.getSongsList();
        final Song currentSong = songArray.get(position);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUI(currentSong);
            }
        });
    }

    private void controls() {
        time();
        seekBar();
        setProgress();
        play();
        next();
        repeat();
        shuffle();
        previous();
    }

//    private void repeat() {
//
//        btnRepeat = findViewById(R.id.repeat);
//        btnRepeat.requestFocus();
//        btnRepeat.setImageResource(R.drawable.ic_repeat_disabled);
//        btnRepeat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                repeat_clickCount++;
//                repeat_clickCount %=3;
//                switch (repeat_clickCount)
//                {
//                    case 0:
//                        btnRepeat.setImageResource(R.drawable.ic_repeat_disabled);
//                        mExoplayerEventListener.onRepeatModeChanged(0);
//                        break;
//                    case 1:
//                        btnRepeat.setImageResource(R.drawable.ic_repeat_one_enabled);
//                        mExoplayerEventListener.onRepeatModeChanged(1);
//                        break;
//                    case 2:
//                        btnRepeat.setImageResource(R.drawable.ic_repeat_enabled);
//                        mExoplayerEventListener.onRepeatModeChanged(2);
//                        break;
//                }
//            }
//        });
//
//    }

    private void shuffle() {
        btnShuffle = findViewById(R.id.player_interface_shuffle);
        btnShuffle.requestFocus();
        btnShuffle.setImageResource(R.drawable.ic_shuffle_black_40dp);
        btnShuffle.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.dark_colorTextOff), android.graphics.PorterDuff.Mode.SRC_IN);
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shuffle_clickCounter++;
                shuffle_clickCounter%=2;

                switch (shuffle_clickCounter)
                {
                    case 0:
                        btnShuffle.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.dark_colorTextOff), android.graphics.PorterDuff.Mode.SRC_IN);
                        mExoplayerEventListener.onShuffleModeEnabledChanged(false);
                        break;
                    case 1:
                        btnShuffle.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
                        mExoplayerEventListener.onShuffleModeEnabledChanged(true);
                        break;
                }
            }
        });
    }

    private void previous() {
        btnPrev = findViewById(R.id.player_interface_button_previous);
        btnPrev.requestFocus();
        btnPrev.setImageResource(R.drawable.ic_skip_previous_black_50dp);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.stop();
//                VerticalAdapter.setPreviousSongPosition(position);
                position--;
                final Song newSong = songArray.get(position);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI(newSong);
                    }
                });
                player.setPlayWhenReady(true);
            }
        });
    }

    private void next() {
        btnNext = findViewById(R.id.player_interface_button_next);
        btnNext.requestFocus();
        btnNext.setImageResource(R.drawable.ic_skip_next_black_50dp);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.stop();
                final Song newSong;
                if(enqueue == null) {
                    position++;
                    newSong = songArray.get(position);
                }
                else{
                    newSong = enqueue.get(0);
                    enqueue.remove(0);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI(newSong);
                    }
                });
                player.setPlayWhenReady(true);

            }
        });
    }

    private void play() {
        btnPlay = findViewById(R.id.player_interface_button_play);
        btnPlay.requestFocus();
        btnPlay.setImageResource(R.drawable.ic_pause_circle_filled_black_60dp);
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
            btnPlay.setImageResource(R.drawable.ic_pause_circle_filled_black_60dp);
        }else{
            setProgress();
            btnPlay.setImageResource(R.drawable.ic_play_circle_filled_black_60dp);
        }
    }

    private void time() {
        txtCurrentTime = findViewById(R.id.player_interface_time_current);
        txtEndTime = findViewById(R.id.player_interface_player_end_time);
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
        seekPlayerProgress = findViewById(R.id.player_interface_media_progress);
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
        player.setPlayWhenReady(true);
    }


//    @Override
//    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
//
//    }
//
//    @Override
//    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
//    }
//
//
//    @Override
//    public void onLoadingChanged(boolean isLoading) {
//
//    }
//
//    @Override
//    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//
//        switch (playbackState)
//        {
//            case ExoPlayer.STATE_READY:
////                Song thisPlay = songArray.get(position);
////                updateUI(thisPlay);
////                player.setPlayWhenReady(true);
//                Toast.makeText(this, "READY", Toast.LENGTH_SHORT).show();
//                break;
//
//            case ExoPlayer.STATE_ENDED:
//                Song nextPlay;
//                if(enqueue == null) {
//                    position++;
//                    nextPlay = songArray.get(position);
//                }
//                else {
//                    nextPlay = enqueue.get(0);
//                    enqueue.remove(0);
//                }
//                updateUI(nextPlay);
//                player.setPlayWhenReady(true);
////                Toast.makeText(this, "ENDED", Toast.LENGTH_SHORT).show();
//                break;
//
//            case ExoPlayer.STATE_IDLE:
//                Toast.makeText(this, "IDLE", Toast.LENGTH_SHORT).show();
//                break;
//
//            case ExoPlayer.STATE_BUFFERING:
//                Toast.makeText(this, "BUFFERING", Toast.LENGTH_SHORT).show();
//                break;
//        }
//
//    }
//
//    @Override
//    public void onRepeatModeChanged(int repeatMode) {
//
//        switch (repeatMode) {
//            case 0:
//                player.setRepeatMode(com.google.android.exoplayer2.Player.REPEAT_MODE_OFF);
//                break;
//            case 1:
//                player.setRepeatMode(com.google.android.exoplayer2.Player.REPEAT_MODE_ONE);
//                break;
//            case 2:
//                player.setRepeatMode(com.google.android.exoplayer2.Player.REPEAT_MODE_ALL);
//                break;
//        }
//    }
//
//    @Override
//    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
//
//        if(shuffleModeEnabled)
//        {
//            player.setShuffleModeEnabled(true);
//        }
//        else
//        {
//            player.setShuffleModeEnabled(false);
//        }
//
//    }
//
//
//    @Override
//    public void onPlayerError(ExoPlaybackException error) {
//
//        Snackbar snackbar = Snackbar.make(findViewById(R.id.player_Layout),"ERROR: " +error,Snackbar.LENGTH_LONG);
//        snackbar.show();
//    }

//    @Override
//    public void onPositionDiscontinuity(int reason) {
//
//    }

//    @Override
//    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
//
//    }


    public static long  getCurrentSongID(){
        return currentSongID;
    }

//    @Override
//    public void onSeekProcessed() {
//
//    }
    public void updateUI(Song currentSong){
        currentSongID = currentSong.getMId();

        trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong.getMId());

        renderersFactory = new DefaultRenderersFactory(getApplicationContext());
        bandwidthMeter = new DefaultBandwidthMeter();
        trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        loadControl = new DefaultLoadControl();

        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
        player.addListener(mExoplayerEventListener);

        dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), "ExoplayerDemo");
        extractorsFactory = new DefaultExtractorsFactory();
        mainHandler = new Handler();
        mediaSource = new ExtractorMediaSource(trackUri,dataSourceFactory,
                extractorsFactory,
                mainHandler,
                null);
        player.prepare(mediaSource);
        controls();
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
            albumN = findViewById(R.id.player_interface_album_name);
            albumN.setText(currentSong.getMAlbum());
            albumN.setSelected(true);
        }
        trackN = findViewById(R.id.player_interface_track_name);
        artistN = findViewById(R.id.player_interface_artist_ame);
        AlbumArtView albumImage = findViewById(R.id.player_interface_image_album_art);

        if(artwork != null) {
            //albumImage.setImageBitmap(artwork);
            Glide.with(this).load(uri).into(albumImage);
        }
        else {
            albumImage.setImageResource(R.drawable.ic_album_art_template);
        }

        trackN.setText(currentSong.getMTitle());
        trackN.setSelected(true);
        artistN.setText(currentSong.getMArtist());
        artistN.setSelected(true);
        albumImage.requestFocus();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void repeat() {
        btnRepeat = findViewById(R.id.player_interface_repeat);
        btnRepeat.requestFocus();
        btnRepeat.setImageResource(R.drawable.ic_repeat_black_40dp);
        btnRepeat.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.dark_colorTextOff), android.graphics.PorterDuff.Mode.SRC_IN);
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repeat_clickCount++;
                repeat_clickCount %=3;
                switch (repeat_clickCount)
                {
                    case 0:
                        btnRepeat.setImageResource(R.drawable.ic_repeat_black_40dp);
                        btnRepeat.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.dark_colorTextOff), android.graphics.PorterDuff.Mode.SRC_IN);
                        mExoplayerEventListener.onRepeatModeChanged(0);
                        break;
                    case 1:
                        btnRepeat.setImageResource(R.drawable.ic_repeat_one_black_40dp);
                        btnRepeat.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
                        mExoplayerEventListener.onRepeatModeChanged(1);
                        break;
                    case 2:
                        btnRepeat.setImageResource(R.drawable.ic_repeat_black_40dp);
                        btnRepeat.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
                        mExoplayerEventListener.onRepeatModeChanged(2);
                        break;
                }
            }
        });
    }

    @Override
    public void setState(int state) {

    }

    @Override
    public int getstate() {
        return 0;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public long getCurrentStreamPosition() {
        return 0;
    }

    @Override
    public void updateLastKnownStreamPosition() {

    }

    @Override
    public void play(MediaSessionCompat.QueueItem item) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void seekTo(long position) {

    }

    @Override
    public void setCurrentMediaId(String mediaId) {

    }

    @Override
    public String getCurrentMediaId() {
        return null;
    }

    @Override
    public void setCallback(Callback callback) {

    }

    private final class ExoplayerEventListener implements ExoPlayer.EventListener{

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
             switch (repeatMode){
            case 0:
                player.setRepeatMode(com.google.android.exoplayer2.Player.REPEAT_MODE_OFF);
                break;
            case 1:
                player.setRepeatMode(com.google.android.exoplayer2.Player.REPEAT_MODE_ONE);
                break;
            case 2:
                player.setRepeatMode(com.google.android.exoplayer2.Player.REPEAT_MODE_ALL);
                break;
             }
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }
    }
}
