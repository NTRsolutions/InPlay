package com.buggyarts.android.cuotos.gaana.vHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.buggyarts.android.cuotos.gaana.R;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

/**
 * Created by mayank on 11/19/17
 */

public class AlbumViewHolder extends GroupViewHolder {

    public TextView title;
    public TextView count;
    public ImageView album_art;

    public AlbumViewHolder(View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.album_title);
    }
}