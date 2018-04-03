package com.project.amplifire.Adapters;

import android.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.project.amplifire.DialogFragments.RenamePlaylistDialog;
import com.project.amplifire.Fragments.PlaylistGridFragment;
import com.project.amplifire.Library;
import com.project.amplifire.Playback.Player;
import com.project.amplifire.songListPlaylistActivity;
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
        AlbumArtView thumbnail;
        ImageButton overflowMenu;

        public MyViewHolder(View view) {
            super(view);
            titleView = itemView.findViewById(R.id.grid_view_item_playlist_name);
            thumbnail = itemView.findViewById(R.id.grid_view_item_thumbnail_00);
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

        if((mPlaylists.get(position).getSongs(mContext.getContentResolver()).size() != 0))
        {
            Uri uri = ContentUris.withAppendedId(sArtworkUri, mPlaylists.get(position).getSongs(mContext.getContentResolver()).get(0).getMAlbumId());
            InputStream in = null;
            try {
                in = mContext.getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap artwork = BitmapFactory.decodeStream(in);
            if (artwork != null) {
                Glide.with(mContext).load(uri).into(holder.thumbnail);
            } else {
                holder.thumbnail.setImageResource(R.drawable.ic_album_art_template);
            }
        }
        else{
            holder.thumbnail.setImageResource(R.drawable.ic_album_art_template);
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
                                Player.enqueue = mPlaylists.get(position).getSongs(mContext.getContentResolver());
                                play(mContext, -1);
                                break;
                            case R.id.playlist_grid_menu_enqueue:
                                Player.enqueue.addAll(mPlaylists.get(position).getSongs(mContext.getContentResolver()));
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
                                                Playlist.deletePlaylist(mContext.getContentResolver(), mPlaylists.get(position).getPlaylistID());
                                                mPlaylists.remove(position);
                                                notifyDataSetChanged();
                                                Toast.makeText(v.getContext(), "Playlist Deleted", Toast.LENGTH_SHORT).show();
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
                Intent newIntent = new Intent(mContext, songListPlaylistActivity.class);
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
