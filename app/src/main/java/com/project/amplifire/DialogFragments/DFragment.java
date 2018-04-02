package com.project.amplifire.DialogFragments;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.project.amplifire.Adapters.VerticalAdapter;
import com.project.amplifire.DataModels.References;
import com.project.amplifire.DataModels.Song;
import com.project.amplifire.Playback.Player;
import com.project.amplifire.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by chait on 17-03-2018.
 */

public class DFragment extends DialogFragment {

    private static Song mSong;
    private PopupMenu mPopupMenu;
    private static int mPosition;

    public DFragment() {
        super();
    }

    public static void setMSong(Song song, int position){
        mSong = song;
        mPosition = position;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.song_dialog_fragment, container,
                false);
        TextView artistView = rootView.findViewById(R.id.song_dialog_fragment_song_artist);
        artistView.setSelected(true);
        TextView albumView = rootView.findViewById(R.id.song_dialog_fragment_song_album);
        albumView.setSelected(true);
        TextView durationView = rootView.findViewById(R.id.song_dialog_fragment_song_duration);
        durationView.setSelected(true);
        TextView titleView = rootView.findViewById(R.id.song_dialog_fragment_song_title);
        titleView.setSelected(true);
        ImageButton overflowMenuInflater = rootView.findViewById(R.id.song_dialog_fragment_song_overflow_button);
        ImageView albumImage = rootView.findViewById(R.id.song_dialog_fragment_album_art_view);
        FloatingActionButton floatingActionButton = rootView.findViewById(R.id.song_dialog_fragment_floating_action_button);
        String songDurationString = VerticalAdapter.getFormattedDuration(mSong.getMDuration());
        artistView.setText(mSong.getMArtist());
        albumView.setText(mSong.getMAlbum());
        durationView.setText(songDurationString);
        titleView.setText(mSong.getMTitle());
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, mSong.getMAlbumId());
        ContentResolver contentResolver = getActivity().getApplicationContext().getContentResolver();
        final FragmentManager fm = getActivity().getFragmentManager();
        InputStream in = null;
        try {
            in = contentResolver.openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap artwork = BitmapFactory.decodeStream(in);
        if(artwork != null) {
            albumImage.setImageBitmap(artwork);
        }
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(rootView.getContext(), mPosition);
            }
        });
        overflowMenuInflater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mPopupMenu = new PopupMenu(v.getContext(), v);
                MenuInflater menuInflater = mPopupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.d_fragment_overflow_menu, mPopupMenu.getMenu());
                mPopupMenu.show();
                mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.dialog_fragment_overflow_enqueue:
                                if(Player.enqueue == null) {
                                    Player.enqueue = new ArrayList<Song>();
                                }
                                Player.enqueue.add(mSong);
                                break;
                            case R.id.dialog_fragment_overflow_add_to_playlist:
                                PlaylistDialog playlistDialog = new PlaylistDialog(mSong);
                                playlistDialog.show(fm, References.FRAGMENT_TAGS.PLAYLIST_FRAGMENT);
                                break;
                        }
                        return false;
                    }
                });
            }
        });

        return rootView;
    }
    public void play(Context context, int position){

        Intent intent = new Intent(context, Player.class);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

}
