package com.buggyarts.android.cuotos.gaana.events;

import java.util.ArrayList;

import com.buggyarts.android.cuotos.gaana.utils.Audio;

/**
 * Created by mayank on 12/7/17
 */

public class UpdatePlayingQueue {

    private ArrayList<Audio> track_list;
    private int index;
    private int updateType;
    private int isPlayingFrom;

    public UpdatePlayingQueue(ArrayList<Audio> track_list,int index,int updateType, int isPlayingFrom){
        this.track_list = track_list;
        this.index = index;
        this.updateType = updateType;
        this.isPlayingFrom = isPlayingFrom;
    }

    public ArrayList<Audio> getTrack_list() {
        return track_list;
    }

    public int getIndex() {
        return index;
    }

    public int getUpdateType() {
        return updateType;
    }

    public int getIsPlayingFrom() {
        return isPlayingFrom;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
