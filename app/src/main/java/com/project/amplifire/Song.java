package com.project.amplifire;

/**
 * Created by chait on 07-03-2018.
 */

public class Song {
    private long mId;
    private long mAlbumId;
    private String mTitle;
    private String mArtist;
    private String mDuration;
    private String mAlbum;
    private String mFullPath;

    public Song(long songId, long songAlbumID, String songTitle, String songArtist, String songAlbum, String songDuration, String songFullPath){

        mId = songId;
        mTitle = songTitle;
        mArtist = songArtist;
        mDuration = songDuration;
        mAlbum = songAlbum;
        mFullPath = songFullPath;
        mAlbumId = songAlbumID;
    }

    public long getMId() {
        return mId;
    }

    public long getMAlbumId() {
        return mAlbumId;
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