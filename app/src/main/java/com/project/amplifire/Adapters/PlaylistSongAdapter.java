package com.project.amplifire.Adapters;

import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.project.amplifire.DataModels.References;
import com.project.amplifire.DataModels.Song;
import com.project.amplifire.Fragments.DFragment;
import com.project.amplifire.Fragments.InfoFragment;
import com.project.amplifire.Fragments.PlaylistDialog;
import com.project.amplifire.Playback.Player;
import com.project.amplifire.R;
import com.project.amplifire.songListPlaylistActivity;
import java.util.ArrayList;

public class PlaylistSongAdapter extends RecyclerView.Adapter<PlaylistSongAdapter.MyViewHolder> implements SectionTitleProvider {

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

    public PlaylistSongAdapter(ArrayList<Song> theSongs) {
        mSongs = theSongs;
        holders = new ArrayList<MyViewHolder>();
    }
    @Override
    public PlaylistSongAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_songs_list_item, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final PlaylistSongAdapter.MyViewHolder holder, int position)
    {
        if(!holders.contains(holder)){
            holders.add(holder);
        }
        final int tempPosition = holder.getAdapterPosition();
        final Song currentSong = mSongs.get(tempPosition);
        final FragmentManager fm = ((songListPlaylistActivity)mContext).getFragmentManager();
        String songDurationString = getFormattedDuration(currentSong.getMDuration());
        holder.titleView.setText(currentSong.getMTitle());
        holder.artistView.setText(currentSong.getMArtist());
        holder.albumView.setText(currentSong.getMAlbum());
        holder.durationView.setText(songDurationString);
        holder.itemView.setTag(tempPosition);
        if(currentSong.getMId() == Player.getCurrentSongID())
        {
            holder.artistView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            holder.titleView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            holder.albumView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            holder.durationView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        }
        else {
            holder.artistView.setTextColor(mContext.getResources().getColor(R.color.colorText));
            holder.titleView.setTextColor(mContext.getResources().getColor(R.color.colorText));
            holder.albumView.setTextColor(mContext.getResources().getColor(R.color.colorText));
            holder.durationView.setTextColor(mContext.getResources().getColor(R.color.colorText));
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
                    menuInflater.inflate(R.menu.playlist_songs_overflow_menu, mPopupMenu.getMenu());
                    mPopupMenu.show();
                    mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()) {
                                case R.id.playlist_songs_overflow_menu_play:
                                    topElementPosition = tempPosition;
                                    setColor();
                                    holder.artistView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                                    holder.titleView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                                    holder.albumView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                                    holder.durationView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                                    play(mContext, tempPosition);
                                    break;
                                case R.id.playlist_songs_overflow_menu_delete:
                                    //Remove from playlist
                                    break;
                                case R.id.playlist_songs_overflow_menu_add_to_playlist:
                                    PlaylistDialog playlistDialog = new PlaylistDialog(currentSong);
                                    playlistDialog.show(fm, References.FRAGMENT_TAGS.PLAYLIST_FRAGMENT);
                                    break;
                                case R.id.playlist_songs_overflow_menu_enqueue:
                                    if(Player.enqueue == null) {
                                        Player.enqueue = new ArrayList<Song>();
                                    }
                                        Player.enqueue.add(currentSong);
                                    break;
                                case R.id.playlist_songs_overflow_menu_apply_ringtone:
                                    Toast.makeText(v.getContext(), "APPLY RINGTONE", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.playlist_songs_overflow_menu_info:
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
    public void renameSong(Long id, String newName){
        ContentResolver musicResolver = mContext.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String where = "_ID?";
        String[] args = {Long.toString(id)};
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.TITLE, newName);
        musicResolver.update(musicUri, values, where, args);
    }
    private void setColor(){
        for(int i=0; i<holders.size(); i++){
            holders.get(i).artistView.setTextColor(mContext.getResources().getColor(R.color.colorText));
            holders.get(i).titleView.setTextColor(mContext.getResources().getColor(R.color.colorText));
            holders.get(i).albumView.setTextColor(mContext.getResources().getColor(R.color.colorText));
            holders.get(i).durationView.setTextColor(mContext.getResources().getColor(R.color.colorText));
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

    public static void setSongAtPosition(int position, Song song){
        mSongs.set(position, song);
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