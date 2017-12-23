package com.buggyarts.android.cuotos.gaana.events;

/**
 * Created by mayank on 12/7/17
 */

public class UpdateProgress {
    private int maxDuration;
    private int progress;

    public UpdateProgress(int maxDuration, int progress){
        this.maxDuration = maxDuration;
        this.progress = progress;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public int getProgress() {
        return progress;
    }

}
