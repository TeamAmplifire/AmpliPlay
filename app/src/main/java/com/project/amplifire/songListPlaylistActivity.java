package com.project.amplifire;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.project.amplifire.Adapters.PlaylistSongAdapter;
import com.project.amplifire.DataModels.Playlist;
import com.project.amplifire.DataModels.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class songListPlaylistActivity extends AppCompatActivity {

    private static final String SAVED_LAYOUT_MANAGER ="layoutmanager" ;
    public ArrayList<Song> mPlaylistSongArrayList;
    private PlaylistSongAdapter playlistSongAdt;
    private RecyclerView mPlaylistSongView;
//    private FastScroller mFastScroller;
    //private TextView nameView;
//    private MaterialSearchView mSearchView;
//    private TabLayout libraryTabLayout;
    private long playlistID;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list_playlist_activity);
        Bundle extras = getIntent().getExtras();
        playlistID = (long)extras.get("playlistID");
        mPlaylistSongArrayList = new ArrayList<Song>();
        mPlaylistSongView = findViewById(R.id.song_list_playlist_recyclerView);
      //  mFastScroller = findViewById(R.id.song_list_playlist_fastscroll);
      //  nameView = findViewById(R.id.song_list_playlist_name);

        Toolbar playlistToolbar = findViewById(R.id.playlist_toolbar);
        setSupportActionBar(playlistToolbar);
        getPlaylistSongList();
        setPlaylistList();
    }

//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//    {
//        View rootView = inflater.inflate(R.layout.playlist_recycler_view_fragment, container, false);
//        rootView.setTag(TAG);
//        mPlaylistSongArrayList = new ArrayList<Song>();
//        getPlaylistSongList();
//        mPlaylistSongView = rootView.findViewById(R.id.playlist_songs_recycler_view);
//        mFastScroller = rootView.findViewById(R.id.playlist_songs_fastScroller);
//        setPlaylistList();
//        setHasOptionsMenu(true);
//        Toolbar libraryToolbar = getActivity().findViewById(R.id.libraryToolbar);
//        libraryToolbar.inflateMenu(R.menu.search_menu);
//        mSearchView = getActivity().findViewById(R.id.search_view);
//        libraryTabLayout = getActivity().findViewById(R.id.library_tab_layout);
//        return rootView;
//    }

    @Override
    public void onStart() {
        super.onStart();
        mPlaylistSongArrayList = new ArrayList<Song>();
        getPlaylistSongList();
    }

    @Override
    public void onPause(){
        super.onPause();
        if(!mPlaylistSongArrayList.isEmpty()){
            mPlaylistSongArrayList.clear();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_LAYOUT_MANAGER, mPlaylistSongView.getLayoutManager().onSaveInstanceState());
    }
    public void getPlaylistSongList()
    {
        Playlist playlist = Playlist.getPlaylistByID(getContentResolver(), playlistID);
        String title = playlist.getPlaylistName();
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
       // nameView.setText(playlist.getPlaylistName());
        mPlaylistSongArrayList = playlist.getSongs(getContentResolver());
    }
    public void setPlaylistList()
    {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        playlistSongAdt = new PlaylistSongAdapter(mPlaylistSongArrayList);
        mPlaylistSongView.setLayoutManager(mLinearLayoutManager);
        Collections.sort(mPlaylistSongArrayList, new Comparator<Song>()
        {
            @Override
            public int compare(Song o1, Song o2)
            {
                return o1.getMTitle().toLowerCase().compareTo(o2.getMTitle().toLowerCase());
            }
        });
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mPlaylistSongView.getContext(),
                mLinearLayoutManager.getOrientation()
        );
        mPlaylistSongView.addItemDecoration(dividerItemDecoration);
        mPlaylistSongView.setAdapter(playlistSongAdt);
        //mFastScroller.setRecyclerView(mPlaylistSongView);
    }

//    public void filter(String text)
//    {
//        ArrayList<Song> temp = new ArrayList<Song>();
//        for (Song d : mPlaylistSongArrayList)
//        {
//            if (d.getMTitle().toLowerCase().contains(text.toLowerCase()))
//            {
//                temp.add(d);
//            }
//        }
//        Collections.sort(temp, new Comparator<Song>()
//        {
//            @Override
//            public int compare(Song o1, Song o2)
//            {
//                return o1.getMTitle().compareTo(o2.getMTitle());
//            }
//        });
//        playlistSongAdt.updateList(temp);
//    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//
//        getMenuInflater().inflate(R.menu.search_menu, menu);
//        MenuItem item = menu.findItem(R.id.action_search);
//        mSearchView.setMenuItem(item);
//        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                filter(newText);
//                return false;
//            }
//        });
//
//        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
//            @Override
//            public void onSearchViewShown() {
//            }
//
//            @Override
//            public void onSearchViewClosed() {
//            }
//        });
//
//        return true;
//    }
}
