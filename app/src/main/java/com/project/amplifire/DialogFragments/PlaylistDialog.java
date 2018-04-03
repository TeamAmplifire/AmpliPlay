package com.project.amplifire.DialogFragments;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.amplifire.Adapters.AddToPLaylistAdapter;
import com.project.amplifire.DataModels.Playlist;
import com.project.amplifire.DataModels.References;
import com.project.amplifire.DataModels.Song;
import com.project.amplifire.R;

import java.util.ArrayList;

/**
 * Created by chait on 17-03-2018.
 */

public class PlaylistDialog extends DialogFragment {

    private ArrayList<Playlist> mPlaylists;
    private Song currentSelectedSong;

    public PlaylistDialog() {
        super();
    }

    @SuppressLint("ValidFragment")
    public PlaylistDialog(Song currentSelectedSong) {
        this.currentSelectedSong = currentSelectedSong;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.playlist_dialog_fragment, container,
                false);
        final FragmentManager fm = getFragmentManager();
        ContentResolver resolver = getActivity().getContentResolver();
        mPlaylists = Playlist.getAllPlaylists(resolver);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        AddToPLaylistAdapter addToPLaylistAdapter = new AddToPLaylistAdapter(mPlaylists, currentSelectedSong, getActivity());
//        Log.d("size", mPlaylists.size()+"");
        RecyclerView playlistRecyclerView = rootView.findViewById(R.id.playlist_dialog_fragment_recycler_view);

        playlistRecyclerView.setLayoutManager(mLinearLayoutManager);
        playlistRecyclerView.setAdapter(addToPLaylistAdapter);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
//                playlistRecyclerView.getContext(),
//                mLinearLayoutManager.getOrientation()
//        );
//        playlistRecyclerView.addItemDecoration(dividerItemDecoration);

        FloatingActionButton floatingActionButton = rootView.findViewById(R.id.playlist_dialog_fragment_floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = fm.findFragmentByTag(References.FRAGMENT_TAGS.PLAYLIST_FRAGMENT);
                if(fragment != null)
                    fm.beginTransaction().remove(fragment).commit();
                SetPlaylistNameFragment newSetPlaylist = new SetPlaylistNameFragment(currentSelectedSong);
                newSetPlaylist.show(fm, References.FRAGMENT_TAGS.CREATE_PLAYLIST_FRAGMENT);
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
        Fragment fragment = fm.findFragmentByTag(References.FRAGMENT_TAGS.CREATE_PLAYLIST_FRAGMENT);
        if(fragment != null)
            fm.beginTransaction().remove(fragment).commit();
    }
}
