package com.project.amplifire.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.amplifire.Adapters.PlaylistGridViewAdapter;
import com.project.amplifire.DataModels.Playlist;
import com.project.amplifire.DataModels.References;
import com.project.amplifire.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PlaylistGridFragment extends Fragment {

    private static final String TAG = "PlaylistListFragment";
    private ArrayList<Playlist> mPlaylists;
    private PlaylistGridViewAdapter mPlaylistGridViewAdapter;
    private RecyclerView mPlaylistView;
    private FastScroller mFastScroller;
    private MaterialSearchView mSearchView;
    private TabLayout libraryTabLayout;
    private ImageView albumArt;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    public PlaylistGridFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.playlist_recycler_view_fragment, container, false);
        rootView.setTag(TAG);
        mPlaylists = new ArrayList<Playlist>();
        mPlaylists = Playlist.getAllPlaylists(getActivity().getContentResolver());
        mPlaylistView = rootView.findViewById(R.id.playlist_songs_recycler_view);
        mFastScroller = rootView.findViewById(R.id.playlist_songs_fastscroller);
        mSwipeRefreshLayout = rootView.findViewById(R.id.playlist_songs_swiperefresh);

        setList();
        setHasOptionsMenu(true);

        Toolbar libraryToolbar = getActivity().findViewById(R.id.library_interface_libraryToolbar);
        libraryToolbar.inflateMenu(R.menu.search_menu);
        mSearchView = getActivity().findViewById(R.id.library_interface_search_view);
        libraryTabLayout = getActivity().findViewById(R.id.library_interface_tab_layout);
        References.sPlaylistGridFragment = this;

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
        return rootView;
    }

    public void refreshList() {
        mPlaylists.clear();
        mPlaylists = Playlist.getAllPlaylists(getActivity().getContentResolver());
        setList();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void setList() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mPlaylistGridViewAdapter = new PlaylistGridViewAdapter(getActivity().getApplicationContext(), mPlaylists);
        mPlaylistView.setLayoutManager(gridLayoutManager);
        Collections.sort(mPlaylists, new Comparator<Playlist>() {
            @Override
            public int compare(Playlist o1, Playlist o2) {
                return o1.getPlaylistName().toLowerCase().compareTo(o2.getPlaylistName().toLowerCase());
            }
        });
        mPlaylistView.setAdapter(mPlaylistGridViewAdapter);
        mFastScroller.setRecyclerView(mPlaylistView);
    }

    public void filter(String text) {
        ArrayList<Playlist> temp = new ArrayList<Playlist>();
        for (Playlist d : mPlaylists) {
            if (d.getPlaylistName().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        Collections.sort(mPlaylists, new Comparator<Playlist>() {
            @Override
            public int compare(Playlist o1, Playlist o2) {
                return o1.getPlaylistName().toLowerCase().compareTo(o2.getPlaylistName().toLowerCase());
            }
        });
        mPlaylistGridViewAdapter.updateList(temp);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        getActivity().getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_menu_action_search);
        ViewGroup.LayoutParams params = libraryTabLayout.getLayoutParams();
        mSearchView.setHint("Search in playlists");
        mSearchView.setMenuItem(item);
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
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

        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
//                ViewGroup.LayoutParams params = libraryTabLayout.getLayoutParams();
//                params.height = 0;
//                params.width = libraryTabLayout.getWidth();
//                libraryTabLayout.setLayoutParams(params);
            }

            @Override
            public void onSearchViewClosed() {
//                ViewGroup.LayoutParams params = libraryTabLayout.getLayoutParams();
//                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                params.width = libraryTabLayout.getWidth();
//                libraryTabLayout.setLayoutParams(params);

            }
        });
    }
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            refreshList();
//        }
//    }
}