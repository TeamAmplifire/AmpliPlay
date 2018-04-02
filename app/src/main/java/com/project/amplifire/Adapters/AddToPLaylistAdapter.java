package com.project.amplifire.Adapters;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.project.amplifire.DataModels.Playlist;
import com.project.amplifire.DataModels.References;
import com.project.amplifire.DataModels.Song;
import com.project.amplifire.R;

import java.util.ArrayList;

/**
 * Created by chait on 17-03-2018.
 */

public class AddToPLaylistAdapter extends RecyclerView.Adapter<AddToPLaylistAdapter.PlaylistViewHolder> {

    private ArrayList<Playlist> mPlaylists;
    private Context mContext;
    private Song currentSong;
    private Activity mActivity;
    class PlaylistViewHolder extends RecyclerView.ViewHolder{

        TextView playlistName;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.playlist_list_name);
            mContext = itemView.getContext();
        }
    }


    public AddToPLaylistAdapter(ArrayList<Playlist> playlists, Song song, Activity activity) {
        mPlaylists = playlists;
        currentSong = song;
        mActivity = activity;
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_list, parent, false);
        return new PlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AddToPLaylistAdapter.PlaylistViewHolder holder, int position) {
        final int tempPosition = holder.getAdapterPosition();
        final Playlist currentPlaylist = mPlaylists.get(tempPosition);
        holder.playlistName.setText(currentPlaylist.getPlaylistName());
        holder.itemView.setTag(tempPosition);
        final FragmentManager fm = mActivity.getFragmentManager();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = Playlist.getPlaylist(mContext.getContentResolver(), currentPlaylist.getPlaylistName());
                Playlist.addToPlaylist(mContext.getContentResolver(), id, currentSong.getMId());
                Fragment fragment = fm.findFragmentByTag(References.FRAGMENT_TAGS.PLAYLIST_FRAGMENT);
                if(fragment != null)
                    fm.beginTransaction().remove(fragment).commit();
                Toast.makeText(mContext, "Added to playlist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlaylists.size();
    }


}
