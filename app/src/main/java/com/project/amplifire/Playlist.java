package com.project.amplifire;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

public class Playlist {


    public static long getPlaylist(ContentResolver resolver, String name)
    {
        long id = -1;

        Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Playlists._ID },
                MediaStore.Audio.Playlists.NAME + "=?",
                new String[] { name }, null);

        if (cursor != null) {
            if (cursor.moveToNext())
                id = cursor.getLong(0);
            cursor.close();
        }

        return id;
    }

    public static long createPlaylist(ContentResolver resolver, String name)
    {
        long id = getPlaylist(resolver, name);

        if (id == -1) {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Audio.Playlists.NAME, name);
            Uri uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                id = Long.parseLong(uri.getLastPathSegment());
            }
        } else {
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id);
            resolver.delete(uri, null, null);
        }

        return id;
    }

    public static int addToPlaylist(ContentResolver resolver, long playlistId, long songID)
    {
        if (playlistId == -1)
            return 0;
        Uri playlistUri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        String[] projection = new String[] { MediaStore.Audio.Playlists.Members.PLAY_ORDER };
        Cursor cursor = resolver.query(playlistUri, projection, null, null, null);
        int base = 0;
        if (cursor.moveToLast())
            base = cursor.getInt(0) + 1;
        cursor.close();
        String where = "_ID=?";
        Uri listMemberUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor from = resolver.query(listMemberUri, null, where,new String[] {Long.toString(songID)}, null);
        if (from == null)
            return 0;
        int count = from.getCount();
        if (count > 0) {
            ContentValues[] values = new ContentValues[count];
            for (int i = 0; i != count; ++i) {
                from.moveToPosition(i);
                ContentValues value = new ContentValues(2);
                value.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + i));
                value.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, from.getLong(0));
                values[i] = value;
            }
            resolver.bulkInsert(playlistUri, values);
        }
        from.close();
        return count;
    }

    public static void deletePlaylist(ContentResolver resolver, long id)
    {
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, id);
        resolver.delete(uri, null, null);
    }

    public static void renamePlaylist(ContentResolver resolver, long id, String newName)
    {
        long existingId = getPlaylist(resolver, newName);
        if (existingId == id)
            return;
        if (existingId != -1)
            deletePlaylist(resolver, existingId);

        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Audio.Playlists.NAME, newName);
        resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values, "_ID?=" + id, null);
    }

    public ArrayList<Song> getRecentlyAdded(ContentResolver resolver){

        ArrayList<Song> recentSongs = new ArrayList<Song>();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = resolver.query(musicUri, null, null, null, MediaStore.Audio.Media.DATE_ADDED);

        ArrayList<Song> songList = new ArrayList<Song>();
        do{
            long thisID = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            long thisAlbumID = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String thisTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String thisArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String thisAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            String thisDuration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            String thisFullPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            songList.add(new Song(thisID, thisAlbumID,thisTitle, thisArtist, thisAlbum, thisDuration, thisFullPath));
        }while(cursor.moveToNext());
        return recentSongs;
    }

    public ArrayList<Song> getSongs(ContentResolver resolver, long playlistID){

        Uri playlistUri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID);
//        String[] projection = new String[] { MediaStore.Audio.Playlists.Members.PLAY_ORDER, MediaStore.Audio.Playlists.Members._ID};
        Cursor musicCursor = resolver.query(playlistUri, null/*projection*/, null, null, null);
        String where = "_ID=?";
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        Cursor musicCursor = resolver.query(musicUri, null, null, null, null);

        ArrayList<Song> songList = new ArrayList<Song>();
        do{
//            long songID = playlistCursor.getLong(playlistCursor.getColumnIndex(MediaStore.Audio.Playlists.Members._ID
//            ));
            long thisID = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
            long thisAlbumID = musicCursor.getLong(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String thisTitle = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String thisArtist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String thisAlbum = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            String thisDuration = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            String thisFullPath = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            songList.add(new Song(thisID, thisAlbumID,thisTitle, thisArtist, thisAlbum, thisDuration, thisFullPath));
        }while(musicCursor.moveToNext());
        return songList;
    }
}


