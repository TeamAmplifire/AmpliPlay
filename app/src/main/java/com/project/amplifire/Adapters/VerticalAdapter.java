package com.project.amplifire.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.project.amplifire.DataModels.References;
import com.project.amplifire.DataModels.Song;
import com.project.amplifire.DialogFragments.DFragment;
import com.project.amplifire.DialogFragments.InfoFragment;
import com.project.amplifire.DialogFragments.PlaylistDialog;
import com.project.amplifire.Library;
import com.project.amplifire.Playback.Player;
import com.project.amplifire.R;

import java.io.File;
import java.util.ArrayList;

public class VerticalAdapter extends RecyclerView.Adapter<VerticalAdapter.MyViewHolder> implements SectionTitleProvider {

    private static ArrayList<Song> mSongs;
    private PopupMenu mPopupMenu;
    private Context mContext;
    private ArrayList<MyViewHolder> holders;
    public static int topElementPosition;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView artistView;
        TextView albumView;
        TextView durationView;
        ImageButton songOverflowButton;

        public MyViewHolder(View view) {
            super(view);
            titleView = itemView.findViewById(R.id.all_songs_list_item_song_title);
            artistView = itemView.findViewById(R.id.all_songs_list_item_song_artist);
            albumView = itemView.findViewById(R.id.all_songs_list_item_song_album);
            durationView = itemView.findViewById(R.id.all_songs_list_item_song_duration);
            songOverflowButton = itemView.findViewById(R.id.all_songs_list_item_song_overflow_button);
            mContext = itemView.getContext();

        }
    }

    public VerticalAdapter(ArrayList<Song> theSongs) {
        mSongs = theSongs;
        holders = new ArrayList<MyViewHolder>();
    }
    @Override
    public VerticalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_songs_list_item, parent, false);
        return new MyViewHolder(itemView);
    }
    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(final VerticalAdapter.MyViewHolder holder, int position)
    {
        if(!holders.contains(holder)){
            holders.add(holder);
        }
        final int tempPosition = holder.getAdapterPosition();
        final Song currentSong = mSongs.get(tempPosition);
        final FragmentManager fm = ((Library)mContext).getFragmentManager();
        String songDurationString = getFormattedDuration(currentSong.getMDuration());
        holder.titleView.setText(currentSong.getMTitle());
        holder.artistView.setText(currentSong.getMArtist());
        holder.albumView.setText(currentSong.getMAlbum());
        holder.durationView.setText(songDurationString);
        holder.itemView.setTag(tempPosition);
        if(currentSong.getMId() == Player.getCurrentSongID())
        {
            holder.artistView.setTextColor(mContext.getResources().getColor(R.color.dark_colorAccent));
            holder.titleView.setTextColor(mContext.getResources().getColor(R.color.dark_colorAccent));
            holder.albumView.setTextColor(mContext.getResources().getColor(R.color.dark_colorAccent));
            holder.durationView.setTextColor(mContext.getResources().getColor(R.color.dark_colorAccent));
        }
        else {
            holder.artistView.setTextColor(mContext.getResources().getColor(R.color.dark_colorText));
            holder.titleView.setTextColor(mContext.getResources().getColor(R.color.dark_colorText));
            holder.albumView.setTextColor(mContext.getResources().getColor(R.color.dark_colorText));
            holder.durationView.setTextColor(mContext.getResources().getColor(R.color.dark_colorText));
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DFragment.setMSong(currentSong, tempPosition);
                DFragment dFragment = new DFragment();
                dFragment.show(fm, References.FRAGMENT_TAGS.SONG_DETAILS);
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   setColor();
                                                   holder.artistView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                                                   holder.titleView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                                                   holder.albumView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                                                   holder.durationView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                                                   play(mContext, tempPosition);
                                                   topElementPosition = tempPosition;
                                               }
                                           });
        holder.songOverflowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    mPopupMenu = new PopupMenu(v.getContext(), v);
                    MenuInflater menuInflater = mPopupMenu.getMenuInflater();
                    menuInflater.inflate(R.menu.all_songs_overflow_menu, mPopupMenu.getMenu());
                    mPopupMenu.show();
                    mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()) {

                                case R.id.all_songs_overflow_menu_play:
                                    topElementPosition = tempPosition;
                                    setColor();
                                    holder.artistView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                                    holder.titleView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                                    holder.albumView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                                    holder.durationView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                                    play(mContext, tempPosition);
                                    break;

                                case R.id.all_songs_overflow_menu_delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AmpliFire_Dark_Dialog);
                                    builder.setTitle("Delete");
                                    builder.setMessage("Are you sure you want to delete this song?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    if (deleteTarget(currentSong.getMFullPath()) == 0){
                                                        mSongs.remove(tempPosition);
                                                        deleteFromContentProvider(currentSong.getMId());
                                                        Toast.makeText(v.getContext(), "Song Deleted", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {}
                                            });

                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                    break;

                                case R.id.all_songs_overflow_menu_add_to_playlist:
                                    PlaylistDialog playlistDialog = new PlaylistDialog(currentSong);
                                    playlistDialog.show(fm, References.FRAGMENT_TAGS.PLAYLIST_FRAGMENT);
                                    break;
                                case R.id.all_songs_overflow_menu_enqueue:
                                    if(Player.enqueue == null) {
                                        Player.enqueue = new ArrayList<Song>();
                                    }
                                        Player.enqueue.add(currentSong);
                                    Toast.makeText(v.getContext(), "ENQUEUE", Toast.LENGTH_SHORT).show();
                                    break;

                                case R.id.all_songs_overflow_menu_apply_ringtone:
                                    Toast.makeText(v.getContext(), "APPLY RINGTONE", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.all_songs_overflow_menu_info:
                                    topElementPosition = tempPosition;
                                    InfoFragment infoFragment = new InfoFragment(currentSong);
                                    infoFragment.show(fm, References.FRAGMENT_TAGS.INFO_FRAGMENT);
                                    notifyDataSetChanged();
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
    public void play(Context context, int position){

        Intent intent = new Intent(context, Player.class);
        intent.putExtra("position", position);
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
    public static ArrayList<Song> getSongsList(){
        return mSongs;
    }
    private void setColor(){
        for(int i=0; i<holders.size(); i++){
            holders.get(i).artistView.setTextColor(mContext.getResources().getColor(R.color.dark_colorText));
            holders.get(i).titleView.setTextColor(mContext.getResources().getColor(R.color.dark_colorText));
            holders.get(i).albumView.setTextColor(mContext.getResources().getColor(R.color.dark_colorText));
            holders.get(i).durationView.setTextColor(mContext.getResources().getColor(R.color.dark_colorText));
        }
    }
    public static String getFormattedDuration(String duration){
        int songDurationInt = Integer.parseInt(duration);
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
        return songDurationString;
    }
    public Song getSongItem(int position)
    {
        return mSongs.get(position);
    }

    @Override
    public String getSectionTitle(int position) {
        //this String will be shown in a bubble for specified position
        return getSongItem(position).getMTitle().substring(0, 1);
    }
}