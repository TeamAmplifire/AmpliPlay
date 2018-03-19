package com.project.amplifire;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;
public class Player extends Activity implements ExoPlayer.EventListener {

    private static final float ALBUM_ART_ELEVATION = 4.0f;


    private SeekBar seekPlayerProgress;
    private Handler handler;
    private TextView txtCurrentTime, txtEndTime, albumN, trackN, artistN;
    private static boolean isPlaying = true;
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
    private ImageView albumImage;
    int repeat_clickCount = 0;
    int shuffle_clickCounter =0;
    static  int position;
    ArrayList<Song> songArray;
    static ArrayList<Song> enqueue;
    private static long currentsongID;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_interface);
        Bundle message = getIntent().getExtras();
        try {
            position = (Integer) message.get("position");
        }catch(Exception e){}
        songArray = VerticalAdapter.getSongsList();
        final Song currentSong = songArray.get(position);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUI(currentSong);
                Intent intent = new Intent(Player.this,Player_service.class);
                intent.setAction(References.ACTION.STARTFOREGROUND_ACTION);
                startService(intent);
            }
        });
//        Intent notificationIntent = new Intent(this, Player.class);
//        notificationIntent.setAction(References.ACTION.MAIN_ACTION);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getForegroundService(this, 0,
//                notificationIntent, 0);
//
//
//        Intent playPause = new Intent(this, Player_service.class);
//        playPause.setAction(References.ACTION.PLAY_PAUSE_ACTION);
//        playPause.putExtra("PLAYER_ACTION",References.ACTION.PLAY_PAUSE_ACTION);
//        PendingIntent pend_play = PendingIntent.getBroadcast(this, 1, playPause, 0);
//
//        Intent stop = new Intent(this,Player_service.class);
//        stop.setAction(References.ACTION.STOPFOREGROUND_ACTION);
//        stop.putExtra("PLAYER_ACTION",References.ACTION.STOPFOREGROUND_ACTION);
//        PendingIntent pend_stop = PendingIntent.getBroadcast(this,2,stop,0);
//
//        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
//        Notification notification = new NotificationCompat.Builder(this)
//                .setContentTitle("Ampliplay")
//                .setContentText("Current $ong")
//                .setSmallIcon(R.mipmap.ic_launcher_foreground)
//                .setLargeIcon(Bitmap.createScaledBitmap(icon, 500, 500, false))
//                .setContentIntent(pendingIntent)
//                .setOngoing(true)
//                .addAction(android.R.drawable.ic_media_play, "Play", pend_play)
//                .addAction(R.drawable.exo_edit_mode_logo,"STOP",pend_stop)
//                .build();
//        startForeground(References.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
        //notification.notify();
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

    private void repeat() {

        btnRepeat = findViewById(R.id.repeat);
        btnRepeat.requestFocus();
        btnRepeat.setImageResource(R.drawable.ic_repeat_disabled);
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repeat_clickCount++;
                repeat_clickCount %=3;
                switch (repeat_clickCount)
                {
                    case 0:
                        btnRepeat.setImageResource(R.drawable.ic_repeat_disabled);
                        onRepeatModeChanged(0);
                        break;
                    case 1:
                        btnRepeat.setImageResource(R.drawable.ic_repeat_one_enabled);
                        onRepeatModeChanged(1);
                        break;
                    case 2:
                        btnRepeat.setImageResource(R.drawable.ic_repeat_enabled);
                        onRepeatModeChanged(2);
                        break;
                }
            }
        });

    }

    private void shuffle() {
        btnShuffle = findViewById(R.id.shuffle);
        btnShuffle.requestFocus();
        btnShuffle.setImageResource(R.drawable.ic_shuffle_disabled);
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shuffle_clickCounter++;
                shuffle_clickCounter%=2;

                switch (shuffle_clickCounter)
                {
                    case 0:
                        btnShuffle.setImageResource(R.drawable.ic_shuffle_disabled);
                        onShuffleModeEnabledChanged(false);
                        break;
                    case 1:
                        btnShuffle.setImageResource(R.drawable.ic_shuffle_enabled);
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
        btnNext = findViewById(R.id.btnNext);
        btnNext.requestFocus();
        btnNext.setImageResource(R.drawable.ic_skip_next_dark);
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


    public void setPlayPause(boolean play){
        isPlaying = play;
        player.setPlayWhenReady(play);
        if(isPlaying){
            setProgress();
            Intent intent = new Intent(this,Player_service.class);
            intent.setAction(References.ACTION.STARTFOREGROUND_ACTION);
            startService(intent);
            btnPlay.setImageResource(R.drawable.ic_pause_circle_filled);
        }else{
            setProgress();
            Intent intent = new Intent(this,Player_service.class);
            intent.setAction(References.ACTION.STOPFOREGROUND_ACTION);
            stopService(intent);
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
        
        switch (playbackState)
        {
            case ExoPlayer.STATE_READY:
//                Song thisPlay = songArray.get(position);
//                updateUI(thisPlay);
//                player.setPlayWhenReady(true);
                Toast.makeText(this, "READY", Toast.LENGTH_SHORT).show();
                break;
                
            case ExoPlayer.STATE_ENDED:
                Song nextPlay;
                if(enqueue == null) {
                    position++;
                    nextPlay = songArray.get(position);
                }
                else {
                    nextPlay = enqueue.get(0);
                    enqueue.remove(0);
                }
                updateUI(nextPlay);
                player.setPlayWhenReady(true);
//                Toast.makeText(this, "ENDED", Toast.LENGTH_SHORT).show();
                break;
                
            case ExoPlayer.STATE_IDLE:
               // Toast.makeText(this, "IDLE", Toast.LENGTH_SHORT).show();
                break;

            case ExoPlayer.STATE_BUFFERING:
               // Toast.makeText(this, "BUFFERING", Toast.LENGTH_SHORT).show();
                break;
        }
            
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

        switch (repeatMode) {
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

        if(shuffleModeEnabled)
        {
            player.setShuffleModeEnabled(true);
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

        albumImage.setElevation(ALBUM_ART_ELEVATION);
    }
    public static SimpleExoPlayer getPlayer(){
        return player;
    }
    public static boolean getIsPlaying(){
        return isPlaying;

    }
}
