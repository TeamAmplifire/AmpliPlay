package com.example.chait.musoic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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

import java.io.File;
import java.util.ArrayList;

/**
 * Created by chait on 07-03-2018.
 */

public class VerticalAdapter extends RecyclerView.Adapter<VerticalAdapter.MyViewHolder> {

    private ArrayList<Song> mSongs;
    private PopupMenu mPopupMenu;
    Context mContext;

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
            mContext = itemView.getContext();

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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {

                                                   Intent intent = new Intent(mContext, Player.class);
                                                   intent.putExtra("songID", currentSong.getMId());
                                                   mContext.startActivity(intent);
                                               }
                                           });
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
                                case R.id.delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                    builder.setTitle("Delete");
                                    builder.setMessage("Are you sure you want to delete this song?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    if (deleteTarget(currentSong.getMFullPath()) != 0){
                                                        mSongs.remove(position);
                                                        Toast.makeText(v.getContext(), "Song Deleted", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {}
                                            });
                                    builder.create();
                                    builder.show();
                                    break;
                                case R.id.add_to_playlist:
                                    Toast.makeText(v.getContext(), "ADD TO PLAYLIST MENU OPEN", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.enqueue:
                                    Toast.makeText(v.getContext(), "ENQUEUE", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.apply_ringtone:
                                    Toast.makeText(v.getContext(), "APPLY RINGTONE", Toast.LENGTH_SHORT).show();
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
        mSongs.clear();
        mSongs.addAll(list);
        notifyDataSetChanged();
    }

    public int deleteTarget(String path) {
        File target = new File(path);

        if(target.exists() && target.isFile() && target.canWrite()) {
            target.delete();
            notifyDataSetChanged();
            return 0;
        }

        else if(target.exists() && target.isDirectory() && target.canRead()) {
            String[] file_list = target.list();

            if(file_list != null && file_list.length == 0) {
                target.delete();
                notifyDataSetChanged();
                return 0;

            } else if(file_list != null && file_list.length > 0) {

                for(int i = 0; i < file_list.length; i++) {
                    File temp_f = new File(target.getAbsolutePath() + "/" + file_list[i]);

                    if(temp_f.isDirectory())
                        deleteTarget(temp_f.getAbsolutePath());
                    else if(temp_f.isFile())
                        temp_f.delete();
                    notifyDataSetChanged();
                }
            }
            if(target.exists())
                if(target.delete()) {
                    notifyDataSetChanged();
                    return 0;
                }
        }
        return -1;
    }
}