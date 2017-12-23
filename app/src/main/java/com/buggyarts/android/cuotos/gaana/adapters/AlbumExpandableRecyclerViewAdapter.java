package com.buggyarts.android.cuotos.gaana.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.buggyarts.android.cuotos.gaana.R;
import com.thoughtbot.expandablecheckrecyclerview.CheckableChildRecyclerViewAdapter;
import com.thoughtbot.expandablecheckrecyclerview.models.CheckedExpandableGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.models.ExpandableList;

import java.util.ArrayList;
import java.util.List;

import com.buggyarts.android.cuotos.gaana.utils.AlbumContent;
import com.buggyarts.android.cuotos.gaana.utils.Audio;
import com.buggyarts.android.cuotos.gaana.vHolders.AlbumViewHolder;
import com.buggyarts.android.cuotos.gaana.vHolders.TrackViewHolder;



/**
 * Created by mayank on 11/19/17
 */

public class AlbumExpandableRecyclerViewAdapter extends CheckableChildRecyclerViewAdapter<AlbumViewHolder,TrackViewHolder>{

    private Context context;
    private List<AlbumContent> album_list;
    private ImageView bg_view;
    private int secondlastOpened = -1;
    private int lastOpened = -1;
    private ExpandableList expandableList;
    private int last_add = 0;
    private int last_remove = 0;
    private int childCount=0;

    public interface PassDataOnClick{
        void PassDataOnClick(ArrayList<Audio> tracks,int index);
    }

    public PassDataOnClick passDataOnClick;

    public AlbumExpandableRecyclerViewAdapter(Context context,List<AlbumContent> list,ImageView bg, PassDataOnClick passDataOnClick){
        super(list);
        this.context = context;
        this.album_list = list;
        this.passDataOnClick = passDataOnClick;
        this.bg_view = bg;
    }

    @Override
    public AlbumViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View album_view = LayoutInflater.from(context).inflate(R.layout.album_item_view,parent,false);
        return new AlbumViewHolder(album_view);
    }

    @Override
    public void onBindGroupViewHolder(AlbumViewHolder holder, int flatPosition, final ExpandableGroup group) {
        final int flatpos = flatPosition;
        holder.title.setText(group.getTitle());
    }

    @Override
    public TrackViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View track_view = LayoutInflater.from(context).inflate(R.layout.track_item_view,parent,false);
        return new TrackViewHolder(track_view);
    }

    @Override
    public void onBindChildViewHolder(TrackViewHolder holder, int flatPosition, final ExpandableGroup group, int childIndex) {
        final Audio audio = (Audio) group.getItems().get(childIndex);
        final int index = childIndex;
        Log.v("Track Name",audio.title);
        holder.track_title.setText(audio.title);
        holder.track_artist.setText(audio.artist);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                passDataOnClick.PassDataOnClick((ArrayList<Audio>) group.getItems(),index);
//                Toast.makeText(context,"You Just Clicked "+audio.title,Toast.LENGTH_SHORT).show();
//                EventBus.getDefault().post(new UpdatePlayingQueue((ArrayList<Audio>)group.getItems(),childIndex, Constants.CLEAR_N_ADD_IN_QUEUE,Constants.playingFromAlbum));
            }
        });
    }

    @Override
    public TrackViewHolder onCreateCheckChildViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindCheckChildViewHolder(TrackViewHolder holder, int flatPosition, CheckedExpandableGroup group, int childIndex) {

    }

    @Override
    public boolean onGroupClick(int flatPos) {
//        Toast.makeText(context,""+flatPos,Toast.LENGTH_SHORT).show();
//        if(lastOpened == -1){
//            lastOpened = flatPos;
//        }else {
//            last_add = album_list.get(lastOpened).getItemCount();
//            childCount = childCount + last_add;
//        }

//        Glide.with(context).load(MediaRetriever.getAlbumArt(album_list.get(flatPos-childCount).getAlbum_id())).asBitmap().centerCrop().into(bg_view);

        return super.onGroupClick(flatPos);

    }

}
