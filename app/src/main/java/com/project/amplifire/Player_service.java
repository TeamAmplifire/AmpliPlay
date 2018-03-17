package com.project.amplifire;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.google.android.exoplayer2.ExoPlayer;

/**
 * Created by Utsav on 3/13/2018.
 */

public class Player_service extends Service {
    ExoPlayer mPlayer;


    @Nullable
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(References.ACTION.STARTFOREGROUND_ACTION)) {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction(References.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Intent play = new Intent(this, Player_service.class);
            play.setAction(References.ACTION.PLAY_ACTION);
            PendingIntent pend_play = PendingIntent.getService(this, 0, play, 0);

            Intent pause = new Intent(this, Player_service.class);
            pause.setAction(References.ACTION.PAUSE_ACTION);
            PendingIntent pend_pause = PendingIntent.getService(this,0, pause,0);

            Intent stop = new Intent(this,Player_service.class);
            stop.setAction(References.ACTION.STOPFOREGROUND_ACTION);
            PendingIntent pend_stop = PendingIntent.getService(this,0,stop,0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Ampliplay")
                    .setContentText("Current $ong")
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 500, 500, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .addAction(android.R.drawable.ic_media_play, "Play", pend_play)
                    .addAction(R.drawable.exo_controls_pause,"PAUSE",pend_pause)
                    .addAction(R.drawable.exo_edit_mode_logo,"STOP",pend_stop)
                    .build();
            startForeground(References.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
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
