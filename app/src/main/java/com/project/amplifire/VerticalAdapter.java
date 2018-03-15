package com.project.amplifire;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
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

public class VerticalAdapter extends RecyclerView.Adapter<VerticalAdapter.MyViewHolder> {

    private ArrayList<com.project.amplifire.Song> mSongs;
    private PopupMenu mPopupMenu;
    private Context mContext;

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

    public VerticalAdapter(ArrayList<com.project.amplifire.Song> theSongs) {
        mSongs = theSongs;
    }
    @Override
    public VerticalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(VerticalAdapter.MyViewHolder holder, final int position)
    {

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
                                                   play(currentSong, mContext);
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
                                    play(currentSong, mContext);
                                    break;
                                case R.id.delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                    builder.setTitle("Delete");
                                    builder.setMessage("Are you sure you want to delete this song?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    if (deleteTarget(currentSong.getMFullPath()) != 0){
                                                        mSongs.remove(position);
                                                        deleteFromContentProvider(currentSong.getMId());
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
                                case R.id.rename:
//                                    String newName;
//                                    renameSong(currentSong.getMId(), newName);
                                    break;
                                case R.id.info:
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
    public void play(Song currentSong, Context context){
        Intent intent = new Intent(context, Player.class);
        String artist = currentSong.getMArtist();
        String album = currentSong.getMAlbum();
        if(artist.equals("<unknown>")){
            artist = "";
        }
        if(album.equals("<unknown>")){
            album = "";
        }
        intent.putExtra("songID", currentSong.getMId());
        intent.putExtra("track",currentSong.getMTitle());
        intent.putExtra("album",album);
        intent.putExtra("artist",artist);
        intent.putExtra("albumID", currentSong.getMAlbumId());
        context.startActivity(intent);
    }
    public void deleteFromContentProvider(Long id){
        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String where = "_ID=?";
        String[] args = {Long.toString(id)};
        musicResolver.delete(musicUri, where, args);
    }
    public void renameSong(Long id, String newName){
        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String where = "_ID?";
        String[] args = {Long.toString(id)};
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.TITLE, newName);
        musicResolver.update(musicUri, values, where, args);
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