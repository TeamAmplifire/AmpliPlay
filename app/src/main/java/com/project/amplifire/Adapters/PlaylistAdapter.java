package com.project.amplifire.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.amplifire.DataModels.Playlist;
import com.project.amplifire.R;

import java.util.ArrayList;

/**
 * Created by chait on 17-03-2018.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private ArrayList<Playlist> mPlaylists;
    private Context mContext;
    class PlaylistViewHolder extends RecyclerView.ViewHolder{

        TextView playlistName;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.playlist_item);
            mContext = itemView.getContext();
        }
    }


    public PlaylistAdapter(ArrayList<Playlist> playlists) {
        mPlaylists = playlists;
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_list, parent, false);
        return new PlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PlaylistAdapter.PlaylistViewHolder holder, int position) {
        final int tempPosition = holder.getAdapterPosition();
        final Playlist currentPlaylist = mPlaylists.get(tempPosition);
        holder.playlistName.setText(currentPlaylist.getPlaylistName());
        holder.itemView.setTag(tempPosition);

    }

    @Override
    public int getItemCount() {
        return mPlaylists.size();
    }


}
