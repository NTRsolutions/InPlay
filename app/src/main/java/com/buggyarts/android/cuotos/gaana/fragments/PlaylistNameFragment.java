package com.buggyarts.android.cuotos.gaana.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.buggyarts.android.cuotos.gaana.R;

/**
 * Created by mayank on 11/29/17
 */

public class PlaylistNameFragment extends Fragment {

    public interface GetPlaylistName{
        void getNewPlaylistName(String name);
    }

    TextView create;
    GetPlaylistName getName;

    EditText entered_text;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_playlist_name,container,false);
        entered_text = v.findViewById(R.id.playlist_name);
        entered_text.setFocusableInTouchMode(true);
        entered_text.requestFocus();
        entered_text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    String playlist_name = entered_text.getText().toString();
                    getName.getNewPlaylistName(playlist_name);

                    return true;
                }
                return false;
            }
        });
        create = v.findViewById(R.id.create_new_playlist);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playlist_name = entered_text.getText().toString();
                getName.getNewPlaylistName(playlist_name);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        getName = (GetPlaylistName) context;
    }
}
