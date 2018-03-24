package com.project.amplifire.DataModels;

/**
 * Created by Utsav on 3/13/2018.
 */



public class References {
    public interface ACTION {
        public static String MAIN_ACTION = "com.project.amplifire.action.main";
        public static String PREV_ACTION = "com.project.amplifire.action.prev";
        public static String PLAY_ACTION = "com.project.amplifire.player.play";
        public static String PAUSE_ACTION = "com.project.amplifire.player.play";
        public static String NEXT_ACTION = "com.project.amplifire.action.next";
        public static String STARTFOREGROUND_ACTION = "com.project.amplifire.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.project.amplifire.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }

    public interface FRAGMENT_TAGS{
        String EDIT_TAGS_FRAGMENT = "Edit Tags Fragment";
        String INFO_FRAGMENT = "Info Fragment";
        String CREATE_PLAYLIST_FRAGMENT = "Create Playlist";
        String PLAYLIST_FRAGMENT = "Playlist Fragment";
        String SONG_DETAILS = "Song details";
    }
}