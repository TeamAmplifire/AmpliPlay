package com.project.amplifire;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.project.amplifire.Adapters.PlaylistSongAdapter;
import com.project.amplifire.DataModels.Playlist;
import com.project.amplifire.DataModels.Song;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class songListPlaylistActivity extends AppCompatActivity {

    private static final String SAVED_LAYOUT_MANAGER = "layoutmanager";
    public ArrayList<Song> mPlaylistSongArrayList;
    private RecyclerView mPlaylistSongView;
    private long playlistID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list_playlist_activity);
        Bundle extras = getIntent().getExtras();
        playlistID = (long) extras.get("playlistID");
        mPlaylistSongArrayList = new ArrayList<Song>();
        mPlaylistSongView = findViewById(R.id.song_list_playlist_recyclerView);

        CollapsingToolbarLayout playlistToolbar = findViewById(R.id.playlist_collapsing_toolbar);

        Playlist playlist = Playlist.getPlaylistByID(getContentResolver(), playlistID);
        ImageView albumArt = findViewById(R.id.playlist_app_bar_image);
        String title = playlist.getPlaylistName();

        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        if ((playlist.getSongs(getApplicationContext().getContentResolver()).size() != 0)) {
            Uri uri = ContentUris.withAppendedId(sArtworkUri, playlist.getSongs(getApplicationContext().getContentResolver()).get(0).getMAlbumId());
            InputStream in = null;
            try {
                in = getApplicationContext().getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap artwork = BitmapFactory.decodeStream(in);
            if (artwork != null) {
                Glide.with(getApplicationContext()).load(uri).into(albumArt);
            } else {
                albumArt.setImageResource(R.drawable.ic_album_art_template);
            }
        } else {
            albumArt.setImageResource(R.drawable.ic_album_art_template);
        }

        playlistToolbar.setTitle(title);
        playlistToolbar.setExpandedTitleColor(getResources().getColor(R.color.colorPrimary));
        playlistToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimary));

        getPlaylistSongList();
        setPlaylistList();
    }

    @Override
    public void onStart() {
        super.onStart();
        mPlaylistSongArrayList = new ArrayList<Song>();
        getPlaylistSongList();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!mPlaylistSongArrayList.isEmpty()) {
            mPlaylistSongArrayList.clear();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_LAYOUT_MANAGER, mPlaylistSongView.getLayoutManager().onSaveInstanceState());
    }

    public void getPlaylistSongList() {
        Playlist playlist = Playlist.getPlaylistByID(getContentResolver(), playlistID);
        mPlaylistSongArrayList = playlist.getSongs(getContentResolver());
    }

    public void setPlaylistList() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        PlaylistSongAdapter playlistSongAdt = new PlaylistSongAdapter(mPlaylistSongArrayList);
        mPlaylistSongView.setLayoutManager(mLinearLayoutManager);
        mPlaylistSongView.setAdapter(playlistSongAdt);
    }

}