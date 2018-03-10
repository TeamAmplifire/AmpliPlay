package com.example.chait.musoic;


import android.content.ContentUris;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by chait on 07-03-2018.
 */

public class VerticalAdapter extends RecyclerView.Adapter<VerticalAdapter.MyViewHolder> {

    private ArrayList<Song> mSongs;
    private PopupMenu mPopupMenu;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView artistView;
        TextView albumView;
        TextView durationView;
        ImageButton songOverflowButton;

        public MyViewHolder(View view) {
            super(view);
            titleView = itemView.findViewById(R.id.song_title);
            artistView = itemView.findViewById(R.id.song_artist);
            albumView = itemView.findViewById(R.id.song_album);
            durationView = itemView.findViewById(R.id.song_duration);
            songOverflowButton = itemView.findViewById(R.id.songOverflowButton);
        }
    }

    public VerticalAdapter(ArrayList<Song> theSongs) {
        mSongs = theSongs;
    }

    @Override
    public VerticalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VerticalAdapter.MyViewHolder holder, final int position) {

        final Song currentSong = mSongs.get(position);
        int songDurationInt = Integer.parseInt(currentSong.getMDuration());
        String songDurationString;
        songDurationInt /= 1000;
        if (songDurationInt >= 3600 && songDurationInt <= 86400) {
            if (songDurationInt%60 < 10) {
                songDurationString = songDurationInt / 3600 + ":0" + songDurationInt / 60 + ":" + songDurationInt % 60;
            }else{
                songDurationString = songDurationInt / 3600 + ":" + songDurationInt / 60 + ":" + songDurationInt % 60;
            }
        }else {
            if(songDurationInt%60 < 10){
            songDurationString = songDurationInt / 60 + ":0" + songDurationInt % 60;
            }else{
                songDurationString = songDurationInt / 60 + ":" + songDurationInt % 60;
            }
        }
        holder.titleView.setText(currentSong.getMTitle());
        holder.artistView.setText(currentSong.getMArtist());
        holder.albumView.setText(currentSong.getMAlbum());
        holder.durationView.setText(songDurationString);
        holder.itemView.setTag(position);

            holder.songOverflowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    mPopupMenu = new PopupMenu(v.getContext(), v);
                    MenuInflater menuInflater = mPopupMenu.getMenuInflater();
                    menuInflater.inflate(R.menu.mymenu, mPopupMenu.getMenu());
                    mPopupMenu.show();
                    mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()) {
                                case R.id.play:
                                    Toast.makeText(v.getContext(), "PLAY SONG", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.enqueue:
                                    Toast.makeText(v.getContext(), "ENQUEUE", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.add_to_playlist:
                                    Toast.makeText(v.getContext(), "ADD TO PLAYLIST MENU OPEN", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.apply_ringtone:
                                    Toast.makeText(v.getContext(), "APPLY RINGTONE", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.delete:
                                    Uri trackUri = ContentUris.withAppendedId(
                                            android.provider.MediaStore.Audio.Media.INTERNAL_CONTENT_URI, currentSong.getMId());
                                    Toast.makeText(v.getContext(), trackUri.getPath(), Toast.LENGTH_SHORT).show();
                                    Toast.makeText(v.getContext(), "DELETE SONG", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            return false;
                        }
                    });
                }
            });

    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public void updateList(ArrayList<Song> list){
        mSongs = list;
        notifyDataSetChanged();
    }
}