package com.project.amplifire.Playback;

import android.content.ContentUris;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.media.session.MediaSessionCompat;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.project.amplifire.DataModels.Song;

/**
 * Created by chait on 21-03-2018.
 */

public class Playernew implements PlaybackInterface{

    private final String TAG = "AmpliPlay";
    public static final float VOLUME_DUCK = 0.2f;
    public static final float VOLUME_NORMAL = 1.0f;
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    private static final int AUDIO_FOCUSED = 2;

    private Context mContext;
    private Callback mCallback;
    private SimpleExoPlayer player ;
    private Song currentSong;
    private Player_service mService;
    //get song info

    private int mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
    private final AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    private SimpleExoPlayer mExoPlayer;
    private final ExoplayerEventListener mEventListener = new ExoplayerEventListener();
    private boolean playing = true;
    private Uri trackUri;
    private DefaultRenderersFactory renderersFactory;
    private DefaultBandwidthMeter bandwidthMeter;
    private AdaptiveTrackSelection.Factory trackSelectionFactory;
    private DefaultLoadControl loadControl;
    private DefaultDataSourceFactory dataSourceFactory;
    private DefaultExtractorsFactory extractorsFactory;
    private Handler mainHandler;
    private ExtractorMediaSource mediaSource;


    public Playernew(Context context, Callback callback , Song song) {
        mContext = context;
        mCallback = callback;
        currentSong = song;
    }


    @Override
    public void start() {

    }

    @Override
    public void stop() {


    }

    @Override
    public void repeat() {

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
        return playing || (player != null && player.getPlayWhenReady());
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
        trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong.getMId());

        renderersFactory = new DefaultRenderersFactory(mContext);
        bandwidthMeter = new DefaultBandwidthMeter();
        trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        loadControl = new DefaultLoadControl();

        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
        player.addListener(mEventListener);

        dataSourceFactory = new DefaultDataSourceFactory(mContext, "AmpliPlay");
        extractorsFactory = new DefaultExtractorsFactory();
        mainHandler = new Handler();
        mediaSource = new ExtractorMediaSource(trackUri,dataSourceFactory,
                extractorsFactory,
                mainHandler,
                null);
        player.prepare(mediaSource);

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

//*****************************************
//*****************************************
//*****************************************
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
