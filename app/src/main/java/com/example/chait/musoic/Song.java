package com.example.chait.musoic;

/**
 * Created by chait on 07-03-2018.
 */

public class Song {
    private long mId;
    private String mTitle;
    private String mArtist;
    private String mDuration;
    private String mAlbum;
    private String mFullPath;

    public Song(long songId, String songTitle, String songArtist, String songAlbum, String songDuration, String songFullPath){

        mId = songId;
        mTitle = songTitle;
        mArtist = songArtist;
        mDuration = songDuration;
        mAlbum = songAlbum;
        mFullPath = songFullPath;
    }

    public long getMId() {
        return mId;
    }

    public String getMTitle() {
        return mTitle;
    }
    public String getMDuration() {
        return mDuration;
    }

    public String getMAlbum() {
        return mAlbum;
    }

    public String getMArtist() {

        return mArtist;
    }

    public String getMFullPath() {
        return mFullPath;
    }
}