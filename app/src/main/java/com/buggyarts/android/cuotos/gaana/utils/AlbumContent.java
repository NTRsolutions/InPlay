package com.buggyarts.android.cuotos.gaana.utils;

import com.thoughtbot.expandablecheckrecyclerview.models.CheckedExpandableGroup;

import java.util.List;

/**
 * Created by mayank on 11/19/17
 */

public class AlbumContent extends CheckedExpandableGroup {

    String title,album_id;
    List<Audio> tracks;
    public AlbumContent(String title, List<Audio> tracks,String album_id) {
        super(title, tracks);
        this.title = title;
        this.tracks = tracks;
        this.album_id = album_id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public String getAlbum_id() {
        return album_id;
    }

    @Override
    public void onChildClicked(int childIndex, boolean checked) {

    }
}
