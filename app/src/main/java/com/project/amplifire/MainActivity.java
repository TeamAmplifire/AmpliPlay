package com.project.amplifire;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {


    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100;
    private ArrayList<Song> mSongArrayList;
    private VerticalAdapter songAdt;
    private RecyclerView mSongView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar libraryToolbar = findViewById(R.id.libraryToolbar);
        setSupportActionBar(libraryToolbar);

        String title = "AmpliPlay";
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#ecf0f1")), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        mSongView = findViewById(R.id.song_list);
        mSongArrayList = new ArrayList<Song>();
    }

    @Override
    protected void onStart(){
        super.onStart();
        getSongList();
        Collections.sort(mSongArrayList, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                return o1.getMTitle().compareTo(o2.getMTitle());
            }
        });
        setList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause(){
        super.onPause();

        if(!mSongArrayList.isEmpty()){
            mSongArrayList.clear();
        }
    }
    public void filter(String text){
        ArrayList<Song> temp = new ArrayList<Song>();
        for(Song d : mSongArrayList){
            if(d.getMTitle().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        songAdt.updateList(temp);
    }
    public void getSongList(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this ,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = null;
        try{
            musicCursor = musicResolver.query(musicUri, null, null, null, null);


            if(musicCursor != null && musicCursor.moveToFirst()){
                int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int idColumn  = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int albumIDColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID); //Added by titan
                int durationColumn  = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int fullPathColumn = (musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                do{
                    long thisID = musicCursor.getLong(idColumn);
                    long thisAlbumID = musicCursor.getLong(albumIDColumn);
                    String thisTitle = musicCursor.getString(titleColumn);
                    String thisArtist = musicCursor.getString(artistColumn);
                    String thisAlbum = musicCursor.getString(albumColumn);
                    String thisDuration = musicCursor.getString(durationColumn);
                    String thisFullPath = musicCursor.getString(fullPathColumn);
                    mSongArrayList.add(new Song(thisID, thisAlbumID,thisTitle, thisArtist, thisAlbum, thisDuration, thisFullPath));
                }while(musicCursor.moveToNext());
            }
        }catch(Exception exception){
            if(musicCursor != null) {
                musicCursor.close();
            }
        }
    }
    public void setList(){
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        songAdt = new VerticalAdapter(mSongArrayList);
        mSongView.setLayoutManager(mLinearLayoutManager);
        mSongView.setAdapter(songAdt);
    }
}
