package com.buggyarts.android.cuotos.gaana.events;

/**
 * Created by mayank on 12/9/17
 */

public class OnStartService {
    private boolean isServiceStarted;

    public OnStartService(boolean isServiceStarted){
        this.isServiceStarted = isServiceStarted;
    }

    public boolean isServiceStarted() {
        return isServiceStarted;
    }
}
