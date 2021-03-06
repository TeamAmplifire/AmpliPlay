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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.project.amplifire.DataModels.Playlist;
import com.project.amplifire.DataModels.References;
import com.project.amplifire.R;

public class RenamePlaylistDialog extends DialogFragment {

    private long playlistId;
    
    public RenamePlaylistDialog() {}

    @SuppressLint("ValidFragment")
    public RenamePlaylistDialog(long playlistId) {
        this.playlistId = playlistId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.AmpliFire_Dark_Dialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            Window window = dialog.getWindow();
//            WindowManager.LayoutParams windowParams = window.getAttributes();
//            windowParams.dimAmount = 0.60f;
//            windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            window.setAttributes(windowParams);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        final ContentResolver resolver = getActivity().getContentResolver();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.set_playlist_name_fragment, null);
        final EditText playlistName = view.findViewById(R.id.set_playlist_name_fragment_edit);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AmpliFire_Dark_Dialog);
        builder.setView(view);
        builder.setTitle("Enter New Name").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        })
                .setPositiveButton("Change Name", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final FragmentManager fm = getActivity().getFragmentManager();
                        String playlistNameValue = playlistName.getText().toString();
                        Playlist.renamePlaylist(resolver, playlistId, playlistNameValue);
                        String tag = References.FRAGMENT_TAGS.RENAME_PLAYLIST_FRAGMENT;
                        Fragment fragment = fm.findFragmentByTag(tag);
                        if(fragment != null)
                            fm.beginTransaction().remove(fragment).commit();
                    }
                })
                .setCancelable(true);
        return builder.create();
    }
}
