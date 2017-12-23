package com.buggyarts.android.cuotos.gaana.vHolders;

import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buggyarts.android.cuotos.gaana.R;
import com.thoughtbot.expandablecheckrecyclerview.viewholders.CheckableChildViewHolder;

/**
 * Created by mayank on 12/2/17
 */

public class PlaylistTracksCCVH extends CheckableChildViewHolder {

    public ImageView thumbnail;
    public TextView title;
    public TextView artist;
    public RelativeLayout childLayout;

    public PlaylistTracksCCVH(View itemView) {
        super(itemView);
        this.thumbnail = itemView.findViewById(R.id.item_art);
        this.title = itemView.findViewById(R.id.item_title);
        this.artist = itemView.findViewById(R.id.item_artist);
        this.childLayout = itemView.findViewById(R.id.item_model_1);

    }

    @Override
    public Checkable getCheckable() {
        return null;
    }
}
