package com.project.amplifire;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;

/**
 * Created by aayush on 18-Mar-18.
 */

public class PlayerReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        context.getApplicationContext();
        Bundle action = intent.getExtras();
        String playerAction = action.getString("PLAYER_ACTION");
        switch(playerAction)
        {
            case References.ACTION.PLAY_PAUSE_ACTION:
//                boolean play = Player.getIsPlaying();
                Toast.makeText(context, "Play Pause Action Received", Toast.LENGTH_SHORT).show();
                break;
            case References.ACTION.PREV_ACTION:
                Toast.makeText(context, "Previous Action Received", Toast.LENGTH_SHORT).show();
                break;
            case References.ACTION.NEXT_ACTION:
                Toast.makeText(context, "Next Action Received", Toast.LENGTH_SHORT).show();
                break;
        }


    }

//    MAIN_ACTION = "com.project.amplifire.action.main";
//    public static String PREV_ACTION = "com.project.amplifire.action.prev";
//    public static String PLAY_PAUSE_ACTION = "com.project.amplifire.player.playpause";
//    public static String NEXT_ACTION = "com.project.amplifire.action.next";
//    public static String STARTFOREGROUND_ACTION = "com.project.amplifire.action.startforeground";
//    public static String STOPFOREGROUND_ACTION
    public static boolean getPlayPause()
    {
        return true;
    }
}
