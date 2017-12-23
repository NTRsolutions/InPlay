package com.buggyarts.android.cuotos.gaana.events;

import com.buggyarts.android.cuotos.gaana.utils.Audio;

/**
 * Created by mayank on 12/14/17
 */

public class OnTrackOptionsClick {

    private Audio audio;
    private int trackIndex;

    public OnTrackOptionsClick(int trackIndex, Audio audio){
        this.audio = audio;
        this.trackIndex = trackIndex;
    }

    public Audio getAudio() {
        return audio;
    }

    public int getTrackIndex() {
        return trackIndex;
    }
}
