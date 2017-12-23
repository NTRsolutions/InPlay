package com.buggyarts.android.cuotos.gaana.utils;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.buggyarts.android.cuotos.gaana.adapters.PlayListRecyclerViewAdapter;

/**
 * Created by mayank on 12/12/17
 */

public class SwipeHelper extends ItemTouchHelper.SimpleCallback {


    PlayListRecyclerViewAdapter playListRecyclerViewAdapter;

    public SwipeHelper(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    public SwipeHelper(PlayListRecyclerViewAdapter adapter){
        super(ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT);
        this.playListRecyclerViewAdapter = adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        playListRecyclerViewAdapter.dismissItem(viewHolder.getAdapterPosition());
    }
}
