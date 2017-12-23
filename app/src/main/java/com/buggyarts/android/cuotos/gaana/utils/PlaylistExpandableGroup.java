package com.buggyarts.android.cuotos.gaana.utils;

import com.thoughtbot.expandablecheckrecyclerview.models.CheckedExpandableGroup;
import java.util.List;

/**
 * Created by mayank on 12/2/17
 */

public class PlaylistExpandableGroup extends CheckedExpandableGroup {

    List<Audio> tracks;
    String playlist_title;

    public PlaylistExpandableGroup(String title, List<Audio> items) {
        super(title, items);
        this.playlist_title = title;
        this.tracks = items;
    }

    @Override
    public void onChildClicked(int childIndex, boolean checked) {

    }
}
