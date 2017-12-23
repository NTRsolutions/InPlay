package com.buggyarts.android.cuotos.gaana.events;

/**
 * Created by mayank on 12/7/17
 */

public class EventPlaybackStatus {

    private int status;

    public EventPlaybackStatus(int status){
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
