package com.project.amplifire.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.project.amplifire.DataModels.References;
import com.project.amplifire.DataModels.Song;
import com.project.amplifire.R;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.IOException;

/**
 * Created by chait on 24-03-2018.
 */

public class EditTagsFragment extends DialogFragment {

    private Song currentSong;
    private DialogInterface.OnDismissListener onDismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }
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

        final EditText titleEditView = rootView.findViewById(R.id.edit_tags_fragment_file_track_name_edit);
        final EditText albumEditView = rootView.findViewById(R.id.edit_tags_fragment_file_album_edit);
        final EditText artistEditView = rootView.findViewById(R.id.edit_tags_fragment_file_artist_edit);
        FloatingActionButton submitButton = rootView.findViewById(R.id.edit_tags_fragment_file_submit_fab);

        String title = currentSong.getMTitle();
        String album = currentSong.getMAlbum();
        String artist = currentSong.getMArtist();

        titleEditView.setText(title);
        albumEditView.setText(album);
        artistEditView.setText(artist);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newTitle = titleEditView.getText().toString();
                final String newAlbum = albumEditView.getText().toString();
                final String newArtist = artistEditView.getText().toString();
                Fragment fragment = fm.findFragmentByTag(References.FRAGMENT_TAGS.EDIT_TAGS_FRAGMENT);
                if(fragment != null) {
                    fm.beginTransaction().remove(fragment).commit();
                }
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        File songFile = new File(currentSong.getMFullPath());
                        MusicMetadataSet songFileSet = null;
                        try{
                            songFileSet = new MyID3().read(songFile);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        if(songFileSet == null){
                            Log.d("NULL", "NULL");
                        }
                        else{
                            IMusicMetadata songMetadata= songFileSet.getSimplified();
                            Log.d("metadata", songMetadata.getArtist());
                            Log.d("metadata", songMetadata.getSongTitle());
                            Log.d("metadata", songMetadata.getAlbum());
                            MusicMetadata newMusicMetadeta = new MusicMetadata(songFile.getName());
                            newMusicMetadeta.setAlbum(newAlbum);
                            newMusicMetadeta.setArtist(newArtist);
                            newMusicMetadeta.setSongTitle(newTitle);
                            try {
                                new MyID3().update(songFile, songFileSet, newMusicMetadeta);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ID3WriteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });

        return rootView;
    }
}
