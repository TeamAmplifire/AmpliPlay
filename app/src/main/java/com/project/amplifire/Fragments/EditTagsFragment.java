package com.project.amplifire.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.project.amplifire.DataModels.References;
import com.project.amplifire.DataModels.Song;
import com.project.amplifire.R;

/**
 * Created by chait on 24-03-2018.
 */

public class EditTagsFragment extends DialogFragment {

    private Song currentSong;

    public EditTagsFragment() {super();}

    @SuppressLint("ValidFragment")
    public EditTagsFragment(Song currentSong) {
        this.currentSong = currentSong;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.edit_tags_fragment, container);
        final FragmentManager fm = getFragmentManager();

        final EditText titleEditView = rootView.findViewById(R.id.file_track_name_edit);
        final EditText albumEditView = rootView.findViewById(R.id.file_album_edit);
        final EditText artistEditView = rootView.findViewById(R.id.file_artist_edit);
        FloatingActionButton submitButton = rootView.findViewById(R.id.file_submit_fab);

        String title = currentSong.getMTitle();
        String album = currentSong.getMAlbum();
        String artist = currentSong.getMArtist();

        titleEditView.setText(title);
        albumEditView.setText(album);
        artistEditView.setText(artist);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTitle = titleEditView.getText().toString();
                String newAlbum = albumEditView.getText().toString();
                String newArtist = artistEditView.getText().toString();
                currentSong.setTitle(newTitle);
                currentSong.setAlbum(newAlbum);
                currentSong.setArtist(newArtist);
                ContentResolver musicResolver = getActivity().getContentResolver();
                Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String where = "_ID=?";
                String[] args = {Long.toString(currentSong.getMId())};
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.TITLE, newTitle);
                values.put(MediaStore.Audio.Media.ALBUM, newAlbum);
                values.put(MediaStore.Audio.Media.ARTIST, newArtist);
                musicResolver.update(musicUri, values, where, args);
                Fragment fragment = fm.findFragmentByTag(References.FRAGMENT_TAGS.EDIT_TAGS_FRAGMENT);
                if(fragment != null)
                    fm.beginTransaction().remove(fragment).commit();
                InfoFragment newInfoFragment = new InfoFragment(currentSong);
                newInfoFragment.show(fm, References.FRAGMENT_TAGS.INFO_FRAGMENT);
            }
        });

        return rootView;
    }
}
