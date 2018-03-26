package com.project.amplifire.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.amplifire.Adapters.VerticalAdapter;
import com.project.amplifire.DataModels.References;
import com.project.amplifire.DataModels.Song;
import com.project.amplifire.R;

import java.io.File;

/**
 * Created by chait on 24-03-2018.
 */

public class InfoFragment extends DialogFragment{

    private Song currentSong;

    public InfoFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public InfoFragment(Song currentSong) {
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

        final View rootView = inflater.inflate(R.layout.info_fragment, container);
        final FragmentManager fm = getFragmentManager();

        TextView locationView = rootView.findViewById(R.id.file_location);
        //TextView extensionView = rootView.findViewById(R.id.file_extension);
        TextView durationView = rootView.findViewById(R.id.file_duration);
        TextView sizeView = rootView.findViewById(R.id.file_quality);
        TextView titleView = rootView.findViewById(R.id.file_track_name);
        TextView albumView = rootView.findViewById(R.id.file_album);
        TextView artistView = rootView.findViewById(R.id.file_artist);
        FloatingActionButton editButton;
        editButton = rootView.findViewById(R.id.edit_fab);
        //Button editButton = rootView.findViewById(R.id.edit_fab);

        File songFile = new File(currentSong.getMFullPath());
        String location = "" + songFile.getParent();
        //String extension = "" + songFile.getAbsolutePath().substring(songFile.getAbsolutePath().lastIndexOf("."));
        String duration = "" + VerticalAdapter.getFormattedDuration(currentSong.getMDuration());
        String size = "" + String.format("%.2f", (double)songFile.getTotalSpace()/(1024*1024*1024)) + "MB";
        String title = "" + currentSong.getMTitle();
        String album = "" + currentSong.getMAlbum();
        String artist = "" + currentSong.getMArtist();

        locationView.setText(location);
//        extensionView.setText(extension);
        durationView.setText(duration);
        sizeView.setText(size);
        titleView.setText(title);
        albumView.setText(album);
        artistView.setText(artist);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = fm.findFragmentByTag(References.FRAGMENT_TAGS.INFO_FRAGMENT);
                if(fragment != null)
                    fm.beginTransaction().remove(fragment).commit();
                EditTagsFragment newEditTagsFragment = new EditTagsFragment(currentSong);
                newEditTagsFragment.show(fm, References.FRAGMENT_TAGS.EDIT_TAGS_FRAGMENT);
            }
        });
        return rootView;
    }
}
