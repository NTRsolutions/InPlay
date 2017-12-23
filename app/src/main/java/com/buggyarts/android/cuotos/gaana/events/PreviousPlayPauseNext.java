package com.buggyarts.android.cuotos.gaana.events;

/**
 * Created by mayank on 12/6/17
 */

public class PreviousPlayPauseNext {
    private int controlCondition;

    public PreviousPlayPauseNext(int controlCondition){
        this.controlCondition = controlCondition;
    }

    public int getControlCondition() {
        return controlCondition;
    }
}
