package com.project.amplifire;

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
}