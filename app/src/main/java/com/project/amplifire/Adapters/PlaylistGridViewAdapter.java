package com.project.amplifire.Adapters;

import android.app.FragmentManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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

import com.bumptech.glide.Glide;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.project.amplifire.DataModels.Playlist;
import com.project.amplifire.DataModels.References;
import com.project.amplifire.DataModels.Song;
import com.project.amplifire.DialogFragments.RenamePlaylistDialog;
import com.project.amplifire.Library;
import com.project.amplifire.Playback.Player;
import com.project.amplifire.SongListPlaylistActivity;
import com.project.amplifire.R;
import com.project.amplifire.Utilities.AlbumArtView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class PlaylistGridViewAdapter extends RecyclerView.Adapter<PlaylistGridViewAdapter.MyViewHolder> implements SectionTitleProvider {

    private Context mContext;
    private ArrayList<Playlist> mPlaylists;
    private PopupMenu mPopupMenu;

    public PlaylistGridViewAdapter(Context context, ArrayList<Playlist> playlists) {
        mContext = context;
        mPlaylists = playlists;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        AlbumArtView thumbnail[] = new AlbumArtView[4];
        ImageButton overflowMenu;

        public MyViewHolder(View view) {
            super(view);
            titleView = itemView.findViewById(R.id.grid_view_item_playlist_name);
            thumbnail[0] = itemView.findViewById(R.id.grid_view_item_thumbnail_00);
            thumbnail[1] = itemView.findViewById(R.id.grid_view_item_thumbnail_01);
            thumbnail[2] = itemView.findViewById(R.id.grid_view_item_thumbnail_10);
            thumbnail[3] = itemView.findViewById(R.id.grid_view_item_thumbnail_11);

            overflowMenu = itemView.findViewById(R.id.grid_view_item_overflow_button);
            mContext = itemView.getContext();
        }
    }


    @NonNull
    @Override
    public PlaylistGridViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_view_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistGridViewAdapter.MyViewHolder holder, final int position) {
        holder.titleView.setText(mPlaylists.get(position).getPlaylistName());
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        final ArrayList<Song> mSongs = mPlaylists.get(position).getSongs(mContext.getContentResolver());
        int playlistSize = mSongs.size();

        if(playlistSize  != 0)
        {
            Uri uri;
            for(int i = 0; i < 4; i++) {
                uri = ContentUris.withAppendedId(sArtworkUri,mSongs.get(i%playlistSize).getMAlbumId());
                Glide.with(mContext).load(uri).into(holder.thumbnail[i]);
            }
        }

        holder.overflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mPopupMenu = new PopupMenu(v.getContext(), v);
                MenuInflater menuInflater = mPopupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.playlist_grid_menu, mPopupMenu.getMenu());
                mPopupMenu.show();
                mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch(item.getItemId())
                        {
                            case R.id.playlist_grid_menu_play:
                                Player.enqueue = mSongs;
                                play(mContext, -1);
                                break;
                            case R.id.playlist_grid_menu_enqueue:
                                Player.enqueue.addAll(mSongs);
                                break;
                            case R.id.playlist_grid_menu_rename:
                                final FragmentManager fm = ((Library)mContext).getFragmentManager();
                                RenamePlaylistDialog renamePlaylistDialog = new RenamePlaylistDialog(mPlaylists.get(position).getPlaylistID());
                                renamePlaylistDialog.show(fm, References.FRAGMENT_TAGS.RENAME_PLAYLIST_FRAGMENT);
                                break;
                            case R.id.playlist_grid_menu_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                builder.setTitle("Delete");
                                builder.setMessage("Are you sure you want to delete this Playlist?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id)
                                            {


                                                if (!mPlaylists.get(position).getPlaylistName().equalsIgnoreCase(References.RECENT_ADDED_PLAYLIST_NAME)) {
                                                    Playlist.deletePlaylist(mContext.getContentResolver(), mPlaylists.get(position).getPlaylistID());
                                                    notifyDataSetChanged();
                                                    Toast.makeText(v.getContext(), "Playlist Deleted", Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    Toast.makeText(References.applicationContext, "Cannot delete playlist", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {}
                                        });
                                builder.create();
                                builder.show();
                                break;
                        }

                        return false;
                    }
                });
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent newIntent = new Intent(mContext, SongListPlaylistActivity.class);
                newIntent.putExtra("playlistID", mPlaylists.get(position).getPlaylistID());
                mContext.startActivity(newIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlaylists.size();
    }

    @Override
    public String getSectionTitle(int position) {
        return null;
    }
    public void updateList(ArrayList<Playlist> list){
        mPlaylists = list;
        notifyDataSetChanged();
    }
    public void play(Context context, int position){
        Intent intent = new Intent(context, Player.class);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }
}