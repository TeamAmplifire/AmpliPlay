package com.project.amplifire.Playback;

import android.support.v4.media.session.MediaSessionCompat;

/**
 * Created by chait on 21-03-2018.
 */

public interface PlaybackInterface {

    void start();
    void stop();
    void repeat();
    void setState(int state);
    int getstate();
    boolean isConnected();
    boolean isPlaying();
    long getCurrentStreamPosition();
    void updateLastKnownStreamPosition();
    void play(MediaSessionCompat.QueueItem item);
    void pause();
    void seekTo(long position);
    void setCurrentMediaId(String mediaId);
    String getCurrentMediaId();
    interface Callback{
        void onCompletion();
        void onPlaybackStatusChanged(int state);
        void onError(String error);
        void setCurrentMediaID(String mediaID);
    }
    void setCallback(Callback callback);
}
