package com.buggyarts.android.cuotos.gaana.vHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.buggyarts.android.cuotos.gaana.R;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

/**
 * Created by mayank on 12/2/17
 */

public class PlaylistGVH extends GroupViewHolder {

    public TextView playlist_header_name;
    public TextView playListDetails;
    public ImageView clickToPlay;

    public PlaylistGVH(View itemView) {
        super(itemView);
        this.playlist_header_name = itemView.findViewById(R.id.play_list_name);
        this.playListDetails = itemView.findViewById(R.id.play_list_details);
        this.clickToPlay = itemView.findViewById(R.id.play_this_list);
    }

}
