package com.buggyarts.android.cuotos.gaana.vHolders;

import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buggyarts.android.cuotos.gaana.R;
import com.thoughtbot.expandablecheckrecyclerview.viewholders.CheckableChildViewHolder;

import com.buggyarts.android.cuotos.gaana.utils.Audio;

/**
 * Created by mayank on 11/19/17
 */

public class TrackViewHolder extends CheckableChildViewHolder {

    public TextView track_title;
    public TextView track_artist;
    public LinearLayout linearLayout;

    public TrackViewHolder(View itemView) {
        super(itemView);
        track_title = itemView.findViewById(R.id.track_title);
        track_artist = itemView.findViewById(R.id.track_artist);
        linearLayout = itemView.findViewById(R.id.album_track_layout_view);
    }

    @Override
    public Checkable getCheckable() {
        return null;
    }

    public void onBind(Audio tracks){
        track_title.setText(tracks.title);
    }
}
