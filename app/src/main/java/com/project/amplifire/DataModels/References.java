package com.project.amplifire.DataModels;


import android.content.Context;
import android.support.v4.app.Fragment;

import com.project.amplifire.Fragments.PlaylistGridFragment;
import com.project.amplifire.Fragments.SongListFragment;

/**
 * Created by Utsav on 3/13/2018.
 */



public class References {

    public static Context applicationContext;
    public static PlaylistGridFragment sPlaylistGridFragment;
    public static SongListFragment sSongListFragment;
    public static String RECENT_ADDED_PLAYLIST_NAME = "Recently Added";

    public interface ACTION {
        public static String MAIN_ACTION = "com.project.amplifire.action.main";
        public static String PREV_ACTION = "com.project.amplifire.action.prev";
        public static String PLAY_PAUSE_ACTION = "com.project.amplifire.player.playpause";
        public static String NEXT_ACTION = "com.project.amplifire.action.next";
        public static String STARTFOREGROUND_ACTION = "com.project.amplifire.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.project.amplifire.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }

    public interface FRAGMENT_TAGS{
        String EDIT_TAGS_FRAGMENT = "Edit Tags Fragment";
        String INFO_FRAGMENT = "Info Fragment";
        String CREATE_PLAYLIST_FRAGMENT = "Create Playlist";
        String PLAYLIST_FRAGMENT = "Playlist Fragment";
        String SONG_DETAILS = "Song details";
        String RENAME_PLAYLIST_FRAGMENT = "Rename Playlist";
    }
}