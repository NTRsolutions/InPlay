package com.buggyarts.android.cuotos.gaana.utils;

import com.thoughtbot.expandablecheckrecyclerview.models.CheckedExpandableGroup;

import java.util.List;

/**
 * Created by mayank on 11/20/17
 */

public class Albums extends CheckedExpandableGroup {

    String title;
    List<Tracks> items;
    public Albums(String title, List<Tracks> items) {
        super(title, items);
        this.title = title;
        this.items = items;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void onChildClicked(int childIndex, boolean checked) {

    }
}
