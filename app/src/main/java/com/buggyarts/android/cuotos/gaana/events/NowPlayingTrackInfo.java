package com.buggyarts.android.cuotos.gaana.events;

import com.buggyarts.android.cuotos.gaana.utils.Audio;

/**
 * Created by mayank on 12/6/17
 */

public class NowPlayingTrackInfo {

    private Audio track;
    private int index;

    public NowPlayingTrackInfo(Audio track,int index){
        this.track = track;
        this.index = index;
    }

    public Audio getTrack() {
        return track;
    }

    public int getIndex() {
        return index;
    }
}
