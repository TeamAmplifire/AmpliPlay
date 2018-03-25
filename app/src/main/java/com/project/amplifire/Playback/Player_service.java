package com.project.amplifire.Playback;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.project.amplifire.DataModels.References;
import com.project.amplifire.Library;
import com.project.amplifire.R;

/**
 * Created by Utsav on 3/13/2018.
 */

public class Player_service extends Service {
    ExoPlayer mPlayer;
    MediaSession mSession;
    BroadcastReceiver mBroadcastReceiver;


    @Nullable
    public void onCreate() {
        super.onCreate();
        mSession = new MediaSession(this, "MusicService");
//        mSession.setCallback(new MediaSessionCallback());
        mSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mBroadcastReceiver = new PlayerReceiver();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (intent.getAction().equals(References.ACTION.STARTFOREGROUND_ACTION)) {
            Intent notificationIntent = new Intent(this, Library.class);
            notificationIntent.setAction(References.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                    notificationIntent, 0);


            Intent playPause = new Intent(this, Player_service.class);
            playPause.setAction(References.ACTION.PLAY_PAUSE_ACTION);
            playPause.putExtra("PLAYER_ACTION",References.ACTION.PLAY_PAUSE_ACTION);
            PendingIntent pend_play = PendingIntent.getBroadcast(this, 0, playPause, 0);

            Intent stop = new Intent(this,Player_service.class);
            stop.setAction(References.ACTION.STOPFOREGROUND_ACTION);
            stop.putExtra("PLAYER_ACTION",References.ACTION.STOPFOREGROUND_ACTION);
            PendingIntent pend_stop = PendingIntent.getBroadcast(this,0,stop,0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Ampliplay")
                    .setContentText("Current $ong")
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 500, 500, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .addAction(android.R.drawable.ic_media_play, "Play", pend_play)
                    .addAction(R.drawable.exo_edit_mode_logo,"STOP",pend_stop)
                    .build();
            startForeground(References.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
           // notification.notify();
        }
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mPlayer.release();
        stopSelf();
    }
}
