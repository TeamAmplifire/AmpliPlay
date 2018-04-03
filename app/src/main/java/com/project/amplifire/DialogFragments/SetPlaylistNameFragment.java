package com.project.amplifire.DialogFragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.project.amplifire.DataModels.Playlist;
import com.project.amplifire.DataModels.References;
import com.project.amplifire.DataModels.Song;
import com.project.amplifire.Fragments.PlaylistGridFragment;
import com.project.amplifire.R;

/**
 * Created by chait on 17-03-2018.
 */

public class SetPlaylistNameFragment extends DialogFragment {

    private Song currentSelectedSong;

    public SetPlaylistNameFragment() {}
    @SuppressLint("ValidFragment")
    public SetPlaylistNameFragment(Song currentSelectedSong) {
        this.currentSelectedSong = currentSelectedSong;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        final ContentResolver resolver = getActivity().getContentResolver();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.set_playlist_name_fragment, null);
        final EditText playlistName = view.findViewById(R.id.set_playlist_name_fragment_edit);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle("Create Playlist").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        })
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final FragmentManager fm = getActivity().getFragmentManager();
                String playlistNameValue = playlistName.getText().toString();
                long id = Playlist.createPlaylist(resolver, playlistNameValue);
                Fragment fragment = fm.findFragmentByTag(References.FRAGMENT_TAGS.CREATE_PLAYLIST_FRAGMENT);
                if(fragment != null)
                    fm.beginTransaction().remove(fragment).commit();
                Playlist.addToPlaylist(References.applicationContext.getContentResolver(), id, currentSelectedSong.getMId());
                Toast.makeText(References.applicationContext, "Added to playlist", Toast.LENGTH_SHORT).show();
//                PlaylistDialog playlistDialog = new PlaylistDialog(currentSelectedSong);
//                playlistDialog.show(fm, References.FRAGMENT_TAGS.PLAYLIST_FRAGMENT);
            }
        })
                .setCancelable(true);
        return builder.create();
    }
}
