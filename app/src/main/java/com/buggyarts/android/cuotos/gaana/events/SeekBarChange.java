package com.buggyarts.android.cuotos.gaana.events;

/**
 * Created by mayank on 12/20/17
 */

public class SeekBarChange {
    int position;

    public SeekBarChange(int position){
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
