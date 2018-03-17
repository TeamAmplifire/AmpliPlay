package com.project.amplifire;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by chait on 17-03-2018.
 */

public class PlaylistDialog extends DialogFragment {

    private ArrayList<Playlist> mPlaylists;
    private PlaylistAdapter mPlaylistAdapter;
    private RecyclerView mPlaylistRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private DividerItemDecoration mDividerItemDecoration;

    public PlaylistDialog() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.dialog_playlist, container,
                false);
        final FragmentManager fm = getFragmentManager();
        ContentResolver resolver = getActivity().getContentResolver();
        mPlaylists = Playlist.getAllPlaylists(resolver);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mPlaylistAdapter = new PlaylistAdapter(mPlaylists);
        Log.d("size", mPlaylists.size()+"");
        mPlaylistRecyclerView = rootView.findViewById(R.id.playlist_View);
        mDividerItemDecoration = new DividerItemDecoration(
                mPlaylistRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation()
        );
        mPlaylistRecyclerView.addItemDecoration(mDividerItemDecoration);
        mPlaylistRecyclerView.setLayoutManager(mLinearLayoutManager);
        mPlaylistRecyclerView.setAdapter(mPlaylistAdapter);
        mFloatingActionButton = rootView.findViewById(R.id.playlist_floating_action);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = "Playlist Fragment";
                Fragment fragment = fm.findFragmentByTag(tag);
                if(fragment != null)
                    fm.beginTransaction().remove(fragment).commit();
                SetPlaylistName newSetPlaylist = new SetPlaylistName();
                newSetPlaylist.show(fm, "Create Playlist");
            }
        });
        return rootView;

    }
    @Override
    public void onPause(){
        super.onPause();
        if(!mPlaylists.isEmpty()){
            mPlaylists.clear();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        final FragmentManager fm = getActivity().getFragmentManager();
        String tag = "Create Playlist";
        Fragment fragment = fm.findFragmentByTag(tag);
        if(fragment != null)
            fm.beginTransaction().remove(fragment).commit();
    }
    //    @Override
//    public void onResume() {
//        super.onResume();
//        updateUI();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        updateUI();
//    }

    public void updateUI(){
        ContentResolver resolver = getActivity().getContentResolver();
        mPlaylists = Playlist.getAllPlaylists(resolver);
        mPlaylistRecyclerView.setAdapter(mPlaylistAdapter);
    }
}
